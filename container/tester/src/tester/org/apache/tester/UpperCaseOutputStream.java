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
 * ServletOutputStream that converts all characters to upper case.
 * WARNING:  This will only work on 8-bit character sets!
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class UpperCaseOutputStream extends ServletOutputStream {

    ServletOutputStream stream = null;

    public UpperCaseOutputStream(ServletOutputStream stream)
      throws IOException {
        super();
        this.stream = stream;
    }

    public void write(int c) throws IOException {
        char ch = (char) c;
        if (Character.isLowerCase(ch))
            ch = Character.toUpperCase(ch);
        stream.write((int) ch);
    }

    public void write(byte buf[], int off, int len) throws IOException {
        for (int i = off; i < (off + len); i++) {
            char ch = (char) buf[i];
            if (Character.isLowerCase(ch))
                ch = Character.toUpperCase(ch);
            stream.write((int) ch);
        }
    }

    public void write(byte buf[]) throws IOException {
        write(buf, 0, buf.length);
    }

}

