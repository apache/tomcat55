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
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Test retrieval of headers.  The client is expected to send two
 * "Accept-Language" headers.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class GetHeaders01 extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        ArrayList values = new ArrayList();
        Enumeration headers = request.getHeaders("Accept-Language");
        while (headers.hasMoreElements()) {
            String header = (String) headers.nextElement() + ",";
            while (true) {
                int comma = header.indexOf(",");
                if (comma < 0)
                    break;
                String value = header.substring(0, comma).trim();
                values.add(value);
                header = header.substring(comma + 1).trim();
            }
        }
        if (values.size() != 2)
            writer.println("GetHeaders01 FAILED - Returned " + values.size()
                           + " headers instead of 2");
        else if (values.get(0) == values.get(1))
            writer.println("GetHeaders01 FAILED - Returned identical values "
                           + values.get(0));
        else {
            int n = 0;
            for (int i = 0; i < values.size(); i++) {
                if ("en-us".equals((String) values.get(i)))
                    n++;
                else if ("en-gb".equals((String) values.get(i)))
                    n++;
            }
            if (n != 2)
                writer.println("GetHeaders01 FAILED - Returned unknown values "
                               + values.get(0) + " and " + values.get(1));
            else
                writer.println("GetHeaders01 PASSED");
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
