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

package org.apache.catalina.cluster.mcast;


import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import org.apache.catalina.cluster.MembershipListener;

/**
 * A <b>membership</b> implementation using simple multicast.
 * This is the representation of a multicast membership service.
 * This class is responsible for maintaining a list of active cluster nodes in the cluster.
 * If a node fails to send out a heartbeat, the node will be dismissed.
 * This is the low level implementation that handles the multicasting sockets.
 * Need to fix this, could use java.nio and only need one thread to send and receive, or
 * just use a timeout on the receive
 * @author Filip Hanik
 * @author Peter Rossbach
 * @version $Id$
 */
public class McastServiceImpl
{
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( McastService.class );
    /**
     * Internal flag used for the listen thread that listens to the multicasting socket.
     */
    protected boolean doRun = false;
    /**
     * Socket that we intend to listen to
     */
    protected MulticastSocket socket;
    /**
     * The local member that we intend to broad cast over and over again
     */
    protected McastMember member;
    /**
     * The multicast address
     */
    protected InetAddress address;
    /**
     * The multicast port
     */
    protected int port;
    /**
     * The time it takes for a member to expire.
     */
    protected long timeToExpiration;
    /**
     * How often to we send out a broadcast saying we are alive, must be smaller than timeToExpiration
     */
    protected long sendFrequency;
    /**
     * Reuse the sendPacket, no need to create a new one everytime
     */
    protected DatagramPacket sendPacket;
    /**
     * Reuse the receivePacket, no need to create a new one everytime
     */
    protected DatagramPacket receivePacket;
    /**
     * The membership, used so that we calculate memberships when they arrive or don't arrive
     */
    protected McastMembership membership;
    /**
     * The actual listener, for callback when shits goes down
     */
    protected MembershipListener service;
    /**
     * Thread to listen for pings
     */
    protected ReceiverThread receiver;
    /**
     * Thread to send pings
     */
    protected SenderThread sender;

    /**
     * When was the service started
     */
    protected long serviceStartTime = System.currentTimeMillis();
    
    protected int mcastTTL = -1;
    protected int mcastSoTimeout = -1;
    protected InetAddress mcastBindAddress = null;

    /**
     * nr of times the system has to fail before a recovery is initiated
     */
    protected int recoveryCounter = 10;
    
    /**
     * The time the recovery thread sleeps between recovery attempts
     */
    protected long recoverySleepTime = 5000;
    
    /**
     * Add the ability to turn on/off recovery
     */
    protected boolean recoveryEnabled = true;
    
    /**
     * Create a new mcast service impl
     * @param member - the local member
     * @param sendFrequency - the time (ms) in between pings sent out
     * @param expireTime - the time (ms) for a member to expire
     * @param port - the mcast port
     * @param bind - the bind address (not sure this is used yet)
     * @param mcastAddress - the mcast address
     * @param service - the callback service
     * @throws IOException
     */
    public McastServiceImpl(
        McastMember member,
        long sendFrequency,
        long expireTime,
        int port,
        InetAddress bind,
        InetAddress mcastAddress,
        int ttl,
        int soTimeout,
        MembershipListener service)
    throws IOException {
        this.member = member;
        address = mcastAddress;
        this.port = port;
        this.mcastSoTimeout = soTimeout;
        this.mcastTTL = ttl;
        this.mcastBindAddress = bind;
        timeToExpiration = expireTime;
        this.service = service;
        this.sendFrequency = sendFrequency;
        init();
    }

    protected void init() throws IOException {
        setupSocket();
        sendPacket = new DatagramPacket(new byte[1000],1000);
        sendPacket.setAddress(address);
        sendPacket.setPort(port);
        receivePacket = new DatagramPacket(new byte[1000],1000);
        receivePacket.setAddress(address);
        receivePacket.setPort(port);
        if(membership == null) membership = new McastMembership(member.getName());
    }
    
