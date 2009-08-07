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

package org.apache.catalina.tribes.tcp.bio;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;

import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.tcp.PooledSender;
import org.apache.catalina.tribes.tcp.DataSender;
import org.apache.catalina.tribes.tcp.SenderState;
import org.apache.catalina.tribes.tcp.SinglePointSender;
import java.net.Inet4Address;

/**
 * Send cluster messages with a pool of sockets (25).
 * 
 * @author Filip Hanik
 * @author Peter Rossbach
 * @version 1.2
 */

public class PooledSocketSender extends PooledSender implements SinglePointSender {

    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(org.apache.catalina.tribes.tcp.bio.PooledSocketSender.class);

    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "PooledSocketSender/2.0";

    // ----------------------------------------------------- Instance Variables

    private int maxPoolSocketLimit = 25;
    private String domain;
    private InetAddress host;
    private int port;
    private SenderState senderState = new SenderState(SenderState.READY);
    private int keepAliveMaxRequestCount = -1;
    private long keepAliveTimeout = 1000*60;
    private long ackTimeout;
    private boolean resend;
    private boolean waitForAck;

    //  ----------------------------------------------------- Constructor

   /**
    * @param domain replication cluster domain (session domain)
    * @param host replication node tcp address
    * @param port replication node tcp port
    */
   public PooledSocketSender(String domain,InetAddress host, int port) {
       this(domain,host,port,25);
   }

    public PooledSocketSender(String domain,InetAddress host, int port, int poolSize) {
        super(poolSize);
        this.host = host;
        this.domain = domain;
        this.port = port;
        this.maxPoolSocketLimit = poolSize;
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

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSenderState(SenderState senderState) {
        this.senderState = senderState;
    }

    public void setKeepAliveMaxRequestCount(int keepAliveMaxRequestCount) {
        this.keepAliveMaxRequestCount = keepAliveMaxRequestCount;
    }

    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public void setTimeout(long ackTimeout) {
        this.ackTimeout = ackTimeout;
    }

    public void setResend(boolean resend) {
        this.resend = resend;
    }

    public void setWaitForAck(boolean waitForAck) {
        this.waitForAck = waitForAck;
    }

    public String getDomain() {
        return domain;
    }

    public InetAddress getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public SenderState getSenderState() {
        return senderState;
    }

    public int getKeepAliveMaxRequestCount() {
        return keepAliveMaxRequestCount;
    }

    public long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public long getTimeout() {
        return ackTimeout;
    }

    public boolean isResend() {
        return resend;
    }

    public boolean getWaitForAck() {
        return waitForAck;
    }

    //  ----------------------------------------------------- Public Methode

    

    /**
     * send message and use a pool of DataSenders
     * 
     * @param messageId Message unique identifier
     * @param data Message data
     * @throws java.io.IOException
     */
    public void sendMessage(ChannelMessage data) throws ChannelException {
        //get a socket sender from the pool
        if(!isConnected()) {
            synchronized(this) {
                if(!isConnected()) connect();
            }
        }
        SinglePointDataSender sender = (SinglePointDataSender)getSender();
        if (sender == null) {
            log.warn("Sender queue is empty. Can not send any messages.");
            return;
        }
        //send the message
        try {
            sender.sendMessage(data);
        } finally {
            //return the connection to the pool
            returnSender(sender);
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("PooledSocketSender[");
        buf.append(getHost()).append(":").append(getPort()).append("]");
        return buf.toString();
    }

    public DataSender getNewDataSender() {
        //new DataSender(
            SinglePointDataSender sender = new SinglePointDataSender(getDomain(),
                                               getHost(),
                                               getPort(),
                                               getSenderState() );
            sender.setKeepAliveMaxRequestCount(getKeepAliveMaxRequestCount());
            sender.setKeepAliveTimeout(getKeepAliveTimeout());
            sender.setTimeout(getTimeout());
            sender.setWaitForAck(getWaitForAck());
            sender.setResend(isResend());
            sender.setRxBufSize(getRxBufSize());
            sender.setTxBufSize(getTxBufSize());
            return sender;

    }
    
    public void setSuspect(boolean suspect) {
        if ( suspect ) 
            senderState.setSuspect();
        else 
            senderState.setReady();
    }
    
    public boolean getSuspect() {
        return senderState.isFailing() || senderState.isSuspect();
    }
    
    public InetAddress getAddress() {
        return getHost();
    }
    
    public void setAddress(InetAddress addr) {
        setHost(addr);
    }


}
