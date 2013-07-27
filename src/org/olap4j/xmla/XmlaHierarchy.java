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

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this hierarchy belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "Not supported");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the cube to which this hierarchy belongs.");
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name of the dimension to which this hierarchy "
            + "belongs.");
    public final Column HierarchyName =
        new Column(
            "HIERARCHY_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the hierarchy. Blank if there is only a single "
            + "hierarchy in the dimension.");
    public final Column HierarchyUniqueName =
        new Column(
            "HIERARCHY_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The unique name of the hierarchy.");
    public final Column HierarchyGuid =
        new Column(
            "HIERARCHY_GUID",
            XmlaType.UUID,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Hierarchy GUID.");
    public final Column HierarchyCaption =
        new Column(
            "HIERARCHY_CAPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A label or a caption associated with the hierarchy.");
    public final Column DimensionType =
        new Column(
            "DIMENSION_TYPE",
            XmlaType.Short,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The type of the dimension.");
    public final Column HierarchyCardinality =
        new Column(
            "HIERARCHY_CARDINALITY",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The number of members in the hierarchy.");
    public final Column DefaultMember =
        new Column(
            "DEFAULT_MEMBER",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The default member for this hierarchy.");
    public final Column AllMember =
        new Column(
            "ALL_MEMBER",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The member at the highest level of rollup in the hierarchy.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A human-readable description of the hierarchy. NULL if no "
            + "description exists.");
    public final Column Structure =
        new Column(
            "STRUCTURE",
            XmlaType.Short,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The structure of the hierarchy.");
    public final Column IsVirtual =
        new Column(
            "IS_VIRTUAL",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Always returns False.");
    public final Column IsReadWrite =
        new Column(
            "IS_READWRITE",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether the Write Back to dimension "
            + "column is enabled.");
    public final Column DimensionUniqueSettings =
        new Column(
            "DIMENSION_UNIQUE_SETTINGS",
            XmlaType.Integer,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Always returns MDDIMENSIONS_MEMBER_KEY_UNIQUE (1).");
    public final Column DimensionIsVisible =
        new Column(
            "DIMENSION_IS_VISIBLE",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether the parent dimension is visible.");
    public final Column HierarchyIsVisible =
        new Column(
            "HIERARCHY_IS_VISIBLE",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that indicates whether the hierarchy is visible.");
    public final Column HierarchyOrdinal =
        new Column(
            "HIERARCHY_ORDINAL",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The ordinal number of the hierarchy across all hierarchies of "
            + "the cube.");
    public final Column DimensionIsShared =
        new Column(
            "DIMENSION_IS_SHARED",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Always returns true.");
    public final Column Levels =
        new Column(
            "LEVELS",
            XmlaType.Rowset,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Levels in this hierarchy.");

    // NOTE: This is non-standard, where did it come from?
    public final Column ParentChild =
        new Column(
            "PARENT_CHILD",
            XmlaType.Boolean,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Is hierarchy a parent.");
}

// End XmlaHierarchy.java
