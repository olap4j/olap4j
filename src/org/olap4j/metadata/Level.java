/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

/**
 * Group of {@link Member} objects in a {@link Hierarchy},
 * all with the same attributes and at the same depth in the hierarchy.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 23, 2006
 */
public interface Level extends MetadataElement {
    /**
     * Returns the depth of this level.
     *
     * <p>Note #1: In an access-controlled context, the first visible level of
     * a hierarchy may not have a depth of 0.</p>
     *
     * <p>Note #2: In a parent-child hierarchy, the depth of a member (as
     * returned by may not be the same as the depth of its level.
     */
    int getDepth();

    /**
     * Returns the Hierarchy this Level belongs to.
     */
    Hierarchy getHierarchy();

    /**
     * Returns the Dimension this Level belongs to.
     * (Always equivalent to <code>getHierarchy().getDimension()</code>.)
     */
    Dimension getDimension();

    /**
     * Returns the type of this Level.
     */
    Level.Type getLevelType();

    /**
     * Returns a list of definitions for the properties available to members
     * of this Level.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getProperties()
     */
    NamedList<Property> getProperties();

    /**
     * Enumeration of the types of a {@link Level}.
     *
     * @see Level#getLevelType
    */
    public enum Type {

        /**
         * Indicates that the level is not related to time.
         */
        Regular(false),

        /**
         * Indicates that a level refers to years.
         * It must be used in a dimension whose type is
         * {@link mondrian.olap.DimensionType#TimeDimension}.
         */
        TimeYears(true),

        /**
         * Indicates that a level refers to quarters.
         * It must be used in a dimension whose type is
         * {@link mondrian.olap.DimensionType#TimeDimension}.
         */
        TimeQuarters(true),

        /**
         * Indicates that a level refers to months.
         * It must be used in a dimension whose type is
         * {@link mondrian.olap.DimensionType#TimeDimension}.
         */
        TimeMonths(true),

        /**
         * Indicates that a level refers to weeks.
         * It must be used in a dimension whose type is
         * {@link mondrian.olap.DimensionType#TimeDimension}.
         */
        TimeWeeks(true),

        /**
         * Indicates that a level refers to days.
         * It must be used in a dimension whose type is
         * {@link mondrian.olap.DimensionType#TimeDimension}.
         */
        TimeDays(true),

        /**
         * Indicates that a level holds the null member.
         */
        Null(false);

        private final boolean time;

        private Type(boolean time) {
            this.time = time;
        }

        /**
         * Returns whether this is a time-related level
         * ({@link #TimeYears}, {@link #TimeQuarters}, {@link #TimeMonths},
         * {@link #TimeWeeks}, {@link #TimeDays}).
         */
        public boolean isTime() {
            return time;
        }
    }
}

// End Level.java
