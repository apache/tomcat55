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

import java.beans.IntrospectionException;
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
public class ConnectorSFTest extends TestCase {
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
        desc.addTransientAttribute("minProcessor");
        desc.addTransientAttribute("maxProcessor");
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

    public void testListener() throws Exception {
        connector
                .addLifecycleListener(new org.apache.catalina.mbeans.ServerLifecycleListener());
        String aspectedResult = "<Connector>"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.mbeans.ServerLifecycleListener\"/>"
                + LF.LINE_SEPARATOR + "</Connector>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testWithProtocolHandler() throws Exception {
        connector.setProperty("acceptCount", "10");
        connector.setProperty("maxSpareThreads", "74");
        String aspectedResult = "<Connector" + LF.LINE_SEPARATOR
                + "    maxSpareThreads=\"74\"" + LF.LINE_SEPARATOR
                + "    acceptCount=\"10\">" + LF.LINE_SEPARATOR
                + "</Connector>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testStoreAJP() throws Exception {
        connector.setProtocol("AJP/1.3");
        String aspectedResult = "<Connector" + LF.LINE_SEPARATOR
                + "    protocol=\"AJP/1.3\">" + LF.LINE_SEPARATOR
                + "</Connector>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testSSL() throws Exception {
        setupSecureConnector();
        String aspectedResult = "<Connector" + LF.LINE_SEPARATOR
                + "    port=\"8443\"" + LF.LINE_SEPARATOR
                + "    scheme=\"https\"" + LF.LINE_SEPARATOR
                + "    secure=\"true\"" + LF.LINE_SEPARATOR
                + "    minSpareThreads=\"30\"" + LF.LINE_SEPARATOR
                + "    clientAuth=\"false\"" + LF.LINE_SEPARATOR
                + "    keystorePass=\"changeit\"" + LF.LINE_SEPARATOR
                + "    keystoreFile=\"conf/catalina.keystore\""
                + LF.LINE_SEPARATOR + "    maxSpareThreads=\"175\""
                + LF.LINE_SEPARATOR + "    sslProtocol=\"TLS\">"
                + LF.LINE_SEPARATOR + "</Connector>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    protected void setupSecureConnector() {
        connector.setPort(8443);
        connector.setProperty("minSpareThreads", "30");
        connector.setProperty("maxSpareThreads", "175");
        connector.setEnableLookups(false);
        connector.setProperty("disableUploadTimeout", "true");
        connector.setSecure(true);
        connector.setProperty("backlog", "100");
        connector.setScheme("https");
        connector.setProperty("clientAuth", "false");
        connector.setProperty("sslProtocol", "TLS");
        connector.setProperty("keystoreFile", "conf/catalina.keystore");
        connector.setProperty("keystorePass", "changeit");
    }

    public void testConnectorAppender() throws IntrospectionException {
        setupSecureConnector();
        ConnectorStoreAppender appender = (ConnectorStoreAppender)desc.getStoreFactory().getStoreAppender();
        List propertyList = appender.getPropertyKeys(connector);
        assertTrue(propertyList.contains("protocol"));   
    }
    
    public void testStoreEmpty() throws Exception {
        String aspectedResult = "<Connector>" + LF.LINE_SEPARATOR
                + "</Connector>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    protected void check(String aspectedResult) throws Exception {
        factory.store(pWriter, -2, connector);
        assertEquals(aspectedResult, writer.toString());
    }

}