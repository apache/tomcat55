/*
 * Copyright 2001-2002,2004 The Apache Software Foundation.
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

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.TomcatTreeBuilder;
import org.apache.webapp.admin.LabelValueBean;
import org.apache.webapp.admin.Lists;

/**
 * The <code>Action</code> that sets up <em>Add Realm</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class AddRealmAction extends Action {

    // the list for types of realms
    private ArrayList types = null;

    // --------------------------------------------------------- Public Methods

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws IOException, ServletException {

        // Acquire the resources that we need
        HttpSession session = request.getSession();
        Locale locale = getLocale(request);
        MessageResources resources = getResources(request);
        // Fill in the form values for display and editing

        String realmTypes[] = new String[5];
        realmTypes[0] = "UserDatabaseRealm";
        realmTypes[1] = "JNDIRealm";
        realmTypes[2] = "MemoryRealm";
        realmTypes[3] = "JDBCRealm";
        realmTypes[4] = "DataSourceRealm";

        String parent = request.getParameter("parent");
        String type = request.getParameter("type");
        if (type == null)
            type = "UserDatabaseRealm";    // default type is UserDatabaseRealm
        
        types = new ArrayList();
        // the first element in the select list should be the type selected
        types.add(new LabelValueBean(type,
                "AddRealm.do?parent=" + 
                URLEncoder.encode(parent,TomcatTreeBuilder.URL_ENCODING)
                + "&type=" + type));
        for (int i=0; i< realmTypes.length; i++) {
            if (!type.equalsIgnoreCase(realmTypes[i])) {
                types.add(new LabelValueBean(realmTypes[i],
                "AddRealm.do?parent=" + 
                URLEncoder.encode(parent,TomcatTreeBuilder.URL_ENCODING)
                + "&type=" + realmTypes[i]));
            }
        }

        if ("UserDatabaseRealm".equalsIgnoreCase(type)) {
            createUserDatabaseRealm(session, parent);
        } else if ("JNDIRealm".equalsIgnoreCase(type)) {
            createJNDIRealm(session, parent);
        } else if ("MemoryRealm".equalsIgnoreCase(type)) {
            createMemoryRealm(session, parent);
        } else if ("JDBCRealm".equalsIgnoreCase(type)){
            createJDBCRealm(session, parent);
        } else if ("DataSourceRealm".equalsIgnoreCase(type)) {
            createDataSourceRealm(session, parent);
        }
        // Forward to the realm display page
        return (mapping.findForward(type));

    }

    private void createUserDatabaseRealm(HttpSession session, String parent) {

        UserDatabaseRealmForm realmFm = new UserDatabaseRealmForm();
        session.setAttribute("userDatabaseRealmForm", realmFm);
        realmFm.setAdminAction("Create");
        realmFm.setObjectName("");
        realmFm.setParentObjectName(parent);
        String realmType = "UserDatabaseRealm";
        realmFm.setNodeLabel("Realm (" + realmType + ")");
        realmFm.setRealmType(realmType);
        realmFm.setResource("");
        realmFm.setRealmTypeVals(types);
    }

    private void createJNDIRealm(HttpSession session, String parent) {

        JNDIRealmForm realmFm = new JNDIRealmForm();
        session.setAttribute("jndiRealmForm", realmFm);
        realmFm.setAdminAction("Create");
        realmFm.setObjectName("");
        realmFm.setParentObjectName(parent);
        String realmType = "JNDIRealm";
        realmFm.setNodeLabel("Realm (" + realmType + ")");
        realmFm.setRealmType(realmType);
        realmFm.setDigest("");
        realmFm.setRoleBase("");
        realmFm.setUserSubtree("false");
        realmFm.setRoleSubtree("false");
        realmFm.setRolePattern("");
        realmFm.setUserRoleName("");
        realmFm.setRoleName("");
        realmFm.setRoleBase("");
        realmFm.setContextFactory("");
        realmFm.setUserPattern("");
        realmFm.setUserSearch("");
        realmFm.setUserPassword("");
        realmFm.setConnectionName("");
        realmFm.setConnectionPassword("");
        realmFm.setConnectionURL("");
        realmFm.setSearchVals(Lists.getBooleanValues());
        realmFm.setRealmTypeVals(types);
    }

    private void createMemoryRealm(HttpSession session, String parent) {

        MemoryRealmForm realmFm = new MemoryRealmForm();
        session.setAttribute("memoryRealmForm", realmFm);
        realmFm.setAdminAction("Create");
        realmFm.setObjectName("");
        realmFm.setParentObjectName(parent);
        String realmType = "MemoryRealm";
        realmFm.setNodeLabel("Realm (" + realmType + ")");
        realmFm.setRealmType(realmType);
        realmFm.setPathName("");
        realmFm.setRealmTypeVals(types);
    }

    private void createJDBCRealm(HttpSession session, String parent) {

        JDBCRealmForm realmFm = new JDBCRealmForm();
        session.setAttribute("jdbcRealmForm", realmFm);
        realmFm.setAdminAction("Create");
        realmFm.setObjectName("");
        realmFm.setParentObjectName(parent);
        String realmType = "JDBCRealm";
        realmFm.setNodeLabel("Realm (" + realmType + ")");
        realmFm.setRealmType(realmType);
        realmFm.setDigest("");
        realmFm.setDriver("");
        realmFm.setRoleNameCol("");
        realmFm.setPasswordCol("");
        realmFm.setUserTable("");
        realmFm.setRoleTable("");
        realmFm.setConnectionName("");
        realmFm.setConnectionPassword("");
        realmFm.setConnectionURL("");
        realmFm.setRealmTypeVals(types);
    }
    
    private void createDataSourceRealm(HttpSession session, String parent) {

        DataSourceRealmForm realmFm = new DataSourceRealmForm();
        session.setAttribute("dataSourceRealmForm", realmFm);
        realmFm.setAdminAction("Create");
        realmFm.setObjectName("");
        realmFm.setParentObjectName(parent);
        String realmType = "DataSourceRealm";
        realmFm.setNodeLabel("Realm (" + realmType + ")");
        realmFm.setRealmType(realmType);
        realmFm.setDataSourceName("");
        realmFm.setDigest("");
        realmFm.setLocalDataSource("false");
        realmFm.setRoleNameCol("");
        realmFm.setUserCredCol("");
        realmFm.setUserNameCol("");
        realmFm.setUserRoleTable("");
        realmFm.setUserTable("");
        realmFm.setRealmTypeVals(types);
        realmFm.setBooleanVals(Lists.getBooleanValues());
    }


}
