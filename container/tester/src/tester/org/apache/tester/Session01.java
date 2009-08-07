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
import org.apache.tester.unpshared.UnpSharedSessionBean;
import org.apache.tester.unshared.UnsharedSessionBean;


/**
 * Part 1 of Session Tests.  Ensures that there is no current session, then
 * creates a new session and sets a session attribute.  Also, ensure that
 * calling setAttribute("name", null) acts like removeAttribute().
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Session01 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Ensure that there is no current session
        boolean ok = true;
        HttpSession session = request.getSession(false);
        if (session != null) {
            writer.println("Session01 FAILED - Requested existing session " +
                           session.getId());
            ok = false;
        }

        // Create a new session
        if (ok) {
            session = request.getSession(true);
            if (session == null) {
                writer.println("Session01 FAILED - No session created");
                ok = false;
            }
        }

        // Store an activation event listener in the session
        if (ok) {
            session.setAttribute("activationListener",
                                 new SessionListener03());
        }

        // Ensure that there is no existing attribute
        if (ok) {
            if (session.getAttribute("sessionBean") != null) {
                writer.println("Session01 FAILED - Attribute already exists");
                ok = false;
            }
        }

        // Create and stash a session attribute
        if (ok) {
            SessionBean bean = new SessionBean();
            bean.setStringProperty("Session01");
            session.setAttribute("sessionBean", bean);
        }

        // Ensure that we can retrieve the attribute successfully
        if (ok) {
            Object bean = session.getAttribute("sessionBean");
            if (bean == null) {
                writer.println("Session01 FAILED - Cannot retrieve attribute");
                ok = false;
            } else if (!(bean instanceof SessionBean)) {
                writer.println("Session01 FAILED - Attribute instance of " +
                               bean.getClass().getName());
                ok = false;
            } else {
                String value = ((SessionBean) bean).getStringProperty();
                if (!"Session01".equals(value)) {
                    writer.println("Session01 FAILED - Property = " + value);
                    ok = false;
                }
            }
        }

        // Ensure that setAttribute("name", null) works correctly
        if (ok) {
            session.setAttribute("FOO", "BAR");
            session.setAttribute("FOO", null);
            if (session.getAttribute("FOO") != null) {
                writer.println("Session01 FAILED - setAttribute(name,null)");
                ok = false;
            }
        }

        // Create more beans that will be used to test application restart
        if (ok) {
            SharedSessionBean ssb = new SharedSessionBean();
            ssb.setStringProperty("Session01");
            session.setAttribute("sharedSessionBean", ssb);
            UnpSharedSessionBean ussb = new UnpSharedSessionBean();
            ussb.setStringProperty("Session01");
            session.setAttribute("unpSharedSessionBean", ussb);
            UnsharedSessionBean usb = new UnsharedSessionBean();
            usb.setStringProperty("Session01");
            session.setAttribute("unsharedSessionBean", usb);
        }

        // Report success if everything is still ok
        if (ok)
            writer.println("Session01 PASSED");
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
