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

import java.sql.Statement;

import org.olap4j.mdx.SelectNode;

/**
 * Object used for statically executing an MDX statement and returning a
 * {@link CellSet}.
 *
 * <p>An <code>OlapStatement</code> is generally created using
 * {@link OlapConnection#createStatement()}.</p>
 *
 * @see PreparedOlapStatement
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface OlapStatement extends Statement, OlapWrapper {

    /**
     * Executes an OLAP statement.
     *
     * @param mdx MDX <code>SELECT</code> statement
     *
     * @return Cell set
     *
     * @throws OlapException if a database access error occurs,
     * this method is called on a closed <code>OlapStatement</code>,
     * the query times out (see {@link #setQueryTimeout(int)})
     * or another thread cancels the statement (see {@link #cancel()})
     */
    CellSet executeOlapQuery(String mdx) throws OlapException;

    /**
     * Executes an OLAP statement expressed as a parse tree.
     *
     * <p>Validates the parse tree before executing it.
     *
     * @param selectNode Parse tree of MDX <code>SELECT</code> statement
     *
     * @return Cell set
     *
     * @throws OlapException if a database access error occurs,
     * this method is called on a closed <code>OlapStatement</code>,
     * the query times out (see {@link #setQueryTimeout(int)})
     * or another thread cancels the statement (see {@link #cancel()})
     */
    CellSet executeOlapQuery(SelectNode selectNode) throws OlapException;
}

// End OlapStatement.java
