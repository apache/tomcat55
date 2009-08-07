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
 * Part 6 of Session Tests.  Ensures that an attempt to create a new session
 * after the response has been committed throws IllegalStateException.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Session06 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare and commit our response
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.print("Session06 ");
        response.flushBuffer();

        // Attempt to create a new session
        try {
            HttpSession session = request.getSession(true);
            if (session == null)
                writer.println("FAILED - Did not throw IllegalStateException");
            else
                writer.println("FAILED - Returned new session");
        } catch (IllegalStateException e) {
            writer.println("PASSED");
        } catch (Throwable t) {
            writer.println("FAILED - Threw " + t);
            t.printStackTrace(writer);
        }

        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();
        response.flushBuffer();

    }

}
