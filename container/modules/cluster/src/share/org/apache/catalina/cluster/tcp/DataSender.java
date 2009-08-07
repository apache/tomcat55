/*
 * Copyright 1999,2005 The Apache Software Foundation.
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import org.apache.catalina.util.StringManager;

/**
 * Send cluster messages with only one socket. Ack and keep Alive Handling is
 * supported
 * 
 * @author Peter Rossbach
 * @author Filip Hanik
 * @version 1.2
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
    private static final String info = "DataSender/1.2";

    private InetAddress address;

    private int port;

    private Socket sc = null;

    private boolean isSocketConnected = false;

    private boolean suspect;

    private long ackTimeout;

    protected long nrOfRequests = 0;

    protected long totalBytes = 0;

    protected long connectCounter = 0;

    protected long disconnectCounter = 0;

    protected long missingAckCounter = 0;

    protected long dataResendCounter = 0;

    /**
     * keep socket open for no more than one min
     */
    private long keepAliveTimeout = 60 * 1000;

    /**
     * max 100 requests before reconnecting
     */
    private int keepAliveMaxRequestCount = 100;

    /**
     * Last connect timestamp
     */
    private long keepAliveConnectTime = 0;

    /**
     * keepalive counter
     */
    private int keepAliveCount = 0;

    private boolean waitForAck = true;

    private int socketCloseCounter;

    private int socketOpenCounter;

    // ------------------------------------------------------------- Constructor

    public DataSender(InetAddress host, int port) {
        this.address = host;
        this.port = port;
        if (log.isInfoEnabled())
            log.info(sm.getString("IDataSender.create", address, new Integer(
                    port)));
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

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean isConnected() {
        return isSocketConnected;
    }

    /**
     * @param isSocketConnected
     *            The isSocketConnected to set.
     */
    protected void setSocketConnected(boolean isSocketConnected) {
        this.isSocketConnected = isSocketConnected;
    }

    public boolean isSuspect() {
        return suspect;
    }

    public boolean getSuspect() {
        return suspect;
    }

    public void setSuspect(boolean suspect) {
        this.suspect = suspect;
    }

    public long getAckTimeout() {
        return ackTimeout;
    }

    public void setAckTimeout(long ackTimeout) {
        this.ackTimeout = ackTimeout;
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

    // --------------------------------------------------------- Public Methods

    public void connect() throws java.io.IOException {
        connectCounter++;
        if (log.isDebugEnabled())
            log.debug(sm.getString("IDataSender.connect", address,
                    new Integer(port)));
        openSocket();
    }

 
    /**
     * close socket
     * 
     * @see org.apache.catalina.cluster.tcp.IDataSender#disconnect()
     * @see DataSender#closeSocket()
     */
    public void disconnect() {
        disconnectCounter++;
        if (log.isDebugEnabled())
            log.debug(sm.getString("IDataSender.disconnect", address,
                    new Integer(port)));
        closeSocket();
    }

    /**
     * Check, if time to close socket! Important for AsyncSocketSender that
     * replication thread is not fork again! <b>Only work when keepAliveTimeout
     * or keepAliveMaxRequestCount greater -1 </b>
     * @return true, is socket close
     * @see DataSender#closeSocket()
     */
    public boolean checkIfCloseSocket() {
        boolean isCloseSocket = true ;
        long ctime = System.currentTimeMillis() - this.keepAliveConnectTime;
        if ((keepAliveTimeout > -1 && ctime > this.keepAliveTimeout)
                || (keepAliveMaxRequestCount > -1 && this.keepAliveCount >= this.keepAliveMaxRequestCount)) {
            closeSocket();
        } else
            isCloseSocket = false ;
        return isCloseSocket;
    }

    /*
     * Send message
     * 
     * @see org.apache.catalina.cluster.tcp.IDataSender#sendMessage(java.lang.String,
     *      byte[])
     */
    public synchronized void sendMessage(String messageid, byte[] data)
            throws java.io.IOException {
        pushMessage(messageid, data);
    }

    /*
     * Reset sender statistics
     */
    public synchronized void resetStatistics() {
        nrOfRequests = 0;
        totalBytes = 0;
        disconnectCounter = 0;
        connectCounter = isConnected() ? 1 : 0;
        missingAckCounter = 0;
        dataResendCounter = 0;
        socketOpenCounter =isConnected() ? 1 : 0;
        socketCloseCounter = 0;
    }

    /**
     * Name of this SockerSender
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("DataSender[");
        buf.append(getAddress()).append(":").append(getPort()).append("]");
        return buf.toString();
    }

    // --------------------------------------------------------- Protected
    // Methods

    /**
     * @throws IOException
     * @throws SocketException
     */
    protected void openSocket() throws IOException, SocketException {
        socketOpenCounter++;
        if (log.isDebugEnabled())
            log.debug(sm.getString("IDataSender.openSocket", address, new Integer(
                    port)));
        sc = new Socket(getAddress(), getPort());
        if (isWaitForAck())
            sc.setSoTimeout((int) ackTimeout);
        isSocketConnected = true;
        this.keepAliveCount = 0;
        this.keepAliveConnectTime = System.currentTimeMillis();
    }

    /**
     * close socket
     * 
     * @see DataSender#disconnect()
     * @see DataSender#checkIfCloseSocket()
     */
    protected void closeSocket() {
        if(isSocketConnected) {
            socketCloseCounter++;
            if (log.isDebugEnabled())
                log.debug(sm.getString("IDataSender.socketclose",
                        address, new Integer(port)));
            try {
                sc.close();
            } catch (Exception x) {
            }
            isSocketConnected = false;
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
        if (log.isDebugEnabled() && (nrOfRequests % 100) == 0) {
            log.debug(sm.getString("IDataSender.stats", new Object[] {
                    getAddress().getHostAddress(), new Integer(getPort()),
                    new Long(totalBytes), new Long(nrOfRequests),
                    new Long(totalBytes / nrOfRequests) }));
        }
    }

    /**
     * push messages with only one socket at a time
     * 
     * @param messageid
     *            unique message id
     * @param data
     *            data to send
     * @throws java.io.IOException
     */
    protected synchronized void pushMessage(String messageid, byte[] data)
            throws java.io.IOException {
        checkIfCloseSocket();
        if (!isConnected())
            openSocket();
        try {
            sc.getOutputStream().write(data);
            sc.getOutputStream().flush();
            if (isWaitForAck())
                waitForAck(ackTimeout);
        } catch (java.io.IOException x) {
            // second try with fresh connection
            dataResendCounter++;
            if (log.isTraceEnabled())
                log.trace(sm.getString("IDataSender.send.again", address,
                        new Integer(port)));
            closeSocket();
            openSocket();
            sc.getOutputStream().write(data);
            sc.getOutputStream().flush();
            if (isWaitForAck())
                waitForAck(ackTimeout);
        }
        this.keepAliveCount++;
        checkIfCloseSocket();
        addStats(data.length);
        if (log.isTraceEnabled())
            log.trace(sm.getString("IDataSender.send.message", address,
                    new Integer(port), messageid, new Long(data.length)));

    }

    /**
     * Wait for Acknowledgement from other server
     * 
     * @param timeout
     * @throws java.io.IOException
     */
    protected void waitForAck(long timeout) throws java.io.IOException {
        try {
            int i = sc.getInputStream().read();
            while ((i != -1) && (i != 3)) {
                i = sc.getInputStream().read();
            }
        } catch (java.net.SocketTimeoutException x) {
            missingAckCounter++;
            log.warn(sm.getString("IDataSender.missing.ack", getAddress(),
                    new Integer(getPort()), new Long(this.ackTimeout)));
            throw x;
        }
    }
}