/*
 * Copyright 1999-2001,2004 The Apache Software Foundation.
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
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.catalina.core.StandardServer;

/**
 * @author Peter Rossbach
 *  
 */
public class StoreRegistryTest extends TestCase {

    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    StandardServer standardServer = new StandardServer();

    IStoreFactory factory;

    StoreDescription desc;

    /*
     * create registry and register Server 
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        registry = new StoreRegistry();
        desc = new StoreDescription();
        desc.setTag("Server");
        desc.setTagClass("org.apache.catalina.core.StandardServer");
        desc.setStandard(true);
        desc
                .setStoreFactoryClass("org.apache.catalina.storeconfig.StoreFactoryBase");
        registry.registerDescription(desc);
        factory = new StoreFactoryBase();
        factory.setRegistry(registry);
    }

    public void testSaveServer() throws Exception {
        assertNotNull(registry.findDescription(StandardServer.class));
        factory.store(pWriter, -2, standardServer);
        assertEquals("XML Diff", "<Server/>" + LF.LINE_SEPARATOR, writer
                .toString());
    }

    public void testAttributes() throws Exception {
        standardServer.setPort(7305);
        factory.store(pWriter, -2, standardServer);
        assertEquals("XML Diff", "<Server" + LF.LINE_SEPARATOR
                + "  port=\"7305\"/>" + LF.LINE_SEPARATOR, writer.toString());

    }

}