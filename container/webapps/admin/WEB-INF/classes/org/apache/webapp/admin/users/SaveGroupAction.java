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


package org.apache.webapp.admin.users;


import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.QueryExp;
import javax.management.Query;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanInfo;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.TomcatTreeBuilder;

/**
 * <p>Implementation of <strong>Action</strong> that saves a new or
 * updated Group back to the underlying database.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 4.1
 */

public final class SaveGroupAction extends Action {


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

        // Has this transaction been cancelled?
        if (isCancelled(request)) {
            return (mapping.findForward("List Roles Setup"));
        }

        // Check the transaction token
        if (!isTokenValid(request)) {
            response.sendError
                (HttpServletResponse.SC_BAD_REQUEST,
                 resources.getMessage(locale, "users.error.token"));
            return (null);
        }

        // Perform any extra validation that is required
        GroupForm groupForm = (GroupForm) form;
        String databaseName =
            URLDecoder.decode(groupForm.getDatabaseName(),TomcatTreeBuilder.URL_ENCODING);
        String objectName = groupForm.getObjectName();

        // Perform an "Add Group" transaction
        if (objectName == null) {

            String signature[] = new String[2];
            signature[0] = "java.lang.String";
            signature[1] = "java.lang.String";

            Object params[] = new Object[2];
            params[0] = groupForm.getGroupname();
            params[1] = groupForm.getDescription();

            ObjectName oname = null;

            try {

                // Construct the MBean Name for our UserDatabase
                oname = new ObjectName(databaseName);

                // Create the new object and associated MBean
                objectName = (String) mserver.invoke(oname, "createGroup",
                                                     params, signature);

            } catch (Exception e) {

                getServlet().log
                    (resources.getMessage(locale, "users.error.invoke",
                                          "createGroup"), e);
                response.sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                     resources.getMessage(locale, "users.error.invoke",
                                          "createGroup"));
                return (null);
            }

        }

        // Perform an "Update Group" transaction
        else {

            ObjectName oname = null;
            String attribute = null;

            try {

                // Construct an object name for this object
                oname = new ObjectName(objectName);

                // Update the specified role
                attribute = "description";
                mserver.setAttribute
                    (oname,
                     new Attribute(attribute, groupForm.getDescription()));

            } catch (Exception e) {

                getServlet().log
                    (resources.getMessage(locale, "users.error.set.attribute",
                                          attribute), e);
                response.sendError
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                     resources.getMessage(locale, "users.error.set.attribute",
                                          attribute));
                return (null);

            }

        }


        // Reset the roles associated with this group
        try {

            ObjectName oname = new ObjectName(objectName);
            mserver.invoke(oname, "removeRoles",
                           new Object[0], new String[0]);
            String roles[] = groupForm.getRoles();
            if (roles == null) {
                roles = new String[0];
            }
            String addsig[] = new String[1];
            addsig[0] = "java.lang.String";
            Object addpar[] = new Object[1];
            for (int i = 0; i < roles.length; i++) {
                addpar[0] =
                    (new ObjectName(roles[i])).getKeyProperty("rolename");
                mserver.invoke(oname, "addRole",
                               addpar, addsig);
            }

        } catch (Exception e) {

            getServlet().log
                (resources.getMessage(locale, "users.error.invoke",
                                      "addRole"), e);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.invoke",
                                      "addRole"));
            return (null);

        }

        // Save the updated database information
        try {

            ObjectName dname = new ObjectName(databaseName);
            mserver.invoke(dname, "save",
                           new Object[0], new String[0]);

        } catch (Exception e) {

            getServlet().log
                (resources.getMessage(locale, "users.error.invoke",
                                      "save"), e);
            response.sendError
                (HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                 resources.getMessage(locale, "users.error.invoke",
                                      "save"));
            return (null);

        }

        // Proceed to the list roles screen
        return (mapping.findForward("Groups List Setup"));

    }


}
