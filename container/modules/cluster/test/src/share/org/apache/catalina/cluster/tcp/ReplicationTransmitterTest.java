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
package org.apache.catalina.cluster.tcp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import junit.framework.TestCase;

import org.apache.catalina.cluster.session.ReplicationStream;
import org.apache.catalina.cluster.session.SessionMessageImpl;

/**
 * @author Peter Rossbach
 * 
 * @version $Revision$ $Date$
 */
public class ReplicationTransmitterTest extends TestCase {

    public void testCreateMessageData() throws Exception {
        ReplicationTransmitter transmitter = new ReplicationTransmitter();
        transmitter.setCompress(true);
        SessionMessageImpl message= new SessionMessageImpl();
        message.setUniqueId("test");
        ClusterData data = transmitter.serialize(message);
        assertTrue(200 < data.getMessage().length);
        Object myobj = getGZPObject(data.getMessage());
        assertTrue(myobj instanceof SessionMessageImpl);
        assertEquals("test", ((SessionMessageImpl)myobj).getUniqueId());
        
    }

    /**
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Object getGZPObject(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bin = 
            new ByteArrayInputStream(data);
        GZIPInputStream gin = 
            new GZIPInputStream(bin);
        byte[] tmp = new byte[1024];
        int length = gin.read(tmp);
        byte[] result = new byte[0];
        while (length > 0) {
            byte[] tmpdata = result;
            result = new byte[result.length + length];
            System.arraycopy(tmpdata, 0, result, 0, tmpdata.length);
            System.arraycopy(tmp, 0, result, tmpdata.length, length);
            length = gin.read(tmp);
        }
        gin.close();
        ReplicationStream stream = new ReplicationStream(
                new java.io.ByteArrayInputStream(result), getClass()
                        .getClassLoader());
        Object myobj = stream.readObject();
        return myobj;
    }
}
