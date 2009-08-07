/*
 * Copyright 1999, 2000 ,2004 The Apache Software Foundation.
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

package org.apache.tester;


import java.io.Serializable;


/**
 * Simple JavaBean to use for context attribute tests.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class ContextBean implements Serializable {


    // ------------------------------------------------------------- Properties


    /**
     * The lifecycle events that have happened on this bean instance.
     */
    protected String lifecycle = "";

    public String getLifecycle() {
        return (this.lifecycle);
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }


    /**
     * A string property.
     */
    protected String stringProperty = "Default String Property Value";

    public String getStringProperty() {
        return (this.stringProperty);
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return a string representation of this bean.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("ContextBean[lifecycle=");
        sb.append(lifecycle);
        sb.append(", stringProperty=");
        sb.append(this.stringProperty);
        sb.append("]");
        return (sb.toString());

    }


}

