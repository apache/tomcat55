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


import java.io.Serializable;
import java.util.HashMap;


/**
 * <p>The overall data structure representing a <em>tree control</em>
 * that can be rendered by the <code>TreeControlTag</code> custom tag.
 * Each node of the tree is represented by an instance of
 * <code>TreeControlNode</code>.</p>
 *
 * @author Jazmin Jonson
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class TreeControl implements Serializable {


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance with no predefined root node.
     */
    public TreeControl() {

        super();
        setRoot(null);

    }


    /**
     * Construct a new instance with the specified root node.
     *
     * @param root The new root node
     */
    public TreeControl(TreeControlNode root) {

        super();
        setRoot(root);

    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The collection of nodes that represent this tree, keyed by name.
     */
    protected HashMap registry = new HashMap();


    /**
     * The most recently selected node.
     */
    protected TreeControlNode selected = null;


    // ------------------------------------------------------------- Properties


    /**
     * The root node of the entire tree.
     */
    protected TreeControlNode root = null;

    public TreeControlNode getRoot() {
        return (this.root);
    }

    protected void setRoot(TreeControlNode root) {
        if (this.root != null)
            removeNode(this.root);
        if (root != null)
            addNode(root);
        root.setLast(true);
        this.root = root;
    }


    /**
     * The current displayable "width" of this tree (that is, the maximum
     * depth of the visible part of the tree).
     */
    public int getWidth() {

        if (root == null)
            return (0);
        else
            return (getWidth(root));

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Find and return the <code>TreeControlNode</code> for the specified
     * node name, if it exists; otherwise, return <code>null</code>.
     *
     * @param name Name of the <code>TreeControlNode</code> to be returned
     */
    public TreeControlNode findNode(String name) {

        synchronized (registry) {
            return ((TreeControlNode) registry.get(name));
        }

    }


    /**
     * Mark the specified node as the one-and-only currently selected one,
     * deselecting any previous node that was so marked.
     *
     * @param node Name of the node to mark as selected, or <code>null</code>
     *  if there should be no currently selected node
     */
    public void selectNode(String name) {

        if (selected != null) {
            selected.setSelected(false);
            selected = null;
        }
        selected = findNode(name);
        if (selected != null)
            selected.setSelected(true);

    }


    // -------------------------------------------------------- Package Methods


    /**
     * Register the specified node in our registry of the complete tree.
     *
     * @param node The <code>TreeControlNode</code> to be registered
     *
     * @exception IllegalArgumentException if the name of this node
     *  is not unique
     */
    void addNode(TreeControlNode node) throws IllegalArgumentException {

        synchronized (registry) {
            String name = node.getName();
            if (registry.containsKey(name))
                throw new IllegalArgumentException("Name '" + name +
                                                   "' is not unique");
            node.setTree(this);
            registry.put(name, node);
        }

    }


    /**
     * Calculate the width of the subtree below the specified node.
     *
     * @param node The node for which to calculate the width
     */
    int getWidth(TreeControlNode node) {

        int width = node.getWidth();
        if (!node.isExpanded())
            return (width);
        TreeControlNode children[] = node.findChildren();
        for (int i = 0; i < children.length; i++) {
            int current = getWidth(children[i]);
            if (current > width)
                width = current;
        }
        return (width);

    }


    /**
     * Deregister the specified node, as well as all child nodes of this
     * node, from our registry of the complete tree.  If this node is not
     * present, no action is taken.
     *
     * @param node The <code>TreeControlNode</code> to be deregistered
     */
    void removeNode(TreeControlNode node) {

        synchronized (registry) {
            TreeControlNode children[] = node.findChildren();
            for (int i = 0; i < children.length; i++)
                removeNode(children[i]);
            TreeControlNode parent = node.getParent();
            if (parent != null) {
                parent.removeChild(node);
            }
            node.setParent(null);
            node.setTree(null);
            if (node == this.root) {
                this.root = null;
            }
            registry.remove(node.getName());
        }

    }


}
