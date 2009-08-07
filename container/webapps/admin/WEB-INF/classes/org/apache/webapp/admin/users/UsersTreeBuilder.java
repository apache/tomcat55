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


import java.net.URLEncoder;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;
import org.apache.webapp.admin.ApplicationServlet;
import org.apache.webapp.admin.TreeBuilder;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;


/**
 * Implementation of <code>TreeBuilder</code> that adds the nodes required
 * for administering the user database.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 4.1
 */

public class UsersTreeBuilder implements TreeBuilder {


    // ----------------------------------------------------- Instance Variables


    // ---------------------------------------------------- TreeBuilder Methods


    /**
     * Add the required nodes to the specified <code>treeControl</code>
     * instance.
     *
     * @param treeControl The <code>TreeControl</code> to which we should
     *  add our nodes
     * @param servlet The controller servlet for the admin application
     * @param request The servlet request we are processing
     */
    public void buildTree(TreeControl treeControl,
                          ApplicationServlet servlet,
                          HttpServletRequest request) {

        MessageResources resources = (MessageResources)
            servlet.getServletContext().getAttribute(Globals.MESSAGES_KEY);
        HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute(Globals.LOCALE_KEY);
        addSubtree(treeControl.getRoot(), resources, locale);

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Add the subtree of nodes required for user administration.
     *
     * @param root The root node of our tree control
     * @param resources The MessageResources for our localized messages
     *  messages
     */
    protected void addSubtree(TreeControlNode root,
                              MessageResources resources, Locale locale) {

        String databaseName = URLEncoder.encode
          ("Users:type=UserDatabase,database=UserDatabase");

        TreeControlNode subtree = new TreeControlNode
            ("Global User and Group Administration",
             "folder_16_pad.gif",
             resources.getMessage(locale, "users.treeBuilder.subtreeNode"),
             null,
             "content",
             true, "Users");
        TreeControlNode groups = new TreeControlNode
            ("Global Administer Groups",
             "Groups.gif",
             resources.getMessage(locale, "users.treeBuilder.groupsNode"),
             "users/listGroups.do?databaseName=" +
             URLEncoder.encode(databaseName) +
             "&forward=" +
             URLEncoder.encode("Groups List Setup"),
             "content",
             false, "Users");
        TreeControlNode roles = new TreeControlNode
            ("Global Administer Roles",
             "Roles.gif",
             resources.getMessage(locale, "users.treeBuilder.rolesNode"),
             "users/listRoles.do?databaseName=" +
             URLEncoder.encode(databaseName) +
             "&forward=" +
             URLEncoder.encode("Roles List Setup"),
             "content",
             false, "Users");
        TreeControlNode users = new TreeControlNode
            ("Global Administer Users",
             "Users.gif",
             resources.getMessage(locale, "users.treeBuilder.usersNode"),
             "users/listUsers.do?databaseName=" +
             URLEncoder.encode(databaseName) +
             "&forward=" +
             URLEncoder.encode("Users List Setup"),
             "content",
             false, "Users");

        root.addChild(subtree);
        subtree.addChild(users);
        subtree.addChild(groups);
        subtree.addChild(roles);

    }


}
