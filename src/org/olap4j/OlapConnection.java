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
package org.olap4j;

import org.olap4j.mdx.parser.MdxParserFactory;
import org.olap4j.metadata.*;

import java.sql.*;
import java.util.List;
import java.util.Locale;

/**
 * Connection to an OLAP server.
 *
 * <p>OlapConnection is a subclass of {@link Connection}. It can be pooled
 * by a connection pooling framework or obtained directly via the Java
 * standard {@link DriverManager}. The JDBC URL prefix of olap connections
 * is dependent of the driver implementation. Such implementations are,
 * among others possible:
 *
 * <ul><li>Olap4j's XML/A driver</li><li>Mondrian</li></ul>
 *
 * <p>Olap connections have a different metadata hierarchy than regular
 * JDBC. The connection's metadata is available using
 * {@link OlapConnection#getMetaData()}, and returns a specialized subclass
 * of {@link DatabaseMetaData}. The objects at the root of the hierarchy
 * are {@link Database} class objects.
 *
 * <p>A connection needs be bound to a database, a catalog and a schema.
 * Implementations are expected to automatically discover these if no
 * driver specific parameters indicated which ones to use.
 *
 * @author jhyde
 * @since Aug 22, 2006
 */
public interface OlapConnection extends Connection, OlapWrapper {

    // overrides Connection, with refined return type and throws list
    /**
     * {@inheritDoc}
     * @throws OlapException if database error occurs
     */
    OlapDatabaseMetaData getMetaData() throws OlapException;

    /**
     * Creates a prepared OLAP Statement.
     *
     * <p>This method is the equivalent, for OLAP, of the
     * {@link Connection#prepareStatement(String)} JDBC method.</p>
     *
     * @param mdx MDX query string
     * @return prepared statement
     * @throws OlapException if database error occurs
     */
    PreparedOlapStatement prepareOlapStatement(String mdx) throws OlapException;

    /**
     * Returns the factory used to create MDX parsers in this connection.
     *
     * @return MDX parser factory
     */
    MdxParserFactory getParserFactory();

    // overrides Connection, with refined return type and throws list
    /**
     * {@inheritDoc}
     * @throws OlapException if database error occurs
     */
    OlapStatement createStatement() throws OlapException;

    /**
     * Returns the database name currently active for this connection. If no
     * database name was specified either through the JDBC URL or through
     * {@link OlapConnection#setDatabase(String)}, the driver will select the
     * first one available.
     *
     * @return The name of the database currently active for this connection.
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No databases exist on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             </ul>
     */
    String getDatabase() throws OlapException;

    /**
     * Sets the name of the database that will be used for this connection.
     * Overrides the value passed, if any, through the JDBC URL.
     *
     * @param databaseName The name of the database to use.
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             </ul>
     */
    void setDatabase(String databaseName) throws OlapException;

    /**
     * Returns the current active {@link org.olap4j.metadata.Database} of this
     * connection.
     *
     * <p>If the user has not specified a database name to use for this
     * connection, the driver will auto-select the first Database available.
     *
     * @see #setDatabase(String)
     * @see #getOlapDatabases()
     * @return The currently active Database, or null of none are currently
     *         selected.
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No databases exist on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             </ul>
     */
    Database getOlapDatabase() throws OlapException;

    /**
     * Returns a list of {@link org.olap4j.metadata.Database} objects which
     * belong to this connection's OLAP server.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @return List of Database objects in this connection's OLAP server
     * @throws OlapException if a database access error occurs
     */
    NamedList<Database> getOlapDatabases() throws OlapException;

    /**
     * Returns the {@link Catalog} name which is currently active for this
     * connection.
     *
     * <p>
     * If the user has not specified a database name to use for this
     * connection, the driver will automatically select the first one
     * available. If the user has not specified a catalog name to use,
     * the driver will also use the first one available on the server.
     *
     * @return The name of the catalog which is active for this connection.
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No database name was specified and no databases exist
     *             on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             <li>No catalog names were specified and no catalogs
     *             exist on the server.</li>
     *             <li>The user specified a catalog name which does not exist
     *             on the server.</li>
     *             </ul>
     */
    String getCatalog() throws OlapException;

