/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.*;

import java.util.List;
import java.util.ListIterator;

/**
 * Implementation of {@link org.olap4j.CellSetAxis}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 5, 2007
 */
class XmlaOlap4jCellSetAxis implements CellSetAxis {
    private final XmlaOlap4jCellSet olap4jCellSet;
    private final Axis axis;
    final List<Position> positions;

    /**
     * Creates an XmlaOlap4jCellSetAxis.
     *
     * @param olap4jCellSet Cell set
     * @param axis Axis identifier
     * @param positions List of positions. Caller must ensure it is immutable
     */
    public XmlaOlap4jCellSetAxis(
        XmlaOlap4jCellSet olap4jCellSet,
        Axis axis,
        List<Position> positions)
    {
        this.olap4jCellSet = olap4jCellSet;
        this.axis = axis;
        this.positions = positions;
    }

    public Axis getAxisOrdinal() {
        return axis;
    }

    public CellSet getCellSet() {
        return olap4jCellSet;
    }

    public CellSetAxisMetaData getAxisMetaData() {
        final CellSetMetaData cellSetMetaData = olap4jCellSet.getMetaData();
        if (axis.isFilter()) {
            return cellSetMetaData.getFilterAxisMetaData();
        } else {
            return cellSetMetaData.getAxesMetaData().get(
                axis.axisOrdinal());
        }
    }

    public List<Position> getPositions() {
        return positions;
    }

    public int getPositionCount() {
        return positions.size();
    }

    public ListIterator<Position> iterator() {
        return positions.listIterator();
    }
}

// End XmlaOlap4jCellSetAxis.java
