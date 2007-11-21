/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

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
        XmlaOlap4jDriver.Proxy proxy,
        String url,
        Properties info) throws SQLException;

    EmptyResultSet newEmptyResultSet(XmlaOlap4jConnection olap4jConnection);

    XmlaOlap4jCellSet newCellSet(
        XmlaOlap4jStatement olap4jStatement, /*,
        Result result */InputStream is);

    XmlaOlap4jPreparedStatement newPreparedStatement(
        String mdx, XmlaOlap4jConnection olap4jConnection);

    XmlaOlap4jDatabaseMetaData newDatabaseMetaData(
        XmlaOlap4jConnection olap4jConnection);
}

// End Factory.java
