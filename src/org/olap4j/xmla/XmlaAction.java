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
 * XML for Analysis entity representing an Action.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_ACTIONS} schema rowset.</p>
 */
public class XmlaAction extends Entity {
    public static final XmlaAction INSTANCE =
        new XmlaAction();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_ACTIONS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            ActionName,
            Coordinate,
            CoordinateType);
    }

    public List<Column> sortColumns() {
        // Spec says sort on CATALOG_NAME, SCHEMA_NAME, CUBE_NAME,
        // ACTION_NAME.
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            ActionName);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this action belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the schema to which this action belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the cube to which this action belongs.");
    public final Column ActionName =
        new Column(
            "ACTION_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the action.");
    public final Column Coordinate =
        new Column(
            "COORDINATE",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            null);
    public final Column CoordinateType =
        new Column(
            "COORDINATE_TYPE",
            XmlaType.Integer,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            null);

    // TODO: optional columns:
    // ACTION_TYPE
    // INVOCATION
    // CUBE_SOURCE
}

// End XmlaAction.java
