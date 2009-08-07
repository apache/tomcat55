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

import java.beans.PropertyEditorSupport;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.sql.Date;

/**
 * PropertyEditor implementation for a java.sql.Date property.
 *
 * @author Craig R. McClanahan
 * @revision $Date$ $Revision$
 */
public class DatePropertyEditor extends PropertyEditorSupport {


    // ----------------------------------------------------- Instance Variables


    /**
     * The date format to which dates converted by this property editor
     * must conform.
     */
    private SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");


    // --------------------------------------------------------- Public Methods


    /**
     * Convert our Date object into a String that conforms to our
     * specified <code>format</code>, and return it.  If this is not
     * possible, return <code>null</code>.
     */
    public String getAsText() {

        try {
            Date date = (Date) getValue();
            return (format.format(date));
        } catch (ClassCastException e) {
            return (null);
        } catch (IllegalArgumentException e) {
            return (null);
        }

    }


    /**
     * Convert the specified String value into a Date, if it conforms to
     * our specified <code>format</code> , else throw IllegalArgumentException.
     *
     * @param value String value to be converted
     *
     * @exception IllegalArgumentException if a conversion error occurs
     */
    public void setAsText(String value) throws IllegalArgumentException {

        // Validate the format of the input string
        if (value == null)
            throw new IllegalArgumentException
                ("Cannot convert null String to a Date");
        if (value.length() != 10)
            throw new IllegalArgumentException
                ("String '" + value + "' has invalid length " +
                 value.length());
        for (int i = 0; i < 10; i++) {
            char ch = value.charAt(i);
            if ((i == 2) || (i == 5)) {
                if (ch != '/')
                    throw new IllegalArgumentException
                        ("String '" + value + "' missing slash at index " +
                         i);
            } else {
                if (!Character.isDigit(ch))
                    throw new IllegalArgumentException
                        ("String '" + value + "' missing digit at index " +
                         i);
            }
        }

        // Convert the incoming value to a java.sql.Date
        java.util.Date temp = format.parse(value, new ParsePosition(0)); 
        java.sql.Date date = new java.sql.Date(temp.getTime());
        setValue(date);
    }

        
}
