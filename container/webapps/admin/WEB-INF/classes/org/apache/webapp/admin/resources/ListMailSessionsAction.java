/*
 * Copyright 2002,2004 The Apache Software Foundation.
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


package org.apache.webapp.admin.resources;


import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.TomcatTreeBuilder;

/**
 * <p>Retrieve the set of MBean names for all currently defined mail sessions,
 * and expose them in a request attribute named "mailSessionsForm".  This action
 * requires the following request parameters to be set:</p>
 * <ul>
 * <li><strong>forward</strong> - Global forward to which we should
 *     go after stashing the mailSessions list.</li>
 * </ul>
 *
 * @author Amy Roh
 * @version $Revision$ $Date$
 * @since 4.1
 */

public class ListMailSessionsAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The MBeanServer we will be interacting with.
     */
    private MBeanServer mserver = null;


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


        // Look up the components we will be using as needed
        if (mserver == null) {
            mserver = ((ApplicationServlet) getServlet()).getServer();
        }
        MessageResources resources = getResources(request);
        HttpSession session = request.getSession();
        Locale locale = getLocale(request);
        
        String resourcetype = request.getParameter("resourcetype");
        String path = request.getParameter("path");
        String host = request.getParameter("host");
        String domain = request.getParameter("domain");
        
        if (resourcetype != null) {
            resourcetype = URLDecoder.decode(resourcetype,TomcatTreeBuilder.URL_ENCODING);
        }
        if (path != null) {
            path = URLDecoder.decode(path,TomcatTreeBuilder.URL_ENCODING);
        }
        if (host != null) {
            host = URLDecoder.decode(host,TomcatTreeBuilder.URL_ENCODING);
        }
        if (domain != null) {
            domain = URLDecoder.decode(domain,TomcatTreeBuilder.URL_ENCODING);
        }
        
        // Create a form bean containing the requested MBean Names
        MailSessionsForm mailSessionsForm = null;
        try {
              mailSessionsForm = 
                ResourceUtils.getMailSessionsForm(mserver, resourcetype,
                                        path, host, domain);
        } catch (Exception e) {
            getServlet().log(resources.getMessage
                             (locale,
                              "users.error.attribute.get", "resources"), e);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage
                 (locale, "users.error.attribute.get", "resources"));
        }

        // Stash the results in request scope
        request.setAttribute("mailSessionsForm", mailSessionsForm);
        saveToken(request);
        String forward =
            URLDecoder.decode(request.getParameter("forward"),TomcatTreeBuilder.URL_ENCODING);
        
        return (mapping.findForward(forward));
    }

}
