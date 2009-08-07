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

import org.apache.catalina.Loader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.storeconfig.LoaderSF;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreRegistry;

/**
 * @author Peter Rossbach
 *  
 */
public class LoaderSFTest extends TestCase {
    StoreRegistry registry;

    StringWriter writer = new StringWriter();

    PrintWriter pWriter = new PrintWriter(writer);

    Loader loader;

    LoaderSF factory;

    StoreDescription desc;

    /*
     * create Registry and register Loader
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        registry = new StoreRegistry();
        desc = DescriptorHelper.registerDescriptor(null, registry,
                WebappLoader.class.getName(), "Loader", WebappLoader.class
                        .getName(), "org.apache.catalina.storeconfig.LoaderSF",
                false, false);
        factory = (LoaderSF) desc.getStoreFactory();
        loader = new WebappLoader();

    }

    public void testManagerNonStandardStore() throws Exception {
        assertTrue(factory.isDefaultLoader(loader));
        loader.setDelegate(true);
        assertFalse(factory.isDefaultLoader(loader));
        String aspectedResult = "<Loader className=\"org.apache.catalina.loader.WebappLoader\""
                + LF.LINE_SEPARATOR
                + "    delegate=\"true\"/>"
                + LF.LINE_SEPARATOR;
        check(aspectedResult);
    }

    public void testStoreEmpty() throws Exception {
        assertTrue(factory.isDefaultLoader(loader));
        String aspectedResult = "";
        check(aspectedResult);
    }

    protected void check(String aspectedResult) throws Exception {
        factory.store(pWriter, -2, loader);
        assertEquals(aspectedResult, writer.toString());
    }

}