/*
 * Copyright 1999,2004 The Apache Software Foundation.
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


package org.apache.catalina.util;


import java.io.InputStream;
import java.util.Properties;


/**
 * Simple utility module to make it easy to plug in the server identifier
 * when integrating Tomcat.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class ServerInfo {


    // ------------------------------------------------------- Static Variables


    /**
     * The server information String with which we identify ourselves.
     */
    private static String serverInfo = null;

    static {

        try {
            InputStream is = ServerInfo.class.getResourceAsStream
                ("/org/apache/catalina/util/ServerInfo.properties");
            Properties props = new Properties();
            props.load(is);
            is.close();
            serverInfo = props.getProperty("server.info");
        } catch (Throwable t) {
            ;
        }
        if (serverInfo == null)
            serverInfo = "Apache Tomcat";

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return the server identification for this version of Tomcat.
     */
    public static String getServerInfo() {

        return (serverInfo);

    }


    public static void main(String args[]) {
        System.out.println("Version: " + getServerInfo());
    }

}
