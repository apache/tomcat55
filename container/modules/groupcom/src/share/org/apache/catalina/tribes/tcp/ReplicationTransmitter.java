/*
 * Copyright 1999,2004-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.catalina.tribes.tcp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.ObjectName;

import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.ChannelSender;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.util.IDynamicProperty;
import org.apache.catalina.util.StringManager;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.catalina.tribes.tcp.nio.PooledParallelSender;

/**
 * Transmit message to other cluster members
 * Actual senders are created based on the replicationMode
 * type 
 * 
 * @author Filip Hanik
 * @version $Revision: 379956 $ $Date: 2006-02-22 16:57:35 -0600 (Wed, 22 Feb 2006) $
 */
public class ReplicationTransmitter implements ChannelSender,IDynamicProperty {
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(ReplicationTransmitter.class);

    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "ReplicationTransmitter/3.0";

    /**
     * The string manager for this package.
     */
    protected StringManager sm = StringManager.getManager(Constants.Package);

    
    private Map map = new HashMap();

    /**
     * @todo make this configurable
     */
    protected int rxBufSize = 43800;
    /**
     * We are only sending acks
     */
    protected int txBufSize = 25188;

    public ReplicationTransmitter() {
    }

    /**
     * current sender replication mode
     */
    private String replicationMode;

    /**
     * sender default ackTimeout
     */
    private long ackTimeout = 15000; //15 seconds by default

    /**
     * enabled wait for ack
     */
    private boolean waitForAck = true;

    /**
     * autoConnect sender when next message send
     */
    private boolean autoConnect = false; 
   
    /**
     * dynamic sender <code>properties</code>
     */
    private Map properties = new HashMap();


    // ------------------------------------------------------------- Properties

    /**
     * Return descriptive information about this implementation and the
     * corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {
        return (info);
    }

    

    /**
     * current replication mode
     * 
     * @return The mode
     */
    public String getReplicationMode() {
        return replicationMode;
    }

    /**
     * set replication Mode (pooled, synchonous, asynchonous, fastasyncqueue)
     * 
     * @see IDataSenderFactory#validateMode(String)
     * @param mode
     */
    public void setReplicationMode(String mode) {
        String msg = DataSenderFactory.validateMode(mode);
        if (msg == null) {
            if (log.isDebugEnabled())
                log.debug("Setting replication mode to " + mode);
            this.replicationMode = mode;
        } else
            throw new IllegalArgumentException(msg);

    }

    

    /**
     * @return Returns the autoConnect.
     */
    public boolean isAutoConnect() {
        return autoConnect;
    }

