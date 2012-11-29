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
package org.olap4j;

import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.driver.xmla.XmlaOlap4jServerInfos;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxyException;
import org.olap4j.test.TestContext;
import org.olap4j.test.TestContext.Tester;

import junit.framework.TestCase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Unit test for XMLA driver connections.
 */
public class XmlaConnectionTest extends TestCase {
    private TestContext testContext = TestContext.instance();
    private TestContext.Tester tester = testContext.getTester();
    public static final String DRIVER_CLASS_NAME =
        "org.olap4j.driver.xmla.XmlaOlap4jDriver";

    protected void setUp() throws Exception {
        super.setUp();
    }

    static class XmlaOlap4jProxyMock implements XmlaOlap4jProxy {
        public byte[] get(
            XmlaOlap4jServerInfos serverInfos,
            String request)
        {
            throw new RuntimeException("Non-Trivial Call!");
        }

        public String getEncodingCharsetName() {
            return "UTF-8";
        }

        public Future<byte[]> submit(
            XmlaOlap4jServerInfos serverInfos,
            String request)
        {
            throw new RuntimeException("Non-Trivial Call!");
        }
    }

    /**
     * Implementation of {@link org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy}
     * that delegates all requests to an underlying proxy.
     */
    public static class DelegatingTestProxy implements XmlaOlap4jProxy {
        protected final XmlaOlap4jProxy proxy;

        /**
         * Creates a DelegatingTestProxy.
         *
         * @param proxy Underlying proxy
         */
        public DelegatingTestProxy(XmlaOlap4jProxy proxy) {
            this.proxy = proxy;
        }

        public byte[] get(XmlaOlap4jServerInfos serverInfos, String request)
            throws XmlaOlap4jProxyException
        {
            return proxy.get(serverInfos, request);
        }

        public Future<byte[]> submit(
            XmlaOlap4jServerInfos serverInfos,
            String request)
        {
            return proxy.submit(serverInfos, request);
        }

        public String getEncodingCharsetName() {
            return proxy.getEncodingCharsetName();
        }
    }

    /**
     * Proxy that checks that the same request does not occur twice.
     */
    public static class DoubleSubmissionTestProxy extends DelegatingTestProxy {
        final Map<String, String> requests = new HashMap<String, String>();
        private static String PROXY_CLASS_NAME;

        /**
         * Creates a DoubleSubmissionTestProxy.
         *
         * <p>Public constructor is required because this is instantiated via
         * reflection.
         *
         * <p>Instantiates an underlying proxy whose name is given by
         * {@link #getProxyClassName()}.
         *
         * @param catalogNameUrls Collection of catalog names and the URL where
         * their catalog is to be found. For testing purposes, this should
         * contain a catalog called "FoodMart".
         *
         * @param urlString JDBC connect string; must begin with
         * "jdbc:mondrian:"
         */
        public DoubleSubmissionTestProxy(
            Map<String, String> catalogNameUrls,
            String urlString)
        {
            super(createProxy(catalogNameUrls, urlString));
        }

        /**
         * Sets the name of the class which is the underlying proxy.
         *
         * @param clazz Proxy class name
         */
        public static void setProxyClassName(String clazz) {
            PROXY_CLASS_NAME = clazz;
        }

        /**
         * Returns the name of the class which is the underlying proxy.
         *
         * @return Proxy class name
         */
        public static String getProxyClassName() {
            return PROXY_CLASS_NAME;
        }

        /**
         * Creates the underlying proxy.
         *
         * @param catalogNameUrls Catalog name URLs
         * @param urlString URL
         * @return Proxy
         */
        static XmlaOlap4jProxy createProxy(
            Map<String, String> catalogNameUrls,
            String urlString)
        {
            try {
                final Class<?> clazz = Class.forName(getProxyClassName());
                final Constructor<?> constructor =
                    clazz.getConstructor(Map.class, String.class);
                return
                (XmlaOlap4jProxy) constructor.newInstance(
                    catalogNameUrls, urlString);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] get(XmlaOlap4jServerInfos serverInfos, String request)
            throws XmlaOlap4jProxyException
        {
            this.checkup(request);
            return super.get(serverInfos, request);
        }

        /**
         * Checks that a request has not been seen before.
         *
         * @param request Request
         *
         * @throws RuntimeException if proxy has been seen before
         */
        private void checkup(String request) {
            String hash = Encoder.convertToHex(request.getBytes());
            if (request.indexOf("<RequestType>MDSCHEMA_CUBES</RequestType>")
                == -1
                && this.requests.containsKey(hash))
            {
                throw new RuntimeException("DOUBLE-REQUEST");
            } else {
                this.requests.put(hash, request);
            }
        }
    }

