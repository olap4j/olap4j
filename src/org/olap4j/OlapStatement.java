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

import org.olap4j.metadata.Cube;

import java.sql.Statement;
import java.sql.SQLException;

import mondrian.olap.Query;

/**
 * Object used for statically executing an MDX statement and returning an
 * {@link OlapResult}.
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
    OlapResult executeOlapQuery(String mdx) throws SQLException;

    /**
     * Executes an OLAP statement expressed as a parse tree.
     *
     * <p>Validates the parse tree before executing it.
     * @param query Parse tree of MDX SELECT statement
     * @return Result
     */
    OlapResult executeOlapQuery(Query query);

    /**
     * Returns the cube (or virtual cube) which this statement is based upon.
     */
    Cube getCube();

}

// End OlapStatement.java
