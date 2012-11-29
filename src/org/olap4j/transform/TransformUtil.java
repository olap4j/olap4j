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

import org.olap4j.*;
import org.olap4j.metadata.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Various helper functions for MDX query transforms.
 *
 * <p>This class is intentionally package-protected. It is NOT part of the
 * public olap4j API.
 *
 * @author etdub
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
