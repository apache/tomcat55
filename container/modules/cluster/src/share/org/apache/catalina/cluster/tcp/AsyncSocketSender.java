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
import org.apache.catalina.cluster.util.SmartQueue;
public class AsyncSocketSender implements IDataSender {
    private static int threadCounter=1;
    
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( AsyncSocketSender.class );

    
    private InetAddress address;
    private int port;
    private Socket sc = null;
    private boolean isSocketConnected = false;
    private SmartQueue queue = new SmartQueue();
    private boolean suspect;
    
    private QueueThread queueThread = null;
    
    public AsyncSocketSender(InetAddress host, int port)  {
        this.address = host;
        this.port = port;
        checkThread();
        log.info("Started async sender thread for TCP replication.");
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void connect() throws java.io.IOException  {
        sc = new Socket(getAddress(),getPort());
        isSocketConnected = true;
        checkThread();
        
    }
    
    protected void checkThread() {
        if ( queueThread == null ) {
            queueThread = new QueueThread(this);
            queueThread.setDaemon(true);
            queueThread.start();
        }
    }

    public void disconnect()  {
        try
        {
            sc.close();
        }catch ( Exception x)
        {}
        isSocketConnected = false;
        if ( queueThread != null ) {
            queueThread.stopRunning();
            queueThread = null;
        }
        
    }

    public boolean isConnected() {
        return isSocketConnected;
    }

    /**
     * Blocking send
     * @param data
     * @throws java.io.IOException
     */
    private synchronized void sendMessage(byte[] data) throws java.io.IOException  {
        if ( !isConnected() ) connect();
        try
        {
            sc.getOutputStream().write(data);
        }
        catch ( java.io.IOException x )
        {
            disconnect();
            connect();
            sc.getOutputStream().write(data);
        }
    }

    public synchronized void sendMessage(String sessionId, byte[] data) throws java.io.IOException {
        SmartQueue.SmartEntry entry = new SmartQueue.SmartEntry(sessionId,data);
        queue.add(entry);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("SocketSender[");
        buf.append(getAddress()).append(":").append(getPort()).append("]");
        return buf.toString();
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
    
    private class QueueThread extends Thread {
        AsyncSocketSender sender;
        private boolean keepRunning = true;

        public QueueThread(AsyncSocketSender sender) {
            this.sender = sender;
            setName("Cluster-AsyncSocketSender-"+(threadCounter++));
        }
        
        public void stopRunning() {
            keepRunning = false;
        }
        
        public void run() {
            while (keepRunning) {
                SmartQueue.SmartEntry entry = sender.queue.remove(5000);
                if ( entry != null ) {
                    try {
                        byte[] data = (byte[]) entry.getValue();
                        sender.sendMessage(data);
                    }
                    catch (Exception x) {
                        log.warn(
                            "Unable to asynchronously send session w/ id=" +
                            entry.getKey()+" message will be ignored.");
                    }
                }
            }
        }
    }
}
