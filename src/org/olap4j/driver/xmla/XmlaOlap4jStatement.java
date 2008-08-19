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
import org.olap4j.driver.xmla.messages.XmlaOlap4jMessenger;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.ParseTreeWriter;

import java.sql.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * Implementation of {@link org.olap4j.OlapStatement}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since May 24, 2007
 */
class XmlaOlap4jStatement implements OlapStatement {
    final XmlaOlap4jConnection olap4jConnection;
    private boolean closed;

    /**
     * Current cell set, or null if the statement is not executing anything.
     * Any method which modifies this member must synchronize
     * on the {@link XmlaOlap4jStatement}.
     */
    XmlaOlap4jCellSet openCellSet;
    private boolean canceled;
    int timeoutSeconds;
    Future<byte []> future;

    XmlaOlap4jStatement(
        XmlaOlap4jConnection olap4jConnection)
    {
        assert olap4jConnection != null;
        this.olap4jConnection = olap4jConnection;
        this.closed = false;
    }

    // implement Statement

    public ResultSet executeQuery(String sql) throws SQLException {
        throw new UnsupportedOperationException();
    }

    // This method is not used anywhere thus marked as deprecated.
    @Deprecated
    private void checkOpen() throws SQLException {
        if (closed) {
            throw XmlaOlap4jMessenger.getInstance().createException("closed");
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
        return timeoutSeconds;
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        if (seconds < 0) {
            throw XmlaOlap4jMessenger.getInstance().createException(
                "XmlaOlap4jStatement.invalid_timeout_value",
                seconds);
        }
        this.timeoutSeconds = seconds;
    }

    public synchronized void cancel() {
        if (!canceled) {
            canceled = true;
            if (future != null) {
                future.cancel(true);
            }
        }
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
        throw XmlaOlap4jMessenger.getInstance().createException(
            "does not implement '" + iface + "'");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    // implement OlapStatement

    public CellSet executeOlapQuery(String mdx) throws OlapException {
        final String catalog = olap4jConnection.getCatalog();
        final String dataSourceInfo = olap4jConnection.getDataSourceInfo();
        StringBuilder buf = new StringBuilder(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soapenv:Envelope\n" +
                "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <soapenv:Body>\n" +
                "        <Execute xmlns=\"urn:schemas-microsoft-com:xml-analysis\">\n" +
                "        <Command>\n" +
                "        <Statement>\n" +
                "           <![CDATA[\n" + mdx + "]]>\n" +
                "         </Statement>\n" +
                "        </Command>\n" +
                "        <Properties>\n" +
                "          <PropertyList>\n");
        if (catalog != null) {
            buf.append("            <Catalog>");
            buf.append(catalog);
            buf.append("</Catalog>\n");
        }
        if (dataSourceInfo != null) {
            buf.append("            <DataSourceInfo>");
            buf.append(dataSourceInfo);
            buf.append("</DataSourceInfo>\n");
        }
        buf.append(
            "            <Format>Multidimensional</Format>\n" +
                "            <AxisFormat>TupleFormat</AxisFormat>\n" +
                "          </PropertyList>\n" +
                "        </Properties>\n" +
                "</Execute>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>");
        final String request = buf.toString();

        // Close the previous open CellSet, if there is one.
        synchronized (this) {
            if (openCellSet != null) {
                final XmlaOlap4jCellSet cs = openCellSet;
                openCellSet = null;
                try {
                    cs.close();
                } catch (SQLException e) {
                    throw XmlaOlap4jMessenger.getInstance().createException(
                        "Error while closing previous CellSet", e);
                }
            }

            this.future =
                olap4jConnection.proxy.submit(
                    olap4jConnection.serverUrl, request);
            openCellSet = olap4jConnection.factory.newCellSet(this);
        }
        // Release the monitor before calling populate, so that cancel can
        // grab the monitor if it needs to.
        openCellSet.populate();
        return openCellSet;
    }

    public CellSet executeOlapQuery(SelectNode selectNode) throws OlapException {
        final String mdx = toString(selectNode);
        return executeOlapQuery(mdx);
    }

    /**
     * Waits for an XMLA request to complete.
     *
     * <p>You must not hold the monitor on this Statement when calling this
     * method; otherwise {@link #cancel()} will not be able to operate.
     *
     * @return Byte array resulting from successful request
     *
     * @throws OlapException if error occurred, or request timed out or
     * was canceled
     */
    byte[] getBytes() throws OlapException {
        synchronized (this) {
            if (future == null) {
                throw new IllegalArgumentException();
            }
        }
        try {
            // Wait for the request to complete, with timeout if necessary.
            // Whether or not timeout is used, the request can still be
            // canceled.
            if (timeoutSeconds > 0) {
                return future.get(timeoutSeconds, TimeUnit.SECONDS);
            } else {
                return future.get();
            }
        } catch (InterruptedException e) {
            throw XmlaOlap4jMessenger.getInstance().createException(
                "XmlaOlap4jStatement.interrupted",
                e);
        } catch (ExecutionException e) {
            throw XmlaOlap4jMessenger.getInstance().createException(
                "XmlaOlap4jStatement.execution_exception",
                e);
        } catch (TimeoutException e) {
            throw XmlaOlap4jMessenger.getInstance().createException(
                "XmlaOlap4jStatement.statement_timed_out",
                timeoutSeconds);
        } catch (CancellationException e) {
            throw XmlaOlap4jMessenger.getInstance().createException(
                "XmlaOlap4jStatement.statement_canceled");
        } finally {
            synchronized (this) {
                if (future == null) {
                    throw new IllegalArgumentException();
                }
                future = null;
            }
        }
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

// End XmlaOlap4jStatement.java
