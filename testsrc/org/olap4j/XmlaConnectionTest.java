package org.olap4j;

import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Future;

import mondrian.olap4j.MondrianInprocProxy;

import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;
import org.olap4j.test.TestContext;

import junit.framework.TestCase;

public class XmlaConnectionTest extends TestCase {

    public static final String DRIVER_CLASS_NAME =
        "org.olap4j.driver.xmla.XmlaOlap4jDriver";
    private TestContext.Tester tester = null;

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
    public static class DoubleSubmissionTestProxy extends MondrianInprocProxy {
        Map<String,String> requests = new HashMap<String,String>();
        public DoubleSubmissionTestProxy(
                Map<String, String> catalogNameUrls,
                String urlString) 
        {
            super(catalogNameUrls,urlString);
        }
        @Override
        public byte[] get(URL url, String request) throws IOException {
            this.checkup(request);
            return super.get(url, request);
        }
//        @Override
//        public Future<byte[]> submit(URL url, String request) {
//            this.checkup(request);
//            return super.submit(url, request);
//        }
        private void checkup(String request) {
            String hash = Encoder.convertToHex(request.getBytes());
            if ( request.indexOf("<RequestType>MDSCHEMA_CUBES</RequestType>") == -1 &&
                    this.requests.containsKey(hash)) {
                throw new RuntimeException("DOUBLE-REQUEST");
            } else {
                this.requests.put(hash,request);
            }
        }
    }

    /**
     * this test verifies that the construction of the necessary
     * xmla objects during DriverManager.getConnection() do not make
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

    public void testNoDoubleQuerySubmission() throws Exception {
        String oldValue = XmlaTester.getProxyClassName();
        XmlaTester.setProxyClassName(
        DoubleSubmissionTestProxy.class.getName());
        if ( TestContext.getTestProperties()
          .get(TestContext.Property.HELPER_CLASS_NAME.path)
          .equals("org.olap4j.XmlaTester"))
        {
            try {
                tester = TestContext.instance().getTester();
                Connection connection = tester.createConnection();
                Statement statement = connection.createStatement();
                final OlapStatement olapStatement =
                    tester.getWrapper().unwrap(statement, OlapStatement.class);
                @SuppressWarnings("unused")
                CellSet cellSet =
                    olapStatement.executeOlapQuery(
                        "SELECT\n" +
                            " {[Measures].[Unit Sales],\n" +
                            "    [Measures].[Store Sales]} ON COLUMNS\n," +
                            " Crossjoin({[Gender].[M]}, [Product].Children) ON ROWS\n" +
                            "FROM [Sales]\n" +
                            "WHERE [Time].[1997].[Q2]");
                cellSet =
                    olapStatement.executeOlapQuery(
                        "SELECT\n" +
                            " {[Measures].[Unit Sales],\n" +
                            "    [Measures].[Store Sales]} ON COLUMNS\n," +
                            " Crossjoin({[Gender].[M]}, [Product].Children) ON ROWS\n" +
                            "FROM [Sales]\n" +
                            "WHERE [Time].[1997].[Q3]");
            } catch(RuntimeException e) {
                fail(e.getMessage());
            }
       }
       XmlaTester.setProxyClassName(oldValue);
    }

    private static class Encoder {
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
