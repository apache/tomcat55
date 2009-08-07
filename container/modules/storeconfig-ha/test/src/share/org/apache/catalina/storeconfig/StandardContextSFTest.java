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

import javax.naming.directory.DirContext;

import junit.framework.TestCase;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.deploy.ContextResourceEnvRef;
import org.apache.catalina.deploy.ContextResourceLink;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.realm.JAASRealm;
import org.apache.catalina.session.FileStore;
import org.apache.catalina.session.JDBCStore;
import org.apache.catalina.session.PersistentManager;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.storeconfig.StandardContextSF;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreRegistry;
import org.apache.naming.resources.FileDirContext;
import org.apache.naming.resources.ProxyDirContext;
import org.apache.naming.resources.WARDirContext;

/**
 * @author Peter Rossbach
 *  
 */
public class StandardContextSFTest extends TestCase {
    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    StandardContext standardContext;

    StandardContextSF factory;

    StoreDescription desc;

    /*
     * create registery and register Context and all subelements
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        registry = new StoreRegistry();
        desc = new StoreDescription();
        desc.setTag("Context");
        desc.setTagClass("org.apache.catalina.core.StandardContext");
        desc.setStandard(true);
        desc
                .setStoreFactoryClass("org.apache.catalina.storeconfig.StandardContextSF");
        String exceptions[] = { "available", "configFile", "configured",
                "distributable", "domain", "engineName", "name", "override",
                "publicId", "replaceWelcomeFiles", "sessionTimeout",
                "startupTime", "tldScanTime" };
        for (int i = 0; i < exceptions.length; i++)
            desc.addTransientAttribute(exceptions[i]);

        registry.registerDescription(desc);
        factory = new StandardContextSF();
        desc.setStoreFactory(factory);
        factory.setRegistry(registry);

        StoreDescription listenerdesc = registerDescriptor("Listener",
                LifecycleListener.class);

        String listenerskippables[] = {
                "org.apache.catalina.core.NamingContextListener",
                "org.apache.catalina.startup.ContextConfig", };
        for (int i = 0; i < listenerskippables.length; i++)
            listenerdesc.addTransientChild(listenerskippables[i]);

        StoreDescription realmdesc = registerDescriptor("Realm",
                JAASRealm.class,
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        StoreDescription managerdesc = registerDescriptor("Manager",
                StandardManager.class,
                "org.apache.catalina.storeconfig.ManagerSF", false, false);
        managerdesc.addTransientAttribute("entropy");
        managerdesc.addTransientAttribute("distributable");
        StoreDescription pmanagerdesc = registerDescriptor("Manager",
                PersistentManager.class,
                "org.apache.catalina.storeconfig.PersistentManagerSF", false,
                false);
        pmanagerdesc.addTransientAttribute("entropy");
        pmanagerdesc.addTransientAttribute("distributable");
        DescriptorHelper.registerDescriptor(pmanagerdesc, registry,
                FileStore.class.getName(), "Store", FileStore.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        DescriptorHelper.registerDescriptor(pmanagerdesc, registry,
                JDBCStore.class.getName(), "Store", JDBCStore.class.getName(),
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        DescriptorHelper.registerNamingDescriptor(desc, registry);
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
                "org.apache.catalina.valves.CertificatesValve" };
        for (int i = 0; i < skippables.length; i++)
            valvedesc.addTransientChild(skippables[i]);

        StoreDescription resdesc = registerDescriptor("Resources",
                DirContext.class,
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        resdesc.addTransientAttribute("docBase");
        resdesc.addTransientAttribute("allowLinking");
        resdesc.addTransientAttribute("cacheMaxSize");
        resdesc.addTransientAttribute("cacheTTL");
        resdesc.addTransientAttribute("caseSensitive");
        resdesc.addTransientChild(FileDirContext.class.getName());
        resdesc.addTransientChild(ProxyDirContext.class.getName());
        resdesc.addTransientChild(WARDirContext.class.getName());
        standardContext = new StandardContext();
        standardContext.setPath("/myapps");
        standardContext.setDocBase("myapps");

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

    /**
     * @TODO Listener only saved at real context. Wrong when next Context
     *       deployed! see Changes.txt
     * @throws Exception
     */
    public void testListenerStore() throws Exception {
        standardContext
                .addLifecycleListener(new org.apache.catalina.storeconfig.InfoLifecycleListener());
        standardContext
                .addInstanceListener("org.apache.catalina.ContainerListener");
        standardContext
                .addWrapperListener("org.apache.catalina.ContainerListener");
        standardContext
                .addWrapperLifecycle("org.apache.catalina.ContainerListener");
        standardContext.addWatchedResource("/tmp/reloaded");
        String aspectedResult = "<Context"
                + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\""
                + LF.LINE_SEPARATOR
                + "    path=\"/myapps\">"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.storeconfig.InfoLifecycleListener\"/>"
                + LF.LINE_SEPARATOR
                + "  <InstanceListener>org.apache.catalina.ContainerListener</InstanceListener>"
                + LF.LINE_SEPARATOR
                + "  <WrapperListener>org.apache.catalina.ContainerListener</WrapperListener>"
                + LF.LINE_SEPARATOR
                + "  <WrapperLifecycle>org.apache.catalina.ContainerListener</WrapperLifecycle>"
                + LF.LINE_SEPARATOR
                + "  <WatchedResource>/tmp/reloaded</WatchedResource>"
                + LF.LINE_SEPARATOR + "</Context>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testNamingStore() throws Exception {
        standardContext
                .addLifecycleListener(new org.apache.catalina.storeconfig.InfoLifecycleListener());
        NamingResources resources = standardContext.getNamingResources();
        ContextResourceEnvRef ref = new ContextResourceEnvRef();
        ref.setName("foo");
        ref.setType("type");
        resources.addResourceEnvRef(ref);
        ContextResourceLink res = new ContextResourceLink();
        res.setName("jdbc/Barlocal");
        res.setType("javax.sql.DataSource");
        res.setGlobal("jdbc/Bar");
        resources.addResourceLink(res);
        String aspectedResult = "<Context"
                + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\""
                + LF.LINE_SEPARATOR
                + "    path=\"/myapps\">"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.storeconfig.InfoLifecycleListener\"/>"
                + LF.LINE_SEPARATOR + "  <ResourceEnvRef" + LF.LINE_SEPARATOR
                + "    name=\"foo\"" + LF.LINE_SEPARATOR
                + "    type=\"type\"/>" + LF.LINE_SEPARATOR + "  <ResourceLink"
                + LF.LINE_SEPARATOR + "    global=\"jdbc/Bar\""
                + LF.LINE_SEPARATOR + "    name=\"jdbc/Barlocal\""
                + LF.LINE_SEPARATOR + "    type=\"javax.sql.DataSource\"/>"
                + LF.LINE_SEPARATOR + "</Context>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testManagerStore() throws Exception {
        standardContext.setManager(new StandardManager());
        String aspectedResult = "<Context" + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\"" + LF.LINE_SEPARATOR
                + "    path=\"/myapps\">" + LF.LINE_SEPARATOR
                + "</Context>\r\n";
        check(aspectedResult);
    }

