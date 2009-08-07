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
 * Test for proper URL decoding of the getServletPath() and getPathInfo()
 * methods of HttpServletRequest.  The desired values are specified by the
 * <strong>servlet</strong> and <strong>path</strong> request parameters,
 * respectively.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Decoding01 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Identify our configuration parameters
        String desiredServlet = request.getParameter("servlet");
        String desiredPath = request.getParameter("path");

        // Prepare for the desired test
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        StringBuffer results = new StringBuffer();

        // Check the value returned by getServletPath()
        String servletPath = request.getServletPath();
        if (desiredServlet == null) {
            if (servletPath != null)
                results.append(" servletPath is '" + servletPath +
                               "' instead of NULL/");
        } else {
            if (servletPath == null)
                results.append(" servletPath is NULL instead of '" +
                               desiredPath + "'/");
            else if (!servletPath.equals(desiredServlet))
                results.append(" servletPath is '" + servletPath +
                               "' instead of '" + desiredServlet + "'/");
        }

        // Check the value returned by getPathInfo()
        String pathInfo = request.getPathInfo();
        if (desiredPath == null) {
            if (pathInfo != null)
                results.append(" pathInfo is '" + pathInfo +
                               "' instead of NULL/");
        } else {
            if (pathInfo == null)
                results.append(" pathInfo is NULL instead of '" +
                               desiredPath + "'/");
            else if (!pathInfo.equals(desiredPath))
                results.append(" pathInfo is '" + pathInfo +
                               "' instead of '" + desiredPath + "'/");
        }

        // Report success or failure
        if (results.length() < 1)
            writer.println("Decoding01 PASSED");
        else {
            writer.print("Decoding01 FAILED -");
            writer.println(results.toString());
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
