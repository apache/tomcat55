/*
 * Copyright 1999, 2000 ,2004 The Apache Software Foundation.
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
 * Negative test for ServletResponse.setBufferSize().
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class SetBufferSize01 extends GenericServlet {


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
    public void service(ServletRequest request, ServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        try {
            writer.print("SetBufferSize01 ");
            response.flushBuffer();
            response.setBufferSize(100);
            writer.println("FAILED - Did not throw IllegalStateException");
        } catch (IllegalStateException e) {
            writer.println("PASSED");
        } catch (IOException e) {
            writer.println("FAILED - flushBuffer() threw IOException");
            throw e;
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
