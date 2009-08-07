/*
 * Copyright 1999,2005 The Apache Software Foundation.
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
package org.apache.catalina.cluster.tcp;

import org.apache.catalina.cluster.ClusterMessage;


/**
 * @author Peter Rossbach
 * @version $Revision$ $Date$
 * @since 5.5.10
 */
public class ClusterData {

    private int resend = ClusterMessage.FLAG_DEFAULT ;
    private int compress = ClusterMessage.FLAG_DEFAULT ;
    private byte[] message ;
    private long timestamp ;
    private String uniqueId ;
    private String type ;
    
    public ClusterData() {}
    
    /**
     * @param type message type (class)
     * @param uniqueId unique message id
     * @param message message data
     * @param timestamp message creation date
     */
    public ClusterData(String type, String uniqueId, byte[] message, long timestamp
            ) {
        this.uniqueId = uniqueId;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    
    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @return Returns the message.
     */
    public byte[] getMessage() {
        return message;
    }
    /**
     * @param message The message to set.
     */
    public void setMessage(byte[] message) {
        this.message = message;
    }
    /**
     * @return Returns the timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }
    /**
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    /**
     * @return Returns the uniqueId.
     */
    public String getUniqueId() {
        return uniqueId;
    }
    /**
     * @param uniqueId The uniqueId to set.
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
    /**
     * @return Returns the compress.
     */
    public int getCompress() {
        return compress;
    }
    /**
     * @param compress The compress to set.
     */
    public void setCompress(int compress) {
        this.compress = compress;
    }
    /**
     * @return Returns the resend.
     */
    public int getResend() {
        return resend;
    }
    /**
     * @param resend The resend to set.
     */
    public void setResend(int resend) {
        this.resend = resend;
    }
}
