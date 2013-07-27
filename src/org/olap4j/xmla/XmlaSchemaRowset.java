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

import java.util.Arrays;
import java.util.List;

/**
 * XML for Analysis entity representing a Schema Rowset.
 *
 * <p>Corresponds to the XML/A {@code DISCOVER_SCHEMA_ROWSETS} schema
 * rowset.</p>
 */
public class XmlaSchemaRowset extends Entity {
    public static XmlaSchemaRowset INSTANCE =
        new XmlaSchemaRowset();

    public RowsetDefinition def() {
        return RowsetDefinition.DISCOVER_SCHEMA_ROWSETS;
    }

    List<Column> columns() {
        return list(
            SchemaName, SchemaGuid, Restrictions, Description);
    }

    List<Column> sortColumns() {
        return list(); // not sorted
    }

    public final Column SchemaName =
        new Column(
            "SchemaName",
            XmlaType.StringArray,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the schema/request. This returns the values in "
            + "the RequestTypes enumeration, plus any additional types "
            + "supported by the provider. The provider defines rowset "
            + "structures for the additional types");
    public final Column SchemaGuid =
        new Column(
            "SchemaGuid",
            XmlaType.UUID,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The GUID of the schema.");
    public final Column Restrictions =
        new Column(
            "Restrictions",
            XmlaType.Array,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "An array of the restrictions suppoted by provider. An example "
            + "follows this table.");
    public final Column Description =
        new Column(
            "Description",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A localizable description of the schema");
}

// End XmlaSchemaRowset.java
