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
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.realm.JAASRealm;
import org.apache.catalina.storeconfig.StandardEngineSF;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreRegistry;

/**
 * @author Peter Rossbach
 *  
 */
public class StandardEngineSFTest extends TestCase {
    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    StandardEngine standardEngine;

    StandardEngineSF factory;

    StoreDescription desc;

    /*
     * create registery and register Engine and direct subelement descriptors
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        registry = new StoreRegistry();
        desc = new StoreDescription();
        desc.setTag("Engine");
        desc.setTagClass("org.apache.catalina.core.StandardEngine");
        desc.setStandard(true);
        desc
                .setStoreFactoryClass("org.apache.catalina.storeconfig.StandardEngineSF");
        desc.addTransientAttribute("domain");
        registry.registerDescription(desc);
        factory = new StandardEngineSF();
        desc.setStoreFactory(factory);
        factory.setRegistry(registry);
        StoreDescription listenerdesc = registerDescriptor("Listener",
                LifecycleListener.class);

        String listenerskippables[] = {
                "org.apache.catalina.core.NamingContextListener",
                "org.apache.catalina.startup.ContextConfig",
                "org.apache.catalina.startup.EngineConfig",
                "org.apache.catalina.startup.HostConfig", };
        for (int i = 0; i < listenerskippables.length; i++)
            listenerdesc.addTransientChild(listenerskippables[i]);

        StoreDescription realmdesc = registerDescriptor("Realm",
                JAASRealm.class,
                "org.apache.catalina.storeconfig.StoreFactoryBase", true, false);
        StoreDescription hostdesc = registerDescriptor("Host",
                StandardHost.class,
                "org.apache.catalina.storeconfig.StoreFactoryBase", true, false);
        hostdesc.addTransientAttribute("domain");
        StoreDescription valvedesc = registerDescriptor("Valve", Valve.class,
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);

        String skippables[] = {
                "org.apache.catalina.authenticator.BasicAuthenticator",
                "org.apache.catalina.authenticator.DigestAuthenticator",
                "org.apache.catalina.authenticator.FormAuthenticator",
                "org.apache.catalina.authenticator.NonLoginAuthenticator",
                "org.apache.catalina.authenticator.SSLAuthenticator",
                "org.apache.catalina.core.StandardContextValve",
                "org.apache.catalina.core.StandardEngineValve",
                "org.apache.catalina.core.StandardHostValve",
                "org.apache.catalina.valves.CertificatesValve",
                "org.apache.catalina.valves.ErrorReportValve",
                "org.apache.catalina.valves.RequestListenerValve", };
        for (int i = 0; i < skippables.length; i++)
            valvedesc.addTransientChild(skippables[i]);

        standardEngine = new StandardEngine();
        standardEngine.setName("Catalina");

    }

    private StoreDescription registerDescriptor(String tag, Class aClass) {
        return registerDescriptor(tag, aClass,
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
    }

    /**
     *  
     */
    private StoreDescription registerDescriptor(String tag, Class aClass,
            String factoryClass, boolean fstandard, boolean fdefault) {
        return DescriptorHelper.registerDescriptor(desc, registry, aClass
                .getName(), tag, aClass.getName(), factoryClass, fstandard,
                fdefault);
    }

    public void testStore() throws Exception {
        standardEngine
                .addLifecycleListener(new org.apache.catalina.storeconfig.InfoLifecycleListener());
        StandardHost host = new StandardHost();
        host.setName("localhost");
        standardEngine.addChild(host);
        String aspectedResult = "<Engine"
                + LF.LINE_SEPARATOR
                + "    name=\"Catalina\">"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.storeconfig.InfoLifecycleListener\"/>"
                + LF.LINE_SEPARATOR + "  <Realm" + LF.LINE_SEPARATOR
                + "    appName=\"Catalina\"/>" + LF.LINE_SEPARATOR + "  <Host"
                + LF.LINE_SEPARATOR + "    name=\"localhost\"/>"
                + LF.LINE_SEPARATOR + "</Engine>" + LF.LINE_SEPARATOR;

        check(aspectedResult);
    }

    public void testElements() throws Exception {
        standardEngine.setName("Catalina");
        standardEngine
                .addLifecycleListener(new org.apache.catalina.storeconfig.InfoLifecycleListener());
        standardEngine
                .addLifecycleListener(new org.apache.catalina.startup.EngineConfig());
        StandardHost host = new StandardHost();
        host.setName("localhost");
        standardEngine.addChild(host);
        String aspectedResult = "<Engine"
                + LF.LINE_SEPARATOR
                + "    name=\"Catalina\">"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.storeconfig.InfoLifecycleListener\"/>"
                + LF.LINE_SEPARATOR + "  <Realm" + LF.LINE_SEPARATOR
                + "    appName=\"Catalina\"/>" + LF.LINE_SEPARATOR + "  <Host"
                + LF.LINE_SEPARATOR + "    name=\"localhost\"/>"
                + LF.LINE_SEPARATOR + "</Engine>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testValve() throws Exception {
        standardEngine.setName("Catalina");
        standardEngine
                .addLifecycleListener(new org.apache.catalina.storeconfig.InfoLifecycleListener());
        standardEngine
                .addLifecycleListener(new org.apache.catalina.startup.EngineConfig());
        StandardHost host = new StandardHost();
        host.setName("localhost");
        standardEngine.addChild(host);
        standardEngine
                .addValve(new org.apache.catalina.valves.ErrorReportValve());
        standardEngine
                .addValve(new org.apache.catalina.valves.RequestDumperValve());
        String aspectedResult = "<Engine"
                + LF.LINE_SEPARATOR
                + "    name=\"Catalina\">"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.storeconfig.InfoLifecycleListener\"/>"
                + LF.LINE_SEPARATOR
                + "  <Realm"
                + LF.LINE_SEPARATOR
                + "    appName=\"Catalina\"/>"
                + LF.LINE_SEPARATOR
                + "  <Valve className=\"org.apache.catalina.valves.RequestDumperValve\"/>"
                + LF.LINE_SEPARATOR + "  <Host" + LF.LINE_SEPARATOR
                + "    name=\"localhost\"/>" + LF.LINE_SEPARATOR + "</Engine>"
                + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testStoreEmpty() throws Exception {
        String aspectedResult = "<Engine" + LF.LINE_SEPARATOR
                + "    name=\"Catalina\">" + LF.LINE_SEPARATOR + "  <Realm"
                + LF.LINE_SEPARATOR + "    appName=\"Catalina\"/>"
                + LF.LINE_SEPARATOR + "</Engine>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    protected void check(String aspectedResult) throws Exception {
        factory.store(pWriter, -2, standardEngine);
        assertEquals(aspectedResult, writer.toString());
    }

}