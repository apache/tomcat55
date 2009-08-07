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

package org.apache.catalina.cluster.io;


import java.net.Socket;
import org.apache.catalina.cluster.io.XByteBuffer;

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
 * @author Peter Rossbach
 * @version $Revision$, $Date$
 */
public class Jdk13ObjectReader
{
    private Socket socket;
    private ListenCallback callback;
    private XByteBuffer buffer;

    /**
     * use this socket and callback to receive messages
     * @param socket listener socket
     * @param callback SimpleTcpCluster listener
     * @param compress is send message data compress or flat.
     */
    public Jdk13ObjectReader( Socket socket,
                               ListenCallback callback, boolean compress)  {
        this.socket = socket;
        this.callback = callback;
        this.buffer = new XByteBuffer(compress);
    }

    
    /**
     * Append new bytes to buffer. 
     * Is message complete receiver send message to callback
     * @see org.apache.catalina.cluster.tcp.SimpleTcpCluster#messageDataReceived(byte[])
     * @see XByteBuffer#doesPackageExist()
     * @see XByteBuffer#extractPackage(boolean)
     * @param data new transfer buffer
     * @param off offset
     * @param len length in buffer
     * @return number of messages that sended to callback
     * @throws java.io.IOException
     */
    public int append(byte[] data,int off,int len) throws java.io.IOException {
        boolean result = false;
        buffer.append(data,off,len);
        int pkgCnt = 0;
        boolean pkgExists = buffer.doesPackageExist();
        while ( pkgExists ) {
            byte[] b = buffer.extractPackage(true);
            callback.messageDataReceived(b);
            pkgCnt++;
            pkgExists = buffer.doesPackageExist();
        }
        return pkgCnt;
    }

    
    /**
     * send message to callback
     * @see Jdk13ObjectReader#append(byte[], int, int)
     * @return
     * @throws java.io.IOException
     */
    public int execute() throws java.io.IOException {
        return append(new byte[0],0,0);
    }

    /**
     * write data to socket (ack)
     * @see org.apache.catalina.cluster.tcp.Jdk13ReplicationListener#sendAck
     * @param data
     * @return
     * @throws java.io.IOException
     */
    public int write(byte[] data)
       throws java.io.IOException {
       socket.getOutputStream().write(data);
       return 0;

    }
}
