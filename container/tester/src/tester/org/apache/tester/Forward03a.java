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
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Test to ensure that a forwarded-to servlet can receive request attributes
 * set by the calling servlet, as well as set their own request attributes.
 *
 * The test forwards to either a servlet ("/Forward03a") or a JSP page
 * ("/Forward03b.jsp") depending on the value specified for the "path"
 * parameter.  The default is the servlet.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Forward03a extends HttpServlet {

    private static final String specials[] =
    { "javax.servlet.include.request_uri",
      "javax.servlet.include.context_path",
      "javax.servlet.include.servlet_path",
      "javax.servlet.include.path_info",
      "javax.servlet.include.query_string" };


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare this response
        StringBuffer sb = new StringBuffer();
        response.setContentType("text/plain");
	PrintWriter writer = response.getWriter();

        // Verify that we can retrieve the forwarded attribute
        if (request.getAttribute("Forward03") == null)
            sb.append(" Cannot retrieve forwarded attribute/");

        // Verify that we can set and retrieve our own attribute
        request.setAttribute("Forward03a", "This is our own attribute");
        if (request.getAttribute("Forward03a") == null)
            sb.append(" Cannot retrieve our own attribute");

        // Verify that no special attributes are present
        for (int i = 0; i < specials.length; i++) {
            if (request.getAttribute(specials[i]) != null) {
                sb.append(" Returned attribute ");
                sb.append(specials[i]);
                sb.append("/");
            }
        }

        // Write our response
        if (sb.length() < 1)
            writer.println("Forward03 PASSED");
        else {
            writer.print("Forward03 FAILED -");
            writer.println(sb.toString());
        }

        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
