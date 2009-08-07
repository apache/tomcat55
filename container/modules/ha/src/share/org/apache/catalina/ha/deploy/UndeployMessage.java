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

package org.apache.catalina.ha.deploy;

import java.io.Serializable;

import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.ClusterMessageBase;

public class UndeployMessage extends ClusterMessageBase implements ClusterMessage,Serializable {
    private long timestamp;
    private String uniqueId;
    private String contextPath;
    private boolean undeploy;

    public UndeployMessage() {} //for serialization
    public UndeployMessage(long timestamp,
                           String uniqueId,
                           String contextPath,
                           boolean undeploy) {
        this.timestamp= timestamp;
        this.undeploy = undeploy;
        this.uniqueId = uniqueId;
        this.undeploy = undeploy;
        this.contextPath = contextPath;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public boolean getUndeploy() {
        return undeploy;
    }

    public void setUndeploy(boolean undeploy) {
        this.undeploy = undeploy;
    }
}
