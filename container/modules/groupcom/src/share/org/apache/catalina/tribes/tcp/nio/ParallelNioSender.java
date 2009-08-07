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
package org.apache.catalina.tribes.tcp.nio;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.io.ClusterData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.tcp.MultiPointSender;

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
public class ParallelNioSender implements MultiPointSender {
    protected long timeout = 15000;
    protected long selectTimeout = 1000; 
    protected boolean waitForAck = false;
    protected int retryAttempts=0;
    protected int keepAliveCount = Integer.MAX_VALUE;
    protected Selector selector;
    protected HashMap nioSenders = new HashMap();
    protected boolean directBuf = false;
    protected int rxBufSize = 43800;
    protected int txBufSize = 25188;
    protected boolean suspect = false;
    private boolean connected;

    public ParallelNioSender(long timeout, 
                             boolean waitForAck,
                             int retryAttempts,
                             boolean directBuf,
                             int rxBufSize,
                             int txBufSize) throws IOException {
        this.timeout = timeout;
        this.waitForAck = waitForAck;
        this.retryAttempts = retryAttempts;
        selector = Selector.open();
        this.directBuf = directBuf;
        this.rxBufSize = rxBufSize;
        this.txBufSize = txBufSize;
    }
    
    
    public synchronized void sendMessage(Member[] destination, ChannelMessage msg) throws ChannelException {
        long start = System.currentTimeMillis();
        byte[] data = XByteBuffer.createDataPackage((ClusterData)msg);
        NioSender[] senders = setupForSend(destination);
        connect(senders);
        setData(senders,data);
        
        int remaining = senders.length;
        try {
            //loop until complete, an error happens, or we timeout
            long delta = System.currentTimeMillis() - start;
            while ( (remaining>0) && (delta<timeout) ) {
                remaining -= doLoop(selectTimeout,retryAttempts);
            }
            if ( remaining > 0 ) {
                //timeout has occured
                ChannelException cx = new ChannelException("Operation has timed out("+timeout+" ms.).");
                for (int i=0; i<senders.length; i++ ) {
                    if (!senders[i].isComplete() ) cx.addFaultyMember(senders[i].getDestination());
                }
                throw cx;
            }
        } catch (Exception x ) {
            try { this.disconnect(); } catch (Exception ignore) {}
            if ( x instanceof ChannelException ) throw (ChannelException)x;
            else throw new ChannelException(x);
        }
        
    }
    
    private int doLoop(long selectTimeOut, int maxAttempts) throws IOException, ChannelException {
        int completed = 0;
        int selectedKeys = selector.select(selectTimeOut);
        
        if (selectedKeys == 0) {
            return 0;
        }
        
        Iterator it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey sk = (SelectionKey) it.next();
            it.remove();
            int readyOps = sk.readyOps();
            sk.interestOps(sk.interestOps() & ~readyOps);
            NioSender sender = (NioSender) sk.attachment();
            try {
                if (sender.process(sk)) {
                    sender.reset();
                    completed++;
                    sender.setComplete(true);
                }//end if
            } catch (Exception x) {
                byte[] data = sender.getMessage();
                int attempt = sender.getAttempt()+1;
                if ( sender.getAttempt() >= maxAttempts && maxAttempts>0 ) {
                    try { 
                        sender.disconnect(); 
                        sender.connect();
                        sender.setAttempt(attempt);
                        sender.setMessage(data);
                    }catch ( Exception ignore){
                        //dont report the error on a resend
                    }
                } else {
                    ChannelException cx = new ChannelException(x);
                    cx.addFaultyMember(sender.getDestination());
                    throw cx;
                }//end if
            }
        }
        return completed;

    }
    
    private void connect(NioSender[] senders) throws ChannelException {
        ChannelException x = null;
        for (int i=0; i<senders.length; i++ ) {
            try {
                if (!senders[i].isConnected()) senders[i].connect();
            }catch ( IOException io ) {
                if ( x==null ) x = new ChannelException(io);
                x.addFaultyMember(senders[i].getDestination());
            }
        }
        if ( x != null ) throw x;
    }
    
    private void setData(NioSender[] senders, byte[] data) throws ChannelException {
        ChannelException x = null;
        for (int i=0; i<senders.length; i++ ) {
            try {
                senders[i].setMessage(data);
            }catch ( IOException io ) {
                if ( x==null ) x = new ChannelException(io);
                x.addFaultyMember(senders[i].getDestination());
            }
        }
        if ( x != null ) throw x;
    }
    
    
    private NioSender[] setupForSend(Member[] destination) {
        NioSender[] result = new NioSender[destination.length];
        for ( int i=0; i<destination.length; i++ ) {
            NioSender sender = (NioSender)nioSenders.get(destination[i]);
            if ( sender == null ) {
                sender = new NioSender(destination[i]);
                nioSenders.put(destination[i],sender);
            }
            sender.reset();
            sender.setSelector(selector);
            sender.setDirect(directBuf);
            sender.setRxBufSize(rxBufSize);
            sender.setTxBufSize(txBufSize);
            sender.setWaitForAck(waitForAck);
            result[i] = sender;
        }
        return result;
    }
    
    public void connect() {
        //do nothing, we connect on demand
        setConnected(true);
    }
    
    
    private synchronized void close() throws ChannelException  {
        ChannelException x = null;
        Object[] members = nioSenders.keySet().toArray();
        for (int i=0; i<members.length; i++ ) {
            Member mbr = (Member)members[i];
            try {
                NioSender sender = (NioSender)nioSenders.get(mbr);
                sender.disconnect();
            }catch ( Exception e ) {
                if ( x == null ) x = new ChannelException(e);
                x.addFaultyMember(mbr);
            }
            nioSenders.remove(mbr);
        }
        if ( x != null ) throw x;
    }
    
    public synchronized void disconnect() {
        try {close(); }catch (Exception x){}
        setConnected(false);
    }
    
    public void finalize() {
        try {disconnect(); }catch ( Exception ignore){}
    }
    
    public boolean getSuspect() {
        return suspect;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setSuspect(boolean suspect) {
        this.suspect = suspect;
    }
    
    public void setUseDirectBuffer(boolean directBuf) {
        this.directBuf = directBuf;
    }
    
    public void setMaxRetryAttempts(int attempts) {
        this.retryAttempts = attempts;
    }
    
    public void setTxBufSize(int size) {
        this.txBufSize = size;
    }
    
    public void setRxBufSize(int size) {
        this.rxBufSize = size;
    }
    
    public void setWaitForAck(boolean wait) {
        this.waitForAck = wait;
    }
    
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    public boolean checkKeepAlive() {
        //throw new UnsupportedOperationException("Method ParallelNioSender.checkKeepAlive() not implemented");
        return false;
    }

}