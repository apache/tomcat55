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

import org.apache.catalina.cluster.MembershipService;
import java.util.Properties;
import org.apache.catalina.cluster.Member;
import org.apache.catalina.cluster.MembershipListener;

/**
 * A <b>membership</b> implementation using simple multicast.
 * This is the representation of a multicast membership service.
 * This class is responsible for maintaining a list of active cluster nodes in the cluster.
 * If a node fails to send out a heartbeat, the node will be dismissed.
 *
 * @author Filip Hanik
 * @version $Revision$, $Date$
 */


public class McastService implements MembershipService,MembershipListener {

    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( McastService.class );
    /**
     * The implementation specific properties
     */
    protected Properties properties = new Properties();
    /**
     * A handle to the actual low level implementation
     */
    protected McastServiceImpl impl;
    /**
     * A membership listener delegate (should be the cluster :)
     */
    protected MembershipListener listener;
    /**
     * The local member
     */
    protected McastMember localMember ;
    private int mcastSoTimeout;
    private int mcastTTL;

    /**
     * Create a membership service.
     */
    public McastService() {
    }


    /**
     *
     * @param properties<BR>All are required<BR>
     * 1. mcastPort - the port to listen to<BR>
     * 2. mcastAddress - the mcast group address<BR>
     * 3. bindAddress - the bind address if any - only one that can be null<BR>
     * 4. memberDropTime - the time a member is gone before it is considered gone.<BR>
     * 5. msgFrequency - the frequency of sending messages<BR>
     * 6. tcpListenPort - the port this member listens to<BR>
     * 7. tcpListenHost - the bind address of this member<BR>
     * @exception java.lang.IllegalArgumentException if a property is missing.
     */
    public void setProperties(Properties properties) {
        hasProperty(properties,"mcastPort");
        hasProperty(properties,"mcastAddress");
        hasProperty(properties,"memberDropTime");
        hasProperty(properties,"msgFrequency");
        hasProperty(properties,"tcpListenPort");
        hasProperty(properties,"tcpListenHost");
        this.properties = properties;
    }

    /**
     * Return the properties, see setProperties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Return the local member
     */
    public Member getLocalMember() {
        localMember.setMemberAliveTime(System.currentTimeMillis()-impl.getServiceStartTime());
        return localMember;
    }
    
    /**
     * Sets the local member properties for broadcasting
     */
    public void setLocalMemberProperties(String listenHost, int listenPort) {
        properties.setProperty("tcpListenHost",listenHost);
        properties.setProperty("tcpListenPort",String.valueOf(listenPort));
    }
    
    public void setMcastAddr(String addr) {
        properties.setProperty("mcastAddress", addr);
    }

    public void setMcastBindAddress(String bindaddr) {
        properties.setProperty("mcastBindAddress", bindaddr);
    }

    public void setMcastPort(int port) {
        properties.setProperty("mcastPort", String.valueOf(port));
    }

    public void setMcastFrequency(long time) {
        properties.setProperty("msgFrequency", String.valueOf(time));
    }

    public void setMcastDropTime(long time) {
        properties.setProperty("memberDropTime", String.valueOf(time));
    }




    /**
     * Check if a required property is available.
     * @param properties The set of properties
     * @param name The property to check for
     */
    protected void hasProperty(Properties properties, String name){
        if ( properties.getProperty(name)==null) throw new IllegalArgumentException("Required property \""+name+"\" is missing.");
    }

    /**
     * Start broadcasting and listening to membership pings
     * @throws java.lang.Exception if a IO error occurs
     */
    public void start() throws java.lang.Exception {
        start(1);
        start(2);
    }
    
