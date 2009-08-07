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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Ensure that we can use the Xerces parser while included in a web application
 * even though the servlet container might utilize its own parser for internal
 * use.
 *
 * @author Amy Roh
 * @author Craig McClanahan
 * @version $Revision$ $Date$
 */

public class Xerces01 extends HttpServlet {


    // --------------------------------------------------------- Public Methods


    /**
     * Perform a simple SAX parse using Xerces (based on the SAXCount
     * example application).
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void service(HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException
    {

        // Prepare our output stream
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        boolean ok = true;

        // Construct a new instance of our parser driver
        URL url = null;
        try {
            url = getServletContext().getResource("/Xerces01.xml");
        } catch (MalformedURLException e) {
            writer.println("Xerces01 FAILED - " + e);
            e.printStackTrace(writer);
            ok = false;
        }
        Xerces01Parser parser = new Xerces01Parser();
        try {
            if (ok)
                parser.parse(url);
        } catch (Exception e) {
            writer.println("Xerces01 FAILED - " + e);
            e.printStackTrace(writer);
            ok = false;
        }

        // Report successful completion if OK
        if (ok)
            writer.println("Xerces01 PASSED");
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }


}
