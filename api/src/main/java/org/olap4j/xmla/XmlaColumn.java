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
 * XML for Analysis entity representing a Column.
 *
 * <p>Corresponds to the XML/A {@code DBSCHEMA_COLUMNS} schema rowset.</p>
 */
public class XmlaColumn extends Entity {
    public static final XmlaColumn INSTANCE =
        new XmlaColumn();

    public RowsetDefinition def() {
        return RowsetDefinition.DBSCHEMA_COLUMNS;
    }

    public List<Column> columns() {
        return list(
            TableCatalog,
            TableSchema,
            TableName,
            ColumnName,
            OrdinalPosition,
            ColumnHasDefault,
            ColumnFlags,
            IsNullable,
            DataType,
            CharacterMaximumLength,
            CharacterOctetLength,
            NumericPrecision,
            NumericScale);
    }

    public List<Column> sortColumns() {
        return list(
            TableCatalog,
            TableSchema,
            TableName);
    }

    public final Column TableCatalog =
        new Column(
            "TABLE_CATALOG",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the Database.");
    public final Column TableSchema =
        new Column(
            "TABLE_SCHEMA",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            null);
    public final Column TableName =
        new Column(
            "TABLE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the cube.");
    public final Column ColumnName =
        new Column(
            "COLUMN_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the attribute hierarchy or measure.");
    public final Column OrdinalPosition =
        new Column(
            "ORDINAL_POSITION",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The position of the column, beginning with 1.");
    public final Column ColumnHasDefault =
        new Column(
            "COLUMN_HAS_DEFAULT",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Not supported.");

    //  A bitmask indicating the information stored in
    //      DBCOLUMNFLAGS in OLE DB.
    //  1 = Bookmark
    //  2 = Fixed length
    //  4 = Nullable
    //  8 = Row versioning
    //  16 = Updateable column
    //
    // And, of course, MS SQL Server sometimes has the value of 80!!
    public final Column ColumnFlags =
        new Column(
            "COLUMN_FLAGS",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A DBCOLUMNFLAGS bitmask indicating column properties.");
    public final Column IsNullable =
        new Column(
            "IS_NULLABLE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Always returns false.");
    public final Column DataType =
        new Column(
            "DATA_TYPE",
            XmlaType.UnsignedShort.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The data type of the column. Returns a string for dimension "
            + "columns and a variant for measures.");
    public final Column CharacterMaximumLength =
        new Column(
            "CHARACTER_MAXIMUM_LENGTH",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The maximum possible length of a value within the column.");
    public final Column CharacterOctetLength =
        new Column(
            "CHARACTER_OCTET_LENGTH",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The maximum possible length of a value within the column, in "
            + "bytes, for character or binary columns.");
    public final Column NumericPrecision =
        new Column(
            "NUMERIC_PRECISION",
            XmlaType.UnsignedShort.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The maximum precision of the column for numeric data types "
            + "other than DBTYPE_VARNUMERIC.");
    public final Column NumericScale =
        new Column(
            "NUMERIC_SCALE",
            XmlaType.Short.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The number of digits to the right of the decimal point for "
            + "DBTYPE_DECIMAL, DBTYPE_NUMERIC, DBTYPE_VARNUMERIC. "
            + "Otherwise, this is NULL.");
}

// End XmlaColumn.java
