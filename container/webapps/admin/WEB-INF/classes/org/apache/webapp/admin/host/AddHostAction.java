/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.webapp.admin.Lists;

/**
 * The <code>Action</code> that sets up <em>Add Host</em> transactions.
 *
 * @author Manveen Kaur
 * @version $Id$
 */

public class AddHostAction extends Action {

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

        // the service Name is needed to retrieve the engine mBean to
        // which the new host mBean will be added.
        String serviceName = request.getParameter("select");

        // Fill in the form values for display and editing
        HostForm hostFm = new HostForm();
        session.setAttribute("hostForm", hostFm);
        hostFm.setAdminAction("Create");
        hostFm.setObjectName("");
        hostFm.setHostName("");
        hostFm.setServiceName(serviceName);
        hostFm.setAppBase("");
        hostFm.setAutoDeploy("true");
        hostFm.setDeployXML("true");
        hostFm.setDeployOnStartup("true");
        hostFm.setUnpackWARs("true");   
        hostFm.setXmlNamespaceAware("false");
        hostFm.setXmlValidation("false");
        hostFm.setBooleanVals(Lists.getBooleanValues());

        // Forward to the host display page
        return (mapping.findForward("Host"));

    }


}
