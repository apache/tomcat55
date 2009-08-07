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

package org.apache.webapp.admin.server;


import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.TomcatTreeBuilder;

import java.util.List;

/**
 * Form bean for the server form page.  
 * @author Patrick Luby
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public final class ServerForm extends ActionForm {
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The text for the node label.
     */
    private String nodeLabel = null;
    
    /**
     * The text for the port number.
     */    
    private String portNumberText = "8080";
    
    /**
     * The text for the shutdown text.
     */    
    private String shutdownText = null;
    
    /**
     * The object name of the Connector this bean refers to.
     */
    private String objectName = null;
    
    // ------------------------------------------------------------- Properties
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
     * Return the portNumberText.
     */
    public String getPortNumberText() {
        
        return this.portNumberText;
        
    }
    
    /**
     * Set the portNumberText.
     */
    public void setPortNumberText(String portNumberText) {
        
        this.portNumberText = portNumberText;
        
    }
    
    /**
     * Return the Shutdown Text.
     */
    public String getShutdownText() {
        
        return this.shutdownText;
        
    }
    
    /**
     * Set the Shut down  Text.
     */
    public void setShutdownText(String shutdownText) {
        
        this.shutdownText = shutdownText;
        
    }
    
    /**
     * Return the object name of the Connector this bean refers to.
     */
    public String getObjectName() {

        return this.objectName;

    }


    /**
     * Set the object name of the Connector this bean refers to.
     */
    public void setObjectName(String objectName) {

        this.objectName = objectName;

    }
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.portNumberText = null;
        this.shutdownText = null;
        
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
            
            // check for portNumber -- must not be blank, must be in
            // the range 1 to 65535.
            
            if ((portNumberText == null) || (portNumberText.length() < 1)) {
                errors.add("portNumberText",
                new ActionError("error.portNumber.required"));
            } else {
                try {
                    int port = Integer.parseInt(portNumberText);
                    if ((port <= 0) || (port >65535 ))
                        errors.add("portNumberText", 
                            new ActionError("error.portNumber.range"));
                } catch (NumberFormatException e) {
                    errors.add("portNumberText", 
                        new ActionError("error.portNumber.format"));
                }
            }
        
            // shutdown text can be any non-empty string of atleast 6 characters.
            
            if ((shutdownText == null) || (shutdownText.length() < 7))
                errors.add("shutdownText",
                new ActionError("error.shutdownText.length"));
            
        //}
        
        return errors;
        
    }
    
}
