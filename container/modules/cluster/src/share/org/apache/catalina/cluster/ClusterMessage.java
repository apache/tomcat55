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

import java.io.Serializable;

public interface ClusterMessage
    extends Serializable {
    /**
     * Get the address that this message originated from.  This would be set
     * if the message was being relayed from a host other than the one
     * that originally sent it.
     */
    public Member getAddress();

    /**
     * Called by the cluster before sending it to the other
     * nodes
     * @param member Member
     */
    public void setAddress(Member member);

    /**
     * Timestamp message
     * @return long
     */
    public long getTimestamp();

    /**
     * Called by the cluster before sending out
     * the message
     * @param timestamp long
     */
    public void setTimestamp(long timestamp);

    /**
     * Each message must have a unique ID, in case of using async replication,
     * and a smart queue, this id is used to replace messages not yet sent
     * @return String
     */
    public String getUniqueId();

}
