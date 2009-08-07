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

package org.apache.catalina.cluster.mcast;


import java.net.MulticastSocket;
import java.io.IOException;
import java.net.InetAddress ;
import java.net.DatagramPacket;
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
 * @version $Revision$, $Date$
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
        setupSocket();
        sendPacket = new DatagramPacket(new byte[1000],1000);
        sendPacket.setAddress(address);
        sendPacket.setPort(port);
        receivePacket = new DatagramPacket(new byte[1000],1000);
        receivePacket.setAddress(address);
        receivePacket.setPort(port);
        membership = new McastMembership(member.getName());
        timeToExpiration = expireTime;
        this.service = service;
        this.sendFrequency = sendFrequency;
    }
    
    protected void setupSocket() throws IOException {
        if (mcastBindAddress != null) socket = new MulticastSocket(new java.net.
            InetSocketAddress(mcastBindAddress, port));
        else socket = new MulticastSocket(port);
        if (mcastBindAddress != null) {
			if(log.isInfoEnabled())
                log.info("Setting multihome multicast interface to:" +
                         mcastBindAddress);
            socket.setInterface(mcastBindAddress);
        } //end if
        if ( mcastSoTimeout >= 0 ) {
 			if(log.isInfoEnabled())
                log.info("Setting cluster mcast soTimeout to "+mcastSoTimeout);
            socket.setSoTimeout(mcastSoTimeout);
        }
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
        socket.leaveGroup(address);
        doRun = false;
        sender = null;
        receiver = null;
        serviceStartTime = Long.MAX_VALUE;
    }

    /**
     * Receive a datagram packet, locking wait
     * @throws IOException
     */
    public void receive() throws IOException {
        socket.receive(receivePacket);
        byte[] data = new byte[receivePacket.getLength()];
        System.arraycopy(receivePacket.getData(),receivePacket.getOffset(),data,0,data.length);
        McastMember m = McastMember.getMember(data);
        if(log.isDebugEnabled())
            log.debug("Mcast receive ping from member " + m);
        if ( membership.memberAlive(m) ) {
            if(log.isDebugEnabled())
                log.debug("Mcast add member " + m);
            service.memberAdded(m);
        }
        McastMember[] expired = membership.expire(timeToExpiration);
        for ( int i=0; i<expired.length; i++) {
            if(log.isDebugEnabled())
                log.debug("Mcast exipre  member " + m);
            service.memberDisappeared(expired[i]);
        }
    }

    /**
     * Send a ping
     * @throws Exception
     */
    public void send() throws Exception{
        member.inc();
        if(log.isDebugEnabled())
            log.debug("Mcast send ping from member " + member);
        byte[] data = member.getData(this.serviceStartTime);
        DatagramPacket p = new DatagramPacket(data,data.length);
        p.setAddress(address);
        p.setPort(port);
        socket.send(p);
    }

    public long getServiceStartTime() {
       return this.serviceStartTime;
    }


    public class ReceiverThread extends Thread {
        public ReceiverThread() {
            super();
            setName("Cluster-MembershipReceiver");
        }
        public void run() {
            while ( doRun ) {
                try {
                    receive();
                } catch ( Exception x ) {
                    log.warn("Error receiving mcast package. Sleeping 500ms",x);
                    try { Thread.sleep(500); } catch ( Exception ignore ){}
                    
                }
            }
        }
    }//class ReceiverThread

    public class SenderThread extends Thread {
        long time;
        public SenderThread(long time) {
            this.time = time;
            setName("Cluster-MembershipSender");

        }
        public void run() {
            while ( doRun ) {
                try {
                    send();
                } catch ( Exception x ) {
                    log.warn("Unable to send mcast message.",x);
                }
                try { Thread.sleep(time); } catch ( Exception ignore ) {}
            }
        }
    }//class SenderThread
}
