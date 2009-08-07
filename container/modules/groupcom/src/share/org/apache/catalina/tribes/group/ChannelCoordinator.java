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
package org.apache.catalina.tribes.group;

import org.apache.catalina.tribes.MembershipService;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelSender;
import org.apache.catalina.tribes.ChannelReceiver;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.InterceptorPayload;
import org.apache.catalina.tribes.MessageListener;


/**
 * The channel coordinator object coordinates the membership service,
 * the sender and the receiver.
 * This is the last interceptor in the chain.
 * @author Filip Hanik
 * @version $Revision: 304032 $, $Date: 2005-07-27 10:11:55 -0500 (Wed, 27 Jul 2005) $
 */
public class ChannelCoordinator extends ChannelInterceptorBase implements MessageListener {
    private ChannelReceiver clusterReceiver;
    private ChannelSender clusterSender;
    private MembershipService membershipService;

    public ChannelCoordinator() {
        
    }
    
    public ChannelCoordinator(ChannelReceiver receiver,
                              ChannelSender sender,
                              MembershipService service) {
        this();
        this.setClusterReceiver(receiver);
        this.setClusterSender(sender);
        this.setMembershipService(service);
    }
    
    /**
     * Send a message to one or more members in the cluster
     * @param destination Member[] - the destinations, null or zero length means all
     * @param msg ClusterMessage - the message to send
     * @param options int - sender options, see class documentation
     * @return ClusterMessage[] - the replies from the members, if any.
     */
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        if ( destination == null ) destination = membershipService.getMembers();
        clusterSender.sendMessage(msg,destination);
    }


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
    public void start(int svc) throws ChannelException {
        try {
            //synchronize, big time FIXME
            membershipService.setLocalMemberProperties(getClusterReceiver().getHost(), getClusterReceiver().getPort());
            //end FIXME
            if ( (svc & Channel.SND_RX_SEQ) == Channel.SND_RX_SEQ) clusterReceiver.start();
            if ( (svc & Channel.SND_TX_SEQ) == Channel.SND_TX_SEQ) clusterSender.start();
            if ( (svc & Channel.MBR_RX_SEQ) == Channel.MBR_RX_SEQ) membershipService.start(MembershipService.MBR_RX);
            if ( (svc & Channel.MBR_TX_SEQ) == Channel.MBR_TX_SEQ) membershipService.start(MembershipService.MBR_TX);
        }catch ( ChannelException cx ) {
            throw cx;
        }catch ( Exception x ) {
            throw new ChannelException(x);
        }
    }

    /**
     * Shuts down the channel. This can be called multiple times for individual services to shutdown
     * The svc parameter can be the logical or value of any constants
     * @param svc int value of <BR>
     * DEFAULT - will shutdown all services <BR>
     * MBR_RX_SEQ - starts the membership receiver <BR>
     * MBR_TX_SEQ - starts the membership broadcaster <BR>
     * SND_TX_SEQ - starts the replication transmitter<BR>
     * SND_RX_SEQ - starts the replication receiver<BR>
     * @throws ChannelException if a startup error occurs or the service is already started.
     */
    public void stop(int svc) throws ChannelException {
        try {
            if ( (svc & Channel.MBR_RX_SEQ) == Channel.MBR_RX_SEQ) membershipService.stop();
            if ( (svc & Channel.SND_RX_SEQ) == Channel.SND_RX_SEQ) clusterReceiver.stop();
            if ( (svc & Channel.SND_TX_SEQ) == Channel.SND_TX_SEQ) clusterSender.stop();
            if ( (svc & Channel.MBR_TX_SEQ) == Channel.MBR_RX_SEQ) membershipService.stop();
        }catch ( Exception x ) {
            throw new ChannelException(x);
        }

    }
    
    public void memberAdded(Member member){
        if ( clusterSender!=null ) clusterSender.add(member);
        super.memberAdded(member);
    }
    
    public void memberDisappeared(Member member){
        if ( clusterSender!=null ) clusterSender.remove(member);
        super.memberDisappeared(member);
    }
    
    public void messageReceived(ChannelMessage msg) {
        super.messageReceived(msg);
    }


    public ChannelReceiver getClusterReceiver() {
        return clusterReceiver;
    }

    public ChannelSender getClusterSender() {
        return clusterSender;
    }

    public MembershipService getMembershipService() {
        return membershipService;
    }

    public void setClusterReceiver(ChannelReceiver clusterReceiver) {
        if ( clusterReceiver != null ) {
            this.clusterReceiver = clusterReceiver;
            this.clusterReceiver.setMessageListener(this);
        } else {
            if  (this.clusterReceiver!=null ) this.clusterReceiver.setMessageListener(null);
            this.clusterReceiver = null;
        }
    }

    public void setClusterSender(ChannelSender clusterSender) {
        this.clusterSender = clusterSender;
    }

    public void setMembershipService(MembershipService membershipService) {
        this.membershipService = membershipService;
        this.membershipService.setMembershipListener(this);
    }
    
    public void hearbeat() {
        if ( clusterSender!=null ) clusterSender.heartbeat();
        super.heartbeat();
    }
   
}
