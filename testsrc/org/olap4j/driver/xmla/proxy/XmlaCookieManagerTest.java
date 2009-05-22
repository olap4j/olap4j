package org.olap4j.driver.xmla.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.TestCase;

import org.olap4j.driver.xmla.proxy.XmlaOlap4jCookieManager;

public class XmlaCookieManagerTest extends TestCase {

    private static final String receivedCookieKey = "Set-Cookie";
    public final static String sentCookieKey = "Cookie";
    public final static String cookieValue = "MyTestCookie=271B79EBCBAAA37C5C51B1979441E5AC";



    /**
     * This simple test makes sure that the cookie manager works as expected. It
     * creates a connection stub which returns fake Set-Cookie response
     * headers. The cookies are then stored in the cookie manager and a new
     * connection stub is created. The second connection is then passed back to
     * the manager and we check if the cookies were applied to the connection.
     *
     * @throws Exception
     */
    public void testCookieManager() throws Exception {
        UrlConnectionStub conn =
            new UrlConnectionStub(new URL("http://example.com"));
        XmlaOlap4jCookieManager manager = new XmlaOlap4jCookieManager();

        conn.connect();

        manager.storeCookies(conn);

        conn = new UrlConnectionStub(new URL("http://example.com"));

        manager.setCookies(conn);

        assertEquals(sentCookieKey, conn.getInternalCookieKey());
        assertEquals(cookieValue, conn.getInternalCookieValue());
    }

    private static class UrlConnectionStub extends HttpURLConnection {
        private String internalCookieKey = null;
        private String internalCookieValue = null;

        protected UrlConnectionStub(URL u) {
            super(u);
        }

        @Override
        public void disconnect() {
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() throws IOException {
            this.connected = true;
        }

        @Override
        public String getHeaderFieldKey(int n) {
            if (n == 1) {
                return receivedCookieKey;
            } else {
                return null;
            }
        }

        @Override
        public String getHeaderField(int n) {
            if (n == 1) {
                return cookieValue;
            } else {
                return null;
            }
        }

        @Override
        public void setRequestProperty(String key, String value) {
            this.internalCookieKey = key;
            this.internalCookieValue = value;
        }

        public String getInternalCookieKey() {
            return internalCookieKey;
        }

        public String getInternalCookieValue() {
            return internalCookieValue;
        }
    }
}

// End XmlaCookieManagerTest.java