    protected void setupSocket() throws IOException {
        if (mcastBindAddress != null) {
            try {
                log.info("Attempting to bind the multicast socket to "+address+":"+port);
                socket = new MulticastSocket(new InetSocketAddress(address,port));
            } catch (BindException e) {
                /*
                 * On some plattforms (e.g. Linux) it is not possible to bind
                 * to the multicast address. In this case only bind to the
                 * port.
                 */
                log.info("Binding to multicast address, failed. Binding to port only.");
                socket = new MulticastSocket(port);
            }
        } else {
            socket = new MulticastSocket(port);
        }

        /**
         * The argument of setLoopbackMode() is named disable.
         * We set it to false, because we need loopback messages for the case
         * when multiple cluster nodes reside on the same machine.
         */
        socket.setLoopbackMode(false);

        if (mcastBindAddress != null) {
			if(log.isInfoEnabled())
                log.info("Setting multihome multicast interface to:" +
                         mcastBindAddress);
            socket.setInterface(mcastBindAddress);
        } //end if
        //force a so timeout so that we don't block forever
        if ( mcastSoTimeout <= 0 ) mcastSoTimeout = (int)sendFrequency;
        if(log.isInfoEnabled())
            log.info("Setting cluster mcast soTimeout to "+mcastSoTimeout);
        socket.setSoTimeout(mcastSoTimeout);
        if ( mcastTTL >= 0 ) {
			if(log.isInfoEnabled())
                log.info("Setting cluster mcast TTL to " + mcastTTL);
            socket.setTimeToLive(mcastTTL);
        }
    }

    /**
     * Start the service
     * @param level 1 starts the receiver, level 2 starts the sender
     * @throws IOException if the service fails to start
     * @throws IllegalStateException if the service is already started
     */
    public synchronized void start(int level) throws IOException {
        if ( sender != null && receiver != null ) throw new IllegalStateException("Service already running.");
        if ( level == 1 ) {
            socket.joinGroup(address);
            doRun = true;
            receiver = new ReceiverThread();
            receiver.setDaemon(true);
            receiver.start();
        }
        if ( level==2 ) {
            serviceStartTime = System.currentTimeMillis();
            sender = new SenderThread(sendFrequency);
            sender.setDaemon(true);
            sender.start();
            
        }
    }

    /**
     * Stops the service
     * @throws IOException if the service fails to disconnect from the sockets
     */
    public synchronized void stop() throws IOException {
        try {
            socket.leaveGroup(address);
        } catch (IOException ignore) {
        } finally {
            doRun = false;
            if(sender!= null) sender.interrupt() ;
            sender = null;
            if(receiver!= null) receiver.interrupt() ;
            receiver = null;
            serviceStartTime = Long.MAX_VALUE;
            socket.close();
        }
    }

    /**
     * Receive a datagram packet, locking wait
     * @throws IOException
     */
    public void receive() throws IOException {
        try {
            socket.receive(receivePacket);

            byte[] data = new byte[receivePacket.getLength()];
            System.arraycopy(receivePacket.getData(),receivePacket.getOffset(),data,0,data.length);
            McastMember m = McastMember.getMember(data);
            if(log.isDebugEnabled())
                log.debug("Mcast receive ping from member " + m);
            synchronized (membershipMutex) {
                if ( membership.memberAlive(m) ) {
                    if(log.isDebugEnabled())
                        log.debug("Mcast add member " + m);
                    service.memberAdded(m);
                }
            }
        } finally {
            checkExpire();
        }
    }

    protected final Object membershipMutex = new Object();

    /**
     * check member expire or alive
     */
    protected void checkExpire() {
        synchronized (membershipMutex) {
            McastMember[] expired = membership.expire(timeToExpiration);
            for ( int i=0; i<expired.length; i++) {
                if(log.isDebugEnabled())
                    log.debug("Mcast expire member " + expired[i]);
                service.memberDisappeared(expired[i]);
            }
        }
    }

    /**
     * Send a ping
     * @throws Exception
     */
    public void send() throws Exception{
        try {
            member.inc();

            if(log.isDebugEnabled())
                log.debug("Mcast send ping from member " + member);
            byte[] data = member.getData(this.serviceStartTime);
            DatagramPacket p = new DatagramPacket(data,data.length);
            p.setAddress(address);
            p.setPort(port);
            socket.send(p);
        } finally {
            checkExpire() ;
        }
    }

    public long getServiceStartTime() {
       return this.serviceStartTime;
    }

    public int getRecoveryCounter() {
        return recoveryCounter;
    }

    public boolean isRecoveryEnabled() {
        return recoveryEnabled;
    }

    public long getRecoverySleepTime() {
        return recoverySleepTime;
    }

    public void setRecoveryCounter(int recoveryCounter) {
        this.recoveryCounter = recoveryCounter;
    }

    public void setRecoveryEnabled(boolean recoveryEnabled) {
        this.recoveryEnabled = recoveryEnabled;
    }

