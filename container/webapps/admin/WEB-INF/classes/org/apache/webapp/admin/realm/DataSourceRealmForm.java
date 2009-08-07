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
 * Form bean for the datasource realm page.
 *
 * @author Amy Roh
 * @version $Revision$ $Date$
 */

public final class DataSourceRealmForm extends RealmForm {
    
    // ----------------------------------------------------- Instance Variables
    
    /**
     * The text for the JNDI named JDBC DataSource for your database.
     */
    private String dataSourceName = null;
      
    /**
     * The text for the digest.
     */
    private String digest = null;
    
    /** 
     * The text for if the DataSource is local to the webapp.
     */
    private String localDataSource = "false";
    
    /**
     * The text for the roleNameCol.
     */
    private String roleNameCol = null;
    
    /**
     * The text for the userCredCol.
     */
    private String userCredCol = null;
    
    /**
     * The text for the userNameCol.
     */
    private String userNameCol = null;
        
    /**
     * The text for the userRoleTable.
     */
    private String userRoleTable = null;
    
    /**
     * The text for the user table.
     */
    private String userTable = null;
        
    /*
     * Represent boolean (true, false) values for unpackWARs etc.
     */
    private List booleanVals = null;
    
    // ------------------------------------------------------------- Properties
    
    
    /**
     * Return the dataSourceName.
     */
    public String getDataSourceName() {
        
        return this.dataSourceName;
        
    }
    
    /**
     * Set the dataSourceName.
     */
    public void setDataSourceName(String dataSourceName) {
        
        this.dataSourceName = dataSourceName;
        
    }
    
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
     * Return the localDataSource.
     */
    public String getLocalDataSource() {
        
        return this.localDataSource;
        
    }
    
    /**
     * Set the localDataSource.
     */
    public void setLocalDataSource(String localDataSource) {
        
        this.localDataSource = localDataSource;
        
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
     * Return the userCredCol.
     */
    public String getUserCredCol() {
        
        return this.userCredCol;
        
    }
    
    /**
     * Set the userCredCol.
     */
    public void setUserCredCol(String userCredCol) {
        
        this.userCredCol = userCredCol;
        
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
     * Return the user role table.
     */
    public String getUserRoleTable() {
        
        return this.userRoleTable;
        
    }
    
    /**
     * Set the user role table.
     */
    public void setUserRoleTable(String userRoleTable) {
        
        this.userRoleTable = userRoleTable;
        
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
    
    // --------------------------------------------------------- Public Methods
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        super.reset(mapping, request);   
        this.dataSourceName = null;
        this.digest = null;
        this.localDataSource = "false";
        
        this.roleNameCol = null;
        this.userCredCol = null;
        this.userNameCol = null;
        this.userTable = null;
        this.userRoleTable = null;
        
    }
    
    /**
     * Render this object as a String.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("DataSourceRealmForm[adminAction=");
        sb.append(getAdminAction());
        sb.append(",dataSourceName=");
        sb.append(dataSourceName);
        sb.append(",digest=");
        sb.append(digest);
        sb.append("',localDataSource='");
        sb.append(localDataSource);
        sb.append("',roleNameCol=");
        sb.append(roleNameCol);
        sb.append("',userCredCol=");
        sb.append(userCredCol);
        sb.append("',userNameCol=");
        sb.append(userNameCol);
        sb.append("',userRoleTable=");
        sb.append(userRoleTable);
        sb.append("',userTable='");
        sb.append(userTable);
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
            
            if ((dataSourceName == null) || (dataSourceName.length() < 1)) {
                errors.add("dataSourceName",
                new ActionError("error.dataSourceName.required"));
            }
         
            if ((roleNameCol == null) || (roleNameCol.length() < 1)) {
                errors.add("roleNameCol",
                new ActionError("error.roleNameCol.required"));
            }

            if ((userCredCol == null) || (userCredCol.length() < 1)) {
                errors.add("userCredCol",
                new ActionError("error.userCredCol.required"));
            }
        
            if ((userNameCol == null) || (userNameCol.length() < 1)) {
                errors.add("userNameCol",
                new ActionError("error.userNameCol.required"));
            }
            
            if ((userRoleTable == null) || (userRoleTable.length() < 1)) {
                errors.add("userRoleTable",
                new ActionError("error.userRoleTable.required"));
            }
        
            if ((userTable == null) || (userTable.length() < 1)) {
                errors.add("userTable",
                new ActionError("error.userTable.required"));
            }
            
        //}
                 
        return errors;
    }
}
