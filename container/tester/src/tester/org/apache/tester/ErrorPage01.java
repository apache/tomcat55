/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
 * Part 1 of the ErrorPage Tests.  Returns a response status code of 412
 * (HttpServletResponse.SC_PRECONDITION_FAILED) which is mapped to the
 * ErrorPage02 servlet in the deployment descriptor.
 *
 * @author Craig R. McClanahan
 * @version $Id$
 */

public class ErrorPage01 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Write a FAILED message that should get replaced by the error text
        writer.println("ErrorPage01 FAILED - Original response returned");

        // Return the appropriate response code
        response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED,
                           "ErrorPage01 Returned Status Code 412");

    }

}
