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
 * This rule redirects the request based
 * on the user's role.
 *
 * @author Yoav Shapira
 */
public class UserRoleRule extends BaseRule {
    /**
     * The desired role for the user.
     * If the user is in this role, the
     * match will succeed.
     */
    private String role;

    /**
     * Sets the desired role.
     *
     * @param theRole The desire role
     */
    public void setRole(String theRole) {
        if (theRole == null) {
            throw new IllegalArgumentException("The role cannot be null.");
        } else {
            role = theRole;
        }
    }

    /**
     * Returns the target role.
     *
     * @return The desired role
     */
    protected String getRole() {
        return role;
    }

    /**
     * @see org.apache.webapp.balancer.Rule#matches(HttpServletRequest)
     */
    public boolean matches(HttpServletRequest request) {
        return request.isUserInRole(getRole());
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

        buffer.append("Target role: ");
        buffer.append(getRole());
        buffer.append(" / ");

        buffer.append("Redirect URL: ");
        buffer.append(getRedirectUrl());

        buffer.append("]");

        return buffer.toString();
    }
}


// End of file: UserRoleRule.java
