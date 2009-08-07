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
package org.apache.webapp.balancer.rules;

import javax.servlet.http.HttpServletRequest;


/**
 * This rule checks for the presence
 * of a specific request header, optionally
 * with a specific value.  The value may
 * be null.
 *
 * @author Yoav Shapira
 */
public class RequestHeaderRule extends BaseRule {
    /**
     * The header name, cannot be null.
     */
    private String headerName;

    /**
     * The header value.  This may be
     * null to indicate any value is OK.
     */
    private String headerValue;

    /**
     * Sets the header name.
     *
     * @param theName The name
     */
    public void setHeaderName(String theName) {
        if (theName == null) {
            throw new IllegalArgumentException(
                "The header name cannot be null.");
        } else {
            headerName = theName;
        }
    }

    /**
     * Returns the header name to match.
     *
     * @return The header name
     */
    protected String getHeaderName() {
        return headerName;
    }

    /**
     * Sets the header value.
     *
     * @param theValue The header value
     */
    public void setHeaderValue(String theValue) {
        headerValue = theValue;
    }

    /**
     * Returns the desired header value,
     * which may be null.
     *
     * @return String
     */
    protected String getHeaderValue() {
        return headerValue;
    }

    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest request)
     */
    public boolean matches(HttpServletRequest request) {
        String actualHeaderValue = request.getHeader(getHeaderName());

        if (actualHeaderValue == null) {
            return (getHeaderValue() == null);
        } else {
            return (actualHeaderValue.compareTo(getHeaderValue()) == 0);
        }
    }

    /**
     * Returns a String representation of this object.
     *
     * @return String
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[");
        buffer.append(getClass().getName());
        buffer.append(": ");

        buffer.append("Header name: ");
        buffer.append(getHeaderName());
        buffer.append(" / ");

        buffer.append("Header value: ");
        buffer.append(getHeaderValue());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: RequestHeaderRule.java
