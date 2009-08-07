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

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;

/**
 * Send cluster messages with a pool of sockets (25).
 * 
 * FIXME support processing stats
 * 
 * @author Filip Hanik
 * @author Peter Rossbach
 * @version 1.2
 */

public class PooledSocketSender extends DataSender {

    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(org.apache.catalina.cluster.tcp.PooledSocketSender.class);

    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "PooledSocketSender/1.2";

    // ----------------------------------------------------- Instance Variables

    private int maxPoolSocketLimit = 25;

    private SenderQueue senderQueue = null;

    //  ----------------------------------------------------- Constructor

    public PooledSocketSender(InetAddress host, int port) {
        super(host, port);
        senderQueue = new SenderQueue(this, maxPoolSocketLimit);
    }

    //  ----------------------------------------------------- Public Properties

    /**
     * Return descriptive information about this implementation and the
     * corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {

        return (info);

    }

    public void setMaxPoolSocketLimit(int limit) {
        maxPoolSocketLimit = limit;
        senderQueue.setLimit(limit);
    }

    public int getMaxPoolSocketLimit() {
        return maxPoolSocketLimit;
    }

    public int getInPoolSize() {
        return senderQueue.getInPoolSize();
    }

    public int getInUsePoolSize() {
        return senderQueue.getInUsePoolSize();
    }

    //  ----------------------------------------------------- Public Methode

    public void connect() throws java.io.IOException {
        //do nothing, happens in the socket sender itself
        senderQueue.open();
        setSocketConnected(true);
        connectCounter++;
    }

    public void disconnect() {
        senderQueue.close();
        setSocketConnected(false);
        disconnectCounter++;
    }

    /**
     * send Message and use a pool of SocketSenders
     * 
     * @param messageId Message unique identifier
     * @param data Message data
     * @throws java.io.IOException
     */
    public void sendMessage(String messageId, byte[] data) throws IOException {
        //get a socket sender from the pool
        SocketSender sender = senderQueue.getSender(0);
        if (sender == null) {
            log.warn(sm.getString("PoolSocketSender.noMoreSender", this
                    .getAddress(), new Integer(this.getPort())));
            return;
        }
        //send the message
        try {
            sender.sendMessage(messageId, data);
        } finally {
            //return the connection to the pool
            senderQueue.returnSender(sender);
        }
        addStats(data.length);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("PooledSocketSender[");
        buf.append(getAddress()).append(":").append(getPort()).append("]");
        return buf.toString();
    }

    //  ----------------------------------------------------- Inner Class

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

        /**
         * @return Returns the limit.
         */
        public int getLimit() {
            return limit;
        }
        /**
         * @param limit The limit to set.
         */
        public void setLimit(int limit) {
            this.limit = limit;
        }
        /**
         * @return
         */
        public int getInUsePoolSize() {
            return inuse.size();
        }

        /**
         * @return
         */
        public int getInPoolSize() {
            return queue.size();
        }

        public SocketSender getSender(long timeout) {
            SocketSender sender = null;
            long start = System.currentTimeMillis();
            long delta = 0;
            do {
                synchronized (mutex) {
                    if (!isOpen)
                        throw new IllegalStateException(
                                "Socket pool is closed.");
                    if (queue.size() > 0) {
                        sender = (SocketSender) queue.removeFirst();
                    } else if (inuse.size() < limit) {
                        sender = getNewSocketSender();
                    } else {
                        try {
                            mutex.wait(timeout);
                        } catch (Exception x) {
                            PooledSocketSender.log
                                    .warn(
                                            sm
                                                    .getString(
                                                            "PoolSocketSender.senderQueue.sender.failed",
                                                            parent.getAddress(),
                                                            new Integer(parent
                                                                    .getPort())),
                                            x);
                        }//catch
                    }//end if
                    if (sender != null) {
                        inuse.add(sender);
                    }
                }//synchronized
                delta = System.currentTimeMillis() - start;
            } while ((isOpen) && (sender == null)
                    && (timeout == 0 ? true : (delta < timeout)));
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
            SocketSender sender = new SocketSender(parent.getAddress(), parent
                    .getPort());
            sender.setKeepAliveMaxRequestCount(parent
                    .getKeepAliveMaxRequestCount());
            sender.setKeepAliveTimeout(parent.getKeepAliveTimeout());
            sender.setAckTimeout(parent.getAckTimeout());
            sender.setWaitForAck(parent.isWaitForAck());
            return sender;

        }

        public void close() {
            synchronized (mutex) {
                for (int i = 0; i < queue.size(); i++) {
                    SocketSender sender = (SocketSender) queue.get(i);
                    sender.disconnect();
                }//for
                for (int i = 0; i < inuse.size(); i++) {
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