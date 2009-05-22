/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.*;

import static org.olap4j.driver.xmla.XmlaOlap4jUtil.*;

import org.olap4j.driver.xmla.proxy.*;
import org.olap4j.impl.*;
import org.olap4j.mdx.ParseTreeWriter;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.parser.*;
import org.olap4j.mdx.parser.impl.DefaultMdxParserImpl;
import org.olap4j.metadata.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Map.*;
import java.util.regex.*;

/**
 * Implementation of {@link org.olap4j.OlapConnection}
 * for XML/A providers.
 *
 * <p>This class has sub-classes which implement JDBC 3.0 and JDBC 4.0 APIs;
 * it is instantiated using {@link Factory#newConnection}.</p>
 *
 * @author jhyde
 * @version $Id$
 * @since May 23, 2007
 */
abstract class XmlaOlap4jConnection implements OlapConnection {
    /**
     * Handler for errors.
     */
    final XmlaHelper helper = new XmlaHelper();

    /**
     * <p>Current schema.
     */
    private XmlaOlap4jSchema olap4jSchema;

    private final XmlaOlap4jDatabaseMetaData olap4jDatabaseMetaData;

    private static final String CONNECT_STRING_PREFIX = "jdbc:xmla:";

    final XmlaOlap4jDriver driver;

    final Factory factory;

    final XmlaOlap4jProxy proxy;

    private boolean closed;

    /**
     * URL of the HTTP server to which to send XML requests.
     */
    final URL serverUrl;

    private Locale locale;
    private String catalogName;
    private static final boolean DEBUG = false;
    private String roleName;

    /**
     * Provider name.
     */
    private String providerName;

    /**
     * Datasource name.
     */
    private String datasourceName;

    /**
     * Holds the datasource name as specified by the server. Necessary because
     * some servers (such as mondrian) return both the provider
     * name and the datasource name in their response.
     *
     * <p>It's this value that we use inside queries, and not the jdbc
     * query value.
     */
    private String nativeDatasourceName;
    private boolean autoCommit;
    private boolean readOnly;

    /**
     * Name of the "DATA_SOURCE_NAME" column returned from
     * {@link org.olap4j.OlapDatabaseMetaData#getDatasources()}.
     */
    private static final String DATA_SOURCE_NAME = "DATA_SOURCE_NAME";

    /**
     * Name of the "PROVIDER_NAME" column returned from
     * {@link org.olap4j.OlapDatabaseMetaData#getDatasources()}.
     */
    private static final String PROVIDER_NAME = "PROVIDER_NAME";

    /**
     * Creates an Olap4j connection an XML/A provider.
     *
     * <p>This method is intentionally package-protected. The public API
     * uses the traditional JDBC {@link java.sql.DriverManager}.
     * See {@link org.olap4j.driver.xmla.XmlaOlap4jDriver} for more details.
     *
     * <p>Note that this constructor should make zero non-trivial calls, which
     * could cause deadlocks due to java.sql.DriverManager synchronization
     * issues.
     *
     * @pre acceptsURL(url)
     *
     * @param factory Factory
     * @param driver Driver
     * @param proxy Proxy object which receives XML requests
     * @param url Connect-string URL
     * @param info Additional properties
     * @throws java.sql.SQLException if there is an error
     */
    XmlaOlap4jConnection(
        Factory factory,
        XmlaOlap4jDriver driver,
        XmlaOlap4jProxy proxy,
        String url,
        Properties info)
        throws SQLException
    {
        this.factory = factory;
        this.driver = driver;
        this.proxy = proxy;
        if (!acceptsURL(url)) {
            // This is not a URL we can handle.
            // DriverManager should not have invoked us.
            throw new AssertionError(
                "does not start with '" + CONNECT_STRING_PREFIX + "'");
        }
        Map<String, String> map = parseConnectString(url, info);

        this.providerName = map.get(XmlaOlap4jDriver.Property.Provider.name());
        this.datasourceName =
            map.get(XmlaOlap4jDriver.Property.DataSource.name());
        this.catalogName = map.get(XmlaOlap4jDriver.Property.Catalog.name());

        // Set URL of HTTP server.
        String serverUrl = map.get(XmlaOlap4jDriver.Property.Server.name());
        if (serverUrl == null) {
            throw getHelper().createException(
                "Connection property '"
                + XmlaOlap4jDriver.Property.Server.name()
                + "' must be specified");
        }

        // Basic authentication. Make sure the credentials passed as standard
        // JDBC parameters override any credentials already included in the URL
        // as part of the standard URL scheme.
        if (map.containsKey("user") && map.containsKey("password")) {
            serverUrl = serverUrl.replaceFirst(
                ":\\/\\/([^@]*@){0,1}",
                "://"
                    .concat(map.get("user"))
                    .concat(":")
                    .concat(map.get("password")
                    .concat("@")));
        }

        // Initialize the SOAP cache if needed
        initSoapCache(map);

        try {
            this.serverUrl = new URL(serverUrl);
        } catch (MalformedURLException e) {
            throw getHelper().createException(
                "Error while creating connection", e);
        }

        this.olap4jDatabaseMetaData =
            factory.newDatabaseMetaData(this);
    }

    /**
     * Returns the error-handler
     * @return Error-handler
     */
    private final XmlaHelper getHelper() {
        return helper;
    }

    /**
     * Initializes a cache object and configures it if cache
     * parameters were specified in the jdbc url.
     *
     * @param map The parameters from the jdbc url.
     * @throws OlapException Thrown when there is an error encountered
     * while creating the cache.
     */
    private void initSoapCache(Map<String, String> map) throws OlapException {
        //  Test if a SOAP cache class was defined
        if (map.containsKey(XmlaOlap4jDriver.Property.Cache.name()
            .toUpperCase()))
        {
            // Create a properties object to pass to the proxy
            // so it can configure it's cache
            Map<String,String> props = new HashMap<String, String>();
            //  Iterate over map entries to find those related to
            //  the cache config
            for (Entry<String, String> entry : map.entrySet()) {
                // Check if the current entry relates to cache config.
                if (entry.getKey().startsWith(
                    XmlaOlap4jDriver.Property.Cache.name().toUpperCase()
                    + ".")) //$NON-NLS-1$
                {
                    props.put(entry.getKey().substring(
                        XmlaOlap4jDriver.Property.Cache.name()
                        .length() + 1), entry.getValue());
                }
            }

            // Init the cache
            ((XmlaOlap4jCachedProxy) this.proxy).setCache(map, props);
        }
    }



