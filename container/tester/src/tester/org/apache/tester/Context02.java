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
 * Part 2 of Context Tests.  The context attribute from Context00 should
 * still be here after a restart (because Context00 is a load-on-startup
 * servlet, so the <code>init()</code> method should have been triggered
 * during the restart).  However, the context attribute from Context01
 * should <strong>not</strong> be here, because context attributes should
 * be cleaned up during a restart.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Context02 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        boolean ok = true;
        PrintWriter writer = response.getWriter();
        ServletContext context = getServletContext();

        // Check for the attribute from Context01
        if (ok) {
            Object bean = context.getAttribute("context01");
            if (bean != null) {
                writer.println("Context02 FAILED - context01 value " +
                               bean);
                ok = false;
                context.removeAttribute("context01");
            }
        }

        // Check for the attribute from Context00
        if (ok) {
            Object bean = context.getAttribute("context00");
            if (bean == null) {
                writer.println("Context02 FAILED - context00 missing");
                ok = false;
            } else if (!(bean instanceof ContextBean)) {
                writer.println("Context02 FAILED - context00 class " +
                               bean.getClass().getName());
                ok = false;
            } else {
                String value = ((ContextBean) bean).getStringProperty();
                if (!"Context00".equals(value)) {
                    writer.println("Context02 FAILED - context00 value " +
                                   value);
                    ok = false;
                } else {
                    String lifecycle = ((ContextBean) bean).getLifecycle();
                    if (!"/add".equals(lifecycle)) {
                        writer.println("Context02 FAILED -" +
                                       " context00 lifecycle " +
                                       lifecycle);
                        ok = false;
                    }
                }
            }
        }

        // Check for the attribute from ContextListener01
        if (ok) {
            Object bean = context.getAttribute("contextListener01");
            if (bean == null) {
                writer.println("Context02 FAILED - contextListener01 missing");
                ok = false;
            } else if (!(bean instanceof ContextBean)) {
                writer.println("Context02 FAILED - contextListener01 class " +
                               bean.getClass().getName());
                ok = false;
            } else {
                String value = ((ContextBean) bean).getStringProperty();
                if (!"ContextListener01".equals(value)) {
                    writer.println("Context02 FAILED - contextListener01 " +
                                   "value " + value);
                    ok = false;
                } else {
                    String lifecycle = ((ContextBean) bean).getLifecycle();
                    if (!"/add".equals(lifecycle)) {
                        writer.println("Context02 FAILED -" +
                                       " contextListener01 lifecycle " +
                                       lifecycle);
                        ok = false;
                    }
                }
            }
        }

        // Report success if everything is still ok
        if (ok)
            writer.println("Context02 PASSED");
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
