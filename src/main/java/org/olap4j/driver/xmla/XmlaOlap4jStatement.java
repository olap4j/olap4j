/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.driver.xmla;

import org.olap4j.CellSet;
import org.olap4j.CellSetListener;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.driver.xmla.XmlaOlap4jConnection.BackendFlavor;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.ParseTreeWriter;
import org.olap4j.mdx.SelectNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.olap4j.driver.xmla.XmlaOlap4jUtil.ROWSET_NS;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.SOAP_NS;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.SQL_NS;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.XMLA_NS;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.XSD_NS;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.childElements;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.findChild;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.findChildren;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.parse;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.prettyPrint;

/**
 * Implementation of {@link org.olap4j.OlapStatement}
 * for XML/A providers.
 *
 * @author jhyde
 * @since May 24, 2007
 */
abstract class XmlaOlap4jStatement implements OlapStatement {
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

    // Tells this statement to cancel as soon as it starts.
    private boolean cancelEarly = false;

    /**
     * Creates an XmlaOlap4jStatement.
     *
     * @param olap4jConnection Connection
     */
    XmlaOlap4jStatement(
        XmlaOlap4jConnection olap4jConnection)
    {
        assert olap4jConnection != null;
        this.olap4jConnection = olap4jConnection;
        this.closed = false;
    }

    /**
     * Returns the error-handler.
     *
     * @return Error handler
     */
    private XmlaHelper getHelper() {
        return olap4jConnection.helper;
    }

    // implement Statement

