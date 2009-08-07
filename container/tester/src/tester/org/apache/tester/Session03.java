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
import org.apache.tester.unpshared.UnpSharedSessionBean;
import org.apache.tester.unshared.UnsharedSessionBean;


/**
 * Part 3 of Session Tests.  Ensures that there is an existing session, and
 * that the session bean stashed in Part 1 can be retrieved successfully.
 * Then, it removes that attribute and verifies successful removal.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Session03 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Ensure that there is a current session
        boolean ok = true;
        HttpSession session = request.getSession(false);
        if (session == null) {
            writer.println("Session03 FAILED - No existing session " +
                           request.getRequestedSessionId());
            ok = false;
        }

        // Ensure that we can retrieve the attribute successfully
	SessionBean bean = null;
        if (ok) {
            Object object = session.getAttribute("sessionBean");
            if (object == null) {
                writer.println("Session03 FAILED - Cannot retrieve attribute");
                ok = false;
            } else if (!(object instanceof SessionBean)) {
                writer.println("Session03 FAILED - Attribute instance of " +
                               object.getClass().getName());
                ok = false;
            } else {
                bean = (SessionBean) object;
                String value = bean.getStringProperty();
                if (!"Session01".equals(value)) {
                    writer.println("Session03 FAILED - Property = " + value);
                    ok = false;
                }
            }
        }

        // Remove the attribute and guarantee that this was successful
        if (ok) {
            session.removeAttribute("sessionBean");
            if (session.getAttribute("sessionBean") != null) {
                writer.println("Session03 FAILED - Removal failed");
                ok = false;
            }
        }

	// Validate the bean lifecycle of this bean
	if (ok) {
	    String lifecycle = bean.getLifecycle();
	    if (!"/vb/swp/sda/vu".equals(lifecycle)) {
	        writer.println("Session03 FAILED - Invalid bean lifecycle '" +
			       lifecycle + "'");
		ok = false;
	    }
	}

        // Retrieve and validate the shared session bean
        SharedSessionBean ssb = null;
        if (ok) {
            Object object = session.getAttribute("sharedSessionBean");
            if (object == null) {
                writer.println("Session03 FAILED - Cannot retrieve ssb");
                ok = false;
            } else if (!(object instanceof SharedSessionBean)) {
                writer.println("Session03 FAILED - Shared attribute class "
                               + object.getClass().getName());
                ok = false;
            } else {
                ssb = (SharedSessionBean) object;
                String value = ssb.getStringProperty();
                if (!"Session01".equals(value)) {
                    writer.println("Session03 FAILED - Shared property ="
                                   + value);
                    ok = false;
                } else {
                    session.removeAttribute("sharedSessionBean");
                    String lifecycle = ssb.getLifecycle();
                    if (!"/vb/swp/sda/vu".equals(lifecycle)) {
                        writer.println("Session03 FAILED - Shared lifecycle ="
                                       + lifecycle);
                        ok = false;
                    }
                }
            }
        }

        // Retrieve and validate the unpacked shared session bean
        UnpSharedSessionBean ussb = null;
        if (ok) {
            Object object = session.getAttribute("unpSharedSessionBean");
            if (object == null) {
                writer.println("Session03 FAILED - Cannot retrieve ussb");
                ok = false;
            } else if (!(object instanceof UnpSharedSessionBean)) {
                writer.println("Session03 FAILED - unpShared attribute class "
                               + object.getClass().getName());
                ok = false;
            } else {
                ussb = (UnpSharedSessionBean) object;
                String value = ussb.getStringProperty();
                if (!"Session01".equals(value)) {
                    writer.println("Session03 FAILED - unpShared property ="
                                   + value);
                    ok = false;
                } else {
                    session.removeAttribute("unpSharedSessionBean");
                    String lifecycle = ssb.getLifecycle();
                    if (!"/vb/swp/sda/vu".equals(lifecycle)) {
                        writer.println("Session03 FAILED - unpShared lifecycle ="
                                       + lifecycle);
                        ok = false;
                    }
                }
            }
        }

        // Retrieve and validate the unshared session bean
        UnsharedSessionBean usb = null;
        if (ok) {
            Object object = session.getAttribute("unsharedSessionBean");
            if (object == null) {
                writer.println("Session03 FAILED - Cannot retrieve usb");
                ok = false;
            } else if (!(object instanceof UnsharedSessionBean)) {
                writer.println("Session03 FAILED - Unshared attribute class "
                               + object.getClass().getName());
                ok = false;
            } else {
                usb = (UnsharedSessionBean) object;
                String value = usb.getStringProperty();
                if (!"Session01".equals(value)) {
                    writer.println("Session03 FAILED - Unshared property = "
                                   + value);
                    ok = false;
                } else {
                    session.removeAttribute("unsharedSessionBean");
                    String lifecycle = usb.getLifecycle();
                    if (!"/vb/swp/sda/vu".equals(lifecycle)) {
                        writer.println("Session03 FAILED - Unshared lifecycle"
                                       + " = " + lifecycle);
                        ok = false;
                    }
                }
            }
        }


        // Report success if everything is still ok
        if (ok)
            writer.println("Session03 PASSED");
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
