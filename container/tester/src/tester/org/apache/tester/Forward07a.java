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

public class Forward07a extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare this response
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Forward to the second servlet
        RequestDispatcher rd =
            getServletContext().getRequestDispatcher("/Forward07b");
        if (rd == null) {
            writer.println("Forward07a FAILED - No request dispatcher" +
                           " for /Forward07b");
        } else {
            rd.forward(request, response);
            writer.println("Forward07a text should NOT be present");
        }

    }

}
