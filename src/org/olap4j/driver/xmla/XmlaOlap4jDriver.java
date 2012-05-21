/*
// $Id$
//
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

import org.olap4j.driver.xmla.proxy.XmlaOlap4jHttpProxy;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;
import org.olap4j.impl.Olap4jUtil;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Olap4j driver for generic XML for Analysis (XMLA) providers.
 *
 * <p>Since olap4j is a superset of JDBC, you register this driver as you would
 * any JDBC driver:
 *
 * <blockquote>
 * <code>Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");</code>
 * </blockquote>
 *
 * Then create a connection using a URL with the prefix "jdbc:xmla:".
 * For example,
 *
 * <blockquote>
 * <code>import java.sql.Connection;<br/>
 * import java.sql.DriverManager;<br/>
 * import org.olap4j.OlapConnection;<br/>
 * <br/>
 * Connection connection =<br/>
 * &nbsp;&nbsp;&nbsp;DriverManager.getConnection(<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"jdbc:xmla:");<br/>
 * OlapConnection olapConnection =<br/>
 * &nbsp;&nbsp;&nbsp;connection.unwrap(OlapConnection.class);</code>
 * </blockquote>
 *
 * <p>Note how we use the java.sql.Connection#unwrap(Class) method to down-cast
 * the JDBC connection object to the extension {@link org.olap4j.OlapConnection}
 * object. This method is only available in JDBC 4.0 (JDK 1.6 onwards).
 *
 * <h3>Connection properties</h3>
 *
 * <p>Unless otherwise stated, properties are optional. If a property occurs
 * multiple times in the connect string, the first occurrence is used.
 *
 * <p>It is also possible to pass properties to the server end-point using
 * JDBC connection properties as part of the XMLA driver connection properties.
 * If the JDBC URL contains properties that are not enumerated in
 * {@link Property}, they will be included as part of the SOAP PropertyList
 * element.
 *
 *
 * <table border="1">
 * <tr><th>Property</th>     <th>Description</th> </tr>
 *
 * <tr><td>Server</td>       <td>URL of HTTP server. Required.</td></tr>
 *
 * <tr><td>Catalog</td>      <td>Catalog name to use.
 *                               By default, the first one returned by the
 *                               XMLA server will be used.</td></tr>
 *
 * <tr><td>Schema</td>      <td>Schema name to use.
 *                               By default, the first one returned by the
 *                               XMLA server will be used.</td></tr>
 *
 * <tr><td>Database</td>    <td>Name of the XMLA database.
 *                               By default, the first one returned by the
 *                               XMLA server will be used.</td></tr>
 *
 * <tr><td>Cache</td>      <td><p>Class name of the SOAP cache to use.
 *                             Must implement interface
 *              {@link org.olap4j.driver.xmla.proxy.XmlaOlap4jCachedProxy}.
 *                             A built-in memory cache is available with
 *              {@link org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache}.
 *
 *                         <p>By default, no SOAP query cache will be
 *                             used.
 *                             </td></tr>
 * <tr><td>Cache.*</td>    <td>Properties to transfer to the selected cache
 *                             implementation. See
 *                          {@link org.olap4j.driver.xmla.cache.XmlaOlap4jCache}
 *                             or your selected implementation for properties
 *                             details.
 *                             </td></tr>
 * <tr><td>TestProxyCookie</td><td>String that uniquely identifies a proxy
 *                             object in {@link #PROXY_MAP} via which to
 *                             send XMLA requests for testing
 *                             purposes.
 *                             </td></tr>
 * <tr><td>Role</td>       <td>Comma separated list of role names used for
 *                             this connection (Optional). <br />
 *                             Available role names can be retrieved via
 *    {@link org.olap4j.driver.xmla.XmlaOlap4jConnection#getAvailableRoleNames}
 *                             </td></tr>
 * <tr><td>User</td>       <td>User name to use when establishing a
 *                             connection to the server. The credentials are
 *                             passed using the HTTP Basic authentication
 *                             protocol, but are also sent as part of the SOAP
 *                             Security headers.
 *                             </td></tr>
 * <tr><td>Password</td>   <td>Password to use when establishing a
 *                             connection to the server. The credentials are
 *                             passed using the HTTP Basic authentication
 *                             protocol, but are also sent as part of the SOAP
 *                             Security headers.
 *                             </td></tr>
 * </table>
 *
 * @author jhyde, Luc Boudreau
 * @version $Id$
 * @since May 22, 2007
 */
public class XmlaOlap4jDriver implements Driver {

    private final Factory factory;

    /**
     * Executor shared by all connections making asynchronous XMLA calls.
     */
    private static final ExecutorService executor;

