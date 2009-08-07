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
 * Tester request wrapper that logs all calls to the configured logger,
 * before passing them on to the underlying request.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class TesterServletRequestWrapper extends ServletRequestWrapper {


    // ------------------------------------------------------------ Constructor


    /**
     * Configure a new request wrapper.
     *
     * @param request The request we are wrapping
     */
    public TesterServletRequestWrapper(ServletRequest request) {

        super(request);

    }


    // --------------------------------------------------------- Public Methods


    // For each public method, log the call and pass it to the wrapped response


    public Object getAttribute(String name) {
        StaticLogger.write("TesterServletRequestWrapper.getAttribute()");
        return (getRequest().getAttribute(name));
    }


    public Enumeration getAttributeNames() {
        StaticLogger.write("TesterServletRequestWrapper.getAttributeNames()");
        return (getRequest().getAttributeNames());
    }


    public String getCharacterEncoding() {
        StaticLogger.write("TesterServletRequestWrapper.getCharacterEncoding()");
        return (getRequest().getCharacterEncoding());
    }


    public int getContentLength() {
        StaticLogger.write("TesterServletRequestWrapper.getContentLength()");
        return (getRequest().getContentLength());
    }


    public String getContentType() {
        StaticLogger.write("TesterServletRequestWrapper.getContentType()");
        return (getRequest().getContentType());
    }


    public ServletInputStream getInputStream() throws IOException {
        StaticLogger.write("TesterServletRequestWrapper.getInputStream()");
        return (getRequest().getInputStream());
    }


    public Locale getLocale() {
        StaticLogger.write("TesterServletRequestWrapper.getLocale()");
        return (getRequest().getLocale());
    }


    public Enumeration getLocales() {
        StaticLogger.write("TesterServletRequestWrapper.getLocales()");
        return (getRequest().getLocales());
    }


    public String getParameter(String name) {
        StaticLogger.write("TesterServletRequestWrapper.getParameter()");
        return (getRequest().getParameter(name));
    }


    public Map getParameterMap() {
        StaticLogger.write("TesterServletRequestWrapper.getParameterMap()");
        return (getRequest().getParameterMap());
    }


    public Enumeration getParameterNames() {
        StaticLogger.write("TesterServletRequestWrapper.getParameterNames()");
        return (getRequest().getParameterNames());
    }


    public String[] getParameterValues(String name) {
        StaticLogger.write("TesterServletRequestWrapper.getParameterValues()");
        return (getRequest().getParameterValues(name));
    }


    public String getProtocol() {
        StaticLogger.write("TesterServletRequestWrapper.getProtocol()");
        return (getRequest().getProtocol());
    }


    public BufferedReader getReader() throws IOException {
        StaticLogger.write("TesterServletRequestWrapper.getReader()");
        return (getRequest().getReader());
    }


    public String getRealPath(String path) {
        StaticLogger.write("TesterServletRequestWrapper.getRealPath()");
        return (getRequest().getRealPath(path));
    }


    public String getRemoteAddr() {
        StaticLogger.write("TesterServletRequestWrapper.getRemoteAddr()");
        return (getRequest().getRemoteAddr());
    }


    public String getRemoteHost() {
        StaticLogger.write("TesterServletRequestWrapper.getRemoteHost()");
        return (getRequest().getRemoteHost());
    }


    public RequestDispatcher getRequestDispatcher(String path) {
        StaticLogger.write("TesterServletRequestWrapper.getRequestDispatcher()");
        return (getRequest().getRequestDispatcher(path));
    }


    public String getScheme() {
        StaticLogger.write("TesterServletRequestWrapper.getScheme()");
        return (getRequest().getScheme());
    }


    public String getServerName() {
        StaticLogger.write("TesterServletRequestWrapper.getServerName()");
        return (getRequest().getServerName());
    }


    public int getServerPort() {
        StaticLogger.write("TesterServletRequestWrapper.getServerPort()");
        return (getRequest().getServerPort());
    }


    public boolean isSecure() {
        StaticLogger.write("TesterServletRequestWrapper.isSecure()");
        return (getRequest().isSecure());
    }


    public void removeAttribute(String name) {
        StaticLogger.write("TesterServletRequestWrapper.removeAttribute()");
        getRequest().removeAttribute(name);
    }


    public void setAttribute(String name, Object value) {
        StaticLogger.write("TesterServletRequestWrapper.setAttribute()");
        getRequest().setAttribute(name, value);
    }


    public void setCharacterEncoding(String enc)
        throws UnsupportedEncodingException {
        StaticLogger.write("TesterServletRequestWrapper.setCharacterEncoding()");
        getRequest().setCharacterEncoding(enc);
    }





}
