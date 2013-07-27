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
 * XML for Analysis entity representing a Catalog.
 *
 * <p>Corresponds to the XML/A {@code DBSCHEMA_CATALOGS} schema rowset.</p>
 */
public class XmlaCatalog extends Entity {
    public static final XmlaCatalog INSTANCE =
        new XmlaCatalog();

    public RowsetDefinition def() {
        return RowsetDefinition.DBSCHEMA_CATALOGS;
    }

    List<Column> columns() {
        return list(
            CatalogName,
            Description,
            Roles,
            DateModified);
    }

    List<Column> sortColumns() {
        return list(CatalogName);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "Catalog name. Cannot be NULL.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "Human-readable description of the catalog.");
    public final Column Roles =
        new Column(
            "ROLES",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "A comma delimited list of roles to which the current user "
            + "belongs. An asterisk (*) is included as a role if the "
            + "current user is a server or database administrator. "
            + "Username is appended to ROLES if one of the roles uses "
            + "dynamic security.");
    public final Column DateModified =
        new Column(
            "DATE_MODIFIED",
            XmlaType.DateTime,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The date that the catalog was last modified.");
}

// End XmlaCatalog.java
