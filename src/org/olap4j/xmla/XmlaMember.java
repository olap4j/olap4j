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
            Description,
            Expression,
            MemberKey,
            IsPlaceholdermember,
            IsDatamember,
            Scope,
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

    @Override
    List<Column> restrictionColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyUniqueName,
            LevelUniqueName,
            LevelNumber,
            MemberName,
            MemberUniqueName,
            MemberCaption,
            MemberType,
            TreeOp,
            CubeSource);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this member belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the schema to which this member belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "Name of the cube to which this member belongs.");
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "Unique name of the dimension to which this member belongs.");
    public final Column HierarchyUniqueName =
        new Column(
            "HIERARCHY_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "Unique name of the hierarchy. If the member belongs to more "
            + "than one hierarchy, there is one row for each hierarchy to "
            + "which it belongs.");
    public final Column LevelUniqueName =
        new Column(
            "LEVEL_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            " Unique name of the level to which the member belongs.");
    public final Column LevelNumber =
        new Column(
            "LEVEL_NUMBER",
            XmlaType.UnsignedInteger.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The distance of the member from the root of the hierarchy.");
    public final Column MemberOrdinal =
        new Column(
            "MEMBER_ORDINAL",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Deprecated. Always returns 0.");
    public final Column MemberName =
        new Column(
            "MEMBER_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "Name of the member.");
    public final Column MemberUniqueName =
        new Column(
            "MEMBER_UNIQUE_NAME",
            XmlaType.StringSometimesArray.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            " Unique name of the member.");
    public final Column MemberType =
        new Column(
            "MEMBER_TYPE",
            XmlaType.Integer.of(Enumeration.MEMBER_TYPE),
            Column.RESTRICTION,
            Column.REQUIRED,
            "Type of the member.");
    public final Column MemberGuid =
        new Column(
            "MEMBER_GUID",
            XmlaType.UUID.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Memeber GUID.");
    public final Column MemberCaption =
        new Column(
            "MEMBER_CAPTION",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "A label or caption associated with the member.");
    public final Column ChildrenCardinality =
        new Column(
            "CHILDREN_CARDINALITY",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Number of children that the member has.");
    public final Column ParentLevel =
        new Column(
            "PARENT_LEVEL",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The distance of the member's parent from the root level of "
            + "the hierarchy.");
    public final Column ParentUniqueName =
        new Column(
            "PARENT_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Unique name of the member's parent.");
    public final Column ParentCount =
        new Column(
            "PARENT_COUNT",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Number of parents that this member has.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "This column always returns a NULL value.\n"
            + "This column exists for backwards compatibility.");
    public final Column Expression =
        new Column(
            "EXPRESSION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The expression for calculations, if the member is of type "
            + "MDMEMBER_TYPE_FORMULA.");
    public final Column MemberKey =
        new Column(
            "MEMBER_KEY",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The value of the member's key column. Returns NULL if the member "
            + "has a composite key.");
    public final Column IsPlaceholdermember =
        new Column(
            "IS_PLACEHOLDERMEMBER",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether a member is a placeholder member "
            + "for an empty position in a dimension hierarchy.\n"
            + "It is valid only if the MDX Compatibility property has been set "
            + "to 1.");
    public final Column IsDatamember =
        new Column(
            "IS_DATAMEMBER",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether the member is a data member.\n"
            + "Returns True if the member is a data member.");
    public final Column Scope =
        new Column(
            "SCOPE",
            XmlaType.Integer.of(Enumeration.MEMBER_SCOPE),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The scope of the member. The member can be a session calculated "
            + "member or global calculated member. The column returns NULL for "
            + "non-calculated members.");

    /* Mondrian specified member properties. */
    public final Column Depth =
        new Column(
            "DEPTH",
            XmlaType.Integer.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "depth");

    // Only a restriction.
    public final Column TreeOp =
        new Column(
            "TREE_OP",
            XmlaType.Enumeration.of(Enumeration.TREE_OP),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "Tree Operation");

    // Only a restriction.
    public final Column CubeSource =
        new Column(
            "CUBE_SOURCE",
            XmlaType.UnsignedShort.scalar(),
            Column.Restriction.OPTIONAL.of(
                Enumeration.CUBE_TYPE, Cube.Type.CUBE),
            Column.OPTIONAL,
            null);

}

// End XmlaMember.java
