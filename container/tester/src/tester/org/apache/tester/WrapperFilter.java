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
 * Configurable filter that will wrap the request and/or response objects
 * it passes on with either generic or HTTP-specific wrappers.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class WrapperFilter implements Filter {


    // ----------------------------------------------------------- Constructors



    // ----------------------------------------------------- Instance Variables


    /**
     * The filter configuration object for this filter.
     */
    protected FilterConfig config = null;


    /**
     * The type of wrapper for each request ("none", "generic", "http").
     */
    protected String requestWrapper = "none";


    /**
     * The type of wrapper for each response ("none", "generic", "http").
     */
    protected String responseWrapper = "none";


    // --------------------------------------------------------- Public Methods


    /**
     * Release this Filter instance from service.
     */
    public void destroy() {

        config = null;
        requestWrapper = "none";
        responseWrapper = "none";

    }


    /**
     * Wrap this request and/or response as configured and pass it on.
     */
    public void doFilter(ServletRequest inRequest, ServletResponse inResponse,
                         FilterChain chain)
        throws IOException, ServletException {

        // Create the appropriate wrappers
        ServletRequest outRequest = inRequest;
        ServletResponse outResponse = inResponse;
        if (requestWrapper.equals("generic")) {
            outRequest = new TesterServletRequestWrapper(inRequest);
        } else if (requestWrapper.equals("http")) {
            outRequest = new TesterHttpServletRequestWrapper
                ((HttpServletRequest) inRequest);
        }
        if (responseWrapper.equals("generic")) {
            outResponse = new TesterServletResponseWrapper(inResponse);
        } else if (responseWrapper.equals("http")) {
            outResponse = new TesterHttpServletResponseWrapper
                ((HttpServletResponse) inResponse);
        }

        // Perform this request
        chain.doFilter(outRequest, outResponse);

    }


    /**
     * Place this Filter instance into service.
     *
     * @param config The filter configuration object
     */
    public void init(FilterConfig config) throws ServletException {

        this.config = config;
        String value = null;
        value = config.getInitParameter("request");
        if (value != null)
            requestWrapper = value;
        value = config.getInitParameter("response");
        if (value != null)
            responseWrapper = value;

    }


}
