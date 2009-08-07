/*
 * Copyright 1999,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.catalina.cluster.io;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.catalina.cluster.tcp.ClusterData;

/**
 * @author Peter Rossbach
 * 
 * @version $Revision$ $Date$
 */
public class XByteBufferTest extends TestCase {

    public void testBytes() {
        XByteBuffer xbuf = new XByteBuffer() ;
        assertEquals(0,xbuf.bufSize);
        assertEquals(1024,xbuf.buf.length);
        assertEquals(0,xbuf.getBytes().length);
        assertNotSame(xbuf.buf,xbuf.getBytes());
    }
    
    
    /**
     * Test append to message buffer
     * Test that only correct message header are appended 
     */
    public void testAppend() {
        XByteBuffer xbuf = new XByteBuffer();
        byte[] bs = new byte[] { 1, 2, 3 };
        boolean status = xbuf.append(bs, 0, 3);
        assertTrue(status);
        assertEquals(3, xbuf.bufSize);
        byte buf[] = xbuf.getBytes();
        assertEquals(3, buf.length);
        status = xbuf.append(bs, 0, 3);
        assertTrue(status);
        status = xbuf.append(bs, 0, 3);
        // not a correct message !!
        assertFalse(status);
        // buffer is cleared
        assertEquals(0,xbuf.bufSize);
        status = xbuf.append(XByteBuffer.START_DATA,0, XByteBuffer.START_DATA.length);
        assertTrue(status);      
    }
    
    /**
     * Test create a uncompressed multi message data package and extract it
     * @throws IOException
     */
    public void testSendUncompressedMessage() throws IOException {
        assertSendMessage();
    }

    /**
     * @throws IOException
     */
    private void assertSendMessage() throws IOException {
        byte[] test = createMessage();
        XByteBuffer b = new XByteBuffer();
        b.append(test, 0, test.length);
        int s = b.countPackages();
        byte[] d ;
        ClusterData data ;
        assertEquals(3, s);
        for (byte i = 1; i < 4; i++) {
            data = b.extractPackage(true);
            d = data.getMessage();
            assertEquals(i, d[0]);
        }
    }

    /**
     * @return
     * @throws IOException
     */
    private byte[] createMessage() throws IOException {
        byte[] d1 = XByteBuffer.createDataPackage(new byte[] { 1 });
        byte[] d2 = XByteBuffer.createDataPackage(new byte[] { 2 });
        byte[] d3 = XByteBuffer.createDataPackage(new byte[] { 3 });
        byte[] test = new byte[d1.length + d2.length + d3.length + 5];
        System.arraycopy(d1, 0, test, 0, d1.length);
        System.arraycopy(d2, 0, test, d1.length, d2.length);
        System.arraycopy(d3, 0, test, d2.length + d1.length, d3.length);
        return test;
    }


    /**
     * Test the type convertes to and from byte array
     */
    public void testTypeconverter() {
        byte[] d = XByteBuffer.toBytes(Integer.MAX_VALUE);
        assertEquals(4, d.length);
        assertEquals(Integer.MAX_VALUE, XByteBuffer.toInt(d, 0));

        d = XByteBuffer.toBytes(Long.MAX_VALUE);
        assertEquals(8, d.length);
        assertEquals(Long.MAX_VALUE, XByteBuffer.toLong(d, 0));
        d = XByteBuffer.toBytes((long) 4564564);
        assertEquals(4564564, XByteBuffer.toLong(d, 0));
    }

}
