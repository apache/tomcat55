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
 * Test for servlet lifecycle management.  It's behavior is controlled by
 * a request parameter <strong>step</strong>, which must be set to one of
 * the following values:
 * <ul>
 * <li><em>1</em> - Throw an <code>UnavailableException</code> that indicates
 *     permanent unavailablility, which should cause this servlet instance
 *     to be destroyed and thrown away.</li>
 * <li><em>2</em> - Check the lifecycle variables to ensure that the old
 *     instance was not reused.</li>
 * </ul>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Lifecycle03 extends HttpServlet {

    private static String staticTrail = "";

    private String instanceTrail = "";

    public void init() throws ServletException {
        staticTrail += "I";
        instanceTrail += "I";
    }

    public void destroy() {
        staticTrail += "D";
        instanceTrail += "D";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        staticTrail += "S";
        instanceTrail += "S";

        // For step=1, throw an exception
        if ("1".equals(request.getParameter("step"))) {
            staticTrail = "IS";
            throw new UnavailableException("Lifecycle03 is permanently unavailable");
        }

        // For step=2, evaluate the results.
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        if (staticTrail.equals("ISDIS") && instanceTrail.equals("IS"))
            writer.println("Lifecycle03 PASSED");
        else
            writer.println("Lifecycle03 FAILED - staticTrail=" + staticTrail +
                           ", instanceTrail=" + instanceTrail);

        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }


}
