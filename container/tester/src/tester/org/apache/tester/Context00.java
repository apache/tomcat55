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
import org.apache.tester.shared.SharedSessionBean;
import org.apache.tester.unshared.UnsharedSessionBean;


/**
 * Part 0 of Context Tests.  This servlet is never executed directly.  Its
 * purpose is to create a servlet context attribute at <code>init()</code>
 * time, and remove it at <code>destroy()</code> time.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Context00 extends HttpServlet {


    public void destroy() {
        getServletContext().log("Context00: Removing attribute 'context00'");
        getServletContext().removeAttribute("context00");
    }


    public void init() throws ServletException {
        getServletContext().log("Context00: Setting attribute 'context00'");
        ContextBean cb = new ContextBean();
        cb.setStringProperty("Context00");
        getServletContext().setAttribute("context00", cb);
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.println("Context00 PASSED");
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();
    }


}
