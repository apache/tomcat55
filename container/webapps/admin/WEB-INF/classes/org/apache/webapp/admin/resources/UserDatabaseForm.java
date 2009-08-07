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

import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.webapp.admin.LabelValueBean;

import java.lang.reflect.Constructor;

/**
 * Form bean for the individual user database page.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 * @since 4.1
 */

public final class UserDatabaseForm extends BaseForm {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties

    /**
     * The domain of this data source.
     */
    private String domain = null;
    
    /**
     * Return the domain of the data source this bean refers to.
     */
    public String getDomain() {
        return this.domain;
    }

    /**
     * Set the domain of the data source this bean refers to.
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * The name of the associated entry.
     */
    private String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The path of the associated user database entry.
     */
    private String path = null;

    public String getPath() {
        return (this.path);
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * The type of the resource.
     */
    private String type = null;

    public String getType() {
        return (this.type);
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * The factory that implements the user database entry.
     */
    private String factory = null;

    public String getFactory() {
        return (this.factory);
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    /**
     * The description of the associated entry.
     */
    private String description = null;

    public String getDescription() {
        return (this.description);
    }

    public void setDescription(String description) {
        this.description = description;
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
        name = null;
        type = null;
        path = null;
        factory = null;
        description = null;

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
    
    private ActionErrors errors = null;
    
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {

        errors = new ActionErrors();

        String submit = request.getParameter("submit");
        //if (submit != null) {

            // name is a required field
            if ((name == null) || (name.length() < 1)) {
                errors.add("name",
                           new ActionError("resources.error.name.required"));
            }

            // path is a required field
            if ((path == null) || (path.length() < 1)) {
                errors.add("path",
                           new ActionError("resources.error.path.required"));
            }

            // Quotes not allowed in name
            if ((name != null) && (name.indexOf('"') >= 0)) {
                errors.add("name",
                           new ActionError("users.error.quotes"));
            }

            // Quotes not allowed in path
            if ((path != null) && (path.indexOf('"') > 0)) {
                errors.add("path",
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
