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
import java.util.StringTokenizer;
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


/**
 * Test <code>Action</code> sets up  tree control data structure
 * for tree widget
 *
 * @author Jazmin Jonson
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class SetUpTreeAction extends Action {

    public static final String DOMAIN_KEY = "domain";
    public static final int INIT_PLUGIN_MAX = 10;
    public static final String TREEBUILDER_KEY = "treebuilders";
    public static final String ROOTNODENAME_KEY = "rootnodename";

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

        ApplicationServlet servlet = (ApplicationServlet)getServlet();

        // Getting init parms from web.xml

        // Get the string to be displayed as root node while rendering the tree
        String rootnodeName = 
            (String)servlet.getServletConfig().getInitParameter(ROOTNODENAME_KEY);
        
        String treeBuildersStr  =
            (String)servlet.getServletConfig().getInitParameter(TREEBUILDER_KEY);
        
        String domain  =
            (String)servlet.getServletConfig().getInitParameter(DOMAIN_KEY);
        
        
        // Make the root node and tree control
        
        // The root node gets rendered only if its value 
        // is set as an init-param in web.xml
        
        TreeControlNode root =
            new TreeControlNode("ROOT-NODE",
                                null, rootnodeName,
                                "setUpTree.do?select=ROOT-NODE",
                                "content", true, domain);
                
        TreeControl control = new TreeControl(root);
        
        if(treeBuildersStr != null) {
            Class treeBuilderImpl;
            TreeBuilder treeBuilderBase;

            ArrayList treeBuilders = new ArrayList(INIT_PLUGIN_MAX);
            int i = 0;
            StringTokenizer st = new StringTokenizer(treeBuildersStr, ",");
            while (st.hasMoreTokens()) {
                treeBuilders.add(st.nextToken().trim());
            }

            if(treeBuilders.size() == 0)
                treeBuilders.add(treeBuildersStr.trim());

            for(i = 0; i < treeBuilders.size(); i++) {

                try{
                    treeBuilderImpl = Class.forName((String)treeBuilders.get(i));
                    treeBuilderBase =
                        (TreeBuilder)treeBuilderImpl.newInstance();
                    treeBuilderBase.buildTree(control, servlet, request);
                }catch(Throwable t){
                    t.printStackTrace(System.out);
                }
            }
        }

        HttpSession session = request.getSession();
        session.setAttribute("treeControlTest", control);

         String  name = request.getParameter("select");
         if (name != null) {
            control.selectNode(name);
            // Forward back to the Blank page
            return (mapping.findForward("Blank"));
        }

         return (mapping.findForward("Tree Control Test"));

    }
}
