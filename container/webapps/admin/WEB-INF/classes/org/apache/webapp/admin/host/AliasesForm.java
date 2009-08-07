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

package org.apache.webapp.admin.host;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * Form bean for deleting aliases.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class AliasesForm extends ActionForm {


    // ------------------------------------------------------------- Properties


    /**
     * The object names of the aliases to be deleted.
     */
    private String aliases[] = new String[0];

    public String[] getAliases() {
        return (this.aliases);
    }

    public void setAliases(String aliases[]) {
        this.aliases = aliases;
    }

    /**
     * The text for the hostName.
     */
    private String hostName = null;

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
    
    // --------------------------------------------------------- Public Methods


    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        this.aliases = new String[0];
        this.hostName = null;        
    }
        
}
