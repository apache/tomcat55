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
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Test getting the servlet input stream after we have retrieved the reader.
 * This should throw an IllegalStateException.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class GetInputStream01 extends GenericServlet {

    public void service(ServletRequest request, ServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        BufferedReader reader = request.getReader();
        try {
            ServletInputStream sis = request.getInputStream();
            writer.println("GetInputStream01 FAILED - Did not throw " +
                           "IllegalStateException");
        } catch (IllegalStateException e) {
            writer.println("GetInputStream01 PASSED");
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
