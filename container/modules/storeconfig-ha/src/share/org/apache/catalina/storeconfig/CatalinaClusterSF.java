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

package org.apache.catalina.storeconfig;

import java.io.PrintWriter;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.ha.ClusterDeployer;
import org.apache.catalina.tribes.ChannelReceiver;
import org.apache.catalina.tribes.ChannelSender;
import org.apache.catalina.tribes.MembershipService;
import org.apache.catalina.tribes.MessageListener;
import org.apache.catalina.ha.tcp.SimpleTcpCluster;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.catalina.tribes.ManagedChannel;
import java.util.Iterator;
import org.apache.catalina.ha.ClusterListener;

/**
 * Generate Cluster Element with Membership,Sender,Receiver,Deployer and
 * ReplicationValve
 * 
 * @author Peter Rossbach
 */
public class CatalinaClusterSF extends StoreFactoryBase {

    private static Log log = LogFactory.getLog(CatalinaClusterSF.class);

    /**
     * Store the specified Cluster childs.
     * 
     * @param aWriter
     *            PrintWriter to which we are storing
     * @param indent
     *            Number of spaces to indent this element
     * @param aCluster
     *            Cluster whose properties are being stored
     * 
     * @exception Exception
     *                if an exception occurs while storing
     */
    public void storeChilds(PrintWriter aWriter, int indent, Object aCluster,
            StoreDescription parentDesc) throws Exception {
        if (aCluster instanceof CatalinaCluster) {
            CatalinaCluster cluster = (CatalinaCluster) aCluster;
            
            //store nested <Group> element
            ManagedChannel channel = (ManagedChannel)cluster.getChannel();
            if (channel != null ) {
                storeElement(aWriter, indent, channel);

                // Store nested <Membership> element
                MembershipService service = channel.getMembershipService();
                if (service != null) {
                    storeElement(aWriter, indent, service);
                }
                // Store nested <Sender> element
                ChannelSender sender = channel.getChannelSender();
                if (sender != null) {
                    storeElement(aWriter, indent, sender);
                }
                // Store nested <Receiver> element
                ChannelReceiver receiver = channel.getChannelReceiver();
                if (receiver != null) {
                    storeElement(aWriter, indent, receiver);
                }
                
                Iterator inti = channel.getInterceptors();
                while ( inti.hasNext() ) {
                    storeElement(aWriter,indent,inti.next());
                }
            }
            // Store nested <Deployer> element
            ClusterDeployer deployer = cluster.getClusterDeployer();
            if (deployer != null) {
                storeElement(aWriter, indent, deployer);
            }
            // Store nested <Valve> element
            // ClusterValve are not store at Hosts element, see
            Valve valves[] = cluster.getValves();
            storeElementArray(aWriter, indent, valves);
 
            if (aCluster instanceof SimpleTcpCluster) {
                // Store nested <Listener> elements
                LifecycleListener listeners[] = ((SimpleTcpCluster)cluster).findLifecycleListeners();
                storeElementArray(aWriter, indent, listeners);
                // Store nested <ClusterListener> elements
                ClusterListener mlisteners[] = ((SimpleTcpCluster)cluster).findClusterListeners();
                storeElementArray(aWriter, indent, mlisteners);
            }            
        }
    }
}