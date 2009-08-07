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

package org.apache.catalina.cluster;

/**
 * The Member interface, defines a member in the Cluster.
 * A member is a Tomcat process that participates in session replication.<BR>
 * Each member can carry a set of properties, defined by the actual implementation.<BR>
 * For TCP replication has been targeted for the first release, the hostname and listen port
 * of the member is defined as hardcoded stuff.<BR>
 * The Member interface together with MembershipListener, MembershipService are interfaces used to
 * switch out the service used to establish membership in between the cluster nodes.
 *
 * @author Filip Hanik
 * @version $Revision$, $Date$
 */


public interface Member {
    /**
     * Return implementation specific properties about this cluster node.
     */
    public java.util.HashMap getMemberProperties();
    /**
     * Returns the name of this node, should be unique within the cluster.
     */
    public String getName();
  
    /**
     * Returns the name of the cluster domain from this node
     */
    public String getDomain();
    
    /**
     * Returns the TCP listen host for the TCP implementation
     */
    public String getHost();
    /**
     * Returns the TCP listen portfor the TCP implementation
     */
    public int getPort();

    /**
     * Contains information on how long this member has been online.
     * The result is the number of milli seconds this member has been
     * broadcasting its membership to the cluster.
     * @return nr of milliseconds since this member started.
     */
    public long getMemberAliveTime();
    
}
