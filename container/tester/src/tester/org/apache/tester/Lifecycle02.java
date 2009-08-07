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
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Positive test for servlet lifecycle management.  This servlet is
 * <strong>not</strong> declared to be load-on-startup, and the first request
 * made to it should be a "POST".
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Lifecycle02 extends HttpServlet {

    private boolean doubled = false;

    private boolean initialized = false;

    public void init() throws ServletException {
        if (initialized)
            doubled = true;
        else
            initialized = true;
    }

    public void destroy() {
        initialized = false;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        if (doubled) {
            writer.println("Lifecycle02 FAILED - GET and double initialization");
        } else if (initialized) {
            writer.println("Lifecycle02 FAILED - GET called");
        } else {
            writer.println("Lifecycle02 FAILED - GET and not initialized");
        }

        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        if (doubled) {
            writer.println("Lifecycle02 FAILED - Double initialization");
        } else if (initialized) {
            writer.println("Lifecycle02 PASSED");
        } else {
            writer.println("Lifecycle02 FAILED - POST but not initialized");
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
