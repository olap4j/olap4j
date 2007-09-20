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
 * An organization of the set of {@link Member}s in a {@link Dimension} and
 * their positions relative to one another.
 *
 * <p>A Hierarchy is a collection of {@link Level}s, each of which is a
 * category of similar {@link Member}s.</p>
 *
 * <p>A Dimension must have at least one Hierarchy, and may have more than one,
 * but most have exactly one Hierarchy.</p>
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 23, 2006
 */
public interface Hierarchy extends MetadataElement {
    /**
     * Returns the {@link Dimension} this <code>Hierarchy</code> belongs to.
     *
     * @return dimension this hierarchy belongs to
     */
    Dimension getDimension();

    /**
     * Returns a list of the {@link Level} objects in this
     * <code>Hierarchy</code>.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getLevels
     *
     * @return list of levels
     */
    NamedList<Level> getLevels();

    /**
     * Returns whether this <code>Hierarchy</code> has an 'all' member.
     *
     * @return whether this hierarchy has an 'all' member
     */
    boolean hasAll();

    /**
     * Returns the default {@link Member} of this <code>Hierarchy</code>.
     *
     * <p>If the hierarchy has an 'all' member, this member is often the
     * default.
     *
     * @return the default member of this hierarchy
     */
    Member getDefaultMember();
}

// End Hierarchy.java
