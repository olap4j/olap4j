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

import static org.olap4j.xmla.Column.Restriction;

/**
 * XML for Analysis entity representing a Property.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_PROPERTIES} schema rowset.</p>
 */
public class XmlaProperty extends Entity {
    public static final XmlaProperty INSTANCE =
        new XmlaProperty();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_PROPERTIES;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            DimensionUniqueName,
            HierarchyUniqueName,
            LevelUniqueName,
            MemberUniqueName,
            PropertyType,
            PropertyName,
            PropertyCaption,
            DataType,
            CharacterMaximumLength,
            CharacterOctetLength,
            NumericPrecision,
            NumericScale,
            Description,
            PropertyContentType,
            SqlColumnName,
            Language,
            PropertyOrigin,
            PropertyAttributeHierarchyName,
            PropertyCardinality,
            MimeType,
            PropertyIsVisible,
            Annotations);
    }

    public List<Column> sortColumns() {
        return list(); // not sorted
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
            MemberUniqueName,
            PropertyName,
            PropertyType,
            PropertyContentType,
            PropertyOrigin,
            CubeSource,
            PropertyVisibility);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the database.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the schema to which this property belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the cube.");
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The unique name of the dimension.");
    public final Column HierarchyUniqueName =
        new Column(
            "HIERARCHY_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The unique name of the hierarchy.");
    public final Column LevelUniqueName =
        new Column(
            "LEVEL_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The unique name of the level to which this property belongs.");
    // According to MS this should not be nullable
    public final Column MemberUniqueName =
        new Column(
            "MEMBER_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The unique name of the member to which the property belongs.");
    public final Column PropertyName =
        new Column(
            "PROPERTY_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "Name of the property.");
    public final Column PropertyType =
        new Column(
            "PROPERTY_TYPE",
            XmlaType.Short.of(Enumeration.PROPERTY_TYPE),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "A bitmap that specifies the type of the property");
    public final Column PropertyCaption =
        new Column(
            "PROPERTY_CAPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.REQUIRED,
            "A label or caption associated with the property, used "
            + "primarily for display purposes.");
    public final Column DataType =
        new Column(
            "DATA_TYPE",
            XmlaType.UnsignedShort.scalar(),
            Restriction.NO,
            Column.REQUIRED,
            "The data type of the property.");
    public final Column CharacterMaximumLength =
        new Column(
            "CHARACTER_MAXIMUM_LENGTH",
            XmlaType.UnsignedLong.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The maximum possible length of the property, if it is a "
            + "character, binary, or bit type.\n"
            + "Zero indicates there is no defined maximum length.\n"
            + "Returns NULL for all other data types.");
    public final Column CharacterOctetLength =
        new Column(
            "CHARACTER_OCTET_LENGTH",
            XmlaType.UnsignedLong.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The maximum possible length (in bytes) of the property, if it is a "
            + "character or binary type.\n"
            + "Zero indicates there is no defined maximum length.\n"
            + "Returns NULL for all other data types.");
    public final Column NumericPrecision =
        new Column(
            "NUMERIC_PRECISION",
            XmlaType.UnsignedShort.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The maximum precision of the property, if it is a numeric data "
            + "type.\n"
            + "Returns NULL for all other data types.");
    public final Column NumericScale =
        new Column(
            "NUMERIC_SCALE",
            XmlaType.UnsignedShort.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The number of digits to the right of the decimal point, if it is "
            + "a DBTYPE_NUMERIC or DBTYPE_DECIMAL type.\n"
            + "Returns NULL for all other data types.");
    public final Column PropertyContentType =
        new Column(
            "PROPERTY_CONTENT_TYPE",
            XmlaType.Short.of(Enumeration.PROPERTY_CONTENT_TYPE),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The type of the property.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "A human-readable description of the measure.");
    public final Column SqlColumnName =
        new Column(
            "SQL_COLUMN_NAME",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The name of the property used in SQL queries from the cube "
            + "dimension or database dimension.");
    public final Column Language =
        new Column(
            "LANGUAGE",
            XmlaType.UnsignedShort.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The translation expressed as an LCID. Only valid for property "
            + "translations.");
    public final Column PropertyOrigin =
        new Column(
            "PROPERTY_ORIGIN",
            XmlaType.UnsignedShort.of(Enumeration.ORIGIN),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "Identifies the type of hierarchy that the property applies to.");
    public final Column PropertyAttributeHierarchyName =
        new Column(
            "PROPERTY_ATTRIBUTE_HIERARCHY_NAME",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The name of the attribute hierarchy sourcing this property.");
    public final Column PropertyCardinality =
        new Column(
            "PROPERTY_CARDINALITY",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The cardinality of the property. Possible values include the"
            + "following strings: ONE, MANY");
    public final Column MimeType =
        new Column(
            "MIME_TYPE",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The mime type for binary large objects (BLOBs).");
    public final Column PropertyIsVisible =
        new Column(
            "PROPERTY_IS_VISIBLE",
            XmlaType.Boolean.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "A Boolean that indicates whether the property is visible.\n"
            + "TRUE if the property is visible; otherwise, FALSE.");

    /** See {@link org.olap4j.metadata.Property#getAnnotations()}.
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
            Restriction.OPTIONAL.of(
                Enumeration.CUBE_TYPE, Cube.Type.CUBE),
            Column.OPTIONAL,
            null);

    // Only a restriction.
    public final Column PropertyVisibility =
        new Column(
            "PROPERTY_VISIBILITY",
            XmlaType.UnsignedShort.scalar(),
            Restriction.OPTIONAL.of(
                Enumeration.VISIBILITY, Dimension.Visibility.VISIBLE),
            Column.OPTIONAL,
            null);
}

// End XmlaProperty.java
