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
 * Test that insures we can include a servlet that does a forward, then
 * includes another servlet, and we see all of the output in the right order.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Include07 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare this response
        response.setBufferSize(8192);
        response.setContentType("text/plain");
	PrintWriter writer = response.getWriter();
        writer.println("Output from Include07");

        // Include our first subservlet
        RequestDispatcher rd1 =
          getServletContext().getRequestDispatcher("/Include07a");
        if (rd1 == null) {
            writer.println("No RD for '/Include07a'");
        } else {
            rd1.include(request, response);
        }

        // Include our second subservlet
        RequestDispatcher rd2 =
          getServletContext().getRequestDispatcher("/Include07c");
        if (rd2 == null) {
            writer.println("No RD for '/Include07c'");
        } else {
            rd2.include(request, response);
        }

        // Finish this response
        writer.println("Output from Include07");
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
