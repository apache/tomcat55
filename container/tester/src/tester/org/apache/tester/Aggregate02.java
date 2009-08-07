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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Test aggregation of query string and POST parameters.  According to
 * Servlet 2.4 PFD, Section 4.1, all such parameters should be aggregated,
 * and if there are duplicate parameter names from both sources, the
 * parameter value(s) from the query string should appear first in the
 * values returned by request.getParameterValues().
 * <p>
 * This test is the same as Aggregate01, except that it uses the new
 * <code>getParameterMap()</code> method to retrieve parameter values.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class Aggregate02 extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        // Accumulate any errors that are noticed
        StringBuffer errors = new StringBuffer();
        Map map = request.getParameterMap();
        if (map == null) {
            errors.append("  No parameter map returned.");
            map = new HashMap();
        }
        String values[] = (String[]) map.get("a");
        if (values == null)
            errors.append("  Received no parameter values for 'a'.");
        else if (values.length != 2)
            errors.append("  Received " + values.length +
                          " parameter values for 'a' instead of 2.");
        else {
            if (!"1".equals(values[0]))
                errors.append("  First value for 'a' was '" + values[0] +
                              "' instead of '1'.");
            if (!"2".equals(values[1]))
                errors.append("  Second value for 'a' was '" + values[1] +
                              "' instead of '2'.");
        }
        values = (String[]) map.get("b");
        if (values == null)
            errors.append("  Received no parameter values for 'b'.");
        else if (values.length != 1)
            errors.append("  Received " + values.length +
                          " parameter values for 'b' instead of 1.");
        else {
            if (!"3".equals(values[0]))
                errors.append("  Value for 'b' was '" + values[0] +
                              "' instead of '3'.");
        }
        Iterator names = map.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            if ("a".equals(name))
                continue;
            if ("b".equals(name))
                continue;
            errors.append("  Received parameter '" + name + "'.");
        }

        // Report the results
        if (errors.length() < 1)
            writer.println("Aggregate02 PASSED");
        else
            writer.println("Aggregate02 FAILED -" + errors.toString());
        while (true) {
            String message = StaticLogger.read();
            if (message == null)
                break;
            writer.println(message);
        }
        StaticLogger.reset();

    }

}