    public void setRecoverySleepTime(long recoverySleepTime) {
        this.recoverySleepTime = recoverySleepTime;
    }
    
    public class ReceiverThread extends Thread {
        
        public ReceiverThread() {
            super();
            setName("Cluster-MembershipReceiver");
        }
        
        public void run() {
            int errorCounter = 0 ;
            while ( doRun ) {
                try {
                    receive();
                    errorCounter = 0;
                } catch ( Exception x ) {
                    if (errorCounter==0) { 
                        if(! (x instanceof SocketTimeoutException))
                            log.warn("Error receiving mcast package (errorCounter=" +errorCounter+ "). Sleeping " +sendFrequency + " ms",x);
                    } else {
                        if(! (x instanceof SocketTimeoutException)
                            && log.isDebugEnabled())
                            log.debug("Error receiving mcast package (errorCounter=" +errorCounter+ "). Sleeping " +sendFrequency+ " ms",x);
                    }
                    try { Thread.sleep(sendFrequency); } catch ( Exception ignore ){}
                    if ( (++errorCounter)>=recoveryCounter ) {
                        log.warn("Error receiving mcast package (errorCounter=" +errorCounter+ "). Try Recovery!",x);
                        errorCounter=0;
                        new RecoveryThread(McastServiceImpl.this);
                    }
                }
            }
            log.warn("Receiver Thread ends with errorCounter=" +errorCounter+ ".");
            
        }
    }

    public class SenderThread extends Thread {
        
        long time;
        
        McastServiceImpl service ;
        
        public SenderThread(long time) {
            this.time = time;
            setName("Cluster-MembershipSender");

        }
        
        public void run() {
            int errorCounter = 0 ;
            while ( doRun ) {
                try {
                    send();
                    errorCounter = 0;
                } catch ( Exception x ) {
                    if (errorCounter==0) {
                        log.warn("Unable to send mcast message.",x);
                    }
                    else {
                        if(log.isDebugEnabled())
                            log.debug("Unable to send mcast message.",x);
                    }
                    if ( (++errorCounter)>=recoveryCounter ) {
                        errorCounter=0;
                        new RecoveryThread(McastServiceImpl.this);
                     }
                }
                try { Thread.sleep(time); } catch ( Exception ignore ) {}
            }
            log.warn("Sender Thread ends with errorCounter=" +errorCounter+ ".");
        }       
    }
    
    protected static class RecoveryThread extends Thread {
        
        static boolean running = false;
        
        McastServiceImpl parent = null;
       
        public RecoveryThread(McastServiceImpl parent) {
            this.parent = parent;
            if (!init(this)) this.parent = null;
        }
        
        public static synchronized boolean init(RecoveryThread t) {
            if ( running ) {
                return false;
            }
            if ( !t.parent.isRecoveryEnabled()) {
                return false;
            }
            running = true;
            t.setName("Cluster-MembershipRecovery");
            t.setDaemon(true);
            t.start();
            return true;
        }

        public boolean stopService() {
            try {
                parent.stop();
                return true;
            } catch (Exception x) {
                log.warn("Recovery thread failed to stop membership service.", x);
                return false;
            }
        }
        
        public boolean startService() {
            try {
                parent.init();
                parent.start(1);
                parent.start(2);
                return true;
            } catch (Exception x) {
                log.warn("Recovery thread failed to start membership service.", x);
                return false;
            }
        }
        
        public void run() {
            boolean success = false;
            int attempt = 0;
            try {
                while (!success) {
                    if(log.isInfoEnabled())
                        log.info("Cluster membership, running recovery thread, multicasting is not functional.");
                    success = stopService();
                    if(success) {
                        try {
                            Thread.sleep(1000 + parent.mcastSoTimeout);
                        } catch (Exception ignore){}
                        success = startService();
                        if(success && log.isInfoEnabled())
                            log.info("Membership recovery was successful.");
                    }
                    try {
                        if (!success) {
                            if(log.isInfoEnabled())
                                log.info("Recovery attempt " + (++attempt) + " failed, trying again in " +parent.recoverySleepTime + " milliseconds");
                            Thread.sleep(parent.recoverySleepTime);
                            // check member expire...
                            parent.checkExpire() ;
                       }
                    }catch (InterruptedException ignore) {
                    }
                }
            } finally {
                running = false;
            }
        }
    }
}
