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
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.*;
import org.olap4j.metadata.*;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of {@link org.olap4j.metadata.Level}
 * for XML/A providers.
 *
 * @author jhyde
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
    final NamedList<XmlaOlap4jMember> memberList;
    private final boolean calculated;

    /**
     * Creates an XmlaOlap4jLevel.
     *
     * @param olap4jHierarchy Hierarchy
     * @param uniqueName Unique name
     * @param name Name
     * @param caption Caption
     * @param description Description
     * @param depth Distance to root
     * @param type Level type
     * @param calculated Whether level is calculated
     * @param cardinality Number of members in this level
     */
    XmlaOlap4jLevel(
        final XmlaOlap4jHierarchy olap4jHierarchy,
        String uniqueName, String name,
        String caption,
        String description,
        int depth,
        Type type,
        boolean calculated,
        int cardinality)
    {
        super(uniqueName, name, caption, description);
        assert olap4jHierarchy != null;
        this.type = type;
        this.calculated = calculated;
        this.cardinality = cardinality;
        this.depth = depth;
        this.olap4jHierarchy = olap4jHierarchy;

        String[] levelRestrictions = {
                "CATALOG_NAME",
                olap4jHierarchy.olap4jDimension.olap4jCube
                    .olap4jSchema.olap4jCatalog.getName(),
                "SCHEMA_NAME",
                olap4jHierarchy.olap4jDimension.olap4jCube
                    .olap4jSchema.getName(),
                "CUBE_NAME",
                olap4jHierarchy.olap4jDimension.olap4jCube.getName(),
                "DIMENSION_UNIQUE_NAME",
                olap4jHierarchy.olap4jDimension.getUniqueName(),
                "HIERARCHY_UNIQUE_NAME",
                olap4jHierarchy.getUniqueName(),
                "LEVEL_UNIQUE_NAME",
                getUniqueName()
            };

        this.propertyList = new DeferredNamedListImpl<XmlaOlap4jProperty>(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_PROPERTIES,
            new XmlaOlap4jConnection.Context(this),
            new XmlaOlap4jConnection.PropertyHandler(),
            levelRestrictions);

        try {
            if (olap4jHierarchy.olap4jDimension.getDimensionType()
                == Dimension.Type.MEASURE)
            {
                String[] restrictions = {
                    "CATALOG_NAME",
                    olap4jHierarchy.olap4jDimension.olap4jCube.olap4jSchema
                        .olap4jCatalog.getName(),
                    "SCHEMA_NAME",
                    olap4jHierarchy.olap4jDimension.olap4jCube.olap4jSchema
                        .getName(),
                    "CUBE_NAME",
                    olap4jHierarchy.olap4jDimension.olap4jCube.getName()
                };
                this.memberList = Olap4jUtil.cast(
                    new DeferredNamedListImpl<XmlaOlap4jMeasure>(
                        XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEASURES,
                        new XmlaOlap4jConnection.Context(
                            olap4jHierarchy.olap4jDimension
                                .olap4jCube.olap4jSchema
                                .olap4jCatalog.olap4jDatabaseMetaData
                                .olap4jConnection,
                            olap4jHierarchy.olap4jDimension
                                .olap4jCube.olap4jSchema
                                .olap4jCatalog.olap4jDatabaseMetaData,
                            olap4jHierarchy.olap4jDimension.olap4jCube
                                .olap4jSchema.olap4jCatalog,
                            olap4jHierarchy.olap4jDimension.olap4jCube
                                .olap4jSchema,
                            olap4jHierarchy.olap4jDimension.olap4jCube,
                            olap4jHierarchy.olap4jDimension,
                            olap4jHierarchy,
                            this),
                        new XmlaOlap4jConnection.MeasureHandler(olap4jHierarchy.olap4jDimension.olap4jCube),
                            restrictions));
            } else {
                this.memberList = new DeferredNamedListImpl<XmlaOlap4jMember>(
                    XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEMBERS,
                    new XmlaOlap4jConnection.Context(
                        olap4jHierarchy.olap4jDimension.olap4jCube.olap4jSchema
                            .olap4jCatalog.olap4jDatabaseMetaData
                            .olap4jConnection,
                        olap4jHierarchy.olap4jDimension.olap4jCube.olap4jSchema
                            .olap4jCatalog.olap4jDatabaseMetaData,
                        olap4jHierarchy.olap4jDimension.olap4jCube.olap4jSchema
                            .olap4jCatalog,
                        olap4jHierarchy.olap4jDimension.olap4jCube.olap4jSchema,
                        olap4jHierarchy.olap4jDimension.olap4jCube,
                        olap4jHierarchy.olap4jDimension,
                        olap4jHierarchy,
                        this),
                    new XmlaOlap4jConnection.MemberHandler(),
                    levelRestrictions);
            }
        } catch (OlapException e) {
            throw new RuntimeException("Programming error", e);
        }
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

    public boolean isCalculated() {
        return calculated;
    }

    public Type getLevelType() {
        return type;
    }

    public NamedList<Property> getProperties() {
        final NamedList<Property> list = new ArrayNamedListImpl<Property>() {
            public String getName(Object property) {
                return ((Property) property).getName();
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
        return Olap4jUtil.cast(this.memberList);
    }

    public int getCardinality() {
        return cardinality;
    }

    public boolean equals(Object obj) {
        return (obj instanceof XmlaOlap4jLevel)
            && this.uniqueName.equals(
                ((XmlaOlap4jLevel) obj).getUniqueName());
    }
}

// End XmlaOlap4jLevel.java
