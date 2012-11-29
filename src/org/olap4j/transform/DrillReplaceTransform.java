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

import org.olap4j.Axis;
import org.olap4j.CellSet;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.Member;

/**
 * Drill replace transformation
 *
 * <p>Description: Replaces a member at a specific position on an axis by its
 * children. The member to drill is identified from a CellSet with the axis,
 * positionOrdinalInAxis and memberOrdinalInPosition arguments.
 *
 * <p>Example of use: the user clicks on a member in a crosstab axis, in order
 * to see its children.
 *
 * <p>Applicability: this transform is applicable only to members in a query
 * that are drillable, i.e. non-leaf members. The CellSet resulting from the
 * execution of the initial MDX query must also be available.
 *
 * @author etdub
 * @since Jul 30, 2008
 */
public class DrillReplaceTransform extends AxisTransform {

    // private final int positionOrdinalInAxis;
    // private final int memberOrdinalInPosition;
    // private final CellSet cellSet;

    // private final Position positionToDrill;
    private final Member memberToDrill;
    // private final List<Member> pathToMember;

    /**
     * ctor
     *
     * @param axis axis (of the resulting CellSet) the member to be drilled
     * @param positionOrdinalInAxis position ordinal in axis of the member to
     *                              be drilled
     * @param memberOrdinalInPosition ordinal in position of the member to be
     *                                drilled
     * @param cellSet the CellSet resulting from execution of the query to be
     *                transformed
     */
    public DrillReplaceTransform(
        Axis axis,
        int positionOrdinalInAxis,
        int memberOrdinalInPosition,
        CellSet cellSet)
    {
        super(axis);

        // this.positionOrdinalInAxis = positionOrdinalInAxis;
        // this.memberOrdinalInPosition = memberOrdinalInPosition;
        // this.cellSet = cellSet;

        // Position positionToDrill =
        //     TransformUtil.getPositionFromCellSet(axis, positionOrdinalInAxis,
        //          cellSet);
        memberToDrill = TransformUtil.getMemberFromCellSet(
            axis, positionOrdinalInAxis, memberOrdinalInPosition, cellSet);
        // pathToMember = getPathToMember(positionToDrill,
        //        memberOrdinalInPosition);
    }

    public String getName() {
        return "Drill Replace On Member";
    }

    public String getDescription() {
        return "Drills and replace (by its children) a member on an axis";
    }

    @Override
    protected ParseTreeNode processAxisExp(ParseTreeNode exp) {
        // FIXME: for now only 1 dimension on an axis is supported,
        // (naive implementation only used for proof of concept)
        return MdxHelper.makeSetCallNode(
            MdxHelper.makeChildrenCallNode(
                MdxHelper.makeMemberNode(memberToDrill)));
    }

}

// End DrillReplaceTransform.java


