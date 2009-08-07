/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
 * This rule redirects requests if they
 * are for a specific character encoding.
 *
 * @author Yoav Shapira
 */
public class CharacterEncodingRule extends BaseRule {
    /**
     * The character encoding.
     */
    private String encoding;

    /**
     * Sets the character encoding.
     *
     * @param theEncoding The encoding value
     */
    public void setEncoding(String theEncoding) {
        if (theEncoding == null) {
            throw new IllegalArgumentException("The encoding cannot be null.");
        } else {
            encoding = theEncoding;
        }
    }

    /**
     * Returns the desired encoding.
     *
     * @return String
     */
    protected String getEncoding() {
        return encoding;
    }

    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest request)
     */
    public boolean matches(HttpServletRequest request) {
        String actualEncoding = request.getCharacterEncoding();

        return (getEncoding().compareTo(actualEncoding) == 0);
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

        buffer.append("Target encoding: ");
        buffer.append(getEncoding());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: CharacterEncodingRule.java
