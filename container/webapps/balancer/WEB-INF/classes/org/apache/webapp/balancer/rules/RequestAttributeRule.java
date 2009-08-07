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
 * based on the presence of a attribute
 * in the request.
 *
 * @author Yoav Shapira
 */
public class RequestAttributeRule extends BaseRule {
    /**
     * The target attribute name (attribute
     * must be present for match to succeed).
     */
    private String attributeName;

    /**
     * The target attribute value.  This
     * is optional: null means any attribute
     * value is OK for a match.  A non-null
     * value will be matches exactly.
     */
    private Object attributeValue;

    /**
     * Sets the target attribute name.
     *
     * @param theAttributeName The attribute name
     */
    public void setAttributeName(String theAttributeName) {
        if (theAttributeName == null) {
            throw new IllegalArgumentException(
                "attributeName cannot be null.");
        } else {
            attributeName = theAttributeName;
        }
    }

    /**
     * Returns the target attribute name.
     *
     * @return String The target attribute name.
     */
    protected String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets the attribute value, which may be null.
     *
     * @param theAttributeValue The attribute value
     */
    public void setAttributeValue(Object theAttributeValue) {
        attributeValue = theAttributeValue;
    }

    /**
     * Returns the target attribute value,
     * which may be null.
     *
     * @return Object The target attribute value
     */
    protected Object getAttributeValue() {
        return attributeValue;
    }

    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest)
     */
    public boolean matches(HttpServletRequest request) {
        Object actualAttributeValue = request.getAttribute(getAttributeName());

        if (actualAttributeValue == null) {
            return (getAttributeValue() == null);
        } else {
            return (actualAttributeValue.equals(getAttributeValue()));
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

        buffer.append("Target attribute name: ");
        buffer.append(getAttributeName());
        buffer.append(" / ");

        buffer.append("Target attribute value: ");
        buffer.append(getAttributeValue());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: RequestAttributeRule.java
