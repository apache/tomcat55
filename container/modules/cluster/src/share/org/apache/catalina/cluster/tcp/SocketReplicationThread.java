/*
 * Copyright 1999,2005 The Apache Software Foundation.
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
import java.io.InputStream;
import java.net.Socket;

import org.apache.catalina.cluster.io.ListenCallback;
import org.apache.catalina.cluster.io.SocketObjectReader;

/**
 * @author Peter Rossbach
 * FIXME ThreadPooling
 * FIXME Socket timeout
 * @version $Revision$, $Date$
 */
public class SocketReplicationThread extends Thread implements ListenCallback {
    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(SocketReplicationThread.class);

    private static byte[] ACK_COMMAND = new byte[] { 6, 2, 3 };

    private static int count = 0;

    private SocketReplicationListener master;

    private Socket socket;

    private SocketObjectReader reader;

    private boolean keepRunning = true;

    /**
     * Fork Listen Worker Thread!
     * 
     * @param socket
     * @param reader
     * @param sendAck
     */
    SocketReplicationThread(SocketReplicationListener master, Socket socket
           ) {
        super("ClusterListenThread-" + count++);
        this.master = master;
        this.socket = socket;
        this.reader =  new SocketObjectReader(socket,this);
    }

    /**
     * read sender messages / is message complete send ack and wait for next
     * message!
     * 
     * @see SocketObjectReader#append(byte[],int,int)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            InputStream in = socket.getInputStream();
            while (keepRunning) {
                int cnt = in.read(buffer);
                if (log.isTraceEnabled()) {
                    log.trace("read " + cnt + " bytes from " + socket.getPort());
                }
                int ack = 0;
                if (cnt > 0) {
                    ack = reader.append(buffer, 0, cnt);
                    if (log.isTraceEnabled()) {
                        log.trace("sending " + ack + " ack packages to " + socket.getLocalPort() );
                    }
                    /**
                    if (sendAck) {
                        // ack only when message is complete receive
                        while (ack > 0) {
                            sendAck();
                            ack--;
                        }
                    }
                    **/
                    keepRunning = master.isDoListen();
                } else
                    // EOF
                    keepRunning = false;
            }
        } catch (IOException x) {
            log.error("Unable to read data from client, disconnecting.", x);
        } finally {
            // finish socket
            if (socket != null) {
                try {

                    socket.close();
                } catch (Exception ignore) {
                }
            }
            keepRunning = false;
            socket = null;
        }
    }
    
    public void messageDataReceived(ClusterData data) {
        master.messageDataReceived(data);
    }   
    
    public boolean isSendAck() {
        return master.isSendAck();
    }
    
    /**
     * send a reply-acknowledgement
     * 
     * @throws java.io.IOException
     */
    public void sendAck() throws java.io.IOException {
        socket.getOutputStream().write(ACK_COMMAND);
        if (log.isTraceEnabled()) {
            log.trace("ACK sent to " + socket.getPort());
        }
    }

}
