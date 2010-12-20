/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.mdx.parser.MdxParserFactory;
import org.olap4j.metadata.*;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;

/**
 * Connection to an OLAP server.
 *
 * @author jhyde
 * @version $Id$
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
     * Returns the database name that was selected for this connection,
     * either through the JDBC URL or via
     * {@link OlapConnection#setDatabase(String)}.
     *
     * @return The name of the database that was selected for this connection.
     * @throws OlapException If a server error occurs.
     */
    String getDatabase() throws OlapException;

    /**
     * Sets the name of the database that will be used for this connection.
     * Overrides the value passed, if any, through the JDBC URL.
     *
     * @param databaseName The name of the database to use.
     * @throws OlapException If a server error occurs.
     */
    void setDatabase(String databaseName) throws OlapException;

    /**
     * Returns a list of {@link org.olap4j.metadata.Catalog} objects which
     * belong to this connection's OLAP server.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see OlapDatabaseMetaData#getCatalogs()
     * @return List of Catalogs in this connection's OLAP server
     * @deprecated Deprecated in favor of
     * {@link OlapDatabaseMetaData#getOlapCatalogs()}. Will be removed as
     * of version 1.0.
     */
    @Deprecated
    NamedList<Catalog> getCatalogs();

    /**
     * Returns the {@link Catalog} name that was selected for this connection,
     * either through the JDBC URL or via
     * {@link OlapConnection#setCatalog(String)}.
     *
     * @return The name of the catalog that was selected for this connection.
     * @throws OlapException If a server error occurs.
     */
    String getCatalog() throws OlapException;

    /**
     * Sets the name of the catalog that will be used for this connection.
     * Overrides the value passed, if any, through the JDBC URL.
     *
     * @param catalogName The name of the catalog to use for this connection.
     * @throws OlapException If a server error occurs.
     */
    void setCatalog(String catalogName) throws OlapException;

    /**
     * Returns the current active {@link org.olap4j.metadata.Schema}
     * of this connection.
     * @return The currently active schema, or null of none are
     * currently selected.
     * @throws OlapException if database error occurs
     * @deprecated Will be removed as of version 1.0.
     */
    @Deprecated
    Schema getSchema() throws OlapException;

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
