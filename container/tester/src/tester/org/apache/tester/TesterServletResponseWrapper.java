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
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Tester response wrapper that logs all calls to the configured logger,
 * before passing them on to the underlying response.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class TesterServletResponseWrapper extends ServletResponseWrapper {


    // ------------------------------------------------------------ Constructor


    /**
     * Configure a new response wrapper.
     *
     * @param response The response we are wrapping
     */
    public TesterServletResponseWrapper(ServletResponse response) {

        super(response);

    }


    // --------------------------------------------------------- Public Methods


    // For each public method, log the call and pass it to the wrapped response


    public void flushBuffer() throws IOException {
        StaticLogger.write("TesterServletResponseWrapper.flushBuffer()");
        getResponse().flushBuffer();
    }


    public int getBufferSize() {
        StaticLogger.write("TesterServletResponseWrapper.getBufferSize()");
        return (getResponse().getBufferSize());
    }


    public String getCharacterEncoding() {
        StaticLogger.write("TesterServletResponseWrapper.getCharacterEncoding()");
        return (getResponse().getCharacterEncoding());
    }


    public Locale getLocale() {
        StaticLogger.write("TesterServletResponseWrapper.getLocale()");
        return (getResponse().getLocale());
    }


    public ServletOutputStream getOutputStream() throws IOException {
        StaticLogger.write("TesterServletResponseWrapper.getOutputStream()");
        return (getResponse().getOutputStream());
    }


    public PrintWriter getWriter() throws IOException {
        StaticLogger.write("TesterServletResponseWrapper.getWriter()");
        return (getResponse().getWriter());
    }


    public boolean isCommitted() {
        StaticLogger.write("TesterServletResponseWrapper.isCommitted()");
        return (getResponse().isCommitted());
    }


    public void reset() {
        StaticLogger.write("TesterServletResponseWrapper.reset()");
        getResponse().reset();
    }


    public void resetBuffer() {
        StaticLogger.write("TesterServletResponseWrapper.resetBuffer()");
        getResponse().resetBuffer();
    }


    public void setBufferSize(int size) {
        StaticLogger.write("TesterServletResponseWrapper.setBufferSize()");
        getResponse().setBufferSize(size);
    }


    public void setContentLength(int len) {
        StaticLogger.write("TesterServletResponseWrapper.setContentLength()");
        getResponse().setContentLength(len);
    }


    public void setContentType(String type) {
        StaticLogger.write("TesterServletResponseWrapper.setContentType()");
        getResponse().setContentType(type);
    }


    public void setLocale(Locale locale) {
        StaticLogger.write("TesterServletResponseWrapper.setLocale()");
        getResponse().setLocale(locale);
    }


}
