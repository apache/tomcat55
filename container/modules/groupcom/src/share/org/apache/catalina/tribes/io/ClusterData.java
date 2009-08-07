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
package org.apache.catalina.tribes.io;

import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import org.apache.catalina.tribes.mcast.McastMember;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import org.apache.catalina.tribes.util.UUIDGenerator;
import java.util.Arrays;

/**
 * The cluster data class is used to transport around the byte array from
 * a ClusterMessage object. This is just a utility class to avoid having to 
 * serialize and deserialize the ClusterMessage more than once. 
 * @author Peter Rossbach
 * @author Filip Hanik
 * @version $Revision: 377484 $ $Date: 2006-02-13 15:00:05 -0600 (Mon, 13 Feb 2006) $
 * @since 5.5.10
 */
public class ClusterData implements ChannelMessage {
    public static boolean USE_SECURE_RANDOM_FOR_UUID = false;
    
    private int options = 0 ;
    private XByteBuffer message ;
    private long timestamp ;
    private byte[] uniqueId ;
    private Member address;

    public ClusterData() {
        this(true);
    }
    
    public ClusterData(boolean generateUUID) {
        if ( generateUUID ) generateUUID();
    }
    
    
    
    /**
     * @param type message type (class)
     * @param uniqueId unique message id
     * @param message message data
     * @param timestamp message creation date
     */
    public ClusterData(byte[] uniqueId, XByteBuffer message, long timestamp) {
        this.uniqueId = uniqueId;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    /**
     * @return Returns the message.
     */
    public XByteBuffer getMessage() {
        return message;
    }
    /**
     * @param message The message to set.
     */
    public void setMessage(XByteBuffer message) {
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
    public byte[] getUniqueId() {
        return uniqueId;
    }
    /**
     * @param uniqueId The uniqueId to set.
     */
    public void setUniqueId(byte[] uniqueId) {
        this.uniqueId = uniqueId;
    }
    /**
     * @return Returns the compress.
     */
    public int getOptions() {

        return options;
    }
    /**
     * @param compress The compress to set.
     */
    public void setOptions(int options) {

        this.options = options;
    }
    
    public Member getAddress() {

        return address;
    }


    public void setAddress(Member address) {

        this.address = address;
    }
    
    public void generateUUID() {
        byte[] data = new byte[16];
        UUIDGenerator.randomUUID(USE_SECURE_RANDOM_FOR_UUID,data,0);
        setUniqueId(data);
    }

    
    
    /**
     * 
    private int options = 0 ;
    private long timestamp ;
    private String uniqueId ;
    private Member address;
    private byte[] message ;

     * @return byte[]
     */
    public byte[] getDataPackage()  {
        byte[] addr = ((McastMember)address).getData(false);
        int length = 
            4 + //options
            8 + //timestamp  off=4
            4 + //unique id length off=12
            uniqueId.length+ //id data off=12+uniqueId.length
            4 + //addr length off=12+uniqueId.length+4
            addr.length+ //member data off=12+uniqueId.length+4+add.length
            4 + //message length off=12+uniqueId.length+4+add.length+4
            message.getLength();
        byte[] data = new byte[length];
        int offset = 0;
        XByteBuffer.toBytes(options,data,offset);
        offset = 4; //options
        XByteBuffer.toBytes(timestamp,data,offset);
        offset += 8; //timestamp
        XByteBuffer.toBytes(uniqueId.length,data,offset);
        offset += 4; //uniqueId.length
        System.arraycopy(uniqueId,0,data,offset,uniqueId.length);
        offset += uniqueId.length; //uniqueId data
        XByteBuffer.toBytes(addr.length,data,offset);
        offset += 4; //addr.length
        System.arraycopy(addr,0,data,offset,addr.length);
        offset += addr.length; //addr data
        XByteBuffer.toBytes(message.getLength(),data,offset);
        offset += 4; //message.length
        System.arraycopy(message.getBytesDirect(),0,data,offset,message.getLength());
        offset += message.getLength(); //message data
        return data;
    }
    
    public static ClusterData getDataFromPackage(byte[] b)  {
        ClusterData data = new ClusterData(false);
        int offset = 0;
        data.setOptions(XByteBuffer.toInt(b,offset));
        offset += 4; //options
        data.setTimestamp(XByteBuffer.toLong(b,offset));
        offset += 8; //timestamp
        data.uniqueId = new byte[XByteBuffer.toInt(b,offset)];
        offset += 4; //uniqueId length
        System.arraycopy(b,offset,data.uniqueId,0,data.uniqueId.length);
        offset += data.uniqueId.length; //uniqueId data
        byte[] addr = new byte[XByteBuffer.toInt(b,offset)];
        offset += 4; //addr length
        System.arraycopy(b,offset,addr,0,addr.length);
        data.setAddress(McastMember.getMember(addr));
        offset += addr.length; //addr data
        data.message = new XByteBuffer(new byte[XByteBuffer.toInt(b,offset)],false);
        offset += 4; //message length
        System.arraycopy(b,offset,data.message.getBytesDirect(),0,data.message.getLength());
        offset += data.message.getLength(); //message data
        return data;
    }
    
    public int hashCode() {
        return XByteBuffer.toInt(getUniqueId(),0);
    }
    
    public boolean equals(Object o) {
        if ( o instanceof ClusterData ) {
            return Arrays.equals(getUniqueId(),((ClusterData)o).getUniqueId());
        } else return false;
    }
    
    public ClusterData clone() {
        byte[] d = this.getDataPackage();
        return ClusterData.getDataFromPackage(d);
    }
    
}
