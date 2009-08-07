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
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Positive test for HttpServletResponse.sendRedirect().
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Redirect01 extends HttpServlet {


    // --------------------------------------------------------- Public Methods


    /**
     * Process a servlet request and create the corresponding response.
     *
     * @param request The request we are processing
     * @param response The response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        try {
            response.sendRedirect(request.getContextPath() + "/Redirect01a");
            return;
        } catch (IllegalStateException e) {
            writer.println("Redirect01 FAILED - Threw IllegaStateException");
            e.printStackTrace(writer);
        }
        try {
            writer.println("Redirect01 FAILED - Output text after redirect");
        } catch (Throwable t) {
            throw new ServletException("Redirect01 Post-Redirect Output Error",
                                       t);
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
