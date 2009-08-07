/*
 * Copyright 1999,2004 The Apache Software Foundation.
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


package org.apache.naming.resources;

import java.io.File;
import java.io.IOException;

import java.util.Date;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.naming.NamingException;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit tests for <code>org.apache.naming.resources.WARDirContext</code>.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class WARDirContextTestCase extends BaseDirContextTestCase {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public WARDirContextTestCase(String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.  This method
     * <strong>MUST</strong> be implemented by a subclass.
     */
    public void setUp() {

        context = new WARDirContext();
        ((WARDirContext) context).setDocBase(docBase);

    }


    /**
     * Return the tests included in this test suite.  This method
     * <strong>MUST</strong> be implemented by a subclass.
     */
    public static Test suite() {

        return (new TestSuite(WARDirContextTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.  This method
     * <strong>MUST</strong> be implemented by a subclass.
     */
    public void tearDown() {

        context = null;

    }


    // ------------------------------------------------ Individual Test Methods


    /**
     * Test the attributes returned for the <code>WEB-INF</code> entry.
     */
    public void testGetAttributesWebInf() {

        try {

            // Look up the attributes of this WAR file entry
            JarFile jarFile = new JarFile(docBase);
            assertNotNull("Created JarFile for " + docBase, jarFile);
            JarEntry jarEntry =
                (JarEntry) jarFile.getEntry("WEB-INF");
            assertNotNull("Created JarEntry for WEB-INF", jarEntry);

            // Look up the attributes for the WEB-INF entry
            Attributes attributes = context.getAttributes("WEB-INF");

            // Enumerate and check the attributes for this entry
            checkWebInfAttributes(attributes,
                                  new Date(jarEntry.getTime()),
                                  jarEntry.getSize(),
                                  "WEB-INF",
                                  new Date(jarEntry.getTime()));

        } catch (IOException e) {

            fail("IOException: " + e);

        } catch (NamingException e) {

            fail("NamingException: " + e);

        }

    }


    /**
     * Test the attributes returned for the <code>WEB-INF/web.xml</code>
     * entry.
     */
    public void testGetAttributesWebXml() {

        try {

            // Look up the attributes of this WAR file entry
            JarFile jarFile = new JarFile(docBase);
            assertNotNull("Created JarFile for " + docBase, jarFile);
            JarEntry jarEntry =
                (JarEntry) jarFile.getEntry("WEB-INF/web.xml");
            assertNotNull("Created JarEntry for WEB-INF/web.xml", jarEntry);

            // Look up the attributes for the WEB-INF/web.xml entry
            Attributes attributes = context.getAttributes("WEB-INF/web.xml");

            // Enumerate and check the attributes for this entry
            checkWebXmlAttributes(attributes,
                                  new Date(jarEntry.getTime()),
                                  jarEntry.getSize(),
                                  "web.xml",
                                  new Date(jarEntry.getTime()));

        } catch (IOException e) {

            fail("IOException: " + e);

        } catch (NamingException e) {

            fail("NamingException:  " + e);

        }

    }


}
