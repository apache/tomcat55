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
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Test getting the servlet input stream after we have retrieved the reader.
 * This should throw an IllegalStateException.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class GetParameterMap00 extends GenericServlet {

    public void service(ServletRequest request, ServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        String errors = "";
        Map map = request.getParameterMap();
        if (map == null)
            errors += "  getParameterMap() returned null.";
        else {
            if (map.size() != 2)
                errors += "  map size is " + map.size() + ".";
            String value1[] = (String[]) map.get("BestLanguage");
            if (value1 == null)
                errors += "  BestLanguage is NULL.";
            else if (value1.length != 1)
                errors += "  BestLanguage has " + value1.length + " values.";
            else if (!value1[0].equals("Java"))
                errors += "  BestLanguage is " + value1 + ".";
            String value2[] = (String[]) map.get("BestJSP");
            if (value2 == null)
                errors += "  BestJSP is NULL.";
            else if (value2.length != 1)
                errors += "  BestJSP has " + value2.length + " values.";
            else if (!value2[0].equals("Java2"))
                errors += "  BestJSP is " + value2 + ".";
            try {
                map.put("ABC", "XYZ");
                errors += "   map.put() was allowed.";
                if (map.get("ABC") != null)
                    errors += "  map is not immutable.";
            } catch (Throwable t) {
                ;
            }
        }

        if (errors.equals(""))
            writer.println("GetParameterMap00 PASSED");
        else {
            writer.println("GetParameterMap00 FAILED:" + errors);
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
