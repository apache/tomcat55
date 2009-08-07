/*
 * Copyright 1999-2001,2004 The Apache Software Foundation.
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

package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.cluster.ClusterValve;
import org.apache.catalina.core.StandardHost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Store server.xml Element Host
 * 
 * @author Peter Rossbach
 */
public class StandardHostSF extends StoreFactoryBase {

    private static Log log = LogFactory.getLog(StandardHostSF.class);

    /**
     * Store the specified Host properties and childs
     * (Listener,Alias,Realm,Valve,Cluster, Context)
     * 
     * @param aWriter
     *            PrintWriter to which we are storing
     * @param indent
     *            Number of spaces to indent this element
     * @param aHost
     *            Host whose properties are being stored
     * 
     * @exception Exception
     *                if an exception occurs while storing
     */
    public void storeChilds(PrintWriter aWriter, int indent, Object aHost,
            StoreDescription parentDesc) throws Exception {
        if (aHost instanceof StandardHost) {
            StandardHost host = (StandardHost) aHost;
            // Store nested <Listener> elements
            if (host instanceof Lifecycle) {
                LifecycleListener listeners[] = ((Lifecycle) host)
                        .findLifecycleListeners();
                storeElementArray(aWriter, indent, listeners);
            }

            // Store nested <Alias> elements
            String aliases[] = host.findAliases();
            getStoreAppender().printTagArray(aWriter, "Alias", indent + 2,
                    aliases);

            // Store nested <Realm> element
            Realm realm = host.getRealm();
            if (realm != null) {
                Realm parentRealm = null;
                if (host.getParent() != null) {
                    parentRealm = host.getParent().getRealm();
                }
                if (realm != parentRealm) {
                    storeElement(aWriter, indent, realm);
                }
            }

            // Store nested <Valve> elements
            if (host instanceof Pipeline) {
                Valve valves[] = ((Pipeline) host).getValves();
                if(valves != null && valves.length > 0 ) {
                    List hostValves = new ArrayList() ;
                    for(int i = 0 ; i < valves.length ; i++ ) {
                        if(!( valves[i] instanceof ClusterValve))
                            hostValves.add(valves[i]);
                    }                
                    storeElementArray(aWriter, indent, hostValves.toArray());
                }
            }

            // store all <Cluster> elements
            Cluster cluster = host.getCluster();
            if (cluster != null) {
                storeElement(aWriter, indent, cluster);
            }

            // store all <Context> elements
            Container children[] = host.findChildren();
            storeElementArray(aWriter, indent, children);
        }
    }

}