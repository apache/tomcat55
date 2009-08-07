/*
 * Copyright 2001,2004 The Apache Software Foundation.
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


package org.apache.webapp.admin;


import java.util.Locale;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;


/**
 * Form bean for the set locale page.  The actual value is copied when this
 * bean is added as a session attribute.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public final class SetLocaleForm
    extends ActionForm
    implements HttpSessionBindingListener {


    // ------------------------------------------------------------- Properties


    /**
     * The name of the locale we are currently running.
     */
    String locale = null;

    public String getLocale() {
        return (this.locale);
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }


    // ------------------------------------- HttpSessionBindingListener Methods


    /**
     * When this form bean is bound into the session, pick up the user's
     * current Locale object and set the current value.
     */
    public void valueBound(HttpSessionBindingEvent event) {

        HttpSession session = event.getSession();
        Locale current = (Locale) session.getAttribute(Globals.LOCALE_KEY);
        if (current != null)
            locale = current.toString();

    }


    /**
     * No action is required when this object is unbound.
     */
    public void valueUnbound(HttpSessionBindingEvent event) {

        locale = null;

    }


}
