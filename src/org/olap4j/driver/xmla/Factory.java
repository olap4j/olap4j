/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;

import java.sql.*;
import java.util.Properties;
import java.util.List;

/**
 * Instantiates classes to implement the olap4j API against the
 * an XML for Analysis provider.
 *
 * <p>There are implementations for JDBC 3.0 (which occurs in JDK 1.5)
 * and JDBC 4.0 (which occurs in JDK 1.6).
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 14, 2007
 */
interface Factory {
    Connection newConnection(
        XmlaOlap4jProxy proxy,
        String url,
        Properties info) throws SQLException;

    EmptyResultSet newEmptyResultSet(
        XmlaOlap4jConnection olap4jConnection);

    ResultSet newFixedResultSet(
        XmlaOlap4jConnection olap4jConnection,
        List<String> headerList,
        List<List<Object>> rowList);

    XmlaOlap4jCellSet newCellSet(
        XmlaOlap4jStatement olap4jStatement) throws OlapException;

    XmlaOlap4jPreparedStatement newPreparedStatement(
        String mdx, 
        XmlaOlap4jConnection olap4jConnection) throws OlapException;

    XmlaOlap4jDatabaseMetaData newDatabaseMetaData(
        XmlaOlap4jConnection olap4jConnection);
}

// End Factory.java
