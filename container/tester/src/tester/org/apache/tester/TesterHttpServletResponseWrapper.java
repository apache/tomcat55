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

public class TesterHttpServletResponseWrapper
    extends HttpServletResponseWrapper {


    // ------------------------------------------------------------ Constructor


    /**
     * Configure a new response wrapper.
     *
     * @param response The response we are wrapping
     */
    public TesterHttpServletResponseWrapper(HttpServletResponse response) {

        super(response);

    }


    // --------------------------------------------------------- Public Methods


    // For each public method, log the call and pass it to the wrapped response


    public void addCookie(Cookie cookie) {
        StaticLogger.write("TesterHttpServletResponseWrapper.addCookie()");
        ((HttpServletResponse) getResponse()).addCookie(cookie);
    }


    public void addDateHeader(String name, long value) {
        StaticLogger.write("TesterHttpServletResponseWrapper.addDateHeader()");
        ((HttpServletResponse) getResponse()).addDateHeader(name, value);
    }


    public void addHeader(String name, String value) {
        StaticLogger.write("TesterHttpServletResponseWrapper.addHeader()");
        ((HttpServletResponse) getResponse()).addHeader(name, value);
    }


    public void addIntHeader(String name, int value) {
        StaticLogger.write("TesterHttpServletResponseWrapper.addIntHeader()");
        ((HttpServletResponse) getResponse()).addIntHeader(name, value);
    }


    public boolean containsHeader(String name) {
        StaticLogger.write("TesterHttpServletResponseWrapper.containsHeader()");
        return (((HttpServletResponse) getResponse()).containsHeader(name));
    }


    public String encodeURL(String url) {
        StaticLogger.write("TesterHttpServletResponseWrapper.encodeURL()");
        return (((HttpServletResponse) getResponse()).encodeURL(url));
    }


    public String encodeUrl(String url) {
        StaticLogger.write("TesterHttpServletResponseWrapper.encodeUrl()");
        return (((HttpServletResponse) getResponse()).encodeUrl(url));
    }


    public String encodeRedirectURL(String url) {
        StaticLogger.write("TesterHttpServletResponseWrapper.encodeRedirectURL()");
        return (((HttpServletResponse) getResponse()).encodeRedirectURL(url));
    }


    public String encodeRedirectUrl(String url) {
        StaticLogger.write("TesterHttpServletResponseWrapper.encodeRedirectUrl()");
        return (((HttpServletResponse) getResponse()).encodeRedirectUrl(url));
    }


    public void flushBuffer() throws IOException {
        StaticLogger.write("TesterHttpServletResponseWrapper.flushBuffer()");
        getResponse().flushBuffer();
    }


    public int getBufferSize() {
        StaticLogger.write("TesterHttpServletResponseWrapper.getBufferSize()");
        return (getResponse().getBufferSize());
    }


    public String getCharacterEncoding() {
        StaticLogger.write("TesterHttpServletResponseWrapper.getCharacterEncoding()");
        return (getResponse().getCharacterEncoding());
    }


    public Locale getLocale() {
        StaticLogger.write("TesterHttpServletResponseWrapper.getLocale()");
        return (getResponse().getLocale());
    }


    public ServletOutputStream getOutputStream() throws IOException {
        StaticLogger.write("TesterHttpServletResponseWrapper.getOutputStream()");
        return (getResponse().getOutputStream());
    }


    public PrintWriter getWriter() throws IOException {
        StaticLogger.write("TesterHttpServletResponseWrapper.getWriter()");
        return (getResponse().getWriter());
    }


    public boolean isCommitted() {
        StaticLogger.write("TesterHttpServletResponseWrapper.isCommitted()");
        return (getResponse().isCommitted());
    }


    public void reset() {
        StaticLogger.write("TesterHttpServletResponseWrapper.reset()");
        getResponse().reset();
    }


    public void resetBuffer() {
        StaticLogger.write("TesterHttpServletResponseWrapper.resetBuffer()");
        getResponse().resetBuffer();
    }


    public void sendError(int sc) throws IOException {
        StaticLogger.write("TesterHttpServletResponseWrapper.sendError(i)");
        ((HttpServletResponse) getResponse()).sendError(sc);
    }


    public void sendError(int sc, String msg) throws IOException {
        StaticLogger.write("TesterHttpServletResponseWrapper.sendError(i,s)");
        ((HttpServletResponse) getResponse()).sendError(sc, msg);
    }


    public void sendRedirect(String location) throws IOException {
        StaticLogger.write("TesterHttpServletResponseWrapper.sendRedirect()");
        ((HttpServletResponse) getResponse()).sendRedirect(location);
    }


    public void setBufferSize(int size) {
        StaticLogger.write("TesterHttpServletResponseWrapper.setBufferSize()");
        getResponse().setBufferSize(size);
    }


    public void setContentLength(int len) {
        StaticLogger.write("TesterHttpServletResponseWrapper.setContentLength()");
        getResponse().setContentLength(len);
    }


    public void setContentType(String type) {
        StaticLogger.write("TesterHttpServletResponseWrapper.setContentType()");
        getResponse().setContentType(type);
    }


    public void setDateHeader(String name, long value) {
        StaticLogger.write("TesterHttpServletResponseWrapper.setDateHeader()");
        ((HttpServletResponse) getResponse()).setDateHeader(name, value);
    }


    public void setHeader(String name, String value) {
        StaticLogger.write("TesterHttpServletResponseWrapper.setHeader()");
        ((HttpServletResponse) getResponse()).setHeader(name, value);
    }


    public void setIntHeader(String name, int value) {
        StaticLogger.write("TesterHttpServletResponseWrapper.setIntHeader()");
        ((HttpServletResponse) getResponse()).setIntHeader(name, value);
    }


    public void setLocale(Locale locale) {
        StaticLogger.write("TesterHttpServletResponseWrapper.setLocale()");
        getResponse().setLocale(locale);
    }


    public void setStatus(int sc) {
        StaticLogger.write("TesterHttpServletResponseWrapper.setStatus(i)");
        ((HttpServletResponse) getResponse()).setStatus(sc);
    }


    public void setStatus(int sc, String msg) {
        StaticLogger.write("TesterHttpServletResponseWrapper.setStatus(i,s)");
        ((HttpServletResponse) getResponse()).setStatus(sc, msg);
    }


}
