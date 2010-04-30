/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
 * Test to insure that an included servlet can set request attributes that are
 * visible to the calling servlet after the <code>include()</code> returns.
 * The spec is silent on this topic, but it seems consistent with the overall
 * intent to behave in this manner.
 *
 * @author Craig R. McClanahan
 * @version $Id$
 */

public class Include03a extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        request.setAttribute("Include03a", "This is a new attribute");

    }

}
