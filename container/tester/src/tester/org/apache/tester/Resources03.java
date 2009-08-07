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
 * Positive test for <code>ServletContext.getResourceAsStream()</code> as well
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

public class Resources03 extends HttpServlet {

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
            if (url == null) {
                if (is == null)
                    writer.println("Resources03 FAILED - No IS or URL was returned");
                else
                    writer.println("Resources03 FAILED - Returned IS but no URL");
            } else {
                if (is == null)
                    writer.println("Resources03 FAILED - Returned URL but no IS");
                else {
                    InputStreamReader isr = new InputStreamReader(is);
                    while (true) {
                        int c = isr.read();
                        if (c < 0)
                            break;
                        char ch = (char) c;
                        if (ch < ' ')
                            break;
                        writer.print(ch);
                    }
                    isr.close();
                }
                writer.println();
                writer.println("url = " + url.toString());
            }
        } catch (MalformedURLException e) {
            writer.println("Resources03 FAILED - MalformedURLException: "
                           + e);
        } catch (IOException e) {
            writer.println("Resources03 FAILED - IOException: " + e);
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
