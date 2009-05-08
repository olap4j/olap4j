/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2008-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.transform;

import java.util.ArrayList;
import java.util.List;

import org.olap4j.mdx.CallNode;
import org.olap4j.mdx.MemberNode;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.Syntax;
import org.olap4j.metadata.Member;

/**
 * Helper class for generating MDX expressions in query transforms.
 *
 * <p>Intentionally package-protected; this class is not to be used outside
 * this package.
 *
 * @author etdub
 * @version $Id$
 * @since Aug 7, 2008
 */
class MdxHelper {

    public static MemberNode makeMemberNode(Member m) {
        return new MemberNode(null, m);
    }

    private static CallNode _makePropCallNode(ParseTreeNode node,
            String funcName) {
        List<ParseTreeNode> callArgs = new ArrayList<ParseTreeNode>();
        callArgs.add(node);
        return new CallNode(null, funcName, Syntax.Property, callArgs);
    }

    public static CallNode makeChildrenCallNode(ParseTreeNode node) {
        return _makePropCallNode(node, "Children");
    }

    public static CallNode makeParentCallNode(ParseTreeNode node) {
        return _makePropCallNode(node, "Parent");
    }

    public static CallNode makeMembersCallNode(ParseTreeNode node) {
        return _makePropCallNode(node, "Members");
    }

    public static CallNode makeLevelCallNode(ParseTreeNode node) {
        return _makePropCallNode(node, "Level");
    }

    public static CallNode makeSetCallNode(List<ParseTreeNode> nodes) {
        return new CallNode(null, "{}", Syntax.Braces, nodes);
    }

    public static CallNode makeSetCallNode(ParseTreeNode... nodes) {
        List<ParseTreeNode> nodesList = new ArrayList<ParseTreeNode>();
        for (ParseTreeNode n : nodes) {
            nodesList.add(n);
        }
        return makeSetCallNode(nodesList);
    }

    public static CallNode makeHierarchizeCallNode(ParseTreeNode node) {
        List<ParseTreeNode> callArgs = new ArrayList<ParseTreeNode>();
        callArgs.add(node);
        return new CallNode(null, "Hierarchize", Syntax.Function, callArgs);
    }

}

// End MdxHelper.java
