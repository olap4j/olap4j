/*
// $Id$
//
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
import org.olap4j.mdx.ParseTreeNode;

import java.util.List;

/**
 * <code>Member</code> is a data value in an OLAP Dimension.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface Member extends MetadataElement {
    /**
     * Returns the children of this Member, indexed by name.
     *
     * <p>If access-control is in place, the list does not contain inaccessible
     * children.
     *
     * <p>If the member has no children, returns an empty list: the result is
     * never null.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getMembers
     *
     * @return children of this member
     *
     * @throws OlapException if database error occurs
     */
    NamedList<? extends Member> getChildMembers() throws OlapException;

    /**
     * Returns the number of children this Member has.
     *
     * <p>This method has the same effect as
     * <code>getChildMembers().size()</code>, but is typically less expensive.
     *
     * @return number of children
     *
     * @throws OlapException if database error occurs
     */
    int getChildMemberCount() throws OlapException;

    /**
     * Returns the parent of this Member, or null if it has no parent.
     *
     * @return Parent member, or null if member has no parent
     */
    Member getParentMember();

    /**
     * Returns the Level of this Member.
     *
     * <p>Never returns null.</p>
     *
     * @return Level which this Member belongs to
     */
    Level getLevel();

    /**
     * Returns the Hierarchy of this Member.
     *
     * <p>Never returns null.
     * Result is always the same as <code>getLevel().getHierarchy()</code>.
     *
     * @return Hierarchy which this Member belongs to
     */
    Hierarchy getHierarchy();

    /**
     * Returns the Dimension of this Member.
     *
     * <p>Never returns null. Result is always the same as
     * <code>getLevel().getHierarchy().getDimension()</code>.
     *
     * @return Dimension which this Member belongs to
     */
    Dimension getDimension();

    /**
     * Returns the type of this Member.
     *
     * <p>Never returns null.</p>
     *
     * @return What kind of member this is
     */
    Type getMemberType();

    /**
     * Returns whether this Member represents the aggregation of all members
     * in its Dimension.
     *
     * <p>An 'all' member is always the root of its Hierarchy; that is,
     * its parent member is the null member, and
     * {@link Hierarchy#getRootMembers()} returns the 'all'
     * member and no others. Some hierarchies do not have an 'all' member.
     *
     * @see Hierarchy#hasAll()
     *
     * @return whether this Member is the 'all' member of its Dimension
     */
    boolean isAll();

    /**
     * Enumeration of types of members.
     *
     * <p>The values are as specified by XMLA,
     * plus the additional {@link #NULL} value not used by XMLA.
     * For example, XMLA specifies <code>MDMEMBER_TYPE_REGULAR</code> with
     * ordinal 1, which corresponds to value {@link #REGULAR}.
     *
     * <p>The {@link #FORMULA} value takes precedence over {@link #MEASURE}.
     * For example, if there is a formula (calculated) member on the Measures
     * dimension, it is listed as <code>FORMULA</code>.
     */
    enum Type {
        UNKNOWN(0),
        REGULAR(1),
        ALL(2),
        MEASURE(3),
        FORMULA(4),
        /**
         * Indicates that this member is its hierarchy's NULL member (such as is
         * returned by the expression
         * <code>[Gender]&#46;[All Gender]&#46;PrevMember</code>, for example).
         */
        NULL(5);

        private Type(int ordinal) {
            assert ordinal == ordinal();
        }
    }

    /**
     * Returns whether <code>member</code> is equal to, a child of, or a
     * descendent of this Member.
     *
     * @param member Member
     * @return Whether the given Member is a descendent of this Member
     */
    boolean isChildOrEqualTo(Member member);

    /**
     * Returns whether this member is calculated using a formula.
     *
     * <p>Examples of calculated members include
     * those defined using a <code>WITH MEMBER</code> clause in an MDX query
     * ({@link #getMemberType()} will return {@link Type#FORMULA} for these),
     *  or a calculated member defined in a cube.
     *
     * @return Whether this Member is calculated
     *
     * @see #isCalculatedInQuery()
     */
    boolean isCalculated();

    /**
     * Returns the solve order of this member in a formula.
     *
     * @return solve order of this Member
     */
    int getSolveOrder();

    /**
     * Expression by which this member is derived, if it is a calculated
     * member. If the member is not calulated, returns null.
     *
     * @return expression for this member
     */
    ParseTreeNode getExpression();

    /**
     * Returns array of all members which are ancestor to <code>this</code>.
     *
     * @return ancestor Members
     */
    List<Member> getAncestorMembers();

    /**
     * Returns whether this member is computed from a <code>WITH MEMBER</code>
     * clause in an MDX query. (Calculated members can also be calculated in a
     * cube.)
     *
     * @return Whether this member is calculated in a query
     *
     * @see #isCalculated()
     */
    boolean isCalculatedInQuery();

    /**
     * Returns the value of a given property.
     *
     * <p>Returns null if the property is not set.</p>
     *
     * <p>Every member has certain system properties such as "name" and
     * "caption" (the full list is described in the
     * {@link org.olap4j.metadata.Property.StandardMemberProperty}
     * enumeration), as well as extra properties defined for its Level
     * (see {@link Level#getProperties()}).</p>
     *
     * @param property Property
     *
     * @return formatted value of the given property
     *
     * @see #getPropertyFormattedValue(Property)
     *
     * @throws OlapException if database error occurs
     */
    Object getPropertyValue(Property property) throws OlapException;

    /**
     * Returns the formatted value of a given property.
     *
     * <p>Returns null if the property is not set.</p>
     *
     * <p>Every member has certain system properties such as "name" and
     * "caption" (the full list is described in the
     * {@link org.olap4j.metadata.Property.StandardMemberProperty}
     * enumeration), as well as extra properties defined for its Level
     * (see {@link Level#getProperties()}).</p>
     *
     * @param property Property
     *
     * @return formatted value of the given property
     *
     * @see #getPropertyValue(Property)
     *
     * @throws OlapException if database error occurs
     */
    String getPropertyFormattedValue(Property property) throws OlapException;

    /**
     * Sets a property of this member to a given value.
     *
     * <p>Every member has certain system properties such as "name" and
     * "caption" (the full list is described in the
     * {@link org.olap4j.metadata.Property.StandardMemberProperty}
     * enumeration), as well as extra properties defined for its Level
     * (see {@link Level#getProperties()}).</p>
     *
     * @param property property
     *
     * @param value Property value
     *
     * @throws OlapException if the value not valid for this property
     *   (for example, a String value assigned to a Boolean property)
     */
    void setProperty(Property property, Object value) throws OlapException;

    /**
     * Returns the definitions of the properties this member may have.
     *
     * <p>For many providers, properties are defined against a Level, so result
     * of this method will be identical to
     * <code>member.getLevel().{@link Level#getProperties() getProperties}()</code>.
     *
     * @return properties of this Member
     */
    NamedList<Property> getProperties();

    /**
     * Returns the ordinal of the member.
     *
     * @return ordinal of this Member
     */
    int getOrdinal();

    /**
     * Returns whether this member is 'hidden', as per the rules which define
     * a ragged hierarchy.
     *
     * @return whether this member is a hidden member of a ragged hierarchy
     */
    boolean isHidden();

    /**
     * Returns the depth of this member.
     *
     * <p>In regular hierarchies, this is as the same as the level's depth,
     * but in parent-child and ragged hierarchies the value may be
     * different.</p>
     *
     * @return depth of this Member
     */
    int getDepth();

    /**
     * Returns the system-generated data member that is associated with a
     * non-leaf member of a dimension.
     *
     * <p>Returns this member if this member is a leaf member, or if the
     * non-leaf member does not have an associated data member.</p>
     *
     * @return system-generated data member
     */
    Member getDataMember();

    /**
     * Enumeration of tree operations which can be used when querying
     * members.
     *
     * <p>Some of the values are as specified by XMLA.
     * For example, XMLA specifies MDTREEOP_CHILDREN with ordinal 1,
     * which corresponds to the value {@link #CHILDREN}.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getMembers
     */
    public enum TreeOp implements XmlaConstant {
        /**
         * Tree operation which returns only the immediate children.
         */
        CHILDREN(
            1,
            "Tree operation which returns only the immediate children."),

        /**
         * Tree operation which returns members on the same level.
         */
        SIBLINGS(
            2,
            "Tree operation which returns members on the same level."),

        /**
         * Tree operation which returns only the immediate parent.
         */
        PARENT(
            4,
            "Tree operation which returns only the immediate parent."),

        /**
         * Tree operation which returns itself in the list of returned rows.
         */
        SELF(
            8,
            "Tree operation which returns itself in the list of returned "
            + "rows."),

        /**
         * Tree operation which returns all of the descendants.
         */
        DESCENDANTS(
            16,
            "Tree operation which returns all of the descendants."),

        /**
         * Tree operation which returns all of the ancestors.
         */
        ANCESTORS(
            32,
            "Tree operation which returns all of the ancestors.");

        private final int xmlaOrdinal;
        private String description;

        private static final Dictionary<TreeOp> DICTIONARY =
            DictionaryImpl.forClass(TreeOp.class);

        /**
         * Per {@link org.olap4j.metadata.XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<TreeOp> getDictionary() {
            return DICTIONARY;
        }

        private TreeOp(int xmlaOrdinal, String description) {
            this.xmlaOrdinal = xmlaOrdinal;
            this.description = description;
        }

        public String xmlaName() {
            return "MDTREEOP_" + name();
        }

        public String getDescription() {
            return description;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }
}

// End Member.java
