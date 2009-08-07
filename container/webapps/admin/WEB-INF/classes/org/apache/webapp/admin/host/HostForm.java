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
 * Form bean for the host page.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public final class HostForm extends ActionForm {

    // ----------------------------------------------------- Instance Variables

    /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";

    /**
     * The object name of this Host bean refers to.
     */
    private String objectName = null;

    /**
     * The text for the node label. This is of the form 'Host(name)'
     * and is picked up from the node of the tree that is clicked on.
     */
    private String nodeLabel = null;

    /**
     * The text for the hostName.
     */
    private String hostName = null;

    /**
     * The object name of the service this host belongs to.
     */
    private String serviceName = null;

    /**
     * The directory for the appBase.
     */
    private String appBase = null;

    /**
     * Boolean for autoDeploy.
     */
    private String autoDeploy = "true";

    /**
     * Boolean for deployXML.
     */
    private String deployXML = "true";

    /**
     * Boolean for deployOnStartup.
     */
    private String deployOnStartup = "true";
    
    /**
     * Boolean for unpack WARs.
     */
    private String unpackWARs = "false";

    /**
     * The text for the port. -- TBD
     */
    private String findAliases = null;

    /*
     * Represent boolean (true, false) values for unpackWARs etc.
     */
    private List booleanVals = null;

    /*
     * Represent aliases as a List.
     */
    private List aliasVals = null;

    /**
     * Boolean for xmlNamespaceAware.
     */
    private String xmlNamespaceAware = "false";

    /**
     * Boolean for xmlValidation.
     */
    private String xmlValidation = "false";

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
     * Return the object name of the Host this bean refers to.
     */
    public String getObjectName() {

        return this.objectName;

    }


    /**
     * Set the object name of the Host this bean refers to.
     */
    public void setObjectName(String objectName) {

        this.objectName = objectName;

    }
    

    /**
     * Return the object name of the service this host belongs to.
     */
    public String getServiceName() {

        return this.serviceName;

    }


    /**
     * Set the object name of the Service this host belongs to.
     */
    public void setServiceName(String serviceName) {

        this.serviceName = serviceName;

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
     * Return the appBase.
     */
    public String getAppBase() {

        return this.appBase;

    }
    
    /**
     * Return the autoDeploy.
     */
    public String getAutoDeploy() {
        
        return this.autoDeploy;
        
    }
    
    /**
     * Set the autoDeploy.
     */
    
    public void setAutoDeploy(String autoDeploy) {
        
        this.autoDeploy = autoDeploy;
        
    }

    /**
     * Return the deployXML.
     */
    public String getDeployXML() {
        
        return this.deployXML;
        
    }
    
    /**
     * Set the deployXML.
     */
    
    public void setDeployXML(String deployXML) {
        
        this.deployXML = deployXML;
        
    }

    /**
     * Return the deployOnStartup.
     */
    public String getDeployOnStartup() {
        
        return this.deployOnStartup;
        
    }
    
    /**
     * Set the deployOnStartup.
     */
    
    public void setDeployOnStartup(String deployOnStartup) {
        
        this.deployOnStartup = deployOnStartup;
        
    }

    /**
     * Set the appBase.
     */

    public void setAppBase(String appBase) {

        this.appBase = appBase;

    }

    /**
     * Return the unpackWARs.
     */
    public String getUnpackWARs() {

        return this.unpackWARs;

    }

    /**
     * Set the unpackWARs.
     */

    public void setUnpackWARs(String unpackWARs) {

        this.unpackWARs = unpackWARs;

    }

    /**
     * Return the booleanVals.
     */
    public List getBooleanVals() {

        return this.booleanVals;

    }

    /**
     * Set the booleanVals.
     */
    public void setBooleanVals(List booleanVals) {

        this.booleanVals = booleanVals;

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

    /**
     * Return the xmlNamespaceAware.
     */
    public String getXmlNamespaceAware() {

        return this.xmlNamespaceAware;

    }

    /**
     * Set the xmlNamespaceAware.
     */

    public void setXmlNamespaceAware(String xmlNamespaceAware) {

        this.xmlNamespaceAware = xmlNamespaceAware;

    }

    /**
     * Return the xmlValidation.
     */
    public String getXmlValidation() {

        return this.xmlValidation;

    }

    /**
     * Set the xmlValidation.
     */

    public void setXmlValidation(String xmlValidation) {

        this.xmlValidation = xmlValidation;

    }

    // --------------------------------------------------------- Public Methods

    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        this.objectName = null;        
        this.serviceName = null;
        this.hostName = null;
        this.appBase = null;
        this.autoDeploy = "true";
        this.deployXML = "true";
        this.deployOnStartup = "true";
        this.unpackWARs = "true";

    }

     /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("HostForm[adminAction=");
        sb.append(adminAction);
        sb.append(",appBase=");
        sb.append(appBase);
        sb.append(",autoDeploy=");
        sb.append(autoDeploy);
        sb.append(",deployXML=");
        sb.append(deployXML);
        sb.append(",deployOnStartup=");
        sb.append(deployOnStartup);
        sb.append(",unpackWARs=");
        sb.append(unpackWARs);
        sb.append("',objectName='");
        sb.append(objectName);
        sb.append("',hostName=");
        sb.append(hostName);
        sb.append("',serviceName=");
        sb.append(serviceName);
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

            // hostName cannot be null
            if ((hostName== null) || (hostName.length() < 1)) {
                errors.add("hostName", new ActionError("error.hostName.required"));
            }

            // appBase cannot be null
            if ((appBase == null) || (appBase.length() < 1)) {
                errors.add("appBase", new ActionError("error.appBase.required"));
            }

        //}
        return errors;

    }

}