    /**
     * Sets the name of the catalog that will be used for this connection.
     * Overrides the value passed, if any, through the JDBC URL.
     *
     * @param catalogName The name of the catalog to use for this connection.
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No database name was specified and no databases
     *             exist on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             <li>The user specified a catalog name which does not exist
     *             on the server.</li>
     *             </ul>
     */
    void setCatalog(String catalogName) throws OlapException;

    /**
     * Returns the current active {@link org.olap4j.metadata.Catalog}
     * of this connection.
     *
     * <p>If the user has not selected a Database and Catalog to use for
     * this connection, the driver will auto-select the first
     * Database and Catalog available on the server.
     *
     * <p>Any auto-discovery performed by implementations must take into
     * account the specified database name and catalog name, if any.
     *
     * @return The currently active catalog, or null of none are
     * currently selected.
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No database name was specified and no databases
     *             exist on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             <li>No catalog name was specified and no catalogs
     *             exist on the server.</li>
     *             <li>The user specified a catalog name which does not exist
     *             on the server.</li>
     *             </ul>
     */
    Catalog getOlapCatalog() throws OlapException;

    /**
     * Returns a list of {@link org.olap4j.metadata.Catalog} objects which
     * belong to this connection's OLAP server.
     *
     * <p>If the user has not selected a Database to use for
     * this connection, the implementation auto-selects
     * the first Database available. Any auto-discovery performed
     * by implementations must take into account the connection
     * Database parameter.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.
     *
     * @return List of Catalogs in this connection's OLAP server
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No database name was specified and no databases
     *             exist on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             </ul>
     */
    NamedList<Catalog> getOlapCatalogs() throws OlapException;

    /**
     * Returns the {@link Schema} name that was selected for this connection,
     * either through the JDBC URL or via
     * {@link #setSchema(String)}.
     *
     * <p>If the user has not selected a Database, Catalog and Schema to use
     * for this connection, the driver will auto-select the first Database,
     * Catalog and Schema available.
     *
     * <p>Any auto-discovery performed by implementations must take into
     * account the specified Database, Catalog and Schema names, if any.
     *
     * @return The name of the schema currently selected for this connection.
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No database name was specified and no databases
     *             exist on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             <li>No catalog name was specified and no catalogs
     *             exist on the server.</li>
     *             <li>The user specified a catalog name which does not exist
     *             on the server.</li>
     *             <li>No schema name was specified and no schema
     *             exist on the server.</li>
     *             <li>The user specified a schema name which does not exist
     *             on the server.</li>
     *             </ul>
     */
    String getSchema() throws OlapException;

    /**
     * Sets the name of the active schema for this connection.
     * Overrides the value passed, if any, through the JDBC URL.
     *
     * @param schemaName The name of the schema to use for this connection.
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No database name was specified and no databases
     *             exist on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             <li>No catalog name was specified and no catalogs
     *             exist on the server.</li>
     *             <li>The user specified a catalog name which does not exist
     *             on the server.</li>
     *             <li>No schema name was specified and no schema
     *             exist on the server.</li>
     *             <li>The user specified a schema name which does not exist
     *             on the server.</li>
     *             </ul>
     */
    void setSchema(String schemaName) throws OlapException;

    /**
     * Returns the current active {@link org.olap4j.metadata.Schema}
     * of this connection.
     *
     * <p>If the user has not selected a Database, Catalog and Schema to use
     * for this connection, the driver will auto-select the first Database,
     * Catalog and Schema available.
     *
     * <p>Any auto-discovery performed by implementations must take into
     * account the specified Database, Catalog and Schema names, if any.
     *
     * @return The currently active schema
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No database name was specified and no databases
     *             exist on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             <li>No catalog name was specified and no catalogs
     *             exist on the server.</li>
     *             <li>The user specified a catalog name which does not exist
     *             on the server.</li>
     *             <li>No schema name was specified and no schema
     *             exist on the server.</li>
     *             <li>The user specified a schema name which does not exist
     *             on the server.</li>
     *             </ul>
     */
    Schema getOlapSchema() throws OlapException;

