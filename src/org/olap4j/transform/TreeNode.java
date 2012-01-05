/*
// $Id$
//
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.transform;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic Tree Node.
 * Adapted from JPivot (class com.tonbeller.jpivot.util.TreeNode)
 *
 * <p>REVIEW: Should this class be in the public olap4j API? (jhyde, 2008/8/14)
 *
 * @author etdub
 * @version $Id$
 * @since Aug 7, 2008
 */
class TreeNode<T> {

    private TreeNode<T> parent = null;
    private final List<TreeNode<T>> children;
    private T reference;

    /**
     * Constructor.
     *
     * @param data the reference to hold in the node
     */
    public TreeNode(T data) {
        this.reference = data;
        this.children = new ArrayList<TreeNode<T>>();
    }

    /**
     * Removes this node from the tree
     */
    public void remove() {
        if (parent != null) {
            parent.removeChild(this);
        }
    }

    /**
     * Removes child node from the tree, if it exists
     * @param child node to remove
     */
    public void removeChild(TreeNode<T> child) {
        if (children.contains(child)) {
            children.remove(child);
        }
    }

    /**
     * Adds a child node to the tree
     * @param child node to be added
     */
    public void addChildNode(TreeNode<T> child) {
        child.parent = this;
        if (!children.contains(child)) {
            children.add(child);
        }
    }

    /**
     * Performs a deep copy (clone) of this node
     * The contained reference is not cloned but passed to the
     * new node.
     * @return the cloned TreeNode
     */
    public TreeNode<T> deepCopy() {
        TreeNode<T> newNode = new TreeNode<T>(reference);
        for (TreeNode<T> child : children) {
            newNode.addChildNode(child.deepCopy());
        }
        return newNode;
    }

    /**
     * Performs a deep copy (clone) of this node, pruning
     * all nodes below level specified by depth
     * @param depth number of child levels to be copied
     * @return the cloned TreeNode
     */
    public TreeNode<T> deepCopyPrune(int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Depth is negative");
        }
        TreeNode<T> newNode = new TreeNode<T>(reference);
        if (depth == 0) {
            return newNode;
        }
        for (TreeNode<T> child : children) {
            newNode.addChildNode(child.deepCopyPrune(depth - 1));
        }
        return newNode;
    }

    /**
     * Returns the level of this node, i.e. distance from
     * the root node
     * @return level distance from root node
     */
    public int getLevel() {
        int level = 0;
        TreeNode<T> p = parent;
        while (p != null) {
            ++level;
            p = p.parent;
        }
        return level;
    }

    /**
     * Gets a list of children nodes.
     *
     * <p>The list is mutable but shouldn't be modified by callers
     * (use the add and remove methods instead).
     * @return the list of children
     */
    public List<TreeNode<T>> getChildren() {
        return children;
    }

    /**
     * Gets the parent node.
     * @return parent node
     */
    public TreeNode<T> getParent() {
        return parent;
    }

    /**
     * Get the contained reference object
     * @return the reference object
     */
    public T getReference() {
        return reference;
    }

    /**
     * Set the contained reference object
     * @param ref the new reference object
     */
    public void setReference(T ref) {
        this.reference = ref;
    }

    /**
     * Walk through subtree of this node
     * @param callbackHandler callback function called on iteration
     * @return code used for navigation in the tree (@see TreeNodeCallback)
     */
    public int walkTree(TreeNodeCallback<T> callbackHandler) {
        int code = callbackHandler.handleTreeNode(this);
        if (code != TreeNodeCallback.CONTINUE) {
            return code;
        }
        for (TreeNode<T> child : children) {
            code = child.walkTree(callbackHandler);
            if (code >= TreeNodeCallback.CONTINUE_PARENT) {
                return code;
            }
        }
        return code;
    }

    /**
     * Walk through children subtrees of this node
     * @param callbackHandler callback function called on iteration
     * @return code used for navigation in the tree (@see TreeNodeCallback)
     */
    public int walkChildren(TreeNodeCallback<T> callbackHandler) {
        int code = 0;
        for (TreeNode<T> child : children) {
            code = callbackHandler.handleTreeNode(child);
            if (code >= TreeNodeCallback.CONTINUE_PARENT) {
                return code;
            }
            if (code == TreeNodeCallback.CONTINUE) {
                code = child.walkChildren(callbackHandler);
                if (code > TreeNodeCallback.CONTINUE_PARENT) {
                    return code;
                }
            }
        }
        return code;
    }

}

// End TreeNode.java
