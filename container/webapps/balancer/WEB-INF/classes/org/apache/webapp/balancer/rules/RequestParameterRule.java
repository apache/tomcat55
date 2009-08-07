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
 * This rule accepts or rejects requests
 * based on the presence of a parameter
 * in the request.
 *
 * @author Yoav Shapira
 */
public class RequestParameterRule extends BaseRule {
    /**
     * The target parameter name (parameter
     * must be present for match to succeed).
     */
    private String paramName;

    /**
     * The target parameter value.  This
     * is optional: null means any parameter
     * value is OK for a match.  A non-null
     * value will be matches exactly.
     */
    private String paramValue;

    /**
     * Sets the target parameter name.
     *
     * @param theParamName The parameter name
     */
    public void setParamName(String theParamName) {
        if (theParamName == null) {
            throw new IllegalArgumentException("paramName cannot be null.");
        } else {
            paramName = theParamName;
        }
    }

    /**
     * Returns the target parameter name.
     *
     * @return String The target parameter name.
     */
    protected String getParamName() {
        return paramName;
    }

    /**
     * Sets the parameter value, which may be null.
     *
     * @param theParamValue The parameter value
     */
    public void setParamValue(String theParamValue) {
        paramValue = theParamValue;
    }

    /**
     * Returns the target parameter value,
     * which may be null.
     *
     * @return String The target parameter value
     */
    protected String getParamValue() {
        return paramValue;
    }

    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest)
     */
    public boolean matches(HttpServletRequest request) {
        String actualParamValue = request.getParameter(getParamName());

        if (actualParamValue == null) {
            return (getParamValue() == null);
        } else {
            return (actualParamValue.compareTo(getParamValue()) == 0);
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

        buffer.append("Target param name: ");
        buffer.append(getParamName());
        buffer.append(" / ");

        buffer.append("Target param value: ");
        buffer.append(getParamValue());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: RequestParameterRule.java
