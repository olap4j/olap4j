/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

/**
 * Enumeration of axis types.
 *
 * <p>Typically used values are ROWS, COLUMNS, and SLICER.
 *
 * @author jhyde
 * @version $Id: $
 * @since Oct 23, 2006
 */
public enum Axis {
    SLICER,
    COLUMNS,
    ROWS,
    PAGES,
    CHAPTERS,
    SECTIONS;

    /**
     * Returns the ordinal which is to be used for retrieving this axis from
     * the {@link org.olap4j.OlapResultSet#getAxes()}, or retrieving its
     * coordinate from {@link ResultCell#getCoordinateList()}.
     *
     * <p>The axis ordinal is one less than the {@link #ordinal} value which
     * every <code>enum</code> value possesses. Hence, {@link #SLICER} is -1
     * (because it is not treated the same as the other axes), {@link #COLUMNS}
     * is 0, {@link #ROWS} is 1, and so forth.
     *
     * @return Axis ordinal
     */
    public int axisOrdinal() {
        return axisOrdinal;
    }

    private final int axisOrdinal = ordinal() - 1;
}

// End Axis.java
