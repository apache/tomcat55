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

import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardManager;

/**
 * @author Peter Rossbach
 *  
 */
public class ManagerSFTest extends TestCase {
    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    StandardManager manager;

    ManagerSF factory;

    StoreDescription desc;

    /*
     * create registery and register Manager descriptor
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        registry = new StoreRegistry();
        desc = DescriptorHelper.registerDescriptor(null, registry,
                Manager.class.getName(), "Manager",
                Manager.class.getName(),
                "org.apache.catalina.storeconfig.ManagerSF", false, false);
        desc.addTransientAttribute("entropy");
        desc.addTransientAttribute("distributable");
        factory = (ManagerSF) desc.getStoreFactory();
        manager = new StandardManager();

    }

    public void testFindStandardManager() {
        StoreDescription managerdesc = registry.findDescription(manager.getClass());
        assertEquals(desc,managerdesc);
    }
    
    public void testManagerNonStandardStore() throws Exception {
        assertTrue(factory.isDefaultManager(manager));
        manager.setMaxActiveSessions(100);
        assertFalse(factory.isDefaultManager(manager));
        String aspectedResult = "<Manager className=\"org.apache.catalina.session.StandardManager\""
                + LF.LINE_SEPARATOR
                + "    maxActiveSessions=\"100\"/>"
                + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testStoreEmpty() throws Exception {
        assertTrue(factory.isDefaultManager(manager));
        String aspectedResult = "";
        check(aspectedResult);
    }

    protected void check(String aspectedResult) throws Exception {
        factory.store(pWriter, -2, manager);
        assertEquals(aspectedResult, writer.toString());
    }

}