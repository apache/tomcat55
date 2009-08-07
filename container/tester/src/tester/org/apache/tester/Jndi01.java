/*
 * Copyright 1999, 2000, 2001 ,2004 The Apache Software Foundation.
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

package org.apache.tester;


import java.io.*;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.tester.SessionBean;
import org.apache.tester.shared.SharedSessionBean;
import org.apache.tester.unpshared.UnpSharedSessionBean;
import org.apache.tester.unshared.UnsharedSessionBean;


/**
 * Negative test for ensuring that the naming context provided by the servlet
 * container is immutable.  No attempt to add, modify, or delete any binding
 * should succeed.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Jndi01 extends HttpServlet {

    public void init() throws ServletException {

        // Access the naming context from init()
        Context ctx = null;
        try {
            ctx = new InitialContext();
            ctx.lookup("java:/comp");
            log("initialized successfully in init()");
        } catch (NamingException e) {
            e.printStackTrace();
            log("Cannot create context in init()", e);
            throw new ServletException(e);
        }

        // Access some application beans from init()

        try {
            SessionBean sb = new SessionBean();
            log("OK Accessing SessionBean");
        } catch (Throwable t) {
            log("FAIL Accessing SessionBean", t);
        }

        try {
            SharedSessionBean sb = new SharedSessionBean();
            log("OK Accessing SharedSessionBean");
        } catch (Throwable t) {
            log("FAIL Accessing SharedSessionBean", t);
        }

        try {
            UnpSharedSessionBean sb = new UnpSharedSessionBean();
            log("OK Accessing UnpSharedSessionBean");
        } catch (Throwable t) {
            log("FAIL Accessing UnpSharedSessionBean", t);
        }

        try {
            UnsharedSessionBean sb = new UnsharedSessionBean();
            log("OK Accessing UnsharedSessionBean");
        } catch (Throwable t) {
            log("FAIL Accessing UnsharedSessionBean", t);
        }

    }

    public void destroy() {
        Context ctx = null;
        try {
            ctx = new InitialContext();
            ctx.lookup("java:/comp");
            log("initialized successfully in destroy()");
        } catch (NamingException e) {
            e.printStackTrace();
            log("Cannot create context in destroy()", e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare to render our output
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        StringBuffer sb = new StringBuffer();
        boolean ok = true;
        Object value = null;

        // Look up the initial context provided by our servlet container
        Context initContext = null;
        try {
            initContext = new InitialContext();
        } catch (NamingException e) {
            log("Create initContext", e);
            sb.append("  Cannot create initContext.");
            ok = false;
        }

        // Look up the environment context provided to our web application
        Context envContext = null;
        try {
            if (ok) {
                value = initContext.lookup("java:comp/env");
                envContext = (Context) value;
                if (envContext == null) {
                    sb.append("  Missing envContext.");
                    ok = false;
                }
            }
        } catch (ClassCastException e) {
            sb.append("  envContext class is ");
            sb.append(value.getClass().getName());
            sb.append(".");
            ok = false;
        } catch (NamingException e) {
            log("Create envContext", e);
            sb.append("  Cannot create envContext.");
            ok = false;
        }

        // Attempt to add a new binding to our environment context
        try {
            if (ok) {
                envContext.bind("newEntry", "New Value");
                sb.append("  Allowed bind().");
                value = envContext.lookup("newEntry");
                if (value != null)
                    sb.append("  Allowed lookup() of added entry.");
            }
        } catch (Throwable e) {
            log("Add binding", e);
        }

        // Attempt to change the value of an existing binding
        try {
            if (ok) {
                envContext.rebind("stringEntry", "Changed Value");
                sb.append("  Allowed rebind().");
                value = envContext.lookup("stringEntry");
                if ((value != null) &&
                    (value instanceof String) &&
                    "Changed Value".equals((String) value))
                    sb.append("  Allowed lookup() of changed entry.");
            }
        } catch (Throwable e) {
            log("Change binding", e);
        }

        // Attempt to delete an existing binding
        try {
            if (ok) {
                envContext.unbind("byteEntry");
                sb.append("  Allowed unbind().");
                value = envContext.lookup("byteEntry");
                if (value == null)
                    sb.append("  Allowed unbind of deleted entry.");
            }
        } catch (Throwable e) {
            log("Delete binding", e);
        }

        // Report our ultimate success or failure
        if (sb.length() < 1)
            writer.println("Jndi01 PASSED");
        else {
            writer.print("Jndi01 FAILED -");
            writer.println(sb);
        }

        // Add wrapper messages as required
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
