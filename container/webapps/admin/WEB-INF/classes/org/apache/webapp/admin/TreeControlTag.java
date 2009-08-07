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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;


/**
 * <p>JSP custom tag that renders a tree control represented by the
 * <code>TreeControl</code> and <code>TreeControlNode</code> classes.
 * This tag has the following user-settable attributes:</p>
 * <ul>
 * <li><strong>action</strong> - Hyperlink to which expand/contract actions
 *     should be sent, with a string "<code>{name}</code> marking where
 *     the node name of the affected node should be included.</li>
 * <li><strong>images</strong> - Name of the directory containing the images
 *     for our icons, relative to the page including this tag.  If not
 *     specified, defaults to "images".</li>
 * <li><strong>scope</strong> - Attribute scope in which the <code>tree</code>
 *     attribute is to be found (page, request, session, application).  If
 *     not specified, the attribute is searched for in all scopes.</li>
 * <li><strong>style</strong> - CSS style <code>class</code> to be applied
 *     to be applied to the entire rendered output of the tree control.
 *     If not specified, no style class is applied.</li>
 * <li><strong>styleSelected</strong> - CSS style <code>class</code> to be
 *     applied to the text of any element that is currently selected.  If not
 *     specified, no additional style class is applied.</li>
 * <li><strong>styleUnselected</strong> - CSS style <code>class</code> to be
 *     applied to the text of any element that is not currently selected.
 *     If not specified, no additional style class is applied.</li>
 * <li><strong>tree</strong> - Attribute name under which the
 *     <code>TreeControl</code> bean of the tree we are rendering
 *     is stored, in the scope specified by the <code>scope</code>
 *     attribute.  This attribute is required.</li>
 * </ul>
 *
 * <strong>FIXME</strong> - Internationalize the exception messages!
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class TreeControlTag extends TagSupport {


    /**
     * The default directory name for icon images.
     */
    static final String DEFAULT_IMAGES = "images";


    /**
     * The names of tree state images that we need.
     */
    static final String IMAGE_HANDLE_DOWN_LAST =    "handledownlast.gif";
    static final String IMAGE_HANDLE_DOWN_MIDDLE =  "handledownmiddle.gif";
    static final String IMAGE_HANDLE_RIGHT_LAST =   "handlerightlast.gif";
    static final String IMAGE_HANDLE_RIGHT_MIDDLE = "handlerightmiddle.gif";
    static final String IMAGE_LINE_LAST =           "linelastnode.gif";
    static final String IMAGE_LINE_MIDDLE =         "linemiddlenode.gif";
    static final String IMAGE_LINE_VERTICAL =       "linevertical.gif";


    // ------------------------------------------------------------- Properties


    /**
     * The hyperlink to be used for submitting requests to expand and
     * contract tree nodes.  The placeholder "<code>{name}</code>" will
     * be replaced by the <code>name</code> property of the current
     * tree node.
     */
    protected String action = null;

    public String getAction() {
        return (this.action);
    }

    public void setAction(String action) {
        this.action = action;
    }


    /**
     * The name of the directory containing the images for our icons,
     * relative to the page including this tag.
     */
    protected String images = DEFAULT_IMAGES;

    public String getImages() {
        return (this.images);
    }

    public void setImages(String images) {
        this.images = images;
    }


    /**
     * The name of the scope in which to search for the <code>tree</code>
     * attribute.  Must be "page", "request", "session", or "application"
     * (or <code>null</code> for an ascending-visibility search).
     */
    protected String scope = null;

    public String getScope() {
        return (this.scope);
    }

    public void setScope(String scope) {
        if (!"page".equals(scope) &&
            !"request".equals(scope) &&
            !"session".equals(scope) &&
            !"application".equals(scope))
            throw new IllegalArgumentException("Invalid scope '" +
                                               scope + "'");
        this.scope = scope;
    }


    /**
     * The CSS style <code>class</code> to be applied to the entire tree.
     */
    protected String style = null;

    public String getStyle() {
        return (this.style);
    }

    public void setStyle(String style) {
        this.style = style;
    }


    /**
     * The CSS style <code>class</code> to be applied to the text
     * of selected nodes.
     */
    protected String styleSelected = null;

    public String getStyleSelected() {
        return (this.styleSelected);
    }

    public void setStyleSelected(String styleSelected) {
        this.styleSelected = styleSelected;
    }


    /**
     * The CSS style <code>class</code> to be applied to the text
     * of unselected nodes.
     */
    protected String styleUnselected = null;

    public String getStyleUnselected() {
        return (this.styleUnselected);
    }

    public void setStyleUnselected(String styleUnselected) {
        this.styleUnselected = styleUnselected;
    }


    /**
     * The name of the attribute (in the specified scope) under which our
     * <code>TreeControl</code> instance is stored.
     */
    protected String tree = null;

    public String getTree() {
        return (this.tree);
    }

    public void setTree(String tree) {
        this.tree = tree;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Render this tree control.
     *
     * @exception JspException if a processing error occurs
     */
    public int doEndTag() throws JspException {

        TreeControl treeControl = getTreeControl();
        JspWriter out = pageContext.getOut();
        try {
            out.print
                ("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"");
            if (style != null) {
                out.print(" class=\"");
                out.print(style);
                out.print("\"");
            }
            out.println(">");
            int level = 0;
            TreeControlNode node = treeControl.getRoot();
            render(out, node, level, treeControl.getWidth(), true);
            out.println("</table>");
        } catch (IOException e) {
            throw new JspException(e);
        }

        return (EVAL_PAGE);

    }


    /**
     * Release all state information set by this tag.
     */
    public void release() {

        this.action = null;
        this.images = DEFAULT_IMAGES;
        this.scope = null;
        this.style = null;
        this.styleSelected = null;
        this.styleUnselected = null;
        this.tree = null;

    }


    // ------------------------------------------------------ Protected Methods


    /**
     * Return the <code>TreeControl</code> instance for the tree control that
     * we are rendering.
     *
     * @exception JspException if no TreeControl instance can be found
     */
    protected TreeControl getTreeControl() throws JspException {

        Object treeControl = null;
        if (scope == null)
            treeControl = pageContext.findAttribute(tree);
        else if ("page".equals(scope))
            treeControl =
                pageContext.getAttribute(tree, PageContext.PAGE_SCOPE);
        else if ("request".equals(scope))
            treeControl =
                pageContext.getAttribute(tree, PageContext.REQUEST_SCOPE);
        else if ("session".equals(scope))
            treeControl =
                pageContext.getAttribute(tree, PageContext.SESSION_SCOPE);
        else if ("application".equals(scope))
            treeControl =
                pageContext.getAttribute(tree, PageContext.APPLICATION_SCOPE);
        if (treeControl == null)
            throw new JspException("Cannot find tree control attribute '" +
                                   tree + "'");
        else if (!(treeControl instanceof TreeControl))
            throw new JspException("Invalid tree control attribute '" +
                                   tree + "'");
        else
            return ((TreeControl) treeControl);

    }


    /**
     * Render the specified node, as controlled by the specified parameters.
     *
     * @param out The <code>JspWriter</code> to which we are writing
     * @param node The <code>TreeControlNode</code> we are currently
     *  rendering
     * @param level The indentation level of this node in the tree
     * @param width Total displayable width of the tree
     * @param last Is this the last node in a list?
     *
     * @exception IOException if an input/output error occurs
     */
    protected void render(JspWriter out, TreeControlNode node,
                          int level, int width, boolean last)
        throws IOException {

        HttpServletResponse response =
            (HttpServletResponse) pageContext.getResponse();
    
        // if the node is root node and the label value is
        // null, then do not render root node in the tree.
        
        if ("ROOT-NODE".equalsIgnoreCase(node.getName()) &&
        (node.getLabel() == null)) {
            // Render the children of this node
            TreeControlNode children[] = node.findChildren();
            int lastIndex = children.length - 1;
            int newLevel = level + 1;
            for (int i = 0; i < children.length; i++) {
                render(out, children[i], newLevel, width, i == lastIndex);
            }
            return;
        }
        
        // Render the beginning of this node
        out.println("  <tr valign=\"middle\">");

        // Create the appropriate number of indents
        for (int i = 0; i < level; i++) {
            int levels = level - i;
            TreeControlNode parent = node;
            for (int j = 1; j <= levels; j++)
                parent = parent.getParent();
            if (parent.isLast())
                out.print("    <td></td>");
            else {
                out.print("    <td><img src=\"");
                out.print(images);
                out.print("/");
                out.print(IMAGE_LINE_VERTICAL);
                out.print("\" alt=\"\" border=\"0\"></td>");
            }
            out.println();
        }

        // Render the tree state image for this node

        // HACK to take into account special characters like = and &
        // in the node name, could remove this code if encode URL
        // and later request.getParameter() could deal with = and &
        // character in parameter values. 
        String encodedNodeName = URLEncoder.encode(node.getName(),TomcatTreeBuilder.URL_ENCODING);

        String action = replace(getAction(), "{name}", encodedNodeName);

        
        String updateTreeAction =
            replace(getAction(), "tree={name}", "select=" + encodedNodeName);
        updateTreeAction =
            ((HttpServletResponse) pageContext.getResponse()).
            encodeURL(updateTreeAction);

        out.print("    <td>");
        if ((action != null) && !node.isLeaf()) {
            out.print("<a href=\"");
            out.print(response.encodeURL(action));
            out.print("\">");
        }
        out.print("<img src=\"");
        out.print(images);
        out.print("/");
        if (node.isLeaf()) {
            if (node.isLast())
                out.print(IMAGE_LINE_LAST);
            else
                out.print(IMAGE_LINE_MIDDLE);
            out.print("\" alt=\"");
        } else if (node.isExpanded()) {
            if (node.isLast())
                out.print(IMAGE_HANDLE_DOWN_LAST);
            else
                out.print(IMAGE_HANDLE_DOWN_MIDDLE);
            out.print("\" alt=\"close node");
        } else {
            if (node.isLast())
                out.print(IMAGE_HANDLE_RIGHT_LAST);
            else
                out.print(IMAGE_HANDLE_RIGHT_MIDDLE);
            out.print("\" alt=\"expand node");
        }
        out.print("\" border=\"0\">");
        if ((action != null) && !node.isLeaf())
            out.print("</a>");
        out.println("</td>");

        // Calculate the hyperlink for this node (if any)
        String hyperlink = null;
        if (node.getAction() != null)
            hyperlink = ((HttpServletResponse) pageContext.getResponse()).
                encodeURL(node.getAction());

        // Render the icon for this node (if any)
        out.print("    <td colspan=\"");
        out.print(width - level + 1);
        out.print("\">");
        if (node.getIcon() != null) {
            if (hyperlink != null) {
                out.print("<a href=\"");
                out.print(hyperlink);
                out.print("\"");
                String target = node.getTarget();
                if(target != null) {
                    out.print(" target=\"");
                    out.print(target);
                    out.print("\"");
                }
                // to refresh the tree in the same 'self' frame
                out.print(" onclick=\"");
                out.print("self.location.href='" + updateTreeAction + "'");
                out.print("\"");
                out.print(">");
            }
            out.print("<img src=\"");
            out.print(images);
            out.print("/");
            out.print(node.getIcon());
            out.print("\" alt=\"");
            out.print("\" border=\"0\">");
            if (hyperlink != null)
                out.print("</a>");
        }

        // Render the label for this node (if any)

        if (node.getLabel() != null) {
            String labelStyle = null;
            if (node.isSelected() && (styleSelected != null))
                labelStyle = styleSelected;
            else if (!node.isSelected() && (styleUnselected != null))
                labelStyle = styleUnselected;
            if (hyperlink != null) {
                // Note the leading space so that the text has some space
                // between it and any preceding images
                out.print(" <a href=\"");
                out.print(hyperlink);
                out.print("\"");
                String target = node.getTarget();
                if(target != null) {
                    out.print(" target=\"");
                    out.print(target);
                    out.print("\"");
                }
                if (labelStyle != null) {
                    out.print(" class=\"");
                    out.print(labelStyle);
                    out.print("\"");
                }
                // to refresh the tree in the same 'self' frame
                out.print(" onclick=\"");
                out.print("self.location.href='" + updateTreeAction + "'");
                out.print("\"");
                out.print(">");
            } else if (labelStyle != null) {
                out.print("<span class=\"");
                out.print(labelStyle);
                out.print("\">");
            }
            out.print(node.getLabel());
            if (hyperlink != null)
                out.print("</a>");
            else if (labelStyle != null)
                out.print("</span>");
        }
        out.println("</td>");

        // Render the end of this node
        out.println("  </tr>");

        // Render the children of this node
        if (node.isExpanded()) {
            TreeControlNode children[] = node.findChildren();
            int lastIndex = children.length - 1;
            int newLevel = level + 1;
            for (int i = 0; i < children.length; i++) {
                render(out, children[i], newLevel, width, i == lastIndex);
            }
        }

    }


    /**
     * Replace any occurrence of the specified placeholder in the specified
     * template string with the specified replacement value.
     *
     * @param template Pattern string possibly containing the placeholder
     * @param placeholder Placeholder expression to be replaced
     * @param value Replacement value for the placeholder
     */
    protected String replace(String template, String placeholder,
                             String value) {

        if (template == null)
            return (null);
        if ((placeholder == null) || (value == null))
            return (template);
        while (true) {
            int index = template.indexOf(placeholder);
            if (index < 0)
                break;
            StringBuffer temp = new StringBuffer(template.substring(0, index));
            temp.append(value);
            temp.append(template.substring(index + placeholder.length()));
            template = temp.toString();
        }
        return (template);

    }


}
