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
 * Part 7 of the error page tests.  This servlet is configured as
 * <code>load-on-startup</code> in the web.xml file, and should return
 * status 503 (service unavailable) when it is called later.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class ErrorPage07 extends HttpServlet {

    public void init() throws ServletException {
        throw new UnavailableException
            ("ErrorPage07 Threw UnavailableException in init()");
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.println("ErrorPage07 FAILED - Called unavailable servlet");

        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
