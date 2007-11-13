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

import java.util.*;

/**
 * Central metadata object for representation of multidimensional data.
 *
 * <p>A Cube belongs to a {@link Schema}, and is described by a list of
 * {@link Dimension}s and a list of {@link Measure}s. It may also have one or
 * more {@link NamedSet}s.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface Cube extends MetadataElement {
    /**
     * Returns the {@link Schema} this Cube belongs to.
     *
     * @return Schema this Cube belongs to
     */
    Schema getSchema();

    /**
     * Returns a list of {@link Dimension} objects in this Cube.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getDimensions(String,String,String,String)
     *
     * @return list of Dimensions
     */
    NamedList<Dimension> getDimensions();

    /**
     * Returns a list of {@link Measure} objects in this Cube.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getMeasures(String,String,String,String,String)
     *
     * @return list of Measures
     */
    List<Measure> getMeasures();

    /**
     * Returns a list of {@link NamedSet} objects in this Cube.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getSets(String,String,String,String)
     *
     * @return list of NamedSets
     */
    NamedList<NamedSet> getSets();

    /**
     * Returns a collection of {@link java.util.Locale} objects for which this
     * <code>Cube</code> has been localized.
     *
     * <p>Consider the following use case. Suppose one cube is available in
     * English and French, and in French and Spanish, and both are shown in same
     * portal. Clients typically say that seeing reports in a mixture of
     * languages is confusing; the portal would figure out the best common
     * language, in this case French. This method allows the client to choose
     * the most appropriate locale.</p>
     * 
     * <p>The list is advisory: a client is free to choose another locale,
     * in which case, the server will probably revert to the base locale for
     * locale-specific behavior such as captions and formatting.</p>
     *
     * @see Schema#getSupportedLocales
     *
     * @return List of locales for which this <code>Cube</code> has been
     * localized
     */
    Collection<Locale> getSupportedLocales();

    /**
     * Finds a member in the current Cube based upon its fully-qualified name.
     * Returns the member, or null if there is no member with this name.
     *
     * <p>The fully-qualified name starts with the name of the dimension,
     * followed by the name of a root member, and continues with the name of
     * each successive member on the path from the root member. If a member's
     * name is unique within its level, preceding member name can be omitted.
     *
     * <p>For example,
     * <code>lookupMember("Product", "Food")</code>
     * and
     * <code>lookupMember("Product", "All Products", "Food")</code>
     * are both valid ways to locate the "Food" member of the "Product"
     * dimension.
     *
     * @param nameParts Components of the fully-qualified member name
     * @return member with the given name, or null if not found
     */
    Member lookupMember(String... nameParts);

    /**
     * Finds a collection of members in the current Cube related to a given
     * member.
     *
     * <p>The method first looks up a member with the given fully-qualified
     * name as for {@link #lookupMember(String[])}, then applies the set of
     * tree-operations to find related members.
     *
     * <p>The returned collection is sorted in hierarchical order. If no member
     * is found with the given name, the collection is empty.
     *
     * <p>For example,
     *
     * <blockquote>
     * <code>lookupMembers(
     *     EnumSet.of(TreeOp.ANCESTORS, TreeOp.CHILDREN),
     *     "Time", "1997", "Q2")</code>
     * </blockquote>
     *
     * returns
     *
     * <blockquote>
     * [Time].[1997], [Time].[1997].[Q2].[4],
     * [Time].[1997].[Q2].[5], [Time].[1997].[Q2].[6]
     * </blockquote>
     *
     * <p>The fully-qualified name starts with the name of the dimension,
     * followed by the name of a root member, and continues with the name of
     * each successive member on the path from the root member. If a member's
     * name is unique within its level, preceding member name can be omitted.
     *
     * <p>For example,
     * <code>lookupMember("Product", "Food")</code>
     * and
     * <code>lookupMember("Product", "All Products", "Food")</code>
     * are both valid ways to locate the "Food" member of the "Product"
     * dimension.
     *
     * @param nameParts Components of the fully-qualified member name
     * @return member with the given name, or null if not found
     */
    List<Member> lookupMembers(
        Set<Member.TreeOp> treeOps,
        String... nameParts);
}

// End Cube.java
