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

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.storeconfig.StandardServerSF;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreRegistry;

/**
 * @author Peter Rossbach
 *  
 */
public class ServerChildsTest extends TestCase {

    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    StandardServer standardServer = new StandardServer();

    StandardServerSF factory;

    StoreDescription desc;

    /*
     * create registery and register Server and direct subelement descriptors
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
                .setStoreFactoryClass("org.apache.catalina.storeconfig.StandardServerSF");
        registry.registerDescription(desc);
        factory = new StandardServerSF();
        desc.setStoreFactory(factory);
        factory.setRegistry(registry);
        StoreDescription listdesc = registerDescriptor("Listener",
                LifecycleListener.class);
        listdesc
                .addTransientChild("org.apache.catalina.core.NamingContextListener");
        listdesc
                .addTransientChild("org.apache.catalina.mbeans.ServerLifecycleListener");
        standardServer
                .addLifecycleListener(new org.apache.catalina.mbeans.ServerLifecycleListener());
        // add GlobalNamingResource
        DescriptorHelper.registerDescriptor(desc, registry,
                NamingResources.class.getName() + ".[GlobalNamingResources]",
                "GlobalNamingResources", NamingResources.class.getName(),
                "org.apache.catalina.storeconfig.GlobalNamingResourcesSF",
                true, false);
        DescriptorHelper.registerNamingDescriptor(desc, registry);
        registerDescriptor("Service", StandardService.class,
                "org.apache.catalina.storeconfig.StandardServiceSF", true,
                false);
        DescriptorHelper.registerDescriptor(desc, registry,
                StandardServer.class.getName() + ".[ServerLifecycleListener]",
                "ServerLifecycleListener", StandardServer.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        standardServer.addService(new StandardService());

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

    public void testSaveListenerAddServer() throws Exception {
        assertTrue(standardServer instanceof Lifecycle);
        assertNotNull(
                "No Listener Descriptor",
                registry
                        .findDescription("org.apache.catalina.mbeans.ServerLifecycleListener"));
        assertNotNull(
                "No Listener StoreFactory",
                registry
                        .findStoreFactory("org.apache.catalina.mbeans.ServerLifecycleListener"));
        factory.store(pWriter, -2, standardServer);
 
        String aspectedResult = "<?xml version=\"1.0\" encoding=\""
                + registry.getEncoding()
                + "\"?>"
                + LF.LINE_SEPARATOR
                + "<Server>"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.mbeans.ServerLifecycleListener\"/>"
                + LF.LINE_SEPARATOR + "  <GlobalNamingResources>"
                + LF.LINE_SEPARATOR + "  </GlobalNamingResources>"
                + LF.LINE_SEPARATOR + "  <Service/>" + LF.LINE_SEPARATOR
                + "</Server>" + LF.LINE_SEPARATOR;
        assertEquals(aspectedResult, writer.toString());
    }
}