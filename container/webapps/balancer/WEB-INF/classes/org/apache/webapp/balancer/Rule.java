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

import javax.servlet.http.HttpServletRequest;


/**
 * The Rule interface is implemented by
 * load balancing rules.
 *
 * @author Yoav Shapira
 */
public interface Rule {
    /**
     * Determine if the given request
     * matches the rule.
     *
     * @param request The request
     * @return boolean True if matches, will be redirected.
     */
    boolean matches(HttpServletRequest request);

    /**
     * Returns the redirect URL for
     * requests that match this rule.
     *
     * @return The redirect URL
     */
    String getRedirectUrl();
}


// End of file: Rule.java
