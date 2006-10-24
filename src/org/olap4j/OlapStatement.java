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

import mondrian.olap.Query;

import java.sql.Statement;

/**
 * Object used for statically executing an MDX statement and returning an
 * {@link OlapResultSet}.
 *
 * @see PreparedOlapStatement
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface OlapStatement extends Statement {

    /**
     * Executes an OLAP statement.
     */
    OlapResultSet executeOlapQuery(String mdx) throws OlapException;

    /**
     * Executes an OLAP statement expressed as a parse tree.
     *
     * <p>Validates the parse tree before executing it.
     * @param query Parse tree of MDX SELECT statement
     * @return Result
     */
    OlapResultSet executeOlapQuery(Query query);
}

// End OlapStatement.java
