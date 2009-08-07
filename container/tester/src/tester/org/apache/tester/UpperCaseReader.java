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
 * BufferedReader that converts all characters to upper case.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class UpperCaseReader extends BufferedReader {

    public UpperCaseReader(BufferedReader reader) throws IOException {
        super(reader);
    }

    public int read() throws IOException {
        int c = super.read();
        if (c < 0)
            return (c);
        char ch = (char) c;
        if (Character.isLowerCase(ch))
            ch = Character.toUpperCase(ch);
        return ((int) ch);
    }

    public int read(char buf[], int off, int len) throws IOException {
        int n = 0;
        for (int i = off; i < (off + len); i++) {
            int c = super.read();
            if (c < 0) {
                if (n == 0)
                    return (-1);
                break;
            }
            char ch = (char) c;
            if (Character.isLowerCase(ch))
                ch = Character.toUpperCase(ch);
            buf[i] = ch;
            n++;
        }
        return (n);
    }

    public int read(char buf[]) throws IOException {
        return (read(buf, 0, buf.length));
    }

}

