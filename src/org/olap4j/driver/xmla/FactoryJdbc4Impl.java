/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;

import java.sql.*;
import java.util.*;

/**
 * Implementation of {@link Factory} for JDBC 4.0.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 14, 2007
 */
class FactoryJdbc4Impl implements Factory {
    /**
     * Creates a FactoryJdbc4Impl.
     */
    public FactoryJdbc4Impl() {
    }

    public Connection newConnection(
        XmlaOlap4jDriver driver,
        XmlaOlap4jProxy proxy,
        String url,
        Properties info)
        throws SQLException
    {
        return new XmlaOlap4jConnectionJdbc4(
            this, driver, proxy, url, info);
    }

    public EmptyResultSet newEmptyResultSet(
        XmlaOlap4jConnection olap4jConnection)
    {
        List<String> headerList = Collections.emptyList();
        List<List<Object>> rowList = Collections.emptyList();
        return new EmptyResultSetJdbc4(olap4jConnection, headerList, rowList);
    }

    public ResultSet newFixedResultSet(
        XmlaOlap4jConnection olap4jConnection,
        List<String> headerList,
        List<List<Object>> rowList)
    {
        return new EmptyResultSetJdbc4(
            olap4jConnection, headerList, rowList);
    }

    public XmlaOlap4jCellSet newCellSet(
        XmlaOlap4jStatement olap4jStatement) throws OlapException
    {
        return new XmlaOlap4jCellSetJdbc4(olap4jStatement);
    }

    public XmlaOlap4jStatement newStatement(
        XmlaOlap4jConnection olap4jConnection)
    {
        return new XmlaOlap4jStatementJdbc4(olap4jConnection);
    }

    public XmlaOlap4jPreparedStatement newPreparedStatement(
        String mdx,
        XmlaOlap4jConnection olap4jConnection) throws OlapException
    {
        return new XmlaOlap4jPreparedStatementJdbc4(olap4jConnection, mdx);
    }

    public XmlaOlap4jDatabaseMetaData newDatabaseMetaData(
        XmlaOlap4jConnection olap4jConnection)
    {
        return new XmlaOlap4jDatabaseMetaDataJdbc4(olap4jConnection);
    }

    // Inner classes

    private static class EmptyResultSetJdbc4
        extends FactoryJdbc4Plus.AbstractEmptyResultSet
    {
        /**
         * Creates a EmptyResultSetJdbc4.
         *
         * @param olap4jConnection Connection
         * @param headerList Column names
         * @param rowList List of row values
         */
        EmptyResultSetJdbc4(
            XmlaOlap4jConnection olap4jConnection,
            List<String> headerList,
            List<List<Object>> rowList)
        {
            super(olap4jConnection, headerList, rowList);
        }
    }

    private static class XmlaOlap4jConnectionJdbc4
        extends FactoryJdbc4Plus.AbstractConnection
    {
        /**
         * Creates a XmlaOlap4jConnectionJdbc4.
         *
         * @param factory Factory
         * @param driver Driver
         * @param proxy Proxy
         * @param url URL
         * @param info Extra properties
         * @throws SQLException on error
         */
        public XmlaOlap4jConnectionJdbc4(
            Factory factory,
            XmlaOlap4jDriver driver,
            XmlaOlap4jProxy proxy,
            String url,
            Properties info) throws SQLException
        {
            super(factory, driver, proxy, url, info);
        }
    }

    private static class XmlaOlap4jCellSetJdbc4
        extends FactoryJdbc4Plus.AbstractCellSet
    {
        /**
         * Creates an XmlaOlap4jCellSetJdbc4.
         *
         * @param olap4jStatement Statement
         * @throws OlapException on error
         */
        XmlaOlap4jCellSetJdbc4(
            XmlaOlap4jStatement olap4jStatement)
            throws OlapException
        {
            super(olap4jStatement);
        }
    }

    private static class XmlaOlap4jStatementJdbc4
        extends XmlaOlap4jStatement
    {
        /**
         * Creates a XmlaOlap4jStatementJdbc4.
         *
         * @param olap4jConnection Connection
         */
        XmlaOlap4jStatementJdbc4(
            XmlaOlap4jConnection olap4jConnection)
        {
            super(olap4jConnection);
        }
    }

    private static class XmlaOlap4jPreparedStatementJdbc4
        extends FactoryJdbc4Plus.AbstractPreparedStatement
    {
        /**
         * Creates a XmlaOlap4jPreparedStatementJdbc4.
         *
         * @param olap4jConnection Connection
         * @param mdx MDX query text
         * @throws OlapException on error
         */
        XmlaOlap4jPreparedStatementJdbc4(
            XmlaOlap4jConnection olap4jConnection,
            String mdx) throws OlapException
        {
            super(olap4jConnection, mdx);
        }
    }

    private static class XmlaOlap4jDatabaseMetaDataJdbc4
        extends FactoryJdbc4Plus.AbstractDatabaseMetaData
    {
        /**
         * Creates an XmlaOlap4jDatabaseMetaDataJdbc4.
         *
         * @param olap4jConnection Connection
         */
        XmlaOlap4jDatabaseMetaDataJdbc4(
            XmlaOlap4jConnection olap4jConnection)
        {
            super(olap4jConnection);
        }
    }
}

// End FactoryJdbc4Impl.java
