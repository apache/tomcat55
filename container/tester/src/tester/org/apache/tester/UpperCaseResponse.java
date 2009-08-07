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
 * HttpServletResponse wrapper that converts all output characters to
 * upper case.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class UpperCaseResponse extends HttpServletResponseWrapper {


    HttpServletResponse response = null;

    boolean stream = false; // Wrap our own output stream

    public UpperCaseResponse(HttpServletResponse response) {
        this(response, false);
    }

    public UpperCaseResponse(HttpServletResponse response, boolean stream) {
        super(response);
        this.response = response;
        this.stream = stream;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return (new UpperCaseOutputStream(response.getOutputStream()));
    }

    public PrintWriter getWriter() throws IOException {
        if (stream)
            return (new PrintWriter(getOutputStream(), true));
        else
            return (new UpperCaseWriter(response.getWriter()));
    }


}



