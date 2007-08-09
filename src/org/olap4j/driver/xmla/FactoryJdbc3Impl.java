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
 * Implementation of {@link Factory} for JDBC 3.0.
 *
 * @author jhyde
 * @version $Id: $
 * @since Jun 14, 2007
 */
class FactoryJdbc3Impl implements Factory {
    public Connection newConnection(
        XmlaOlap4jDriver.Proxy proxy,
        String url,
        Properties info)
        throws SQLException
    {
        return new FactoryJdbc3Impl.XmlaOlap4jConnectionJdbc3(
            proxy, url, info);
    }

    public EmptyResultSet newEmptyResultSet(
        XmlaOlap4jConnection olap4jConnection)
    {
        return new FactoryJdbc3Impl.EmptyResultSetJdbc3(olap4jConnection);
    }

    public XmlaOlap4jCellSet newCellSet(
        XmlaOlap4jStatement olap4jStatement,
        InputStream is)
    {
        return new FactoryJdbc3Impl.XmlaOlap4jCellSetJdbc3(
            olap4jStatement, is);
    }

    public XmlaOlap4jPreparedStatement newPreparedStatement(
        String mdx,
        XmlaOlap4jConnection olap4jConnection)
    {
        return new FactoryJdbc3Impl.XmlaOlap4jPreparedStatementJdbc3(olap4jConnection, mdx);
    }

    public XmlaOlap4jDatabaseMetaData newDatabaseMetaData(
        XmlaOlap4jConnection olap4jConnection)
    {
        return new FactoryJdbc3Impl.XmlaOlap4jDatabaseMetaDataJdbc3(olap4jConnection);
    }

    // Inner classes

    private static class XmlaOlap4jPreparedStatementJdbc3
        extends XmlaOlap4jPreparedStatement {
        public XmlaOlap4jPreparedStatementJdbc3(
            XmlaOlap4jConnection olap4jConnection,
            String mdx) {
            super(olap4jConnection, mdx);
        }
    }

    private static class XmlaOlap4jCellSetJdbc3
        extends XmlaOlap4jCellSet {
        public XmlaOlap4jCellSetJdbc3(
            XmlaOlap4jStatement olap4jStatement,
            InputStream is)
        {
            super(olap4jStatement, is);
        }
    }

    private static class EmptyResultSetJdbc3 extends EmptyResultSet {
        public EmptyResultSetJdbc3(
            XmlaOlap4jConnection olap4jConnection)
        {
            super(olap4jConnection);
        }
    }

    private class XmlaOlap4jConnectionJdbc3 extends XmlaOlap4jConnection {
        public XmlaOlap4jConnectionJdbc3(
            XmlaOlap4jDriver.Proxy proxy,
            String url,
            Properties info)
            throws SQLException
        {
            super(FactoryJdbc3Impl.this, proxy, url, info);
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
