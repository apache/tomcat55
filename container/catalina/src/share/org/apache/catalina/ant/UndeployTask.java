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


package org.apache.catalina.ant;


import org.apache.tools.ant.BuildException;


/**
 * Ant task that implements the <code>/undeploy</code> command, supported by
 * the Tomcat manager application.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 * @since 4.1
 */
public class UndeployTask extends AbstractCatalinaTask {


    // ------------------------------------------------------------- Properties
    /**
     * Whether to fail (with a BuildException) if
     * an error occurs.  The default behavior is
     * to do so.
     */
    protected boolean failOnError = true;

    /**
     * Returns the value of the failOnError
     * property.
     */
    public boolean isFailOnError() {
      return failOnError;
    }
 
    /**
     * Sets the value of the failOnError property.
     *
     * @param newFailOnError New attribute value
     */
    public void setFailOnError(boolean newFailOnError) {
      failOnError = newFailOnError;
    }

    /**
     * The context path of the web application we are managing.
     */
    protected String path = null;

    public String getPath() {
        return (this.path);
    }

    public void setPath(String path) {
        this.path = path;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Execute the requested operation.
     *
     * @exception BuildException if an error occurs
     */
    public void execute() throws BuildException {

        super.execute();
        if (path == null) {
            throw new BuildException
                ("Must specify 'path' attribute");
        }

        try {
          execute("/undeploy?path=" + this.path);
	} catch (BuildException e) {
	  if( isFailOnError() ) {
	    throw e;
          }
        }

    }


}
