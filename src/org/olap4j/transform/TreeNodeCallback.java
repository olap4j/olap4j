/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2008-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.transform;

/**
 * Handle callback for navigating in a tree of TreeNode<T>
 *
 * Adapted from JPivot (interface com.tonbeller.jpivot.util.TreeNodeCallback)
 *
 * <p>REVIEW: Should this class be in the public olap4j API? (jhyde, 2008/8/14)
 *
 * @author etdub
 * @version $Id$
 * @since Aug 7, 2008
 */
interface TreeNodeCallback<T> {

   public static final int CONTINUE = 0;
   public static final int CONTINUE_SIBLING = 1;
   public static final int CONTINUE_PARENT = 2;
   public static final int BREAK = 3;

   /**
    * Callback function.
    *
    * @param node the current node to handle
    * @return CONTINUE (0) : continue tree walk
    *         CONTINUE_SIBLING (1) : continue to the sibling
    *         CONTINUE_PARENT (2) : continue at parent level
    *         BREAK (3) : break tree walk
    */
   int handleTreeNode(TreeNode<T> node);

}

// End TreeNodeCallback.java
