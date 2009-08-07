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
 * Form bean for the jdbc realm page.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public final class JDBCRealmForm extends RealmForm {
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The text for the realm name, used to retrieve
     * the corresponding realm mBean.
     */
    private String realmName = null;
      
    /**
     * The text for the digest.
     */
    private String digest = null;
    
    /**
     * The text for the roleNameCol.
     */
    private String roleNameCol = null;

    /**
     * The text for the userNameCol.
     */
    private String userNameCol = null;

    /**
     * The text for the passwordCol.
     */
    private String passwordCol = null;
    
    /**
     * The text for the driver.
     */
    private String driver = null;
        
    /**
     * The text for the role table.
     */
    private String roleTable = null;
    
    /**
     * The text for the user table.
     */
    private String userTable = null;
        
    /**
     * The text for the connection user name.
     */
    private String connectionName = null;
    
    /**
     * The text for the connection Password.
     */
    private String connectionPassword = null;
    
    /**
     * The text for the connection URL.
     */
    private String connectionURL = null;
    
    // ------------------------------------------------------------- Properties
    
    
    /**
     * Return the digest.
     */
    public String getDigest() {
        
        return this.digest;
        
    }
    
    /**
     * Set the digest.
     */
    public void setDigest(String digest) {
        
        this.digest = digest;
        
    }
    
    /**
     * Return the roleNameCol.
     */
    public String getRoleNameCol() {
        
        return this.roleNameCol;
        
    }
    
    /**
     * Set the roleNameCol.
     */
    public void setRoleNameCol(String roleNameCol) {
        
        this.roleNameCol = roleNameCol;
        
    }
    
    /**
     * Return the userNameCol.
     */
    public String getUserNameCol() {
        
        return this.userNameCol;
        
    }
    
    /**
     * Set the userNameCol.
     */
    public void setUserNameCol(String userNameCol) {
        
        this.userNameCol = userNameCol;
        
    }
    /**
     * Return the driver.
     */
    public String getDriver() {
        
        return this.driver;
        
    }
    
    /**
     * Set the driver.
     */
    public void setDriver(String driver) {
        
        this.driver = driver;
        
    }
    
    /**
     * Return the role table.
     */
    public String getRoleTable() {
        
        return this.roleTable;
        
    }
    
    /**
     * Set the roleTable.
     */
    public void setRoleTable(String roleTable) {
        
        this.roleTable = roleTable;
        
    }
    
    /**
     * Return the user table.
     */
    public String getUserTable() {
        
        return this.userTable;
        
    }
    
    /**
     * Set the user Table.
     */
    public void setUserTable(String userTable) {
        
        this.userTable = userTable;
        
    }
    
    /**
     * Return the passwordCol.
     */
    public String getPasswordCol() {
        
        return this.passwordCol;
        
    }
    
    /**
     * Set the passwordCol.
     */
    public void setPasswordCol(String passwordCol) {
        
        this.passwordCol = passwordCol;
        
    }
    
    
    /**
     * Return the connection name.
     */
    public String getConnectionName() {
        
        return this.connectionName;
        
    }
    
    /**
     * Set the connectionName.
     */
    public void setConnectionName(String connectionName) {
        
        this.connectionName = connectionName;
        
    }
    
    
    /**
     * Return the connection password.
     */
    public String getConnectionPassword() {
        
        return this.connectionPassword;
        
    }
    
    /**
     * Set the connection password.
     */
    public void setConnectionPassword(String connectionPassword) {
        
        this.connectionPassword = connectionPassword;
        
    }
    
    
    /**
     * Return the connection URL.
     */
    public String getConnectionURL() {
        
        return this.connectionURL;
        
    }
    
    /**
     * Set the connectionURL.
     */
    public void setConnectionURL(String connectionURL) {
        
        this.connectionURL = connectionURL;
        
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
        this.digest = null;
        this.driver = null;
        
        this.roleNameCol = null;
        this.userNameCol = null;
        this.passwordCol = null;
        this.userTable = null;
        this.roleTable = null;
        
        this.connectionName = null;
        this.connectionPassword = null;
        this.connectionURL = null;
        
    }
    
    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("UserDatabaseRealmForm[adminAction=");
        sb.append(getAdminAction());
        sb.append(",digest=");
        sb.append(digest);
        sb.append("',driver='");
        sb.append(driver);
        sb.append("',roleNameCol=");
        sb.append(roleNameCol);
        sb.append("',userNameCol=");
        sb.append(userNameCol);
        sb.append(",passwordCol=");
        sb.append(passwordCol);
        sb.append("',userTable='");
        sb.append(userTable);
        sb.append("',roleTable=");
        sb.append(roleTable);
        sb.append(",connectionName=");
        sb.append(connectionName);        
        sb.append("',connectionPassword=");
        sb.append(connectionPassword);
        sb.append(",connectionURL=");
        sb.append(connectionURL);
        sb.append("',objectName='");
        sb.append(getObjectName());
        sb.append("',realmType=");
        sb.append(getRealmType());
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
        //String type = request.getParameter("realmType");
        
        // front end validation when save is clicked.        
         //if (submit != null) {
             // the following fields are required.
            
            if ((driver == null) || (driver.length() < 1)) {
                errors.add("driver",
                new ActionError("error.driver.required"));
            }
         
            if ((roleNameCol == null) || (roleNameCol.length() < 1)) {
                errors.add("roleNameCol",
                new ActionError("error.roleNameCol.required"));
            }

            if ((userNameCol == null) || (userNameCol.length() < 1)) {
                errors.add("userNameCol",
                new ActionError("error.userNameCol.required"));
            }

             if ((passwordCol == null) || (passwordCol.length() < 1)) {
                errors.add("passwordCol",
                new ActionError("error.passwordCol.required"));
            }
            
            if ((userTable == null) || (userTable.length() < 1)) {
                errors.add("userTable",
                new ActionError("error.userTable.required"));
            }
            
            if ((roleTable == null) || (roleTable.length() < 1)) {
                errors.add("roleTable",
                new ActionError("error.roleTable.required"));
            }
            
            if ((connectionName == null) || (connectionName.length() < 1)) {
                errors.add("connectionName",
                new ActionError("error.connectionName.required"));
            }
            
            if ((connectionPassword == null) || (connectionPassword.length() < 1)) {
                errors.add("connectionPassword",
                new ActionError("error.connectionPassword.required"));
            }
            
             if ((connectionURL == null) || (connectionURL.length() < 1)) {
                errors.add("connectionURL",
                new ActionError("error.connectionURL.required"));
            }
        //}
                 
        return errors;
    }
}