    public void start(int level) throws java.lang.Exception {
        if ( impl != null ) {
            impl.start(level);
            return;
        }
        String host = getProperties().getProperty("tcpListenHost");
        int port = Integer.parseInt(getProperties().getProperty("tcpListenPort"));
        String name = "tcp://"+host+":"+port;
        if ( localMember == null ) {
            localMember = new McastMember(name, host, port, 100);
        } else {
            localMember.setName(name);
            localMember.setHost(host);
            localMember.setPort(port);
            localMember.setMemberAliveTime(100);
        }
        java.net.InetAddress bind = null;
        if ( properties.getProperty("mcastBindAddress")!= null ) {
            bind = java.net.InetAddress.getByName(properties.getProperty("mcastBindAddress"));
        }
        int ttl = -1;
        int soTimeout = -1;
        if ( properties.getProperty("mcastTTL") != null ) {
            try {
                ttl = Integer.parseInt(properties.getProperty("mcastTTL"));
            } catch ( Exception x ) {
                log.error("Unable to parse mcastTTL="+properties.getProperty("mcastTTL"),x);
            }
        }
        if ( properties.getProperty("mcastSoTimeout") != null ) {
            try {
                soTimeout = Integer.parseInt(properties.getProperty("mcastSoTimeout"));
            } catch ( Exception x ) {
                log.error("Unable to parse mcastSoTimeout="+properties.getProperty("mcastSoTimeout"),x);
            }
        }

        impl = new McastServiceImpl((McastMember)localMember,Long.parseLong(properties.getProperty("msgFrequency")),
                                    Long.parseLong(properties.getProperty("memberDropTime")),
                                    Integer.parseInt(properties.getProperty("mcastPort")),
                                    bind,
                                    java.net.InetAddress.getByName(properties.getProperty("mcastAddress")),
                                    ttl,
                                    soTimeout,
                                    this);

        impl.start(level);
        log.info("Sleeping for "+(Long.parseLong(properties.getProperty("msgFrequency"))*4)+" secs to establish cluster membership");
        Thread.sleep((Long.parseLong(properties.getProperty("msgFrequency"))*4));

    }

    /**
     * Stop broadcasting and listening to membership pings
     */
    public void stop() {
        try  {
            if ( impl != null) impl.stop();
        } catch ( Exception x)  {
            log.error("Unable to stop the mcast service.",x);
        }
        impl = null;
    }

    /**
     * Return all the members
     */
    public Member[] getMembers() {
        if ( impl == null || impl.membership == null ) return null;
        return impl.membership.getMembers();
    }
    /**
     * Add a membership listener, this version only supports one listener per service,
     * so calling this method twice will result in only the second listener being active.
     * @param listener The listener
     */
    public void addMembershipListener(MembershipListener listener) {
        this.listener = listener;
    }
    /**
     * Remove the membership listener
     */
    public void removeMembershipListener(){
        listener = null;
    }

    public void memberAdded(Member member) {
        if ( listener!=null ) listener.memberAdded(member);
    }

    /**
     * Callback from the impl when a new member has been received
     * @param member The member
     */
    public void memberDisappeared(Member member)
    {
        if ( listener!=null ) listener.memberDisappeared(member);
    }

    /**
     * Simple test program
     * @param args Command-line arguments
     * @throws Exception If an error occurs
     */
    public static void main(String args[]) throws Exception {
        log.info("Usage McastService hostname tcpport");
        McastService service = new McastService();
        java.util.Properties p = new java.util.Properties();
        p.setProperty("mcastPort","5555");
        p.setProperty("mcastAddress","224.10.10.10");
        p.setProperty("bindAddress","localhost");
        p.setProperty("memberDropTime","3000");
        p.setProperty("msgFrequency","500");
        p.setProperty("tcpListenPort",args[1]);
        p.setProperty("tcpListenHost",args[0]);
        service.setProperties(p);
        service.start();
        Thread.sleep(60*1000*60);
    }
    public int getMcastSoTimeout() {
        return mcastSoTimeout;
    }
    public void setMcastSoTimeout(int mcastSoTimeout) {
        this.mcastSoTimeout = mcastSoTimeout;
        properties.setProperty("mcastSoTimeout", String.valueOf(mcastSoTimeout));
    }
    public int getMcastTTL() {
        return mcastTTL;
    }
    public void setMcastTTL(int mcastTTL) {
        this.mcastTTL = mcastTTL;
        properties.setProperty("mcastTTL", String.valueOf(mcastTTL));
    }
}
