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

import java.util.List;

import org.olap4j.OlapException;
import org.olap4j.mdx.ParseTreeNode;

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
     * @see org.olap4j.OlapDatabaseMetaData#getMembers()
     *
     * @return children of this member
     */
    NamedList<? extends Member> getChildMembers();

    /**
     * Returns the parent of this Member, or null if it has no parent.
     */
    Member getParentMember();

    /**
     * Returns the Level of this Member.
     *
     * <p>Never returns null.</p>
     */
    Level getLevel();

    /**
     * Returns the Hierarchy of this Member.
     *
     * <p>Never returns null.
     * Result is always the same as <code>getLevel().getHierarchy()</code>.
     */
    Hierarchy getHierarchy();

    /**
     * Returns the Dimension of this Member.
     *
     * <p>Never returns null. Result is always the same as
     * <code>getLevel().getHierarchy().getDimension()</code>.
     */
    Dimension getDimension();

    /**
     * Returns the type of this Member.
     *
     * <p>Never returns null.</p>
     */
    Type getMemberType();

    /**
     * Enumeration of types of members.
     *
     * @see Dimension.Type
     * @see Level.Type
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
     */
    boolean isChildOrEqualTo(Member member);

    /**
     * Returns whether this member is calculated using a formula.
     *
     * <p>Examples of calculated members include
     * those defined using a <code>WITH MEMBER</code> clause in an MDX query
     * ({@link #getMemberType()} will return {@link Type#FORMULA} for these),
     *  or a calculated member defined in a cube.
     */
    boolean isCalculated();

    /**
     * Returns the solve order of this member in a formula.
     */
    int getSolveOrder();

    ParseTreeNode getExpression();

    /**
     * Returns array of all members, which are ancestor to <code>this</code>.
     */
    List<Member> getAncestorMembers();

    /**
     * Returns whether this member is computed from a <code>with member</code>
     * clause in an mdx query.
     */
    boolean isCalculatedInQuery();

    /**
     * Returns the value of the property named <code>propertyName</code>.
     *
     * @see #getPropertyFormattedValue(String)
     */
    Object getPropertyValue(String propertyName);

    /**
     * Returns the formatted value of the property named
     * <code>propertyName</code>.
     *
     * <p>Every member has certain system properties such as "name" and
     * "caption" (the full list is described in the {@link Property}
     * enumeration), as well as extra properties defined for its Level
     * (see {@link Level#getProperties()}).</p>
     *
     * @see #getPropertyValue(String)
     */
    String getPropertyFormattedValue(String propertyName);

    /**
     * Sets a property of this member to a given value.
     *
     * @param name Property name
     * @param value Property value
     * @throws OlapException if the value not valid for this property
     *   (for example, a String value assigned to a Boolean property)
     */
    void setProperty(String name, Object value) throws OlapException;

    /**
     * Returns the definitions of the properties this member may have.
     */
    List<Property> getProperties();

    /**
     * Returns the ordinal of the member.
     */
    int getOrdinal();

    /**
     * Returns whether this member is 'hidden', as per the rules which define
     * a ragged hierarchy.
     */
    boolean isHidden();

    /**
     * Returns the depth of this member.
     *
     * <p>In regular hierarchies, this is as the same as the level's depth,
     * but in parent-child and ragged hierarchies the value may be
     * different.</p>
     */
    int getDepth();

    /**
     * Returns the system-generated data member that is associated with a
     * nonleaf member of a dimension.
     *
     * <p>Returns this member if this member is a leaf member, or if the
     * nonleaf member does not have an associated data member.</p>
     */
    Member getDataMember();
}

// End Member.java
