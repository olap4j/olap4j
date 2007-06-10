/*
// $Id: Level.java 18 2007-06-10 18:31:17Z jhyde $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import java.util.List;

/**
 * Group of {@link Member} objects in a {@link Hierarchy},
 * all with the same attributes and at the same depth in the hierarchy.
 *
 * @author jhyde
 * @version $Id: Level.java 18 2007-06-10 18:31:17Z jhyde $
 * @since Aug 23, 2006
 */
public interface Level extends MetadataElement {
    /**
     * Returns the depth of this <code>Level</code>.
     *
     * <p>Note #1: In an access-controlled context, the first visible level of
     * a hierarchy may not have a depth of 0.</p>
     *
     * <p>Note #2: In a parent-child hierarchy, the depth of a member (as
     * returned by may not be the same as the depth of its level.
     */
    int getDepth();

    /**
     * Returns the {@link Hierarchy} this <code>Level</code> belongs to.
     */
    Hierarchy getHierarchy();

    /**
     * Returns the Dimension this <code>Level</code> belongs to.
     * (Always equivalent to <code>getHierarchy().getDimension()</code>.)
     */
    Dimension getDimension();

    /**
     * Returns the type of this <code>Level</code>.
     */
    Level.Type getLevelType();

    /**
     * Returns a list of definitions for the properties available to members
     * of this <code>Level</code>.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getProperties()
     */
    NamedList<Property> getProperties();

    // todo: needs specification
    Member findMember(String memberName);

    // todo: better query interface? (level can have a lot of members)
    // todo: return NamedList?
    List<Member> getMembers();

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
         * {@link org.olap4j.metadata.Dimension.Type#Time}.
         */
        TimeYears(true),

        /**
         * Indicates that a level refers to quarters.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#Time}.
         */
        TimeQuarters(true),

        /**
         * Indicates that a level refers to months.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#Time}.
         */
        TimeMonths(true),

        /**
         * Indicates that a level refers to weeks.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#Time}.
         */
        TimeWeeks(true),

        /**
         * Indicates that a level refers to days.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#Time}.
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
