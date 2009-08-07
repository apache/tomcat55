/*
 * Copyright 1999-2001,2004 The Apache Software Foundation.
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

import java.io.Serializable;

/**
 * Simple JavaBean to represent label-value pairs for use in collections
 * that are utilized by the <code>&lt;form:options&gt;</code> tag.
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class LabelValueBean implements Serializable {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new LabelValueBean with the specified values.
     *
     * @param label The label to be displayed to the user
     * @param value The value to be returned to the server
     */
    public LabelValueBean(String label, String value) {
        this.label = label;
        this.value = value;
    }


    // ------------------------------------------------------------- Properties


    /**
     * The label to be displayed to the user.
     */
    protected String label = null;

    public String getLabel() {
        return (this.label);
    }


    /**
     * The value to be returned to the server.
     */
    protected String value = null;

    public String getValue() {
        return (this.value);
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Return a string representation of this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("LabelValueBean[");
        sb.append(this.label);
        sb.append(", ");
        sb.append(this.value);
        sb.append("]");
        return (sb.toString());
    }


}
