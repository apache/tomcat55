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
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Part 2 of the request locale tests.  Should receive a Locale that
 * corresponds to "en_CA" and then "en_GB" as sent by the client in
 * "Accept-Language" headers.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class GetLocales02 extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        response.reset();
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        StringBuffer sb = new StringBuffer();
        boolean ok = true;

        Enumeration locales = request.getLocales();
        if (locales == null) {
            sb.append(" No locales returned/");
            ok = false;
        }

        if (ok) {
            if (locales.hasMoreElements()) {
                Locale expected = new Locale("en", "CA");
                Locale received = (Locale) locales.nextElement();
                if (!expected.equals(received)) {
                    sb.append(" Expected1='" +
                           expected.toString() + "' Received1='" +
                           received.toString() + "'");
                }
            } else {
                sb.append(" Zero locales returned/");
                ok = false;
            }
        }

        if (ok) {
            if (locales.hasMoreElements()) {
                Locale expected = new Locale("en", "GB");
                Locale received = (Locale) locales.nextElement();
                if (!expected.equals(received)) {
                    sb.append(" Expected2='" +
                           expected.toString() + "' Received2='" +
                           received.toString() + "'");
                }
            } else {
                sb.append(" One locale returned/");
                ok = false;
            }
        }

        if (ok) {
            if (locales.hasMoreElements()) {
                sb.append(" More than two locales returned/");
                ok = false;
            }
        }

        if (ok && (sb.length() < 1)) {
            writer.println("GetLocales02 PASSED");
        } else {
            writer.print("GetLocales02 FAILED -");
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
