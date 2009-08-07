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
 * Part 5 of Session Tests.  Ensures that the appropriate session listener
 * events get called in the appropriate order.  Relies on proper configuration
 * of SessionListener01 and SessionListener02 in the web.xml file
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Session05 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Reset the static logger to ensure no leftovers exist
        StaticLogger.reset();

        // Perform session management activities to trigger listener events
        HttpSession session = request.getSession(true);
        session.setAttribute("attribute1", "value1");
        session.setAttribute("attribute1", "value2");
        session.removeAttribute("attribute1");
        session.removeAttribute("attribute2"); // Not present, so no logging
        session.invalidate();

        // Render the response (to be compared as a golden file)
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
