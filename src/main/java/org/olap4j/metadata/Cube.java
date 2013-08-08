/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.metadata;

import org.olap4j.OlapException;
import org.olap4j.mdx.IdentifierSegment;

import java.util.*;

/**
 * Central metadata object for representation of multidimensional data.
 *
 * <p>A Cube belongs to a {@link Schema}, and is described by a list of
 * {@link Dimension}s and a list of {@link Measure}s. It may also have one or
 * more {@link NamedSet}s.
 *
 * @see org.olap4j.metadata.Cube#getMeasures()
 *
 * @author jhyde
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
     * Returns a list of {@link Hierarchy} objects in this Cube.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getHierarchies(String, String, String, String, String)
     *
     * @return list of Dimensions
     */
    NamedList<Hierarchy> getHierarchies();

    /**
     * Returns a list of {@link MeasureGroup} objects in this Cube.
     *
     * @return list of MeasureGroups
     */
    NamedList<MeasureGroup> getMeasureGroups();

    /**
     * Returns a list of {@link Measure} objects in this Cube.
     *
     * <p>The list includes both stored and calculated members, and (unlike
     * the {@link org.olap4j.OlapDatabaseMetaData#getMeasures} method or the
     * MDSCHEMA_MEASURES XMLA request) is sorted by ordinal.
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
     * <p>For example, {@code "[Product].[Food]"} and
     * {@code "[Product].[All Products].[Food]"}
     * are both valid ways to locate the "Food" member of the "Product"
     * dimension.
     *
     * <p>The name is represented as a list of {@link IdentifierSegment}
     * objects. There are some common ways to create such a list. If you have an
     * identifier, call
     * {@link org.olap4j.mdx.IdentifierNode#parseIdentifier(String)}
     * to parse the string into an identifier, then
     * {@link org.olap4j.mdx.IdentifierNode#getSegmentList()}. For example,
     *
     * <blockquote><code>Member member = cube.lookupMember(<br/>
     * &nbsp;&nbsp;IdentifierNode.parseIdentifier(
     * "[Product].[Food]").getSegmentList())</code></blockquote>
     *
     * <p>If you have an array of names, call
     * {@link org.olap4j.mdx.IdentifierNode#ofNames(String...)}. For example,
     *
     * <blockquote><code>Member member = cube.lookupMember(<br/>
     * &nbsp;&nbsp;IdentifierNode.parseIdentifier(
     * "[Product].[Food]").getSegmentList())</code></blockquote>
     *
     * @param nameParts Components of the fully-qualified member name
     *
     * @return member with the given name, or null if not found
     *
     * @throws OlapException if error occurs
     */
    Member lookupMember(List<IdentifierSegment> nameParts) throws OlapException;

    /**
     * Finds a collection of members in the current Cube related to a given
     * member.
     *
     * <p>The method first looks up a member with the given fully-qualified
     * name as for {@link #lookupMember(java.util.List)}, then applies the set
     * of tree-operations to find related members.
     *
     * <p>The returned collection is sorted by level number then by member
     * ordinal. If no member is found with the given name, the collection is
     * empty.
     *
     * <p>For example,
     *
     * <blockquote><pre>
     * <code>lookupMembers(
     *     EnumSet.of(TreeOp.ANCESTORS, TreeOp.CHILDREN),
     *     "Time", "1997", "Q2")</code>
     * </pre></blockquote>
     *
     * returns
     *
     * <blockquote><pre><code>
     * [Time].[1997]
     * [Time].[1997].[Q2].[4]
     * [Time].[1997].[Q2].[5]
     * [Time].[1997].[Q2].[6]
     * </code></pre></blockquote>
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
     *
     * @param treeOps Collection of tree operations to travel relative to
     * given member in order to create list of members
     *
     * @return collection of members related to the given member, or empty
     * set if the member is not found
     *
     * @throws OlapException if error occurs
     */
    List<Member> lookupMembers(
        Set<Member.TreeOp> treeOps,
        List<IdentifierSegment> nameParts) throws OlapException;

    /**
     * Tells whether or not drill through operations are
     * possible in this cube.
     * @return True if drillthrough is enabled, false otherwise.
     */
    boolean isDrillThroughEnabled();

    /** Type of a cube. */
    enum Type implements XmlaConstant {
        /** Cube (1). */
        CUBE(1),

        /** Dimension (2). */
        DIMENSION(2);

        private final int xmlaOrdinal;

        /** Per {@link XmlaConstant}. */
        public static final DictionaryImpl<Type> DICTIONARY =
            DictionaryImpl.forClass(Type.class);

        private Type(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return "";
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }
}

// End Cube.java