    public void testRealmStore() throws Exception {
        standardContext.setManager(new StandardManager());
        JAASRealm realm = new JAASRealm();
        standardContext.setRealm(realm);
        String aspectedResult = "<Context" + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\"" + LF.LINE_SEPARATOR
                + "    path=\"/myapps\">" + LF.LINE_SEPARATOR
                + "  <Realm className=\"org.apache.catalina.realm.JAASRealm\""
                + LF.LINE_SEPARATOR + "    appName=\"myapps\"/>"
                + LF.LINE_SEPARATOR + "</Context>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    // @TODO Why the MaxInactiveInterval is after setManager set to 1800 sec?
    public void testManagerNonStandardStore() throws Exception {
        StandardManager manager = new StandardManager();
        manager.setMaxActiveSessions(100);
        assertEquals(60, manager.getMaxInactiveInterval());
        standardContext.setManager(manager);
        assertEquals(1800, manager.getMaxInactiveInterval());
        String aspectedResult = "<Context"
                + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\""
                + LF.LINE_SEPARATOR
                + "    path=\"/myapps\">"
                + LF.LINE_SEPARATOR
                + "  <Manager className=\"org.apache.catalina.session.StandardManager\""
                + LF.LINE_SEPARATOR + "      maxActiveSessions=\"100\""
                + LF.LINE_SEPARATOR + "      maxInactiveInterval=\"1800\"/>"
                + LF.LINE_SEPARATOR + "</Context>\r\n";
        check(aspectedResult);
    }

    public void testPersistentManagerStore() throws Exception {
        PersistentManager manager = new PersistentManager();
        manager.setSaveOnRestart(false);
        standardContext.setManager(manager);
        String aspectedResult = "<Context"
                + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\""
                + LF.LINE_SEPARATOR
                + "    path=\"/myapps\">"
                + LF.LINE_SEPARATOR
                + "  <Manager className=\"org.apache.catalina.session.PersistentManager\""
                + LF.LINE_SEPARATOR + "      maxInactiveInterval=\"1800\""
                + LF.LINE_SEPARATOR + "      saveOnRestart=\"false\">"
                + LF.LINE_SEPARATOR + "  </Manager>" + LF.LINE_SEPARATOR
                + "</Context>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testPersistentManagerFileStore() throws Exception {
        PersistentManager manager = new PersistentManager();
        manager.setSaveOnRestart(false);
        FileStore store = new FileStore();
        manager.setStore(store);
        standardContext.setManager(manager);
        String aspectedResult = "<Context"
                + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\""
                + LF.LINE_SEPARATOR
                + "    path=\"/myapps\">"
                + LF.LINE_SEPARATOR
                + "  <Manager className=\"org.apache.catalina.session.PersistentManager\""
                + LF.LINE_SEPARATOR
                + "      maxInactiveInterval=\"1800\""
                + LF.LINE_SEPARATOR
                + "      saveOnRestart=\"false\">"
                + LF.LINE_SEPARATOR
                + "    <Store className=\"org.apache.catalina.session.FileStore\"/>"
                + LF.LINE_SEPARATOR + "  </Manager>" + LF.LINE_SEPARATOR
                + "</Context>" + LF.LINE_SEPARATOR;
        check(aspectedResult);

    }

    public void testDefaultResources() throws Exception {
        FileDirContext dirC = new FileDirContext();
        standardContext.setAllowLinking(true);
        standardContext.setResources(dirC);
        StandardHost host = new StandardHost();
        host.addChild(standardContext);
        host.setName("localhost");
        host.setAppBase("webapps");
        standardContext.resourcesStart();
        assertNotNull(standardContext.getResources());
        String aspectedResult = "<Context" + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\"" + LF.LINE_SEPARATOR
                + "    path=\"/myapps\">" + LF.LINE_SEPARATOR + "</Context>"
                + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    /*
     * + " <Resources className=\"org.apache.naming.resources.FileDirContext\"" +
     * Constants.LINE_SEPARATOR + " allowLinking=\"true\"/>" +
     * Constants.LINE_SEPARATOR
     */
    public void testStoreEmpty() throws Exception {
        String aspectedResult = "<Context" + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\"" + LF.LINE_SEPARATOR
                + "    path=\"/myapps\">" + LF.LINE_SEPARATOR + "</Context>"
                + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    protected void check(String aspectedResult) throws Exception {
        factory.store(pWriter, -2, standardContext);
        assertEquals(aspectedResult, writer.toString());
    }

}