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

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.mbeans.ServerLifecycleListener;
import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.SAXException;

/**
 * @author Peter Rossbach
 *  
 */
public class StoreLoaderTest extends TestCase {

    public void testDigester() throws IOException, SAXException {
        Digester digester = StoreLoader.createDigester();
        String example = "<Registry name=\"Tomcat\" version=\"5.5.0\" encoding=\"UTF-8\" >"
                + " <Description "
                + "  tag=\"Server\""
                + "	standard=\"true\""
                + "	default=\"true\""
                + "  tagClass=\"org.apache.catalina.core.StandardServer\""
                + "  storeFactoryClass=\"org.apache.catalina.storeconfig.StandardServerSF\">"
                + " </Description>" + "</Registry>";
        StringReader reader = new StringReader(example);
        StoreRegistry registry = (StoreRegistry) digester.parse(reader);
        assertNotNull(registry);
        assertEquals("Tomcat", registry.getName());
        assertEquals("5.5.0", registry.getVersion());
        StoreDescription desc = registry.findDescription(StandardServer.class);
        assertNotNull(desc);
        assertEquals("org.apache.catalina.core.StandardServer", desc
                .getTagClass());
        assertEquals("Server", desc.getTag());
    }

    public void testLoadRegistry() {
        StoreLoader loader = new StoreLoader();
        loader.load();
        StoreRegistry registry = loader.getRegistry();
        assertNotNull(registry);
        assertEquals("UTF-8", registry.getEncoding());
        StoreDescription desc = registry.findDescription(StandardServer.class);
        assertNotNull(desc);
        assertEquals("org.apache.catalina.core.StandardServer", desc
                .getTagClass());
        desc = registry.findDescription(StandardContext.class);
        assertNotNull(desc);
        assertEquals(StandardContext.class.getName(), desc.getTagClass());
        assertTrue(desc.isStoreSeparate());
        assertNotNull(desc.getStoreFactory());
        assertEquals(registry, desc.getStoreFactory().getRegistry());
        assertEquals(StandardContextSF.class, desc.getStoreFactory().getClass());
        desc = registry
                .findDescription("org.apache.catalina.core.StandardServer.[ServerLifecycleListener]");
        assertEquals(ServerLifecycleListener.class.getName(), desc
                .getTagClass());
    }
}