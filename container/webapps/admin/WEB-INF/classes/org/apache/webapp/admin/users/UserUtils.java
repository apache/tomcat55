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


import java.util.Arrays;
import javax.management.MBeanServer;
import javax.management.ObjectName;


/**
 * <p>Shared utility methods for the user database administration module.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 4.1
 */

public class UserUtils {


    // --------------------------------------------------------- Public Methods


    /**
     * Construct and return a GroupsForm identifying all currently defined
     * groups in the specified user database.
     *
     * @param mserver MBeanServer to be consulted
     * @param databaseName MBean Name of the user database to be consulted
     *
     * @exception Exception if an error occurs
     */
    public static GroupsForm getGroupsForm(MBeanServer mserver,
                                           String databaseName)
        throws Exception {

        ObjectName dname = new ObjectName(databaseName);
        String results[] =
            (String[]) mserver.getAttribute(dname, "groups");
        if (results == null) {
            results = new String[0];
        }
        Arrays.sort(results);

        GroupsForm groupsForm = new GroupsForm();
        groupsForm.setDatabaseName(databaseName);
        groupsForm.setGroups(results);
        return (groupsForm);

    }


    /**
     * Construct and return a RolesForm identifying all currently defined
     * roles in the specified user database.
     *
     * @param mserver MBeanServer to be consulted
     * @param databaseName MBean Name of the user database to be consulted
     *
     * @exception Exception if an error occurs
     */
    public static RolesForm getRolesForm(MBeanServer mserver,
                                           String databaseName)
        throws Exception {

        ObjectName dname = new ObjectName(databaseName);
        String results[] =
            (String[]) mserver.getAttribute(dname, "roles");
        if (results == null) {
            results = new String[0];
        }
        Arrays.sort(results);

        RolesForm rolesForm = new RolesForm();
        rolesForm.setDatabaseName(databaseName);
        rolesForm.setRoles(results);
        return (rolesForm);

    }


    /**
     * Construct and return a UsersForm identifying all currently defined
     * users in the specified user database.
     *
     * @param mserver MBeanServer to be consulted
     * @param databaseName MBean Name of the user database to be consulted
     *
     * @exception Exception if an error occurs
     */
    public static UsersForm getUsersForm(MBeanServer mserver,
                                           String databaseName)
        throws Exception {

        ObjectName dname = new ObjectName(databaseName);
        String results[] =
            (String[]) mserver.getAttribute(dname, "users");
        if (results == null) {
            results = new String[0];
        }
        Arrays.sort(results);

        UsersForm usersForm = new UsersForm();
        usersForm.setDatabaseName(databaseName);
        usersForm.setUsers(results);
        return (usersForm);

    }


}
