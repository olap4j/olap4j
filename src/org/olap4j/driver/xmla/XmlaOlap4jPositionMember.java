/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.metadata.*;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.OlapException;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.metadata.Member}
 * for positions on an axis in a cell set
 * from an XML/A provider.
 *
 * <p>This class is necessary because a member can have different properties
 * when it is retrieved as part of a cell set than if it is retrieved by
 * querying schema metadata (e.g. using {@link Cube#lookupMember(String[])}.
 * XmlaOlap4jPositionMember wraps the schema member (which might potentially
 * be cached between queries - even though today it is not) and adds extra
 * properties. All other methods are delegated to the underlying member.</p>
 *
 * @author jhyde
 * @version $Id: $
 * @since Dec 7, 2007
 */
class XmlaOlap4jPositionMember implements Member {
    private final Member member;
    private final Map<Property, String> propertyValues;

    XmlaOlap4jPositionMember(
        Member member,
        Map<Property, String> propertyValues)
    {
        this.member = member;
        this.propertyValues = propertyValues;
    }

    public NamedList<? extends Member> getChildMembers() throws OlapException {
        return member.getChildMembers();
    }

    public int getChildMemberCount() {
        return member.getChildMemberCount();
    }

    public Member getParentMember() {
        return member.getParentMember();
    }

    public Level getLevel() {
        return member.getLevel();
    }

    public Hierarchy getHierarchy() {
        return member.getHierarchy();
    }

    public Dimension getDimension() {
        return member.getDimension();
    }

    public Type getMemberType() {
        return member.getMemberType();
    }

    public boolean isAll() {
        return member.isAll();
    }

    public boolean isChildOrEqualTo(Member member) {
        return member.isChildOrEqualTo(member);
    }

    public boolean isCalculated() {
        return member.isCalculated();
    }

    public int getSolveOrder() {
        return member.getSolveOrder();
    }

    public ParseTreeNode getExpression() {
        return member.getExpression();
    }

    public List<Member> getAncestorMembers() {
        return member.getAncestorMembers();
    }

    public boolean isCalculatedInQuery() {
        return member.isCalculatedInQuery();
    }

    public Object getPropertyValue(Property property) {
        if (propertyValues.containsKey(property)) {
            return propertyValues.get(property);
        }
        return member.getPropertyValue(property);
    }

    public String getPropertyFormattedValue(Property property) {
        // REVIEW: Formatted value is not available for properties which
        // come back as part of axis tuple. Unformatted property is best we
        // can do.
        if (propertyValues.containsKey(property)) {
            return propertyValues.get(property);
        }
        return member.getPropertyFormattedValue(property);
    }

    public void setProperty(Property property, Object value) throws OlapException {
        throw new UnsupportedOperationException();
    }

    public NamedList<Property> getProperties() {
        return member.getProperties();
    }

    public int getOrdinal() {
        return member.getOrdinal();
    }

    public boolean isHidden() {
        return member.isHidden();
    }

    public int getDepth() {
        return member.getDepth();
    }

    public Member getDataMember() {
        return member.getDataMember();
    }

    public String getName() {
        return member.getName();
    }

    public String getUniqueName() {
        return member.getUniqueName();
    }

    public String getCaption(Locale locale) {
        return member.getCaption(locale);
    }

    public String getDescription(Locale locale) {
        return member.getDescription(locale);
    }
}

// End XmlaOlap4jPositionMember.java