    static Map<String, String> parseConnectString(String url, Properties info) {
        String x = url.substring(CONNECT_STRING_PREFIX.length());
        Map<String, String> map =
            ConnectStringParser.parseConnectString(x);
        for (Map.Entry<String,String> entry : toMap(info).entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    static boolean acceptsURL(String url) {
        return url.startsWith(CONNECT_STRING_PREFIX);
    }

    /**
     * Returns the native datasource name, using the cached value if already
     * computed.
     *
     * <p>Not part of public API.
     *
     * @return native datasource name
     * @throws OlapException if cannot find a datasource that matches
     */
    String getDataSourceInfo() throws OlapException {
        // If we already know it, return it.
        if (this.nativeDatasourceName != null) {
            return this.nativeDatasourceName;
        }

        ResultSet rSet = null;
        try {
            // We need to query for it
            rSet = this.olap4jDatabaseMetaData.getDatasources();

            // Check if the user requested a particular one.
            if (this.datasourceName != null || this.providerName != null) {
                // We iterate through the datasources
                while (rSet.next()) {
                    // Get current values
                    String currentDatasource = rSet.getString(DATA_SOURCE_NAME);
                    String currentProvider = rSet.getString(PROVIDER_NAME);

                    // If datasource and provider match, we got it.
                    // If datasource matches but no provider is specified, we
                    // got it.
                    // If provider matches but no datasource specified, we
                    // consider it good.
                    if (currentDatasource.equals(this.datasourceName)
                        && currentProvider.equals(this.providerName)
                        || currentDatasource.equals(this.datasourceName)
                        && this.providerName == null
                        || currentProvider.equals(this.providerName)
                        && this.datasourceName == null)
                    {
                        // Got it
                        this.nativeDatasourceName = currentDatasource;
                        break;
                    }
                }
            } else {
                // Use first
                if (rSet.first()) {
                    this.nativeDatasourceName =
                        rSet.getString(DATA_SOURCE_NAME);
                }
            }

            // Throws exception to the client.  Tells that there are
            // no datasource corresponding to the search criterias.
            if (this.nativeDatasourceName == null) {
                throw getHelper().createException(
                    "No datasource could be found.");
            }

            // If there is a provider
            return this.nativeDatasourceName;
        } catch (OlapException e) {
            throw e;
        } catch (SQLException e) {
            throw getHelper().createException("Datasource name not found.", e);
        } finally {
            try {
                if (rSet != null) {
                    rSet.close();
                }
            } catch (SQLException e) {
                // ignore
            }
        }
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
        this.autoCommit = autoCommit;
    }

    public boolean getAutoCommit() throws SQLException {
        return autoCommit;
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
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() throws SQLException {
        return readOnly;
    }

    public void setCatalog(String catalog) throws SQLException {
        this.catalogName = catalog;
    }

    public String getCatalog() throws OlapException {
        if (this.catalogName == null) {
            // This means that no particular catalog name
            // was specified by the user.
            List<Catalog> catalogs = this.getCatalogs();
            if (catalogs.size() == 0) {
                throw new OlapException(
                    "There is no catalog available to query against.");
            } else {
                this.catalogName = catalogs.get(0).getName();
            }
        } else {
            // We must verify that the requested catalog name
            // exists in the metadata.
            Catalog buf = this.getCatalogs().get(this.catalogName);
            if (buf != null) {
                this.catalogName = buf.getName();
            } else {
                throw new OlapException("There is no catalog named " +
                    this.catalogName + " available to query against.");
            }
        }
        return catalogName;
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
        // this driver does not support warnings, so nothing to do
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
        throw getHelper().createException("does not implement '" + iface + "'");
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

            public MdxValidator createMdxValidator(OlapConnection connection) {
                return new XmlaOlap4jMdxValidator(connection);
            }
        };
    }

    public synchronized org.olap4j.metadata.Schema getSchema()
        throws OlapException
    {
        // initializes the olap4jSchema if necessary
        if (this.olap4jSchema == null) {
            final XmlaOlap4jCatalog catalog = (XmlaOlap4jCatalog)
                this.olap4jDatabaseMetaData.getCatalogObjects().get(
                    this.getCatalog());
            this.olap4jSchema = (XmlaOlap4jSchema)
                catalog.getSchemas().get(0);
            }
        return olap4jSchema;
    }

    public static Map<String, String> toMap(final Properties properties) {
        return new AbstractMap<String, String>() {
            public Set<Entry<String, String>> entrySet() {
                return Olap4jUtil.cast(properties.entrySet());
            }
        };
    }

    /**
     * Returns the URL which was used to create this connection.
     *
     * @return URL
     */
    String getURL() {
        throw Olap4jUtil.needToImplement(this);
    }

    public void setLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale must not be null");
        }
        this.locale = locale;
    }

    public Locale getLocale() {
        if (locale == null) {
            return Locale.getDefault();
        }
        return locale;
    }

