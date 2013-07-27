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
 * XML for Analysis entity representing a Cube.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_CUBES} schema rowset.</p>
 */
public class XmlaCube extends Entity {
    public static final XmlaCube INSTANCE =
        new XmlaCube();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_CUBES;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            CubeType,
            CubeGuid,
            CreatedOn,
            LastSchemaUpdate,
            SchemaUpdatedBy,
            LastDataUpdate,
            DataUpdatedBy,
            IsDrillthroughEnabled,
            IsWriteEnabled,
            IsLinkable,
            IsSqlEnabled,
            CubeCaption,
            Description,
            Dimensions,
            Sets,
            Measures);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this cube belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the schema to which this cube belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "Name of the cube.");
    public final Column CubeType =
        new Column(
            "CUBE_TYPE",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "Cube type.");
    public final Column CubeGuid =
        new Column(
            "CUBE_GUID",
            XmlaType.UUID,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Cube type.");
    public final Column CreatedOn =
        new Column(
            "CREATED_ON",
            XmlaType.DateTime,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Date and time of cube creation.");
    public final Column LastSchemaUpdate =
        new Column(
            "LAST_SCHEMA_UPDATE",
            XmlaType.DateTime,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Date and time of last schema update.");
    public final Column SchemaUpdatedBy =
        new Column(
            "SCHEMA_UPDATED_BY",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "User ID of the person who last updated the schema.");
    public final Column LastDataUpdate =
        new Column(
            "LAST_DATA_UPDATE",
            XmlaType.DateTime,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Date and time of last data update.");
    public final Column DataUpdatedBy =
        new Column(
            "DATA_UPDATED_BY",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "User ID of the person who last updated the data.");
    public final Column IsDrillthroughEnabled =
        new Column(
            "IS_DRILLTHROUGH_ENABLED",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Describes whether DRILLTHROUGH can be performed on the "
            + "members of a cube");
    public final Column IsWriteEnabled =
        new Column(
            "IS_WRITE_ENABLED",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Describes whether a cube is write-enabled");
    public final Column IsLinkable =
        new Column(
            "IS_LINKABLE",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Describes whether a cube can be used in a linked cube");
    public final Column IsSqlEnabled =
        new Column(
            "IS_SQL_ENABLED",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Describes whether or not SQL can be used on the cube");
    public final Column CubeCaption =
        new Column(
            "CUBE_CAPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The caption of the cube.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A user-friendly description of the dimension.");
    public final Column Dimensions =
        new Column(
            "DIMENSIONS",
            XmlaType.Rowset,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Dimensions in this cube.");
    public final Column Sets =
        new Column(
            "SETS",
            XmlaType.Rowset,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Sets in this cube.");
    public final Column Measures =
        new Column(
            "MEASURES",
            XmlaType.Rowset,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Measures in this cube.");
}

// End XmlaCube.java
