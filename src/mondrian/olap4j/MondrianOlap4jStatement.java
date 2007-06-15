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

import org.olap4j.OlapStatement;
import org.olap4j.CellSet;
import org.olap4j.OlapException;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.ParseTreeWriter;

import java.sql.*;
import java.io.StringWriter;
import java.io.PrintWriter;

import mondrian.olap.Query;
import mondrian.olap.Result;

/**
 * Implementation of {@link org.olap4j.OlapStatement}
 * for the Mondrian OLAP engine.
 *
 * @author jhyde
 * @version $Id$
 * @since May 24, 2007
 */
class MondrianOlap4jStatement implements OlapStatement {
    final MondrianOlap4jConnection olap4jConnection;
    private boolean closed;
    MondrianOlap4jCellSet openCellSet;

    MondrianOlap4jStatement(
        MondrianOlap4jConnection olap4jConnection)
    {
        assert olap4jConnection != null;
        this.olap4jConnection = olap4jConnection;
        this.closed = false;
    }

    // implement Statement

    public ResultSet executeQuery(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    private void checkOpen() throws SQLException {
        if (closed) {
            throw olap4jConnection.helper.createException("closed");
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void close() throws SQLException {
        if (!closed) {
            closed = true;
            if (openCellSet != null) {
                CellSet c = openCellSet;
                openCellSet = null;
                c.close();
            }
        }
    }

    public int getMaxFieldSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setMaxFieldSize(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxRows() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setMaxRows(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getQueryTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void cancel() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean execute(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public ResultSet getResultSet() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getUpdateCount() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean getMoreResults() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getResultSetConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getResultSetType() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void addBatch(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void clearBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int[] executeBatch() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate(
        String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate(
        String sql, int columnIndexes[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate(
        String sql, String columnNames[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean execute(
        String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean execute(
        String sql, int columnIndexes[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean execute(
        String sql, String columnNames[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getResultSetHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isClosed() throws SQLException {
        return closed;
    }

    public void setPoolable(boolean poolable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isPoolable() throws SQLException {
        throw new UnsupportedOperationException();
    }

    // implement Wrapper

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw olap4jConnection.helper.createException(
            "does not implement '" + iface + "'");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    // implement OlapStatement

    public CellSet executeOlapQuery(String mdx) throws OlapException {
        Query query = olap4jConnection.connection.parseQuery(mdx);
        return executeOlapQueryInternal(query);
    }

    /**
     * Executes a parsed query, closing any previously open cellset.
     *
     * @param query Parsed query
     * @return Cell set
     * @throws OlapException if a database error occurs
     */
    protected CellSet executeOlapQueryInternal(Query query) throws OlapException {
        // Close the previous open CellSet, if there is one.
        if (openCellSet != null) {
            final MondrianOlap4jCellSet cs = openCellSet;
            openCellSet = null;
            try {
                cs.close();
            } catch (SQLException e) {
                throw olap4jConnection.helper.createException(
                    null, "Error while closing previous CellSet", e);
            }
        }
        Result result = olap4jConnection.connection.execute(query);
        openCellSet = olap4jConnection.factory.newCellSet(this, result);
        return openCellSet;
    }

    public CellSet executeOlapQuery(SelectNode selectNode) throws OlapException {
        final String mdx = toString(selectNode);
        return executeOlapQuery(mdx);
    }

    /**
     * Converts a {@link org.olap4j.mdx.ParseTreeNode} to MDX string.
     *
     * @param node Parse tree node
     * @return MDX text
     */
    private static String toString(ParseTreeNode node) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ParseTreeWriter parseTreeWriter = new ParseTreeWriter(pw);
        node.unparse(parseTreeWriter);
        pw.flush();
        return sw.toString();
    }
}

// End MondrianOlap4jStatement.java
