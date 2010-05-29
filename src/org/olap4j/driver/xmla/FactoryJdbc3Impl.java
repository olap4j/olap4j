/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;

import java.sql.*;
import java.util.*;

/**
 * Implementation of {@link Factory} for JDBC 3.0.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 14, 2007
 */
class FactoryJdbc3Impl implements Factory {
    /**
     * Creates a FactoryJdbc3Impl.
     */
    public FactoryJdbc3Impl() {
    }

    public Connection newConnection(
        XmlaOlap4jDriver driver,
        XmlaOlap4jProxy proxy,
        String url,
        Properties info)
        throws SQLException
    {
        return new FactoryJdbc3Impl.XmlaOlap4jConnectionJdbc3(
            driver, proxy, url, info);
    }

    public EmptyResultSet newEmptyResultSet(
        XmlaOlap4jConnection olap4jConnection)
    {
        List<String> headerList = Collections.emptyList();
        List<List<Object>> rowList = Collections.emptyList();
        return new FactoryJdbc3Impl.EmptyResultSetJdbc3(
            olap4jConnection, headerList, rowList);
    }

    public ResultSet newFixedResultSet(
        XmlaOlap4jConnection olap4jConnection,
        List<String> headerList,
        List<List<Object>> rowList)
    {
        return new EmptyResultSetJdbc3(olap4jConnection, headerList, rowList);
    }

    public XmlaOlap4jCellSet newCellSet(
        XmlaOlap4jStatement olap4jStatement) throws OlapException
    {
        return new FactoryJdbc3Impl.XmlaOlap4jCellSetJdbc3(
            olap4jStatement);
    }

    public XmlaOlap4jPreparedStatement newPreparedStatement(
        String mdx,
        XmlaOlap4jConnection olap4jConnection) throws OlapException
    {
        return new FactoryJdbc3Impl.XmlaOlap4jPreparedStatementJdbc3(
            olap4jConnection, mdx);
    }

    public XmlaOlap4jDatabaseMetaData newDatabaseMetaData(
        XmlaOlap4jConnection olap4jConnection)
    {
        return new FactoryJdbc3Impl.XmlaOlap4jDatabaseMetaDataJdbc3(
            olap4jConnection);
    }

    // Inner classes

    private static class XmlaOlap4jPreparedStatementJdbc3
        extends XmlaOlap4jPreparedStatement
    {
        public XmlaOlap4jPreparedStatementJdbc3(
            XmlaOlap4jConnection olap4jConnection,
            String mdx) throws OlapException
        {
            super(olap4jConnection, mdx);
        }
    }

    private static class XmlaOlap4jCellSetJdbc3
        extends XmlaOlap4jCellSet
    {
        public XmlaOlap4jCellSetJdbc3(
            XmlaOlap4jStatement olap4jStatement) throws OlapException
        {
            super(olap4jStatement);
        }
    }

    private static class EmptyResultSetJdbc3 extends EmptyResultSet {
        public EmptyResultSetJdbc3(
            XmlaOlap4jConnection olap4jConnection,
            List<String> headerList,
            List<List<Object>> rowList)
        {
            super(olap4jConnection, headerList, rowList);
        }
    }

    private class XmlaOlap4jConnectionJdbc3 extends XmlaOlap4jConnection {
        public XmlaOlap4jConnectionJdbc3(
            XmlaOlap4jDriver driver,
            XmlaOlap4jProxy proxy,
            String url,
            Properties info)
            throws SQLException
        {
            super(FactoryJdbc3Impl.this, driver, proxy, url, info);
        }
    }

    private static class XmlaOlap4jDatabaseMetaDataJdbc3
        extends XmlaOlap4jDatabaseMetaData
    {
        public XmlaOlap4jDatabaseMetaDataJdbc3(
            XmlaOlap4jConnection olap4jConnection)
        {
            super(olap4jConnection);
        }
    }
}

// End FactoryJdbc3Impl.java