    protected void tearDown() throws Exception {
        testContext = null;
        tester = null;
        super.tearDown();
    }

    /**
     * Verifies that the construction of the necessary
     * XMLA objects during DriverManager.getConnection() do not make
     * calls that could cause deadlocks.
     */
    public void testNoNonTrivalCallsOnConnect() throws Exception {
        String cookie = XmlaOlap4jDriver.nextCookie();
        try {
            XmlaOlap4jDriver.PROXY_MAP.put(cookie, new XmlaOlap4jProxyMock());
            try {
                Class.forName(DRIVER_CLASS_NAME);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("oops", e);
            }
            Properties info = new Properties();
            info.setProperty(
                XmlaOlap4jDriver.Property.CATALOG.name(), "FoodMart");
            DriverManager.getConnection(
                "jdbc:xmla:Server=http://foo;Catalog=FoodMart;TestProxyCookie="
                + cookie,
                info);
        } catch (Throwable t) {
            t.printStackTrace();
            fail(
                "Non-Trival Call executed during construction of XmlaOlap4j "
                + "Connection");
        }
    }

    public void testDbSchemaSchemata() throws Exception {
        if (!testContext.getTester().getFlavor()
                .equals(Tester.Flavor.XMLA))
        {
            return;
        }
        class Proxy extends DoubleSubmissionTestProxy {
            boolean schemata = false;
            boolean cubes = false;
            public Proxy(
                Map<String, String> catalogNameUrls,
                String urlString)
            {
                super(catalogNameUrls, urlString);
            }
            @Override
            public byte[] get(
                XmlaOlap4jServerInfos serverInfos,
                String request)
                throws XmlaOlap4jProxyException
            {
                if (request.contains("DBSCHEMA_SCHEMATA")) {
                    if (schemata || cubes) {
                        fail();
                    }
                    schemata = true;
                } else if (request.contains("MDSCHEMA_CUBES")) {
                    if (!schemata || cubes) {
                        fail();
                    }
                }
                return super.get(serverInfos, request);
            }
        }
        String oldValue = XmlaTester.getProxyClassName();
        XmlaTester.setProxyClassName(
            Proxy.class.getName());
        DoubleSubmissionTestProxy.setProxyClassName(oldValue);
        try {
            Connection connection = tester.createConnection();
            OlapConnection oConn =
                tester.getWrapper().unwrap(connection, OlapConnection.class);
            oConn.getOlapSchema().getCubes().size();
        } finally {
            XmlaTester.setProxyClassName(oldValue);
        }
    }

    /**
     * Tests that no request is sent to XMLA more than once.
     * If the same request is sent twice, throws an exception. The only
     * exception to this is the MDSCHEMA_CUBES query that is used to
     * populate both the catalogs and the schemas associated to a
     * given catalog. This was due to a flaw in SSAS; it doesn't
     * return a SCHEMA_NAME column when asked to. We fixed it this
     * way in some other revision.
     * @throws Exception If the test fails.
     */
    public void testNoDoubleQuerySubmission() throws Exception {
        if (!testContext.getTester().getFlavor()
                .equals(Tester.Flavor.XMLA)
            && !testContext.getTester().getFlavor()
                .equals(Tester.Flavor.REMOTE_XMLA))
        {
            return;
        }
        String oldValue = XmlaTester.getProxyClassName();
        XmlaTester.setProxyClassName(
            DoubleSubmissionTestProxy.class.getName());
        DoubleSubmissionTestProxy.setProxyClassName(oldValue);
        try {
            Connection connection = tester.createConnection();
            Statement statement = connection.createStatement();
            final OlapStatement olapStatement =
                tester.getWrapper().unwrap(statement, OlapStatement.class);
            CellSet cellSet =
                olapStatement.executeOlapQuery(
                    "SELECT\n"
                    + " {[Measures].[Unit Sales],\n"
                    + "    [Measures].[Store Sales]} ON COLUMNS\n,"
                    + " Crossjoin({[Gender].[M]}, [Product].Children) ON ROWS\n"
                    + "FROM [Sales]\n"
                    + "WHERE [Time].[1997].[Q2]");
            assertNotNull(cellSet);
            cellSet =
                olapStatement.executeOlapQuery(
                    "SELECT\n"
                    + " {[Measures].[Unit Sales],\n"
                    + "    [Measures].[Store Sales]} ON COLUMNS\n,"
                    + " Crossjoin({[Gender].[M]}, [Product].Children) ON ROWS\n"
                    + "FROM [Sales]\n"
                    + "WHERE [Time].[1997].[Q3]");
        } finally {
            XmlaTester.setProxyClassName(oldValue);
        }
    }

