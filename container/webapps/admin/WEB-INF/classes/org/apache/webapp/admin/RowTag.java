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
 * <p>Nested tag that represents an individual "instant table".  This tag
 * is valid <strong>only</strong> when nested within an TableTag tag.
 * This tag has the following user-settable attributes:</p>
 * <ul>
 * <li><strong>header</strong> - Is this  a header row?</li>
 * <li><strong>label</strong> - label to be displayed.</li>
 * <li><strong>data</strong> - data of the table data element.</li>
 * <li><strong>labelStyle</strong> - Style to be applied to the
 * label table data element.</li>
 * <li><strong>dataStyle</strong> - Style to be applied to the data table
 * data element.</li>
 *
 * </ul>
 *
 * @author Manveen Kaur
 * @version $Revision$ $Date$
 */

public class RowTag extends BodyTagSupport {
    
    /**
     * Is this the header row?
     */
    protected boolean header = false;
    
    public boolean getHeader() {
        return (this.header);
    }
    
    public void setHeader(boolean header) {
        this.header = header;
    }    
    
    /**
     * The label that will be rendered for this row's table data element.
     */
    protected String label = null;
   
    public void setLabel(String label) {
        this.label = label;
    }
    
    
    /**
     * The data of the table data element of this row.
     */
    protected String data = null;
    
    public void setData(String data) {
        this.data = data;
    }
    
    /**
     * The style of the label.
     */
    protected String labelStyle = null;
    
    public String getLabelStyle() {
        return (this.labelStyle);
    }
    
    public void setLabelStyle(String labelStyle) {
        this.labelStyle = labelStyle;
    }
    
    
    /**
     * The style of the data.
     */
    protected String dataStyle = null;
    
    public String getdataStyle() {
        return (this.dataStyle);
    }
    
    public void setdataStyle(String dataStyle) {
        this.dataStyle = dataStyle;
    }
 
    /**
     * The styleId for the label.
     */
    protected String styleId = null;
    
    public String getStyleId() {
        return (this.styleId);
    }
    
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }
    
        
    // --------------------------------------------------------- Public Methods
    
    
    /**
     * Process the start of this tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {
        
         // Do no further processing for now
        return (EVAL_BODY_TAG);
        
    }
    
    
    /**
     * Process the body text of this tag (if any).
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doAfterBody() throws JspException {
       
        return (SKIP_BODY);
        
    }
    
    
    /**
     * Record this action with our surrounding ActionsTag instance.
     *
     * @exception JspException if a processing error occurs
     */
    public int doEndTag() throws JspException {
        
        // Find our parent TableTag instance
        Tag parent = getParent();
        while ((parent != null) && !(parent instanceof TableTag)) {
            parent = parent.getParent();
        }
        if (parent == null) {
            throw new JspException("Must be nested in a TableTag instance");
        }
        TableTag table = (TableTag) parent;
        
        // Register the information for the row represented by
        // this row
        HttpServletResponse response =
        (HttpServletResponse) pageContext.getResponse();
        table.addRow(header, label, data, labelStyle, dataStyle, styleId);
        
        return (EVAL_PAGE);
        
    }
    
    
    /**
     * Release all state information set by this tag.
     */
    public void release() {
        
        //super.release();
        
        this.header= false;
        this.label = null;
        this.data = null;
        this.labelStyle = null;
        this.dataStyle = null;
        this.styleId = null;
        
    }
    
    
}
