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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;


/**
 * <p>Nested tag that represents an individual "instant action".  This tag
 * is valid <strong>only</strong> when nested within an ActoinsTag tag.
 * This tag has the following user-settable attributes:</p>
 * <ul>
 * <li><strong>selected</strong> - Set to <code>true</code> if this action
 *     should be selected when the control is initially displayed.</li>
 * <li><strong>url</strong> - URL to which control should be transferred
 *     (in the current frame or window) if this action is selected.</li>
 * </ul>
 *
 * <p>In addition, the body content of this tag is used as the user-visible
 * label for the action, so that it may be conveniently localized.</p>
 *
 * <strong>FIXME</strong> - Internationalize the exception messages!
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class ActionTag extends BodyTagSupport {


    // ----------------------------------------------------- Instance Variables


    /**
     * The label that will be rendered for this action.
     */
    protected String label = null;


    // ------------------------------------------------------------- Properties


    /**
     * Should this action be selected when the control is initially displayed?
     */
    protected boolean selected = false;

    public boolean getSelected() {
        return (this.selected);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Should this action selection be disabled? 
     * e.g. Action separators should be disabled.
     */
    protected boolean disabled = false;

    public boolean getDisabled() {
        return (this.disabled);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * The URL to which control is transferred if this action is selected.
     */
    protected String url = null;

    public String getUrl() {
        return (this.url);
    }

    public void setUrl(String url) {
        this.url = url;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Process the start of this tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Initialize the holder for our label text
        this.label = null;

        // Do no further processing for now
        return (EVAL_BODY_TAG);

    }


    /**
     * Process the body text of this tag (if any).
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {

        String label = bodyContent.getString();
        if (label != null) {
            label = label.trim();
            if (label.length() > 0)
                this.label = label;
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
        while ((parent != null) && !(parent instanceof ActionsTag)) {
            parent = parent.getParent();
        }
        if ((parent == null) || !(parent instanceof ActionsTag))
            throw new JspException("Must be nested in an ActionsTag isntance");
        ActionsTag actions = (ActionsTag) parent;

        // Register the information for the action represented by
        // this action
        HttpServletRequest request =
            (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response =
            (HttpServletResponse) pageContext.getResponse();
        String path = null;
        if ((url != null) && (url.startsWith("/"))) {
            path = request.getContextPath() + url;
        } else {
            path = url;
        }
        actions.addAction(label, selected, disabled,
                          response.encodeURL(path));

        return (EVAL_PAGE);

    }


    /**
     * Release all state information set by this tag.
     */
    public void release() {

        this.label = null;
        this.selected = false;
        this.disabled = false;
        this.url = null;

    }


}
