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


import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * Base class for form beans for the user administration
 * options.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 4.1
 */

public class BaseForm extends ActionForm {


    // ----------------------------------------------------- Instance Variables


    // ------------------------------------------------------------- Properties


    /**
     * The MBean Name of UserDatabase containing this object.
     */
    private String databaseName = null;

    public String getDatabaseName() {
        if ((this.databaseName == null) && (this.objectName != null)) {
            try {
                ObjectName oname = new ObjectName(this.objectName);
                this.databaseName = oname.getDomain() + ":" +
                  "type=UserDatabase,database=" +
                  oname.getKeyProperty("database");
            } catch (Throwable t) {
                this.databaseName = null;
            }
        }
        return (this.databaseName);
    }

    public void setDatabaseName(String databaseName) {
        if ((databaseName != null) && (databaseName.length() < 1)) {
            this.databaseName = null;
        } else {
            this.databaseName = databaseName;
        }
    }


    /**
     * The node label to be displayed in the user interface.
     */
    private String nodeLabel = null;

    public String getNodeLabel() {
        return (this.nodeLabel);
    }

    public void setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
    }


    /**
     * The MBean object name of this object.  A null or zero-length
     * value indicates that this is a new object.
     */
    private String objectName = null;

    public String getObjectName() {
        return (this.objectName);
    }

    public void setObjectName(String objectName) {
        if ((objectName != null) && (objectName.length() < 1)) {
            this.objectName = null;
        } else {
            this.objectName = objectName;
        }
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        databaseName = null;
        nodeLabel = null;
        objectName = null;

    }


}
