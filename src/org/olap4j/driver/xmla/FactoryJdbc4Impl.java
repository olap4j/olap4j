/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import java.sql.*;
import java.util.*;
import java.io.Reader;
import java.io.InputStream;

import org.olap4j.*;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;

/**
 * Implementation of {@link Factory} for JDBC 4.0.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 14, 2007
 */
class FactoryJdbc4Impl implements Factory {
    public Connection newConnection(
        XmlaOlap4jProxy proxy,
        String url,
        Properties info)
        throws SQLException
    {
        return new XmlaOlap4jConnectionJdbc4(
            this, proxy, url, info);
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

    private static class EmptyResultSetJdbc4 extends EmptyResultSet {
        EmptyResultSetJdbc4(
            XmlaOlap4jConnection olap4jConnection,
            List<String> headerList,
            List<List<Object>> rowList)
        {
            super(olap4jConnection, headerList, rowList);
        }

        // implement java.sql.ResultSet methods
        // introduced in JDBC 4.0/JDK 1.6

        public RowId getRowId(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public RowId getRowId(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateRowId(int columnIndex, RowId x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateRowId(String columnLabel, RowId x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public int getHoldability() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public boolean isClosed() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNString(
            int columnIndex, String nString) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateNString(
            String columnLabel, String nString) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            String columnLabel, NClob nClob) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public NClob getNClob(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public NClob getNClob(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public SQLXML getSQLXML(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public SQLXML getSQLXML(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateSQLXML(
            int columnIndex, SQLXML xmlObject) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateSQLXML(
            String columnLabel, SQLXML xmlObject) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public String getNString(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public String getNString(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public Reader getNCharacterStream(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public Reader getNCharacterStream(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNCharacterStream(
            int columnIndex, Reader x, long length) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateNCharacterStream(
            String columnLabel, Reader reader, long length) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateAsciiStream(
            int columnIndex, InputStream x, long length) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateBinaryStream(
            int columnIndex, InputStream x, long length) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateCharacterStream(
            int columnIndex, Reader x, long length) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateAsciiStream(
            String columnLabel, InputStream x, long length) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateBinaryStream(
            String columnLabel, InputStream x, long length) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateCharacterStream(
            String columnLabel, Reader reader, long length) throws SQLException
        {
            throw new UnsupportedOperationException();
        }

        public void updateBlob(
            int columnIndex,
            InputStream inputStream,
            long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBlob(
            String columnLabel,
            InputStream inputStream,
            long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateClob(
            int columnIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateClob(
            String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            int columnIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNCharacterStream(
            int columnIndex, Reader x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNCharacterStream(
            String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateAsciiStream(
            int columnIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBinaryStream(
            int columnIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateCharacterStream(
            int columnIndex, Reader x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateAsciiStream(
            String columnLabel, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBinaryStream(
            String columnLabel, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateCharacterStream(
            String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBlob(
            int columnIndex, InputStream inputStream) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBlob(
            String columnLabel, InputStream inputStream) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateClob(int columnIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateClob(
            String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            int columnIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    private static class XmlaOlap4jConnectionJdbc4
        extends XmlaOlap4jConnection
        implements OlapConnection
    {
        public XmlaOlap4jConnectionJdbc4(
            Factory factory,
            XmlaOlap4jProxy proxy,
            String url,
            Properties info) throws SQLException
        {
            super(factory, proxy, url, info);
        }

        public OlapStatement createStatement() {
            return super.createStatement();
        }

        public OlapDatabaseMetaData getMetaData() {
            return super.getMetaData();
        }

        // implement java.sql.Connection methods
        // introduced in JDBC 4.0/JDK 1.6

        public Clob createClob() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public Blob createBlob() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public NClob createNClob() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public SQLXML createSQLXML() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public boolean isValid(int timeout) throws SQLException {
            return !isClosed();
        }

        public void setClientInfo(
            String name, String value) throws SQLClientInfoException {
            throw new UnsupportedOperationException();
        }

        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            throw new UnsupportedOperationException();
        }

        public String getClientInfo(String name) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public Properties getClientInfo() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public Array createArrayOf(
            String typeName, Object[] elements) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public Struct createStruct(
            String typeName, Object[] attributes) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    private static class XmlaOlap4jCellSetJdbc4 extends XmlaOlap4jCellSet {
        XmlaOlap4jCellSetJdbc4(
            XmlaOlap4jStatement olap4jStatement)
            throws OlapException
        {
            super(olap4jStatement);
        }

        public CellSetMetaData getMetaData() {
            return super.getMetaData();
        }

        // implement java.sql.CellSet methods
        // introduced in JDBC 4.0/JDK 1.6

        public RowId getRowId(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public RowId getRowId(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateRowId(int columnIndex, RowId x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateRowId(String columnLabel, RowId x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public int getHoldability() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public boolean isClosed() throws SQLException {
            return closed;
        }

        public void updateNString(
            int columnIndex, String nString) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNString(
            String columnLabel, String nString) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            String columnLabel, NClob nClob) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public NClob getNClob(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public NClob getNClob(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public SQLXML getSQLXML(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public SQLXML getSQLXML(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateSQLXML(
            int columnIndex, SQLXML xmlObject) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateSQLXML(
            String columnLabel, SQLXML xmlObject) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public String getNString(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public String getNString(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public Reader getNCharacterStream(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public Reader getNCharacterStream(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNCharacterStream(
            int columnIndex, Reader x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNCharacterStream(
            String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateAsciiStream(
            int columnIndex, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBinaryStream(
            int columnIndex, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateCharacterStream(
            int columnIndex, Reader x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateAsciiStream(
            String columnLabel, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBinaryStream(
            String columnLabel, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateCharacterStream(
            String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBlob(
            int columnIndex,
            InputStream inputStream,
            long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBlob(
            String columnLabel,
            InputStream inputStream,
            long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateClob(
            int columnIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateClob(
            String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            int columnIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNCharacterStream(
            int columnIndex, Reader x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNCharacterStream(
            String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateAsciiStream(
            int columnIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBinaryStream(
            int columnIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateCharacterStream(
            int columnIndex, Reader x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateAsciiStream(
            String columnLabel, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBinaryStream(
            String columnLabel, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateCharacterStream(
            String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBlob(
            int columnIndex, InputStream inputStream) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateBlob(
            String columnLabel, InputStream inputStream) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateClob(int columnIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateClob(
            String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            int columnIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void updateNClob(
            String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    private static class XmlaOlap4jPreparedStatementJdbc4
        extends XmlaOlap4jPreparedStatement
    {
        XmlaOlap4jPreparedStatementJdbc4(
            XmlaOlap4jConnection olap4jConnection,
            String mdx) throws OlapException
        {
            super(olap4jConnection, mdx);
        }

        public CellSetMetaData getMetaData() {
            return super.getMetaData();
        }

        // implement java.sql.PreparedStatement methods
        // introduced in JDBC 4.0/JDK 1.6

        public void setRowId(int parameterIndex, RowId x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setNString(
            int parameterIndex, String value) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setNCharacterStream(
            int parameterIndex, Reader value, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setNClob(int parameterIndex, NClob value) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setClob(
            int parameterIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setBlob(
            int parameterIndex,
            InputStream inputStream,
            long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setNClob(
            int parameterIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setSQLXML(
            int parameterIndex, SQLXML xmlObject) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setAsciiStream(
            int parameterIndex, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setBinaryStream(
            int parameterIndex, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setCharacterStream(
            int parameterIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setAsciiStream(
            int parameterIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setBinaryStream(
            int parameterIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setCharacterStream(
            int parameterIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setNCharacterStream(
            int parameterIndex, Reader value) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setClob(int parameterIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setBlob(
            int parameterIndex, InputStream inputStream) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public void setNClob(
            int parameterIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    private static class XmlaOlap4jDatabaseMetaDataJdbc4
        extends XmlaOlap4jDatabaseMetaData
    {
        XmlaOlap4jDatabaseMetaDataJdbc4(
            XmlaOlap4jConnection olap4jConnection)
        {
            super(olap4jConnection);
        }

        public OlapConnection getConnection() {
            return super.getConnection();
        }

        // implement java.sql.DatabaseMetaData methods
        // introduced in JDBC 4.0/JDK 1.6

        public RowIdLifetime getRowIdLifetime() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public ResultSet getSchemas(
            String catalog, String schemaPattern) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public ResultSet getClientInfoProperties() throws SQLException {
            throw new UnsupportedOperationException();
        }

        public ResultSet getFunctions(
            String catalog,
            String schemaPattern,
            String functionNamePattern) throws SQLException {
            throw new UnsupportedOperationException();
        }

        public ResultSet getFunctionColumns(
            String catalog,
            String schemaPattern,
            String functionNamePattern,
            String columnNamePattern) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }
}

// End FactoryJdbc4Impl.java