    /**
     * @param autoConnect
     *            The autoConnect to set.
     */
    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
        setProperty("autoConnect", String.valueOf(autoConnect));

    }

    /**
     * @return The ack timeout
     */
    public long getAckTimeout() {
        return ackTimeout;
    }

    /**
     * @param ackTimeout
     */
    public void setAckTimeout(long ackTimeout) {
        this.ackTimeout = ackTimeout;
        setProperty("ackTimeout", String.valueOf(ackTimeout));
    }

    /**
     * @return Returns the waitForAck.
     */
    public boolean getWaitForAck() {
        return waitForAck;
    }

    /**
     * @param waitForAck
     *            The waitForAck to set.
     */
    public void setWaitForAck(boolean waitForAck) {
        this.waitForAck = waitForAck;
        setProperty("waitForAck", String.valueOf(waitForAck));
    }

    
    public int getTxBufSize() {
        return txBufSize;
    }

    public int getRxBufSize() {
        return rxBufSize;
    }

    public boolean isParallel() {
        return "parallel".equals(replicationMode);
    }

    public void setTxBufSize(int txBufSize) {
        this.txBufSize = txBufSize;
    }

    public void setRxBufSize(int rxBufSize) {
        this.rxBufSize = rxBufSize;
    }

    /**
     * @return True if synchronized sender
     */
    public boolean getIsSenderSynchronized() {
        return 
            DataSenderFactory.SYNC_MODE.equals(replicationMode) ||
            DataSenderFactory.POOLED_SYNC_MODE.equals(replicationMode) ||
            (DataSenderFactory.PARALLEL_MODE.equals(replicationMode) && waitForAck);
    }

    // ------------------------------------------------------------- dynamic
    // sender property handling

    /**
     * set config attributes with reflect
     * 
     * @param name
     * @param value
     */
    public void setProperty(String name, Object value) {
        if (log.isTraceEnabled())
            log.trace(sm.getString("ReplicationTransmitter.setProperty", name,
                    value, properties.get(name)));

        properties.put(name, value);
    }

    /**
     * get current config
     * 
     * @param key
     * @return The property
     */
    public Object getProperty(String key) {
        if (log.isTraceEnabled())
            log.trace(sm.getString("ReplicationTransmitter.getProperty", key));
        return properties.get(key);
    }

    /**
     * Get all properties keys
     * 
     * @return An iterator over the propery name set
     */
    public Iterator getPropertyNames() {
        return properties.keySet().iterator();
    }

    /**
     * remove a configured property.
     * 
     * @param key
     */
    public void removeProperty(String key) {
        properties.remove(key);
    }

    // ------------------------------------------------------------- public
    
    /**
     * Send data to one member
     * @see org.apache.catalina.tribes.ClusterSender#sendMessage(org.apache.catalina.tribes.ClusterMessage, org.apache.catalina.tribes.Member)
     */
    public void sendMessage(ChannelMessage message, Member[] destination) throws ChannelException {
        if ( !isParallel() ) {
            ChannelException exception = null;
            for (int i = 0; i < destination.length; i++) {
                try {
                    sendMessage(message, destination[i]);
                } catch (Exception x) {
                    if (exception == null) exception = new ChannelException(x);
                    exception.addFaultyMember(destination[i]);
                }
            }
            if (exception != null)throw exception;
        } else {
            MultiPointSender sender = getParallelSender();
            sender.sendMessage(destination,message);
        }
    }
    
    /**
     * @todo FIX THIS TO BE IN THE FACTORY
     */
    PooledParallelSender parallelsender = null;
    public MultiPointSender getParallelSender() {
        if ( parallelsender == null ) {
            PooledParallelSender sender = new PooledParallelSender();
            sender.setMaxRetryAttempts(2);
            sender.setRxBufSize(getRxBufSize());
            sender.setTimeout(ackTimeout);
            sender.setUseDirectBuffer(true);
            sender.setWaitForAck(getWaitForAck());
            sender.setTxBufSize(getTxBufSize());
            parallelsender = sender;
        }
        return parallelsender;
    }
    
    public void sendMessage(ChannelMessage message, Member destination) throws ChannelException {       
        Object key = getKey(destination);
        SinglePointSender sender = (SinglePointSender) map.get(key);
        if ( sender == null ) {
            add(destination);
            sender = (SinglePointSender) map.get(key);
        }
        sendMessageData(message, sender);
    }
    
    /**
     * start the sender and register transmitter mbean
     * 
     * @see org.apache.catalina.tribes.ClusterSender#start()
     */
    public void start() throws java.io.IOException {
    }

    /*
     * stop the sender and deregister mbeans (transmitter, senders)
     * 
     * @see org.apache.catalina.tribes.ClusterSender#stop()
     */
    public synchronized void stop() {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            SinglePointSender sender = (SinglePointSender) ((java.util.Map.Entry) i.next())
                    .getValue();
            try {
                sender.disconnect();
            } catch (Exception x) {
            }
            i.remove();
        }
    }

    /**
     * Call transmitter to check for sender socket status
     * 
     * @see SimpleTcpCluster#backgroundProcess()
     */

    public void heartbeat() {
        checkKeepAlive();
    }

    /**
     * Check all DataSender Socket to close socket at keepAlive mode
     * @see DataSender#checkKeepAlive()
     */
    public void checkKeepAlive() {
        if (map.size() > 0) {
            java.util.Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                SinglePointSender sender = (SinglePointSender) ((java.util.Map.Entry) iter
                        .next()).getValue();
                if (sender != null)
                    sender.checkKeepAlive();
            }
        }
    }

    /**
     * get all current senders
     * 
     * @return The senders
     */
    public SinglePointSender[] getSenders() {
        java.util.Iterator iter = map.entrySet().iterator();
        SinglePointSender[] array = new SinglePointSender[map.size()];
        int i = 0;
        while (iter.hasNext()) {
            SinglePointSender sender = (SinglePointSender) ((java.util.Map.Entry) iter
                    .next()).getValue();
            if (sender != null)
                array[i] = sender;
            i++;
        }
        return array;
    }

    /**
     * add new cluster member and create sender ( s. replicationMode) transfer
     * current properties to sender
     * 
     * @see org.apache.catalina.tribes.ClusterSender#add(org.apache.catalina.tribes.Member)
     */
    public synchronized void add(Member member) {
        try {
            if ( !isParallel() ) {
                Object key = getKey(member);
                if (!map.containsKey(key)) {
                    SinglePointSender sender = DataSenderFactory.getSingleSender(replicationMode, member);
                    if (sender != null) {
                        transferSenderProperty(sender);
                        sender.setRxBufSize(getRxBufSize());
                        sender.setTxBufSize(getTxBufSize());
                        map.put(key, sender);
                    }
                }
            }
        } catch (java.io.IOException x) {
            log.error("Unable to create and add a IDataSender object.", x);
        }
    }

    /**
     * remove sender from transmitter. ( deregister mbean and disconnect sender )
     * 
     * @see org.apache.catalina.tribes.ClusterSender#remove(org.apache.catalina.tribes.Member)
     */
    public synchronized void remove(Member member) {
        Object key = getKey(member);
        SinglePointSender toberemoved = (SinglePointSender) map.get(key);
        if (toberemoved == null)
            return;
        toberemoved.disconnect();
        map.remove(key);

    }

    // ------------------------------------------------------------- protected

    /**
     * Transfer all properties from transmitter to concrete sender
     * 
     * @param sender
     */
    protected void transferSenderProperty(SinglePointSender sender) {
        for (Iterator iter = getPropertyNames(); iter.hasNext();) {
            String pkey = (String) iter.next();
            Object value = getProperty(pkey);
            IntrospectionUtils.setProperty(sender, pkey, value.toString());
        }
    }

    /**
     * set unique key to find sender
     * 
     * @param member
     * @return concat member.host:member.port
     */
    protected Object getKey(Member member) {
        return member;
    }

    /**
     * Send message to concrete sender. If autoConnect is true, check is
     * connection broken and the reconnect the complete sender.
     * <ul>
     * <li>failure the suspect flag is set true. After successfully sending the
     * suspect flag is set to false.</li>
     * <li>Stats is only update after sussesfull sending</li>
     * </ul>
     * 
     * @param data message Data
     * @param sender concrete message sender
     * @return true if the message got sent, false otherwise
     * @throws java.io.IOException If an error occurs
     */
    protected void sendMessageData(ChannelMessage data,
                                   SinglePointSender sender) throws ChannelException {
        if (sender == null)
            throw new RuntimeException("Sender not available. Make sure sender information is available to the ReplicationTransmitter.");
        try {
            // deprecated not needed DataSender#pushMessage can handle connection
            if (autoConnect) {
                synchronized(sender) {
                    if(!sender.isConnected()) sender.connect();
                }
            }
            sender.sendMessage(data);
            sender.setSuspect(false);
        } catch (ChannelException x) {
            if (!sender.getSuspect()) {
                if (log.isErrorEnabled() ) log.error("Unable to send replicated message, is member ["+sender.toString()+"] down?",x);
            } else if (log.isDebugEnabled() ) {
                log.debug("Unable to send replicated message, is member ["+sender.toString()+"] down?",x);
            }
            sender.setSuspect(true);
            throw x;
        }

    }
}
