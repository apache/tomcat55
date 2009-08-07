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
package org.apache.catalina.tribes;

import java.io.Serializable;
import org.apache.catalina.tribes.io.XByteBuffer;

/**
 * @author Filip Hanik
 * 
 */
public interface ChannelMessage extends Serializable {
    
    
    
    
    /**
     * Get the address that this message originated from.  This would be set
     * if the message was being relayed from a host other than the one
     * that originally sent it.
     */
    public Member getAddress();

    /**
     * Called by the cluster before sending it to the other
     * nodes.
     *
     * @param member Member
     */
    public void setAddress(Member member);

    /**
     * Timestamp message.
     *
     * @return long
     */
    public long getTimestamp();

    /**
     * Called by the cluster before sending out
     * the message.
     *
     * @param timestamp The timestamp
     */
    public void setTimestamp(long timestamp);

    /**
     * Each message must have a unique ID, in case of using async replication,
     * and a smart queue, this id is used to replace messages not yet sent.
     *
     * @return byte
     */
    public byte[] getUniqueId();
    
    public void setMessage(XByteBuffer buf);
    
    public XByteBuffer getMessage();
    
    public int getOptions();
    public void setOptions(int options);
    
    /**
     * Shallow clone, only the actual message(getMessage()) is cloned, the rest remains as references
     * @return ChannelMessage
     */
    public Object clone();

    /**
     * Deep clone, everything gets cloned
     * @return ChannelMessage
     */
    public Object deepclone();
}
