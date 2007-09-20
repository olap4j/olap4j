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

import org.olap4j.OlapException;

/**
 * An organized hierarchy of categories, known as levels, that describes data
 * in a cube.
 *
 * <p>A Dimension typically describes a similar set of members upon which the
 * user wants to base an analysis.
 *
 * <p>A Dimension must have at least one Hierarchy, and may have more than one,
 * but most have exactly one Hierarchy.</p>
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface Dimension extends MetadataElement {

    /**
     * Returns the hierarchies in this Dimension.
     *
     * <p>Many dimensions have only one Hierarchy, whose name is the same as the
     * Dimension.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getHierarchies
     *
     * @return hierarchies in this dimension
     */
    NamedList<Hierarchy> getHierarchies();

    /**
     * Returns the root member or members of this Dimension.
     *
     * <p>If the dimension has an 'all' member, then this will be the sole
     * root member.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @return root members of this hierarchy
     *
     * @throws OlapException if database error occurs
     */
    NamedList<Member> getRootMembers() throws OlapException;

    /**
     * Returns the type of this Dimension.
     *
     * @return dimension type
     *
     * @throws OlapException if database error occurs
     */
    Dimension.Type getDimensionType() throws OlapException;

    /**
     * Returns the default <code>Hierarchy</code> of this Dimension.
     *
     * @return default hierarchy
     */
    Hierarchy getDefaultHierarchy();

    /**
     * Enumeration of the types of a <code>Dimension</code>.
     *
     * @see Level.Type
     * @see Member.Type
     * @see Dimension#getDimensionType
     */
    public enum Type {
        /**
         * Indicates that the dimension is not related to time.
         */
        Standard,

        /**
         * Indicates that a dimension is a time dimension.
         */
        Time,

        /**
         * Indicates that a dimension is the Measures dimension.
         */
        Measures,
    }
}

// End Dimension.java
