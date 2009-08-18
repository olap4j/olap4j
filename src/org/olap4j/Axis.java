/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2009 Julian Hyde
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
public interface Axis {

    /**
     * @deprecated Will be removed before olap4j 1.0.
     */
    // REVIEW: Is it wise to remove this axis enum value?
    // It's existence IS relevant.
    Standard UNUSED = null;

    /**
     * @deprecated Will be removed before olap4j 1.0.
     */
    Standard NONE = null;

    /**
     * Abbreviation for {@link org.olap4j.Axis.Standard#FILTER}.
     */
    Standard FILTER = Standard.FILTER;

    /**
     * Abbreviation for {@link org.olap4j.Axis.Standard#COLUMNS}.
     */
    Standard COLUMNS = Standard.COLUMNS;

    /**
     * Abbreviation for {@link org.olap4j.Axis.Standard#ROWS}.
     */
    Standard ROWS = Standard.ROWS;

    /**
     * Abbreviation for {@link org.olap4j.Axis.Standard#PAGES}.
     */
    Standard PAGES = Standard.PAGES;

    /**
     * Abbreviation for {@link org.olap4j.Axis.Standard#CHAPTERS}.
     */
    Standard SECTIONS = Standard.SECTIONS;

    /**
     * Abbreviation for {@link org.olap4j.Axis.Standard#FILTER}.
     */
    Standard CHAPTERS = Standard.CHAPTERS;

    /**
     * Returns the name of this axis, e.g. "COLUMNS", "FILTER", "AXIS(17)".
     *
     * @return Name of the axis
     */
    String name();

    /**
     * Returns whether this is the filter (slicer) axis.
     *
     * @return whether this is the filter axis
     */
    boolean isFilter();


    /**
     * Returns the ordinal which is to be used for retrieving this axis from
     * the {@link org.olap4j.CellSet#getAxes()}, or retrieving its
     * coordinate from {@link Cell#getCoordinateList()}.
     *
     * <p>For example:
     * <ul>
     * <li>-1 {@link org.olap4j.Axis.Standard#FILTER FILTER}</li>
     * <li>0 {@link org.olap4j.Axis.Standard#COLUMNS COLUMNS}</li>
     * <li>1 {@link org.olap4j.Axis.Standard#ROWS ROWS}</li>
     * <li>2 {@link org.olap4j.Axis.Standard#PAGES PAGES}</li>
     * <li>3 {@link org.olap4j.Axis.Standard#CHAPTERS CHAPTERS}</li>
     * <li>4 {@link org.olap4j.Axis.Standard#SECTIONS SECTIONS}</li>
     * <li>5 {@link org.olap4j.Axis.Standard#SECTIONS SECTIONS}</li>
     * <li>6 AXES(6)</li>
     * <li>123 AXES(123)</li>
     * </ul>
     *
     * @return ordinal of this axis
     */
    int axisOrdinal();

    /**
     * Returns localized name for this Axis.
     *
     * <p>Examples: "FILTER", "ROWS", "COLUMNS", "AXIS(10)".
     *
     * @param locale Locale for which to give the name
     * @return localized name for this Axis
     */
    String getCaption(Locale locale);

    /**
     * Enumeration of standard, named axes descriptors.
     */
    public enum Standard implements Axis {
        /**
         * Filter axis, also known as the slicer axis, and represented by the
         * WHERE clause of an MDX query.
         */
        FILTER,

        /** COLUMNS axis, also known as X axis and AXIS(0). */
        COLUMNS,

        /** ROWS axis, also known as Y axis and AXIS(1). */
        ROWS,

        /** PAGES axis, also known as AXIS(2). */
        PAGES,

        /** CHAPTERS axis, also known as AXIS(3). */
        CHAPTERS,

        /** SECTIONS axis, also known as AXIS(4). */
        SECTIONS;

        public int axisOrdinal() {
            return ordinal() - 1;
        }

        public boolean isFilter() {
            return this == FILTER;
        }

        public String getCaption(Locale locale) {
            // TODO: localize
            return name();
        }
    }

    /**
     * Container class for various Axis factory methods.
     */
    class Factory {
        private static final Standard[] STANDARD_VALUES = Standard.values();

        /**
         * Returns the axis with a given ordinal.
         *
         * <p>For example, {@code forOrdinal(0)} returns the COLUMNS axis;
         * {@code forOrdinal(-1)} returns the SLICER axis;
         * {@code forOrdinal(100)} returns AXIS(100).
         *
         * @param ordinal Axis ordinal
         * @return Axis whose ordinal is as given
         */
        public static Axis forOrdinal(final int ordinal) {
            if (ordinal < -1) {
                throw new IllegalArgumentException(
                    "Axis ordinal must be -1 or higher");
            }
            if (ordinal + 1 < STANDARD_VALUES.length) {
                return STANDARD_VALUES[ordinal + 1];
            }
            return new Axis() {
                public String toString() {
                    return name();
                }

                public String name() {
                    return "AXIS(" + ordinal + ")";
                }

                public boolean isFilter() {
                    return false;
                }

                public int axisOrdinal() {
                    return ordinal;
                }

                public String getCaption(Locale locale) {
                    // TODO: localize
                    return name();
                }
            };
        }
    }
}

// End Axis.java
