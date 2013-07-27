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

import java.util.List;

/**
 * XML for Analysis entity representing a Dimension.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_DIMENSIONS} schema rowset.</p>
 */
public class XmlaDimension extends Entity {
    public static final XmlaDimension INSTANCE =
        new XmlaDimension();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_DIMENSIONS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionName,
            DimensionUniqueName,
            DimensionGuid,
            DimensionCaption,
            DimensionOrdinal,
            DimensionType,
            DimensionCardinality,
            DefaultHierarchy,
            Description,
            IsVirtual,
            IsReadWrite,
            DimensionUniqueSettings,
            DimensionMasterUniqueName,
            DimensionIsVisible,
            Hierarchies);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionName);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the database.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "Not supported.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the cube.");
    public final Column DimensionName =
        new Column(
            "DIMENSION_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the dimension.");
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name of the dimension.");
    public final Column DimensionGuid =
        new Column(
            "DIMENSION_GUID",
            XmlaType.UUID,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Not supported.");
    public final Column DimensionCaption =
        new Column(
            "DIMENSION_CAPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The caption of the dimension.");
    public final Column DimensionOrdinal =
        new Column(
            "DIMENSION_ORDINAL",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The position of the dimension within the cube.");
    // SQL Server returns values:
    //   MD_DIMTYPE_TIME (1)
    //   MD_DIMTYPE_MEASURE (2)
    //   MD_DIMTYPE_OTHER (3)
    public final Column DimensionType =
        new Column(
            "DIMENSION_TYPE",
            XmlaType.Short,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The type of the dimension.");
    public final Column DimensionCardinality =
        new Column(
            "DIMENSION_CARDINALITY",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The number of members in the key attribute.");
    public final Column DefaultHierarchy =
        new Column(
            "DEFAULT_HIERARCHY",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A hierarchy from the dimension. Preserved for backwards "
            + "compatibility.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A user-friendly description of the dimension.");
    public final Column IsVirtual =
        new Column(
            "IS_VIRTUAL",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Always FALSE.");
    public final Column IsReadWrite =
        new Column(
            "IS_READWRITE",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether the dimension is "
            + "write-enabled.");
    // SQL Server returns values: 0 or 1
    public final Column DimensionUniqueSettings =
        new Column(
            "DIMENSION_UNIQUE_SETTINGS",
            XmlaType.Integer,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A bitmap that specifies which columns contain unique values "
            + "if the dimension contains only members with unique names.");
    public final Column DimensionMasterUniqueName =
        new Column(
            "DIMENSION_MASTER_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Always NULL.");
    public final Column DimensionIsVisible =
        new Column(
            "DIMENSION_IS_VISIBLE",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Always TRUE.");
    public final Column Hierarchies =
        new Column(
            "HIERARCHIES",
            XmlaType.Rowset,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Hierarchies in this dimension.");
}

// End XmlaDimension.java
