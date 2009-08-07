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
import java.security.Principal;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Ensure that the "tomcat" user has been successfully authenticated.  Although
 * this URI is not protected by a security constraint, the test client will
 * have authenticated a user previously by calling "/Authentication01".
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Authentication02 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        String remoteUser = request.getRemoteUser();
        Principal userPrincipal = request.getUserPrincipal();
        String errors = "";
        if (remoteUser == null)
            errors += "  getRemoteUser() returned NULL.";
        else if (!remoteUser.equals("tomcat"))
            errors += "  remoteUser=" + remoteUser + ".";
        if (userPrincipal == null)
            errors += "  getUserPrincpal() returned NULL.";
        else if (!userPrincipal.getName().equals("tomcat"))
            errors += "  userPrincipal=" + userPrincipal.getName() + ".";
        if ((remoteUser != null) &&
            (userPrincipal != null) &&
            !remoteUser.equals(userPrincipal.getName()))
            errors += "  remoteUser=" + remoteUser + " userPrincipal=" +
                userPrincipal.getName();
        if (errors.length() > 0)
            writer.println("Authentication02 FAILED:" + errors);
        else
            writer.println("Authentication02 PASSED");

    }

}
