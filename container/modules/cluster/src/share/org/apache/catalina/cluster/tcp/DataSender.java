/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Date;

import org.apache.catalina.cluster.ClusterMessage;
import org.apache.catalina.cluster.io.XByteBuffer;
import org.apache.catalina.util.StringManager;

/**
 * Send cluster messages with only one socket. Ack and keep Alive Handling is
 * supported
 * 
 * @author Peter Rossbach
 * @author Filip Hanik
 * @version $Id$
 * @since 5.5.7
 */
public class DataSender implements IDataSender {

    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(DataSender.class);

    /**
     * The string manager for this package.
     */
    protected static StringManager sm = StringManager
            .getManager(Constants.Package);

    // ----------------------------------------------------- Instance Variables

    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "DataSender/2.2";

    /**
     * receiver address
     */
    private InetAddress address;

    /**
     * receiver port
     */
    private int port;

    
    /**
     * cluster domain
     */
    private String domain;

    /**
     * current sender socket
     */
    private Socket socket = null;

    /**
     * is Socket really connected
     */
    private boolean isSocketConnected = false;

    /**
     * Message transfer over socket ?
     */
    private boolean isMessageTransferStarted = false;

    /**
     * sender is in suspect state (last transfer failed)
     */
    private SenderState senderState = new SenderState();

    private int rxBufSize = 25188;
    private int txBufSize = 43800;
    private int timeout = 30000;
    private boolean tcpNoDelay = true;
    private boolean soKeepAlive = false;
    private boolean ooBInline = true;
    private boolean soReuseAddress = true;
    private boolean soLingerOn = false;
    private int soLingerTime = 3;
    private int soTrafficClass = 0x04 | 0x08 | 0x010;
     
    /**
     * createTime
     */
    protected long createTime ;

    /**
     * number of requests
     */
    protected long nrOfRequests = 0;

    /**
     * total bytes to transfer
     */
    protected long totalBytes = 0;

    /**
     * number of connects
     */
    protected long connectCounter = 0;

    /**
     * number of explizit disconnects
     */
    protected long disconnectCounter = 0;

    /**
     * number of failing acks
     */
    protected long missingAckCounter = 0;

    /**
     * number of data resends (second trys after socket failure)
     */
    protected long dataResendCounter = 0;

    /**
     * number of data failure sends 
     */
    protected long dataFailureCounter = 0;
    
    /**
     * doProcessingStats
     */
    protected boolean doProcessingStats = false;

    /**
     * proessingTime
     */
    protected long processingTime = 0;
    
    /**
     * min proessingTime
     */
    protected long minProcessingTime = Long.MAX_VALUE ;

    /**
     * max proessingTime
     */
    protected long maxProcessingTime = 0;
   
    /**
     * doWaitAckStats
     */
    protected boolean doWaitAckStats = false;

    /**
     * waitAckTime
     */
    protected long waitAckTime = 0;
    
    /**
     * min waitAckTime
     */
    protected long minWaitAckTime = Long.MAX_VALUE ;

    /**
     * max waitAckTime
     */
    protected long maxWaitAckTime = 0;

    /**
     * keep socket open for no more than one min
     */
    private long keepAliveTimeout = 60 * 1000;

    /**
     * max requests before reconnecting (default -1 unlimited)
     */
    private int keepAliveMaxRequestCount = -1;

    /**
     * Last connect timestamp
     */
    protected long keepAliveConnectTime = 0;

    /**
     * keepalive counter
     */
    protected int keepAliveCount = 0;

    /**
     * wait for receiver Ack
     */
    private boolean waitForAck = false;

    /**
     * number of socket close
     */
    private int socketCloseCounter = 0 ;

    /**
     * number of socket open
     */
    private int socketOpenCounter = 0 ;

    /**
     * number of socket open failures
     */
    private int socketOpenFailureCounter = 0 ;

    /**
     * After failure make a resend
     */
    private boolean resend = true ;
    
    // ------------------------------------------------------------- Constructor
    
    public DataSender(String domain,InetAddress host, int port) {
        this.address = host;
        this.port = port;
        this.domain = domain;
        this.createTime = System.currentTimeMillis() ;
        if (log.isDebugEnabled())
            log.debug(sm.getString("IDataSender.create",address, new Integer(port), new Date(createTime)));
    }

