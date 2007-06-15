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
 * Object used for statically executing an MDX statement and returning an
 * {@link CellSet}.
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
     * @return Cell set
     * @throws OlapException if error occurs
     */
    CellSet executeOlapQuery(String mdx) throws OlapException;

    /**
     * Executes an OLAP statement expressed as a parse tree.
     *
     * <p>Validates the parse tree before executing it.
     *
     * @param selectNode Parse tree of MDX <code>SELECT</code> statement
     * @return Cell set
     * @throws OlapException if error occurs
     */
    CellSet executeOlapQuery(SelectNode selectNode) throws OlapException;
}

// End OlapStatement.java
