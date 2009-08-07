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

import org.apache.catalina.cluster.util.FastQueue;
import org.apache.catalina.cluster.util.LinkObject;
import org.apache.catalina.cluster.util.IQueue;

/**
 * Send cluster messages from a Message queue with only one socket. Ack and keep
 * Alive Handling is supported. Fast Queue can limit queue size and consume all messages at queue at one block.<br/>
 * Limit the queue lock contention under high load!<br/>
 * <ul>
 * <li>With autoConnect=false at ReplicationTransmitter, you can disconnect the
 * sender and all messages are queued. Only use this for small maintaince
 * isuses!</li>
 * <li>waitForAck=true, means that receiver ack the transfer</li>
 * <li>after one minute idle time, or number of request (100) the connection is
 * reconnected with next request. Change this for production use!</li>
 * <li>default ackTimeout is 15 sec: this is very low for big all session
 * replication messages after restart a node</li>
 * <li>disable keepAlive: keepAliveTimeout="-1" and
 * keepAliveMaxRequestCount="-1"</li>
 * <li>maxQueueLength: Limit the sender queue length (membership goes well, but transfer is failure!!)</li>
 * </ul>
 * FIXME: refactor code duplications with AsyncSocketSender => configurable or extract super class 
 * @author Peter Rossbach ( idea comes form Rainer Jung)
 * @version $Revision$ $Date$
 * @since 5.5.9
 */
public class FastAsyncSocketSender extends DataSender {

    private static int threadCounter = 1;

    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(FastAsyncSocketSender.class);

    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "FastAsyncSocketSender/1.1";

    // ----------------------------------------------------- Instance Variables

    /**
     * Message Queue
     */
    private FastQueue queue = new FastQueue();

    /**
     * Active thread to push messages asynchronous to the other replication node
     */
    private FastQueueThread queueThread = null;

    /**
     * Count number of queue message
     */
    private long inQueueCounter = 0;

    /**
     * Count all successfull push messages from queue
     */
    private long outQueueCounter = 0;

    /**
     * Current number of bytes from all queued messages
     */
    private long queuedNrOfBytes = 0;

    // ------------------------------------------------------------- Constructor

