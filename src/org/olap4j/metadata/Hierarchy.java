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
 * <code>Hierarchy</code> ...
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 23, 2006
 */
public interface Hierarchy extends MetadataElement {
    /**
     * Returns the {@link Dimension} this <code>Hierarchy</code> belongs to.
     */
    Dimension getDimension();

    /**
     * Returns a list of the {@link Level} objects in this
     * <code>Hierarchy</code>.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getLevels
     */
    NamedList<Level> getLevels();

    /**
     * Returns whether this <code>Hierarchy</code> has an 'all' member.
     */
    boolean hasAll();

    Member getDefaultMember();
}

// End Hierarchy.java
