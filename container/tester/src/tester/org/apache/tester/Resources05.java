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
 * Positive test for <code>ServletContext.getResource()</code> as well as
 * <code>ClassLoader.getResource()</code>.  The URL returned by
 * these calls is then opened with <code>url.openStream()</code>
 * in order to ensure that the correct data is actually read.
 * Operation is controlled by query parameters:
 * <ul>
 * <li><strong>mode</strong> - Use <code>context</code> for servlet context
 *     test, or <code>class</code> for class loader test.  [context]</li>
 * <li><strong>path</strong> - Resource path to the requested resource,
 *     starting with a slash.  [/WEB-INF/web.xml]</li>
 * <li><strong>stringify</strong> - If set to any arbitrary value, the URL
 *     returned by getResource() will be converted to a String and then back
 *     to a URL before being opened.  [not set]</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Resources05 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Identify our configuration parameters
        String mode = request.getParameter("mode");
        if (mode == null)
            mode = "context";
        String path = request.getParameter("path");
        if (path == null)
            path = "/WEB-INF/web.xml";
        boolean stringify = (request.getParameter("stringify") != null);

        // Prepare for the desired test
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        InputStream is = null;
        InputStreamReader isr = null;
        URL url = null;
        StringBuffer results = new StringBuffer();
        boolean ok = true;

        // Acquire the appropriate URL
        try {
            if ("context".equals(mode))
                url = getServletContext().getResource(path);
            else
                url = this.getClass().getResource(path);
            if (url == null) {
                results.append(" No URL returned");
                ok = false;
            }
        } catch (MalformedURLException e) {
            results.append(" getResource MalformedURLException");
            ok = false;
        }

        // Stringify the URL if requested
        try {
            if (ok) {
                StaticLogger.write("Stringifying the URL");
                String urlString = url.toString();
                url = new URL(urlString);
            }
        } catch (MalformedURLException e) {
            results.append(" stringify MalformedURLException");
        }

        // Open an input stream and input stream reader on this URL
        try {
            if (ok) {
                is = url.openStream();
                isr = new InputStreamReader(is);
            }
        } catch (IOException e) {
            results.append(" Open IOException: " + e);
            ok = false;
        }

        // Copy the contents of this stream to our output
        try {
            if (ok) {
                while (true) {
                    int ch = isr.read();
                    if (ch < 0)
                        break;
                    writer.print((char) ch);
                }
            }
        } catch (IOException e) {
            results.append(" Copy IOException: " + e);
            ok = false;
        }

        // Close the input stream
        try {
            if (ok) {
                isr.close();
            }
        } catch (IOException e) {
            results.append(" Close IOException: " + e);
        }

        // Report any failures we have encountered
        if (!ok) {
            writer.print("Resources05 FAILED -");
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
