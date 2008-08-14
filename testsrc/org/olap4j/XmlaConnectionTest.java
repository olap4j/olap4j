package org.olap4j;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.concurrent.Future;

import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;

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
}

// End XmlaConnectionTest.java
