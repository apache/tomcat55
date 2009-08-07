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
 * Ensure that a resource protected a a security constratint that allows all
 * roles will permit access to an authenticated user.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Authentication05 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        StringBuffer sb = new StringBuffer();

        String remoteUser = request.getRemoteUser();
        if (remoteUser == null)
            sb.append(" No remote user returned/");
        else if (!"tomcat".equals(remoteUser)) {
            sb.append(" Remote user is '");
            sb.append(remoteUser);
            sb.append("'/");
        }

        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal == null)
            sb.append(" No user principal returned/");
        else if (!"tomcat".equals(userPrincipal.getName())) {
            sb.append(" User principal is '");
            sb.append(userPrincipal);
            sb.append("'/");
        }

        if (!request.isUserInRole("tomcat"))
            sb.append(" Not in role 'tomcat'/");

        if (sb.length() < 1)
            writer.println("Authentication05 PASSED");
        else {
            writer.print("Authentication05 FAILED -");
            writer.println(sb.toString());
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
