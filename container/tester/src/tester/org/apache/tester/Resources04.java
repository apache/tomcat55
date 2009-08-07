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
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Negative test for <code>ServletContext.getResourceAsStream()</code> as well
 * as <code>ClassLoader.getResourceAsStream()</code>.  Operation is controlled
 * by query parameters:
 * <ul>
 * <li><strong>mode</strong> - Use <code>context</code> for servlet context
 *     test, or <code>class</code> for class loader test.  [context]</li>
 * <li><strong>path</strong> - Resource path to the requested resource,
 *     starting with a slash.  [/WEB-INF/web.xml]</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Resources04 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Identify our configuration parameters
        String mode = request.getParameter("mode");
        if (mode == null)
            mode = "context";
        String path = request.getParameter("path");
        if (path == null)
            path = "/WEB-INF/web.xml";

        // Execute the desired test
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        InputStream is = null;
        URL url = null;
        try {
            if ("context".equals(mode)) {
                is = getServletContext().getResourceAsStream(path);
                url = getServletContext().getResource(path);
            } else {
                is = this.getClass().getResourceAsStream(path);
                url = this.getClass().getResource(path);
            }
            if (is == null) {
                if (url == null)
                    writer.println("Resources04 PASSED");
                else
                    writer.println("Resources04 FAILED - Stream is null but URL is " + url);
            } else {
                if (url != null)
                    writer.println("Resources04 FAILED - Stream is not null and  URL is " + url);
                else
                    writer.println("Resources04 FAILED - Stream is not null and URL is null");
            }
        } catch (MalformedURLException e) {
            writer.println("Resources04 FAILED - MalformedURLException: "
                           + e);
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
