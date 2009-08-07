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


package org.apache.catalina.launcher;


import java.util.ArrayList;
import org.apache.commons.launcher.LaunchCommand;
import org.apache.commons.launcher.LaunchFilter;
import org.apache.tools.ant.BuildException;


/**
 * This class implements the LaunchFilter interface. This class is designed to
 * unconditionally force the "waitforchild" attribute for certain Catalina
 * applications to true.
 *
 * @author Patrick Luby
 */
public class CatalinaLaunchFilter implements LaunchFilter {

    //----------------------------------------------------------- Static Fields

    /**
     * The Catalina bootstrap class name.
     */
    private static String CATALINA_BOOTSTRAP_CLASS_NAME = "org.apache.catalina.startup.Bootstrap";

    //----------------------------------------------------------------- Methods

    /**
     * This method allows dynamic configuration and error checking of the
     * attributes and nested elements in a "launch" task that is launching a
     * Catalina application. This method evaluates the nested command line
     * arguments and, depending on which class is specified in the task's
     * "classname" attribute, may force the application to run
     * in the foreground by forcing the "waitforchild" attribute to "true".
     *
     * @param launchCommand a configured instance of the {@link LaunchCommand}
     *  class
     * @throws BuildException if any errors occur
     */
    public void filter(LaunchCommand launchCommand) throws BuildException {

        // Get attributes
        String mainClassName = launchCommand.getClassname();
        boolean waitForChild = launchCommand.getWaitforchild();
        ArrayList argsList = launchCommand.getArgs();
        String[] args = (String[])argsList.toArray(new String[argsList.size()]);

        // Evaluate main class
        if (CatalinaLaunchFilter.CATALINA_BOOTSTRAP_CLASS_NAME.equals(mainClassName)) {
            // If "start" is not the last argument, make "waitforchild" true
            if (args.length == 0 || !"start".equals(args[args.length - 1])) {
                launchCommand.setWaitforchild(true);
                return;
            }

            // If "start" is the last argument, make sure that all of the
            // preceding arguments are OK for running in the background
            for (int i = 0; i < args.length - 1; i++) {
                if ("-config".equals(args[i])) {
                    // Skip next argument since it should be a file
                    if (args.length > i + 1) {
                        i++;
                    } else {
                        launchCommand.setWaitforchild(true);
                        return;
                    }
                } else if ("-debug".equals(args[i])) {
                    // Do nothing
                } else if ("-nonaming".equals(args[i])) {
                    // Do nothing
                } else {
                     launchCommand.setWaitforchild(true);
                     return;
                }
            }
        }

    }

}
