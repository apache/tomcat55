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
package org.apache.catalina.tribes;

import java.io.Serializable;

/**
 * Channel interface
 * A channel is an object that manages a group of members.
 * It manages a complete cluster group, both membership and replication.
 * @author Filip Hanik
 * @version $Revision: 304032 $, $Date: 2005-07-27 10:11:55 -0500 (Wed, 27 Jul 2005) $
 */
public interface Channel {
    
    /**
     * Start and stop sequences can be controlled by these constants
     */
    public static final int DEFAULT = 15;
    public static final int MBR_RX_SEQ = 1;
    public static final int SND_TX_SEQ = 2;
    public static final int SND_RX_SEQ = 4;
    public static final int MBR_TX_SEQ = 8;
    
    /**
     * Adds an interceptor to the channel message chain.
     * @param interceptor ChannelInterceptor
     */
    public void addInterceptor(ChannelInterceptor interceptor);
    
    /**
     * Starts up the channel. This can be called multiple times for individual services to start
     * The svc parameter can be the logical or value of any constants
     * @param svc int value of <BR>
     * DEFAULT - will start all services <BR>
     * MBR_RX_SEQ - starts the membership receiver <BR>
     * MBR_TX_SEQ - starts the membership broadcaster <BR>
     * SND_TX_SEQ - starts the replication transmitter<BR>
     * SND_RX_SEQ - starts the replication receiver<BR>
     * @throws ChannelException if a startup error occurs or the service is already started.
     */
    public void start(int svc) throws ChannelException;

    /**
     * Shuts down the channel. This can be called multiple times for individual services to shutdown
     * The svc parameter can be the logical or value of any constants
     * @param svc int value of <BR>
     * DEFAULT - will shutdown all services <BR>
     * MBR_RX_SEQ - stops the membership receiver <BR>
     * MBR_TX_SEQ - stops the membership broadcaster <BR>
     * SND_TX_SEQ - stops the replication transmitter<BR>
     * SND_RX_SEQ - stops the replication receiver<BR>
     * @throws ChannelException if a startup error occurs or the service is already started.
     */
    public void stop(int svc) throws ChannelException;    
    
    /**
     * Send a message to one or more members in the cluster
     * @param destination Member[] - the destinations, null or zero length means all
     * @param msg ClusterMessage - the message to send
     * @param options int - sender options, see class documentation
     * @return ClusterMessage[] - the replies from the members, if any. 
     */
    public void send(Member[] destination, Serializable msg) throws ChannelException;

    
    /**
     * Sends a heart beat through the interceptor stacks
     */
    public void heartbeat();
    
    public void setMembershipListener(MembershipListener listener);
    public void setChannelListener(ChannelListener listener);
    
    /**
     * has members
     */
    public boolean hasMembers() ;

    /**
     * Get all current cluster members
     * @return all members or empty array 
     */
    public Member[] getMembers() ;

    /**
     * Return the member that represents this node.
     * 
     * @return Member
     */
    public Member getLocalMember() ;
    
    /**
     * 
     * @param mbr Member
     * @return Member
     */
    public Member getMember(Member mbr);

    
}
