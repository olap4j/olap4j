/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.mdx.parser.MdxParserFactory;

import java.sql.*;

/**
 * Miscellaneous utility functions, in particular dealing with the wrapped
 * objects created by connection pools.
 *
 * <p>Connection pools typically work by creating wrapped versions of
 * connection, statement, prepared statement, result set, and metadata objects.
 * These are problematic for olap4j, because olap4j extends the JDBC interfaces,
 * and the wrapped objects will not implement those interfaces. The following
 * table lists the interfaces extended by olap4j:</p>
 *
 * <table border="1">
 * <tr>
 * 		<th>olap4j class</th>
 * 		<th>extends JDBC class</th>
 * 		<th>created by</th>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link OlapConnection}</td>
 * 		<td><code>{@link Connection}</code></td>
 * 		<td>
 * 		<ul>
 * 			<li><code>{@link DriverManager#getConnection(String) Connection DriverManager.getConnection(String)}</code></li>
 * 			<li><code>{@link javax.sql.DataSource#getConnection() Connection DataSource.getConnection()}</code></li>
 * 		</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link OlapStatement}</td>
 * 		<td><code>{@link Statement}</code></td>
 * 		<td>
 * 		<ul>
 * 			<li><code>{@link java.sql.Connection#createStatement() Statement Connection.createStatement()}</code></li>
 * 		</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link PreparedOlapStatement}</td>
 * 		<td><code>{@link PreparedStatement}</code></td>
 * 		<td>
 * 		<ul>
 * 			<li><code>{@link Connection#prepareStatement(String) PreparedStatement Connection.prepareStatement(String)}</code></li>
 * 		</ul>
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>{@link OlapResultSet}</td>
 * 		<td><code>{@link ResultSet}</code></td>
 * 		<td>
 * 		<ul>
 * 			<li><code>{@link Statement#execute(String) ResultSet Statement.executeQuery(String)}</code></li>
 * 			<li><code>{@link PreparedStatement#execute(String) ResultSet PreparedStatement.executeQuery()}</code></li>
 * 		</ul>
 * 		</td>
 * 	</tr>
 * </table>
 *
 * <p>If you are using a connection pool, you cannot safely cast a
 * {@link Connection} to an {@link OlapConnection}, or a {@link Statement} to
 * a {@link OlapStatement}. Use the appropriate
 * {@link #convert(java.sql.Connection)} method, and the resulting object will
 * implement the necessary interface and also implement the behavior required
 * by the connection pool in order to manage sub-objects safely.
 *
 * <p>We plan to support common connection-pooling libraries:
 * <ul>
 * <li><a href="http://jakarta.apache.org/commons/dbcp/">Jakarta Commons DBCP</a>;</li>
 * <li><a href="http://sourceforge.net/projects/c3p0">C3P0</a>.</li>
 * </ul>
 *
 * @author jhyde
 * @version $Id: $
 * @since Oct 13, 2006
 */
public class Olap4j {
    /**
     * Converts a JDBC connection ({@link java.sql.Connection})
     * to an olap4j connection ({@link OlapConnection}).
     *
     * <p>The connection produced by an olap4j driver will already implement the
     * <code>OlapConnection</code> interface, but certain connection pools
     * add wrappers. This method removes those wrappers.
     */
    public static OlapConnection convert(Connection connection) {
        return adapterFor(connection).convert(connection);
    }

    /**
     * Converts a JDBC statement ({@link java.sql.Statement})
     * to an olap4j statement ({@link OlapStatement}).
     *
     * <p>The statement produced by an olap4j driver will already implement the
     * <code>OlapStatement</code> interface, but certain connection pools
     * add wrappers. This method removes those wrappers.
     */
    public static OlapStatement convert(Statement stmt) {
        return adapterFor(stmt).convert(stmt);
    }

    /**
     * Converts a JDBC prepared statement ({@link java.sql.Connection})
     * to an olap4j prepared statement ({@link PreparedOlapStatement}).
     *
     * <p>The prepared statement produced by an olap4j driver will already
     * implement the <code>PreparedOlapStatement</code> interface, but certain
     * connection pools add wrappers. This method removes those wrappers.
     */
    public static PreparedOlapStatement convert(PreparedStatement pstmt) {
        return adapterFor(pstmt).convert(pstmt);
    }

    /**
     * Converts a JDBC result set ({@link java.sql.ResultSet})
     * to an olap4j result set ({@link OlapResultSet}).
     *
     * <p>The result set eproduced by an olap4j driver will already
     * implement the <code>OlapResultSet</code> interface, but certain
     * connection pools add wrappers. This method removes those wrappers.
     */
    public static OlapResultSet convert(ResultSet resultSet) {
        return adapterFor(resultSet).convert(resultSet);
    }

    /**
     * Converts a JDBC result set ({@link java.sql.DatabaseMetaData})
     * to an olap4j result set ({@link OlapDatabaseMetaData}).
     *
     * <p>The <code>DatabaseMetaData</code> eproduced by an olap4j driver will
     * already implement the <code>OlapDatabaseMetaData</code> interface, but
     * certain connection pools add wrappers. This method removes those
     * wrappers.
     */
    public static OlapDatabaseMetaData convert(DatabaseMetaData metadata) {
        return adapterFor(metadata).convert(metadata);
    }

    /**
     * Helper method to automatically create the right adapter to unpack a
     * connection.
     */
    private static Adapter adapterFor(Connection connection) {
        return CastingAdapter.INSTANCE;
    }

    /**
     * Helper method to automatically create the right adapter to unpack a
     * statement.
     */
    private static Adapter adapterFor(Statement stmt) {
        return CastingAdapter.INSTANCE;
    }

    /**
     * Helper method to automatically create the right adapter to unpack a
     * prepared statement.
     */
    private static Adapter adapterFor(PreparedStatement pstmt) {
        return CastingAdapter.INSTANCE;
    }

    /**
     * Helper method to automatically create the right adapter to unpack a
     * database metadata.
     */
    private static Adapter adapterFor(DatabaseMetaData metadata) {
        return CastingAdapter.INSTANCE;
    }

    /**
     * Helper method to automatically create the right adapter to unpack a
     * result set.
     */
    private static Adapter adapterFor(ResultSet resultSet) {
        return CastingAdapter.INSTANCE;
    }

    public static MdxParserFactory getParserFactory(Connection connection) {
        return convert(connection).getParserFactory();
    }

    /**
     * Specification for a helper which can remove the wrappers added by
     * certain connection pools.
     */
    private static interface Adapter {
        OlapConnection convert(Connection connection);
        OlapStatement convert(Statement stmt);
        PreparedOlapStatement convert(PreparedStatement pstmt);
        OlapDatabaseMetaData convert(DatabaseMetaData metadata);
        OlapResultSet convert(ResultSet resultSet);
    }


    /**
     * Implementation of {@link Adapter} which simply casts objects to the
     * desired type.
     */
    private static class CastingAdapter implements Adapter {
        private static final Adapter INSTANCE = new CastingAdapter();

        public OlapConnection convert(Connection connection) {
            return (OlapConnection) connection;
        }

        public OlapStatement convert(Statement stmt) {
            return (OlapStatement) stmt;
        }

        public PreparedOlapStatement convert(PreparedStatement pstmt) {
            return (PreparedOlapStatement) pstmt;
        }

        public OlapDatabaseMetaData convert(DatabaseMetaData metadata) {
            return (OlapDatabaseMetaData) metadata;
        }

        public OlapResultSet convert(ResultSet resultSet) {
            return (OlapResultSet) resultSet;
        }
    }
}

// End Olap4j.java
