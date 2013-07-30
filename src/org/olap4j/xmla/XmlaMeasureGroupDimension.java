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
package org.olap4j.xmla;

import org.olap4j.metadata.Dimension;

import java.util.List;

/**
 * XML for Analysis entity that enumerates the dimensions of measure groups.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_MEASUREGROUP_DIMENSIONS} schema
 * rowset.</p>
 */
public class XmlaMeasureGroupDimension extends Entity {
    public static final XmlaMeasureGroupDimension INSTANCE =
        new XmlaMeasureGroupDimension();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_MEASUREGROUP_DIMENSIONS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            MeasuregroupName,
            MeasuregroupCardinality,
            DimensionUniqueName,
            DimensionCardinality,
            DimensionIsVisible,
            DimensionIsFactDimension,
            DimensionPath,
            DimensionGranularity);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            MeasuregroupName,
            DimensionUniqueName);
    }

    @Override
    List<Column> restrictionColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            MeasuregroupName,
            DimensionUniqueName,
            DimensionVisibility);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this cube belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the schema to which this cube belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the cube to which this measure group belongs.");
    public final Column MeasuregroupName =
        new Column(
            "MEASUREGROUP_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of measure group.");
    public final Column MeasuregroupCardinality =
        new Column(
            "MEASUREGROUP_CARDINALITY",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The number of instances a measure in the measure group can have "
            + "for a single dimension member. Possible values include: ONE, "
            + "MANY");
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name for the dimension.");
    public final Column DimensionCardinality =
        new Column(
            "DIMENSION_CARDINALITY",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The number of instances a dimension member can have for a single "
            + "instance of a measure group measure. Possible values include: "
            + "ONE, MANY");
    public final Column DimensionIsVisible =
        new Column(
            "DIMENSION_IS_VISIBLE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether hierarchies in the dimension "
            + "are visible.\n"
            + "Returns TRUE if one or more hierarchies in the dimension is "
            + "visible; otherwise, FALSE.");
    public final Column DimensionIsFactDimension =
        new Column(
            "DIMENSION_IS_FACT_DIMENSION",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether the dimension is a fact "
            + "dimension. Returns TRUE if the dimension is a fact dimension; "
            + "otherwise, FALSE.");
    public final Column DimensionPath =
        new Column(
            "DIMENSION_PATH",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A list of dimensions for the reference dimension.");
    public final Column DimensionGranularity =
        new Column(
            "DIMENSION_GRANULARITY",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Unique name of the granularity hierarchy.");

    // Only a restriction.
    public final Column DimensionVisibility =
        new Column(
            "DIMENSION_VISIBILITY",
            XmlaType.UnsignedShort.scalar(),
            Column.Restriction.OPTIONAL.of(
                Enumeration.VISIBILITY, Dimension.Visibility.VISIBLE),
            Column.OPTIONAL,
            null);

}

// End XmlaMeasureGroupDimension.java
