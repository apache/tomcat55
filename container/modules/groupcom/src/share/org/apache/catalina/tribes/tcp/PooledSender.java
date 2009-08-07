package org.apache.catalina.tribes.tcp;

import java.util.LinkedList;

import org.apache.catalina.tribes.ChannelException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.List;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class PooledSender implements DataSender {
    
    private SenderQueue queue = null;
    private boolean connected;
    private int rxBufSize;
    private int txBufSize;
    private boolean waitForAck;
    private long timeout;

    public PooledSender(int queueSize) {
        queue = new SenderQueue(this,queueSize);
    }
    
    public abstract DataSender getNewDataSender();
    
    public DataSender getSender() {
        return queue.getSender(0);
    }
    
    public void returnSender(DataSender sender) {
        sender.checkKeepAlive();
        queue.returnSender(sender);
    }
    
    public synchronized void connect() throws ChannelException {
        //do nothing, happens in the socket sender itself
        queue.open();
        setConnected(true);
    }
    
    public synchronized void disconnect() {
        queue.close();
        setConnected(false);
    }
    
    
    public int getInPoolSize() {
        return queue.getInPoolSize();
    }

    public int getInUsePoolSize() {
        return queue.getInUsePoolSize();
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setRxBufSize(int rxBufSize) {
        this.rxBufSize = rxBufSize;
    }

    public void setTxBufSize(int txBufSize) {
        this.txBufSize = txBufSize;
    }

    public void setWaitForAck(boolean waitForAck) {
        this.waitForAck = waitForAck;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isConnected() {
        return connected;
    }

    public int getRxBufSize() {
        return rxBufSize;
    }

    public int getTxBufSize() {
        return txBufSize;
    }

    public boolean getWaitForAck() {
        return waitForAck;
    }

    public long getTimeout() {
        return timeout;
    }

    public boolean checkKeepAlive() {
        //do nothing, the pool checks on every return
        return false;
    }

    

    //  ----------------------------------------------------- Inner Class

    private class SenderQueue {
        private int limit = 25;

        PooledSender parent = null;

        private List notinuse = null;

        private List inuse = null;

        private boolean isOpen = true;

        public SenderQueue(PooledSender parent, int limit) {
            this.limit = limit;
            this.parent = parent;
            notinuse = new java.util.LinkedList();
            inuse = new java.util.LinkedList();
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
            return notinuse.size();
        }

        public synchronized DataSender getSender(long timeout) {
            if ( !isOpen ) throw new IllegalStateException("Queue is closed");
            DataSender sender = null;
            if ( notinuse.size() == 0 && inuse.size()<limit) {
                sender = parent.getNewDataSender();
            } else if (notinuse.size() > 0) {
                    sender = (DataSender) notinuse.remove(0);
            }            
            if ( sender != null ) inuse.add(sender);
//            System.out.println("get: in use:"+inuse.size()+" not:"+notinuse.size()+" thread:"+Thread.currentThread().getName());
            return sender;
        }

        public synchronized void returnSender(DataSender sender) {
            if ( !isOpen) {
                sender.disconnect();
                return;
            }
            //to do
            inuse.remove(sender);
            notinuse.add(sender);
//            System.out.println("return: in use:"+inuse.size()+" not:"+notinuse.size()+" thread:"+Thread.currentThread().getName());
        }

        public synchronized void close() {
            isOpen = false;
            Object[] unused = notinuse.toArray();
            Object[] used = inuse.toArray();
            for (int i = 0; i < unused.length; i++) {
                DataSender sender = (DataSender) unused[i];
                sender.disconnect();
            }//for
            for (int i = 0; i < used.length; i++) {
                DataSender sender = (DataSender) used[i];
                sender.disconnect();
            }//for
            notinuse.clear();
            inuse.clear();
            


        }

        public synchronized void open() {
            isOpen = true;
        }
    }
    
    public static void printArr(Object[] arr) {
        System.out.print("[");
        for (int i=0; i<arr.length; i++ ) {
            System.out.print(arr[i]);
            if ( (i+1)<arr.length )System.out.print(", ");
        }
        System.out.println("]");
    }

    
}