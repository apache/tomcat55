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
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.catalina.util.StringManager;

/**
 * @author Peter Rossbach
 * @version $Revision$, $Date$
 */
public class SocketReplicationListener extends ClusterReceiverBase {

    // ---------------------------------------------------- Statics

    private static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(SocketReplicationListener.class);

    /**
     * The string manager for this package.
     */
    protected static StringManager sm = StringManager
            .getManager(Constants.Package);
   
    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "SocketReplicationListener/1.2";

    //  ---------------------------------------------------- Properties
    private ServerSocket serverSocket = null;

    private int tcpListenMaxPort ;
    
    /**
     * 
     * One second timeout to wait that socket started
     */
    private int tcpListenTimeout = 1 ;
    
    //  ---------------------------------------------------- Constructor

    public SocketReplicationListener() {
    }

    //  ---------------------------------------------------- Properties

    /**
     * Return descriptive information about this implementation and the
     * corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {
        return (info);
    }
    
    /**
     * @return Returns the tcpListenMaxPort.
     */
    public int getTcpListenMaxPort() {
        return tcpListenMaxPort;
    }
    
    /**
     * @param tcpListenMaxPort The tcpListenMaxPort to set.
     */
    public void setTcpListenMaxPort(int maxListenPort) {
        this.tcpListenMaxPort = maxListenPort;
    }
     
    /**
     * @return Returns the tcpListenTimeout.
     */
    public int getTcpListenTimeout() {
        return tcpListenTimeout;
    }
    /**
     * @param tcpListenTimeout The tcpListenTimeout to set.
     */
    public void setTcpListenTimeout(int tcpListenTimeout) {
        this.tcpListenTimeout = tcpListenTimeout;
    }

    //  ---------------------------------------------------- public methods

    /**
     * Wait the createServerSocket find the correct socket port when default config is used.
     * @see org.apache.catalina.cluster.ClusterReceiver#start()
     * @see #createServerSocket()
     */
    public void start() {
        super.start();
        long reqStart = System.currentTimeMillis();
        long reqNow = 0 ;
        boolean isTimeout = false ;
        do {
            try {
                Thread.sleep(50);
            } catch (Exception sleep) {
            }
            reqNow = System.currentTimeMillis();
            isTimeout = ((reqNow - reqStart) > (1000 * getTcpListenTimeout()));
        } while (!doListen && (!isTimeout));
        if (isTimeout || (!doListen)) {
            log.error(sm.getString("SocketReplictionListener.timeout",
                    getTcpListenAddress(),Integer.toString(getTcpListenPort()),
                    Long.toString(reqNow - reqStart), Boolean.toString(doListen)));
        }
    }
    
    //  ---------------------------------------------------- protected methods

    /**
     * Master/Slave Sender handling / bind Server Socket at addres and port
     * 
     * @throws Exception
     */
    protected void listen() {
        if (doListen) {
            log.warn(sm.getString("SocketReplictionListener.allreadyExists",
                    getTcpListenAddress(),Integer.toString(getTcpListenPort())));
            return;
        }

        // Get the associated ServerSocket to bind it with
        try {
            serverSocket = createServerSocket();
            if(serverSocket != null) {
                doListen = true;
                while (doListen) {
                    try {
                        Socket socket = serverSocket.accept();
                        if (doListen) {
                            SocketReplicationThread t = new SocketReplicationThread(
                                    this, socket);
                            t.setDaemon(true);
                            t.start();
                        }
                    } catch (IOException iex) {
                        log.warn(sm.getString("SocketReplictionListener.accept.failure",
                                getTcpListenAddress(),
                                Integer.toString(getTcpListenPort())), iex);
                    }
                }
                serverSocket.close();
            } else {
                log.fatal(sm.getString("SocketReplictionListener.serverSocket.notExists",
                        getTcpListenAddress(),
                        Integer.toString(getTcpListenPort()),
                        Integer.toString(getTcpListenMaxPort())));
            }                
        } catch (IOException iex) {
            log.warn(sm.getString("SocketReplictionListener.openclose.failure",
                    getTcpListenAddress(),
                    Integer.toString(getTcpListenPort())), iex);
        } finally {
            doListen = false;
            serverSocket = null;
        }
    }

    /**
     * create a Server Socket between tcpListenerPort and tcpListenMaxPort
     */
    protected ServerSocket createServerSocket() {
        int startPort = getTcpListenPort() ;
        int maxPort = getTcpListenMaxPort() ;
        InetAddress inet = getBind() ;
        ServerSocket sSocket = null ;
        if (maxPort < startPort)
            maxPort = startPort;
        for( int i=startPort; i<=maxPort; i++ ) {
            try {
                if( inet == null ) {
                    sSocket = new ServerSocket( i, 0 );
                } else {
                    sSocket=new ServerSocket( i, 0, inet );
                }
                setTcpListenPort(i);
                break;
            } catch( IOException ex ) {
                if(log.isDebugEnabled())
                    log.debug(sm.getString("SocketReplictionListener.portbusy",
                            inet.getHostAddress(),
                            Integer.toString(i), 
                            ex.toString()));
                continue;
            }
        }
        if(sSocket != null && log.isInfoEnabled())
            log.info(sm.getString("SocketReplictionListener.open",
                    inet.getHostAddress(),
                    Integer.toString(getTcpListenPort())));
        return sSocket ;
   }

    /**
     * Need to create a connection to unlock the ServerSocker#accept(). Very
     * fine trick from channelSocket :-)
     * 
     * @see org.apache.jk.common.ChannelSocket#unLockSocket()
     */
    protected void unLockSocket() {
        Socket s = null;
        InetAddress ladr = getBind();

        try {
            if (ladr == null || "0.0.0.0".equals(ladr.getHostAddress())) {
                ladr = InetAddress.getLocalHost();
            }
            s = new Socket(ladr, getTcpListenPort());
            // setting soLinger to a small value will help shutdown the
            // connection quicker
            s.setSoLinger(true, 0);

        } catch (IOException iex) {
            log.warn(sm.getString("SocketReplictionListener.unlockSocket.failure",
                    getTcpListenAddress(),
                    Integer.toString(getTcpListenPort())), iex);
        } finally {
            try {
                if (s != null)
                    s.close();
            } catch (IOException ignore) {
            }
        }
    }

    /**
     * Close serverSockets
     * FIXME the channelSocket to connect own socket to terminate accpet loop!
     */
    protected void stopListening() {
        unLockSocket();
        doListen = false;
    }
    
 }
