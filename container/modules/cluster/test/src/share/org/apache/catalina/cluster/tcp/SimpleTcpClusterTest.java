/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

import org.apache.catalina.LifecycleException;
import org.apache.catalina.cluster.deploy.FarmWarDeployer;
import org.apache.catalina.cluster.mcast.McastService;
import org.apache.catalina.cluster.session.ClusterSessionListener;

/*
* @author Peter Rossbach
* @version $Revision$ $Date$
*/
public class SimpleTcpClusterTest extends TestCase {

    
    public void testCreateClusterSessionListenerAtStart() throws LifecycleException
    {
        SimpleTcpCluster cluster = new SimpleTcpCluster() ;
        cluster.setMembershipService( new McastService() { public void start() {} });
        cluster.setClusterDeployer(new FarmWarDeployer() { public void start() {}});
        SocketReplicationListener receiver = new SocketReplicationListener(){ public void start() {}};
        receiver.setTcpListenAddress("localhost");
        receiver.setTcpListenPort(45660);
        cluster.setClusterReceiver(receiver);
        cluster.setClusterSender(new ReplicationTransmitter(){ public void start() {}});
        cluster.start();
        assertEquals(1,cluster.clusterListeners.size());
        assertTrue( cluster.clusterListeners.get(0) instanceof ClusterSessionListener);
    }
}
