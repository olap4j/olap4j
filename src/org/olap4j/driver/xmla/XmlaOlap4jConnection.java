/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.*;
import org.olap4j.mdx.parser.MdxParserFactory;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.mdx.parser.impl.DefaultMdxParserImpl;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.NamedList;

import java.util.*;
import java.sql.*;
import java.net.URL;
import java.net.MalformedURLException;

import mondrian.olap.Util;

/**
 * Implementation of {@link org.olap4j.OlapConnection}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id: MondrianOlap4jConnection.java 23 2007-06-15 03:53:14Z jhyde $
 * @since May 23, 2007
 */
abstract class XmlaOlap4jConnection implements OlapConnection {
    /**
     * Handler for errors.
     */
    final Helper helper = new Helper();

    /**
     * Current schema.
     */
    XmlaOlap4jSchema olap4jSchema;

    private final XmlaOlap4jDatabaseMetaData olap4jDatabaseMetaData;

    /**
     * The name of the sole catalog.
     */
    static final String LOCALDB_CATALOG_NAME = "LOCALDB";
    private static final String CONNECT_STRING_PREFIX = "jdbc:xmla:";

    final Factory factory;

    XmlaOlap4jDriver.Proxy proxy;

    private boolean closed;

    /**
     * URL of the HTTP server to which to send XML requests.
     */
    final URL serverUrl;

    /**
     * Creates an Olap4j connection an XML/A provider.
     *
     * <p>This method is intentionally package-protected. The public API
     * uses the traditional JDBC {@link java.sql.DriverManager}.
     * See {@link org.olap4j.driver.xmla.XmlaOlap4jDriver} for more details.
     *
     * @pre acceptsURL(url)
     *
     * @param factory Factory
     * @param proxy Proxy object which receives XML requests
     * @param url Connect-string URL
     * @param info Additional properties
     * @throws java.sql.SQLException if there is an error
     */
    XmlaOlap4jConnection(
        Factory factory,
        XmlaOlap4jDriver.Proxy proxy, 
        String url,
        Properties info)
        throws SQLException
    {
        this.factory = factory;
        this.proxy = proxy;
        if (!acceptsURL(url)) {
            // This is not a URL we can handle.
            // DriverManager should not have invoked us.
            throw new AssertionError(
                "does not start with '" + CONNECT_STRING_PREFIX + "'");
        }
        String x = url.substring(CONNECT_STRING_PREFIX.length());
        Util.PropertyList list = Util.parseConnectString(x);
        for (Map.Entry<String,String> entry : toMap(info).entrySet()) {
            list.put(entry.getKey(), entry.getValue());
        }
        this.olap4jDatabaseMetaData =
            factory.newDatabaseMetaData(this);
        this.olap4jSchema = null; // todo:

        // Set URL of HTTP server.
        String serverUrl = list.get(XmlaOlap4jDriver.Property.Server.name());
        if (serverUrl == null) {
            throw helper.createException("Connection property '"
                + XmlaOlap4jDriver.Property.Server.name()
                + "' must be specified");
        }
        try {
            this.serverUrl = new URL(serverUrl);
        } catch (MalformedURLException e) {
            throw helper.createException(
                "Error while creating connection", e);
        }
    }

    static boolean acceptsURL(String url) {
        return url.startsWith(CONNECT_STRING_PREFIX);
    }

    public OlapStatement createStatement() {
        return new XmlaOlap4jStatement(this);
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
        closed = true;
    }

    public boolean isClosed() throws SQLException {
        return closed;
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

    // implement Wrapper

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw helper.createException("does not implement '" + iface + "'");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    // implement OlapConnection

    public PreparedOlapStatement prepareOlapStatement(
        String mdx)
        throws OlapException {
        return factory.newPreparedStatement(mdx, this);
    }

    public MdxParserFactory getParserFactory() {
        return new MdxParserFactory() {
            public MdxParser createMdxParser(OlapConnection connection) {
                return new DefaultMdxParserImpl(connection);
            }
        };
    }

    public org.olap4j.metadata.Schema getSchema() throws OlapException {
        return olap4jSchema;
    }

    public static Map<String, String> toMap(final Properties properties) {
        return new AbstractMap<String, String>() {
            public Set<Entry<String, String>> entrySet() {
                return (Set<Entry<String, String>>) (Set) properties.entrySet();
            }
        };
    }

    /**
     * Returns the URL which was used to create this connection.
     *
     * @return URL
     */
    String getURL() {
        throw Util.needToImplement(this);
    }

    // inner classes

    static class Helper {
        SQLException createException(String msg) {
            return new SQLException(msg);
        }

        SQLException createException(String msg, Throwable cause) {
            return new OlapException(msg, cause);
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

// End XmlaOlap4jConnection.java
