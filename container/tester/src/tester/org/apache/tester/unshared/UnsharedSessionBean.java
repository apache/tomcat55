/*
 * Copyright 1999, 2000 ,2004 The Apache Software Foundation.
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

package org.apache.tester.unshared;


import java.io.Serializable;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import org.apache.tester.SessionBean;


/**
 * Simple JavaBean to use for session attribute tests.  It is Serializable
 * so that instances can be saved and restored across server restarts.
 * <p>
 * This bean is functionally equivalent to
 * <code>org.apache.tester.SessionBean</code>, but will be deployed under
 * <code>/WEB-INF/classes</code> instead of inside
 * <code>/WEB-INF/lib/tester.jar</code>.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class UnsharedSessionBean extends SessionBean implements
    HttpSessionActivationListener, HttpSessionBindingListener, Serializable {


    /**
     * Return a string representation of this bean.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("UnsharedSessionBean[lifecycle=");
        sb.append(this.lifecycle);
        sb.append(",stringProperty=");
        sb.append(this.stringProperty);
        sb.append("]");
        return (sb.toString());

    }


}

