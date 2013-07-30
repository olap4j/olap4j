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

import java.util.List;

import static org.olap4j.metadata.XmlaConstants.*;

/**
 * XML for Analysis entity representing a Hierarchy.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_HIERARCHIES} schema rowset.</p>
 */
public class XmlaHierarchy extends Entity {
    public static final XmlaHierarchy INSTANCE =
        new XmlaHierarchy();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_HIERARCHIES;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyName,
            HierarchyUniqueName,
            HierarchyGuid,
            HierarchyCaption,
            DimensionType,
            HierarchyCardinality,
            DefaultMember,
            AllMember,
            Description,
            Structure,
            IsVirtual,
            IsReadWrite,
            DimensionUniqueSettings,
            DimensionIsVisible,
            HierarchyIsVisible,
            HierarchyOrdinal,
            DimensionIsShared,
            ParentChild,
            Levels);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyName);
    }

    @Override
    List<Column> restrictionColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyName,
            HierarchyUniqueName,
            HierarchyOrigin,
            CubeSource,
            HierarchyVisibility);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this hierarchy belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "Not supported");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the cube to which this hierarchy belongs.");
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name of the dimension to which this hierarchy "
            + "belongs.");
    public final Column HierarchyName =
        new Column(
            "HIERARCHY_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the hierarchy. Blank if there is only a single "
            + "hierarchy in the dimension.");
    public final Column HierarchyUniqueName =
        new Column(
            "HIERARCHY_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name of the hierarchy.");
    public final Column HierarchyGuid =
        new Column(
            "HIERARCHY_GUID",
            XmlaType.UUID.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Not supported.");
    public final Column HierarchyCaption =
        new Column(
            "HIERARCHY_CAPTION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A label or a caption associated with the hierarchy.");
    public final Column DimensionType =
        new Column(
            "DIMENSION_TYPE",
            XmlaType.Short.of(Enumeration.DIMENSION_TYPE),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The type of the dimension.");
    public final Column HierarchyCardinality =
        new Column(
            "HIERARCHY_CARDINALITY",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The number of members in the hierarchy.");
    public final Column DefaultMember =
        new Column(
            "DEFAULT_MEMBER",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The default member for this hierarchy. This is a unique name. "
            + "Every hierarchy must have a default member.");
    public final Column AllMember =
        new Column(
            "ALL_MEMBER",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The member at the highest level of the rollup.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A human-readable description of the hierarchy. NULL if no "
            + "description exists.");
    public final Column Structure =
        new Column(
            "STRUCTURE",
            XmlaType.Short.of(Enumeration.STRUCTURE),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The structure of the hierarchy.");
    public final Column IsVirtual =
        new Column(
            "IS_VIRTUAL",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Always returns false.");
    public final Column IsReadWrite =
        new Column(
            "IS_READWRITE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether the Write Back to dimension "
            + "column is enabled.");
    public final Column DimensionUniqueSettings =
        new Column(
            "DIMENSION_UNIQUE_SETTINGS",
            XmlaType.Integer.of(Enumeration.DIMENSION_KEY_UNIQUENESS),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Always returns "
            + sameXmlaNameCode(
                "MDDIMENSIONS_MEMBER_KEY_UNIQUE (1)",
                Dimension.KeyUniqueness.MEMBER_KEY_UNIQUE)
             + ".");
    public final Column DimensionIsVisible =
        new Column(
            "DIMENSION_IS_VISIBLE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether the parent dimension is visible.");
    public final Column HierarchyIsVisible =
        new Column(
            "HIERARCHY_IS_VISIBLE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether the hierarchy is visible.");
    public final Column HierarchyOrdinal =
        new Column(
            "HIERARCHY_ORDINAL",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The ordinal number of the hierarchy across all hierarchies of "
            + "the cube.");
    public final Column DimensionIsShared =
        new Column(
            "DIMENSION_IS_SHARED",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Always returns true.");
    public final Column DimensionMasterUniqueName =
        new Column(
            "DIMENSION_MASTER_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Always returns null.");
    public final Column HierarchyOrigin =
        new Column(
            "HIERARCHY_ORIGIN",
            XmlaType.UnsignedShort.of(Enumeration.ORIGIN),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A bit mak that determines the source of the hierarchy.");
    public final Column HierarchyDisplayFolder =
        new Column(
            "HIERARCHY_DISPLAY_FOLDER",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The path to be used when displaying the hierarchy in the user "
            + "interface. Folder names will be separated by a semicolon "
            + "(;). Nested folders are indicated by a backslash (\\).");
    public final Column InstanceSelection =
        new Column(
            "INSTANCE_SELECTION",
            XmlaType.UnsignedShort.of(Enumeration.INSTANCE_SELECTION),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A hint to the client application on how to show the hierarchy.");
    public final Column GroupingBehavior =
        new Column(
            "GROUPING_BEHAVIOR",
            XmlaType.Short.of(Enumeration.GROUPING_BEHAVIOR),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "An enumeration that specifies the expected grouping behavior of "
            + "clients for this hierarchy.");
    public final Column StructureType =
        new Column(
            "STRUCTURE_TYPE",
            XmlaType.String.of(Enumeration.STRUCTURE_TYPE),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Indicates the type of hierarchy.");

    // Mondrian extension. Not in XMLA spec.
    public final Column Levels =
        new Column(
            "LEVELS",
            XmlaType.Rowset.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Levels in this hierarchy.");

    // NOTE: This is non-standard, where did it come from?
    public final Column ParentChild =
        new Column(
            "PARENT_CHILD",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Is hierarchy a parent.");

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
    public final Column HierarchyVisibility =
        new Column(
            "HIERARCHY_VISIBILITY",
            XmlaType.UnsignedShort.scalar(),
            Column.Restriction.OPTIONAL.of(
                Enumeration.VISIBILITY, Dimension.Visibility.VISIBLE),
            Column.OPTIONAL,
            null);
}

// End XmlaHierarchy.java
