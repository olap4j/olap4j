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
import org.olap4j.metadata.*;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.metadata.Level}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 4, 2007
 */
class XmlaOlap4jLevel
    extends XmlaOlap4jElement
    implements Level, Named
{
    final XmlaOlap4jHierarchy olap4jHierarchy;
    private final int depth;
    private final Type type;
    private final int cardinality;
    private final NamedList<XmlaOlap4jProperty> propertyList;

    XmlaOlap4jLevel(
        final XmlaOlap4jHierarchy olap4jHierarchy,
        String uniqueName, String name,
        String caption,
        String description,
        int depth,
        Type type,
        int cardinality)
    {
        super(uniqueName, name, caption, description);
        assert olap4jHierarchy != null;
        this.type = type;
        this.cardinality = cardinality;
        this.depth = depth;
        this.olap4jHierarchy = olap4jHierarchy;
        this.propertyList = new DeferredNamedListImpl<XmlaOlap4jProperty>(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_PROPERTIES,
            new XmlaOlap4jConnection.Context(this),
            new XmlaOlap4jConnection.PropertyHandler())
        {
            protected void populateList(
                NamedList<XmlaOlap4jProperty> list) throws OlapException
            {
                context.olap4jConnection.populateList(
                    list, context, metadataRequest, handler,
                    new Object[] {
                        "CATALOG_NAME",
                        olap4jHierarchy.olap4jDimension.olap4jCube.olap4jSchema
                            .olap4jCatalog.getName(),
                        "SCHEMA_NAME",
                        olap4jHierarchy.olap4jDimension.olap4jCube.olap4jSchema
                            .getName(),
                        "CUBE_NAME",
                        olap4jHierarchy.olap4jDimension.olap4jCube.getName(),
                        "DIMENSION_UNIQUE_NAME",
                        olap4jHierarchy.olap4jDimension.getUniqueName(),
                        "HIERARCHY_UNIQUE_NAME",
                        olap4jHierarchy.getUniqueName(),
                        "LEVEL_UNIQUE_NAME",
                        getUniqueName()
                    });
            }
        };
    }

    public int getDepth() {
        return depth;
    }

    public Hierarchy getHierarchy() {
        return olap4jHierarchy;
    }

    public Dimension getDimension() {
        return olap4jHierarchy.olap4jDimension;
    }

    public Type getLevelType() {
        return type;
    }

    public NamedList<Property> getProperties() {
        final NamedList<Property> list = new ArrayNamedListImpl<Property>() {
            protected String getName(Property property) {
                return property.getName();
            }
        };
        // standard properties first
        list.addAll(
            Arrays.asList(Property.StandardMemberProperty.values()));
        // then level-specific properties
        list.addAll(propertyList);
        return list;
    }

    public List<Member> getMembers() throws OlapException {
        return Olap4jUtil.cast(
            olap4jHierarchy.olap4jDimension.olap4jCube.getMetadataReader()
                .getLevelMembers(this));
    }

    public int getCardinality() {
        return cardinality;
    }
}

// End XmlaOlap4jLevel.java
