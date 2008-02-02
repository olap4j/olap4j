/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.*;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.CellSetAxis}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id: $
 * @since Dec 5, 2007
 */
class XmlaOlap4jCellSetAxis implements CellSetAxis {
    private final XmlaOlap4jCellSet olap4jCellSet;
    private final Axis axis;
    final List<Position> positions = new ArrayList<Position>();
    private final List<Position> immutablePositions =
        Collections.unmodifiableList(positions);

    public XmlaOlap4jCellSetAxis(
        XmlaOlap4jCellSet olap4jCellSet,
        Axis axis)
    {
        this.olap4jCellSet = olap4jCellSet;
        this.axis = axis;
    }

    public Axis getAxisOrdinal() {
        return axis;
    }

    public CellSet getCellSet() {
        return olap4jCellSet;
    }

    public CellSetAxisMetaData getAxisMetaData() {
        final CellSetMetaData cellSetMetaData = olap4jCellSet.getMetaData();
        switch (axis) {
        case FILTER:
            return cellSetMetaData.getFilterAxisMetaData();
        default:
            return cellSetMetaData.getAxesMetaData().get(
                axis.axisOrdinal());
        }
    }

    public List<Position> getPositions() {
        return immutablePositions;
    }

    public int getPositionCount() {
        return positions.size();
    }

    public ListIterator<Position> iterator() {
        return immutablePositions.listIterator();
    }
}

// End XmlaOlap4jCellSetAxis.java
