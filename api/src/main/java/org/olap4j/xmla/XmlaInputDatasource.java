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
 * XML for Analysis entity representing a data source defined within the
 * database,
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_INPUT_DATASOURCES} schema
 * rowset.</p>
 */
public class XmlaInputDatasource extends Entity {
    public static final XmlaInputDatasource INSTANCE =
        new XmlaInputDatasource();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_INPUT_DATASOURCES;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            DatasourceName,
            DatasourceType,
            CreatedOn,
            LastSchemaUpdate,
            Description,
            Timeout);
    }

    public List<Column> sortColumns() {
        return list(); // not sorted
    }

    @Override
    List<Column> restrictionColumns() {
        return list(
            CatalogName,
            SchemaName,
            DatasourceName,
            DatasourceType);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the catalog to which this cube belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "The name of the schema to which this cube belongs.");
    public final Column DatasourceName =
        new Column(
            "DATASOURCE_NAME",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "Name of the data source object.");
    public final Column DatasourceType =
        new Column(
            "DATASOURCE_TYPE",
            XmlaType.String.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The type of the data source. Valid values include: Relational, "
            + "Olap");
    public final Column CreatedOn =
        new Column(
            "CREATED_ON",
            XmlaType.DateTime.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The date that the data source was created.");
    public final Column LastSchemaUpdate =
        new Column(
            "LAST_SCHEMA_UPDATE",
            XmlaType.DateTime.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The date and time that the data source was last modified.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.DateTime.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A user-friendly description of the data source.");
    public final Column Timeout =
        new Column(
            "TIMEOUT",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The timeout of the data source.");
}

// End XmlaInputDatasource.java
