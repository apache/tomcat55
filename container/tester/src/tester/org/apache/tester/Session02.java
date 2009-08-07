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

/**
 * Part 2 of Session Tests.  Ensures that there is an existing session, and
 * that the session bean stashed in Part 1 can be retrieved successfully.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Session02 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Ensure that there is a current session
        boolean ok = true;
        HttpSession session = request.getSession(false);
        if (session == null) {
            writer.println("Session02 FAILED - No existing session " +
                           request.getRequestedSessionId());
            ok = false;
        }

        // Ensure that we can retrieve the attribute successfully
        if (ok) {
            Object bean = session.getAttribute("sessionBean");
            if (bean == null) {
                writer.println("Session02 FAILED - Cannot retrieve attribute");
                ok = false;
            } else if (!(bean instanceof SessionBean)) {
                writer.println("Session02 FAILED - Attribute instance of " +
                               bean.getClass().getName());
                ok = false;
            } else {
                String value = ((SessionBean) bean).getStringProperty();
                if (!"Session01".equals(value)) {
                    writer.println("Session02 FAILED - Property = " + value);
                    ok = false;
                }
            }
        }

        // Report success if everything is still ok
        if (ok)
            writer.println("Session02 PASSED");
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
