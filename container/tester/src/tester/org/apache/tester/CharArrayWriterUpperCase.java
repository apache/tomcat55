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
 * Implementation of CharArrayWriter that upper cases its output.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class CharArrayWriterUpperCase extends CharArrayWriter {


    CharArrayWriter writer = new CharArrayWriter();

    public void close() {
        writer.close();
    }

    public void flush() {
        writer.flush();
    }

    public void reset() {
        writer.reset();
    }

    public int size() {
        return (writer.size());
    }

    public char[] toCharArray() {
        return (writer.toCharArray());
    }

    public String toString() {
        return (writer.toString());
    }

    public void write(int c) {
        char ch = (char) c;
        if (Character.isLowerCase(ch))
            ch = Character.toUpperCase(ch);
        writer.write((int) ch);
    }

    public void write(char c[]) throws IOException {
        write(c, 0, c.length);
    }

    public void write(char c[], int off, int len) {
        for (int i = off; i < (off + len); i++)
            write(c[i]);
    }

    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    public void write(String str, int off, int len) {
        for (int i = off; i < (off + len); i++)
            write(str.charAt(i));
    }

    public void writeTo(Writer out) throws IOException {
        writer.writeTo(out);
    }


}
