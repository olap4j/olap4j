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

import org.olap4j.*;
import org.olap4j.mdx.*;
import org.olap4j.metadata.Member;

import java.util.List;

/**
 * Drill down on position transform
 *
 * TODO: transform to be completed, not working for now.
 *
 * <p>Description: Adds the children of a member at a specific position on an
 * axis. The member to drill is identified from a CellSet with the axis,
 * positionOrdinalInAxis and memberOrdinalInPosition arguments. The drilled
 * member will still be present on the axis, in addition to its children. It
 * is recommended to apply a Hierarchize transform to the same axis of the
 * resulting query, in order to have members in correct hierarchical order.
 *
 * <p>Example of use: the user clicks on a member in a crosstab axis, in order
 * to see its children in addition to the member itself.
 *
 * <p>Applicability: this transform is applicable only to members in a query
 * that are drillable, i.e. non-leaf members. The CellSet resulting from the
 * execution of the initial MDX query must also be available.
 *
 * @author etdub
 * @author jhyde
 * @version $Id$
 * @since Jul 30, 2008
 */
public class DrillDownOnPositionTransform extends AxisTransform {

    // private final int positionOrdinalInAxis;
    // private final int memberOrdinalInPosition;
    // private final CellSet cellSet;

    private final Position positionToDrill;
    private final Member memberToDrill;
    private final List<Member> pathToMember;

    /**
     * ctor
     *
     * @param axis
     * @param positionOrdinalInAxis
     * @param memberOrdinalInPosition
     * @param cellSet
     */
    public DrillDownOnPositionTransform(
        Axis axis,
        int positionOrdinalInAxis,
        int memberOrdinalInPosition,
        CellSet cellSet)
    {
        super(axis);
        // this.positionOrdinalInAxis = positionOrdinalInAxis;
        // this.memberOrdinalInPosition = memberOrdinalInPosition;
        // this.cellSet = cellSet;

        positionToDrill =
            TransformUtil.getPositionFromCellSet(
                axis, positionOrdinalInAxis, cellSet);
        memberToDrill = TransformUtil.getMemberFromCellSet(
            axis, positionOrdinalInAxis, memberOrdinalInPosition, cellSet);
        pathToMember = TransformUtil.getPathToMember(
            positionToDrill,
            memberOrdinalInPosition);
    }

    public String getName() {
        return "Drill down a member on a specific position";
    }

    public String getDescription() {
        return "Expand a member on a position by adding its children";
    }

    @Override
    protected ParseTreeNode processAxisExp(ParseTreeNode exp) {
        // TODO: implement me!

        return null;
    }


    // visitor for a tree of expressions inside a query axis
    // (not sure this should go here)
    class DrillDownOnPositionVisitor
        implements ParseTreeVisitor<ParseTreeNode>
    {

        public ParseTreeNode visit(SelectNode selectNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(AxisNode axis) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(WithMemberNode calcMemberNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(WithSetNode calcSetNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(CallNode call) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(IdentifierNode id) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(ParameterNode parameterNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(CubeNode cubeNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(DimensionNode dimensionNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(HierarchyNode hierarchyNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(LevelNode levelNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(MemberNode memberNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(LiteralNode literalNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(PropertyValueNode propertyValueNode) {
            // TODO Auto-generated method stub
            return null;
        }

        public ParseTreeNode visit(DrillThroughNode drillThroughNode) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}

// End DrillDownOnPositionTransform.java
