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
 * The remote access rule
 * redirects if the request came
 * from specified remote address.
 *
 * @author Yoav Shapira
 */
public class RemoteAddressRule extends BaseRule {
    /**
     * The target remote address.
     */
    private String remoteAddress;

    /**
     * Sets the target remote address.
     *
     * @param theAddress The address
     */
    public void setRemoteAddress(String theAddress) {
        if (theAddress == null) {
            throw new IllegalArgumentException("The address cannot be null.");
        } else {
            remoteAddress = theAddress;
        }
    }

    /**
     * Returns the target remote address.
     *
     * @return String
     */
    protected String getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest)
     *
     * Looks for the request's remote address.
     */
    public boolean matches(HttpServletRequest request) {
        String requestAddr = request.getRemoteAddr();

        return (requestAddr.compareTo(getRemoteAddress()) == 0);
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

        buffer.append("Target remote address: ");
        buffer.append(getRemoteAddress());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: RemoteAddressRule.java
