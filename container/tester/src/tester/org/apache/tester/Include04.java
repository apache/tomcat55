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
 * Test to ensure that we can include a servlet (without flushing the buffer),
 * then forward to another servlet that replaces the original contents.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Include04 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare this response
        response.setBufferSize(8192);
        StringBuffer sb = new StringBuffer();
        response.setContentType("text/plain");
	PrintWriter writer = response.getWriter();

        // Include our first subservlet
        RequestDispatcher rd1 =
          getServletContext().getRequestDispatcher("/Include04a");
        if (rd1 == null) {
            sb.append(" No RD for '/Include04a'/");
        } else {
            rd1.include(request, response);
        }

        // Forward to our second subservlet
        RequestDispatcher rd2 =
          getServletContext().getRequestDispatcher("/Include04b");
        if (rd2 == null) {
            sb.append("No RD for '/Include04b'/");
        } else {
          rd2.forward(request, response);
        }

        // Append error messages if necessary
        if (sb.length() > 0) {
            writer.print("Include04 FAILED -");
            writer.println(sb.toString());
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
