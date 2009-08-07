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


package org.apache.webapp.admin.host;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.util.List;

/**
 * Form bean for the alias page.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public final class AliasForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables

    /**
     * The text for the hostName.
     */
    private String hostName = null;

    /**
     * The text for the aliasName.
     */
    private String aliasName = null;

    /*
     * Represent aliases as a List.
     */    
    private List aliasVals = null;
   
    // ------------------------------------------------------------- Properties
    
    /**
     * Return the host name.
     */
    public String getHostName() {
        
        return this.hostName;
        
    }
    
    /**
     * Set the host name.
     */
    public void setHostName(String hostName) {
        
        this.hostName = hostName;
        
    }

    /**
     * Return the alias name.
     */
    public String getAliasName() {
        
        return this.aliasName;
        
    }
    
    /**
     * Set the alias name.
     */
    public void setAliasName(String aliasName) {
        
        this.aliasName = aliasName;
        
    }

    /**
     * Return the List of alias Vals.
     */
    public List getAliasVals() {
        
        return this.aliasVals;
        
    }
    
    /**
     * Set the alias Vals.
     */
    public void setAliasVals(List aliasVals) {
        
        this.aliasVals = aliasVals;
        
    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.aliasName = null;
        this.hostName = null;

    }
    
     /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("AliasForm[hostName=");
        sb.append(hostName);
        sb.append("]");
        return (sb.toString());

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
        
        // front end validation when save is clicked.
        //if (submit != null) {
            
            // aliasName cannot be null
            if ((aliasName== null) || (aliasName.length() < 1)) {
                errors.add("aliasName", new ActionError("error.aliasName.required"));
            }
                        
        //}        
        return errors;       
    }
    
}
