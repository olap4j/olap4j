/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.mdx.parser.MdxParserFactory;
import org.olap4j.metadata.Schema;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.NamedList;

import java.sql.Connection;

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
}

// End OlapConnection.java
