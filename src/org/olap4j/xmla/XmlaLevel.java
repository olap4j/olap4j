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
 * XML for Analysis entity representing a Level.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_DIMENSIONS} schema rowset.</p>
 */
public class XmlaLevel extends Entity {
    public static final XmlaLevel INSTANCE =
        new XmlaLevel();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_LEVELS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyUniqueName,
            LevelName,
            LevelUniqueName,
            LevelGuid,
            LevelCaption,
            LevelNumber,
            LevelCardinality,
            LevelType,
            CustomRollupSettings,
            LevelUniqueSettings,
            LevelIsVisible,
            Description);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyUniqueName,
            LevelNumber);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this level belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the schema to which this level belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the cube to which this level belongs.");
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name of the dimension to which this level "
            + "belongs.");
    public final Column HierarchyUniqueName =
        new Column(
            "HIERARCHY_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name of the hierarchy.");
    public final Column LevelName =
        new Column(
            "LEVEL_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the level.");
    public final Column LevelUniqueName =
        new Column(
            "LEVEL_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The properly escaped unique name of the level.");
    public final Column LevelGuid =
        new Column(
            "LEVEL_GUID",
            XmlaType.UUID,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Level GUID.");
    public final Column LevelCaption =
        new Column(
            "LEVEL_CAPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A label or caption associated with the hierarchy.");
    public final Column LevelNumber =
        new Column(
            "LEVEL_NUMBER",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The distance of the level from the root of the hierarchy. "
            + "Root level is zero (0).");
    public final Column LevelCardinality =
        new Column(
            "LEVEL_CARDINALITY",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The number of members in the level. This value can be an "
            + "approximation of the real cardinality.");
    public final Column LevelType =
        new Column(
            "LEVEL_TYPE",
            XmlaType.Integer,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Type of the level");
    public final Column CustomRollupSettings =
        new Column(
            "CUSTOM_ROLLUP_SETTINGS",
            XmlaType.Integer,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A bitmap that specifies the custom rollup options.");
    public final Column LevelUniqueSettings =
        new Column(
            "LEVEL_UNIQUE_SETTINGS",
            XmlaType.Integer,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A bitmap that specifies which columns contain unique values, "
            + "if the level only has members with unique names or keys.");
    public final Column LevelIsVisible =
        new Column(
            "LEVEL_IS_VISIBLE",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether the level is visible.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A human-readable description of the level. NULL if no "
            + "description exists.");
}

// End XmlaLevel.java