    /**
     * This is a test to verify that server specific properties
     * can be sent to the server in the PropertyList element
     * of SOAP messages. A property that is not part of those enumerated
     * in {@link org.olap4j.driver.xmla.XmlaOlap4jDriver.Property} must
     * not be sent to the server.
     */
    public void testPropertyList() throws Exception {
        if (!testContext.getTester().getFlavor()
                .equals(Tester.Flavor.XMLA)
            && !testContext.getTester().getFlavor()
                .equals(Tester.Flavor.REMOTE_XMLA))
        {
            return;
        }
        switch (testContext.getTester().getWrapper()) {
        case DBCP:
            return;
        }
        final String oldValue = XmlaTester.getProxyClassName();
        try {
            XmlaTester.setProxyClassName(
                PropertyListTestProxy.class.getName());

            OlapConnection connection =
                tester.getWrapper().unwrap(
                    tester.createConnection(), OlapConnection.class);
            OlapStatement olapStatement = connection.createStatement();
            olapStatement.executeOlapQuery(
                "SELECT\n"
                + " {[Measures].[Unit Sales],\n"
                + "    [Measures].[Store Sales]} ON COLUMNS\n,"
                + " Crossjoin({[Gender].[M]}, [Product].Children) ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE [Time].[1997].[Q2]");
            assertEquals(0, PropertyListTestProxy.count);
            connection.close();

            connection =
                tester.getWrapper().unwrap(
                    tester.createConnectionWithUserPassword(),
                    OlapConnection.class);
            olapStatement = connection.createStatement();
            olapStatement.executeOlapQuery(
                "SELECT\n"
                + " {[Measures].[Unit Sales],\n"
                + "    [Measures].[Store Sales]} ON COLUMNS\n,"
                + " Crossjoin({[Gender].[M]}, [Product].Children) ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE [Time].[1997].[Q2]");
            assertEquals(0, PropertyListTestProxy.count);
            connection.close();

            final Properties props = new Properties();
            props.put("FOOBAR", "Bacon");
            connection =
                tester.getWrapper().unwrap(
                    ((XmlaTester)tester)
                        .createConnectionWithUserPassword(props),
                    OlapConnection.class);
            olapStatement = connection.createStatement();
            try {
                olapStatement.executeOlapQuery(
                    "SELECT\n"
                    + " {[Measures].[Unit Sales],\n"
                    + "    [Measures].[Store Sales]} ON COLUMNS\n,"
                    + " Crossjoin({[Gender].[M]}, [Product].Children) ON ROWS\n"
                    + "FROM [Sales]\n"
                    + "WHERE [Time].[1997].[Q2]");
            } catch (Throwable e) {
                assertTrue(e.getCause().getMessage().contains("FOOBAR"));
            }
            connection.close();
        } finally {
            XmlaTester.setProxyClassName(oldValue);
        }
    }

    /**
     * This is a class for the test
     * {@link XmlaConnectionTest#testPropertyList()}.
     */
    public static class PropertyListTestProxy extends DelegatingTestProxy {
        public PropertyListTestProxy(XmlaOlap4jProxy proxy) {
            super(proxy);
        }

        private static final String[] lookup = {
            "<PASSWORD>"
        };

        private static int count = 0;

        public byte[] get(
            XmlaOlap4jServerInfos serverInfos,
            String request)
            throws XmlaOlap4jProxyException
        {
            for (String token : lookup) {
                if (request.contains(token)) {
                    count++;
                }
            }
            return super.get(serverInfos, request);
        }
    }

    private static class Encoder {
        /**
         * Converts an array of bytes to a hex string.
         *
         * <p>For example, <code>convertToHex(new byte[] {(byte) 0xDE,
         * (byte) 0xAD})</code> returns <code>"DEAD"</code>.
         *
         * @param data Array of bytes
         * @return Bytes encoded as hex
         */
        private static String convertToHex(byte[] data) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                int halfbyte = (data[i] >>> 4) & 0x0F;
                int two_halfs = 0;
                do {
                    if ((0 <= halfbyte) && (halfbyte <= 9)) {
                        buf.append((char) ('0' + halfbyte));
                    } else {
                        buf.append((char) ('a' + (halfbyte - 10)));
                    }
                    halfbyte = data[i] & 0x0F;
                } while (two_halfs++ < 1);
            }
            return buf.toString();
        }
    }
}

// End XmlaConnectionTest.java
