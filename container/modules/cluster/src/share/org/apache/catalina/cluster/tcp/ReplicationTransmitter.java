/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.catalina.cluster.tcp;

import org.apache.catalina.cluster.io.XByteBuffer;
import org.apache.catalina.cluster.Member;
import org.apache.catalina.cluster.ClusterSender;

 
public class ReplicationTransmitter implements ClusterSender
{
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( ReplicationTransmitter.class );

    private java.util.HashMap map = new java.util.HashMap();
    public ReplicationTransmitter()
    {
    }

    private static long nrOfRequests = 0;
    private static long totalBytes = 0;
    private String replicationMode;
    private long ackTimeout = 15000; //15 seconds by default
    
    private static synchronized void addStats(int length) {
        nrOfRequests++;
        totalBytes+=length;
        if ( (nrOfRequests % 100) == 0 ) {
           	if(log.isInfoEnabled())
           	    log.info("Nr of bytes sent="+totalBytes+" over "+nrOfRequests+" =="+(totalBytes/nrOfRequests)+" bytes/request");
        }

    }
    
    public void setReplicationMode(String mode) {
        String msg = IDataSenderFactory.validateMode(mode);
        if (msg == null) {
           	if(log.isDebugEnabled())
           	     log.debug("Setting replcation mode to " + mode);
            this.replicationMode = mode;
        }
        else
            throw new IllegalArgumentException(msg);

    }


    public synchronized void add(Member member)
    {
        try {
            String key = getKey(member);
            if (!map.containsKey(key)) {
                IDataSender sender = IDataSenderFactory.getIDataSender(
                        replicationMode, member);
                map.put(key, sender);
            }
        }catch ( java.io.IOException x ) {
            log.error("Unable to create and add a IDataSender object.",x);
        }
    }//add

    private String getKey(Member member) {
        return member.getHost() + ":" + member.getPort();
    }
    
    public synchronized void remove(Member member)
    {
        String key = getKey(member);
        IDataSender toberemoved = (IDataSender) map.get(key);
        if (toberemoved == null)return;
        toberemoved.disconnect();
        map.remove(key);
    }

    public void start() throws java.io.IOException
    {
        //don't have to do shit, we connect on demand
    }

    public synchronized void stop()
    {
        java.util.Iterator i = map.entrySet().iterator();
        while ( i.hasNext() )
        {
            IDataSender sender = (IDataSender)((java.util.Map.Entry)i.next()).getValue();
            try { sender.disconnect(); } catch ( Exception x ){}
        }//while
    }//stop

    public IDataSender[] getSenders()
    {
        java.util.Iterator i = map.entrySet().iterator();
        java.util.Vector v = new java.util.Vector();
        while ( i.hasNext() )
        {
            IDataSender sender = (IDataSender)((java.util.Map.Entry)i.next()).getValue();
            if ( sender!=null) v.addElement(sender);
        }
        IDataSender[] result = new IDataSender[v.size()];
        v.copyInto(result);
        return result;
    }

    protected void sendMessageData(String sessionId, byte[] data, IDataSender sender) throws java.io.IOException  {
        if ( sender == null ) throw new java.io.IOException("Sender not available. Make sure sender information is available to the ReplicationTransmitter.");
        try
        {
            if (!sender.isConnected())
                sender.connect();
            //set the timeout, will be ignored by async senders
            sender.setAckTimeout(getAckTimeout());
            sender.sendMessage(sessionId,data);
            sender.setSuspect(false);
            addStats(data.length);
        }catch ( Exception x)
        {  
        	if(log.isWarnEnabled()) {
                if ( !sender.getSuspect() ) {
                    log.warn("Unable to send replicated message, is server down?",
                         x);
                }
        	}
            sender.setSuspect(true);

        }

    }
    public void sendMessage(String sessionId, byte[] indata, Member member) throws java.io.IOException
    {
        byte[] data = XByteBuffer.createDataPackage(indata);
        String key = member.getHost()+":"+member.getPort();
        IDataSender sender = (IDataSender)map.get(key);
        sendMessageData(sessionId,data,sender);
    }

    public void sendMessage(String sessionId, byte[] indata) throws java.io.IOException
    {
         IDataSender[] senders = getSenders();
        byte[] data = XByteBuffer.createDataPackage(indata);
        for ( int i=0; i<senders.length; i++ )
        {

            IDataSender sender = senders[i];
            try
            {
                sendMessageData(sessionId,data,sender);
            }catch ( Exception x)
            {

                if ( !sender.getSuspect()) log.warn("Unable to send replicated message to "+sender+", is server down?",x);
                sender.setSuspect(true);
            }
        }//while
    }
    public String getReplicationMode() {
        return replicationMode;
    }
    
    public boolean getIsSenderSynchronized() {
        return IDataSenderFactory.SYNC_MODE.equals(replicationMode) ||
            IDataSenderFactory.POOLED_SYNC_MODE.equals(replicationMode);
    }
    public long getAckTimeout() {
        return ackTimeout;
    }
    public void setAckTimeout(long ackTimeout) {
        this.ackTimeout = ackTimeout;
    }



}
