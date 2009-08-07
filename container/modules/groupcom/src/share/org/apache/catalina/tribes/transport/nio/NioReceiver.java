/*
 * Copyright 1999,2004-2005 The Apache Software Foundation.
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

package org.apache.catalina.tribes.transport.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.catalina.tribes.ChannelReceiver;
import org.apache.catalina.tribes.io.ListenCallback;
import org.apache.catalina.tribes.io.ObjectReader;
import org.apache.catalina.tribes.transport.Constants;
import org.apache.catalina.tribes.transport.ReceiverBase;
import org.apache.catalina.tribes.transport.ThreadPool;
import org.apache.catalina.tribes.transport.WorkerThread;
import org.apache.catalina.tribes.util.StringManager;

/**
 * @author Filip Hanik
 * @version $Revision: 379904 $ $Date: 2006-02-22 15:16:25 -0600 (Wed, 22 Feb 2006) $
 */
public class NioReceiver extends ReceiverBase implements Runnable, ChannelReceiver, ListenCallback {

    protected static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(NioReceiver.class);

    /**
     * The string manager for this package.
     */
    protected StringManager sm = StringManager.getManager(Constants.Package);

    /**
     * The descriptive information about this implementation.
     */
    private static final String info = "NioReceiver/1.0";

    private Selector selector = null;
    private ServerSocketChannel serverChannel = null;


    private Object interestOpsMutex = new Object();

    public NioReceiver() {
    }

    /**
     * Return descriptive information about this implementation and the
     * corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    public String getInfo() {
        return (info);
    }

    public Object getInterestOpsMutex() {
        return interestOpsMutex;
    }

    public void stop() {
        this.stopListening();
    }

    /**
     * start cluster receiver
     * @throws Exception
     * @see org.apache.catalina.tribes.ClusterReceiver#start()
     */
    public void start() {
        try {
            setPool(new ThreadPool(interestOpsMutex, getMaxThreads(),getMinThreads(),this));
        } catch (Exception e) {
            log.error("ThreadPool can initilzed. Listener not started", e);
            return;
        }
        try {
            getBind();
            bind();
            Thread t = new Thread(this, "NioReceiver");
            t.setDaemon(true);
            t.start();
        } catch (Exception x) {
            log.fatal("Unable to start cluster receiver", x);
        }
    }
    
    public WorkerThread getWorkerThread() {
        NioReplicationThread thread = new NioReplicationThread(this);
        thread.setRxBufSize(getRxBufSize());
        thread.setOptions(getWorkerThreadOptions());
        return thread;
    }
    
    
    
    protected void bind() throws IOException {
        // allocate an unbound server socket channel
        serverChannel = ServerSocketChannel.open();
        // Get the associated ServerSocket to bind it with
        ServerSocket serverSocket = serverChannel.socket();
        // create a new Selector for use below
        selector = Selector.open();
        // set the port the server channel will listen to
        //serverSocket.bind(new InetSocketAddress(getBind(), getTcpListenPort()));
        bind(serverSocket,getTcpListenPort(),getAutoBind());
        // set non-blocking mode for the listening socket
        serverChannel.configureBlocking(false);
        // register the ServerSocketChannel with the Selector
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
    }
    /**
     * get data from channel and store in byte array
     * send it to cluster
     * @throws IOException
     * @throws java.nio.channels.ClosedChannelException
     */
    protected void listen() throws Exception {
        if (doListen()) {
            log.warn("ServerSocketChannel already started");
            return;
        }
        
        setListen(true);

        while (doListen() && selector != null) {
            // this may block for a long time, upon return the
            // selected set contains keys of the ready channels
            try {

                int n = selector.select(getTcpSelectorTimeout());
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

                        channel.socket().setReceiveBufferSize(getRxBufSize());
                        channel.socket().setSendBufferSize(getTxBufSize());
                        channel.socket().setTcpNoDelay(getTcpNoDelay());
                        channel.socket().setKeepAlive(getSoKeepAlive());
                        channel.socket().setOOBInline(getOoBInline());
                        channel.socket().setReuseAddress(getSoReuseAddress());
                        channel.socket().setSoLinger(getSoLingerOn(),getSoLingerTime());
                        channel.socket().setTrafficClass(getSoTrafficClass());
                        channel.socket().setSoTimeout(getTimeout());
                        Object attach = new ObjectReader(channel);
                        registerChannel(selector,
                                        channel,
                                        SelectionKey.OP_READ,
                                        attach);
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
            } catch (java.nio.channels.ClosedSelectorException cse) {
                // ignore is normal at shutdown or stop listen socket
            } catch (java.nio.channels.CancelledKeyException nx) {
                log.warn(
                    "Replication client disconnected, error when polling key. Ignoring client.");
            } catch (Exception x) {
                log.error("Unable to process request in NioReceiver", x);
            }

        }
        serverChannel.close();
        if (selector != null)
            selector.close();
    }

    /**
     * Close Selector.
     *
     * @see org.apache.catalina.tribes.transport.ClusterReceiverBase#stopListening()
     */
    protected void stopListening() {
        // Bugzilla 37529: http://issues.apache.org/bugzilla/show_bug.cgi?id=37529
        setListen(false);
        if (selector != null) {
            try {
                for (int i = 0; i < getMaxThreads(); i++) {
                    selector.wakeup();
                }
                selector.close();
            } catch (Exception x) {
                log.error("Unable to close cluster receiver selector.", x);
            } finally {
                selector = null;
            }
        }
    }

    // ----------------------------------------------------------

    /**
     * Register the given channel with the given selector for
     * the given operations of interest
     */
    protected void registerChannel(Selector selector,
                                   SelectableChannel channel,
                                   int ops,
                                   Object attach) throws Exception {
        if (channel == null)return; // could happen
        // set the new channel non-blocking
        channel.configureBlocking(false);
        // register it with the selector
        channel.register(selector, ops, attach);
    }

    /**
     * Start thread and listen
     */
    public void run() {
        try {
            listen();
        } catch (Exception x) {
            log.error("Unable to run replication listener.", x);
        }
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
    protected void readDataFromSocket(SelectionKey key) throws Exception {
        NioReplicationThread worker = (NioReplicationThread) getPool().getWorker();
        if (worker == null) {
            // No threads available, do nothing, the selection
            // loop will keep calling this method until a
            // thread becomes available.
            // FIXME: This design could be improved.
            if (log.isDebugEnabled())
                log.debug("No TcpReplicationThread available");
        } else {
            // invoking this wakes up the worker thread then returns
            worker.serviceChannel(key);
        }
    }


}
