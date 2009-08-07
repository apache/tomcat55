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

import java.net.URLDecoder;
import javax.management.MBeanServer;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.TomcatTreeBuilder;

/**
 * Form bean for the individual group page.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 4.1
 */

public final class GroupForm extends BaseForm {


    // ----------------------------------------------------- Instance Variables

   /**
     * The MBeanServer we will be interacting with.
     */
    private MBeanServer mserver = null;
    

    // ------------------------------------------------------------- Properties


    /**
     * The description of this group.
     */
    private String description = null;

    public String getDescription() {
        return (this.description);
    }

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * The groupname of this group.
     */
    private String groupname = null;

    public String getGroupname() {
        return (this.groupname);
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }


    /**
     * The MBean Names of the roles associated with this group.
     */
    private String roles[] = new String[0];

    public String[] getRoles() {
        return (this.roles);
    }

    public void setRoles(String roles[]) {
        if (roles == null) {
            roles = new String[0];
        }
        this.roles = roles;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        super.reset(mapping, request);
        description = null;
        groupname = null;
        roles = new String[0];

    }


    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {
        
        try {
            // Look up the components we will be using as needed
            if (mserver == null) {
                mserver = ((ApplicationServlet) getServlet()).getServer();
            }
         
            // Set up beans containing all possible groups and roles
            String databaseName =
                URLDecoder.decode(request.getParameter("databaseName"),TomcatTreeBuilder.URL_ENCODING);
            request.setAttribute("rolesForm",
                                 UserUtils.getRolesForm(mserver,
                                                        databaseName));
        } catch (Exception e) {
            // do nothing since the form returns validation error
        }
        
        ActionErrors errors = new ActionErrors();

        String submit = request.getParameter("submit");
        //if (submit != null) {

            // groupname is a required field
            if ((groupname == null) || (groupname.length() < 1)) {
                errors.add("groupname",
                           new ActionError("users.error.groupname.required"));
            }

            // Quotes not allowed in groupname
            if ((groupname != null) && (groupname.indexOf('"') >= 0)) {
                errors.add("groupname",
                           new ActionError("users.error.quotes"));
            }

            // Quotes not allowed in description
            if ((description != null) && (description.indexOf('"') > 0)) {
                errors.add("description",
                           new ActionError("users.error.quotes"));
            }

        //}

        return (errors);

    }


}
