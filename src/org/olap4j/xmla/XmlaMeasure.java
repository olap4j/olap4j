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

import static org.olap4j.xmla.Column.Restriction;

/**
 * XML for Analysis entity representing a Measure.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_MEASURES} schema rowset.</p>
 */
public class XmlaMeasure extends Entity {
    public static final XmlaMeasure INSTANCE =
        new XmlaMeasure();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_MEASURES;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            MeasureName,
            MeasureUniqueName,
            MeasureCaption,
            MeasureGuid,
            MeasureAggregator,
            DataType,
            NumericPrecision,
            NumericScale,
            MeasureUnits,
            Description,
            Expression,
            MeasureIsVisible,
            LevelsList,
            MeasureNameSqlColumnName,
            MeasureUnqualifiedCaption,
            MeasuregroupName,
            MeasureDisplayFolder,
            DefaultFormatString);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            MeasureName);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this measure belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the schema to which this measure belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the cube to which this measure belongs.");
    public final Column MeasureName =
        new Column(
            "MEASURE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the measure.");
    public final Column MeasureUniqueName =
        new Column(
            "MEASURE_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The Unique name of the measure.");
    public final Column MeasureCaption =
        new Column(
            "MEASURE_CAPTION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A label or caption associated with the measure.");
    public final Column MeasureGuid =
        new Column(
            "MEASURE_GUID",
            XmlaType.UUID.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Measure GUID.");
    public final Column MeasureAggregator =
        new Column(
            "MEASURE_AGGREGATOR",
            XmlaType.Integer.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "How a measure was derived.");
    public final Column DataType =
        new Column(
            "DATA_TYPE",
            XmlaType.UnsignedShort.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Data type of the measure.");
    public final Column NumericPrecision =
        new Column(
            "NUMERIC_PRECISION",
            XmlaType.UnsignedShort.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The maximum precision of the property if the measure object's "
            + "data type is exact numeric. NULL for all other property types.");
    public final Column NumericScale =
        new Column(
            "NUMERIC_SCALE",
            XmlaType.Short.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The number of digits to the right of the decimal point if the "
            + "measure object's type indicator is DBTYPE_NUMERIC or "
            + "DBTYPE_DECIMAL. Otherwise, this value is NULL.");
    public final Column MeasureUnits =
        new Column(
            "MEASURE_UNITS",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "Not supported.");
    public final Column MeasureIsVisible =
        new Column(
            "MEASURE_IS_VISIBLE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A Boolean that always returns True. If the measure is not "
            + "visible, it will not be included in the schema rowset.");
    public final Column LevelsList =
        new Column(
            "LEVELS_LIST",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A string that always returns NULL. EXCEPT that SQL Server "
            + "returns non-null values!!!");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A human-readable description of the measure.");
    public final Column Expression =
        new Column(
            "EXPRESSION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "An expression for the member.");
    public final Column MeasureNameSqlColumnName =
        new Column(
            "MEASURE_NAME_SQL_COLUMN_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The name of the column in the SQL query that corresponds to the "
            + "measure's name.");
    public final Column MeasureUnqualifiedCaption =
        new Column(
            "MEASURE_UNQUALIFIED_CAPTION",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The name of the measure, not qualified with the measure group "
            + "name.");
    public final Column MeasuregroupName =
        new Column(
            "MEASUREGROUP_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The name of the measure group to which the measure belongs.");
    public final Column MeasureDisplayFolder =
        new Column(
            "MEASURE_DISPLAY_FOLDER",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The path to be used when displaying the measure in the user "
            + "interface. Folder names will be separated by a semicolon. "
            + "Nested folders are indicated by a backslash (\\).");
    public final Column DefaultFormatString =
        new Column(
            "DEFAULT_FORMAT_STRING",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The default format string for the measure.");
}

// End XmlaMeasure.java