    public void setRoleName(String roleName) throws OlapException {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    <T extends Named> void populateList(
        List<T> list,
        Context context,
        MetadataRequest metadataRequest,
        Handler<T> handler,
        Object[] restrictions) throws OlapException
    {
        String request =
            generateRequest(context, metadataRequest, restrictions);
        Element root = xxx(request);
        for (Element o : childElements(root)) {
            if (o.getLocalName().equals("row")) {
                handler.handle(o, context, list);
            }
        }
        handler.sortList(list);
    }

    Element xxx(String request) throws OlapException {
        byte[] bytes;
        try {
            bytes = proxy.get(serverUrl, request);
        } catch (IOException e) {
            /*
             * FIXME This type of exception should not reach this point.
             * It was maintained because some other proxy implementations
             * exists out there that still throw an IOException arround.
             * This was a bad design which we will fix at some point but not
             * before the 1.0 release.
             */
            throw getHelper().createException(e);
        } catch (XmlaOlap4jProxyException e) {
            throw getHelper().createException(
                "This connection encountered an exception while executing a query.",
                e);
        }
        Document doc;
        try {
            doc = parse(bytes);
        } catch (IOException e) {
            throw getHelper().createException(
                "error discovering metadata", e);
        } catch (SAXException e) {
            throw getHelper().createException(
                "error discovering metadata", e);
        }
        // <SOAP-ENV:Envelope>
        //   <SOAP-ENV:Header/>
        //   <SOAP-ENV:Body>
        //     <xmla:DiscoverResponse>
        //       <xmla:return>
        //         <root>
        //           (see below)
        //         </root>
        //       <xmla:return>
        //     </xmla:DiscoverResponse>
        //   </SOAP-ENV:Body>
        // </SOAP-ENV:Envelope>
        final Element envelope = doc.getDocumentElement();
        if (DEBUG) {
            System.out.println(XmlaOlap4jUtil.toString(doc, true));
        }
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
                + XmlaOlap4jUtil.prettyPrint(fault)
                + "\n"
                + "Request was:\n"
                + request);
        }
        Element discoverResponse =
            findChild(body, XMLA_NS, "DiscoverResponse");
        Element returnElement =
            findChild(discoverResponse, XMLA_NS, "return");
        return findChild(returnElement, ROWSET_NS, "root");
    }

    /**
     * Generates a metadata request.
     *
     * <p>The list of restrictions must have even length. Even elements must
     * be a string (the name of the restriction); odd elements must be either
     * a string (the value of the restriction) or a list of strings (multiple
     * values of the restriction)
     *
     * @param context Context
     * @param metadataRequest Metadata request
     * @param restrictions List of restrictions
     * @return XMLA SOAP request as a string.
     *
     * @throws OlapException when the query depends on a datasource name but
     * the one specified doesn't exist at the url, or there are no default
     * datasource (should use the first one)
     */
    public String generateRequest(
        Context context,
        MetadataRequest metadataRequest,
        Object[] restrictions) throws OlapException
    {
        final String content = "Data";
        final String encoding = proxy.getEncodingCharsetName();
        final StringBuilder buf = new StringBuilder(
            "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n"
                + "<SOAP-ENV:Envelope\n"
                + "    xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
                + "    SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"
                + "  <SOAP-ENV:Body>\n"
                + "    <Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n"
                + "        SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"
                + "    <RequestType>");
        buf.append(metadataRequest.name());
        buf.append("</RequestType>\n"
                + "    <Restrictions>\n"
                + "      <RestrictionList>\n");
        if (restrictions.length > 0) {
            if (restrictions.length % 2 != 0) {
                throw new IllegalArgumentException();
            }
            for (int i = 0; i < restrictions.length; i += 2) {
                final String restriction = (String) restrictions[i];
                final Object o = restrictions[i + 1];
                if (o instanceof String) {
                    buf.append("<").append(restriction).append(">");
                    final String value = (String) o;
                    buf.append(xmlEncode(value));
                    buf.append("</").append(restriction).append(">");

                } else {
                    //noinspection unchecked
                    List<String> valueList = (List<String>) o;
                    for (String value : valueList) {
                        buf.append("<").append(restriction).append(">");
                        buf.append(xmlEncode(value));
                        buf.append("</").append(restriction).append(">");
                    }
                }
            }
        }
        buf.append("      </RestrictionList>\n"
            + "    </Restrictions>\n"
            + "    <Properties>\n"
            + "      <PropertyList>\n");

        // Add the datasource node only if this request requires it.
        if (metadataRequest.requiresDatasourceName()) {
            buf.append("        <DataSourceInfo>");
            buf.append(xmlEncode(context.olap4jConnection.getDataSourceInfo()));
            buf.append("</DataSourceInfo>\n");
        }

     // Add the catalog node only if this request requires it.
        if (metadataRequest.requiresCatalogName()) {
            buf.append("        <Catalog>");
            buf.append(xmlEncode(context.olap4jConnection.getCatalog()));
            buf.append("</Catalog>\n");
        }

        buf.append("        <Content>");
        buf.append(xmlEncode(content));
        buf.append("</Content>\n"
            + "      </PropertyList>\n"
            + "    </Properties>\n"
            + "    </Discover>\n"
            + "</SOAP-ENV:Body>\n"
            + "</SOAP-ENV:Envelope>");
        return buf.toString();
    }

    /**
     * Encodes a string for use in an XML CDATA section.
     *
     * @param value to be xml encoded
     * @return an XML encode string or the value is not required.
     */
    private static String xmlEncode(String value) {
        value = Olap4jUtil.replace(value, "&", "&amp;");
        value = Olap4jUtil.replace(value, "<", "&lt;");
        value = Olap4jUtil.replace(value, ">", "&gt;");
        value = Olap4jUtil.replace(value, "\"", "&quot;");
        value = Olap4jUtil.replace(value, "'", "&apos;");
        return value;
    }

    // ~ inner classes --------------------------------------------------------
    static class CatalogHandler
        extends HandlerImpl<XmlaOlap4jCatalog>
    {
        public void handle(
            Element row,
            Context context,
            List<XmlaOlap4jCatalog> list)
        {
            /*
            Example:

                    <row>
                        <CATALOG_NAME>FoodMart</CATALOG_NAME>
                        <DESCRIPTION>No description available</DESCRIPTION>
                        <ROLES>California manager,No HR Cube</ROLES>
                    </row>
             */
            String catalogName = XmlaOlap4jUtil.stringElement(row, "CATALOG_NAME");
            // Unused: DESCRIPTION, ROLES
            list.add(
                new XmlaOlap4jCatalog(
                    context.olap4jDatabaseMetaData, catalogName));
        }
    }

    static class CubeHandler extends HandlerImpl<XmlaOlap4jCube> {

        public void handle(Element row, Context context,
            List<XmlaOlap4jCube> list)
            throws OlapException
        {
            /*
            Example:

                    <row>
                        <CATALOG_NAME>FoodMart</CATALOG_NAME>
                        <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                        <CUBE_NAME>HR</CUBE_NAME>
                        <CUBE_TYPE>CUBE</CUBE_TYPE>
                        <IS_DRILLTHROUGH_ENABLED>true</IS_DRILLTHROUGH_ENABLED>
                        <IS_WRITE_ENABLED>false</IS_WRITE_ENABLED>
                        <IS_LINKABLE>false</IS_LINKABLE>
                        <IS_SQL_ENABLED>false</IS_SQL_ENABLED>
                        <DESCRIPTION>FoodMart Schema - HR Cube</DESCRIPTION>
                    </row>
             */
            // Unused: CATALOG_NAME, SCHEMA_NAME, CUBE_TYPE,
            //   IS_DRILLTHROUGH_ENABLED, IS_WRITE_ENABLED, IS_LINKABLE,
            //   IS_SQL_ENABLED
            String cubeName = stringElement(row, "CUBE_NAME");
            String description = stringElement(row, "DESCRIPTION");
            list.add(
                new XmlaOlap4jCube(
                    context.olap4jSchema, cubeName, description));
        }
    }

    static class DimensionHandler extends HandlerImpl<XmlaOlap4jDimension> {
        public void handle(
            Element row,
            Context context,
            List<XmlaOlap4jDimension> list)
        {
            /*
            Example:

            <row>
                <CATALOG_NAME>FoodMart</CATALOG_NAME>
                <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                <CUBE_NAME>HR</CUBE_NAME>
                <DIMENSION_NAME>Department</DIMENSION_NAME>
                <DIMENSION_UNIQUE_NAME>[Department]</DIMENSION_UNIQUE_NAME>
                <DIMENSION_CAPTION>Department</DIMENSION_CAPTION>
                <DIMENSION_ORDINAL>6</DIMENSION_ORDINAL>
                <DIMENSION_TYPE>3</DIMENSION_TYPE>
                <DIMENSION_CARDINALITY>13</DIMENSION_CARDINALITY>
                <DEFAULT_HIERARCHY>[Department]</DEFAULT_HIERARCHY>
                <DESCRIPTION>HR Cube - Department Dimension</DESCRIPTION>
                <IS_VIRTUAL>false</IS_VIRTUAL>
                <IS_READWRITE>false</IS_READWRITE>
                <DIMENSION_UNIQUE_SETTINGS>0</DIMENSION_UNIQUE_SETTINGS>
                <DIMENSION_IS_VISIBLE>true</DIMENSION_IS_VISIBLE>
            </row>

             */
            final String dimensionName =
                stringElement(row, "DIMENSION_NAME");
            final String dimensionUniqueName =
                stringElement(row, "DIMENSION_UNIQUE_NAME");
            final String dimensionCaption =
                stringElement(row, "DIMENSION_CAPTION");
            final String description =
                stringElement(row, "DESCRIPTION");
            final int dimensionType =
                integerElement(row, "DIMENSION_TYPE");
            final Dimension.Type type =
                Dimension.Type.forXmlaOrdinal(dimensionType);
            final String defaultHierarchyUniqueName =
                stringElement(row, "DEFAULT_HIERARCHY");
            list.add(
                new XmlaOlap4jDimension(
                    context.olap4jCube, dimensionUniqueName, dimensionName,
                    dimensionCaption, description, type,
                    defaultHierarchyUniqueName));
        }
    }

    static class HierarchyHandler extends HandlerImpl<XmlaOlap4jHierarchy> {
        public void handle(
            Element row, Context context, List<XmlaOlap4jHierarchy> list)
            throws OlapException
        {
            /*
            Example:

            <row>
                <CATALOG_NAME>FoodMart</CATALOG_NAME>
                <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                <CUBE_NAME>Sales</CUBE_NAME>
                <DIMENSION_UNIQUE_NAME>[Customers]</DIMENSION_UNIQUE_NAME>
                <HIERARCHY_NAME>Customers</HIERARCHY_NAME>
                <HIERARCHY_UNIQUE_NAME>[Customers]</HIERARCHY_UNIQUE_NAME>
                <HIERARCHY_CAPTION>Customers</HIERARCHY_CAPTION>
                <DIMENSION_TYPE>3</DIMENSION_TYPE>
                <HIERARCHY_CARDINALITY>10407</HIERARCHY_CARDINALITY>
                <DEFAULT_MEMBER>[Customers].[All Customers]</DEFAULT_MEMBER>
                <ALL_MEMBER>[Customers].[All Customers]</ALL_MEMBER>
                <DESCRIPTION>Sales Cube - Customers Hierarchy</DESCRIPTION>
                <STRUCTURE>0</STRUCTURE>
                <IS_VIRTUAL>false</IS_VIRTUAL>
                <IS_READWRITE>false</IS_READWRITE>
                <DIMENSION_UNIQUE_SETTINGS>0</DIMENSION_UNIQUE_SETTINGS>
                <DIMENSION_IS_VISIBLE>true</DIMENSION_IS_VISIBLE>
                <HIERARCHY_ORDINAL>9</HIERARCHY_ORDINAL>
                <DIMENSION_IS_SHARED>true</DIMENSION_IS_SHARED>
                <PARENT_CHILD>false</PARENT_CHILD>
            </row>

             */
            final String hierarchyName =
                stringElement(row, "HIERARCHY_NAME");
            final String hierarchyUniqueName =
                stringElement(row, "HIERARCHY_UNIQUE_NAME");
            final String hierarchyCaption =
                stringElement(row, "HIERARCHY_CAPTION");
            final String description =
                stringElement(row, "DESCRIPTION");
            final String allMember =
                stringElement(row, "ALL_MEMBER");
            final String defaultMemberUniqueName =
                stringElement(row, "DEFAULT_MEMBER");
            list.add(
                new XmlaOlap4jHierarchy(
                    context.getDimension(row), hierarchyUniqueName,
                    hierarchyName, hierarchyCaption, description,
                    allMember != null, defaultMemberUniqueName));
        }
    }

    static class LevelHandler extends HandlerImpl<XmlaOlap4jLevel> {
        public static final int MDLEVEL_TYPE_CALCULATED = 0x0002;

        public void handle(
            Element row,
            Context context,
            List<XmlaOlap4jLevel> list)
        {
            /*
            Example:

            <row>
                <CATALOG_NAME>FoodMart</CATALOG_NAME>
                <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                <CUBE_NAME>Sales</CUBE_NAME>
                <DIMENSION_UNIQUE_NAME>[Customers]</DIMENSION_UNIQUE_NAME>
                <HIERARCHY_UNIQUE_NAME>[Customers]</HIERARCHY_UNIQUE_NAME>
                <LEVEL_NAME>(All)</LEVEL_NAME>
                <LEVEL_UNIQUE_NAME>[Customers].[(All)]</LEVEL_UNIQUE_NAME>
                <LEVEL_CAPTION>(All)</LEVEL_CAPTION>
                <LEVEL_NUMBER>0</LEVEL_NUMBER>
                <LEVEL_CARDINALITY>1</LEVEL_CARDINALITY>
                <LEVEL_TYPE>1</LEVEL_TYPE>
                <CUSTOM_ROLLUP_SETTINGS>0</CUSTOM_ROLLUP_SETTINGS>
                <LEVEL_UNIQUE_SETTINGS>3</LEVEL_UNIQUE_SETTINGS>
                <LEVEL_IS_VISIBLE>true</LEVEL_IS_VISIBLE>
                <DESCRIPTION>Sales Cube - Customers Hierarchy - (All)
                Level</DESCRIPTION>
            </row>

             */
            final String levelName =
                stringElement(row, "LEVEL_NAME");
            final String levelUniqueName =
                stringElement(row, "LEVEL_UNIQUE_NAME");
            final String levelCaption =
                stringElement(row, "LEVEL_CAPTION");
            final String description =
                stringElement(row, "DESCRIPTION");
            final int levelNumber =
                integerElement(row, "LEVEL_NUMBER");
            final Integer levelTypeCode = integerElement(row, "LEVEL_TYPE");
            final Level.Type levelType =
                Level.Type.forXmlaOrdinal(levelTypeCode);
            boolean calculated = (levelTypeCode & MDLEVEL_TYPE_CALCULATED) != 0;
            final int levelCardinality =
                integerElement(row, "LEVEL_CARDINALITY");
            list.add(
                new XmlaOlap4jLevel(
                    context.getHierarchy(row), levelUniqueName, levelName,
                    levelCaption, description, levelNumber, levelType,
                    calculated, levelCardinality));
        }
    }

    static class MeasureHandler extends HandlerImpl<XmlaOlap4jMeasure> {
        public void handle(
            Element row,
            Context context,
            List<XmlaOlap4jMeasure> list)
            throws OlapException
        {
            /*
            Example:

            <row>
                <CATALOG_NAME>FoodMart</CATALOG_NAME>
                <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                <CUBE_NAME>Sales</CUBE_NAME>
                <MEASURE_NAME>Profit</MEASURE_NAME>
                <MEASURE_UNIQUE_NAME>[Measures].[Profit]</MEASURE_UNIQUE_NAME>
                <MEASURE_CAPTION>Profit</MEASURE_CAPTION>
                <MEASURE_AGGREGATOR>127</MEASURE_AGGREGATOR>
                <DATA_TYPE>130</DATA_TYPE>
                <MEASURE_IS_VISIBLE>true</MEASURE_IS_VISIBLE>
                <DESCRIPTION>Sales Cube - Profit Member</DESCRIPTION>
            </row>

             */
            final String measureName =
                stringElement(row, "MEASURE_NAME");
            final String measureUniqueName =
                stringElement(row, "MEASURE_UNIQUE_NAME");
            final String measureCaption =
                stringElement(row, "MEASURE_CAPTION");
            final String description =
                stringElement(row, "DESCRIPTION");
            final Measure.Aggregator measureAggregator =
                Measure.Aggregator.forXmlaOrdinal(
                    integerElement(row, "MEASURE_AGGREGATOR"));
            final Datatype datatype =
                Datatype.forXmlaOrdinal(
                    integerElement(row, "DATA_TYPE"));
            final boolean measureIsVisible =
                booleanElement(row, "MEASURE_IS_VISIBLE");
            // REVIEW: We're making a lot of assumptions about where Measures
            // live.
            final XmlaOlap4jLevel measuresLevel =
                (XmlaOlap4jLevel)
                    context.getCube(row).getHierarchies().get("Measures")
                        .getLevels().get(0);

            // Every measure is a member. MDSCHEMA_MEASURES does not return all
            // properties of measures, so lookup the corresponding member. In
            // particular, we need the ordinal.
            if (list.isEmpty()) {
                // First call this method, ask for all members of the measures
                // level. This should ensures that we get all members in one
                // round trip.
                final List<Member> measureMembers = measuresLevel.getMembers();
                Olap4jUtil.discard(measureMembers);
            }
            Member member =
                context.getCube(row).getMetadataReader()
                    .lookupMemberByUniqueName(
                        measureUniqueName);
            final int ordinal;
            if (member != null) {
                ordinal = member.getOrdinal();
            } else {
                ordinal = -1;
            }

            list.add(
                new XmlaOlap4jMeasure(
                    measuresLevel, measureUniqueName, measureName,
                    measureCaption, description, null, measureAggregator,
                    datatype, measureIsVisible, ordinal));
        }

        public void sortList(List<XmlaOlap4jMeasure> list) {
            Collections.sort(
                list,
                new Comparator<XmlaOlap4jMeasure>() {
                    public int compare(
                        XmlaOlap4jMeasure o1,
                        XmlaOlap4jMeasure o2)
                    {
                        return o1.getOrdinal() - o2.getOrdinal();
                    }
                }
            );
        }
    }

    static class MemberHandler extends HandlerImpl<XmlaOlap4jMember> {

        /**
         * Collection of nodes to ignore because they represent standard
         * built-in properties of Members.
         */
        private static final Set<String> EXCLUDED_PROPERTY_NAMES =
            new HashSet<String>(
                Arrays.asList(
                    Property.StandardMemberProperty.CATALOG_NAME.name(),
                    Property.StandardMemberProperty.CUBE_NAME.name(),
                    Property.StandardMemberProperty.DIMENSION_UNIQUE_NAME
                        .name(),
                    Property.StandardMemberProperty.HIERARCHY_UNIQUE_NAME
                        .name(),
                    Property.StandardMemberProperty.LEVEL_UNIQUE_NAME.name(),
                    Property.StandardMemberProperty.PARENT_LEVEL.name(),
                    Property.StandardMemberProperty.PARENT_COUNT.name(),
                    Property.StandardMemberProperty.MEMBER_KEY.name(),
                    Property.StandardMemberProperty.IS_PLACEHOLDERMEMBER.name(),
                    Property.StandardMemberProperty.IS_DATAMEMBER.name(),
                    Property.StandardMemberProperty.LEVEL_NUMBER.name(),
                    Property.StandardMemberProperty.MEMBER_ORDINAL.name(),
                    Property.StandardMemberProperty.MEMBER_UNIQUE_NAME.name(),
                    Property.StandardMemberProperty.MEMBER_NAME.name(),
                    Property.StandardMemberProperty.PARENT_UNIQUE_NAME.name(),
                    Property.StandardMemberProperty.MEMBER_TYPE.name(),
                    Property.StandardMemberProperty.MEMBER_CAPTION.name(),
                    Property.StandardMemberProperty.CHILDREN_CARDINALITY.name(),
                    Property.StandardMemberProperty.DEPTH.name()));

        public void handle(
            Element row,
            Context context,
            List<XmlaOlap4jMember> list)
        {
            /*
            Example:

            <row>
                <CATALOG_NAME>FoodMart</CATALOG_NAME>
                <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                <CUBE_NAME>Sales</CUBE_NAME>
                <DIMENSION_UNIQUE_NAME>[Gender]</DIMENSION_UNIQUE_NAME>
                <HIERARCHY_UNIQUE_NAME>[Gender]</HIERARCHY_UNIQUE_NAME>
                <LEVEL_UNIQUE_NAME>[Gender].[Gender]</LEVEL_UNIQUE_NAME>
                <LEVEL_NUMBER>1</LEVEL_NUMBER>
                <MEMBER_ORDINAL>1</MEMBER_ORDINAL>
                <MEMBER_NAME>F</MEMBER_NAME>
                <MEMBER_UNIQUE_NAME>[Gender].[F]</MEMBER_UNIQUE_NAME>
                <MEMBER_TYPE>1</MEMBER_TYPE>
                <MEMBER_CAPTION>F</MEMBER_CAPTION>
                <CHILDREN_CARDINALITY>0</CHILDREN_CARDINALITY>
                <PARENT_LEVEL>0</PARENT_LEVEL>
                <PARENT_UNIQUE_NAME>[Gender].[All Gender]</PARENT_UNIQUE_NAME>
                <PARENT_COUNT>1</PARENT_COUNT>
                <DEPTH>1</DEPTH>          <!-- mondrian-specific -->
            </row>

             */
            if (false) {
            int levelNumber =
                integerElement(
                    row,
                    Property.StandardMemberProperty.LEVEL_NUMBER.name());
            }
            int memberOrdinal =
                integerElement(
                    row,
                    Property.StandardMemberProperty.MEMBER_ORDINAL.name());
            String memberUniqueName =
                stringElement(
                    row,
                    Property.StandardMemberProperty.MEMBER_UNIQUE_NAME.name());
            String memberName =
                stringElement(
                    row,
                    Property.StandardMemberProperty.MEMBER_NAME.name());
            String parentUniqueName =
                stringElement(
                    row,
                    Property.StandardMemberProperty.PARENT_UNIQUE_NAME.name());
            Member.Type memberType =
                Member.Type.values()[
                    integerElement(
                        row,
                        Property.StandardMemberProperty.MEMBER_TYPE.name())];
            String memberCaption =
                stringElement(
                    row,
                    Property.StandardMemberProperty.MEMBER_CAPTION.name());
            int childrenCardinality =
                integerElement(
                    row,
                    Property.StandardMemberProperty.CHILDREN_CARDINALITY
                        .name());
            // If this member is a measure, we want to return an object that
            // implements the Measure interface to all API calls. But we also
            // need to retrieve the properties that occur in MDSCHEMA_MEMBERS
            // that are not available in MDSCHEMA_MEASURES, so we create a
            // member for internal use.
            XmlaOlap4jMember member =
                new XmlaOlap4jMember(
                    context.getLevel(row), memberUniqueName, memberName,
                    memberCaption, "", parentUniqueName, memberType,
                    childrenCardinality, memberOrdinal);
            addUserDefinedDimensionProperties(row, member);

            // Usually members have the same depth as their level. (Ragged and
            // parent-child hierarchies are an exception.) Only store depth for
            // the unusual ones.
            final Integer depth =
                integerElement(
                    row,
                    Property.StandardMemberProperty.DEPTH.name());
            if (depth != null
                && depth.intValue() != member.getLevel().getDepth())
            {
                member.setProperty(
                    Property.StandardMemberProperty.DEPTH,
                    depth);
            }
            list.add(member);
        }

        private void addUserDefinedDimensionProperties(
            Element row,
            XmlaOlap4jMember member)
        {
            NodeList nodes = row.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (EXCLUDED_PROPERTY_NAMES.contains(node.getLocalName())) {
                    continue;
                }
                for (Property property : member.getLevel().getProperties()) {
                    if (property instanceof XmlaOlap4jProperty
                        && property.getName().equalsIgnoreCase(
                        node.getLocalName()))
                    {
                        member.setProperty(property, node.getTextContent());
                    }
                }
            }
        }
    }

    static class NamedSetHandler extends HandlerImpl<XmlaOlap4jNamedSet> {
        public void handle(
            Element row,
            Context context,
            List<XmlaOlap4jNamedSet> list)
        {
            /*
            Example:

            <row>
                <CATALOG_NAME>FoodMart</CATALOG_NAME>
                <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                <CUBE_NAME>Warehouse</CUBE_NAME>
                <SET_NAME>[Top Sellers]</SET_NAME>
                <SCOPE>1</SCOPE>
            </row>

             */
            final String setName =
                stringElement(row, "SET_NAME");
            list.add(
                new XmlaOlap4jNamedSet(
                    context.getCube(row), setName));
        }
    }

    static class SchemaHandler extends HandlerImpl<XmlaOlap4jSchema> {
        public void handle(
            Element row,
            Context context,
            List<XmlaOlap4jSchema> list)
            throws OlapException
        {
            /*
            Example:

            <row>
                <CATALOG_NAME>LOCALDB</CATLAOG_NAME>
                <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                <SCHEMA_OWNER>dbo</SCHEMA_OWNER>
            </row>
             */
            String schemaName = stringElement(row, "SCHEMA_NAME");
            list.add(
                new XmlaOlap4jSchema(
                    context.getCatalog(row),
                    (schemaName == null) ? "" : schemaName));
        }
    }

    static class CatalogSchemaHandler extends HandlerImpl<XmlaOlap4jSchema> {

        private String catalogName;

        public CatalogSchemaHandler(String catalogName) {
            super();
            if (catalogName == null) {
                throw new RuntimeException("The CatalogSchemaHandler handler requires a catalog name.");
            }
            this.catalogName = catalogName;
        }

        public void handle(
            Element row,
            Context context,
            List<XmlaOlap4jSchema> list)
            throws OlapException
        {
            /*
            Example:

            <row>
                <CATALOG_NAME>CatalogName</CATLAOG_NAME>
                <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                <SCHEMA_OWNER>dbo</SCHEMA_OWNER>
            </row>
             */

            /*
             * We are looking for a schema name from the cubes query restricted
             * on the catalog name. Some servers don't support nor include the
             * SCHEMA_NAME column in its response. If it's null, we convert it
             * to an empty string as to not cause problems later on.
             */
            String schemaName = stringElement(row, "SCHEMA_NAME");
            String catalogName = stringElement(row, "CATALOG_NAME");

            if (this.catalogName.equals(catalogName)) {
                list.add(
                    new XmlaOlap4jSchema(
                        context.getCatalog(row),
                        (schemaName == null) ? "" : schemaName));
            }
        }
    }

    static class PropertyHandler extends HandlerImpl<XmlaOlap4jProperty> {
        public void handle(
            Element row,
            Context context, List<XmlaOlap4jProperty> list) throws OlapException
        {
            /*
            Example:

            <row>
                <CATALOG_NAME>FoodMart</CATALOG_NAME>
                <SCHEMA_NAME>FoodMart</SCHEMA_NAME>
                <CUBE_NAME>HR</CUBE_NAME>
                <DIMENSION_UNIQUE_NAME>[Store]</DIMENSION_UNIQUE_NAME>
                <HIERARCHY_UNIQUE_NAME>[Store]</HIERARCHY_UNIQUE_NAME>
                <LEVEL_UNIQUE_NAME>[Store].[Store Name]</LEVEL_UNIQUE_NAME>
                <PROPERTY_NAME>Store Manager</PROPERTY_NAME>
                <PROPERTY_CAPTION>Store Manager</PROPERTY_CAPTION>
                <PROPERTY_TYPE>1</PROPERTY_TYPE>
                <DATA_TYPE>130</DATA_TYPE>
                <PROPERTY_CONTENT_TYPE>0</PROPERTY_CONTENT_TYPE>
                <DESCRIPTION>HR Cube - Store Hierarchy - Store
                    Name Level - Store Manager Property</DESCRIPTION>
            </row>
             */
            String description = stringElement(row, "DESCRIPTION");
            String uniqueName = stringElement(row, "DESCRIPTION");
            String caption = stringElement(row, "PROPERTY_CAPTION");
            String name = stringElement(row, "PROPERTY_NAME");
            Datatype dataType =
                Datatype.forXmlaOrdinal(
                    integerElement(row, "DATA_TYPE"));
            final Integer contentTypeOrdinal =
                integerElement(row, "PROPERTY_CONTENT_TYPE");
            Property.ContentType contentType =
                contentTypeOrdinal == null
                    ? null
                    : Property.ContentType.forXmlaOrdinal(contentTypeOrdinal);
            int propertyType = integerElement(row, "PROPERTY_TYPE");
            Set<Property.TypeFlag> type =
                Property.TypeFlag.forMask(propertyType);
            list.add(
                new XmlaOlap4jProperty(
                    uniqueName, name, caption, description, dataType, type,
                    contentType));
        }
    }

    /**
     * Callback for converting XMLA results into metadata elements.
     */
    interface Handler<T extends Named> {
        /**
         * Converts an XML element from an XMLA result set into a metadata
         * element and appends it to a list of metadata elements.
         *
         * @param row XMLA element
         *
         * @param context Context (schema, cube, dimension, etc.) that the
         * request was executed in and that the element will belong to
         *
         * @param list List of metadata elements to append new metadata element
         *
         * @throws OlapException on error
         */
        void handle(
            Element row,
            Context context,
            List<T> list) throws OlapException;

        /**
         * Sorts a list of metadata elements.
         *
         * <p>For most element types, the order returned by XMLA is correct, and
         * this method will no-op.
         *
         * @param list List of metadata elements
         */
        void sortList(List<T> list);
    }

    static abstract class HandlerImpl<T extends Named> implements Handler<T> {
        public void sortList(List<T> list) {
            // do nothing - assume XMLA returned list in correct order
        }
    }

    static class Context {
        final XmlaOlap4jConnection olap4jConnection;
        final XmlaOlap4jDatabaseMetaData olap4jDatabaseMetaData;
        final XmlaOlap4jCatalog olap4jCatalog;
        final XmlaOlap4jSchema olap4jSchema;
        final XmlaOlap4jCube olap4jCube;
        final XmlaOlap4jDimension olap4jDimension;
        final XmlaOlap4jHierarchy olap4jHierarchy;
        final XmlaOlap4jLevel olap4jLevel;

        /**
         * Creates a Context.
         *
         * @param olap4jConnection Connection (must not be null)
         * @param olap4jDatabaseMetaData DatabaseMetaData (may be null)
         * @param olap4jCatalog Catalog (may be null if DatabaseMetaData is
         * null)
         * @param olap4jSchema Schema (may be null if Catalog is null)
         * @param olap4jCube Cube (may be null if Schema is null)
         * @param olap4jDimension Dimension (may be null if Cube is null)
         * @param olap4jHierarchy Hierarchy (may be null if Dimension is null)
         * @param olap4jLevel Level (may be null if Hierarchy is null)
         */
        Context(
            XmlaOlap4jConnection olap4jConnection,
            XmlaOlap4jDatabaseMetaData olap4jDatabaseMetaData,
            XmlaOlap4jCatalog olap4jCatalog,
            XmlaOlap4jSchema olap4jSchema,
            XmlaOlap4jCube olap4jCube,
            XmlaOlap4jDimension olap4jDimension,
            XmlaOlap4jHierarchy olap4jHierarchy,
            XmlaOlap4jLevel olap4jLevel)
        {
            this.olap4jConnection = olap4jConnection;
            this.olap4jDatabaseMetaData = olap4jDatabaseMetaData;
            this.olap4jCatalog = olap4jCatalog;
            this.olap4jSchema = olap4jSchema;
            this.olap4jCube = olap4jCube;
            this.olap4jDimension = olap4jDimension;
            this.olap4jHierarchy = olap4jHierarchy;
            this.olap4jLevel = olap4jLevel;
            assert (olap4jDatabaseMetaData != null || olap4jCatalog == null)
                && (olap4jCatalog != null || olap4jSchema == null)
                && (olap4jSchema != null || olap4jCube == null)
                && (olap4jCube != null || olap4jDimension == null)
                && (olap4jDimension != null || olap4jHierarchy == null)
                && (olap4jHierarchy != null || olap4jLevel == null);
        }

        /**
         * Shorthand way to create a Context at Cube level or finer.
         *
         * @param olap4jCube Cube (must not be null)
         * @param olap4jDimension Dimension (may be null)
         * @param olap4jHierarchy Hierarchy (may be null if Dimension is null)
         * @param olap4jLevel Level (may be null if Hierarchy is null)
         */
        Context(
            XmlaOlap4jCube olap4jCube,
            XmlaOlap4jDimension olap4jDimension,
            XmlaOlap4jHierarchy olap4jHierarchy,
            XmlaOlap4jLevel olap4jLevel)
        {
            this(
                olap4jCube.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData
                    .olap4jConnection,
                olap4jCube.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData,
                olap4jCube.olap4jSchema.olap4jCatalog,
                olap4jCube.olap4jSchema,
                olap4jCube,
                olap4jDimension,
                olap4jHierarchy,
                olap4jLevel);
        }

        /**
         * Shorthand way to create a Context at Level level.
         *
         * @param olap4jLevel Level (must not be null)
         */
        Context(XmlaOlap4jLevel olap4jLevel)
        {
            this(
                olap4jLevel.olap4jHierarchy.olap4jDimension.olap4jCube,
                olap4jLevel.olap4jHierarchy.olap4jDimension,
                olap4jLevel.olap4jHierarchy,
                olap4jLevel);
        }

        XmlaOlap4jHierarchy getHierarchy(Element row) {
            if (olap4jHierarchy != null) {
                return olap4jHierarchy;
            }
            final String hierarchyUniqueName =
                stringElement(row, "HIERARCHY_UNIQUE_NAME");
            return getCube(row).hierarchiesByUname.get(hierarchyUniqueName);
        }

        XmlaOlap4jCube getCube(Element row) {
            if (olap4jCube != null) {
                return olap4jCube;
            }
            throw new UnsupportedOperationException(); // todo:
        }

        XmlaOlap4jDimension getDimension(Element row) {
            if (olap4jDimension != null) {
                return olap4jDimension;
            }
            final String dimensionUniqueName =
                stringElement(row, "DIMENSION_UNIQUE_NAME");
            return getCube(row).dimensionsByUname.get(dimensionUniqueName);
        }

        public XmlaOlap4jLevel getLevel(Element row) {
            if (olap4jLevel != null) {
                return olap4jLevel;
            }
            final String levelUniqueName =
                stringElement(row, "LEVEL_UNIQUE_NAME");
            return getCube(row).levelsByUname.get(levelUniqueName);
        }

        public XmlaOlap4jCatalog getCatalog(Element row) throws OlapException {
            if (olap4jCatalog != null) {
                return olap4jCatalog;
            }
            final String catalogName =
                stringElement(row, "CATALOG_NAME");
            return (XmlaOlap4jCatalog) olap4jConnection.getCatalogs().get(
                catalogName);
        }
    }

    enum MetadataRequest {
        DISCOVER_DATASOURCES(
            new MetadataColumn("DataSourceName"),
            new MetadataColumn("DataSourceDescription"),
            new MetadataColumn("URL"),
            new MetadataColumn("DataSourceInfo"),
            new MetadataColumn("ProviderName"),
            new MetadataColumn("ProviderType"),
            new MetadataColumn("AuthenticationMode")),
        DISCOVER_SCHEMA_ROWSETS(
            new MetadataColumn("SchemaName"),
            new MetadataColumn("SchemaGuid"),
            new MetadataColumn("Restrictions"),
            new MetadataColumn("Description")),
        DISCOVER_ENUMERATORS(
            new MetadataColumn("EnumName"),
            new MetadataColumn("EnumDescription"),
            new MetadataColumn("EnumType"),
            new MetadataColumn("ElementName"),
            new MetadataColumn("ElementDescription"),
            new MetadataColumn("ElementValue")),
        DISCOVER_PROPERTIES(
            new MetadataColumn("PropertyName"),
            new MetadataColumn("PropertyDescription"),
            new MetadataColumn("PropertyType"),
            new MetadataColumn("PropertyAccessType"),
            new MetadataColumn("IsRequired"),
            new MetadataColumn("Value")),
        DISCOVER_KEYWORDS(
            new MetadataColumn("Keyword")),
        DISCOVER_LITERALS(
            new MetadataColumn("LiteralName"),
            new MetadataColumn("LiteralValue"),
            new MetadataColumn("LiteralInvalidChars"),
            new MetadataColumn("LiteralInvalidStartingChars"),
            new MetadataColumn("LiteralMaxLength")),
        DBSCHEMA_CATALOGS(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("DESCRIPTION"),
            new MetadataColumn("ROLES"),
            new MetadataColumn("DATE_MODIFIED")),
        DBSCHEMA_COLUMNS(
            new MetadataColumn("TABLE_CATALOG"),
            new MetadataColumn("TABLE_SCHEMA"),
            new MetadataColumn("TABLE_NAME"),
            new MetadataColumn("COLUMN_NAME"),
            new MetadataColumn("ORDINAL_POSITION"),
            new MetadataColumn("COLUMN_HAS_DEFAULT"),
            new MetadataColumn("COLUMN_FLAGS"),
            new MetadataColumn("IS_NULLABLE"),
            new MetadataColumn("DATA_TYPE"),
            new MetadataColumn("CHARACTER_MAXIMUM_LENGTH"),
            new MetadataColumn("CHARACTER_OCTET_LENGTH"),
            new MetadataColumn("NUMERIC_PRECISION"),
            new MetadataColumn("NUMERIC_SCALE")),
        DBSCHEMA_PROVIDER_TYPES(
            new MetadataColumn("TYPE_NAME"),
            new MetadataColumn("DATA_TYPE"),
            new MetadataColumn("COLUMN_SIZE"),
            new MetadataColumn("LITERAL_PREFIX"),
            new MetadataColumn("LITERAL_SUFFIX"),
            new MetadataColumn("IS_NULLABLE"),
            new MetadataColumn("CASE_SENSITIVE"),
            new MetadataColumn("SEARCHABLE"),
            new MetadataColumn("UNSIGNED_ATTRIBUTE"),
            new MetadataColumn("FIXED_PREC_SCALE"),
            new MetadataColumn("AUTO_UNIQUE_VALUE"),
            new MetadataColumn("IS_LONG"),
            new MetadataColumn("BEST_MATCH")),
        DBSCHEMA_TABLES(
            new MetadataColumn("TABLE_CATALOG"),
            new MetadataColumn("TABLE_SCHEMA"),
            new MetadataColumn("TABLE_NAME"),
            new MetadataColumn("TABLE_TYPE"),
            new MetadataColumn("TABLE_GUID"),
            new MetadataColumn("DESCRIPTION"),
            new MetadataColumn("TABLE_PROPID"),
            new MetadataColumn("DATE_CREATED"),
            new MetadataColumn("DATE_MODIFIED")),
        DBSCHEMA_TABLES_INFO(
            new MetadataColumn("TABLE_CATALOG"),
            new MetadataColumn("TABLE_SCHEMA"),
            new MetadataColumn("TABLE_NAME"),
            new MetadataColumn("TABLE_TYPE"),
            new MetadataColumn("TABLE_GUID"),
            new MetadataColumn("BOOKMARKS"),
            new MetadataColumn("BOOKMARK_TYPE"),
            new MetadataColumn("BOOKMARK_DATATYPE"),
            new MetadataColumn("BOOKMARK_MAXIMUM_LENGTH"),
            new MetadataColumn("BOOKMARK_INFORMATION"),
            new MetadataColumn("TABLE_VERSION"),
            new MetadataColumn("CARDINALITY"),
            new MetadataColumn("DESCRIPTION"),
            new MetadataColumn("TABLE_PROPID")),
        DBSCHEMA_SCHEMATA(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("SCHEMA_OWNER")),
        MDSCHEMA_ACTIONS(
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("CUBE_NAME"),
            new MetadataColumn("ACTION_NAME"),
            new MetadataColumn("COORDINATE"),
            new MetadataColumn("COORDINATE_TYPE")),
        MDSCHEMA_CUBES(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("CUBE_NAME"),
            new MetadataColumn("CUBE_TYPE"),
            new MetadataColumn("CUBE_GUID"),
            new MetadataColumn("CREATED_ON"),
            new MetadataColumn("LAST_SCHEMA_UPDATE"),
            new MetadataColumn("SCHEMA_UPDATED_BY"),
            new MetadataColumn("LAST_DATA_UPDATE"),
            new MetadataColumn("DATA_UPDATED_BY"),
            new MetadataColumn("IS_DRILLTHROUGH_ENABLED"),
            new MetadataColumn("IS_WRITE_ENABLED"),
            new MetadataColumn("IS_LINKABLE"),
            new MetadataColumn("IS_SQL_ENABLED"),
            new MetadataColumn("DESCRIPTION")),
        MDSCHEMA_DIMENSIONS(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("CUBE_NAME"),
            new MetadataColumn("DIMENSION_NAME"),
            new MetadataColumn("DIMENSION_UNIQUE_NAME"),
            new MetadataColumn("DIMENSION_GUID"),
            new MetadataColumn("DIMENSION_CAPTION"),
            new MetadataColumn("DIMENSION_ORDINAL"),
            new MetadataColumn("DIMENSION_TYPE"),
            new MetadataColumn("DIMENSION_CARDINALITY"),
            new MetadataColumn("DEFAULT_HIERARCHY"),
            new MetadataColumn("DESCRIPTION"),
            new MetadataColumn("IS_VIRTUAL"),
            new MetadataColumn("IS_READWRITE"),
            new MetadataColumn("DIMENSION_UNIQUE_SETTINGS"),
            new MetadataColumn("DIMENSION_MASTER_UNIQUE_NAME"),
            new MetadataColumn("DIMENSION_IS_VISIBLE")),
        MDSCHEMA_FUNCTIONS(
            new MetadataColumn("FUNCTION_NAME"),
            new MetadataColumn("DESCRIPTION"),
            new MetadataColumn("PARAMETER_LIST"),
            new MetadataColumn("RETURN_TYPE"),
            new MetadataColumn("ORIGIN"),
            new MetadataColumn("INTERFACE_NAME"),
            new MetadataColumn("LIBRARY_NAME"),
            new MetadataColumn("CAPTION")),
        MDSCHEMA_HIERARCHIES(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("CUBE_NAME"),
            new MetadataColumn("DIMENSION_UNIQUE_NAME"),
            new MetadataColumn("HIERARCHY_NAME"),
            new MetadataColumn("HIERARCHY_UNIQUE_NAME"),
            new MetadataColumn("HIERARCHY_GUID"),
            new MetadataColumn("HIERARCHY_CAPTION"),
            new MetadataColumn("DIMENSION_TYPE"),
            new MetadataColumn("HIERARCHY_CARDINALITY"),
            new MetadataColumn("DEFAULT_MEMBER"),
            new MetadataColumn("ALL_MEMBER"),
            new MetadataColumn("DESCRIPTION"),
            new MetadataColumn("STRUCTURE"),
            new MetadataColumn("IS_VIRTUAL"),
            new MetadataColumn("IS_READWRITE"),
            new MetadataColumn("DIMENSION_UNIQUE_SETTINGS"),
            new MetadataColumn("DIMENSION_IS_VISIBLE"),
            new MetadataColumn("HIERARCHY_ORDINAL"),
            new MetadataColumn("DIMENSION_IS_SHARED"),
            new MetadataColumn("PARENT_CHILD")),
        MDSCHEMA_LEVELS(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("CUBE_NAME"),
            new MetadataColumn("DIMENSION_UNIQUE_NAME"),
            new MetadataColumn("HIERARCHY_UNIQUE_NAME"),
            new MetadataColumn("LEVEL_NAME"),
            new MetadataColumn("LEVEL_UNIQUE_NAME"),
            new MetadataColumn("LEVEL_GUID"),
            new MetadataColumn("LEVEL_CAPTION"),
            new MetadataColumn("LEVEL_NUMBER"),
            new MetadataColumn("LEVEL_CARDINALITY"),
            new MetadataColumn("LEVEL_TYPE"),
            new MetadataColumn("CUSTOM_ROLLUP_SETTINGS"),
            new MetadataColumn("LEVEL_UNIQUE_SETTINGS"),
            new MetadataColumn("LEVEL_IS_VISIBLE"),
            new MetadataColumn("DESCRIPTION")),
        MDSCHEMA_MEASURES(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("CUBE_NAME"),
            new MetadataColumn("MEASURE_NAME"),
            new MetadataColumn("MEASURE_UNIQUE_NAME"),
            new MetadataColumn("MEASURE_CAPTION"),
            new MetadataColumn("MEASURE_GUID"),
            new MetadataColumn("MEASURE_AGGREGATOR"),
            new MetadataColumn("DATA_TYPE"),
            new MetadataColumn("MEASURE_IS_VISIBLE"),
            new MetadataColumn("LEVELS_LIST"),
            new MetadataColumn("DESCRIPTION")),
        MDSCHEMA_MEMBERS(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("CUBE_NAME"),
            new MetadataColumn("DIMENSION_UNIQUE_NAME"),
            new MetadataColumn("HIERARCHY_UNIQUE_NAME"),
            new MetadataColumn("LEVEL_UNIQUE_NAME"),
            new MetadataColumn("LEVEL_NUMBER"),
            new MetadataColumn("MEMBER_ORDINAL"),
            new MetadataColumn("MEMBER_NAME"),
            new MetadataColumn("MEMBER_UNIQUE_NAME"),
            new MetadataColumn("MEMBER_TYPE"),
            new MetadataColumn("MEMBER_GUID"),
            new MetadataColumn("MEMBER_CAPTION"),
            new MetadataColumn("CHILDREN_CARDINALITY"),
            new MetadataColumn("PARENT_LEVEL"),
            new MetadataColumn("PARENT_UNIQUE_NAME"),
            new MetadataColumn("PARENT_COUNT"),
            new MetadataColumn("TREE_OP"),
            new MetadataColumn("DEPTH")),
        MDSCHEMA_PROPERTIES(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("CUBE_NAME"),
            new MetadataColumn("DIMENSION_UNIQUE_NAME"),
            new MetadataColumn("HIERARCHY_UNIQUE_NAME"),
            new MetadataColumn("LEVEL_UNIQUE_NAME"),
            new MetadataColumn("MEMBER_UNIQUE_NAME"),
            new MetadataColumn("PROPERTY_NAME"),
            new MetadataColumn("PROPERTY_CAPTION"),
            new MetadataColumn("PROPERTY_TYPE"),
            new MetadataColumn("DATA_TYPE"),
            new MetadataColumn("PROPERTY_CONTENT_TYPE"),
            new MetadataColumn("DESCRIPTION")),
        MDSCHEMA_SETS(
            new MetadataColumn("CATALOG_NAME"),
            new MetadataColumn("SCHEMA_NAME"),
            new MetadataColumn("CUBE_NAME"),
            new MetadataColumn("SET_NAME"),
            new MetadataColumn("SCOPE"));

        final List<MetadataColumn> columns;
        final Map<String, MetadataColumn> columnsByName;

        /**
         * Creates a MetadataRequest.
         *
         * @param columns Columns
         */
        MetadataRequest(MetadataColumn... columns) {
            if (name().equals("DBSCHEMA_CATALOGS")) {
                // DatabaseMetaData.getCatalogs() is defined by JDBC not XMLA,
                // so has just one column. Ignore the 4 columns from XMLA.
                columns = new MetadataColumn[] {
                    new MetadataColumn("CATALOG_NAME", "TABLE_CAT")
                };
            } else if (name().equals("DBSCHEMA_SCHEMATA")) {
                // DatabaseMetaData.getCatalogs() is defined by JDBC not XMLA,
                // so has just one column. Ignore the 4 columns from XMLA.
                columns = new MetadataColumn[] {
                    new MetadataColumn("SCHEMA_NAME", "TABLE_SCHEM"),
                    new MetadataColumn("CATALOG_NAME", "TABLE_CAT")
                };
            }
            this.columns = UnmodifiableArrayList.asCopyOf(columns);
            final Map<String, MetadataColumn> map =
                new HashMap<String, MetadataColumn>();
            for (MetadataColumn column : columns) {
                map.put(column.name, column);
            }
            this.columnsByName = Collections.unmodifiableMap(map);
        }

        /**
         * Returns whether this request requires a
         * {@code &lt;DatasourceName&gt;} element.
         *
         * @return whether this request requires a DatasourceName element
         */
        public boolean requiresDatasourceName() {
            return this != DISCOVER_DATASOURCES;
        }

        /**
         * Returns whether this request requires a
         * {@code &lt;CatalogName&gt;} element.
         *
         * @return whether this request requires a CatalogName element
         */
        public boolean requiresCatalogName() {
            return (this != DBSCHEMA_CATALOGS && this != DISCOVER_DATASOURCES);
        }

        /**
         * Returns the column with a given name, or null if there is no such
         * column.
         *
         * @param name Column name
         * @return Column, or null if not found
         */
        public MetadataColumn getColumn(String name) {
            return columnsByName.get(name);
        }
    }

    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");

    static class MetadataColumn {
        final String name;
        final String xmlaName;

        MetadataColumn(String xmlaName, String name) {
            this.xmlaName = xmlaName;
            this.name = name;
        }

        MetadataColumn(String xmlaName) {
            this.xmlaName = xmlaName;
            String name = xmlaName;
            if (LOWERCASE_PATTERN.matcher(name).matches()) {
                name = Olap4jUtil.camelToUpper(name);
            }
            // VALUE is a SQL reserved word
            if (name.equals("VALUE")) {
                name = "PROPERTY_VALUE";
            }
            this.name = name;
        }
    }

    private static class XmlaOlap4jMdxValidator implements MdxValidator {
        private final OlapConnection connection;

        XmlaOlap4jMdxValidator(OlapConnection connection) {
            this.connection = connection;
        }

        public SelectNode validateSelect(
            SelectNode selectNode)
            throws OlapException
        {
            StringWriter sw = new StringWriter();
            selectNode.unparse(new ParseTreeWriter(new PrintWriter(sw)));
            String mdx = sw.toString();
            final XmlaOlap4jConnection olap4jConnection =
                (XmlaOlap4jConnection) connection;
            return selectNode;
        }
    }
}

// End XmlaOlap4jConnection.java
