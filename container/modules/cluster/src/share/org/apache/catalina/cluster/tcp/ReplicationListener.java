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


import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SelectableChannel;

import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.util.Iterator;
import org.apache.catalina.cluster.io.ListenCallback;
import org.apache.catalina.cluster.io.ObjectReader;
import org.apache.catalina.cluster.CatalinaCluster;
import org.apache.catalina.cluster.ClusterReceiver;
/**
 */
public class ReplicationListener implements Runnable,ClusterReceiver
{

    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( ReplicationListener.class );
    private ThreadPool pool = null;
    private boolean doListen = false;
    private ListenCallback callback;
    private java.net.InetAddress bind;
    private String tcpListenAddress;
    private int tcpThreadCount;
    private long tcpSelectorTimeout;
    private int tcpListenPort;
    private boolean waitForAck;
    private Selector selector = null;
    
    private Object interestOpsMutex = new Object();
    
    public ReplicationListener() {
    }

    public void start() {
        try {
            pool = new ThreadPool(tcpThreadCount, TcpReplicationThread.class, interestOpsMutex);
            if ( "auto".equals(tcpListenAddress) ) {
                tcpListenAddress = java.net.InetAddress.getLocalHost().
                    getHostAddress();
            }
            if(log.isDebugEnabled())
                log.debug("Starting replication listener on address:"+tcpListenAddress);
            bind = java.net.InetAddress.getByName(tcpListenAddress);
            Thread t = new Thread(this,"ClusterReceiver");
            t.setDaemon(true);
            t.start();
        } catch ( Exception x ) {
            log.fatal("Unable to start cluster receiver",x);
        }

    }
    
    public void stop() {
        stopListening();
    }
    

    public void run()
    {
        try
        {
            listen();
        }
        catch ( Exception x )
        {
            log.error("Unable to start cluster listener.",x);
        }
    }

    public void listen ()
        throws Exception
    {
        doListen = true;
        // allocate an unbound server socket channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // Get the associated ServerSocket to bind it with
        ServerSocket serverSocket = serverChannel.socket();
        // create a new Selector for use below
        selector = Selector.open();
        // set the port the server channel will listen to
        serverSocket.bind (new InetSocketAddress (bind,tcpListenPort));
        // set non-blocking mode for the listening socket
        serverChannel.configureBlocking (false);
        // register the ServerSocketChannel with the Selector
        serverChannel.register (selector, SelectionKey.OP_ACCEPT);
        while (doListen) {
            // this may block for a long time, upon return the
            // selected set contains keys of the ready channels
            try {

                int n = selector.select(tcpSelectorTimeout);
                if (n == 0) {
                    //there is a good chance that we got here 
                    //because the TcpReplicationThread called
                    //selector wakeup().
                    //if that happens, we must ensure that that
                    //thread has enough time to call interestOps
                    synchronized (interestOpsMutex) {
                        //if we got the lock, means there are no
                        //keys trying to register for the 
                        //interestOps method
                    }
                    continue; // nothing to do
                }
                // get an iterator over the set of selected keys
                Iterator it = selector.selectedKeys().iterator();
                // look at each key in the selected set
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    // Is a new connection coming in?
                    if (key.isAcceptable()) {
                        ServerSocketChannel server =
                            (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        registerChannel(selector,
                                        channel,
                                        SelectionKey.OP_READ,
                                        new ObjectReader(channel, selector,
                            callback));
                    }
                    // is there data to read on this channel?
                    if (key.isReadable()) {
                        readDataFromSocket(key);
                    } else {
                        key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
                    }

                    // remove key from selected set, it's been handled
                    it.remove();
                }
            }
            catch (java.nio.channels.CancelledKeyException nx) {
                log.warn(
                    "Replication client disconnected, error when polling key. Ignoring client.");
            }
            catch (Exception x) {
                log.error("Unable to process request in ReplicationListener", x);
            }

        } //while
        serverChannel.close();
        selector.close();
    }

    public void stopListening(){
        doListen = false;
        if ( selector != null ) {
            try {
                selector.close();
                selector = null;
            } catch ( Exception x ) {
                log.error("Unable to close cluster receiver selector.",x);
            }
        }
    }
    
    public void setCatalinaCluster(CatalinaCluster cluster) {
        callback = cluster;
    }


    // ----------------------------------------------------------

    /**
     * Register the given channel with the given selector for
     * the given operations of interest
     */
    protected void registerChannel (Selector selector,
                                    SelectableChannel channel,
                                    int ops,
                                    Object attach)
    throws Exception {
        if (channel == null) return; // could happen
        // set the new channel non-blocking
        channel.configureBlocking (false);
        // register it with the selector
        channel.register (selector, ops, attach);
    }

    // ----------------------------------------------------------

    /**
     * Sample data handler method for a channel with data ready to read.
     * @param key A SelectionKey object associated with a channel
     *  determined by the selector to be ready for reading.  If the
     *  channel returns an EOF condition, it is closed here, which
     *  automatically invalidates the associated key.  The selector
     *  will then de-register the channel on the next select call.
     */
    protected void readDataFromSocket (SelectionKey key)
        throws Exception
    {
        TcpReplicationThread worker = (TcpReplicationThread)pool.getWorker();
        if (worker == null) {
            // No threads available, do nothing, the selection
            // loop will keep calling this method until a
            // thread becomes available.  This design could
            // be improved.
            return;
        } else {
            // invoking this wakes up the worker thread then returns
            worker.serviceChannel(key, waitForAck);
            return;
        }
    }
    public String getTcpListenAddress() {
        return tcpListenAddress;
    }
    public void setTcpListenAddress(String tcpListenAddress) {
        this.tcpListenAddress = tcpListenAddress;
    }
    public int getTcpListenPort() {
        return tcpListenPort;
    }
    public void setTcpListenPort(int tcpListenPort) {
        this.tcpListenPort = tcpListenPort;
    }
    public long getTcpSelectorTimeout() {
        return tcpSelectorTimeout;
    }
    public void setTcpSelectorTimeout(long tcpSelectorTimeout) {
        this.tcpSelectorTimeout = tcpSelectorTimeout;
    }
    public int getTcpThreadCount() {
        return tcpThreadCount;
    }
    public void setTcpThreadCount(int tcpThreadCount) {
        this.tcpThreadCount = tcpThreadCount;
    }
    public boolean isWaitForAck() {
        return waitForAck;
    }
    public void setWaitForAck(boolean waitForAck) {
        this.waitForAck = waitForAck;
    }
    
    public String getHost() {
        return getTcpListenAddress();
    }

    public int getPort() {
        return getTcpListenPort();
    }
    public Object getInterestOpsMutex() {
        return interestOpsMutex;
    }

}
