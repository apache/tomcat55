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

package org.apache.webapp.admin.realm;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.net.InetAddress;
import java.util.List;

import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.LabelValueBean;

/**
 * Form bean for the generic realm page.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class RealmForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    
   /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";

    /**
     * The object name of the realm this bean refers to.
     */
    private String objectName = null;
    
    /**
     * The text for the realm type.
     */
    private String realmType = null;
        
    /**
     * The text for the node label.
     */
    private String nodeLabel = null;
    
    /**
     * The object name of the parent of this realm.
     */
    private String parentObjectName = null;
        
    /**
     * Set of valid values for realms.
     */
    private List realmTypeVals = null;

    /**
     * The text for whether "delete this realm" operation is allowed
     * on the realm or not.
     */
    private String allowDeletion = null;

    // ------------------------------------------------------------- Properties

   /**
     * Return the administrative action represented by this form.
     */
    public String getAdminAction() {

        return this.adminAction;

    }

    /**
     * Set the administrative action represented by this form.
     */
    public void setAdminAction(String adminAction) {

        this.adminAction = adminAction;

    }

    /**
     * Return the Object Name.
     */
    public String getObjectName() {
        
        return this.objectName;
        
    }
    
    /**
     * Set the Object Name.
     */
    public void setObjectName(String objectName) {
        
        this.objectName = objectName;
        
    }
    
    /**
     * Return the realm type.
     */
    public String getRealmType() {
        
        return this.realmType;
        
    }
    
    /**
     * Set the realm type.
     */
    public void setRealmType(String realmType) {
        
        this.realmType = realmType;
        
    }
    
    /**
     * Return the label of the node that was clicked.
     */
    public String getNodeLabel() {
        
        return this.nodeLabel;
        
    }
    
    /**
     * Set the node label.
     */
    public void setNodeLabel(String nodeLabel) {
        
        this.nodeLabel = nodeLabel;
        
    }
    
    /**
     * Return the parent object name of the realm this bean refers to.
     */
    public String getParentObjectName() {

        return this.parentObjectName;

    }


    /**
     * Set the parent object name of the realm this bean refers to.
     */
    public void setParentObjectName(String parentObjectName) {

        this.parentObjectName = parentObjectName;

    }
    
        
   /**
     * Return the realmTypeVals.
     */
    public List getRealmTypeVals() {
        
        return this.realmTypeVals;
        
    }
    
    /**
     * Set the realmTypeVals.
     */
    public void setRealmTypeVals(List realmTypeVals) {
        
        this.realmTypeVals = realmTypeVals;
        
    }
    
    /**
     * Return the allow deletion value.
     */
    public String getAllowDeletion() {
        
        return this.allowDeletion;
        
    }
    
    /**
     * Set the allow Deletion value.
     */
    public void setAllowDeletion(String allowDeletion) {
        
        this.allowDeletion = allowDeletion;
        
    }
   
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        objectName = null;
        adminAction = "Edit";
        
    }

}
