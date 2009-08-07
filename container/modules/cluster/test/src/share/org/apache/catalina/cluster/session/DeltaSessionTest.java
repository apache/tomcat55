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

import junit.framework.TestCase;

import org.apache.catalina.Manager;

/**
 * @author Peter Rossbach
 * 
 * @version $Revision$ $Date$
 */
public class DeltaSessionTest extends TestCase {

    public void testPrimarySessionisValid() {
        DeltaManager manager = new DeltaManager() { public DeltaSession getNewDeltaSession() { return new MockSession(this) ; } } ;
        manager.setMaxInactiveInterval(1);
        MockSession session = (MockSession)manager.createSession("hello",false);
        assertTrue(session.isPrimarySession());
        assertTrue(session.isValid()) ;
        assertEquals(1,session.getMaxInactiveInterval());
        try {
            Thread.sleep(2000);
        } catch (Exception sleep) {
        }
        long timeNow = System.currentTimeMillis();
        int timeIdle = (int) ((timeNow - session.getLastAccessedTime()) / 1000L);
        assertEquals(2,timeIdle);
        assertTrue(timeIdle > session.getMaxInactiveInterval());
        assertFalse(session.isValid()) ;
        assertEquals(1,session.expireNotifyCalled);
        assertEquals(1,session.expireNotifyClusterCalled);
    }
 
    public void testBackupSessionisValid() {
        DeltaManager manager = new DeltaManager() { public DeltaSession getNewDeltaSession() { return new MockSession(this) ; } } ;
        manager.setMaxInactiveInterval(1);
        MockSession session = (MockSession)manager.createSession("hello",false);
        session.setPrimarySession(false);
        try {
            Thread.sleep(1000);
        } catch (Exception sleep) {
        }
        assertTrue(session.isValid()) ;
        assertEquals(0,session.expireNotifyCalled);
        assertEquals(0,session.expireNotifyClusterCalled);
        try {
            Thread.sleep(2000);
        } catch (Exception sleep) {
        }
        assertFalse(session.isValid()) ;
        assertEquals(1,session.expireNotifyCalled);
        // no cluster notification
        assertEquals(0,session.expireNotifyClusterCalled);
    }
    
    class MockSession extends DeltaSession {

        long expireCalled = 0 ;
        long expireNotifyCalled = 0 ;
        long expireNotifyClusterCalled = 0 ;
        
        
        /**
         * @param manager
         */
        public MockSession(Manager manager) {
            super(manager);
        }
      
        /* (non-Javadoc)
         * @see org.apache.catalina.cluster.session.DeltaSession#expire(boolean, boolean)
         */
        public void expire(boolean notify, boolean notifyCluster) {
            expireCalled++ ;
            if(notify)
                expireNotifyCalled++ ;
            if(notifyCluster)
                expireNotifyClusterCalled++ ;
            isValid = false ;
        }
    }
}
