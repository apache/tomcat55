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

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardService;

/**
 * @author Peter Rossbach
 *  
 */
public class StandardServiceSFTest extends TestCase {
    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    StandardService standardService = new StandardService();

    StandardServiceSF factory;

    StoreDescription desc;

    /*
     * create registry and register Service and all direct subelement
     * descriptors
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        registry = new StoreRegistry();
        desc = new StoreDescription();
        desc.setTag("Service");
        desc.setTagClass("org.apache.catalina.core.StandardService");
        desc.setStandard(true);
        desc.setStoreFactoryClass("org.apache.catalina.core.StandardServiceSF");
        registry.registerDescription(desc);
        factory = new StandardServiceSF();
        desc.setStoreFactory(factory);
        factory.setRegistry(registry);
        registerDescriptor("Listener", LifecycleListener.class);
        registerDescriptor("Engine", StandardEngine.class,
                "org.apache.catalina.storeconfig.StoreFactoryBase", true, false);
        StoreDescription cdesc = registerDescriptor("Connector",
                Connector.class, "org.apache.catalina.storeconfig.ConnectorSF",
                true, true);
        cdesc.getStoreFactory().setStoreAppender(new ConnectorStoreAppender());
        StoreDescription pdesc = DescriptorHelper
                .registerDescriptor(null, registry, Connector.class.getName()
                        + ".[ProtocolHandler]", "ProtocolHandler",
                        Connector.class.getName(),
                        "org.apache.catalina.storeconfig.StoreFactoryBase",
                        true, false);
        pdesc.addTransientAttribute("keystore");
        pdesc.addTransientAttribute("keypass");
        pdesc.addTransientAttribute("keytype");
        pdesc.addTransientAttribute("randomfile");
        pdesc.addTransientAttribute("protocols");
        pdesc.addTransientAttribute("clientauth");
        pdesc.addTransientAttribute("protocol");
        pdesc.addTransientAttribute("port");
        pdesc.addTransientAttribute("secure");

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

    public void testStoreAJP() throws Exception {
        standardService
                .addLifecycleListener(new org.apache.catalina.mbeans.ServerLifecycleListener());
        Connector connector = new Connector();
        standardService.addConnector(connector);
        standardService.setContainer(new StandardEngine());
        String aspectedResult = "<Service>"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.mbeans.ServerLifecycleListener\"/>"
                + LF.LINE_SEPARATOR + "  <Connector/>" + LF.LINE_SEPARATOR
                + "  <Engine/>" + LF.LINE_SEPARATOR + "</Service>"
                + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testStoreEmpty() throws Exception {
        String aspectedResult = "<Service>" + LF.LINE_SEPARATOR + "</Service>"
                + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    protected void check(String aspectedResult) throws Exception {
        factory.store(pWriter, -2, standardService);
        assertEquals(aspectedResult, writer.toString());
    }

}