    /**
     * Returns a list of {@link org.olap4j.metadata.Schema} objects which
     * belong to this connection's OLAP server.
     *
     * <p>If the user has not selected a Database, Catalog and Schema to use
     * for this connection, the driver will auto-select the first Database and
     * Catalog available.
     *
     * <p>Any auto-discovery performed by implementations must take into
     * account the specified Database, Catalog and Schema names, if any.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.
     *
     * @return List of Catalogs in this connection's OLAP server
     * @throws OlapException if any of these conditions are true:
     *             <ul>
     *             <li>A server error occurs.</li>
     *             <li>No database name was specified and no databases
     *             exist on the server.</li>
     *             <li>The user specified a database name which does not
     *             exist on the server.</li>
     *             <li>No catalog name was specified and no catalogs
     *             exist on the server.</li>
     *             <li>The user specified a catalog name which does not exist
     *             on the server.</li>
     *             <li>No schema name was specified and no schema
     *             exist on the server.</li>
     *             <li>The user specified a schema name which does not exist
     *             on the server.</li>
     *             </ul>
     */
    NamedList<Schema> getOlapSchemas() throws OlapException;

    /**
     * Sets the current locale of this connection. The value must not be null.
     *
     * <p>If the locale is not set, the JDK's current locale is used (see
     * {@link java.util.Locale#getDefault()}).
     *
     * <p>Most drivers support a <code>Locale</code> connect-string property.
     *
     * @param locale Locale
     *
     * @see #getLocale()
     */
    void setLocale(Locale locale);

    /**
     * Returns this connection's locale. The value is never null.
     *
     * @return locale of this connection
     *
     * @see #setLocale(java.util.Locale)
     * @see org.olap4j.metadata.MetadataElement#getCaption()
     * @see org.olap4j.metadata.MetadataElement#getDescription()
     */
    Locale getLocale();

    /**
     * Sets the name of the role in which this connection executes queries. If
     * the name of the role is null, the connection reverts to the default
     * access control context.
     *
     * @param roleName Name of role
     * @throws OlapException if role name is invalid
     */
    void setRoleName(String roleName) throws OlapException;

    /**
     * Returns the name of the role in which this connection executes queries.
     *
     * @return name of the role in which this connection executes queries
     */
    String getRoleName();

    /**
     * Returns a list of the names of roles that are available for this user to
     * execute queries.
     *
     * @return a list of role names, or null if the available roles are not
     *    known
     *
     * @throws OlapException if database error occurs
     */
    List<String> getAvailableRoleNames() throws OlapException;

    /**
     * Creates a Scenario.
     *
     * <p>It does not become the active scenario for the current connection.
     * To do this, call {@link #setScenario(Scenario)}.
     *
     * @see #setScenario
     *
     * @return a new Scenario
     *
     * @throws OlapException if database error occurs
     */
    Scenario createScenario() throws OlapException;

    /**
     * Sets the active Scenario of this connection.
     *
     * <p>After setting a scenario, the client may call
     * {@link Cell#setValue} to change the value of cells returned
     * from queries. The value of those cells is changed. This operation is
     * referred to as 'writeback', and is used to perform 'what if' analysis,
     * such as budgeting. See {@link Scenario} for more details.
     *
     * <p>If {@code scenario} is null, the connection will have no active
     * scenario, and writeback is not allowed.
     *
     * <p>Scenarios are created using {@link #createScenario()}.
     *
     * @param scenario Scenario
     *
     * @throws OlapException if database error occurs
     */
    void setScenario(Scenario scenario) throws OlapException;

    /**
     * Returns this connection's active Scenario, or null if there is no
     * active Scenario.
     *
     * @return Active scenario, or null
     *
     * @throws OlapException if database error occurs
     */
    Scenario getScenario() throws OlapException;
}

// End OlapConnection.java
