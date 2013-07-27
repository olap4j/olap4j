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
            PropertyName,
            PropertyCaption,
            PropertyType,
            DataType,
            PropertyContentType,
            Description);
    }

    public List<Column> sortColumns() {
        return list(); // not sorted
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
            "The name of the schema to which this property belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the cube.");
    public final Column DimensionUniqueName =
        new Column(
            "DIMENSION_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The unique name of the dimension.");
    public final Column HierarchyUniqueName =
        new Column(
            "HIERARCHY_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The unique name of the hierarchy.");
    public final Column LevelUniqueName =
        new Column(
            "LEVEL_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The unique name of the level to which this property belongs.");
    // According to MS this should not be nullable
    public final Column MemberUniqueName =
        new Column(
            "MEMBER_UNIQUE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The unique name of the member to which the property belongs.");
    public final Column PropertyName =
        new Column(
            "PROPERTY_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "Name of the property.");
    public final Column PropertyType =
        new Column(
            "PROPERTY_TYPE",
            XmlaType.Short,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "A bitmap that specifies the type of the property");
    public final Column PropertyCaption =
        new Column(
            "PROPERTY_CAPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A label or caption associated with the property, used "
            + "primarily for display purposes.");
    public final Column DataType =
        new Column(
            "DATA_TYPE",
            XmlaType.UnsignedShort,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Data type of the property.");
    public final Column PropertyContentType =
        new Column(
            "PROPERTY_CONTENT_TYPE",
            XmlaType.Short,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The type of the property.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A human-readable description of the measure.");
}

// End XmlaProperty.java
