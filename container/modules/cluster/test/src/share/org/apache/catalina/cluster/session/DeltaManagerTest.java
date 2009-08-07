/*
 * Copyright 1999,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.catalina.cluster.session;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.catalina.Session;
import org.apache.catalina.cluster.ClusterMessage;
import org.apache.catalina.cluster.Member;
import org.apache.catalina.cluster.mcast.McastMember;
import org.apache.catalina.cluster.mcast.McastService;
import org.apache.catalina.cluster.tcp.SimpleTcpCluster;

/**
 * @author Peter Rossbach
 * 
 * @version $Revision$ $Date$
 */
public class DeltaManagerTest extends TestCase {

    public void testCreateSession() {
        MockDeltaManager manager = new MockDeltaManager() ;
        Session session = manager.createSession(null,false);
        assertNotNull(session.getId());
        assertEquals(32, session.getId().length());
        session = manager.createSession(null,true);
        assertEquals(session,manager.session);
        assertEquals(session.getId(),manager.sessionID);
    }
    
    public void testhandleGET_ALL_SESSIONS() throws Exception {
        MockDeltaManager manager = new MockDeltaManager() ;
        Session session = manager.createSession(null,false);
        assertEquals(session, manager.findSession(session.getId()));
        for (int i = 0; i < 10; i++) {
            manager.createSession(null,false);
        }
        assertEquals(11,manager.getSessionCounter());
        Member sender = new McastMember("test","d10","localhost",8080,3000);
        MockCluster cluster = new MockCluster ();
        manager.setCluster(cluster);
        manager.setSendAllSessionsSize(2);
        manager.handleGET_ALL_SESSIONS(null,sender);
        // send all session activ - 6 sessions message and one transfer complete
        assertEquals(2,cluster.sendcounter);
        
        // send session blockwise
        cluster.sendcounter=0;
        manager.setSendAllSessions(false);
        manager.handleGET_ALL_SESSIONS(null,sender);
        // 11 session activ - 6 sessions message and one transfer complete
        assertEquals(7,cluster.sendcounter);
    }
    
    public void testFirstMemberhandleGET_ALL_SESSIONS() throws Exception {
        MockDeltaManager manager = new MockDeltaManager() ;
        MockCluster cluster = new MockCluster ();
        McastService service = new MockMcastService() ; 
        cluster.setMembershipService(service);
        manager.setCluster(cluster);
        manager.getAllClusterSessions();
        assertEquals(0,cluster.sendcounter);
        manager.setSendClusterDomainOnly(false);
        manager.getAllClusterSessions();
        assertEquals(0,cluster.sendcounter);
        manager.setSendClusterDomainOnly(true);
        service.memberAdded(new McastMember("franz2", "d11", "127.0.0.1", 7002, 100));
        manager.getAllClusterSessions();
        assertEquals(0,cluster.sendcounter);
        service.memberAdded(new McastMember("franz3", "d10", "127.0.0.1", 7003, 100));
        assertNotNull(manager.findSessionMasterMember());
    }
    
    class MockMcastService extends McastService {
        List members = new ArrayList();
        
        public MockMcastService() {
            localMember =new McastMember("franz", "d10", "127.0.0.1", 7001, 100) ; 
       }
        public void memberAdded(Member member) {
            members.add(member);
        }
        public Member getLocalMember() {
            return localMember;
        }

        public Member[] getMembers() {
            return (Member[]) members.toArray(new Member[members.size()]);
        }
    }
    
    class MockDeltaManager extends DeltaManager {
        
        private String sessionID;
        private DeltaSession session;


        /**
         * 
         */
        public MockDeltaManager() {
            super();
        }
        
        
        /* (non-Javadoc)
         * @see org.apache.catalina.cluster.session.DeltaManager#sendCreateSession(java.lang.String, org.apache.catalina.cluster.session.DeltaSession)
         */
        protected void sendCreateSession(String sessionId, DeltaSession session) {
           this.sessionID = sessionId ;
           this.session = session ;
        }       
        
    }
    
    class MockCluster extends SimpleTcpCluster {
    
        private int sendcounter;

        /** don't send only count sends
         * @see org.apache.catalina.cluster.CatalinaCluster#send(org.apache.catalina.cluster.ClusterMessage, org.apache.catalina.cluster.Member)
         */
        public void send(ClusterMessage msg, Member dest) {
            sendcounter++ ;
        }
}
}
