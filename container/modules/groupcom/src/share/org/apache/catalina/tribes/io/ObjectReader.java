/*
 * Copyright 1999,2004-2005 The Apache Software Foundation.
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

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import org.apache.catalina.tribes.ChannelMessage;
import java.io.IOException;



/**
 * The object reader object is an object used in conjunction with
 * java.nio TCP messages. This object stores the message bytes in a
 * <code>XByteBuffer</code> until a full package has been received.
 * When a full package has been received, the append method will call messageDataReceived
 * on the callback object associated with this object reader.<BR>
 * This object uses an XByteBuffer which is an extendable object buffer that also allows
 * for message encoding and decoding.
 *
 * @author Filip Hanik
 * @version $Revision: 377484 $, $Date: 2006-02-13 15:00:05 -0600 (Mon, 13 Feb 2006) $
 */
public class ObjectReader {

    protected static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ObjectReader.class);

    private SocketChannel channel;

    private ListenCallback callback;

    private XByteBuffer buffer;

    /**
     * Create XByteBuffer and store parameter
     * @param channel
     * @param selector
     * @param callback
     */
    public ObjectReader(SocketChannel channel, Selector selector, ListenCallback callback) {
        this.channel = channel;
        this.callback = callback;
        try {
            this.buffer = new XByteBuffer(channel.socket().getReceiveBufferSize(), true);
        }catch ( IOException x ) {
            //unable to get buffer size
            log.warn("Unable to retrieve the socket channel receiver buffer size, setting to default 43800 bytes.");
            this.buffer = new XByteBuffer(43800,true);
        }
    }

    /**
     * get the current SimpleTcpCluster
     * @return Returns the callback.
     */
    public ListenCallback getCallback() {
        return callback;
    }

    /**
     * Get underlying NIO channel
     * @return The socket
     */
    public SocketChannel getChannel() {
        return this.channel;
    }

    /**
     * Append new bytes to buffer. 
     * @see XByteBuffer#countPackages()
     * @param data new transfer buffer
     * @param off offset
     * @param len length in buffer
     * @return number of messages that sended to callback
     * @throws java.io.IOException
     */
    public int append(ByteBuffer data, int len, boolean count) throws java.io.IOException {
       buffer.append(data,len);
       int pkgCnt = -1;
       if ( count ) pkgCnt = buffer.countPackages();
       return pkgCnt;
   }

     public int append(byte[] data,int off,int len, boolean count) throws java.io.IOException {
        buffer.append(data,off,len);
        int pkgCnt = -1;
        if ( count ) pkgCnt = buffer.countPackages();
        return pkgCnt;
    }

    /**
     * Send buffer to cluster listener (callback).
     * Is message complete receiver send message to callback?
     *
     * @see org.apache.catalina.tribes.tcp.ClusterReceiverBase#messageDataReceived(ChannelMessage)
     * @see XByteBuffer#doesPackageExist()
     * @see XByteBuffer#extractPackage(boolean)
     *
     * @return number of received packages/messages
     * @throws java.io.IOException
     */
    public int execute() throws java.io.IOException {
        int pkgCnt = 0;
        boolean pkgExists = buffer.doesPackageExist();
        while ( pkgExists ) {
            ChannelMessage data = buffer.extractPackage(true);
            getCallback().messageDataReceived(data);
            pkgCnt++;
            pkgExists = buffer.doesPackageExist();
        }
        return pkgCnt;
    }
    
    /**
     * Returns the number of packages that the reader has read
     * @return int
     */
    public int count() {
        return buffer.countPackages();
    }
    
    /**
     * Write Ack to sender
     * @param buf
     * @return The bytes written count
     * @throws java.io.IOException
     */
    public int write(ByteBuffer buf) throws java.io.IOException {
        return getChannel().write(buf);
    }

}
