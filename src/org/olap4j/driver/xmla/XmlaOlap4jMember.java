/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
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
 * <li>implement Hierarchy.getRootMembers and Hierarchy.getDefaultMember</li>
 * </ol>
 *
 * @author jhyde
 * @version $Id: $
 * @since Dec 5, 2007
 */
class XmlaOlap4jMember
    extends XmlaOlap4jElement
    implements Member, Named
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
    private final ArrayMap<Property, Object> propertyValueMap =
        new ArrayMap<Property, Object>();

    XmlaOlap4jMember(
        XmlaOlap4jLevel olap4jLevel,
        String uniqueName,
        String name,
        String caption,
        String description,
        String parentMemberUniqueName,
        Type type,
        int childMemberCount,
        int ordinal)
    {
        super(uniqueName, name, caption, description);
        this.ordinal = ordinal;
        assert olap4jLevel != null;
        assert type != null;
        this.olap4jLevel = olap4jLevel;
        this.parentMemberUniqueName = parentMemberUniqueName;
        this.type = type;
        this.childMemberCount = childMemberCount;
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
            .lookupMembersByUniqueName(
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
                    getCube()
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
                return getCaption(getConnection().getLocale());
            case MEMBER_NAME:
                return getName();
            case MEMBER_UNIQUE_NAME:
                return getUniqueName();
            case CATALOG_NAME:
                return getCatalog().getName();
            case CHILDREN_CARDINALITY:
                return getChildMemberCount();
            case CUBE_NAME:
                return getCube().getName();
            case DEPTH:
                return getDepth();
            case DESCRIPTION:
                return getDescription(getConnection().getLocale());
            case DIMENSION_UNIQUE_NAME:
                return getDimension().getUniqueName();
            case DISPLAY_INFO:
                // TODO:
                return null;
            case HIERARCHY_UNIQUE_NAME:
                return getHierarchy().getUniqueName();
            case LEVEL_NUMBER:
                return getLevel().getDepth();
            case LEVEL_UNIQUE_NAME:
                return getLevel().getUniqueName();
            case MEMBER_GUID:
                // TODO:
                return null;
            case MEMBER_ORDINAL:
                return getOrdinal();
            case MEMBER_TYPE:
                return getMemberType();
            case PARENT_COUNT:
                return 1;
            case PARENT_LEVEL:
                return getParentMember().getLevel().getDepth();
            case PARENT_UNIQUE_NAME:
                return getParentMember().getUniqueName();
            case SCHEMA_NAME:
                return getCube().olap4jSchema.getName();
            case VALUE:
                // TODO:
                return null;
            }
        }
        return null;
    }

    // convenience method - not part of olap4j API
    private XmlaOlap4jCube getCube() {
        return olap4jLevel.olap4jHierarchy.olap4jDimension.olap4jCube;
    }

    // convenience method - not part of olap4j API
    private XmlaOlap4jCatalog getCatalog() {
        return olap4jLevel.olap4jHierarchy.olap4jDimension.olap4jCube
            .olap4jSchema.olap4jCatalog;
    }

    // convenience method - not part of olap4j API
    private XmlaOlap4jConnection getConnection() {
        return olap4jLevel.olap4jHierarchy.olap4jDimension.olap4jCube
            .olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData
            .olap4jConnection;
    }

    public String getPropertyFormattedValue(Property property) {
        // FIXME: need to use a format string; but what format string; and how
        // to format the property on the client side?
        return String.valueOf(getPropertyValue(property));
    }

    public void setProperty(Property property, Object value) throws OlapException {
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
        return olap4jLevel.getDepth();
    }

    public Member getDataMember() {
        throw new UnsupportedOperationException();
    }
}

// End XmlaOlap4jMember.java
