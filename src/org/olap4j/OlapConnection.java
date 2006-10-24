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

import java.sql.Connection;

/**
 * Connection to an OLAP server.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface OlapConnection extends Connection {

    // overrides Connection, with refined return type and throws list
    OlapDatabaseMetaData getMetaData() throws OlapException;

    /**
     * Creates a prepared OLAP Statement.
     */
    PreparedOlapStatement prepareOlapStatement(String mdx) throws OlapException;

    /**
     * Returns the factory used to create MDX parsers in this connection.
     */
    MdxParserFactory getParserFactory();

    // overrides Connection, with refined return type and throws list
    OlapStatement createStatement() throws OlapException;

    /**
     * Returns the current {@link org.olap4j.metadata.Schema} of this
     * connection.
     */
    Schema getSchema() throws OlapException;
}

// End OlapConnection.java
