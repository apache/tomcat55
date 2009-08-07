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
 * @version $Revision$, $Date$
 */

import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.ByteBuffer;
import org.apache.catalina.cluster.io.XByteBuffer;
public class ObjectReader
{
    private SocketChannel channel;
    private Selector selector;
    private ListenCallback callback;
    private XByteBuffer buffer;

    public ObjectReader( SocketChannel channel,
                         Selector selector,
                         ListenCallback callback )  {
        this.channel = channel;
        this.selector = selector;
        this.callback = callback;
        this.buffer = new XByteBuffer();
    }


    public SocketChannel getChannel()  {
        return this.channel;
    }

    public int append(byte[] data,int off,int len) throws java.io.IOException {
        boolean result = false;
        buffer.append(data,off,len);
        int pkgCnt = buffer.countPackages();
        return pkgCnt;
    }

    public int execute() throws java.io.IOException {
        int pkgCnt = 0;
        boolean pkgExists = buffer.doesPackageExist();
        while ( pkgExists ) {
            byte[] b = buffer.extractPackage(true);
            callback.messageDataReceived(b);
            pkgCnt++;
            pkgExists = buffer.doesPackageExist();
        }//end if
        return pkgCnt;
    }

    public int write(ByteBuffer buf)
       throws java.io.IOException {
        return getChannel().write(buf);
    }




}
