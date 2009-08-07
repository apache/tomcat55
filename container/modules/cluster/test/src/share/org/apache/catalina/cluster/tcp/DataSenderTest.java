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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import junit.framework.TestCase;

/**
 * @author Peter Rossbach
 * 
 * @version $Revision$ $Date$
 */
public class DataSenderTest extends TestCase {
    
    /**
     * Test that close Socket before socket open again!
     * @throws Exception
     */
    public void testOpenAgain() throws Exception {
        DataSender sender = createMockDataSender(); 
        assertEquals(1,sender.getSocketOpenCounter());
        assertEquals(0,sender.getSocketCloseCounter());
        sender.openSocket() ;
        assertEquals(1,sender.getSocketOpenCounter());
        assertEquals(0,sender.getSocketCloseCounter());
        sender.closeSocket() ;
        sender.openSocket() ;
        assertEquals(1,sender.getSocketCloseCounter());
        assertEquals(2,sender.getSocketOpenCounter());
    }
    
    /**
     * Test Connect/disconnet open and close underlying sockets
     * @throws Exception
     */
    public void testConnectDisconnect() throws Exception {
        InetAddress host = InetAddress.getByName("127.0.0.1");
        DataSender sender = new MockDataSender("catalina",host, 3434);
        sender.connect() ;
        assertTrue(sender.isConnected());
        assertEquals(1,sender.getSocketOpenCounter());
        assertEquals(0,sender.getSocketCloseCounter());
        assertEquals(1,sender.getConnectCounter());
        sender.disconnect();
        assertFalse(sender.isConnected());
        assertEquals(1,sender.getSocketCloseCounter());
        assertEquals(1,sender.getDisconnectCounter());
   }
    
    /**
     * Test Socket setup and OpenClose Counter
     * @throws Exception
     */
    public void testOpenCloseSocketCounter() throws Exception {
        DataSender sender = createMockDataSender();
        assertEquals(0, sender.getSocket().getSoTimeout());
        sender.closeSocket();
        assertEquals(1, sender.getSocketOpenCounter());
        assertEquals(1, sender.getSocketCloseCounter());
    }

    /**
     * Test Socket IOException with SocketCounter increment
     * @throws Exception
     */
    public void testFailedOpenSocketCounter() throws Exception {
        InetAddress host = InetAddress.getByName("127.0.0.1");
        DataSender sender = new MockFailedDataSender("catalina",host, 3434);
        try {
            sender.openSocket();
            fail("Sender not send expected IOException");
        } catch (IOException ioe) {
            assertEquals(0, sender.getSocketOpenCounter());
            assertEquals(1, sender.getSocketOpenFailureCounter());
        }
    }

    /**
     * read ack from receiver
     * @throws Exception
     */
    public void testWaitForAck() throws Exception {
        DataSender sender = createMockDataSender();
        assertNotNull(sender.getSocket());
        sender.waitForAck(15000);  
        ByteArrayInputStream stream = (ByteArrayInputStream)sender.getSocket().getInputStream();
        assertEquals(-1,stream.read());
    }
    
    /**
     * @return
     * @throws UnknownHostException
     * @throws IOException
     * @throws SocketException
     */
    private DataSender createMockDataSender() throws UnknownHostException, IOException, SocketException {
        InetAddress host = InetAddress.getByName("127.0.0.1");
        DataSender sender = new MockDataSender("catalina",host, 3434);
        sender.openSocket();
        return sender;
    }

    /**
     * Send message with  wait ack and simulate ack Exceptions
     * @throws Exception
     */
    public void testWriteData()throws Exception {
        DataSender sender = createMockDataSender();
        ClusterData data = new ClusterData("test", "123",new byte[]{ 1,2,3 }, System.currentTimeMillis() );
        sender.writeData(data) ;
        ByteArrayOutputStream stream = (ByteArrayOutputStream)sender.getSocket().getOutputStream();
        assertEquals(21,stream.size());
        ByteArrayInputStream istream = (ByteArrayInputStream)sender.getSocket().getInputStream();
        assertEquals(-1,istream.read());    
        MockSocket socket =((MockSocket)sender.getSocket());
        socket.reset();
        socket.setReadIOException(true);
        try {
            sender.writeData(data);
            fail("Missing Ack IOExcpetion") ;
        } catch (IOException ioe) {} ;
        socket.reset();
        socket.setReadIOException(false);
        socket.setReadSocketTimeoutException(true);
        try {
            sender.writeData(data);
            fail("Missing Ack SocketTimeoutException") ;
        } catch (SocketTimeoutException soe) {} ;               

    }
    
