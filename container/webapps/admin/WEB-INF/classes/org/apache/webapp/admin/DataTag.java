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
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;


/**
 * <p>Nested tag that represents an individual "data" for a row.  This tag
 * is valid <strong>only</strong> when nested within a RowTag tag.
 *
 * <p>In addition, the body content of this tag is used as the user-visible
 * data for the action, so that it may be conveniently localized.</p>
 *
 * <strong>FIXME</strong> - Internationalize the exception messages!
 *
 * @author Manveen Kaur
 * @version $Revision$
 */

public class DataTag extends BodyTagSupport {


    // ----------------------------------------------------- Instance Variables


    /**
     * The data that will be rendered for this table row.
     */
    protected String data = null;


    // --------------------------------------------------------- Public Methods


    /**
     * Process the start of this tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Initialize the holder for our data text
        this.data = null;

        // Do no further processing for now
        return (EVAL_BODY_TAG);

    }


    /**
     * Process the body text of this tag (if any).
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {

        String data = bodyContent.getString();
        if (data != null) {
            data = data.trim();
            if (data.length() > 0)
                this.data = data;
        }
        return (SKIP_BODY);

    }


    /**
     * Record this action with our surrounding ActionsTag instance.
     *
     * @exception JspException if a processing error occurs
     */
    public int doEndTag() throws JspException {

        // Find our parent ActionsTag instance
        Tag parent = getParent();
        if ((parent == null) || !(parent instanceof RowTag))
            throw new JspException("Must be nested in a rowTag isntance");
        RowTag row = (RowTag) parent;

        // Register the information for the action represented by
        // this action
        HttpServletResponse response =
            (HttpServletResponse) pageContext.getResponse();
        row.setData(data);
        
        return (EVAL_PAGE);

    }


    /**
     * Release all state information set by this tag.
     */
    public void release() {

        this.data = null;
    }


}
