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
 * Ensure the correct container managed request attributes are set.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Include10a extends HttpServlet {

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
	PrintWriter writer = response.getWriter();

        // Validate the original request properties
        String value = null;
        value = (String) request.getAttribute("original.request_uri");
        if (!value.equals(request.getRequestURI()))
            sb.append(" getRequestURI() is " + request.getRequestURI() +
                      " but should be " + value + "|");
        value = (String) request.getAttribute("original.context_path");
        if (!value.equals(request.getContextPath()))
            sb.append(" getContextPath() is " + request.getContextPath() +
                      " but should be " + value + "|");
        value = (String) request.getAttribute("original.servlet_path");
        if (!value.equals(request.getServletPath()))
            sb.append(" getServletPath() is " + request.getServletPath() +
                      " but should be " + value + "|");
        value = (String) request.getAttribute("original.path_info");
        if (!value.equals(request.getPathInfo()))
            sb.append(" getPathInfo() is " + request.getPathInfo() +
                      " but should be " + value + "|");
        value = (String) request.getAttribute("original.query_string");
        if (!value.equals(request.getQueryString()))
            sb.append(" getQueryString() is " + request.getQueryString() +
                      " but should be " + value + "|");

        // Validate the container provided request attributes
        value = (String)
            request.getAttribute("javax.servlet.include.request_uri");
        if (!(request.getContextPath() + "/Include10a/include/path").equals(value))
            sb.append(" request_uri is " + value +
                      " but should be " + request.getContextPath() +
                      "/Include10a/include/path|");
        value = (String)
            request.getAttribute("javax.servlet.include.context_path");
        if (!request.getContextPath().equals(value))
            sb.append(" context_path is " + value +
                      " but should be " + request.getContextPath() + "|");
        value = (String)
            request.getAttribute("javax.servlet.include.servlet_path");
        if (!"/Include10a".equals(value))
            sb.append(" servlet_path is " + value +
                      " but should be /Include10a|");
        value = (String)
            request.getAttribute("javax.servlet.include.path_info");
        if (!"/include/path".equals(value))
            sb.append(" path_info is " + value +
                      " but should be /include/path|");
        value = (String)
            request.getAttribute("javax.servlet.include.query_string");
        if (!"name2=value2".equals(value))
            sb.append(" query_string is " + value +
                      " but should be name2=value2|");

        // Generate our success or failure report
        if (sb.length() < 1)
            writer.println("Include10a PASSED");
        else
            writer.println("Include10a FAILED -" + sb.toString());

    }

}
