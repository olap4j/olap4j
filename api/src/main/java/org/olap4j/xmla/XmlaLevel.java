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

import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;

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
            Description,
            LevelOrderingProperty,
            LevelDbtype,
            LevelMasterUniqueName,
            LevelNameSqlColumnName,
            LevelKeySqlColumnName,
            LevelUniqueNameSqlColumnName,
            LevelAttributeHierarchyName,
            LevelKeyCardinality,
            LevelOrigin,
            Annotations);
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

    @Override
    List<Column> restrictionColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyUniqueName,
            LevelName,
            LevelUniqueName,
            LevelOrigin,
            CubeSource,
            LevelVisibility);
    }

    /** Via {@link org.olap4j.metadata.Level#getDimension()},
     * {@link org.olap4j.metadata.Dimension#getCube()},
     * {@link org.olap4j.metadata.Cube#getSchema()},
     * {@link org.olap4j.metadata.Schema#getCatalog()},
     * {@link org.olap4j.metadata.Catalog#getName()}. */
    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this level belongs.");
    /** Via {@link org.olap4j.metadata.Level#getDimension()},
     * {@link org.olap4j.metadata.Dimension#getCube()},
     * {@link org.olap4j.metadata.Cube#getSchema()},
     * {@link org.olap4j.metadata.Schema#getName()}. */
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the schema to which this level belongs.");
    /** Via {@link org.olap4j.metadata.Level#getDimension()},
     * {@link org.olap4j.metadata.Dimension#getCube()},
     * {@link org.olap4j.metadata.Cube#getName()}. */
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the cube to which this level belongs.");
    /** See {@link org.olap4j.metadata.Level#getHierarchy()},
     * {@link org.olap4j.metadata.Hierarchy#getDimension()},
     * {@link org.olap4j.metadata.Dimension#getUniqueName()}. */
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name of the dimension to which this level "
            + "belongs.");
    /** See {@link org.olap4j.metadata.Level#getHierarchy()},
     * {@link org.olap4j.metadata.Hierarchy#getUniqueName()}. */
    public final Column HierarchyUniqueName =
        new Column(
            "HIERARCHY_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name of the hierarchy. If the level belongs to more "
            + "than one hierarchy, there is one row for each hierarchy to "
            + "which it belongs. For providers that generate unique names by "
            + "qualification, each component of this name is delimited.");
    /** See {@link org.olap4j.metadata.Level#getName()}. */
    public final Column LevelName =
        new Column(
            "LEVEL_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the level.");
    /** See {@link org.olap4j.metadata.Level#getUniqueName()}. */
    public final Column LevelUniqueName =
        new Column(
            "LEVEL_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The properly escaped unique name of the level.");
    public final Column LevelGuid =
        new Column(
            "LEVEL_GUID",
            XmlaType.UUID.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Not supported.");
    /** See {@link org.olap4j.metadata.Level#getCaption()}. */
    public final Column LevelCaption =
        new Column(
            "LEVEL_CAPTION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A label or caption associated with the hierarchy. Used primarily "
            + "for display purposes. If a caption does not exist, "
            + "LEVEL_NAME is returned.");
    /** See {@link org.olap4j.metadata.Level#getDepth()}. */
    public final Column LevelNumber =
        new Column(
            "LEVEL_NUMBER",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The distance of the level from the root of the hierarchy. "
            + "Root level is zero (0).");
    /** See {@link org.olap4j.metadata.Level#getCardinality()}. */
    public final Column LevelCardinality =
        new Column(
            "LEVEL_CARDINALITY",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The number of members in the level. This value can be an "
            + "approximation of the real cardinality.");
    /** See {@link org.olap4j.metadata.Level#getLevelType()}. */
    public final Column LevelType =
        new Column(
            "LEVEL_TYPE",
            XmlaType.Integer.of(Enumeration.LEVEL_TYPE),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Type of the level");
    public final Column CustomRollupSettings =
        new Column(
            "CUSTOM_ROLLUP_SETTINGS",
            XmlaType.Integer.of(Enumeration.LEVEL_CUSTOM_ROLLUP),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A bitmap that specifies the custom rollup options.");
    public final Column LevelUniqueSettings =
        new Column(
            "LEVEL_UNIQUE_SETTINGS",
            XmlaType.Integer.of(Enumeration.DIMENSION_KEY_UNIQUENESS),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A bitmap that specifies which columns contain unique values, "
            + "if the level only has members with unique names or keys.");
    public final Column LevelIsVisible =
        new Column(
            "LEVEL_IS_VISIBLE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether the level is visible.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A human-readable description of the level. NULL if no "
            + "description exists.");
    public final Column LevelOrderingProperty =
        new Column(
            "LEVEL_ORDERING_PROPERTY",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The ID of the attribute that the level is sorted on.");
    /** See {@link org.olap4j.metadata.Level#getKeyTypes()}. */
    public final Column LevelDbtype =
        new Column(
            "LEVEL_DBTYPE",
            XmlaType.Integer.of(Enumeration.DBTYPE),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The DBTYPE enumeration of the member key column that is used for "
            + "the level attribute.\n"
            + "Null if concatenated keys are used as the member key column.");
    public final Column LevelMasterUniqueName =
        new Column(
            "LEVEL_MASTER_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Always returns NULL.");
    public final Column LevelNameSqlColumnName =
        new Column(
            "LEVEL_NAME_SQL_COLUMN_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The SQL representation of the level member names.");
    public final Column LevelKeySqlColumnName =
        new Column(
            "LEVEL_KEY_SQL_COLUMN_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The SQL representation of the level member key values.");
    public final Column LevelUniqueNameSqlColumnName =
        new Column(
            "LEVEL_UNIQUE_NAME_SQL_COLUMN_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The SQL representation of the member unique names.");
    /** See {@link org.olap4j.metadata.Level#getAttributeHierarchyName}. */
    public final Column LevelAttributeHierarchyName =
        new Column(
            "LEVEL_ATTRIBUTE_HIERARCHY_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The name of the attribute hierarchy providing the source of the "
            + "level");
    /** See {@link org.olap4j.metadata.Level#getKeyTypes()},
     * {@link java.util.List#size()}. */
    public final Column LevelKeyCardinality =
        new Column(
            "LEVEL_KEY_CARDINALITY",
            XmlaType.UnsignedShort.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The number of columns in the level key.");
    /** See {@link org.olap4j.metadata.Level#getHierarchy()},
     * {@link Hierarchy#getOrigin}. */
    public final Column LevelOrigin =
        new Column(
            "LEVEL_ORIGIN",
            XmlaType.UnsignedShort.of(Enumeration.ORIGIN),
            Column.Restriction.OPTIONAL.of(
                Enumeration.ORIGIN,
                Hierarchy.Origin.USER_DEFINED), // + MD_SYSTEM_ENABLED
            Column.OPTIONAL,
            "A bit map that defines how the level was sourced.");

    /** To access the annotations of a {@link org.olap4j.metadata.Level},
     * see how to unwrap an {@link org.olap4j.metadata.Annotated}.
     *
     * @since olap4j 2.0; mondrian extension (not in XMLA spec) */
    public final Column Annotations =
        new Column(
            "ANNOTATIONS",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A set of notes, in XML format.").extension();

    // Only a restriction.
    public final Column CubeSource =
        new Column(
            "CUBE_SOURCE",
            XmlaType.UnsignedShort.scalar(),
            Column.Restriction.OPTIONAL.of(
                Enumeration.CUBE_TYPE, Cube.Type.CUBE),
            Column.OPTIONAL,
            null);

    // Only a restriction.
    public final Column LevelVisibility =
        new Column(
            "LEVEL_VISIBILITY",
            XmlaType.UnsignedShort.scalar(),
            Column.Restriction.OPTIONAL.of(
                Enumeration.VISIBILITY, Dimension.Visibility.VISIBLE),
            Column.OPTIONAL,
            null);
}

// End XmlaLevel.java
