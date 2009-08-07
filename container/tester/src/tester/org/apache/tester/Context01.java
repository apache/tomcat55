/*
 * Copyright 1999, 2000 ,2004 The Apache Software Foundation.
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
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.tester.shared.SharedSessionBean;
import org.apache.tester.unshared.UnsharedSessionBean;


/**
 * Part 1 of Context Tests.  Exercise various methods for dealing with
 * servlet context attributes.  Leave an attribute named "context01"
 * present, which should be erased after a web application restart.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Context01 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        boolean ok = true;
        PrintWriter writer = response.getWriter();
        ServletContext context = getServletContext();

        // Ensure that there is no existing attribute
        if (ok) {
            if (context.getAttribute("context01") != null) {
                writer.println("Context01 FAILED - Attribute already exists");
                ok = false;
            }
        }

        // Create and stash a context attribute
        if (ok) {
            ContextBean bean = new ContextBean();
            bean.setStringProperty("Context01");
            context.setAttribute("context01", bean);
        }

        // Ensure that we can retrieve the attribute successfully
        if (ok) {
            Object bean = context.getAttribute("context01");
            if (bean == null) {
                writer.println("Context01 FAILED - Cannot retrieve attribute");
                ok = false;
            }
            if (ok) {
                if (!(bean instanceof ContextBean)) {
                    writer.println("Context01 FAILED - Bean instance of " +
                                   bean.getClass().getName());
                    ok = false;
                }
            }
            if (ok) {
                String value = ((ContextBean) bean).getStringProperty();
                if (!"Context01".equals(value)) {
                    writer.println("Context01 FAILED - Value = " + value);
                    ok = false;
                }
            }
            if (ok) {
                String lifecycle = ((ContextBean) bean).getLifecycle();
                if (!"/add".equals(lifecycle)) {
                    writer.println("Context01 FAILED - Bean lifecycle is " +
                                   lifecycle);
                    ok = false;
                }
            }
        }

        // Ensure that we can update this attribute and check its lifecycle
        if (ok) {
            ContextBean bean = (ContextBean) context.getAttribute("context01");
            context.setAttribute("context01", bean);
            String lifecycle = bean.getLifecycle();
            if (!"/add/rep".equals(lifecycle)) {
                writer.println("Context01 FAILED - Bean lifecycle is " +
                               lifecycle);
                ok = false;
            }
        }

        // Ensure that we can remove this attribute and check its lifecycle
        if (ok) {
            ContextBean bean = (ContextBean) context.getAttribute("context01");
            context.removeAttribute("context01");
            String lifecycle = bean.getLifecycle();
            if (!"/add/rep/rem".equals(lifecycle)) {
                writer.println("Context01 FAILED - Bean lifecycle is " +
                               lifecycle);
                ok = false;
            }
        }

        // Add a bean back for the restart application test
        context.setAttribute("context01", new ContextBean());

        // Ensure that setAttribute("name", null) works correctly
        if (ok) {
            context.setAttribute("FOO", "BAR");
            context.setAttribute("FOO", null);
            if (context.getAttribute("FOO") != null) {
                writer.println("Context01 FAILED - setAttribute(name,null)");
                ok = false;
            }
        }

        // Report success if everything is still ok
        if (ok)
            writer.println("Context01 PASSED");
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
