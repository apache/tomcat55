/*
 * Copyright 2000,2004 The Apache Software Foundation.
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
package org.apache.webapp.balancer;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * The balancer filter redirects incoming requests
 * based on what rules they match.  The rules
 * are configurable via an XML document whose URL
 * is specified as an init-param to this filter.
 *
 * @author Yoav Shapira
 */
public class BalancerFilter implements Filter {
    /**
     * The rules this filter consults.
     */
    private RuleChain ruleChain;

    /**
     * The servlet context.
     */
    private ServletContext context;

    /**
     * Returns the rule chain.
     *
     * @return The rule chain
     */
    protected RuleChain getRuleChain() {
        return ruleChain;
    }

    /**
     * Initialize this filter.
     *
     * @param filterConfig The filter config
     * @throws ServletException If an error occurs
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();

        String configUrlParam = filterConfig.getInitParameter("configUrl");

        if (configUrlParam == null) {
            throw new ServletException("configUrl is required.");
        }

        try {
            InputStream input = context.getResourceAsStream(configUrlParam);
            RulesParser parser = new RulesParser(input);
            ruleChain = parser.getResult();
            context.log(
                getClass().getName() + ": init(): ruleChain: " + ruleChain);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Filter the incoming request.
     * Consults the rule chain to see if
     * any rules match this request, and if
     * so redirects.  Otherwise simply
     * let request through.
     *
     * @param request The request
     * @param response The response
     * @param chain The filter chain
     * @throws IOException If an error occurs
     * @throws ServletException If an error occurs
     */
    public void doFilter(
        ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (response.isCommitted()) {
            context.log(
                getClass().getName()
                + ": doFilter(): not inspecting committed response.");
            chain.doFilter(request, response);
        } else if (!(request instanceof HttpServletRequest)) {
            context.log(
                getClass().getName()
                + ": doFilter(): not inspecting non-Http request.");
            chain.doFilter(request, response);
        } else {
            HttpServletRequest hreq = (HttpServletRequest) request;
            HttpServletResponse hres = (HttpServletResponse) response;

            URL redirectUrl = getRuleChain().evaluate(hreq);

            if (redirectUrl != null) {
                String encoded =
                    hres.encodeRedirectURL(redirectUrl.toString());

                context.log(
                    getClass().getName()
                    + ": doFilter(): redirecting request for "
                    + hreq.getRequestURL().toString() + " to " + encoded);

                hres.sendRedirect(encoded);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    /**
     * Destroy this filter.
     */
    public void destroy() {
        context = null;
        ruleChain = null;
    }
}


// End of file: BalanceFilter.java
