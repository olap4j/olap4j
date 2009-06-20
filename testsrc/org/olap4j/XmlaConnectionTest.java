package org.olap4j;

import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Future;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxyException;
import org.olap4j.test.TestContext;

import junit.framework.TestCase;

public class XmlaConnectionTest extends TestCase {

    public static final String DRIVER_CLASS_NAME =
        "org.olap4j.driver.xmla.XmlaOlap4jDriver";

    static class XmlaOlap4jProxyMock implements XmlaOlap4jProxy {
        public byte[] get(URL url, String request) throws IOException {
            throw new RuntimeException("Non-Trivial Call!");
        }

        public String getEncodingCharsetName() {
            return "UTF-8";
        }

        public Future<byte[]> submit(URL url, String request) {
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

        public byte[] get(URL url, String request)
            throws XmlaOlap4jProxyException, IOException
        {
            return proxy.get(url, request);
        }

        public Future<byte[]> submit(
            URL url,
            String request)
        {
            return proxy.submit(url, request);
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
        private static XmlaOlap4jProxy createProxy(
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

        public byte[] get(URL url, String request)
            throws IOException, XmlaOlap4jProxyException
        {
            this.checkup(request);
            return super.get(url, request);
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
            if (request.indexOf("<RequestType>MDSCHEMA_CUBES</RequestType>") == -1
                && this.requests.containsKey(hash))
            {
                throw new RuntimeException("DOUBLE-REQUEST");
            } else {
                this.requests.put(hash, request);
            }
        }
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
                XmlaOlap4jDriver.Property.Catalog.name(), "FoodMart");
            DriverManager.getConnection(
                "jdbc:xmla:Server=http://foo;Catalog=FoodMart;TestProxyCookie=" + cookie,
                info);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Non-Trival Call executed during construction of XmlaOlap4j Connection");
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
        if (!TestContext.getTestProperties()
            .get(TestContext.Property.HELPER_CLASS_NAME.path)
            .equals("org.olap4j.XmlaTester"))
        {
            return;
        }
        String oldValue = XmlaTester.getProxyClassName();
        XmlaTester.setProxyClassName(
            DoubleSubmissionTestProxy.class.getName());
        DoubleSubmissionTestProxy.setProxyClassName(oldValue);
        try {
            final TestContext.Tester tester =
                TestContext.instance().getTester();
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

        /**
         * Computes the SHA-1 digest of a string, encoded as a hex string.
         * @param text String
         * @return Digest
         */
        public static String SHA1(String text) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e) {
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e1) {
                    throw new RuntimeException(e1);
                }
            }
            byte[] sha1hash = new byte[40];
            md.update(text.getBytes(), 0, text.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        }
    }
}

// End XmlaConnectionTest.java
