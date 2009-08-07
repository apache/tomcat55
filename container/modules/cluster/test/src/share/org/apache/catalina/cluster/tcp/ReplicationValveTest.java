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
package org.apache.catalina.cluster.tcp;

import junit.framework.TestCase;

/**
 * @author Peter Rossbach
 * 
 * @version $Revision$ $Date$
 */
public class ReplicationValveTest extends TestCase {

    
    public void testIsRequestWithoutSessionChange() {
        ReplicationValve valve = new ReplicationValve() ;
        valve.setFilter(".*\\.html");
        assertEquals(1,valve.getReqFilters().length) ;
        assertTrue(valve.isRequestWithoutSessionChange("/ClusterTest/index.html")) ;        
        assertFalse(valve.isRequestWithoutSessionChange("/ClusterTest/index.jsp")) ;        
        valve.setFilter(".*\\.html;.*\\.css");
        assertEquals(2,valve.getReqFilters().length) ;
        assertTrue(valve.isRequestWithoutSessionChange("/ClusterTest/index.html")) ;        
        assertTrue(valve.isRequestWithoutSessionChange("/ClusterTest/layout.css")) ;        
    }
    
}
