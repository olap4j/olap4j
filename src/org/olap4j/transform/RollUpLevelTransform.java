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

import org.olap4j.Axis;
import org.olap4j.CellSet;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.Member;

/**
 * Roll-up level transformation
 *
 * <p>Description: Replaces a member at a specific position on an axis by all
 * the members of its parent's level. The member to roll-up is identified from
 * a CellSet with the axis, positionOrdinalInAxis and memberOrdinalInPosition
 * arguments.
 *
 * <p>Example of use: the user clicks on a member in a crosstab axis, in order
 * to roll up to the members of the upper level.
 *
 * <p>Applicability: this transform is applicable only to members in a query
 * that are have a parent. (Note: how would this work in parent-child
 * hierarchies?)
 *
 * @author etdub
 * @version $Id$
 * @since Aug 4, 2008
 */
public class RollUpLevelTransform extends AxisTransform {

    // private final int positionOrdinalInAxis;
    // private final int memberOrdinalInPosition;
    // private final CellSet cellSet;

    // private final Position positionToDrill;
    private final Member memberToDrill;
    // private final List<Member> pathToMember;

    /**
     * ctor
     *
     * @param axis
     * @param positionOrdinalInAxis
     * @param memberOrdinalInPosition
     * @param cellSet
     */
    public RollUpLevelTransform(
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
        memberToDrill = TransformUtil.getMemberFromCellSet(axis,
                positionOrdinalInAxis, memberOrdinalInPosition, cellSet);
        // pathToMember = getPathToMember(positionToDrill,
        //        memberOrdinalInPosition);
    }

    public String getName() {
        return "Roll member up a level";
    }

    public String getDescription() {
        return "Replaces the member expression on the axis by all members " +
            "on its parent level";
    }

    @Override
    protected ParseTreeNode processAxisExp(ParseTreeNode exp) {
        // FIXME: for now only 1 dimension on an axis is supported,
        // (naive implementation only used for proof of concept)
        return MdxHelper.makeSetCallNode(
            MdxHelper.makeMembersCallNode(
                MdxHelper.makeLevelCallNode(
                    MdxHelper.makeParentCallNode(
                        MdxHelper.makeMemberNode(memberToDrill)))));
    }

}

// End RollUpLevelTransform.java
