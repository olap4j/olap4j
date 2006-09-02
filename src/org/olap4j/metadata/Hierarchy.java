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
public interface Hierarchy {
    /**
     * Returns the {@link Dimension} this Hierarchy belongs to.
     */
    Dimension getDimension();

    /**
     * Returns the name of this Hierarchy.
     */
    String getName();

    /**
     * Returns a list of the {@link Level}s in this Hierarchy.
     */
    NamedList<Level> getLevels();

    /**
     * Returns whether this Hierarchy has an 'all' member.
     */
    boolean hasAll();
}

// End Hierarchy.java
