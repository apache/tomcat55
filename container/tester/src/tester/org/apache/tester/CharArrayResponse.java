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
 * upper case via an intermediate buffer.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class CharArrayResponse extends HttpServletResponseWrapper {


    CharArrayWriterUpperCase writer = null;

    public CharArrayResponse(HttpServletResponse response) {
        super(response);
        writer = new CharArrayWriterUpperCase();
    }

    public void flushBuffer() throws IOException {
        int n = 0;
        Reader reader = getReader();
        PrintWriter writer = getResponse().getWriter();
        while (true) {
            int ch = reader.read();
            if (ch < 0)
                break;
            n++;
            writer.print((char) ch);
        }
        writer.println("[" + n + "]");
        this.writer.reset();
    }

    public Reader getReader() {
        return (new CharArrayReader(writer.toCharArray()));
    }

    public PrintWriter getWriter() throws IOException {
        return (new PrintWriter(writer, true));
    }


}



