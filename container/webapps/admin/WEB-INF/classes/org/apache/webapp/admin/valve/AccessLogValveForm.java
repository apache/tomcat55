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

package org.apache.webapp.admin.valve;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.util.List;

/**
 * Form bean for the accesslog valve page.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public final class AccessLogValveForm extends ValveForm {
    
    // ----------------------------------------------------- Instance Variables
    /**
     * The text for the debug level.
     */
    private String debugLvl = "0";
  
    /**
     * Set of valid values for debug level.
     */
    private List debugLvlVals = null;
    
    /**
     * The text for the directory.
     */
    private String directory = null;
    
    /**
     * The text for the pattern.
     */
    private String pattern = null;
        
    /**
     * The text for the prefix.
     */
    private String prefix = null;
    
    /**
     * The text for the suffix.
     */
    private String suffix = null;
      
    /**
     * The text for the connection URL.
     */
    private String resolveHosts = "false";
      
    /**
     * The text for the rotatable.
     */
    private String rotatable = "true";    
       
    /**
     * Set of boolean values.
     */
    private List booleanVals = null;
 
    // ------------------------------------------------------------- Properties

    /**
     * Return the debugVals.
     */
    public List getDebugLvlVals() {
        
        return this.debugLvlVals;
        
    }
    
    /**
     * Set the debugVals.
     */
    public void setDebugLvlVals(List debugLvlVals) {
        
        this.debugLvlVals = debugLvlVals;
        
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
     * Return the Debug Level Text.
     */
    public String getDebugLvl() {
        
        return this.debugLvl;
        
    }
    
    /**
     * Set the Debug Level Text.
     */
    public void setDebugLvl(String debugLvl) {
        
        this.debugLvl = debugLvl;
        
    }
    
    /**
     * Return the directory.
     */
    public String getDirectory() {
        
        return this.directory;
        
    }
    
    /**
     * Set the directory.
     */
    public void setDirectory(String directory) {
        
        this.directory = directory;
        
    }
    
    /**
     * Return the pattern.
     */
    public String getPattern() {
        
        return this.pattern;
        
    }
    
    /**
     * Set the pattern.
     */
    public void setPattern(String pattern) {
        
        this.pattern = pattern;
        
    }
    
    /**
     * Return the prefix.
     */
    public String getPrefix() {
        
        return this.prefix;
        
    }
    
    /**
     * Set the prefix.
     */
    public void setPrefix(String prefix) {
        
        this.prefix = prefix;
        
    }
    
    /**
     * Return the suffix.
     */
    public String getSuffix() {
        
        return this.suffix;
        
    }
    
    /**
     * Set the suffix.
     */
    public void setSuffix(String suffix) {
        
        this.suffix = suffix;
        
    }
            
    /**
     * Return the resolve hosts.
     */
    public String getResolveHosts() {
        
        return this.resolveHosts;
        
    }
    
    /**
     * Set the resolveHosts.
     */
    public void setResolveHosts(String resolveHosts) {
        
        this.resolveHosts = resolveHosts;
        
    }  
    
    /**
     * Return the rotatable.
     */
    public String getRotatable() {
        
        return this.rotatable;
        
    }
    
    /**
     * Set the rotatable.
     */
    public void setRotatable(String rotatable) {
        
        this.rotatable = rotatable;
        
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
        this.debugLvl = "0";
        
        this.directory = null;
        this.prefix = null;
        this.suffix = null;
        this.pattern = null;        
        this.resolveHosts = "false";
        this.rotatable = "true";
        
    }
    
    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("AccessLogValveForm[adminAction=");
        sb.append(getAdminAction());
        sb.append("',valveType=");
        sb.append(getValveType());
        sb.append(",debugLvl=");
        sb.append(debugLvl);
        sb.append(",directory=");
        sb.append(directory);
        sb.append("',prefix='");
        sb.append(prefix);
        sb.append("',pattern=");
        sb.append(pattern);
        sb.append(",resolveHosts=");
        sb.append(resolveHosts);
        sb.append(",rotatable=");
        sb.append(rotatable);
        sb.append("',objectName='");
        sb.append(getObjectName());
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
            
             // if not specified, default is access_log.
             // to specify no prefix, specify a 0 length string...
            if ((prefix == null) || (prefix.length() == 0)){
                prefix = "access_log.";
            }
            
            // default is a 0 length string
            if ((suffix == null) || (suffix.length() < 1)) {
                suffix = "";
            }
                                    
            // If no directory attribute is specified, the default
            // value is "logs".
            if ((directory == null) || (directory.length() < 1)) {
                directory = "logs";
            }

            if ((pattern == null) || (pattern.length() < 1)) {
                errors.add("pattern",
                new ActionError("error.pattern.required"));
            }         
        //}
                 
        return errors;
    }
}
