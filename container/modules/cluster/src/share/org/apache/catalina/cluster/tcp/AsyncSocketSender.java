/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package org.apache.catalina.cluster.tcp;

import java.net.InetAddress;

import org.apache.catalina.cluster.util.SmartQueue;

/**
 * Send cluster messages from a Message queue with only one socket. Ack and keep
 * Alive Handling is supported.
 * <ul>
 * <li>With autoConnect=false at ReplicationTransmitter, you can disconnect the
 * sender and all messages are queued. Only use this for small maintaince
 * isuses!</li>
 * <li>waitForAck=true, means that receiver ack the transfer</li>
 * <li>after one minute idle time, or number of request (100) the connection is
 * reconnected with next request. Change this for production use!</li>
 * <li>default ackTimeout is 15 sec: this is very low for big all session replication messages after restart a node</li>
 * <li>disable keepAlive: keepAliveTimeout="-1" and keepAliveMaxRequestCount="-1"</li>
 * </ul>
 * 
 * @author Filip Hanik
 * @author Peter Rossbach
 * @version $Revision$ $Date$
 */
public class AsyncSocketSender extends DataSender {
    
    private static int threadCounter = 1;

    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(AsyncSocketSender.class);

    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "AsyncSocketSender/2.0";

    // ----------------------------------------------------- Instance Variables

    /**
     * Message Queue
     */
    private SmartQueue queue = new SmartQueue();

    /**
     * Active thread to push messages asynchronous to the other replication node
     */
    private QueueThread queueThread = null;

    /**
     * Count number of queue message
     */
    private long inQueueCounter = 0;

    /**
     * Count all successfull push messages from queue
     */
    private long outQueueCounter = 0;

    // ------------------------------------------------------------- Constructor

    /**
     * start background thread to push incomming cluster messages to replication
     * node
     * 
     * @param domain replication cluster domain (session domain)
     * @param host replication node tcp address
     * @param port replication node tcp port
     */
    public AsyncSocketSender(String domain,InetAddress host, int port) {
        super(domain,host, port);
        checkThread();
    }

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
     * @return Returns the inQueueCounter.
     */
    public long getInQueueCounter() {
        return inQueueCounter;
    }

    /**
     * @return Returns the outQueueCounter.
     */
    public long getOutQueueCounter() {
        return outQueueCounter;
    }

    /**
     * @return Returns the queueSize.
     */
    public int getQueueSize() {
        return queue.size();
    }

    /**
     * @return Returns the queuedNrOfBytes.
     */
    public long getQueuedNrOfBytes() {
        if(queueThread != null)
            return queueThread.getQueuedNrOfBytes();
        return 0l ;
    }

    // --------------------------------------------------------- Public Methods

    /*
     * Connect to socket and start background thread to ppush queued messages
     * 
     * @see org.apache.catalina.cluster.tcp.IDataSender#connect()
     */
    public void connect() throws java.io.IOException {
        super.connect();
        checkThread();
    }

    /**
     * Disconnect socket ad stop queue thread
     * 
     * @see org.apache.catalina.cluster.tcp.IDataSender#disconnect()
     */
    public void disconnect() {
        stopThread();
        super.disconnect();
    }

    /*
     * Send message to queue for later sending
     * 
     * @see org.apache.catalina.cluster.tcp.IDataSender#sendMessage(java.lang.String,
     *      byte[])
     */
    public void sendMessage(String messageid, ClusterData data)
            throws java.io.IOException {
        SmartQueue.SmartEntry entry = new SmartQueue.SmartEntry(messageid, data);
        queue.add(entry);
        synchronized (this) {
            inQueueCounter++;
            queueThread.incQueuedNrOfBytes(data.getMessage().length);
       }
        if (log.isTraceEnabled())
            log.trace(sm.getString("AsyncSocketSender.queue.message",
                    getAddress().getHostAddress(), new Integer(getPort()), messageid, new Long(
                            data.getMessage().length)));
    }

    /*
     * Reset sender statistics
     */
    public synchronized void resetStatistics() {
        super.resetStatistics();
        inQueueCounter = queue.size();
        outQueueCounter = 0;

    }

    /**
     * Name of this SockerSender
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("AsyncSocketSender[");
        buf.append(getAddress().getHostAddress()).append(":").append(getPort()).append("]");
        return buf.toString();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Start Queue thread as daemon
     */
    protected void checkThread() {
        if (queueThread == null) {
            if (log.isInfoEnabled())
                log.info(sm.getString("AsyncSocketSender.create.thread",
                        getAddress(), new Integer(getPort())));
            queueThread = new QueueThread(this);
            queueThread.setDaemon(true);
            queueThread.start();
        }
    }

    /**
     * stop queue worker thread
     */
    protected void stopThread() {
        if (queueThread != null) {
            queueThread.stopRunning();
            queueThread = null;
        }
    }

    // -------------------------------------------------------- Inner Class

    private class QueueThread extends Thread {
        AsyncSocketSender sender;

        private boolean keepRunning = true;

        /**
         * Current number of bytes from all queued messages
         */
        private long queuedNrOfBytes = 0;

        public QueueThread(AsyncSocketSender sender) {
            this.sender = sender;
            setName("Cluster-AsyncSocketSender-" + (threadCounter++));
        }

        protected long getQueuedNrOfBytes() {
            return queuedNrOfBytes ;
        }
        
        protected synchronized void setQueuedNrOfBytes(long queuedNrOfBytes) {
            this.queuedNrOfBytes = queuedNrOfBytes;
        }

        protected synchronized void incQueuedNrOfBytes(long size) {
            queuedNrOfBytes += size;
        }
        
        protected synchronized void decQueuedNrOfBytes(long size) {
            queuedNrOfBytes -= size;
        }

        public void stopRunning() {
            keepRunning = false;
        }

        /**
         * Get one queued message and push it to the replication node
         * 
         * @see DataSender#pushMessage(String, byte[])
         */
        public void run() {
            while (keepRunning) {
                SmartQueue.SmartEntry entry = sender.queue.remove(5000);
                if (entry != null) {
                    int messagesize = 0;
                    try {
                        ClusterData data = (ClusterData) entry.getValue();
                        messagesize = data.getMessage().length;
                        sender.pushMessage(data);
                        outQueueCounter++;
                    } catch (Exception x) {
                        log.warn(sm.getString("AsyncSocketSender.send.error",
                                entry.getKey()));
                    } finally {
                        decQueuedNrOfBytes(messagesize);
                    }
                }
            }
        }
    }
}