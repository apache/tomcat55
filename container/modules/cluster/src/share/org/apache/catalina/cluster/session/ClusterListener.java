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

package org.apache.catalina.cluster.session;

import org.apache.catalina.cluster.CatalinaCluster;
import org.apache.catalina.cluster.ClusterMessage;
import org.apache.catalina.cluster.MessageListener;
import org.apache.catalina.util.StringManager;

/**
 * Receive SessionID cluster change from other backup node after primary session
 * node is failed.
 * 
 * @author Peter Rossbach
 * @version $Revision$ $Date$
 */
public abstract class ClusterListener implements MessageListener {

    public static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(ClusterListener.class);


    //--Instance Variables--------------------------------------

    /**
     * The string manager for this package.
     */
    protected StringManager sm = StringManager.getManager(Constants.Package);

    protected CatalinaCluster cluster = null;

    //--Constructor---------------------------------------------

    public ClusterListener() {
    }
    
    //--Instance Getters/Setters--------------------------------
    
    public CatalinaCluster getCluster() {
        return cluster;
    }

    public void setCluster(CatalinaCluster cluster) {
        if (log.isDebugEnabled()) {
            if (cluster != null)
                log.debug("add ClusterListener " + this.toString()
                        + " to cluster" + cluster);
            else
                log.debug("remove ClusterListener " + this.toString()
                        + " from cluster");
        }
        this.cluster = cluster;
    }

    public boolean equals(Object listener) {
        return super.equals(listener);
    }

    public int hashCode() {
        return super.hashCode();
    }

    //--Logic---------------------------------------------------


    /**
     * Callback from the cluster, when a message is received, The cluster will
     * broadcast it invoking the messageReceived on the receiver.
     * 
     * @param msg
     *            ClusterMessage - the message received from the cluster
     */
    public abstract void messageReceived(ClusterMessage msg) ;
    

    /**
     * Accept only SessionIDMessages
     * 
     * @param msg
     *            ClusterMessage
     * @return boolean - returns true to indicate that messageReceived should be
     *         invoked. If false is returned, the messageReceived method will
     *         not be invoked.
     */
    public abstract boolean accept(ClusterMessage msg) ;

}
