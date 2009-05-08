/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.Axis;
import org.olap4j.CellSetAxisMetaData;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Property;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.CellSetMetaData}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
* @since Nov 17, 2007
*/
class XmlaOlap4jCellSetAxisMetaData implements CellSetAxisMetaData {
    private final Axis axis;
    private final List<Hierarchy> hierarchyList;
    private final List<XmlaOlap4jCellSetMemberProperty> propertyList;

    XmlaOlap4jCellSetAxisMetaData(
        XmlaOlap4jConnection olap4jConnection,
        Axis axis,
        List<Hierarchy> hierarchyList,
        List<XmlaOlap4jCellSetMemberProperty> propertyList)
    {
        this.axis = axis;
        this.hierarchyList = hierarchyList;
        this.propertyList = propertyList;
    }

    public Axis getAxisOrdinal() {
        return axis;
    }

    public List<Hierarchy> getHierarchies() {
        return hierarchyList;
    }

    public List<Property> getProperties() {
        return Olap4jUtil.cast(propertyList);
    }

    XmlaOlap4jCellSetMemberProperty lookupProperty(
        String hierarchyName,
        String tag)
    {
        for (XmlaOlap4jCellSetMemberProperty property : propertyList) {
            if (property.hierarchy.getName().equals(hierarchyName)
                && property.tag.equals(tag)) {
                return property;
            }
        }
        return null;
    }
}

// End XmlaOlap4jCellSetAxisMetaData.java
