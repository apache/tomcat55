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
 * Test to insure that an included servlet can set request attributes that are
 * visible to the calling servlet after the <code>include()</code> returns.
 * The spec is silent on this topic, but it seems consistent with the overall
 * intent to behave in this manner.
 *
 * The test includes either a servlet ("/Include03a") or a JSP page
 * ("/Include03b.jsp") depending on the value specified for the "path"
 * parameter.  The default is the servlet.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Include03 extends HttpServlet {

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

        // Acquire the path to which we will issue an include
        String path = request.getParameter("path");
        if (path == null)
            path = "/Include03a";

        // Create a request dispatcher and call include() on it
        RequestDispatcher rd =
            getServletContext().getRequestDispatcher(path);
        if (rd == null) {
            sb.append(" No RequestDispatcher returned/");
        } else {
            rd.include(request, response);
        }
        response.resetBuffer();

        // We MUST be able to see the attribute created by the includee
        String value = null;
        try {
            value = (String)
                request.getAttribute(path.substring(1));
        } catch (ClassCastException e) {
            sb.append(" Returned attribute not of type String/");
        }
        if ((sb.length() < 1) && (value == null)) {
            sb.append(" No includee-created attribute was returned/");
        }

        // We MUST NOT see the special attributes created by the container
        for (int i = 0; i < specials.length; i++) {
            if (request.getAttribute(specials[i]) != null) {
                sb.append(" Returned attribute ");
                sb.append(specials[i]);
                sb.append("/");
            }
        }

        // Write our response
        if (sb.length() < 1)
            writer.println("Include03 PASSED");
        else {
            writer.print("Include03 FAILED -");
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
