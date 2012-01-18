/*
// $Id$
//
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
package org.olap4j.driver.xmla;

import org.olap4j.OlapDatabaseMetaData;
import org.olap4j.OlapException;
import org.olap4j.impl.Named;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of {@link org.olap4j.metadata.Catalog}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since May 23, 2007
 */
class XmlaOlap4jCatalog implements Catalog, Named {
    final XmlaOlap4jDatabaseMetaData olap4jDatabaseMetaData;
    private final String name;
    final DeferredNamedListImpl<XmlaOlap4jSchema> schemas;
    private final XmlaOlap4jDatabase database;
	private List<String> roles;

    XmlaOlap4jCatalog(
        XmlaOlap4jDatabaseMetaData olap4jDatabaseMetaData,
        XmlaOlap4jDatabase database,
        String name,
        List<String> roles)
    {
        this.database = database;
        assert olap4jDatabaseMetaData != null;
        assert name != null;
        this.olap4jDatabaseMetaData = olap4jDatabaseMetaData;
        this.name = name;
        this.roles = roles;

        // Some servers don't support MDSCHEMA_MDSCHEMATA, so we will
        // override the list class so it tries it first, and falls
        // back to the MDSCHEMA_CUBES trick, where ask for the cubes,
        // restricting results on the catalog, and while
        // iterating on the cubes, take the schema name from this recordset.
        //
        // Many servers (SSAS for example) won't support the schema name column
        // in the returned rowset. This has to be taken into account as well.
        this.schemas =
            new DeferredNamedListImpl<XmlaOlap4jSchema>(
                XmlaOlap4jConnection.MetadataRequest.DBSCHEMA_SCHEMATA,
                new XmlaOlap4jConnection.Context(
                    olap4jDatabaseMetaData.olap4jConnection,
                    olap4jDatabaseMetaData,
                    this,
                    null, null, null, null, null),
                new XmlaOlap4jConnection.CatalogSchemaHandler(this.name),
                null)
            {
                private boolean useSchemata = false;

                @Override
                protected void populateList(
                    NamedList<XmlaOlap4jSchema> list)
                    throws OlapException
                {
                    try {
                        /*
                         * Some OLAP servers don't support DBSCHEMA_SCHEMATA
                         * so we fork the behavior here according to the
                         * database product name.
                         */
                        if (XmlaOlap4jCatalog.this.olap4jDatabaseMetaData
                            .getDatabaseProductName().contains("Mondrian"))
                        {
                            this.useSchemata = true;
                        }
                    } catch (SQLException e1) {
                        throw new OlapException(
                            "Failed to obtain the database product name.",
                            e1);
                    }
                    try {
                        if (this.useSchemata) {
                            super.populateList(list);
                            return;
                        }
                    } catch (OlapException e) {
                        // no op. we know how to fallback.
                        useSchemata = false;
                    }
                    // Fallback to MDSCHEMA_CUBES trick
                    populateInternal(list);
                }

                private void populateInternal(
                    NamedList<XmlaOlap4jSchema> list)
                    throws OlapException
                {
                    XmlaOlap4jConnection conn =
                        XmlaOlap4jCatalog.this
                        .olap4jDatabaseMetaData.olap4jConnection;
                    conn.populateList(
                        list,
                        new XmlaOlap4jConnection.Context(
                            conn,
                            conn.olap4jDatabaseMetaData,
                            XmlaOlap4jCatalog.this,
                            null, null, null, null, null),
                            XmlaOlap4jConnection.MetadataRequest
                                .MDSCHEMA_CUBES,
                            new XmlaOlap4jConnection.CatalogSchemaHandler(
                                XmlaOlap4jCatalog.this.name),
                            new Object[0]);
                }
            };
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof XmlaOlap4jCatalog) {
            XmlaOlap4jCatalog that = (XmlaOlap4jCatalog) obj;
            return this.name.equals(that.name);
        }
        return false;
    }

    public NamedList<Schema> getSchemas() throws OlapException {
        return Olap4jUtil.cast(schemas);
    }

    public String getName() {
        return name;
    }
    
    public List<String> getAvailableRoles() {
    	return roles;
    }

    public OlapDatabaseMetaData getMetaData() {
        return olap4jDatabaseMetaData;
    }

    public XmlaOlap4jDatabase getDatabase() {
        return database;
    }
}

// End XmlaOlap4jCatalog.java


