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
 * XML for Analysis entity representing a Table.
 *
 * <p>Corresponds to the XML/A {@code DBSCHEMA_TABLES} schema rowset.</p>
 */
public class XmlaTable extends Entity {
    public static final XmlaTable INSTANCE =
        new XmlaTable();

    public RowsetDefinition def() {
        return RowsetDefinition.DBSCHEMA_TABLES;
    }

    public List<Column> columns() {
        return list(
            TableCatalog,
            TableSchema,
            TableName,
            TableType,
            TableGuid,
            Description,
            TablePropId,
            DateCreated,
            DateModified);
    }

    public List<Column> sortColumns() {
        return list(
            TableType,
            TableCatalog,
            TableSchema,
            TableName);
    }

    public final Column TableCatalog =
        new Column(
            "TABLE_CATALOG",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the catalog to which this object belongs.");
    public final Column TableSchema =
        new Column(
            "TABLE_SCHEMA",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the cube to which this object belongs.");
    public final Column TableName =
        new Column(
            "TABLE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the object, if TABLE_TYPE is TABLE.");
    public final Column TableType =
        new Column(
            "TABLE_TYPE",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The type of the table. TABLE indicates the object is a "
            + "measure group. SYSTEM TABLE indicates the object is a "
            + "dimension.");
    public final Column TableGuid =
        new Column(
            "TABLE_GUID",
            XmlaType.UUID,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Not supported.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A human-readable description of the object.");
    public final Column TablePropId =
        new Column(
            "TABLE_PROPID",
            XmlaType.UnsignedInteger,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Not supported.");
    public final Column DateCreated =
        new Column(
            "DATE_CREATED",
            XmlaType.DateTime,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Not supported.");
    public final Column DateModified =
        new Column(
            "DATE_MODIFIED",
            XmlaType.DateTime,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The date the object was last modified.");
/*
    public final Column TableOlapType =
        new Column(
            "TABLE_OLAP_TYPE",
            Type.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The OLAP type of the object.  MEASURE_GROUP indicates the "
            + "object is a measure group.  CUBE_DIMENSION indicated the "
            + "object is a dimension.");
*/
}

// End XmlaTable.java
