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

    /**
     * Adds a listener to be notified of events to {@link CellSet}s created by
     * this statement.
     *
     * <p>NOTE: You may wonder why this method belongs to the
     * {@link OlapStatement} class and not {@code CellSet}. If the method
     * belonged to {@code CellSet} there would be a window between creation and
     * registering a listener during which events might be lost, whereas
     * registering the listener with the statement ensures that the listener is
     * attached immediately that the cell set is opened. It follows that
     * registering a listener does not affect the cell set <em>currently
     * open</em> (if any), and that no events will be received if the statement
     * has no open cell sets.
     *
     * @param granularity Granularity of cell set events to listen for
     *
     * @param listener Listener to be notified of changes
     *
     * @throws OlapException if granularity is not one supported by this server,
     *   per the
     *   {@link OlapDatabaseMetaData#getSupportedCellSetListenerGranularities()}
     *   method
     */
    void addListener(
        CellSetListener.Granularity granularity,
        CellSetListener listener)
        throws OlapException;
}

// End OlapStatement.java
