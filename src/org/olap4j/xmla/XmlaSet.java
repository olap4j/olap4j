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
 * XML for Analysis entity representing a Set.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_SETS} schema rowset.</p>
 */
public class XmlaSet extends Entity {
    public static final XmlaSet INSTANCE =
        new XmlaSet();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_SETS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            SetName,
            Scope);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String,
            null,
            true,
            true,
            null);
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String,
            null,
            true,
            true,
            null);
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String,
            null,
            true,
            false,
            null);
    public final Column SetName =
        new Column(
            "SET_NAME",
            XmlaType.String,
            null,
            true,
            false,
            null);
    public final Column SetCaption =
        new Column(
            "SET_CAPTION",
            XmlaType.String,
            null,
            true,
            true,
            null);
    public final Column Scope =
        new Column(
            "SCOPE",
            XmlaType.Integer,
            null,
            true,
            false,
            null);
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String,
            null,
            false,
            true,
            "A human-readable description of the measure.");
}

// End XmlaSet.java
