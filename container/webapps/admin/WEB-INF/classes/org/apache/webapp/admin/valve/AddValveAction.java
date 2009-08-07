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

package org.apache.webapp.admin.valve;

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
 * The <code>Action</code> that sets up <em>Add Valve</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class AddValveAction extends Action {
        
    // the list for types of valves
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
        
        String valveTypes[] = new String[5];
        valveTypes[0] = "AccessLogValve";
        valveTypes[1] = "RemoteAddrValve";
        valveTypes[2] = "RemoteHostValve";
        valveTypes[3] = "RequestDumperValve";       
        valveTypes[4] = "SingleSignOn";
                     
        String parent = request.getParameter("parent");
        String type = request.getParameter("type");        
        if (type == null) 
            type = "AccessLogValve";    // default type is AccessLog
        
        types = new ArrayList();    
        // the first element in the select list should be the type selected
        types.add(new LabelValueBean(type,
                "AddValve.do?parent=" + 
                URLEncoder.encode(parent,TomcatTreeBuilder.URL_ENCODING) 
                + "&type=" + type));        
        for (int i=0; i< valveTypes.length; i++) {
            if (!type.equalsIgnoreCase(valveTypes[i])) {
                types.add(new LabelValueBean(valveTypes[i],
                "AddValve.do?parent=" + 
                URLEncoder.encode(parent,TomcatTreeBuilder.URL_ENCODING) 
                + "&type=" + valveTypes[i]));        
            }
        }
       
        if ("AccessLogValve".equalsIgnoreCase(type)) {
            createAccessLogger(session, parent);
        } else if ("RemoteAddrValve".equalsIgnoreCase(type)) {
            createRemoteAddrValve(session, parent);
        } else if ("RemoteHostValve".equalsIgnoreCase(type)) {
            createRemoteHostValve(session, parent);
        } else if ("RequestDumperValve".equalsIgnoreCase(type)) {
            createRequestDumperValve(session, parent);
        } else {
            //SingleSignOn
            createSingleSignOnValve(session, parent);
        }
        // Forward to the valve display page
        return (mapping.findForward(type));
        
    }

    private void createAccessLogger(HttpSession session, String parent) {

        AccessLogValveForm valveFm = new AccessLogValveForm();
        session.setAttribute("accessLogValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "AccessLogValve";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setPattern("");
        valveFm.setDirectory("logs");
        valveFm.setPrefix("access_log.");
        valveFm.setSuffix("");
        valveFm.setResolveHosts("false");
        valveFm.setRotatable("true");
        valveFm.setBooleanVals(Lists.getBooleanValues());
        valveFm.setValveTypeVals(types);        
    }

    private void createRemoteAddrValve(HttpSession session, String parent) {

        RemoteAddrValveForm valveFm = new RemoteAddrValveForm();
        session.setAttribute("remoteAddrValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "RemoteAddrValve";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setAllow("");
        valveFm.setDeny("");
        valveFm.setValveTypeVals(types);        
    }

    private void createRemoteHostValve(HttpSession session, String parent) {

        RemoteHostValveForm valveFm = new RemoteHostValveForm();
        session.setAttribute("remoteHostValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "RemoteHostValve";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setAllow("");
        valveFm.setDeny("");
        valveFm.setValveTypeVals(types);        
    }

    private void createRequestDumperValve(HttpSession session, String parent) {

        RequestDumperValveForm valveFm = new RequestDumperValveForm();
        session.setAttribute("requestDumperValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "RequestDumperValve";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setValveTypeVals(types);        
    }

    private void createSingleSignOnValve(HttpSession session, String parent) {

        SingleSignOnValveForm valveFm = new SingleSignOnValveForm();
        session.setAttribute("singleSignOnValveForm", valveFm);
        valveFm.setAdminAction("Create");
        valveFm.setObjectName("");
        valveFm.setParentObjectName(parent);
        String valveType = "SingleSignOn";
        valveFm.setNodeLabel("Valve (" + valveType + ")");
        valveFm.setValveType(valveType);
        valveFm.setValveTypeVals(types);        
    }

}