    public DataSender(String domain,InetAddress host, int port, SenderState state) {
        this(domain,host,port);
        if ( state != null ) this.senderState = state;
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
     * @return the createTime
     */
    public long getCreateTime() {
        return createTime;
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

    /**
     * @return Returns the avg totalBytes/nrOfRequests.
     */
    public long getAvgMessageSize() {
        if ( nrOfRequests > 0 ) {
            return totalBytes / nrOfRequests;
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the avg processingTime/nrOfRequests.
     */
    public double getAvgProcessingTime() {
        if ( nrOfRequests > 0 ) {
            return ((double)processingTime) / nrOfRequests;
        } else {
            return 0;
        }
    }
 
    /**
     * @return Returns the maxProcessingTime.
     */
    public long getMaxProcessingTime() {
        return maxProcessingTime;
    }
    
    /**
     * @return Returns the minProcessingTime.
     */
    public long getMinProcessingTime() {
        return minProcessingTime;
    }
    
    /**
     * @return Returns the processingTime.
     */
    public long getProcessingTime() {
        return processingTime;
    }
    
    /**
     * @return Returns the doProcessingStats.
     */
    public boolean isDoProcessingStats() {
        return doProcessingStats;
    }
    
    /**
     * @param doProcessingStats The doProcessingStats to set.
     */
    public void setDoProcessingStats(boolean doProcessingStats) {
        this.doProcessingStats = doProcessingStats;
    }
 
 
    /**
     * @return Returns the doWaitAckStats.
     */
    public boolean isDoWaitAckStats() {
        return doWaitAckStats;
    }
    
    /**
     * @param doWaitAckStats The doWaitAckStats to set.
     */
    public void setDoWaitAckStats(boolean doWaitAckStats) {
        this.doWaitAckStats = doWaitAckStats;
    }
    
    /**
     * @return Returns the avg waitAckTime/nrOfRequests.
     */
    public double getAvgWaitAckTime() {
        if ( nrOfRequests > 0 ) {
            return ((double)waitAckTime) / nrOfRequests;
        } else {
            return 0;
        }
    }
 
    /**
     * @return Returns the maxWaitAckTime.
     */
    public long getMaxWaitAckTime() {
        return maxWaitAckTime;
    }
    
    /**
     * @return Returns the minWaitAckTime.
     */
    public long getMinWaitAckTime() {
        return minWaitAckTime;
    }
    
    /**
     * @return Returns the waitAckTime.
     */
    public long getWaitAckTime() {
        return waitAckTime;
    }
    
    /**
     * @return Returns the connectCounter.
     */
    public long getConnectCounter() {
        return connectCounter;
    }

    /**
     * @return Returns the disconnectCounter.
     */
    public long getDisconnectCounter() {
        return disconnectCounter;
    }

    /**
     * @return Returns the missingAckCounter.
     */
    public long getMissingAckCounter() {
        return missingAckCounter;
    }

    /**
     * @return Returns the socketOpenCounter.
     */
    public int getSocketOpenCounter() {
        return socketOpenCounter;
    }
    
    /**
     * @return Returns the socketOpenFailureCounter.
     */
    public int getSocketOpenFailureCounter() {
        return socketOpenFailureCounter;
    }

    /**
     * @return Returns the socketCloseCounter.
     */
    public int getSocketCloseCounter() {
        return socketCloseCounter;
    }

    /**
     * @return Returns the dataResendCounter.
     */
    public long getDataResendCounter() {
        return dataResendCounter;
    }

    /**
     * @return Returns the dataFailureCounter.
     */
    public long getDataFailureCounter() {
        return dataFailureCounter;
    }
    
    /**
     * @param address The address to set.
     */
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }

    
    /**
     * @param port The port to set.
     */
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }

    /**
     * @return Returns the domain.
     */
    public String getDomain() {
        return domain;
    }
    
    /**
     * @param domain The domain to set.
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public boolean isConnected() {
        return isSocketConnected;
    }

    /**
     * @return Is DataSender send a message
     */
    public boolean isMessageTransferStarted() {
        return isMessageTransferStarted;
    }
    
    /**
     * @param isSocketConnected
     *            The isSocketConnected to set.
     */
    protected void setSocketConnected(boolean isSocketConnected) {
        this.isSocketConnected = isSocketConnected;
    }

    public boolean isSuspect() {
        return senderState.isSuspect() || senderState.isFailing();
    }

    public boolean getSuspect() {
        return isSuspect();
    }

    public void setSuspect(boolean suspect) {
        if ( suspect ) 
            this.senderState.setSuspect();
        else
            this.senderState.setReady();
    }

    /**
     * @return the ooBInline
     * @since 5.5.25
     */
    public boolean isOoBInline() {
        return ooBInline;
    }

    /**
     * @param ooBInline the ooBInline to set
     * @since 5.5.25
     */
    public void setOoBInline(boolean ooBInline) {
        this.ooBInline = ooBInline;
    }

    /**
     * @return the rxBufSize
     * @since 5.5.25
     */
    public int getRxBufSize() {
        return rxBufSize;
    }

    /**
     * @param rxBufSize the rxBufSize to set
     * @since 5.5.25
     */
    public void setRxBufSize(int rxBufSize) {
        this.rxBufSize = rxBufSize;
    }

    /**
     * @return the soKeepAlive
     * @since 5.5.25
     */
    public boolean isSoKeepAlive() {
        return soKeepAlive;
    }

    /**
     * @param soKeepAlive the soKeepAlive to set
     * @since 5.5.25
     */
    public void setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
    }

    /**
     * @return the soLingerOn
     * @since 5.5.25
     */
    public boolean isSoLingerOn() {
        return soLingerOn;
    }

    /**
     * @param soLingerOn the soLingerOn to set
     * @since 5.5.25
     */
    public void setSoLingerOn(boolean soLingerOn) {
        this.soLingerOn = soLingerOn;
    }

    /**
     * @return the soLingerTime
     * @since 5.5.25
     */
    public int getSoLingerTime() {
        return soLingerTime;
    }

    /**
     * @param soLingerTime the soLingerTime to set
     * @since 5.5.25
     */
    public void setSoLingerTime(int soLingerTime) {
        this.soLingerTime = soLingerTime;
    }

    /**
     * @return the soReuseAddress
     * @since 5.5.25
     */
    public boolean isSoReuseAddress() {
        return soReuseAddress;
    }

    /**
     * @param soReuseAddress the soReuseAddress to set
     * @since 5.5.25
     */
    public void setSoReuseAddress(boolean soReuseAddress) {
        this.soReuseAddress = soReuseAddress;
    }

    /**
     * @return the soTrafficClass
     * @since 5.5.25
     */
    public int getSoTrafficClass() {
        return soTrafficClass;
    }

    /**
     * @param soTrafficClass the soTrafficClass to set
     * @since 5.5.25
     */
    public void setSoTrafficClass(int soTrafficClass) {
        this.soTrafficClass = soTrafficClass;
    }

    /**
     * @return the tcpNoDelay
     * @since 5.5.25
     */
    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    /**
     * @param tcpNoDelay the tcpNoDelay to set
     * @since 5.5.25
     */
    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    /**
     * @return the timeout
     * @since 5.5.25
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     * @since 5.5.25
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the txBufSize
     * @since 5.5.25
     */
    public int getTxBufSize() {
        return txBufSize;
    }

    /**
     * @param txBufSize the txBufSize to set
     * @since 5.5.25
     */
    public void setTxBufSize(int txBufSize) {
        this.txBufSize = txBufSize;
    }

    /**
     * @deprecated since 5.5.25 use timeout instead
     */
    public long getAckTimeout() {
        return timeout;
    }

    /**
     * @deprecated since 5.5.25 use timeout instead
     */
    public void setAckTimeout(long ackTimeout) {
        this.timeout = (int)ackTimeout;
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

    /**
     * @return Returns the waitForAck.
     */
    public boolean isWaitForAck() {
        return waitForAck;
    }

    /**
     * @param waitForAck
     *            The waitForAck to set.
     */
    public void setWaitForAck(boolean waitForAck) {
        this.waitForAck = waitForAck;
    }

    /**
     * @return Returns the resend.
     */
    public boolean isResend() {
        return resend;
    }
    /**
     * @param resend The resend to set.
     */
    public void setResend(boolean resend) {
        this.resend = resend;
    }
    /**
     * @return Returns the socket.
     */
    public Socket getSocket() {
        return socket;
    }

    public SenderState getSenderState() {
        return senderState;
    }

    /**
     * @param socket The socket to set.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    // --------------------------------------------------------- Public Methods

    /**
     * Connect other cluster member receiver 
     * @see org.apache.catalina.cluster.tcp.IDataSender#connect()
     */
    public synchronized void connect() throws java.io.IOException {
        if(!isMessageTransferStarted) {
            openSocket();
            if(isConnected()) {
                connectCounter++;
                if (log.isDebugEnabled())
                    log.debug(sm.getString("IDataSender.connect", address.getHostAddress(),
                            new Integer(port),new Long(connectCounter)));
            }
        } else 
            if (log.isWarnEnabled())
               log.warn(sm.getString("IDataSender.message.create", address.getHostAddress(),new Integer(port)));
   }

 
    /**
     * disconnect and close socket
     * 
     * @see IDataSender#disconnect()
     */
    public synchronized void disconnect() {
        if(!isMessageTransferStarted) {
            boolean connect = isConnected() ;
            closeSocket();
            if(connect) {
                disconnectCounter++;
                if (log.isDebugEnabled())
                    log.debug(sm.getString("IDataSender.disconnect", address.getHostAddress(),
                        new Integer(port),new Long(disconnectCounter)));
            }
        } else 
            if (log.isWarnEnabled())
               log.warn(sm.getString("IDataSender.message.disconnect", address.getHostAddress(),new Integer(port)));
        
    }

    /**
     * Check, if time to close socket! Important for AsyncSocketSender that
     * replication thread is not fork again! <b>Only work when keepAliveTimeout
     * or keepAliveMaxRequestCount greater -1 </b>
     * FIXME Can we close a socket when a message wait for ack?
     * @return true, is socket close
     * @see DataSender#closeSocket()
     */
    public synchronized boolean checkKeepAlive() {
        boolean isCloseSocket = true ;
        if(!isMessageTransferStarted) {
            if(isConnected()) {
                if ((keepAliveTimeout > -1 && (System.currentTimeMillis() - keepAliveConnectTime) > keepAliveTimeout)
                    || (keepAliveMaxRequestCount > -1 && keepAliveCount >= keepAliveMaxRequestCount)) {
                        closeSocket();
               } else
                    isCloseSocket = false ;
            }
        } else
            isCloseSocket = false ;
        
        return isCloseSocket;
    }

    /**
     * Send message
     * 
     * @see org.apache.catalina.cluster.tcp.IDataSender#sendMessage(,
     *      ClusterData)
     */
    public synchronized void sendMessage(ClusterData data)
            throws java.io.IOException {
        pushMessage(data);
    }

    /**
     * Reset sender statistics
     */
    public synchronized void resetStatistics() {
        nrOfRequests = 0;
        totalBytes = 0;
        disconnectCounter = 0;
        connectCounter = isConnected() ? 1 : 0;
        missingAckCounter = 0;
        dataResendCounter = 0;
        dataFailureCounter = 0 ;
        socketOpenCounter =isConnected() ? 1 : 0;
        socketOpenFailureCounter = 0 ;
        socketCloseCounter = 0;
        processingTime = 0 ;
        minProcessingTime = Long.MAX_VALUE ;
        maxProcessingTime = 0 ;
        waitAckTime = 0 ;
        minWaitAckTime = Long.MAX_VALUE ;
        maxWaitAckTime = 0 ;
    }

    /**
     * Name of this SockerSender
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("DataSender[");
        buf.append(getAddress()).append(":").append(getPort()).append("]");
        return buf.toString();
    }

    // --------------------------------------------------------- Protected Methods
 
    /**
     * open real socket and set time out when waitForAck is enabled
     * is socket open return directly
     * @throws IOException
     * @throws SocketException
     */
    protected void openSocket() throws IOException, SocketException {
       if(isConnected())
           return ;
       try {
            createSocket();
            if (isWaitForAck())
                socket.setSoTimeout((int) getTimeout());
            isSocketConnected = true;
            socketOpenCounter++;
            this.keepAliveCount = 0;
            this.keepAliveConnectTime = System.currentTimeMillis();
            if (log.isDebugEnabled())
                log.debug(sm.getString("IDataSender.openSocket", address
                        .getHostAddress(), new Integer(port),new Long(socketOpenCounter)));
      } catch (IOException ex1) {
            socketOpenFailureCounter++ ;
            if (log.isDebugEnabled())
                log.debug(sm.getString("IDataSender.openSocket.failure",
                        address.getHostAddress(), new Integer(port),new Long(socketOpenFailureCounter)), ex1);
            throw ex1;
        }
        
     }

    /**
     * Create new Socket and use actTimeout (>0) as connect timeout
     * 
     * @throws IOException
     * @throws SocketException
     */
    protected void createSocket() throws IOException, SocketException {
        SocketAddress sockAddr = new InetSocketAddress(getAddress(), getPort());
        socket = new Socket();
        int timeout = getTimeout();
        if (timeout > 0) {
           socket.connect(sockAddr, timeout);
           socket.setSoTimeout(timeout);
        } else {
           socket.connect(sockAddr);
        }
        socket.setSendBufferSize(getTxBufSize());
        socket.setReceiveBufferSize(getRxBufSize());
        socket.setTcpNoDelay(isTcpNoDelay());
        socket.setKeepAlive(isSoKeepAlive());
        socket.setReuseAddress(isSoReuseAddress());
        socket.setOOBInline(isOoBInline());
        socket.setSoLinger(isSoLingerOn(),getSoLingerTime());
        socket.setTrafficClass(getSoTrafficClass());

    }

    /**
     * close socket
     * 
     * @see DataSender#disconnect()
     * @see DataSender#closeSocket()
     */
    protected void closeSocket() {
        if(isConnected()) {
             if (socket != null) {
                try {
                    socket.close();
                } catch (IOException x) {
                } finally {
                    socket = null;
                }
            }
            this.keepAliveCount = 0;
            isSocketConnected = false;
            socketCloseCounter++;
            if (log.isDebugEnabled())
                log.debug(sm.getString("IDataSender.closeSocket",
                        address.getHostAddress(), new Integer(port),new Long(socketCloseCounter)));
       }
    }

    /**
     * Add statistic for this socket instance
     * 
     * @param length
     */
    protected void addStats(int length) {
        nrOfRequests++;
        totalBytes += length;
        if (log.isInfoEnabled() && (nrOfRequests % 1000) == 0) {
            log.info(sm.getString("IDataSender.stats", new Object[] {
                    getAddress().getHostAddress(), new Integer(getPort()),
                    new Long(totalBytes), new Long(nrOfRequests),
                    new Long(totalBytes / nrOfRequests),
                    new Long(getProcessingTime()),
                    new Double(getAvgProcessingTime())}));
        }
    }

    /**
     * Add processing stats times
     * @param startTime
     */
    protected void addProcessingStats(long startTime) {
        long time = System.currentTimeMillis() - startTime ;
        if(time < minProcessingTime)
            minProcessingTime = time ;
        if( time > maxProcessingTime)
            maxProcessingTime = time ;
        processingTime += time ;
    }
    
    /**
     * Add waitAck stats times
     * @param startTime
     */
    protected void addWaitAckStats(long startTime) {
        long time = System.currentTimeMillis() - startTime ;
        if(time < minWaitAckTime)
            minWaitAckTime = time ;
        if( time > maxWaitAckTime)
            maxWaitAckTime = time ;
        waitAckTime += time ;
    }
    /**
     * Push messages with only one socket at a time
     * Wait for ack is needed and make auto retry when write message is failed.
     * After sending error close and reopen socket again.
     * 
     * After successfull sending update stats
     * 
     * WARNING: Subclasses must be very carefull that only one thread call this pushMessage at once!!!
     * 
     * @see #closeSocket()
     * @see #openSocket()
     * @see #writeData(ClusterData)
     * 
     * @param data
     *            data to send
     * @throws java.io.IOException
     * @since 5.5.10
     */
    protected void pushMessage( ClusterData data)
            throws java.io.IOException {
        long time = 0 ;
        if(doProcessingStats) {
            time = System.currentTimeMillis();
        }
        boolean messageTransfered = false ;
        synchronized(this) {
            checkKeepAlive();
            if (!isConnected())
                openSocket();
            else if(keepAliveTimeout > -1)
                this.keepAliveConnectTime = System.currentTimeMillis();
        }
        IOException exception = null;
        try {
             writeData(data);
             messageTransfered = true ;
        } catch (java.io.IOException x) {
            if(data.getResend() == ClusterMessage.FLAG_ALLOWED || 
                    (data.getResend() == ClusterMessage.FLAG_DEFAULT && isResend() )) {
                // second try with fresh connection
                dataResendCounter++;
                if (log.isTraceEnabled())
                    log.trace(sm.getString("IDataSender.send.again", address.getHostAddress(),
                            new Integer(port)),x);
                synchronized(this) {
                    closeSocket();
                    openSocket();
                }
                try {
                    writeData(data);
                    messageTransfered = true;
                } catch (IOException xx) {
                    exception = xx;
                    throw xx ;
                }
            } else {
                synchronized(this) {
                    closeSocket();
                }
                exception = x;
                throw x ;
            }
        } finally {
            this.keepAliveCount++;
            checkKeepAlive();
            if(doProcessingStats) {
                addProcessingStats(time);
            }
            if(messageTransfered) {
                addStats(data.getMessage().length);
                if (log.isTraceEnabled()) {
                    log.trace(sm.getString("IDataSender.send.message", address.getHostAddress(),
                        new Integer(port), data.getUniqueId(), new Long(data.getMessage().length)));
                }
            } else {
                dataFailureCounter++;
                throw exception;
            }
        }
    }

    /**
     * Sent real cluster Message to socket stream
     * FIXME send compress
     * @param data
     * @throws IOException
     * @since 5.5.10
     */
    protected void writeData(ClusterData data) throws IOException { 
        synchronized(this) {
            isMessageTransferStarted = true ;
        }
        try {
            byte[] message = data.getMessage();
            OutputStream out = socket.getOutputStream();
            out.write(XByteBuffer.createDataPackage(message,data.getCompress()));
            out.flush();
            if (isWaitForAck())
                waitForAck(getTimeout());
        } finally {
            synchronized(this) {
                isMessageTransferStarted = false ;
            }
        }
    }

    /**
     * Wait for Acknowledgement from other server
     * FIXME Please, not wait only for three charcters, better control that the wait ack message is correct.
     * @param timeout
     * @throws java.io.IOException
     * @throws java.net.SocketTimeoutException
     */
    protected void waitForAck(long timeout) throws java.io.IOException {
        long time = 0 ;
        if(doWaitAckStats) {
            time = System.currentTimeMillis();
        }
        try {
            int bytesRead = 0;
            if ( log.isTraceEnabled() ) 
                log.trace(sm.getString("IDataSender.ack.start",getAddress(), new Integer(socket.getLocalPort())));
            int i = socket.getInputStream().read();
            while ((i != -1) && (i != 3) && bytesRead < 10) {
                if ( log.isTraceEnabled() ) 
                    log.trace(sm.getString("IDataSender.ack.read",getAddress(), new Integer(socket.getLocalPort()),new Character((char) i)));
                bytesRead++;
                i = socket.getInputStream().read();
            }
            if (i != 3) {
                if (i == -1) {
                    throw new IOException(sm.getString("IDataSender.ack.eof",getAddress(), new Integer(socket.getLocalPort())));
                } else {
                    throw new IOException(sm.getString("IDataSender.ack.wrong",getAddress(), new Integer(socket.getLocalPort())));
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace(sm.getString("IDataSender.ack.receive", getAddress(),new Integer(socket.getLocalPort())));
                }
            }
        } catch (IOException x) {
            missingAckCounter++;
            String errmsg = sm.getString("IDataSender.ack.missing", getAddress(),
                                         new Integer(socket.getLocalPort()), 
                                         new Long(this.timeout));
            if ( !this.isSuspect() ) {
                this.setSuspect(true);
            }
            if ( log.isDebugEnabled() ) log.debug(errmsg, x);
            throw x;
        } finally {
            if(doWaitAckStats) {
                addWaitAckStats(time);
            }
        }
    }
}
