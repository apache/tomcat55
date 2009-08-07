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


package org.apache.webapp.admin.service;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.util.List;

/**
 * Form bean for the service page.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public final class ServiceForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    

    /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";


    /**
     * The object name of the Engine this bean refers to.
     */
    private String engineObjectName = null;


    /**
     * The object name of the Service this bean refers to.
     */
    private String objectName = null;


    /**
     * The text for the serviceName.
     */
    private String serviceName = null;    

    /**
     * The text for the serverObjectName.
     */
    private String serverObjectName = null; 
    
   /**
     * The text for the node label.
    */
    private String nodeLabel = null; 
    
    /**
     * The text for the engine Name.
     */
    private String engineName = null;
    
    
    /**
     * The name of the service the admin app runs on.
     */
    private String adminServiceName = null;    

    /**
     * The text for the defaultHost Name.
     */
    private String defaultHost = null;
    
    private List hostNameVals = null;


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
     * Return the object name of the Engine this bean refers to.
     */
    public String getEngineObjectName() {

        return this.engineObjectName;

    }


    /**
     * Set the object name of the Engine this bean refers to.
     */
    public void setEngineObjectName(String engineObjectName) {

        this.engineObjectName = engineObjectName;

    }


    /**
     * Return the object name of the Service this bean refers to.
     */
    public String getObjectName() {

        return this.objectName;

    }


    /**
     * Set the object name of the Service this bean refers to.
     */
    public void setObjectName(String objectName) {

        this.objectName = objectName;

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
     * Return the host name values.
     */
    public List getHostNameVals() {
        
        return this.hostNameVals;
        
    }
    
    /**
     * Set the hostName values.
     */
    public void setHostNameVals(List hostNameVals) {
        
        this.hostNameVals = hostNameVals;
        
    }
    
    /**
     * Set the engineName.
     */
    
    public void setEngineName(String engineName) {
        
        this.engineName = engineName;
        
    }
    
    
    /**
     * Return the engineName.
     */
    
    public String getEngineName() {
        
        return this.engineName;
        
    }
    
    /**
     * Return the Server ObjectName.
     */
    public String getServerObjectName() {
        
        return this.serverObjectName;
        
    }
    
    /**
     * Set the Server Name.
     */
    public void setServerObjectName(String serverObjectName) {
        
        this.serverObjectName = serverObjectName;
        
    }
    
    /**
     * Return the Service Name.
     */
    public String getServiceName() {
        
        return this.serviceName;
        
    }
    
    /**
     * Set the Service Name.
     */
    public void setServiceName(String serviceName) {
        
        this.serviceName = serviceName;
        
    }

    /**
     * Return the name of the service the admin app runs on.
     */
    public String getAdminServiceName() {

        return this.adminServiceName;

    }

    /**
     * Set the name of the service the admin app runs on.
     */
    public void setAdminServiceName(String adminServiceName) {

        this.adminServiceName = adminServiceName;

    }

    /**
     * Return the default Host.
     */
    public String getDefaultHost() {
        
        return this.defaultHost;
        
    }
    
    /**
     * Set the default Host.
     */
    public void setDefaultHost(String defaultHost) {

        this.defaultHost = defaultHost;

    }


    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.engineObjectName = null;
        this.objectName = null;
        this.serviceName = null;
        this.engineName = null;
        this.adminServiceName = null;
        this.defaultHost = null;
    }
    

    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ServiceForm[adminAction=");
        sb.append(adminAction);
        sb.append(",defaultHost=");
        sb.append(defaultHost);
        sb.append(",engineName=");
        sb.append(engineName);
        sb.append(",engineObjectName='");
        sb.append(engineObjectName);
        sb.append("',objectName='");
        sb.append(objectName);
        sb.append("',serviceName=");
        sb.append(serviceName);
        sb.append("',serverObjectName=");
        sb.append(serverObjectName);
        sb.append("',adminServiceName=");
        sb.append(adminServiceName);
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
        
        //if (submit != null) {

            if ((serviceName == null) || (serviceName.length() < 1)) {
                errors.add("serviceName",
                           new ActionError("error.serviceName.required"));
            }
            
            if ((engineName == null) || (engineName.length() < 1)) {
                errors.add("engineName",
                           new ActionError("error.engineName.required"));
            }

        //}
        
        return errors;
    }
    
}
