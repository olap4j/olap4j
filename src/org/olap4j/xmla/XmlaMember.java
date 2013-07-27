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
 * XML for Analysis entity representing a Member.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_MEMBERS} schema rowset.</p>
 */
public class XmlaMember extends Entity {
    public static final XmlaMember INSTANCE =
        new XmlaMember();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_MEMBERS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyUniqueName,
            LevelUniqueName,
            LevelNumber,
            MemberOrdinal,
            MemberName,
            MemberUniqueName,
            MemberType,
            MemberGuid,
            MemberCaption,
            ChildrenCardinality,
            ParentLevel,
            ParentUniqueName,
            ParentCount,
            TreeOp_,
            Depth);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyUniqueName,
            LevelUniqueName,
            LevelNumber,
            MemberOrdinal);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this member belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the schema to which this member belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "Name of the cube to which this member belongs.");
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "Unique name of the dimension to which this member belongs.");
    public final Column HierarchyUniqueName =
        new Column(
            "HIERARCHY_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "Unique name of the hierarchy. If the member belongs to more "
            + "than one hierarchy, there is one row for each hierarchy to "
            + "which it belongs.");
    public final Column LevelUniqueName =
        new Column(
            "LEVEL_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            " Unique name of the level to which the member belongs.");
    public final Column LevelNumber =
        new Column(
            "LEVEL_NUMBER",
            XmlaType.UnsignedInteger,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The distance of the member from the root of the hierarchy.");
    public final Column MemberOrdinal =
        new Column(
            "MEMBER_ORDINAL",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Ordinal number of the member. Sort rank of the member when "
            + "members of this dimension are sorted in their natural sort "
            + "order. If providers do not have the concept of natural "
            + "ordering, this should be the rank when sorted by "
            + "MEMBER_NAME.");
    public final Column MemberName =
        new Column(
            "MEMBER_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "Name of the member.");
    public final Column MemberUniqueName =
        new Column(
            "MEMBER_UNIQUE_NAME",
            XmlaType.StringSometimesArray,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            " Unique name of the member.");
    public final Column MemberType =
        new Column(
            "MEMBER_TYPE",
            XmlaType.Integer,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "Type of the member.");
    public final Column MemberGuid =
        new Column(
            "MEMBER_GUID",
            XmlaType.UUID,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Memeber GUID.");
    public final Column MemberCaption =
        new Column(
            "MEMBER_CAPTION",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "A label or caption associated with the member.");
    public final Column ChildrenCardinality =
        new Column(
            "CHILDREN_CARDINALITY",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Number of children that the member has.");
    public final Column ParentLevel =
        new Column(
            "PARENT_LEVEL",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The distance of the member's parent from the root level of "
            + "the hierarchy.");
    public final Column ParentUniqueName =
        new Column(
            "PARENT_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Unique name of the member's parent.");
    public final Column ParentCount =
        new Column(
            "PARENT_COUNT",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Number of parents that this member has.");
    public final Column TreeOp_ =
        new Column(
            "TREE_OP",
            XmlaType.Enumeration,
            Enumeration.TREE_OP,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "Tree Operation");
    /* Mondrian specified member properties. */
    public final Column Depth =
        new Column(
            "DEPTH",
            XmlaType.Integer,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "depth");
}

// End XmlaMember.java
