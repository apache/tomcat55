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
 * Simple filter to reset the static log at the beginning of each request,
 * so that no leftovers from the previous request are inadvertently included.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class StaticFilter implements Filter {


    // ----------------------------------------------------------- Constructors



    // ----------------------------------------------------- Instance Variables


    /**
     * The filter configuration object for this filter.
     */
    protected FilterConfig config = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Release this Filter instance from service.
     */
    public void destroy() {

        config = null;

    }


    /**
     * Wrap this request and/or response as configured and pass it on.
     */
    public void doFilter(ServletRequest inRequest, ServletResponse inResponse,
                         FilterChain chain)
        throws IOException, ServletException {

        // Reset our logger and perform this request
        StaticLogger.reset();
        chain.doFilter(inRequest, inResponse);

    }


    /**
     * Place this Filter instance into service.
     *
     * @param config The filter configuration object
     */
    public void init(FilterConfig config) throws ServletException {

        this.config = config;

    }


}