    /**
     * Send message without wait ack
     * @throws Exception
     */
    public void testWriteDataWithOutAck()throws Exception {
        InetAddress host = InetAddress.getByName("127.0.0.1");
        DataSender sender = new MockDataSender("catalina",host, 3434);
        sender.setWaitForAck(false);
        sender.openSocket();
        ClusterData data = new ClusterData("test", "123",new byte[]{ 1,2,3 }, System.currentTimeMillis() );
        sender.writeData(data) ;
        ByteArrayOutputStream stream = (ByteArrayOutputStream)sender.getSocket().getOutputStream();
        assertEquals(21,stream.size());
        ByteArrayInputStream istream = (ByteArrayInputStream)sender.getSocket().getInputStream();
        assertEquals(3,TcpReplicationThread.ACK_COMMAND.length);
        assertEquals(TcpReplicationThread.ACK_COMMAND[0],istream.read());        
        assertEquals(TcpReplicationThread.ACK_COMMAND[1],istream.read());        
        assertEquals(TcpReplicationThread.ACK_COMMAND[2],istream.read());   
     }
    
    /**
     * Check close socket fro keep alive handling is correct (number of request and timeout
     * @throws Exception
     */
    public void testcheckKeepAlive() throws Exception {
        DataSender sender = createMockDataSender() ;
        assertFalse(sender.checkKeepAlive()) ;
        sender.setKeepAliveMaxRequestCount(1);
        sender.keepAliveCount = 1;
        assertTrue(sender.checkKeepAlive());
        assertEquals(1,sender.getSocketCloseCounter());
        assertEquals(0,sender.getKeepAliveCount());
        sender.openSocket();
        assertEquals(0,sender.getKeepAliveCount());
        sender.setKeepAliveMaxRequestCount(100);
        sender.keepAliveConnectTime = System.currentTimeMillis() - sender.getKeepAliveTimeout() ;
        assertFalse(sender.checkKeepAlive());
        assertTrue(sender.isConnected());
        assertEquals(1,sender.getSocketCloseCounter());
        sender.keepAliveConnectTime-- ;
        assertTrue(sender.checkKeepAlive());
        assertEquals(2,sender.getSocketCloseCounter());
    }
    
    
    /**
     * Push a mesage over moch socket to receiver
     * @throws Exception
     */
    public void testPushMessage() throws Exception {
        InetAddress host = InetAddress.getByName("127.0.0.1");
        DataSender sender = new MockDataSender("catalina",host, 3434);
        assertFalse(sender.isConnected());
        assertPushMessage(sender);
        ((MockSocket)sender.getSocket()).reset();
        // let see the processingtime 
        sender.setDoProcessingStats(true);
        pushMessage(sender);
        assertEquals(sender.getProcessingTime(),sender.getMinProcessingTime());
        assertEquals(sender.getProcessingTime(),sender.getMaxProcessingTime());
    }
   
    /**
     * Test retry after socket write failure
     * @throws Exception
     */
    public void testPushMessageRetryFailure() throws Exception {
        InetAddress host = InetAddress.getByName("127.0.0.1");
        DataSender sender = new MockDataSender("catalina",host, 3434);
        sender.openSocket() ;
        ((MockSocket)sender.getSocket()).setWriteIOException(true);
        assertPushMessage(sender);
        assertEquals(2,sender.getSocketOpenCounter());
        assertEquals(1,sender.getSocketCloseCounter());
    }
    
