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
 * Part 5 of the ErrorPage Tests.  Throws a RuntimeException of a type
 * specified by the <code>type</code> query parameter, which must be one
 * of the following:
 * <ul>
 * <li><strong>ArithmeticException</strong> - Forwarded to "/ErrorPage06".</li>
 * <li><strong>ArrayIndexOutOfBoundsException</strong> -
 *     Forwarded to "/ErrorPage06.jsp".</li>
 * <li><strong>NumberFormatException</strong> -
 *     Forwarded to "/ErrorPage06.html".</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class ErrorPage05 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Write a FAILED message that should get replaced by the error text
        writer.println("ErrorPage05 FAILED - Original response returned");

        // Throw the specified exception
        String type = request.getParameter("type");
        if ("Arithmetic".equals(type)) {
            throw new ArithmeticException
                ("ErrorPage05 Threw ArithmeticException");
        } else if ("Array".equals(type)) {
            throw new ArrayIndexOutOfBoundsException
                ("ErrorPage05 Threw ArrayIndexOutOfBoundsException");
        } else if ("Number".equals(type)) {
            throw new NumberFormatException
                ("ErrorPage05 Threw NumberFormatException");
        }

    }

}
