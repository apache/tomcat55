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
 * Testing for double forwarding.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Forward08 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare this response
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Forward to the first servlet
        log("Getting RD for /Forward08a");
        RequestDispatcher rd =
            getServletContext().getRequestDispatcher("/Forward08a");
        if (rd == null) {
            log("Missing RD for /Forward08a");
            writer.println("Forward08 FAILED - No request dispatcher" +
                           " for /Forward08a");
        } else {
            log("Forwarding to /Forward08a");
            rd.forward(request, response);
            log("Returned from /Forward08a");
            writer.println("Forward08 text should NOT be present");
        }

        // Static logger output (should not actually be rendered)
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
