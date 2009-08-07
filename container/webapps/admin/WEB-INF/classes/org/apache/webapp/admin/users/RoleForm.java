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


import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * Form bean for the individual role page.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 4.1
 */

public final class RoleForm extends BaseForm {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


    /**
     * The description of this role.
     */
    private String description = null;

    public String getDescription() {
        return (this.description);
    }

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * The rolename of this role.
     */
    private String rolename = null;

    public String getRolename() {
        return (this.rolename);
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
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
        rolename = null;

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

        ActionErrors errors = new ActionErrors();

        String submit = request.getParameter("submit");
        //if (submit != null) {

            // rolename is a required field
            if ((rolename == null) || (rolename.length() < 1)) {
                errors.add("rolename",
                           new ActionError("users.error.rolename.required"));
            }

            // Quotes not allowed in rolename
            if ((rolename != null) && (rolename.indexOf('"') >= 0)) {
                errors.add("rolename",
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
