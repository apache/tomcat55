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
import java.util.List;

import junit.framework.TestCase;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Connector;

/**
 * @author Peter Rossbach
 *  
 */
public class ConnectorStoreAppenderTest extends TestCase {
    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    Connector connector;

    ConnectorSF factory;

    StoreDescription desc;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        registry = new StoreRegistry();
        desc = new StoreDescription();
        desc.setTag("Connector");
        desc.setTagClass("org.apache.catalina.connector.Connector");
        desc.setStandard(false);
        desc
                .setStoreFactoryClass("org.apache.catalina.storeconfig.ConnectorSF");
        registry.registerDescription(desc);
        factory = new ConnectorSF();
        desc.setStoreFactory(factory);
        desc.getStoreFactory().setStoreAppender(new ConnectorStoreAppender());
        factory.setRegistry(registry);
        registerDescriptor("Listener", LifecycleListener.class);
        connector = new Connector("HTTP/1.1");
    }

    private StoreDescription registerDescriptor(String tag, Class aClass) {
        return registerDescriptor(tag, aClass,
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
    }

    private StoreDescription registerDescriptor(String tag, Class aClass,
            String factoryClass, boolean fstandard, boolean fdefault) {
        return DescriptorHelper.registerDescriptor(desc, registry, aClass
                .getName(), tag, aClass.getName(), factoryClass, fstandard,
                fdefault);
    }

    public void testGetProperties() throws Exception {
        ConnectorStoreAppender appender = new ConnectorStoreAppender();
        List properties = appender.getPropertyKeys(connector);
        assertTrue(properties.contains("emptySessionPath"));
        assertFalse(properties.contains("protocol"));
        assertFalse(properties.contains("protocolHandlerClassName"));
        // HTTP/1.1 SSL Protocol Test
        connector.setProperty("sslProtocol", "TLS");
        properties = appender.getPropertyKeys(connector);
        assertTrue(properties.contains("protocol"));
    }

    public void testPrintAttributes() throws Exception {
        ConnectorStoreAppender appender = new ConnectorStoreAppender();
        connector.setProxyPort(80);
        connector.setProperty("acceptCount", "110");
        appender.printAttributes(pWriter, -2, false, connector, desc);

        String aspectedResult = LF.LINE_SEPARATOR + "  proxyPort=\"80\""
                + LF.LINE_SEPARATOR + "  acceptCount=\"110\"";
        assertEquals(aspectedResult, writer.toString());
    }

}