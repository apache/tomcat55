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


package org.apache.catalina.util;

import javax.servlet.http.Cookie;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit tests for the <code>CookieTools</code> class.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class CookieToolsTestCase extends TestCase {


    public static void main(String args[]) {
        System.out.println("TestCase started");
    }

    // ----------------------------------------------------- Instance Variables


    /**
     * A "version 0" cookie.
     */
    protected Cookie version0 = null;


    /**
     * A "version 1" cookie.
     */
    protected Cookie version1 = null;



    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public CookieToolsTestCase(String name) {

        super(name);

    }


    // --------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {

        version0 = new Cookie("Version 0 Name", "Version 0 Value");
        version0.setComment("Version 0 Comment");
        version0.setDomain("localhost");
        version0.setPath("/version0");
        version0.setVersion(0);

        version1 = new Cookie("Version 1 Name", "Version 1 Value");
        version1.setComment("Version 1 Comment");
        version1.setDomain("localhost");
        version1.setPath("/version1");
        version1.setVersion(1);

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(CookieToolsTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        version0 = null;
        version1 = null;

    }


    // ------------------------------------------------ Individual Test Methods


    /**
     * Check the value returned by <code>getCookieHeaderName()</code>.
     */
    public void testGetCookieHeaderName() {

        assertEquals("Version 0 cookie header name", "Set-Cookie",
                     CookieTools.getCookieHeaderName(version0));
        assertEquals("Version 1 cookie header name", "Set-Cookie2",
                     CookieTools.getCookieHeaderName(version1));

    }


    /**
     * Check the value returned by <code>getCookieHeaderValue()</code>
     */
    public void testGetCookieHeaderValue() {

        StringBuffer sb = null;

        sb = new StringBuffer();
        CookieTools.getCookieHeaderValue(version0, sb);
        assertEquals("Version 0 cookie header value",
                     "Version 0 Name=Version 0 Value;Domain=localhost;Path=/version0",
                     sb.toString());

        sb = new StringBuffer();
        CookieTools.getCookieHeaderValue(version1, sb);
        assertEquals("Version 1 cookie header value",
                     "Version 1 Name=\"Version 1 Value\";Version=1;Comment=\"Version 1 Comment\";Domain=localhost;Discard;Path=\"/version1\"",
                     sb.toString());

    }


}
