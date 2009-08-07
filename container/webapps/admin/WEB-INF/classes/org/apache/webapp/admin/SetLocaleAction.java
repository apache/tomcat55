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


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * Implementation of <strong>Action</strong> that sets the current Locale
 * to the one specified by the <code>locale</code> request parameter.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public final class SetLocaleAction extends Action {


    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws IOException, ServletException {

        // What locale does the user want to switch to?
        String requestedLocale = ((SetLocaleForm) form).getLocale();

        // Switch to the specified locale, if it exists
        if (requestedLocale != null) {
            ApplicationLocales locales = (ApplicationLocales)
                getServlet().getServletContext().getAttribute
                (ApplicationServlet.LOCALES_KEY);
            Iterator iterator = locales.getSupportedLocales().iterator();
            Locale currentLocale = null;
            while (iterator.hasNext()) {
                currentLocale = (Locale) iterator.next();
                if (requestedLocale.equals(currentLocale.toString())) {
                    HttpSession session = request.getSession();
                    session.setAttribute(Globals.LOCALE_KEY, currentLocale);
                    // Remove form bean so it will get recreated next time
                    session.removeAttribute(mapping.getAttribute());
                    break;
                }
            }
        }

        // Forward control back to the main menu
        return (mapping.findForward("Main Menu"));

    }


}
