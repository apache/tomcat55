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


package org.apache.webapp.admin;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * Test <code>Action</code> that handles events from the tree control test
 * page.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class TreeControlTestAction extends Action {


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

        getServlet().log("Entered TreeControlTestAction:perform()");

        String name = null;
        HttpSession session = request.getSession();
        TreeControl control =
            (TreeControl) session.getAttribute("treeControlTest");

        // Handle a tree expand/contract event
        name = request.getParameter("tree");

        if (name != null) {
            getServlet().log("Tree expand/contract on " + name);

            TreeControlNode node = control.findNode(name);

            if (node != null){
                getServlet().log("Found Node: " + name);
                node.setExpanded(!node.isExpanded());
            }
        }else{
            getServlet().log("tree param is null");
        }

        // Handle a select item event
        name = request.getParameter("select");
        if (name != null) {
            getServlet().log("Select event on " + name);
            control.selectNode(name);
        }

        // Forward back to the test page
        return (mapping.findForward("Tree Control Test"));

    }


}
