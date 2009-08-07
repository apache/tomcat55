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
import java.net.InetAddress ;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Send cluster messages with a pool of sockets (25).
 * 
 * @author Filip Hanik
 * @author Peter Rossbach
 * @version 1.1
 */


public class PooledSocketSender implements IDataSender
{

    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( org.apache.catalina.cluster.CatalinaCluster.class );

    private InetAddress address;
    private int port;
    private Socket sc = null;
    private boolean isSocketConnected = true;
    private boolean suspect;
    private long ackTimeout = 15*1000;  //15 seconds socket read timeout (for acknowledgement)
    private long keepAliveTimeout = 60*1000; //keep socket open for no more than one min
    private int keepAliveMaxRequestCount = 100; //max 100 requests before reconnecting
    private long keepAliveConnectTime = 0;
    private int keepAliveCount = 0;
    private int maxPoolSocketLimit = 25;

    private SenderQueue senderQueue = null;
    private long nrOfRequests = 0;

    private long totalBytes = 0;

    public PooledSocketSender(InetAddress host, int port)
    {
        this.address = host;
        this.port = port;
        senderQueue = new SenderQueue(this,maxPoolSocketLimit);
    }

    private synchronized void addStats(int length) {
        nrOfRequests++;
        totalBytes += length;
        if (log.isDebugEnabled() && (nrOfRequests % 100) == 0) {
            log.debug("Send stats from " + getAddress().getHostAddress() + ":" + getPort()
                    + "Nr of bytes sent=" + totalBytes + " over "
                    + nrOfRequests + " ==" + (totalBytes / nrOfRequests)
                    + " bytes/request");
        }

    }

    /**
     * @return Returns the nrOfRequests.
     */
    public long getNrOfRequests() {
        return nrOfRequests;
    }

    /**
     * @return Returns the totalBytes.
     */
    public long getTotalBytes() {
        return totalBytes;
    }


    public InetAddress getAddress()
    {
        return address;
    }

    public int getPort()
    {
        return port;
    }

    public void connect() throws java.io.IOException
    {
        //do nothing, happens in the socket sender itself
        senderQueue.open();
        isSocketConnected = true;
    }

    public void disconnect()
    {
        senderQueue.close();
        isSocketConnected = false;
    }

    public boolean isConnected()
    {
        return isSocketConnected;
    }

    public void setAckTimeout(long timeout) {
        this.ackTimeout = timeout;
    }

    public long getAckTimeout() {
        return ackTimeout;
    }

    public void setMaxPoolSocketLimit(int limit) {
        maxPoolSocketLimit = limit;
    }

    public int getMaxPoolSocketLimit() {
        return maxPoolSocketLimit;
    }


    /**
     * Blocking send
     * @param data
     * @throws java.io.IOException
     */
    public void sendMessage(String sessionId, byte[] data) throws java.io.IOException
    {
        //get a socket sender from the pool
        SocketSender sender = senderQueue.getSender(0);
        if ( sender == null ) {
            log.warn("No socket sender available for client="+this.getAddress()+":"+this.getPort()+" did it disappear?");
            return;
        }//end if
        //send the message
        sender.sendMessage(sessionId,data);
        //return the connection to the pool
        senderQueue.returnSender(sender);
        addStats(data.length);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("PooledSocketSender[");
        buf.append(getAddress()).append(":").append(getPort()).append("]");
        return buf.toString();
    }

    public boolean getSuspect() {
        return suspect;
    }

    public void setSuspect(boolean suspect) {
        this.suspect = suspect;
    }

    public long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }
    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }
    public int getKeepAliveMaxRequestCount() {
        return keepAliveMaxRequestCount;
    }
    public void setKeepAliveMaxRequestCount(int keepAliveMaxRequestCount) {
        this.keepAliveMaxRequestCount = keepAliveMaxRequestCount;
    }

    /**
     * @return Returns the keepAliveConnectTime.
     */
    public long getKeepAliveConnectTime() {
        return keepAliveConnectTime;
    }
    /**
     * @return Returns the keepAliveCount.
     */
    public int getKeepAliveCount() {
        return keepAliveCount;
    }

    private class SenderQueue {
        private int limit = 25;
        PooledSocketSender parent = null;
        private LinkedList queue = new LinkedList();
        private LinkedList inuse = new LinkedList();
        private Object mutex = new Object();
        private boolean isOpen = true;

        public SenderQueue(PooledSocketSender parent, int limit) {
            this.limit = limit;
            this.parent = parent;
        }

        public SocketSender getSender(long timeout) {
            SocketSender sender = null;
            long start = System.currentTimeMillis();
            long delta = 0;
            do {
                synchronized (mutex) {
                    if ( !isOpen ) throw new IllegalStateException("Socket pool is closed.");
                    if ( queue.size() > 0 ) {
                        sender = (SocketSender) queue.removeFirst();
                    } else if ( inuse.size() < limit ) {
                        sender = getNewSocketSender();
                    } else {
                        try {
                            mutex.wait(timeout);
                        }catch ( Exception x ) {
                            PooledSocketSender.log.warn("PoolSocketSender.senderQueue.getSender failed",x);
                        }//catch
                    }//end if
                    if ( sender != null ) {
                        inuse.add(sender);
                    }
                }//synchronized
                delta = System.currentTimeMillis() - start;
            } while ( (isOpen) && (sender == null) && (timeout==0?true:(delta<timeout)) );
            //to do
            return sender;
        }

        public void returnSender(SocketSender sender) {
            //to do
            synchronized (mutex) {
                queue.add(sender);
                inuse.remove(sender);
                mutex.notify();
            }
        }

        private SocketSender getNewSocketSender() {
            //new SocketSender(
            SocketSender sender = new SocketSender(parent.getAddress(),parent.getPort());
            sender.setKeepAliveMaxRequestCount(parent.getKeepAliveMaxRequestCount());
            sender.setKeepAliveTimeout(parent.getKeepAliveTimeout());
            sender.setAckTimeout(parent.getAckTimeout());
            return sender;

        }

        public void close() {
            synchronized (mutex) {
                for ( int i=0; i<queue.size(); i++ ) {
                    SocketSender sender = (SocketSender)queue.get(i);
                    sender.disconnect();
                }//for
                for ( int i=0; i<inuse.size(); i++ ) {
                    SocketSender sender = (SocketSender) inuse.get(i);
                    sender.disconnect();
                }//for
                queue.clear();
                inuse.clear();
                isOpen = false;
                mutex.notifyAll();
            }
        }
        
        public void open() {
            synchronized (mutex) {
                isOpen = true;
                mutex.notifyAll();
            }
        }
    }
}
