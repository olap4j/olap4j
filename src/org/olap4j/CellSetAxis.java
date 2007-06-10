/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import java.util.List;
import java.util.ListIterator;

/**
 * <code>CellSetAxis</code> ...
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface CellSetAxis {
    /**
     * Returns the ordinal of this <code>CellSetAxis</code>.
     *
     * <p>0 = ROWS, 1 = COLUMNS, and so forth, as described by the
     * {@link Axis#axisOrdinal()} method of the {@link Axis} enumeration.</p>
     */
    int getOrdinal();

    /**
     * Returns the {@link CellSet} which this <code>CellSetAxis</code>
     * belongs to.
     */
    CellSet getCellSet();

    /**
     * Returns a description of the type (e.g. {@link Axis#ROWS}) of this
     * axis, and the hierarchies and properties which will be found on it.
     *
     * <p>The result is identical to evaluating
     * <blockquote>
     * <code>
     * getCellSet().getMetaData().getAxesMetaData(getOrdinal())
     * </code>
     * </blockquote>
     */
    CellSetAxisMetaData getAxisMetaData();

    /**
     * Returns a list of <code>Position</code> objects on this CellSetAxis.
     *
     * @return List of positions on this axis (never null)
     */
    List<Position> getPositions();

    /**
     * Returns the number of positions on this CellSetAxis.
     *
     * <p>This method can be called at any time. In particular, it is not
     * necessary to complete an iteration through all positions before calling
     * this method.</p>
     *
     * <p>The number of positions on an axis is important when computing the
     * ordinal of a cell.</p>
     */
    int getPositionCount();

    /**
     * Opens an iterator on
     */
    ListIterator<Position> iterate();

}

// End CellSetAxis.java
