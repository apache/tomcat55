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

import org.apache.webapp.balancer.Rule;

import javax.servlet.http.HttpServletRequest;


/**
 * The BaseRule is an empty rule
 * implementation which can be
 * subclassed for extension.
 *
 * @author Yoav Shapira
 */
public abstract class BaseRule implements Rule {
    /**
     * The rule name.
     */
    private String name;

    /**
     * The URL where matching
     * requested will be redirected.
     */
    private String redirectUrl;

    /**
     * Sets the rule name.
     *
     * @param theName The rule name.
     */
    public void setName(String theName) {
        if (theName == null) {
            throw new IllegalArgumentException("The name cannot be null.");
        } else {
            name = theName;
        }
    }

    /**
     * Returns the rule name.
     *
     * @return String
     */
    protected String getName() {
        return name;
    }

    /**
     * @see Rule#getRedirectUrl
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * Sets the redirect URL.
     *
     * @param theRedirectUrl Where matching requests will be redirected
     */
    public void setRedirectUrl(String theRedirectUrl) {
        if (theRedirectUrl == null) {
            throw new IllegalArgumentException("redirectUrl may not be null.");
        } else {
            redirectUrl = theRedirectUrl;
        }
    }

    /**
     * @see Rule#matches(HttpServletRequest)
     */
    public abstract boolean matches(HttpServletRequest request);

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

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: BaseRule.java
