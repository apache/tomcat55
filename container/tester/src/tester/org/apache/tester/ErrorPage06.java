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
 * Part 6 of the ErrorPage Tests.  Should be mapped by the container when
 * the ErrorPage05 servlet returns the appropriate exception.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class ErrorPage06 extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.reset();
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Accumulate all the reasons this request might fail
        StringBuffer sb = new StringBuffer();
        Object value = null;

        value = request.getAttribute("javax.servlet.error.exception");
        StaticLogger.write("exception is '" + value + "'");
        if (value == null) {
            sb.append(" exception is missing/");
        } else if (!(value instanceof java.lang.ArithmeticException)) {
            sb.append(" exception class is ");
            sb.append(value.getClass().getName());
            sb.append("/");
        }

        value = request.getAttribute("javax.servlet.error.exception_type");
        StaticLogger.write("exception_type is '" + value + "'");
        if (value != null)
            StaticLogger.write("exception_type class is '" +
                               value.getClass().getName() + "'");
        if (value == null)
            sb.append(" exception_type is missing/");
        else if (!(value instanceof Class)) {
            sb.append(" exception_type class is ");
            sb.append(value.getClass().getName());
            sb.append("/");
        } else {
            Class clazz = (Class) value;
            String name = clazz.getName();
            if (!"java.lang.ArithmeticException".equals(name)) {
                sb.append(" exception_type is ");
                sb.append(name);
                sb.append("/");
            }
        }

        value = request.getAttribute("javax.servlet.error.message");
        StaticLogger.write("message is '" + value + "'");
        if (value == null)
            sb.append(" message is missing/");
        else if (!(value instanceof String)) {
            sb.append(" message class is ");
            sb.append(value.getClass().getName());
            sb.append("/");
        } else if (!"ErrorPage05 Threw ArithmeticException".equals(value) &&
                   !"ErrorPage08 Threw ArithmeticException".equals(value)) {
            sb.append(" message is not correct");
        }

        value = request.getAttribute("javax.servlet.error.request_uri");
        StaticLogger.write("request_uri is '" + value + "'");
        if (value == null)
            sb.append(" request_uri is missing/");
        else if (!(value instanceof String)) {
            sb.append(" request_uri class is ");
            sb.append(value.getClass().getName());
            sb.append("/");
        } else {
            String request_uri = (String) value;
            String test1 = request.getContextPath() + "/ErrorPage05";
            String test2 = request.getContextPath() + "/WrappedErrorPage05";
            String test3 = request.getContextPath() + "/ErrorPage08";
            String test4 = request.getContextPath() + "/WrappedErrorPage08";
            if (!request_uri.equals(test1) && !request_uri.equals(test2) &&
                !request_uri.equals(test3) && !request_uri.equals(test4)) {
                sb.append(" request_uri is ");
                sb.append(request_uri);
                sb.append("/");
            }
        }

        value = request.getAttribute("javax.servlet.error.servlet_name");
        StaticLogger.write("servlet_name is '" + value + "'");
        if (value == null)
            sb.append(" servlet_name is missing/");
        else if (!(value instanceof String)) {
            sb.append(" servlet_name class is ");
            sb.append(value.getClass().getName());
            sb.append("/");
        } else {
            String servlet_name = (String) value;
            if (!"ErrorPage05".equals(servlet_name) &&
                !"ErrorPage08".equals(servlet_name)) {
                sb.append(" servlet_name is ");
                sb.append(servlet_name);
                sb.append("/");
            }
        }

        // Report ultimate success or failure
        if (sb.length() < 1)
            writer.println("ErrorPage06 PASSED - SERVLET");
        else
            writer.println("ErrorPage06 FAILED -" + sb.toString());

        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }


}
