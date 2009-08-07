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
 * Make sure container sets up include reques attributes.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Include10 extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare this response
        StringBuffer sb = new StringBuffer();
        response.setContentType("text/plain");
	PrintWriter writer = response.getWriter();

        // Pass copies of the original request properties
        request.setAttribute("original.request_uri",
                             request.getRequestURI());
        request.setAttribute("original.context_path",
                             request.getContextPath());
        request.setAttribute("original.servlet_path",
                             request.getServletPath());
        request.setAttribute("original.path_info",
                             request.getPathInfo());
        request.setAttribute("original.query_string",
                             request.getQueryString());

        // Create a request dispatcher and call include() on it
        RequestDispatcher rd =
            getServletContext().getRequestDispatcher
            ("/Include10a/include/path?name2=value2");
        if (rd == null) {
            sb.append(" No RequestDispatcher returned/");
        } else {
            if (sb.length() < 1)
                rd.include(request, response);
        }

        // Write our response if an error occurred
        if (sb.length() >= 1) {
            writer.print("Include00 FAILED -");
            writer.println(sb.toString());
            while (true) {
                String message = StaticLogger.read();
                if (message == null)
                    break;
                writer.println(message);
            }
        }
        StaticLogger.reset();

    }

}
