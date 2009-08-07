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
import java.lang.reflect.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Negative test for access to Catalina internals through the objects that
 * are exposed to this servlet by the container.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Reflection01 extends HttpServlet {

    public void service(HttpServletRequest request,
                        HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        StringBuffer results = new StringBuffer();

        // Check the ServletConfig object
        try {
            ServletConfig servletConfig = getServletConfig();
            Method method = servletConfig.getClass().getMethod
                ("getParent", new Class[] {});
            Object parent = method.invoke(servletConfig,
                                          new Object[] {});
            results.append(" Can reflect on ServletConfig/");
        } catch (Throwable t) {
            StaticLogger.write("ServletConfig: " + t);
        }

        // Check the ServletContext object
        try {
            ServletContext servletContext = getServletContext();
            Method method = servletContext.getClass().getMethod
                ("getResources", new Class[] {});
            Object resources = method.invoke(servletContext,
                                             new Object[] {});
            results.append(" Can reflect on ServletContext/");
        } catch (Throwable t) {
            StaticLogger.write("ServletContext: " + t);
        }

        // Check the HttpServletRequest object
        try {
            Method method = request.getClass().getMethod
                ("getInfo", new Class[] {});
            Object info = method.invoke(request,
                                        new Object[] {});
            results.append(" Can reflect on HttpServletRequest/");
        } catch (Throwable t) {
            StaticLogger.write("HttpServletRequest: " + t);
        }

        // Check the HttpServletResponse object
        try {
            Method method = request.getClass().getMethod
                ("getInfo", new Class[] {});
            Object info = method.invoke(request,
                                        new Object[] {});
            results.append(" Can reflect on HttpServletResponse/");
        } catch (Throwable t) {
            StaticLogger.write("HttpServletResponse: " + t);
        }

        // Check the HttpSession object
        try {
            HttpSession session = request.getSession(true);
            Method method = session.getClass().getMethod
                ("getInfo", new Class[] {});
            results.append(" Can reflect on HttpSession/");
        } catch (Throwable t) {
            StaticLogger.write("HttpSession: " + t);
        }

        // Check the RequestDispatcher object
        try {
            RequestDispatcher rd =
                getServletContext().getRequestDispatcher("/index.shtml");
            Method method = rd.getClass().getMethod
                ("getInfo", new Class[] {});
            results.append(" Can reflect on RequestDispatcher/");
        } catch (Throwable t) {
            StaticLogger.write("RequestDispatcher: " + t);
        }

        // Report final results
        if (results.length() < 1)
            writer.println("Reflection01 PASSED");
        else {
            writer.print("Reflection01 FAILED -");
            writer.println(results.toString());
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
