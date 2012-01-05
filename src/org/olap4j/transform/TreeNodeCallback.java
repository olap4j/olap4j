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
