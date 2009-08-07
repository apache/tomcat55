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

public class Forward08a extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare this response
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Forward to the second servlet
        log("Getting RD for /servlet/Forward08b");
        RequestDispatcher rd =
            getServletContext().getRequestDispatcher("/servlet/Forward08b");
        if (rd == null) {
            log("Missing RD for /servlet/Forward08b");
            writer.println("Forward08a FAILED - No request dispatcher" +
                           " for /servlet/Forward08b");
        } else {
            log("Forwarding to /servlet/Forward08b");
            rd.forward(request, response);
            log("Returned from /servlet/Forward08b");
            writer.println("Forward08a text should NOT be present");
        }

    }

}
