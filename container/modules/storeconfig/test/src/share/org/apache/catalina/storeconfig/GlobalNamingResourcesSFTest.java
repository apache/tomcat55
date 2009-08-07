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

import org.apache.catalina.deploy.ContextEjb;
import org.apache.catalina.deploy.ContextEnvironment;
import org.apache.catalina.deploy.ContextLocalEjb;
import org.apache.catalina.deploy.ContextResource;
import org.apache.catalina.deploy.ContextResourceEnvRef;
import org.apache.catalina.deploy.ContextResourceLink;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.storeconfig.GlobalNamingResourcesSF;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreRegistry;

/**
 * @author Peter Rossbach
 *  
 */
public class GlobalNamingResourcesSFTest extends TestCase {
    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    NamingResources reource = new NamingResources();

    GlobalNamingResourcesSF factory;

    StoreDescription desc;

    StoreDescription nameingDesc;

    /*
     * create registery and configure naming decriptors
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        registry = new StoreRegistry();
        desc = DescriptorHelper.registerDescriptor(null, registry,
                NamingResources.class.getName() + ".[GlobalNamingResources]",
                "GlobalNamingResources", NamingResources.class.getName(),
                "org.apache.catalina.storeconfig.GlobalNamingResourcesSF",
                true, false);
        factory = (GlobalNamingResourcesSF) desc.getStoreFactory();
        nameingDesc = DescriptorHelper.registerNamingDescriptor(desc, registry);
        super.setUp();
    }

    protected void registerDescriptor(String tag, Class aClass,
            String factoryClass, boolean fstandard, boolean fdefault) {
        DescriptorHelper.registerDescriptor(nameingDesc, registry, aClass
                .getName(), tag, aClass.getName(), factoryClass, fstandard,
                fdefault);
    }

    public void testStore() throws Exception {
        assertNotNull(registry.findDescription(NamingResources.class));
        assertNotNull(registry.findDescription(ContextResourceEnvRef.class
                .getName()));
        assertEquals("ResourceEnvRef", registry.findDescription(
                ContextResourceEnvRef.class.getName()).getTag());

        NamingResources resources = new NamingResources();
        ContextResourceEnvRef ref = new ContextResourceEnvRef();
        ref.setName("peter");
        ref.setType("type");
        resources.addResourceEnvRef(ref);
        String aspectedResult = "<GlobalNamingResources>" + LF.LINE_SEPARATOR
                + "  <ResourceEnvRef" + LF.LINE_SEPARATOR
                + "    name=\"peter\"" + LF.LINE_SEPARATOR
                + "    type=\"type\"/>" + LF.LINE_SEPARATOR
                + "</GlobalNamingResources>" + LF.LINE_SEPARATOR;
        check(resources, aspectedResult);

    }

    public void testEJBStore() throws Exception {

        NamingResources resources = new NamingResources();
        ContextEjb ejb = new ContextEjb();
        ejb.setName("ejb/Service");
        ejb.setType("org.super.Bean");
        ejb.setHome("org.super.BeanHome");
        resources.addEjb(ejb);
        String aspectedResult = "<GlobalNamingResources>" + LF.LINE_SEPARATOR
                + "  <EJB" + LF.LINE_SEPARATOR
                + "    home=\"org.super.BeanHome\"" + LF.LINE_SEPARATOR
                + "    name=\"ejb/Service\"" + LF.LINE_SEPARATOR
                + "    type=\"org.super.Bean\"/>" + LF.LINE_SEPARATOR
                + "</GlobalNamingResources>" + LF.LINE_SEPARATOR;
        check(resources, aspectedResult);
    }

    public void testLocalEjbStore() throws Exception {

        NamingResources resources = new NamingResources();
        ContextLocalEjb ejb = new ContextLocalEjb();
        ejb.setName("ejb/Service");
        ejb.setType("org.super.Bean");
        ejb.setHome("org.super.BeanHome");
        resources.addLocalEjb(ejb);
        String aspectedResult = "<GlobalNamingResources>" + LF.LINE_SEPARATOR
                + "  <LocalEjb" + LF.LINE_SEPARATOR
                + "    home=\"org.super.BeanHome\"" + LF.LINE_SEPARATOR
                + "    name=\"ejb/Service\"" + LF.LINE_SEPARATOR
                + "    type=\"org.super.Bean\"/>" + LF.LINE_SEPARATOR
                + "</GlobalNamingResources>" + LF.LINE_SEPARATOR;
        check(resources, aspectedResult);
    }

    public void testEnvironmentStore() throws Exception {

        NamingResources resources = new NamingResources();
        ContextEnvironment env = new ContextEnvironment();
        env.setName("env/SelectEmp");
        env.setType("java.lang.String");
        env.setValue("select * from emp");
        resources.addEnvironment(env);
        String aspectedResult = "<GlobalNamingResources>" + LF.LINE_SEPARATOR
                + "  <Environment" + LF.LINE_SEPARATOR
                + "    name=\"env/SelectEmp\"" + LF.LINE_SEPARATOR
                + "    type=\"java.lang.String\"" + LF.LINE_SEPARATOR
                + "    value=\"select * from emp\"/>" + LF.LINE_SEPARATOR
                + "</GlobalNamingResources>" + LF.LINE_SEPARATOR;
        check(resources, aspectedResult);
    }

    public void testResourceStore() throws Exception {

        NamingResources resources = new NamingResources();
        ContextResource res = new ContextResource();
        res.setName("jdbc/Emp");
        res.setType("javax.sql.DataSource");
        res.setAuth("Container");
        resources.addResource(res);
        String aspectedResult = "<GlobalNamingResources>" + LF.LINE_SEPARATOR
                + "  <Resource" + LF.LINE_SEPARATOR + "    auth=\"Container\""
                + LF.LINE_SEPARATOR + "    name=\"jdbc/Emp\""
                + LF.LINE_SEPARATOR + "    type=\"javax.sql.DataSource\"/>"
                + LF.LINE_SEPARATOR + "</GlobalNamingResources>"
                + LF.LINE_SEPARATOR;
        check(resources, aspectedResult);
    }

    public void testResourceStoreProperty() throws Exception {

        NamingResources resources = new NamingResources();
        ContextResource res = new ContextResource();
        res.setName("mail/MailSession");
        res.setType("javax.mail.Session");
        res.setAuth("Container");
        res.setProperty("mail.host", "localhost");
        resources.addResource(res);
        String aspectedResult = "<GlobalNamingResources>" + LF.LINE_SEPARATOR
                + "  <Resource" + LF.LINE_SEPARATOR + "    auth=\"Container\""
                + LF.LINE_SEPARATOR + "    name=\"mail/MailSession\""
                + LF.LINE_SEPARATOR + "    type=\"javax.mail.Session\""
                + LF.LINE_SEPARATOR + "    mail.host=\"localhost\"/>"
                + LF.LINE_SEPARATOR + "</GlobalNamingResources>"
                + LF.LINE_SEPARATOR;
        check(resources, aspectedResult);
    }

    // @TODO ResourceLink can only be exists at Context Tag
    public void testResourceLinkStore() throws Exception {

        NamingResources resources = new NamingResources();
        ContextResourceLink res = new ContextResourceLink();
        res.setName("jdbc/Emp1");
        res.setType("javax.sql.DataSource");
        res.setGlobal("jdbc/Emp");
        resources.addResourceLink(res);
        String aspectedResult = "<GlobalNamingResources>" + LF.LINE_SEPARATOR
                + "  <ResourceLink" + LF.LINE_SEPARATOR
                + "    global=\"jdbc/Emp\"" + LF.LINE_SEPARATOR
                + "    name=\"jdbc/Emp1\"" + LF.LINE_SEPARATOR
                + "    type=\"javax.sql.DataSource\"/>" + LF.LINE_SEPARATOR
                + "</GlobalNamingResources>" + LF.LINE_SEPARATOR;
        check(resources, aspectedResult);
    }

    public void testStoreEmpty() throws Exception {
        NamingResources resources = new NamingResources();
        String aspectedResult = "<GlobalNamingResources>" + LF.LINE_SEPARATOR
                + "</GlobalNamingResources>" + LF.LINE_SEPARATOR;
        check(resources, aspectedResult);
    }

    protected void check(NamingResources resources, String aspectedResult)
            throws Exception {
        factory.store(pWriter, -2, resources);
        assertEquals(aspectedResult, writer.toString());
    }

}