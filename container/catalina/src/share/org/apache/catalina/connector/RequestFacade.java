/*
 * Copyright 1999,2004 The Apache Software Foundation.
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


package org.apache.catalina.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Facade class that wraps a Coyote request object.  
 * All methods are delegated to the wrapped request.
 *
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 * @author Jean-Francois Arcand
 * @version $Revision$ $Date$
 */


public class RequestFacade 
    implements HttpServletRequest {
        
        
    // ----------------------------------------------------------- DoPrivileged
    
    private final class GetAttributePrivilegedAction implements PrivilegedAction{
        
        public Object run() {
            return request.getAttributeNames();
        }            
    }
     
    
    private final class GetParameterMapPrivilegedAction implements PrivilegedAction{
        
        public Object run() {
            return request.getParameterMap();
        }        
    }    
    
    
    private final class GetRequestDispatcherPrivilegedAction implements PrivilegedAction{
        private String path;
        public GetRequestDispatcherPrivilegedAction(String path){
            this.path = path;
        }
        
        public Object run() {   
            return request.getRequestDispatcher(path);
       }           
    }    
    
    
    private final class GetParameterPrivilegedAction implements PrivilegedAction{
        public String name;
        public GetParameterPrivilegedAction(String name){
            this.name = name;
        }
        public Object run() {       
            return request.getParameter(name);
        }           
    }    
    
     
    private final class GetParameterNamesPrivilegedAction implements PrivilegedAction{
        
        public Object run() {          
            return request.getParameterNames();
        }           
    } 
    
    
    private final class GetParameterValuePrivilegedAction implements PrivilegedAction{
        public String name;
        public GetParameterValuePrivilegedAction(String name){
            this.name = name;
        }
        public Object run() {       
            return request.getParameterValues(name);
        }           
    }    
  
    
    private final class GetCookiesPrivilegedAction implements PrivilegedAction{
        
       public Object run() {       
            return request.getCookies();
        }           
    }      
    
    
    private final class GetCharacterEncodingPrivilegedAction implements PrivilegedAction{
        
        public Object run() {       
            return request.getCharacterEncoding();
        }           
    }   
        
    
    private final class GetHeadersPrivilegedAction implements PrivilegedAction{
        private String name;
        public GetHeadersPrivilegedAction(String name){
            this.name = name;
        }
        
        public Object run() {       
            return request.getHeaders(name);
        }           
    }    
        
    
    private final class GetHeaderNamesPrivilegedAction implements PrivilegedAction{

        public Object run() {       
            return request.getHeaderNames();
        }           
    }  
            
    
    private final class GetLocalePrivilegedAction implements PrivilegedAction{

        public Object run() {       
            return request.getLocale();
        }           
    }    
            
    
    private final class GetLocalesPrivilegedAction implements PrivilegedAction{

        public Object run() {       
            return request.getLocales();
        }           
    }    
    
    private final class GetSessionPrivilegedAction implements PrivilegedAction{
        private boolean create;
        
        public GetSessionPrivilegedAction(boolean create){
            this.create = create;
        }
                
        public Object run() {  
            return request.getSession(create);
        }           
    }      
    // ----------------------------------------------------------- Constructors


    /**
     * Construct a wrapper for the specified request.
     *
     * @param request The request to be wrapped
     */
    public RequestFacade(Request request) {

        this.request = request;

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The wrapped request.
     */
    protected Request request = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Clear facade.
     */
    public void clear() {
        request = null;
    }


    // ------------------------------------------------- ServletRequest Methods


    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }


    public Enumeration getAttributeNames() {
        if (System.getSecurityManager() != null){
            return (Enumeration)AccessController.doPrivileged(
                new GetAttributePrivilegedAction());        
        } else {
            return request.getAttributeNames();
        }
    }


    public String getCharacterEncoding() {
        if (System.getSecurityManager() != null){
            return (String)AccessController.doPrivileged(
                new GetCharacterEncodingPrivilegedAction());
        } else {
            return request.getCharacterEncoding();
        }         
    }


    public void setCharacterEncoding(String env)
        throws java.io.UnsupportedEncodingException {
        request.setCharacterEncoding(env);
    }


    public int getContentLength() {
        return request.getContentLength();
    }


    public String getContentType() {
        return request.getContentType();
    }


    public ServletInputStream getInputStream()
        throws IOException {
        return request.getInputStream();
    }


    public String getParameter(String name) {
        if (System.getSecurityManager() != null){
            return (String)AccessController.doPrivileged(
                new GetParameterPrivilegedAction(name));
        } else {
            return request.getParameter(name);
        }
    }


    public Enumeration getParameterNames() {
        if (System.getSecurityManager() != null){
            return (Enumeration)AccessController.doPrivileged(
                new GetParameterNamesPrivilegedAction());
        } else {
            return request.getParameterNames();
        }
    }


    public String[] getParameterValues(String name) {

        String[] ret = null;

        /*
         * Clone the returned array only if there is a security manager
         * in place, so that performance won't suffer in the nonsecure case
         */
        if (System.getSecurityManager() != null){
            ret = (String[]) AccessController.doPrivileged(
                new GetParameterValuePrivilegedAction(name));
            if (ret != null) {
                ret = (String[]) ret.clone();
            }
        } else {
            ret = request.getParameterValues(name);
        }

        return ret;
    }


    public Map getParameterMap() {
        if (System.getSecurityManager() != null){
            return (Map)AccessController.doPrivileged(
                new GetParameterMapPrivilegedAction());        
        } else {
            return request.getParameterMap();
        }
    }


    public String getProtocol() {
        return request.getProtocol();
    }


    public String getScheme() {
        return request.getScheme();
    }


    public String getServerName() {
        return request.getServerName();
    }


    public int getServerPort() {
        return request.getServerPort();
    }


    public BufferedReader getReader()
        throws IOException {
        return request.getReader();
    }


    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }


    public String getRemoteHost() {
        return request.getRemoteHost();
    }


    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }


    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }


    public Locale getLocale() {
        if (System.getSecurityManager() != null){
            return (Locale)AccessController.doPrivileged(
                new GetLocalePrivilegedAction());
        } else {
            return request.getLocale();
        }        
    }


    public Enumeration getLocales() {
        if (System.getSecurityManager() != null){
            return (Enumeration)AccessController.doPrivileged(
                new GetLocalesPrivilegedAction());
        } else {
            return request.getLocales();
        }        
    }


    public boolean isSecure() {
        return request.isSecure();
    }


    public RequestDispatcher getRequestDispatcher(String path) {
        if (System.getSecurityManager() != null){
            return (RequestDispatcher)AccessController.doPrivileged(
                new GetRequestDispatcherPrivilegedAction(path));
        } else {
            return request.getRequestDispatcher(path);
        }
    }


    public String getRealPath(String path) {
        return request.getRealPath(path);
    }


    public String getAuthType() {
        return request.getAuthType();
    }


    public Cookie[] getCookies() {

        Cookie[] ret = null;

        /*
         * Clone the returned array only if there is a security manager
         * in place, so that performance won't suffer in the nonsecure case
         */
        if (System.getSecurityManager() != null){
            ret = (Cookie[])AccessController.doPrivileged(
                new GetCookiesPrivilegedAction());
            if (ret != null) {
                ret = (Cookie[]) ret.clone();
            }
        } else {
            ret = request.getCookies();
        }

        return ret;
    }


    public long getDateHeader(String name) {
        return request.getDateHeader(name);
    }


    public String getHeader(String name) {
        return request.getHeader(name);
    }


    public Enumeration getHeaders(String name) {
        if (System.getSecurityManager() != null){
            return (Enumeration)AccessController.doPrivileged(
                new GetHeadersPrivilegedAction(name));
        } else {
            return request.getHeaders(name);
        }         
    }


    public Enumeration getHeaderNames() {
        if (System.getSecurityManager() != null){
            return (Enumeration)AccessController.doPrivileged(
                new GetHeaderNamesPrivilegedAction());
        } else {
            return request.getHeaderNames();
        }             
    }


    public int getIntHeader(String name) {
        return request.getIntHeader(name);
    }


    public String getMethod() {
        return request.getMethod();
    }


    public String getPathInfo() {
        return request.getPathInfo();
    }


    public String getPathTranslated() {
        return request.getPathTranslated();
    }


    public String getContextPath() {
        return request.getContextPath();
    }


    public String getQueryString() {
        return request.getQueryString();
    }


    public String getRemoteUser() {
        return request.getRemoteUser();
    }


    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }


    public java.security.Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }


    public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }


    public String getRequestURI() {
        return request.getRequestURI();
    }


    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }


    public String getServletPath() {
        return request.getServletPath();
    }


    public HttpSession getSession(boolean create) {

        if (System.getSecurityManager() != null){
            return (HttpSession)AccessController.
                doPrivileged(new GetSessionPrivilegedAction(create));
        } else {
            return request.getSession(create);
        }
    }

    public HttpSession getSession() {
        return getSession(true);
    }


    public boolean isRequestedSessionIdValid() {
        return request.isRequestedSessionIdValid();
    }


    public boolean isRequestedSessionIdFromCookie() {
        return request.isRequestedSessionIdFromCookie();
    }


    public boolean isRequestedSessionIdFromURL() {
        return request.isRequestedSessionIdFromURL();
    }


    public boolean isRequestedSessionIdFromUrl() {
        return request.isRequestedSessionIdFromURL();
    }


    public String getLocalAddr() {
		return request.getLocalAddr();
	}


	public String getLocalName() {
		return request.getLocalName();
	}


	public int getLocalPort() {
		return request.getLocalPort();
	}


	public int getRemotePort() {
		return request.getRemotePort();
	}

}
