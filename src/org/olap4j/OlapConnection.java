/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.mdx.parser.MdxParserFactory;
import org.olap4j.metadata.*;

import java.sql.Connection;
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
     * Returns the current {@link org.olap4j.metadata.Schema} of this
     * connection.
     *
     * @return current Schema
     * @throws OlapException if database error occurs
     */
    Schema getSchema() throws OlapException;

    /**
     * Returns a list of {@link org.olap4j.metadata.Catalog} objects which
     * belong to this connection's OLAP server.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see OlapDatabaseMetaData#getCatalogs()
     * @return List of Catalogs in this connection's OLAP server
     */
    NamedList<Catalog> getCatalogs();

    /**
     * Sets the current locale of this connection. The value must not be null.
     *
     * <p>If the locale is not set, the JDK's current locale is used (see
     * {@link java.util.Locale#getDefault()}).
     *
     * <p>Most drivers support a <code>Locale</code> connect-string property.
     *
     * @param locale Locale
     */
    void setLocale(Locale locale);

    /**
     * Returns this connection's locale. The value is never null.
     *
     * @return locale of this connection
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
}

// End OlapConnection.java
