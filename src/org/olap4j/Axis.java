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

import java.util.Locale;

/**
 * Enumeration of axis types.
 *
 * <p>The most commonly used values are
 * <code>COLUMNS</code> (the first axis of a 2-dimensional query),
 * <code>ROWS</code> (the second axis of a 2-dimensional query) and
 * <code>FILTER</code> (also known as the slicer axis, denoted by a
 * <code>WHERE</code> clause in an MDX statement).
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 23, 2006
 */
public enum Axis {
    UNUSED,
    FILTER,
    COLUMNS,
    ROWS,
    PAGES,
    CHAPTERS,
    SECTIONS;

    /**
     * Returns the ordinal which is to be used for retrieving this axis from
     * the {@link org.olap4j.CellSet#getAxes()}, or retrieving its
     * coordinate from {@link Cell#getCoordinateList()}.
     *
     * <p>The axis ordinal is two less than the {@link #ordinal} value which
     * every <code>enum</code> value possesses. Hence, {@link #UNUSED} is -2
     * and {@link #FILTER} is -1 (because they are not treated the same as the
     * other axes), {@link #COLUMNS} is 0, {@link #ROWS} is 1, and so forth.
     *
     * @return Axis ordinal
     */
    public int axisOrdinal() {
        return axisOrdinal;
    }

    /**
     * Returns localized name for this Axis.
     *
     * @param locale Locale for which to give the name
     * @return localized name for this Axis
     */
    public String getCaption(Locale locale) {
        // todo: localize
        return name();
    }

    /**
     * Returns the axis with a given {@link #axisOrdinal()}.
     *
     * @param axisOrdinal Axis ordinal
     * @return Axis whose {@link #axisOrdinal()} is as given
     */
    public static Axis forOrdinal(int axisOrdinal) {
        Axis axis = values()[axisOrdinal + 2];
        assert axis.axisOrdinal() == axisOrdinal;
        return axis;
    }

    private final int axisOrdinal = ordinal() - 2;

    /**
     * The largest legal value for {@link #forOrdinal(int)}.
     */
    public static final int MAX_ORDINAL = SECTIONS.axisOrdinal();
}

// End Axis.java
