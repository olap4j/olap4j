/*
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

import org.olap4j.mdx.*;
import org.olap4j.metadata.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for generating MDX expressions in query transforms.
 *
 * <p>Intentionally package-protected; this class is not to be used outside
 * this package.
 *
 * @author etdub
 * @since Aug 7, 2008
 */
class MdxHelper {

    public static MemberNode makeMemberNode(Member m) {
        return new MemberNode(null, m);
    }

    private static CallNode _makePropCallNode(
        ParseTreeNode node,
        String funcName)
    {
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
