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
 * This rule looks for a specific string
 * in the URL for a positive match.
 *
 * @author Yoav Shapira
 */
public class URLStringMatchRule extends BaseRule {
    /**
     * The target string that must be present
     * in the URL, may not be null.
     */
    private String targetString;

    /**
     * Sets the target string that must
     * be present in the URL.
     *
     * @param theTargetString The target string
     */
    public void setTargetString(String theTargetString) {
        if (theTargetString == null) {
            throw new IllegalArgumentException(
                "The target string cannot be null.");
        } else {
            targetString = theTargetString;
        }
    }

    /**
     * Returns the target string that must
     * be present in the URL.
     *
     * @return The target string
     */
    protected String getTargetString() {
        return targetString;
    }

    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest)
     *
     * Looks for the target string in the request URL.
     */
    public boolean matches(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        int index = requestUrl.indexOf(getTargetString());

        return (index > -1);
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

        buffer.append("Target string: ");
        buffer.append(getTargetString());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: URLStringMatchRule.java