    /**
     * @param sender
     * @throws IOException
     */
    private void assertPushMessage(DataSender sender) throws IOException {
        ByteArrayOutputStream stream = pushMessage(sender);
        assertEquals(21,stream.size());
        assertEquals(1,sender.getKeepAliveCount());
        assertEquals(1,sender.getNrOfRequests());
        assertEquals(0,sender.getProcessingTime());
        assertEquals(Long.MAX_VALUE,sender.getMinProcessingTime());
    }

    /**
     * @param sender
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream pushMessage(DataSender sender) throws IOException {
        ClusterData data = new ClusterData("unique-id", "123",new byte[]{ 1,2,3 }, System.currentTimeMillis() );
        sender.pushMessage(data );
        assertTrue(sender.isConnected());
        ByteArrayOutputStream stream = (ByteArrayOutputStream)sender.getSocket().getOutputStream();
        return stream;
    }

    /**
     * Simulate Create socket failure 
     */
    class MockFailedDataSender extends DataSender {

        /**
         * @param host
         * @param port
         */
        public MockFailedDataSender(String domain,InetAddress host, int port) {
            super(domain,host, port);
        }

        /*
         * throw IOException
         * 
         * @see org.apache.catalina.cluster.tcp.DataSender#createSocket()
         */
        protected void createSocket() throws IOException, SocketException {
            throw new IOException();
        }
    }

    /**
     * Simulate open real socket to a server!!
     */
    class MockDataSender extends DataSender {

        
        /**
         * @param host
         * @param port
         */
        public MockDataSender(String domain,InetAddress host, int port) {
            super(domain,host, port);
            
        }

        protected void createSocket() throws IOException, SocketException {
            setSocket(new MockSocket(getAddress(), getPort()));
        }
        
    }
    
    /**
     * Don't open Socket really
     */
    class MockSocket extends Socket {

        private InputStream ackInputStream ;
        private OutputStream messageStream ;
        private boolean writeIOException = false ;
        private boolean readIOException = false ;
        private boolean readSocketTimeoutException = false ;
               
        /**
         * @param address
         * @param port
         * @throws java.io.IOException
         */
        public MockSocket(InetAddress address, int port) throws IOException {
            ackInputStream = new ByteArrayInputStream(TcpReplicationThread.ACK_COMMAND);
            messageStream = new ByteArrayOutputStream() ;
        }
        
        public void reset() throws IOException {
           ackInputStream.reset() ;
        }
        
        
        /**
         * @return Returns the readIOException.
         */
        public boolean isReadIOException() {
            return readIOException;
        }
        
        /**
         * @param readIOException The readIOException to set.
         */
        public void setReadIOException(boolean readIOException) {
            this.readIOException = readIOException;
        }
        
        /**
         * @return Returns the readSocketTimeoutException.
         */
        public boolean isReadSocketTimeoutException() {
            return readSocketTimeoutException;
        }
        
        /**
         * @param readSocketTimeoutException The readSocketTimeoutException to set.
         */
        public void setReadSocketTimeoutException(
                boolean readSocketTimeoutException) {
            this.readSocketTimeoutException = readSocketTimeoutException;
        }
        /**
         * @return Returns the writeIOException.
         */
        public boolean isWriteIOException() {
            return writeIOException;
        }
        
        /**
         * @param writeIOException The writeIOException to set.
         */
        public void setWriteIOException(boolean writeIOException) {
            this.writeIOException = writeIOException;
        }
        
        /**
         *  get ack Stream ( 3 bytes)
         * @see TcpReplicationThread#ACK_COMMAND
         * @see java.net.Socket#getInputStream()
         */
        public InputStream getInputStream() throws IOException {
            if(isReadIOException()) {
                throw new IOException("MockSocket");
            }
            if(isReadSocketTimeoutException()) {
                throw new SocketTimeoutException("MockSocket");
            }
            return ackInputStream;
        }
        
        
        /**
         * Buffer Output in simple byte array stream
         * @see java.net.Socket#getOutputStream()
         */
        public OutputStream getOutputStream() throws IOException {
            if(isWriteIOException()) {
                throw new IOException("MockSocket");
            }
            return messageStream;
        }
    }
}
