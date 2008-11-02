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

/**
 * Standard transformations library
 *
 * NOTE: is this really needed since transforms' ctors have the same
 * parameters as these functions? This serves only as a place to conveniently
 * regroup transforms in a "library".
 *
 * @author etdub
 * @author jhyde
 * @version $Id$
 * @since Jul 28, 2008
 */
public class StandardTransformLibrary {

    public static MdxQueryTransform createDrillReplaceTransform(
        Axis axis,
        int positionOrdinalInAxis,
        int memberOrdinalInPosition,
        CellSet cellSet)
    {
        return new DrillReplaceTransform(
            axis,
            positionOrdinalInAxis,
            memberOrdinalInPosition,
            cellSet);
    }

    public static MdxQueryTransform createDrillDownOnPositionTransform(
        Axis axis,
        int positionOrdinalInAxis,
        int memberOrdinalInPosition,
        CellSet cellSet)
    {
        return new DrillDownOnPositionTransform(
            axis,
            positionOrdinalInAxis,
            memberOrdinalInPosition,
            cellSet);
    }

    public static MdxQueryTransform createRollUpLevelTransform(
        Axis axis,
        int positionOrdinalInAxis,
        int memberOrdinalInPosition,
        CellSet cellSet)
    {
        return new RollUpLevelTransform(
            axis,
            positionOrdinalInAxis,
            memberOrdinalInPosition,
            cellSet);
    }

    // many other transforms ...
}

// End StandardTransformLibrary.java