    static {
        executor = Executors.newCachedThreadPool(
            new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setDaemon(true);
                    return t;
               }
            }
        );
    }

    private static int nextCookie;

    static {
        try {
            register();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Creates an XmlaOlap4jDriver.
     */
    public XmlaOlap4jDriver() {
        factory = createFactory();
    }

    private static Factory createFactory() {
        final String factoryClassName = getFactoryClassName();
        try {
            final Class<?> clazz = Class.forName(factoryClassName);
            return (Factory) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFactoryClassName() {
        try {
            // If java.sql.PseudoColumnUsage is present, we are running JDBC 4.1
            // or later.
            Class.forName("java.sql.PseudoColumnUsage");
            return "org.olap4j.driver.xmla.FactoryJdbc41Impl";
        } catch (ClassNotFoundException e) {
            // java.sql.PseudoColumnUsage is not present. This means we are
            // running JDBC 4.0 or earlier.
            try {
                Class.forName("java.sql.Wrapper");
                return "org.olap4j.driver.xmla.FactoryJdbc4Impl";
            } catch (ClassNotFoundException e2) {
                // java.sql.Wrapper is not present. This means we are running
                // JDBC 3.0 or earlier (probably JDK 1.5). Load the JDBC 3.0
                // factory.
                return "org.olap4j.driver.xmla.FactoryJdbc3Impl";
            }
        }
    }

    /**
     * Registers an instance of XmlaOlap4jDriver.
     *
     * <p>Called implicitly on class load, and implements the traditional
     * 'Class.forName' way of registering JDBC drivers.
     *
     * @throws SQLException on error
     */
    private static void register() throws SQLException {
        DriverManager.registerDriver(new XmlaOlap4jDriver());
    }

    public Connection connect(String url, Properties info) throws SQLException {
        // Checks if this driver handles this connection, exit otherwise.
        if (!XmlaOlap4jConnection.acceptsURL(url)) {
            return null;
        }

        // Parses the connection string
        Map<String, String> map =
            XmlaOlap4jConnection.parseConnectString(url, info);

        // Creates a connection proxy
        XmlaOlap4jProxy proxy = createProxy(map);

        // returns a connection object to the java API
        return factory.newConnection(this, proxy, url, info);
    }

    public boolean acceptsURL(String url) throws SQLException {
        return XmlaOlap4jConnection.acceptsURL(url);
    }

    public DriverPropertyInfo[] getPropertyInfo(
        String url, Properties info) throws SQLException
    {
        List<DriverPropertyInfo> list = new ArrayList<DriverPropertyInfo>();

        // Add the contents of info
        for (Map.Entry<Object, Object> entry : info.entrySet()) {
            list.add(
                new DriverPropertyInfo(
                    (String) entry.getKey(),
                    (String) entry.getValue()));
        }
        // Next add standard properties

        return list.toArray(new DriverPropertyInfo[list.size()]);
    }

    /**
     * Returns the driver name. Not in the JDBC API.
     * @return Driver name
     */
    String getName() {
        return XmlaOlap4jDriverVersion.NAME;
    }

    /**
     * Returns the driver version. Not in the JDBC API.
     * @return Driver version
     */
    public String getVersion() {
        return XmlaOlap4jDriverVersion.VERSION;
    }

    public int getMajorVersion() {
        return XmlaOlap4jDriverVersion.MAJOR_VERSION;
    }

    public int getMinorVersion() {
        return XmlaOlap4jDriverVersion.MINOR_VERSION;
    }

    public boolean jdbcCompliant() {
        return false;
    }

    // for JDBC 4.1
    public Logger getParentLogger() {
        return Logger.getLogger("");
    }

    /**
     * Creates a Proxy with which to talk to send XML web-service calls.
     * The usual implementation of Proxy uses HTTP; there is another
     * implementation, for testing, which talks to mondrian's XMLA service
     * in-process.
     *
     * @param map Connection properties
     * @return A Proxy with which to submit XML requests
     */
    protected XmlaOlap4jProxy createProxy(Map<String, String> map) {
        String cookie = map.get(Property.TESTPROXYCOOKIE.name());
        if (cookie != null) {
            XmlaOlap4jProxy proxy = PROXY_MAP.get(cookie);
            if (proxy != null) {
                return proxy;
            }
        }
        return new XmlaOlap4jHttpProxy(this);
    }

    /**
     * Returns a future object representing an asynchronous submission of an
     * XMLA request to a URL.
     *
     * @param proxy Proxy via which to send the request
     * @param serverInfos Server infos.
     * @param request Request
     * @return Future object from which the byte array containing the result
     * of the XMLA call can be obtained
     */
    public static Future<byte[]> getFuture(
        final XmlaOlap4jProxy proxy,
        final XmlaOlap4jServerInfos serverInfos,
        final String request)
    {
        return executor.submit(
            new Callable<byte[]>() {
                public byte[] call() throws Exception {
                    return proxy.get(serverInfos, request);
                }
            }
        );
    }

    /**
     * For testing. Map from a cookie value (which is uniquely generated for
     * each test) to a proxy object. Uses a weak hash map so that, if the code
     * that created the proxy 'forgets' the cookie value, then the proxy can
     * be garbage-collected.
     */
    public static final Map<String, XmlaOlap4jProxy> PROXY_MAP =
        Collections.synchronizedMap(new WeakHashMap<String, XmlaOlap4jProxy>());

    /**
     * Generates and returns a unique string.
     *
     * @return unique string
     */
    public static synchronized String nextCookie() {
        return "cookie" + nextCookie++;
    }

    /**
     * Properties supported by this driver.
     */
    public enum Property {
        TESTPROXYCOOKIE(
            "String that uniquely identifies a proxy object via which to send "
            + "XMLA requests for testing purposes."),
        SERVER("URL of HTTP server"),
        DATABASE("Name of the database"),
        CATALOG("Catalog name"),
        SCHEMA("Name of the schema"),
        CACHE("Class name of the SOAP cache implementation"),
        ROLE("Comma separated list of roles this connection impersonates"),
        USER("Username to use when creating connections to the server."),
        PASSWORD("Password to use when creating connections to the server.");

        /**
         * Creates a property.
         *
         * @param description Description of property
         */
        Property(String description) {
            Olap4jUtil.discard(description);
        }
    }
}

// End XmlaOlap4jDriver.java
