/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import org.olap4j.OlapException;

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
    Member getDefaultMember() throws OlapException;

    /**
     * Returns the root member or members of this Dimension.
     *
     * <p>If the dimension has an 'all' member, then this will be the sole
     * root member.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * <p>The result is similar to that returned by
     * <code>getLevels().get(0).getMembers()</code>; the contents will be the
     * same, but this method returns a {@link NamedList} rather than a
     * mere {@link java.util.List} because the members of the root level are
     * known to have unique names.
     *
     * @return root members of this hierarchy
     *
     * @throws OlapException on database error
     */
    NamedList<Member> getRootMembers() throws OlapException;
}

// End Hierarchy.java
