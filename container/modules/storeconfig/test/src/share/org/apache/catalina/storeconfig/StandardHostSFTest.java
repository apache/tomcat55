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
import org.apache.catalina.cluster.CatalinaCluster;
import org.apache.catalina.cluster.deploy.FarmWarDeployer;
import org.apache.catalina.cluster.mcast.McastService;
import org.apache.catalina.cluster.session.JvmRouteSessionIDBinderListener;
import org.apache.catalina.cluster.tcp.ReplicationListener;
import org.apache.catalina.cluster.tcp.ReplicationTransmitter;
import org.apache.catalina.cluster.tcp.ReplicationValve;
import org.apache.catalina.cluster.tcp.SimpleTcpCluster;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.realm.JAASRealm;

/**
 * @author Peter Rossbach
 *  
 */
public class StandardHostSFTest extends TestCase {
    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    StandardHost standardHost;

    StandardHostSF factory;

    StoreDescription desc;

    /*
     * create registry and register Host and all subelement descriptors
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        registry = new StoreRegistry();
        desc = new StoreDescription();
        desc.setTag("Host");
        desc.setTagClass("org.apache.catalina.core.StandardHost");
        desc.setStandard(true);
        desc.setStoreFactoryClass("org.apache.catalina.core.StandardHostSF");
        desc.addTransientAttribute("domain");
        registry.registerDescription(desc);
        factory = new StandardHostSF();
        desc.setStoreFactory(factory);
        factory.setRegistry(registry);
        StoreDescription listenerdesc = registerDescriptor("Listener",
                LifecycleListener.class);

        String listenerskippables[] = {
                "org.apache.catalina.core.NamingContextListener",
                "org.apache.catalina.startup.HostConfig", };
        for (int i = 0; i < listenerskippables.length; i++)
            listenerdesc.addTransientChild(listenerskippables[i]);

        StoreDescription realmdesc = registerDescriptor("Realm",
                JAASRealm.class,
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);
        StoreDescription contextdesc = registerDescriptor("Context",
                StandardContext.class,
                "org.apache.catalina.storeconfig.StoreFactoryBase", true, false);
        String exceptions[] = { "available", "configFile", "configured",
                "distributable", "domain", "engineName", "name", "publicId",
                "sessionTimeout", "startupTime", "tldScanTime" };
        for (int i = 0; i < exceptions.length; i++)
            contextdesc.addTransientAttribute(exceptions[i]);

        StoreDescription valvedesc = registerDescriptor("Valve", Valve.class,
                "org.apache.catalina.storeconfig.StoreFactoryBase", false,
                false);

        String skippables[] = { "org.apache.catalina.core.StandardHostValve",
                "org.apache.catalina.valves.CertificatesValve",
                "org.apache.catalina.valves.ErrorReportValve",
                "org.apache.catalina.valves.RequestListenerValve", };
        for (int i = 0; i < skippables.length; i++)
            valvedesc.addTransientChild(skippables[i]);

        DescriptorHelper.registerClusterDescriptor(desc, registry);
        standardHost = new StandardHost();
        standardHost.setName("localhost");

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

    public void testStore() throws Exception {
        standardHost
                .addLifecycleListener(new org.apache.catalina.storeconfig.InfoLifecycleListener());
        String aspectedResult = "<Host"
                + LF.LINE_SEPARATOR
                + "    name=\"localhost\">"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.storeconfig.InfoLifecycleListener\"/>"
                + LF.LINE_SEPARATOR + "</Host>" + LF.LINE_SEPARATOR;

        check(aspectedResult);
    }

    public void testElements() throws Exception {
        standardHost
                .addLifecycleListener(new org.apache.catalina.storeconfig.InfoLifecycleListener());
        standardHost
                .addLifecycleListener(new org.apache.catalina.startup.HostConfig());
        standardHost.setRealm(new JAASRealm());
        StandardContext context = new StandardContext();
        context.setDocBase("myapps");
        context.setPath("/myapps");
        standardHost.addChild(context);
        standardHost.addAlias("jovi");
        String aspectedResult = "<Host"
                + LF.LINE_SEPARATOR
                + "    name=\"localhost\">"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.storeconfig.InfoLifecycleListener\"/>"
                + LF.LINE_SEPARATOR + "  <Alias>jovi</Alias>"
                + LF.LINE_SEPARATOR
                + "  <Realm className=\"org.apache.catalina.realm.JAASRealm\""
                + LF.LINE_SEPARATOR + "    appName=\"localhost\"/>"
                + LF.LINE_SEPARATOR + "  <Context" + LF.LINE_SEPARATOR
                + "    docBase=\"myapps\"" + LF.LINE_SEPARATOR
                + "    path=\"/myapps\"/>" + LF.LINE_SEPARATOR + "</Host>"
                + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testValve() throws Exception {
        standardHost
                .addLifecycleListener(new org.apache.catalina.storeconfig.InfoLifecycleListener());
        standardHost
                .addValve(new org.apache.catalina.valves.ErrorReportValve());
        standardHost
                .addValve(new org.apache.catalina.valves.RequestDumperValve());
        standardHost.addValve(new ReplicationValve());
        String aspectedResult = "<Host"
                + LF.LINE_SEPARATOR
                + "    name=\"localhost\">"
                + LF.LINE_SEPARATOR
                + "  <Listener className=\"org.apache.catalina.storeconfig.InfoLifecycleListener\"/>"
                + LF.LINE_SEPARATOR
                + "  <Valve className=\"org.apache.catalina.valves.RequestDumperValve\"/>"
                + LF.LINE_SEPARATOR + "</Host>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testClusterEmpty() throws Exception {
        CatalinaCluster cluster = new SimpleTcpCluster();
        standardHost.setCluster(cluster);
        String aspectedResult = "<Host"
                + LF.LINE_SEPARATOR
                + "    name=\"localhost\">"
                + LF.LINE_SEPARATOR
                + "  <Cluster className=\"org.apache.catalina.cluster.tcp.SimpleTcpCluster\">"
                + LF.LINE_SEPARATOR + "  </Cluster>" + LF.LINE_SEPARATOR
                + "</Host>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testCluster() throws Exception {
        SimpleTcpCluster cluster = new SimpleTcpCluster();
        cluster.setClusterName("cluster");
        cluster.setProperty("expireSessionsOnShutdown","false");
        cluster
                .setManagerClassName("org.apache.catalina.cluster.session.DeltaManager");
        McastService service = new McastService();
        service.setMcastAddr("228.0.0.4");
        service.setMcastPort(45564);
        service.setMcastFrequency(500l);
        service.setMcastDropTime(3000l);
        cluster.setMembershipService(service);
        ReplicationListener receiver = new ReplicationListener();
        receiver.setTcpListenAddress("auto");
        receiver.setTcpListenPort(4001);
        receiver.setTcpSelectorTimeout(100l);
        receiver.setTcpThreadCount(6);
        cluster.setClusterReceiver(receiver);
        ReplicationTransmitter sender = new ReplicationTransmitter();
        sender.setReplicationMode("pooled");
        cluster.setClusterSender(sender);
        ReplicationValve valve = new ReplicationValve();
        valve
                .setFilter(".*\\.gif;.*\\.js;.*\\.jpg;.*\\.jpeg;.*\\.htm;.*\\.html;.*\\.txt;");
        cluster.addValve(valve);
        FarmWarDeployer deployer = new FarmWarDeployer();
        deployer.setTempDir("/tmp/war-temp/");
        deployer.setDeployDir("/tmp/war-deploy/");
        deployer.setWatchDir("/tmp/war-listen/");
        deployer.setWatchEnabled(false);
        cluster.setClusterDeployer(deployer);
        standardHost.setCluster(cluster);
        cluster.addLifecycleListener(new InfoLifecycleListener());
        cluster.addClusterListener(new JvmRouteSessionIDBinderListener());
        // DeltaManager is default!
        String aspectedResult = "<Host"
                + LF.LINE_SEPARATOR
                + "    name=\"localhost\">"
                + LF.LINE_SEPARATOR
                + "  <Cluster className=\"org.apache.catalina.cluster.tcp.SimpleTcpCluster\""
                + LF.LINE_SEPARATOR
                + "      clusterName=\"cluster\">"
                + LF.LINE_SEPARATOR
                + "    <Membership className=\"org.apache.catalina.cluster.mcast.McastService\""
                + LF.LINE_SEPARATOR
                + "      mcastAddr=\"228.0.0.4\""
                + LF.LINE_SEPARATOR
                + "      mcastDropTime=\"3000\""
                + LF.LINE_SEPARATOR
                + "      mcastFrequency=\"500\""
                + LF.LINE_SEPARATOR
                + "      mcastPort=\"45564\"/>"
                + LF.LINE_SEPARATOR
                + "    <Sender className=\"org.apache.catalina.cluster.tcp.ReplicationTransmitter\""
                + LF.LINE_SEPARATOR
                + "      replicationMode=\"pooled\"/>"
                + LF.LINE_SEPARATOR
                + "    <Receiver className=\"org.apache.catalina.cluster.tcp.ReplicationListener\""
                + LF.LINE_SEPARATOR
                + "      tcpListenAddress=\"auto\""
                + LF.LINE_SEPARATOR
                + "      tcpListenPort=\"4001\""
                + LF.LINE_SEPARATOR
                + "      tcpSelectorTimeout=\"100\""
                + LF.LINE_SEPARATOR
                + "      tcpThreadCount=\"6\"/>"
                + LF.LINE_SEPARATOR
                + "    <Deployer className=\"org.apache.catalina.cluster.deploy.FarmWarDeployer\""
                + LF.LINE_SEPARATOR
                + "      deployDir=\"/tmp/war-deploy/\""
                + LF.LINE_SEPARATOR
                + "      tempDir=\"/tmp/war-temp/\""
                + LF.LINE_SEPARATOR
                + "      watchDir=\"/tmp/war-listen/\"/>"
                + LF.LINE_SEPARATOR
                + "    <Valve className=\"org.apache.catalina.cluster.tcp.ReplicationValve\""
                + LF.LINE_SEPARATOR
                + "      filter=\".*\\.gif;.*\\.js;.*\\.jpg;.*\\.jpeg;.*\\.htm;.*\\.html;.*\\.txt;\"/>"
                + LF.LINE_SEPARATOR
                + "    <Listener className=\"org.apache.catalina.storeconfig.InfoLifecycleListener\"/>"
                + LF.LINE_SEPARATOR
                + "    <ClusterListener className=\"org.apache.catalina.cluster.session.JvmRouteSessionIDBinderListener\"/>"
                + LF.LINE_SEPARATOR + "  </Cluster>" + LF.LINE_SEPARATOR
                + "</Host>" + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testStoreEmpty() throws Exception {
        String aspectedResult = "<Host" + LF.LINE_SEPARATOR
                + "    name=\"localhost\">" + LF.LINE_SEPARATOR + "</Host>"
                + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    protected void check(String aspectedResult) throws Exception {
        factory.store(pWriter, -2, standardHost);
        assertEquals(aspectedResult, writer.toString());
    }

}