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

import java.util.ArrayList;
import java.util.List;

import org.olap4j.Axis;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

/**
 * Various helper functions for MDX query transforms.
 *
 * <p>This class is intentionally package-protected. It is NOT part of the
 * public olap4j API.
 *
 * @author etdub
 * @version $Id$
 * @since Aug 7, 2008
 */
class TransformUtil {

    public static CellSetAxis getCellSetAxisFromCellSet(
        Axis axis,
        CellSet cellSet)
    {
        for (CellSetAxis a : cellSet.getAxes()) {
            if (a.getAxisOrdinal() == axis) {
                return a;
            }
        }

        // axis not found
        throw new IndexOutOfBoundsException();
    }

    public static Position getPositionFromCellSet(
        Axis axis,
        int positionOrdinalInAxis,
        CellSet cellSet)
    {
        CellSetAxis a = getCellSetAxisFromCellSet(axis, cellSet);

        return a.getPositions().get(positionOrdinalInAxis);
    }

    public static Member getMemberFromCellSet(
        Axis axis,
        int positionOrdinalInAxis,
        int memberOrdinalInPosition,
        CellSet cellSet)
    {
        Position p =
            getPositionFromCellSet(
                axis, positionOrdinalInAxis, cellSet);
        return p.getMembers().get(memberOrdinalInPosition);
    }

    public static List<Member> getPathToMember(
        Position p,
        int memberOrdinalInPosition)
    {
        List<Member> pathToMember = new ArrayList<Member>();
        for (int i = 0 ; i < memberOrdinalInPosition ; i++) {
            pathToMember.add(p.getMembers().get(i));
        }

        return pathToMember;
    }
}

// End TransformUtil.java
