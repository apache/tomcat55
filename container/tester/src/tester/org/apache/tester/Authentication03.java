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
import java.security.Principal;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Ensure that we get the correct results from <code>isUserInRole()</code>
 * for an actual role, a role aliased with a
 * <code>&lt;security-role-ref&gt;</code> element, and for a role that is
 * not assigned to the specified user.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Authentication03 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        // Prepare to create this response
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        StringBuffer results = new StringBuffer();

        // Validate that we have been authenticated correctly
        String remoteUser = request.getRemoteUser();
        if (remoteUser == null) {
            results.append("  Not Authenticated/");
        } else if (!"tomcat".equals(remoteUser)) {
            results.append("  Authenticated as '");
            results.append(remoteUser);
            results.append("'/");
        }

        // Validate that this user is part of the "tomcat" role
        if (!request.isUserInRole("tomcat")) {
            results.append("  Not in role 'tomcat'/");
        }

        // Validate that this user is part of the "alias" role
        // (mapped to "tomcat" in a <security-role-ref> element
        if (!request.isUserInRole("alias")) {
            results.append("  Not in role 'alias'/");
        }

        // Validate that this user is NOT part of the "unknown" role
        if (request.isUserInRole("unknown")) {
            results.append("  In role 'unknown'/");
        }

        // Generate our response
        if (results.length() < 1) {
            writer.println("Authentication03 PASSED");
        } else {
            writer.print("Authentication03 FAILED -");
            writer.println(results.toString());
        }

        // Add wrapper messages as required
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();


    }

}
