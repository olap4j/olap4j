/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap4j;

import mondrian.olap.Util;

import java.sql.*;
import java.util.Properties;
import java.util.Map;

import org.olap4j.*;
import org.olap4j.metadata.Schema;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.NamedList;
import org.olap4j.mdx.parser.MdxParserFactory;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.mdx.parser.impl.DefaultMdxParserImpl;

/**
 * <code>MondrianOlap4jConnection</code> ...
 *
 * @author jhyde
 * @version $Id$
 * @since May 23, 2007
 */
class MondrianOlap4jConnection implements OlapConnection {
    /**
     * Handler for errors.
     */
    final Helper helper = new Helper();

    /**
     * Underlying mondrian connection. Set on creation, cleared on close.
     */
    mondrian.olap.Connection connection;

    /**
     * Current schema.
     */
    MondrianOlap4jSchema olap4jSchema;

    private final MondrianOlap4jDatabaseMetaData olap4jDatabaseMetaData;

    /**
     * The name of the sole catalog.
     */
    private static final String LOCALDB_CATALOG_NAME = "LOCALDB";
    private static final String CONNECT_STRING_PREFIX = "jdbc:mondrian:";

    /**
     * Creates an Olap4j connection to Mondrian.
     *
     * <p>This method is intentionally package-protected. The public API
     * uses the traditional JDBC {@link java.sql.DriverManager}.
     * See {@link mondrian.olap4j.MondrianOlap4jDriver} for more details.
     *
     * @param url Connect-string URL
     * @param info Additional properties
     * @throws SQLException if there is an error
     */
    MondrianOlap4jConnection(String url, Properties info) throws SQLException {
        if (!url.startsWith(CONNECT_STRING_PREFIX)) {
            // This is not a URL we can handle.
            // DriverManager should not have invoked us.
            throw new AssertionError(
                "does not start with '" + CONNECT_STRING_PREFIX + "'");
        }
        String x = url.substring(CONNECT_STRING_PREFIX.length());
        Util.PropertyList list = Util.parseConnectString(x);
        for (Map.Entry<String,String> entry : Util.toMap(info).entrySet()) {
            list.put(entry.getKey(), entry.getValue());
        }
        this.connection =
            mondrian.olap.DriverManager.getConnection(list, null);
        this.olap4jDatabaseMetaData =
            new MondrianOlap4jDatabaseMetaData(this, connection);
        String catalogName = getCatalog();
        final Catalog catalog = getCatalogs().get(catalogName);
        this.olap4jSchema =
            new MondrianOlap4jSchema(
                (MondrianOlap4jCatalog) catalog,
                connection.getSchemaReader(),
                connection.getSchema());
    }

    public OlapStatement createStatement() {
        return new MondrianOlap4jStatement(this);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String nativeSQL(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean getAutoCommit() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void commit() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void rollback() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void close() throws SQLException {
        if (connection != null) {
            mondrian.olap.Connection c = connection;
            connection = null;
            c.close();
        }
    }

    public boolean isClosed() throws SQLException {
        return connection == null;
    }

    public OlapDatabaseMetaData getMetaData() {
        return olap4jDatabaseMetaData;
    }

    public NamedList<Catalog> getCatalogs() {
        return olap4jDatabaseMetaData.getCatalogObjects();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isReadOnly() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setCatalog(String catalog) throws SQLException {
        if (!catalog.equals(LOCALDB_CATALOG_NAME)) {
            throw new UnsupportedOperationException();
        }
    }

    public String getCatalog() throws SQLException {
        return LOCALDB_CATALOG_NAME;
    }

    public void setTransactionIsolation(int level) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getTransactionIsolation() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Statement createStatement(
        int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(
        String sql,
        int resultSetType,
        int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall(
        String sql,
        int resultSetType,
        int resultSetConcurrency) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setHoldability(int holdability) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Savepoint setSavepoint() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Statement createStatement(
        int resultSetType,
        int resultSetConcurrency,
        int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(
        String sql,
        int resultSetType,
        int resultSetConcurrency,
        int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall(
        String sql,
        int resultSetType,
        int resultSetConcurrency,
        int resultSetHoldability) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(
        String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(
        String sql, int columnIndexes[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement(
        String sql, String columnNames[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

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

    // implement Wrapper

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        } else if (iface.isInstance(connection)) {
            return iface.cast(connection);
        }
        throw helper.createException("does not implement '" + iface + "'");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) ||
            iface.isInstance(connection);
    }

    // implement OlapConnection

    public PreparedOlapStatement prepareOlapStatement(
        String mdx)
        throws OlapException
    {
        throw new UnsupportedOperationException();
    }

    public MdxParserFactory getParserFactory() {
        return new MdxParserFactory() {
            public MdxParser createMdxParser(OlapConnection connection) {
                return new DefaultMdxParserImpl(connection);
            }
        };
    }

    public Schema getSchema() throws OlapException {
        return olap4jSchema;
    }

    // inner classes

    static class Helper {
        SQLException createException(String msg) {
            return new SQLException(msg);
        }

        OlapException createException(Cell context, String msg) {
            OlapException exception = new OlapException(msg);
            exception.setContext(context);
            return exception;
        }

        OlapException createException(Cell context, String msg, Throwable cause) {
            OlapException exception = new OlapException(msg, cause);
            exception.setContext(context);
            return exception;
        }

        public OlapException toOlapException(SQLException e) {
            if (e instanceof OlapException) {
                return (OlapException) e;
            } else {
                return new OlapException(null, e);
            }
        }
    }
}

// End MondrianOlap4jConnection.java
