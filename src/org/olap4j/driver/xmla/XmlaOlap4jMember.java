/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.*;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.*;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.metadata.Member}
 * for XML/A providers.
 *
 * <p>TODO:<ol>
 * <li>create members with a pointer to their parent member (not the name)</li>
 * <li>implement a member cache (by unique name, belongs to cube, soft)</li>
 * <li>implement Hierarchy.getRootMembers</li>
 * </ol>
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 5, 2007
 */
class XmlaOlap4jMember
    extends XmlaOlap4jElement
    implements XmlaOlap4jMemberBase, Member, Named
{
    private final XmlaOlap4jLevel olap4jLevel;

    // TODO: We would rather have a refernce to the parent member, but it is
    // tricky to populate
    /*
    private final XmlaOlap4jMember parentMember;
    */
    private final String parentMemberUniqueName;
    private final Type type;
    private XmlaOlap4jMember parentMember;
    private final int childMemberCount;
    private final int ordinal;
    private final Map<Property, Object> propertyValueMap;

    /**
     * Creates an XmlaOlap4jMember.
     *
     * @param olap4jLevel Level
     * @param uniqueName Unique name
     * @param name Name
     * @param caption Caption
     * @param description Description
     * @param parentMemberUniqueName Unique name of parent, or null if no parent
     * @param type Type
     * @param childMemberCount Number of children
     * @param ordinal Ordinal in its hierarchy
     * @param propertyValueMap Property values
     */
    XmlaOlap4jMember(
        XmlaOlap4jLevel olap4jLevel,
        String uniqueName,
        String name,
        String caption,
        String description,
        String parentMemberUniqueName,
        Type type,
        int childMemberCount,
        int ordinal,
        Map<Property, Object> propertyValueMap)
    {
        super(uniqueName, name, caption, description);
        this.ordinal = ordinal;
        assert olap4jLevel != null;
        assert type != null;
        this.olap4jLevel = olap4jLevel;
        this.parentMemberUniqueName = parentMemberUniqueName;
        this.type = type;
        this.childMemberCount = childMemberCount;
        if (propertyValueMap.isEmpty()) {
            this.propertyValueMap = Collections.emptyMap();
        } else {
            this.propertyValueMap =
                new ArrayMap<Property, Object>(propertyValueMap);
        }
    }

    public int hashCode() {
        return uniqueName.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof XmlaOlap4jMember
            && ((XmlaOlap4jMember) obj).uniqueName.equals(uniqueName);
    }

    public NamedList<? extends Member> getChildMembers() throws OlapException {
        final NamedList<XmlaOlap4jMember> list =
            new NamedListImpl<XmlaOlap4jMember>();
        getCube()
            .getMetadataReader()
            .lookupMemberRelatives(
                EnumSet.of(TreeOp.CHILDREN),
                uniqueName,
                list);
        return list;
    }

    public int getChildMemberCount() {
        return childMemberCount;
    }

    public XmlaOlap4jMember getParentMember() {
        if (parentMemberUniqueName == null) {
            return null;
        }
        if (parentMember == null) {
            try {
                parentMember =
                    getCube().getMetadataReader()
                        .lookupMemberByUniqueName(parentMemberUniqueName);
            } catch (OlapException e) {
                throw new RuntimeException("yuck!"); // FIXME
            }
        }
        return parentMember;
    }

    public XmlaOlap4jLevel getLevel() {
        return olap4jLevel;
    }

    public XmlaOlap4jHierarchy getHierarchy() {
        return olap4jLevel.olap4jHierarchy;
    }

    public XmlaOlap4jDimension getDimension() {
        return olap4jLevel.olap4jHierarchy.olap4jDimension;
    }

    public Type getMemberType() {
        return type;
    }

    public boolean isAll() {
        return type == Type.ALL;
    }

    public boolean isChildOrEqualTo(Member member) {
        throw new UnsupportedOperationException();
    }

    public boolean isCalculated() {
        return type == Type.FORMULA;
    }

    public int getSolveOrder() {
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode getExpression() {
        throw new UnsupportedOperationException();
    }

    public List<Member> getAncestorMembers() {
        final List<Member> list = new ArrayList<Member>();
        XmlaOlap4jMember m = getParentMember();
        while (m != null) {
            list.add(m);
            m = m.getParentMember();
        }
        return list;
    }

    public boolean isCalculatedInQuery() {
        throw new UnsupportedOperationException();
    }

    public Object getPropertyValue(Property property) {
        return getPropertyValue(
            property,
            this,
            propertyValueMap);
    }

    /**
     * Helper method to retrieve the value of a property from a member.
     *
     * @param property Property
     * @param member Member
     * @param propertyValueMap Map of property-value pairs
     * @return Property value
     */
    static Object getPropertyValue(
        Property property,
        XmlaOlap4jMemberBase member,
        Map<Property, Object> propertyValueMap)
    {
        // If property map contains a value for this property (even if that
        // value is null), that overrides.
        final Object value = propertyValueMap.get(property);
        if (value != null || propertyValueMap.containsKey(property)) {
            return value;
        }
        if (property instanceof Property.StandardMemberProperty) {
            Property.StandardMemberProperty o =
                (Property.StandardMemberProperty) property;
            switch (o) {
            case MEMBER_CAPTION:
                return member.getCaption(member.getConnection().getLocale());
            case MEMBER_NAME:
                return member.getName();
            case MEMBER_UNIQUE_NAME:
                return member.getUniqueName();
            case CATALOG_NAME:
                return member.getCatalog().getName();
            case CHILDREN_CARDINALITY:
                return member.getChildMemberCount();
            case CUBE_NAME:
                return member.getCube().getName();
            case DEPTH:
                return member.getDepth();
            case DESCRIPTION:
                return member.getDescription(
                    member.getConnection().getLocale());
            case DIMENSION_UNIQUE_NAME:
                return member.getDimension().getUniqueName();
            case DISPLAY_INFO:
                // TODO:
                return null;
            case HIERARCHY_UNIQUE_NAME:
                return member.getHierarchy().getUniqueName();
            case LEVEL_NUMBER:
                return member.getLevel().getDepth();
            case LEVEL_UNIQUE_NAME:
                return member.getLevel().getUniqueName();
            case MEMBER_GUID:
                // TODO:
                return null;
            case MEMBER_ORDINAL:
                return member.getOrdinal();
            case MEMBER_TYPE:
                return member.getMemberType();
            case PARENT_COUNT:
                return 1;
            case PARENT_LEVEL:
                return member.getParentMember().getLevel().getDepth();
            case PARENT_UNIQUE_NAME:
                return member.getParentMember().getUniqueName();
            case SCHEMA_NAME:
                return member.getCube().olap4jSchema.getName();
            case VALUE:
                // TODO:
                return null;
            }
        }
        return null;
    }

    // convenience method - not part of olap4j API
    public XmlaOlap4jCube getCube() {
        return olap4jLevel.olap4jHierarchy.olap4jDimension.olap4jCube;
    }

    // convenience method - not part of olap4j API
    public XmlaOlap4jCatalog getCatalog() {
        return olap4jLevel.olap4jHierarchy.olap4jDimension.olap4jCube
            .olap4jSchema.olap4jCatalog;
    }

    // convenience method - not part of olap4j API
    public XmlaOlap4jConnection getConnection() {
        return olap4jLevel.olap4jHierarchy.olap4jDimension.olap4jCube
            .olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData
            .olap4jConnection;
    }

    // convenience method - not part of olap4j API
    public Map<Property, Object> getPropertyValueMap() {
        return propertyValueMap;
    }

    public String getPropertyFormattedValue(Property property) {
        // FIXME: need to use a format string; but what format string; and how
        // to format the property on the client side?
        return String.valueOf(getPropertyValue(property));
    }

    public void setProperty(Property property, Object value) {
        propertyValueMap.put(property, value);
    }

    public NamedList<Property> getProperties() {
        return olap4jLevel.getProperties();
    }

    public int getOrdinal() {
        return ordinal;
    }

    public boolean isHidden() {
        throw new UnsupportedOperationException();
    }

    public int getDepth() {
        // Since in regular hierarchies members have the same depth as their
        // level, we store depth as a property only where it is different.
        final Object depth =
            propertyValueMap.get(Property.StandardMemberProperty.DEPTH);
        if (depth == null) {
            return olap4jLevel.getDepth();
        } else {
            return toInteger(depth);
        }
    }

    /**
     * Converts an object to an integer value. Must not be null.
     *
     * @param o Object
     * @return Integer value
     */
    static int toInteger(Object o) {
        if (o instanceof Number) {
            Number number = (Number) o;
            return number.intValue();
        }
        return Integer.valueOf(o.toString());
    }

    public Member getDataMember() {
        throw new UnsupportedOperationException();
    }
}

// End XmlaOlap4jMember.java