    public ResultSet executeQuery(String sql) throws SQLException {
        final String catalog = olap4jConnection.getCatalog();
        final String dataSourceInfo = olap4jConnection.getDatabase();
        final String roleName = olap4jConnection.getRoleName();
        final String propList = olap4jConnection.makeConnectionPropertyList();
        final StringBuilder buf = new StringBuilder(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<soapenv:Envelope\n"
            + "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
            + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
            + "    <soapenv:Body>\n"
            + "        <Execute xmlns=\"urn:schemas-microsoft-com:xml-analysis\">\n"
            + "        <Command>\n"
            + "        <Statement>\n"
            + "           <![CDATA[\n" + sql + "]]>\n"
            + "         </Statement>\n"
            + "        </Command>\n"
            + "        <Properties>\n"
            + "          <PropertyList>\n");
        if (catalog != null) {
            buf.append("            <Catalog>");
            buf.append(catalog);
            buf.append("</Catalog>\n");
        }
        if (propList != null) {
            buf.append(propList);
        }
        if (roleName != null && !("".equals(roleName))) {
            buf.append("        <Roles>");
            buf.append(roleName);
            buf.append("</Roles>\n");
        }
        if (dataSourceInfo != null) {
            buf.append("            <DataSourceInfo>");
            buf.append(dataSourceInfo);
            buf.append("</DataSourceInfo>\n");
        }
        buf.append(
            "            <Format>Tabular</Format>\n"
            + "            <AxisFormat>TupleFormat</AxisFormat>\n"
            + "          </PropertyList>\n"
            + "        </Properties>\n"
            + "</Execute>\n"
            + "</soapenv:Body>\n"
            + "</soapenv:Envelope>");
        final String request = buf.toString();

        this.future =
            olap4jConnection.proxy.submit(
                olap4jConnection.serverInfos, request);

        byte[] bytes = this.getBytes();

        Document doc;
        try {
            doc = parse(bytes);
        } catch (IOException e) {
            throw getHelper().createException("error creating ResultSet", e);
        } catch (SAXException e) {
            throw getHelper().createException("error creating ResultSet", e);
        }

        // <SOAP-ENV:Envelope>
        //   <SOAP-ENV:Header/>
        //   <SOAP-ENV:Body>
        //     <xmla:ExecuteResponse>
        //       <xmla:return>
        //         <root>
        //           (see below)
        //         </root>
        //       <xmla:return>
        //     </xmla:ExecuteResponse>
        //   </SOAP-ENV:Body>DEBUG
        // </SOAP-ENV:Envelope>
        final Element envelope = doc.getDocumentElement();
        assert envelope.getLocalName().equals("Envelope");
        assert envelope.getNamespaceURI().equals(SOAP_NS);
        Element body =
            findChild(envelope, SOAP_NS, "Body");
        Element fault =
            findChild(body, SOAP_NS, "Fault");
        if (fault != null) {
/*
        <SOAP-ENV:Fault>
            <faultcode>SOAP-ENV:Client.00HSBC01</faultcode>
            <faultstring>XMLA connection datasource not found</faultstring>
            <faultactor>Mondrian</faultactor>
            <detail>
                <XA:error xmlns:XA="http://mondrian.sourceforge.net">
                    <code>00HSBC01</code>
                    <desc>The Mondrian XML: Mondrian Error:Internal
                        error: no catalog named 'LOCALDB'</desc>
                </XA:error>
            </detail>
        </SOAP-ENV:Fault>
*/
            // TODO: log doc to logfile
            throw getHelper().createException(
                "XMLA provider gave exception: "
                + prettyPrint(fault));
        }
        Element executeResponse =
            findChild(body, XMLA_NS, "ExecuteResponse");
        Element returnElement =
            findChild(executeResponse, XMLA_NS, "return");
        // <root> has children
        //   <xsd:schema/>
        //   <row> ...
        final Element root =
            findChild(returnElement, ROWSET_NS, "root");

        Element schema = findChild(root, XSD_NS, "schema");
        List<Element> complexTypes =
            findChildren(schema, XSD_NS, "complexType");

        List<String> headerList = new ArrayList<String>();
        List<String> elementNameList = new ArrayList<String>();
        for (Element complexType : complexTypes) {
            if (complexType.getAttribute("name").equals("row")) {
                Element sequence = findChild(complexType, XSD_NS, "sequence");
                List<Element> elements =
                    findChildren(sequence, XSD_NS, "element");
                for (Element element : elements) {
                    String sqlField = element.getAttributeNS(SQL_NS, "field");
                    headerList.add(sqlField);

                    String name = element.getAttribute("name");
                    elementNameList.add(name);
                }

                break;
            }
        }

        final List<Element> rowNodes = findChildren(root, ROWSET_NS, "row");

        List<List<Object>> rowList = new ArrayList<List<Object>>();
        for (Element rowNode : rowNodes) {
            List<Object> row = new ArrayList<Object>();
            rowList.add(row);

            row.addAll(Collections.nCopies(headerList.size(), null));

            for (Element childElement : childElements(rowNode)) {
                String elementName = childElement.getLocalName();
                String value = childElement.getTextContent();

                int index = elementNameList.indexOf(elementName);
                row.set(index, value);
            }
        }

        return olap4jConnection.factory
            .newFixedResultSet(olap4jConnection, headerList, rowList);
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
            throw getHelper().createException(
                "illegal timeout value " + seconds);
        }
        this.timeoutSeconds = seconds;
    }

    public synchronized void cancel() {
        synchronized (this) {
            if (!canceled) {
                if (future != null) {
                    canceled = true;
                    future.cancel(true);
                } else {
                    this.cancelEarly = true;
                }
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

    public OlapConnection getConnection() {
        return olap4jConnection;
    }

    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate(
        String sql, int autoGeneratedKeys) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate(
        String sql, int columnIndexes[]) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int executeUpdate(
        String sql, String columnNames[]) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean execute(
        String sql, int autoGeneratedKeys) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean execute(
        String sql, int columnIndexes[]) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean execute(
        String sql, String columnNames[]) throws SQLException
    {
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
        throw getHelper().createException(
            "does not implement '" + iface + "'");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    // implement OlapStatement

    public CellSet executeOlapQuery(String mdx) throws OlapException {
        final String catalog = olap4jConnection.getCatalog();
        final String roleName = olap4jConnection.getRoleName();
        final String propList = olap4jConnection.makeConnectionPropertyList();

        final String dataSourceInfo;
        switch (BackendFlavor.getFlavor(olap4jConnection, true)) {
        case ESSBASE:
            dataSourceInfo =
                olap4jConnection.getOlapDatabase().getDataSourceInfo();
            break;
        default:
            dataSourceInfo =
                olap4jConnection.getDatabase();
        }

        StringBuilder buf = new StringBuilder(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<soapenv:Envelope\n"
            + "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
            + "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
            + "    <soapenv:Body>\n"
            + "        <Execute xmlns=\"urn:schemas-microsoft-com:xml-analysis\">\n"
            + "        <Command>\n"
            + "        <Statement>\n"
            + "           <![CDATA[\n" + mdx + "]]>\n"
            + "         </Statement>\n"
            + "        </Command>\n"
            + "        <Properties>\n"
            + "          <PropertyList>\n");
        if (catalog != null) {
            buf.append("            <Catalog>");
            buf.append(catalog);
            buf.append("</Catalog>\n");
        }
        if (propList != null) {
            buf.append(propList);
        }
        if (roleName != null && !roleName.equals("")) {
            buf.append("        <Roles>");
            buf.append(roleName);
            buf.append("</Roles>\n");
        }
        if (dataSourceInfo != null) {
            buf.append("            <DataSourceInfo>");
            buf.append(dataSourceInfo);
            buf.append("</DataSourceInfo>\n");
        }
        buf.append(
            "            <Format>Multidimensional</Format>\n"
            + "            <AxisFormat>TupleFormat</AxisFormat>\n"
            + "          </PropertyList>\n"
            + "        </Properties>\n"
            + "</Execute>\n"
            + "</soapenv:Body>\n"
            + "</soapenv:Envelope>");
        final String request = buf.toString();

        // Close the previous open CellSet, if there is one.
        synchronized (this) {
            if (openCellSet != null) {
                final XmlaOlap4jCellSet cs = openCellSet;
                openCellSet = null;
                try {
                    cs.close();
                } catch (SQLException e) {
                    throw getHelper().createException(
                        "Error while closing previous CellSet", e);
                }
            }

            this.future =
                olap4jConnection.proxy.submit(
                    olap4jConnection.serverInfos, request);
            openCellSet = olap4jConnection.factory.newCellSet(this);
        }
        if (cancelEarly) {
            cancel();
        }
        // Release the monitor before calling populate, so that cancel can
        // grab the monitor if it needs to.
        openCellSet.populate();
        return openCellSet;
    }

    public CellSet executeOlapQuery(
        SelectNode selectNode)
        throws OlapException
    {
        final String mdx = toString(selectNode);
        return executeOlapQuery(mdx);
    }

    public void addListener(
        CellSetListener.Granularity granularity,
        CellSetListener listener)
        throws OlapException
    {
        throw getHelper().createException(
            "This driver does not support the cell listener API.");
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
            throw getHelper().createException(null, e);
        } catch (ExecutionException e) {
            throw getHelper().createException(null, e.getCause());
        } catch (TimeoutException e) {
            throw getHelper().createException(
                "Query timeout of " + timeoutSeconds + " seconds exceeded");
        } catch (CancellationException e) {
            throw getHelper().createException("Query canceled");
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
        ParseTreeWriter parseTreeWriter = new ParseTreeWriter(sw);
        node.unparse(parseTreeWriter);
        return sw.toString();
    }
}

// End XmlaOlap4jStatement.java
