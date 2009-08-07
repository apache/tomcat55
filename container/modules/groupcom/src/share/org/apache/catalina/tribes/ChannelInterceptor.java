/*
 * Copyright 1999,2004-2006 The Apache Software Foundation.
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

import org.apache.catalina.tribes.group.InterceptorPayload;

/**
 * Abstract class for the interceptor base class.
 * @author Filip Hanik
 * @version $Revision: 304032 $, $Date: 2005-07-27 10:11:55 -0500 (Wed, 27 Jul 2005) $
 */   

public interface ChannelInterceptor extends MembershipListener {

    public int getOptionFlag();
    public void setOptionFlag(int flag);

    public void setNext(ChannelInterceptor next) ;

    public ChannelInterceptor getNext();

    public void setPrevious(ChannelInterceptor previous);

    public ChannelInterceptor getPrevious();

    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException;
    
    public void messageReceived(ChannelMessage data);
    
    public void heartbeat();
    
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
    public Member getLocalMember(boolean incAliveTime) ;

    /**
     * 
     * @param mbr Member
     * @return Member - the actual member information, including stay alive
     */
    public Member getMember(Member mbr);
    
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

    
    

}