    /**
     * start background thread to push incomming cluster messages to replication
     * node
     * 
     * @param host
     *            replication node tcp address
     * @param port
     *            replication node tcp port
     */
    public FastAsyncSocketSender(InetAddress host, int port) {
        super(host, port);
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
     * get current add wait timeout 
     * @return current wait timeout
     */
    public long getQueueAddWaitTimeout() {
        
        return queue.getAddWaitTimeout();
    }

    /**
     * Set add wait timeout (default 10000 msec)
     * @param timeout
     */
    public void setQueueAddWaitTimeout(long timeout) {
        queue.setAddWaitTimeout(timeout);
    }

    /**
     * get current remove wait timeout
     * @return
     */
    public long getQueueRemoveWaitTimeout() {
        return queue.getRemoveWaitTimeout();
    }

    /**
     * set remove wait timeout ( default 30000 msec)
     * @param timeout
     */
    public void setRemoveWaitTimeout(long timeout) {
        queue.setRemoveWaitTimeout(timeout);
    }

    /**
     * @return Returns the checkLock.
     */
    public boolean isQueueCheckLock() {
        return queue.isCheckLock();
    }
    /**
     * @param checkLock The checkLock to set.
     */
    public void setQueueCheckLock(boolean checkLock) {
        queue.setCheckLock(checkLock);
    }
    /**
     * @return Returns the doStats.
     */
    public boolean isQueueDoStats() {
        return queue.isDoStats();
    }
    /**
     * @param doStats The doStats to set.
     */
    public void setQueueDoStats(boolean doStats) {
        queue.setDoStats(doStats);
    }
    /**
     * @return Returns the timeWait.
     */
    public boolean isQueueTimeWait() {
        return queue.isTimeWait();
    }
    /**
     * @param timeWait The timeWait to set.
     */
    public void setQueueTimeWait(boolean timeWait) {
        queue.setTimeWait(timeWait);
    }
        
    /**
     * @return Returns the inQueueCounter.
     */
    public int getMaxQueueLength() {
        return queue.getMaxQueueLength();
    }

    /**
     * @param set max
     *            Queue length
     */
    public void setMaxQueueLength(int length) {
        queue.setMaxQueueLength(length);
    }

    /**
     * @return Returns the add wait times.
     */
    public long getQueueAddWaitTime() {
        return queue.getAddWait();
    }

    /**
     * @return Returns the add wait times.
     */
    public long getQueueRemoveWaitTime() {
        return queue.getRemoveWait();
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
        return queue.getSize();
    }

    /**
     * @return Returns the queuedNrOfBytes.
     */
    public long getQueuedNrOfBytes() {
        return queuedNrOfBytes;
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
        queue.start() ;
    }

    /**
     * Disconnect socket ad stop queue thread
     * 
     * @see org.apache.catalina.cluster.tcp.IDataSender#disconnect()
     */
    public void disconnect() {
        stopThread();
        queue.stop() ; // FIXME what is when message in queue => auto reconnect after one sending failure?
        super.disconnect();
    }

    /*
     * Send message to queue for later sending
     * 
     * @see org.apache.catalina.cluster.tcp.IDataSender#sendMessage(java.lang.String,
     *      byte[])
     */
    public synchronized void sendMessage(String messageid, byte[] data)
            throws java.io.IOException {
        queue.add(messageid, data);
        inQueueCounter++;
        queuedNrOfBytes += data.length;
        if (log.isTraceEnabled())
            log.trace(sm.getString("AsyncSocketSender.queue.message",
                    getAddress().getHostAddress(), new Integer(getPort()), messageid, new Long(
                            data.length)));
    }

    /*
     * Reset sender statistics
     */
    public synchronized void resetStatistics() {
        super.resetStatistics();
        inQueueCounter = queue.getSize();
        outQueueCounter = 0;
        queue.resetStatistics();
    }

    /**
     * Name of this SockerSender
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("FastAsyncSocketSender[");
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
            queueThread = new FastQueueThread(this, queue);
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

    /*
     * Reduce queued message date size counter
     */
    protected void reduceQueuedCounter(int size) {
        queuedNrOfBytes -= size;
    }

    // -------------------------------------------------------- Inner Class

    private class FastQueueThread extends Thread {

        
        /**
         * Sender queue
         */
        private IQueue queue = null;

        /**
         * Active sender
         */
        private FastAsyncSocketSender sender = null;

        /**
         * Thread is active
         */
        private boolean keepRunning = true;

        /**
         * Only use inside FastAsyncSocketSender
         * @param sender
         * @param queue
         */
        private FastQueueThread(FastAsyncSocketSender sender, IQueue queue) {
            setName("Cluster-FastAsyncSocketSender-" + (threadCounter++));
            this.queue = queue;
            this.sender = sender;
        }

        public void stopRunning() {
            keepRunning = false;
        }

        /* Get the objects from queue and send all mesages to the sender.
         * @see java.lang.Runnable#run()
         */
        public void run() {
            while (keepRunning) {
                // get a link list of all queued objects
                LinkObject entry = queue.remove();
                if (entry != null) {
                    do {
                        int messagesize = 0;
                        try {
                            byte[] data = (byte[]) entry.data();
                            messagesize = data.length;
                            sender.pushMessage((String) entry.getKey(), data);
                            outQueueCounter++;
                        } catch (Exception x) {
                            log.warn(sm.getString(
                                    "AsyncSocketSender.send.error", entry
                                            .getKey()),x);
                        } finally {
                            reduceQueuedCounter(messagesize);
                        }
                        entry = entry.next();
                    } while (entry != null);
                } else {
                    log.error(sm.getString("AsyncSocketSender.queue.empty",sender.getAddress(), new Integer(sender.getPort())));
                }
            }
        }

    }

}