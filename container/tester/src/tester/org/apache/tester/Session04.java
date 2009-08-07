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
 * Part 4 of Session Tests.  Ensures that there is an existing session, and
 * that the requested session information matches it.  Also, ensure that we
 * can invalidate this session and create a new one (with a different session
 * identifier) while processing this request.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Session04 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        log("Session04 - Starting, requestedSessionId = " +
            request.getRequestedSessionId());
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Ensure that there is a current session
        StringBuffer results = new StringBuffer();
        HttpSession oldSession = request.getSession(false);
        if (oldSession == null)
            results.append(" No existing session/");

        // Acquire the session identifier of the old session
        String oldSessionId = null;
        if (oldSession != null) {
            try {
                oldSessionId = oldSession.getId();
            } catch (IllegalStateException e) {
                results.append(" Old session is expired/");
            }
        }

        // Match against the requested session identifier
        String requestedSessionId = null;
        if (oldSessionId != null) {
            requestedSessionId = request.getRequestedSessionId();
            if (requestedSessionId == null) {
                results.append(" No requested session id/");
            } else {
                if (!request.isRequestedSessionIdValid())
                    results.append(" Requested session id is not valid/");
                if (!oldSessionId.equals(requestedSessionId)) {
                    results.append(" Requested session=");
                    results.append(requestedSessionId);
                    results.append(" Old session=");
                    results.append(oldSessionId);
                    results.append("/");
                }
            }
        }

        // Verify that we received the requested session identifier correctly
        if (requestedSessionId != null) {
            if (!request.isRequestedSessionIdFromCookie())
                results.append(" Requested session not from cookie/");
            if (request.isRequestedSessionIdFromURL())
                results.append(" Requested session from URL/");
        }

        // Verify that we can create an attribute in the old session
        if (oldSession != null) {
            SessionBean bean = new SessionBean();
            bean.setStringProperty("Session04");
            oldSession.setAttribute("sessionBean", bean);
        }

        // Verify that we can invalidate the old session
        if (oldSession != null) {
            try {
                oldSession.invalidate();
            } catch (IllegalStateException e) {
                results.append(" Old session is already invalidated/");
            }
        }

        // Verify that we can create a new session
        HttpSession newSession = request.getSession(true);
        if (newSession == null) {
            results.append(" Cannot create new session/");
        } else {
            String newSessionId = null;
            try {
                newSessionId = newSession.getId();
            } catch (IllegalStateException e) {
                results.append(" New session is already invalidated/");
            }
            if ((oldSession != null) && (newSession != null)) {
                if (oldSession == newSession)
                    results.append(" oldSession == newSession/");
                if (oldSession.equals(newSession))
                    results.append(" oldSession equals newSession/");
            }
            if ((oldSessionId != null) && (newSessionId != null) &&
                oldSessionId.equals(newSessionId)) {
                results.append(" New session id = old session id/");
            }
        }

        // Verify that the old session's attribute did not carry forward
        if (newSession != null) {
            SessionBean bean =
                (SessionBean) newSession.getAttribute("sessionBean");
            if (bean != null)
                results.append(" New session has attribute already/");
        }

        // Store an activation event listener in the session
        newSession.setAttribute("activationListener",
                                    new SessionListener03());

        // Report success if everything is still ok
        if (results.length() == 0)
            writer.println("Session04 PASSED");
        else {
            writer.print("Session04 FAILED -");
            writer.println(results.toString());
        }
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();
        log("Session04 - Stopping");

    }

}
