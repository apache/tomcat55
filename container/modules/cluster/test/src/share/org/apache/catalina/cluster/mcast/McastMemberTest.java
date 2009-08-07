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
package org.apache.catalina.cluster.mcast;

import org.apache.catalina.cluster.io.XByteBuffer;

import junit.framework.TestCase;

/**
 * @author Peter Rossbach
 * 
 * @version $Revision$
 */
public class McastMemberTest extends TestCase{

    private static final String TEST = "0123456789";
    public void testGetMember() throws Exception {
        byte[] data = new byte[44];
        long alive=System.currentTimeMillis()-20;
        System.arraycopy(XByteBuffer.toBytes((long)alive),0,data,0,8);
        System.arraycopy(XByteBuffer.toBytes(20000),0,data,8,4);
        System.arraycopy(XByteBuffer.toBytes(120000000),0,data,12,4);
        System.arraycopy(XByteBuffer.toBytes(10),0,data,16,4);
        System.arraycopy(TEST.getBytes(),0,data,20,TEST.length());
        System.arraycopy(XByteBuffer.toBytes(10),0,data,30,4);
        System.arraycopy(TEST.getBytes(),0,data,34,TEST.length());
        McastMember member = McastMember.getMember(data);
        assertEquals(member.getName(),TEST);
        assertEquals(member.getDomain(),TEST);
        assertEquals(member.getPort(),20000);
        assertEquals(member.getHost(),"7.39.14.0");
        byte[] data1 = member.getData(20);
        assertEquals(data.length,data1.length);
        McastMember member1 = McastMember.getMember(data1);
        assertEquals(member1.getName(),TEST);
        assertEquals(member1.getDomain(),TEST);
        assertEquals(member1.getPort(),20000);
        assertEquals(member1.getHost(),"7.39.14.0");
        
    }
}
