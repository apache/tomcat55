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


package org.apache.webapp.admin.context;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.util.List;

/**
 * Form bean for the context page.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public final class ContextForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    
   /**
     * The administrative action represented by this form.
     */
    private String adminAction = "Edit";

    /**
     * The object name of the Context this bean refers to.
     */
    private String objectName = null;
   
    /**
     * The object name of the parent of this Context.
     */
    private String parentObjectName = null;
   
   /**
     * The object name of the loader of this Context.
     */
    private String loaderObjectName = null;
   
    /**
     * The object name of the manager of this Context.
     */
    private String managerObjectName = null;
   
    /**
     * The text for the node label.
     */
    private String nodeLabel = null;
    
    /**
     * The value of cookies.
     */
    private String cookies = "false";
    
    /**
     * The value of cross context.
     */
    private String crossContext = "false";
    
    /**
     * The text for the document Base.
     */
    private String docBase = null;
    
    /**
     * The text for override boolean.
     */
    private String override = "false";
    
    /**
     * The text for privileged boolean.
     */
    private String privileged = "false";
    
    /**
     * The text for the context path for this context.
     */
    private String path = null;
    
    /**
     * The text for reloadable boolean.
     */
    private String reloadable = "false";

    /**
     * The text for swallowOutput boolean.
     */
    private String swallowOutput = "false";

    /**
     * The text for use naming boolean.
     */
    private String useNaming = "false";
    
    /**
     * The text for the working directory for this context.
     */
    private String workDir = null;
    
    /**
     * The text for the loader check interval.
     */
    private String ldrCheckInterval = "15";
    
    /**
     * The text for the boolean value of loader reloadable.
     */
    private String ldrReloadable = "false";
    
    /**
     * The text for the session manager check interval.
     */
    private String mgrCheckInterval = "60";
    
    
    /**
     * The text for the session mgr session ID initializer.
     */
    private String mgrSessionIDInit = "";
    
    /**
     * The text for the session mgr max active sessions.
     */
    private String mgrMaxSessions = "0";
    
    /**
     * The text for the anti resource locking flag.
     */
    private String antiResourceLocking = "false";

    /**
     * The text for the anti jar locking flag.
     */
    private String antiJarLocking = "false";

    /*
     * Represent boolean (true, false) values for cookies etc.
     */
    private List booleanVals = null;
    
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
     * Return the object name of the Context this bean refers to.
     */
    public String getObjectName() {

        return this.objectName;

    }

    /**
     * Set the object name of the Context this bean refers to.
     */
    public void setObjectName(String objectName) {

        this.objectName = objectName;

    }    
    
    /**
     * Return the parent object name of the Context this bean refers to.
     */
    public String getParentObjectName() {

        return this.parentObjectName;

    }

    /**
     * Set the parent object name of the Context this bean refers to.
     */
    public void setParentObjectName(String parentObjectName) {

        this.parentObjectName = parentObjectName;

    }
    
      /**
     * Return the loader object name of the Context this bean refers to.
     */
    public String getLoaderObjectName() {

        return this.loaderObjectName;

    }

    /**
     * Set the loader object name of the Context this bean refers to.
     */
    public void setLoaderObjectName(String loaderObjectName) {

        this.loaderObjectName = loaderObjectName;

    }
    
      /**
     * Return the manager object name of the Context this bean refers to.
     */
    public String getManagerObjectName() {

        return this.managerObjectName;

    }

    /**
     * Set the manager object name of the Context this bean refers to.
     */
    public void setManagerObjectName(String managerObjectName) {

        this.managerObjectName = managerObjectName;

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
     * Return the booleanVals.
     */
    public List getBooleanVals() {
        
        return this.booleanVals;
        
    }
    
    /**
     * Set the debugVals.
     */
    public void setBooleanVals(List booleanVals) {
        
        this.booleanVals = booleanVals;
        
    }
    
    
    /**
     * Return the Cookies.
     */
    
    public String getCookies() {
        
        return this.cookies;
        
    }
    
    /**
     * Set the Cookies.
     */
    public void setCookies(String cookies) {
        
        this.cookies = cookies;
        
    }
    
    /**
     * Return the Cross Context.
     */
    
    public String getCrossContext() {
        
        return this.crossContext;
        
    }
    
    /**
     * Set the Cross Context.
     */
    public void setCrossContext(String crossContext) {
        
        this.crossContext = crossContext;
        
    }
    
    
    /**
     * Return the Document Base Text.
     */
    
    public String getDocBase() {
        
        return this.docBase;
        
    }
    
    /**
     * Set the document Base text.
     */
    public void setDocBase(String docBase) {
        
        this.docBase = docBase;
        
    }
    
    
    /**
     * Return the Override boolean value.
     */
    
    public String getOverride() {
        
        return this.override;
        
    }
    
    /**
     * Set the override value.
     */
    public void setOverride(String override) {
        
        this.override = override;
        
    }
    
    
    /**
     * Return the privileged boolean value.
     */
    
    public String getPrivileged() {
        
        return this.privileged;
        
    }
    
    /**
     * Set the privileged value.
     */
    public void setPrivileged(String privileged) {
        
        this.privileged = privileged;
        
    }
    
    
    /**
     * Return the context path.
     */
    
    public String getPath() {
        
        return this.path;
        
    }
    
    /**
     * Set the context path text.
     */
    public void setPath(String path) {
        
        this.path = path;
        
    }
    
    
    /**
     * Return the reloadable boolean value.
     */
    
    public String getReloadable() {
        
        return this.reloadable;
        
    }
    
    /**
     * Set the reloadable value.
     */
    public void setReloadable(String reloadable) {
        
        this.reloadable = reloadable;
        
    }
    
    /**
     * Return the swallowOutput boolean value.
     */

    public String getSwallowOutput() {

        return this.swallowOutput;

    }

    /**
     * Set the swallowOutput value.
     */
    public void setSwallowOutput(String swallowOutput) {

        this.swallowOutput = swallowOutput;

    }

    /**
     * Return the use naming boolean value.
     */
    
    public String getUseNaming() {
        
        return this.useNaming;
        
    }
    
    /**
     * Set the useNaming value.
     */
    public void setUseNaming(String useNaming) {
        
        this.useNaming = useNaming;
        
    }
    
    /**
     * Return the Working Directory.
     */
    public String getWorkDir() {
        
        return this.workDir;
        
    }
    
    /**
     * Set the working directory.
     */
    public void setWorkDir(String workDir) {
        
        this.workDir = workDir;
        
    }
    
    
    /**
     * Return the loader check interval.
     */
    public String getLdrCheckInterval() {
        
        return this.ldrCheckInterval;
        
    }
    
    /**
     * Set the loader Check Interval.
     */
    public void setLdrCheckInterval(String ldrCheckInterval) {
        
        this.ldrCheckInterval = ldrCheckInterval;
        
    }
    
    
    /**
     * Return the loader reloadable boolean value.
     */
    public String getLdrReloadable() {
        
        return this.ldrReloadable;
        
    }
    
    /**
     * Set the loader reloadable value.
     */
    public void setLdrReloadable(String ldrReloadable) {
        
        this.ldrReloadable = ldrReloadable;
        
    }
    
    /**
     * Return the session manager check interval.
     */
    public String getMgrCheckInterval() {
        
        return this.mgrCheckInterval;
        
    }
    
    /**
     * Set the session manager Check Interval.
     */
    public void setMgrCheckInterval(String mgrCheckInterval) {
        
        this.mgrCheckInterval = mgrCheckInterval;
        
    }
    
    /**
     * Return the session ID initializer.
     */
    public String getMgrSessionIDInit() {
        
        return this.mgrSessionIDInit;
        
    }
    
    /**
     * Set the mgr Session ID Initizializer.
     */
    public void setMgrSessionIDInit(String mgrSessionIDInit) {
        
        this.mgrSessionIDInit = mgrSessionIDInit;
        
    }
    
    /**
     * Return the Session mgr maximum active sessions.
     */
    
    public String getMgrMaxSessions() {
        
        return this.mgrMaxSessions;
        
    }
    
    /**
     * Set the Session mgr maximum active sessions.
     */
    public void setMgrMaxSessions(String mgrMaxSessions) {
        
        this.mgrMaxSessions = mgrMaxSessions;
        
    }

    /**
     * Get the anti resouce locking flag
     */
    public String getAntiResourceLocking() {
        return antiResourceLocking;
    }

    /**
     * Set the anti resource locking flag
     */
    public void setAntiResourceLocking(String arl) {
	antiResourceLocking = arl;
    }


    /**
     * Get the anti jar locking flag
     */
    public String getAntiJarLocking() {
        return antiJarLocking;
    }

    /**
     * Set the anti jar locking flag
     */
    public void setAntiJarLocking(String ajl) {
        antiJarLocking = ajl;
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
        this.parentObjectName = null;
        this.loaderObjectName = null;
        this.managerObjectName = null;
        
        // context properties
        this.cookies = "false";
        this.crossContext = "false";
        this.docBase = null;
        this.override= "false";
        this.path = null;
        this.reloadable = "false";
        this.swallowOutput = "false";
        this.antiResourceLocking = "false";
        this.antiJarLocking = "false";

        // loader properties
        this.ldrCheckInterval = "15";
        this.ldrReloadable = "true";
        
        // session manager properties
        this.mgrCheckInterval = "60";
        this.mgrSessionIDInit = "0";
        this.mgrMaxSessions = "-1";
    }
    
    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ContextForm[adminAction=");
        sb.append(adminAction);
        sb.append(",docBase=");
        sb.append(docBase);
        sb.append(",path=");
        sb.append(path);
        sb.append(",cookies=");
        sb.append(cookies);
        sb.append(",crossContext=");
        sb.append(crossContext);
        sb.append(",override=");
        sb.append(override);
        sb.append(",reloadable=");
        sb.append(reloadable);
        sb.append(",swallowOutput=");
        sb.append(swallowOutput);

        // loader properties
        sb.append(",ldrCheckInterval=");
        sb.append(ldrCheckInterval);        
        sb.append(",ldrReloadable=");
        sb.append(ldrReloadable);
        // manager properties
        sb.append(",mgrCheckInterval=");
        sb.append(mgrCheckInterval);
        sb.append(",mgrSessionIDInit=");
        sb.append(mgrSessionIDInit);
        sb.append(",mgrMaxSessions=");
        sb.append(mgrMaxSessions);
        // object names
        sb.append("',objectName='");
        sb.append(objectName);
        sb.append("',parentObjectName=");
        sb.append(parentObjectName);
        sb.append("',loaderObjectName=");
        sb.append(loaderObjectName);
        sb.append("',managerObjectName=");
        sb.append(managerObjectName);
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
    
    private ActionErrors errors;
    
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {
        
        errors = new ActionErrors();
        
        String submit = request.getParameter("submit");
        
        // front end validation when save is clicked.
        //if (submit != null) {
            
            // docBase cannot be null
            if ((docBase == null) || (docBase.length() < 1)) {
                errors.add("docBase", new ActionError("error.docBase.required"));
            }
            
            // if path is empty, it's root context
            // validate context starting with "/" only at the time of context creation.
            if ("Create".equalsIgnoreCase(adminAction) && !path.startsWith("/")) {
                errors.add("path", new ActionError("error.path.prefix"));                
            }
                        
            //if ((workDir == null) || (workDir.length() < 1)) {
            //    errors.add("workDir", new ActionError("error.workDir.required"));
            //}
            
            // loader properties
            // FIXME-- verify if these ranges are ok.
            numberCheck("ldrCheckInterval", ldrCheckInterval  , true, 0, 10000);
            
            // session manager properties            
            numberCheck("mgrCheckInterval",  mgrCheckInterval, true, 0, 10000);
            numberCheck("mgrMaxSessions",  mgrMaxSessions, false, -1, 100);
            
            //if ((mgrSessionIDInit == null) || (mgrSessionIDInit.length() < 1)) {
            //    errors.add("mgrSessionIDInit", new ActionError("error.mgrSessionIDInit.required"));
            //}
        //}
        
        return errors;
    }
    
    /*
     * Helper method to check that it is a required number and
     * is a valid integer within the given range. (min, max).
     *
     * @param  field  The field name in the form for which this error occured.
     * @param  numText  The string representation of the number.
     * @param rangeCheck  Boolean value set to true of reange check should be performed.
     *
     * @param  min  The lower limit of the range
     * @param  max  The upper limit of the range
     *
     */
    
    private void numberCheck(String field, String numText, boolean rangeCheck,
    int min, int max) {
        
        // Check for 'is required'
        if ((numText == null) || (numText.length() < 1)) {
            errors.add(field, new ActionError("error."+field+".required"));
        } else {
            
            // check for 'must be a number' in the 'valid range'
            try {
                int num = Integer.parseInt(numText);
                // perform range check only if required
                if (rangeCheck) {
                    if ((num < min) || (num > max ))
                        errors.add( field,
                        new ActionError("error."+ field +".range"));
                }
            } catch (NumberFormatException e) {
                errors.add(field,
                new ActionError("error."+ field + ".format"));
            }
        }
    }
    
}
