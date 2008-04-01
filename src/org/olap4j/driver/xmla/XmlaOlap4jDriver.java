/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.impl.Base64;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

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
 * <p>Note how we use the {@link java.sql.Connection#unwrap(Class)} method to down-cast
 * the JDBC connection object to the extension {@link org.olap4j.OlapConnection}
 * object. This method is only available in JDBC 4.0 (JDK 1.6 onwards).
 *
 * <h3>Connection properties</h3>
 *
 * <table border="1">
 * <tr> <th>Property</th>        <th>Description</th> </tr>
 *
 * <tr> <td>Server</td>          <td>URL of HTTP server.</td> </tr>
 * 
 * <tr> <td>Catalog</td>          <td>Catalog name to use</td> </tr>
 * 
 * <tr> <td>Provider</td>          <td>Name of the XMLA provider. This option is facultative. By default, the first one available will be used.</td> </tr>
 * 
 * <tr> <td>DataSource</td>          <td>Name of the XMLA datasource. This option is facultative. By default, the first one available will be used. When using a Mondrian backed XMLA server, be sure to include the full datasource name between quotes.</td> </tr>
 *
 * <tr> <td>UseThreadProxy</td>  <td>If true, use the proxy object in the
 *                                   {@link #THREAD_PROXY} field. For testing.
 *                                   Default is false.</td> </tr>
 *
 * </table>
 *
 * @author jhyde
 * @version $Id$
 * @since May 22, 2007
 */
public class XmlaOlap4jDriver implements Driver {
    public static final String NAME = "olap4j driver for XML/A";
    public static final String VERSION = "0.9.4";
    public static final int MAJOR_VERSION = 0;
    public static final int MINOR_VERSION = 904;
    private final Factory factory;

    /**
     * Executor shared by all connections making asynchronous XMLA calls.
     */
    private static final ExecutorService executor =
        Executors.newCachedThreadPool();

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

    protected XmlaOlap4jDriver() {
        String factoryClassName;
        try {
            Class.forName("java.sql.Wrapper");
            factoryClassName = "org.olap4j.driver.xmla.FactoryJdbc4Impl";
        } catch (ClassNotFoundException e) {
            // java.sql.Wrapper is not present. This means we are running JDBC
            // 3.0 or earlier (probably JDK 1.5). Load the JDBC 3.0 factory
            factoryClassName = "org.olap4j.driver.xmla.FactoryJdbc3Impl";
        }
        try {
            final Class clazz = Class.forName(factoryClassName);
            factory = (Factory) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void register() throws SQLException {
        DriverManager.registerDriver(new XmlaOlap4jDriver());
    }

    public Connection connect(String url, Properties info) throws SQLException {
        if (!XmlaOlap4jConnection.acceptsURL(url)) {
            return null;
        }
        Proxy proxy = createProxy(info);
        return factory.newConnection(proxy, url, info);
    }

    public boolean acceptsURL(String url) throws SQLException {
        return XmlaOlap4jConnection.acceptsURL(url);
    }

    public DriverPropertyInfo[] getPropertyInfo(
        String url, Properties info) throws SQLException
    {
        List<DriverPropertyInfo> list = new ArrayList<DriverPropertyInfo>();

        // Add the contents of info
        for (Map.Entry<Object,Object> entry : info.entrySet()) {
            list.add(
                new DriverPropertyInfo(
                    (String) entry.getKey(),
                    (String) entry.getValue()));
        }
        // Next add standard properties

        return list.toArray(new DriverPropertyInfo[list.size()]);
    }

    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    public boolean jdbcCompliant() {
        return false;
    }

    /**
     * Creates a Proxy with which to talk to send XML web-service calls.
     * The usual implementation of Proxy uses HTTP; there is another
     * implementation, for testing, which talks to mondrian's XMLA service
     * in-process.
     *
     * @param info Connection properties
     * @return A Proxy with which to submit XML requests
     */
    protected Proxy createProxy(Properties info) {
        String useThreadProxy =
            info.getProperty(
                Property.UseThreadProxy.name());
        if (useThreadProxy != null &&
            Boolean.valueOf(useThreadProxy)) {
            Proxy proxy = THREAD_PROXY.get();
            if (proxy != null) {
                return proxy;
            }
        }
        return new HttpProxy();
    }

    public Future<byte[]> getFuture(
        final Proxy proxy,
        final URL url,
        final String request)
    {
        return executor.submit(
            new Callable<byte[] >() {
                public byte[] call() throws Exception {
                    return proxy.get(url, request);
                }
            }
        );
    }

    /**
     * Object which can respond to HTTP requests.
     */
    public interface Proxy {
        /**
         * Sends a request to a URL and returns the response.
         *
         * @param url Target URL
         * @param request Request string
         * @return Response
         * @throws IOException
         */
        byte[] get(URL url, String request) throws IOException;

        /**
         * Submits a request for background execution.
         *
         * @param url URL
         * @param request Request
         * @return Future object representing the submitted job
         */
        Future<byte[]> submit(
            URL url,
            String request);

        /**
         * Returns the name of the character set use for encoding the XML
         * string.
         */
        String getEncodingCharsetName();
    }

    /**
     * Implementation of {@link Proxy} which uses HTTP POST.
     */
    protected static class HttpProxy implements Proxy {
        public byte[] get(URL url, String request) throws IOException {
            // Open connection to manipulate the properties
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("content-type", "text/xml");

            // Encode credentials for basic authentication
            if (url.getUserInfo() != null) {
                String encoding =
                    Base64.encodeBytes(url.getUserInfo().getBytes(), 0);
                urlConnection.setRequestProperty(
                    "Authorization", "Basic " + encoding);
            }

            // Send data (i.e. POST). Use same encoding as specified in the
            // header.
            final String encoding = getEncodingCharsetName();
            urlConnection.getOutputStream().write(request.getBytes(encoding));

            // Get the response, again assuming default encoding.
            InputStream is = urlConnection.getInputStream();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int count;
            while ((count = is.read(buf)) > 0) {
                baos.write(buf, 0, count);
            }
            return baos.toByteArray();
        }

        public Future<byte[]> submit(
            final URL url,
            final String request)
        {
            return executor.submit(
                new Callable<byte[] >() {
                    public byte[] call() throws Exception {
                        return get(url, request);
                    }
                }
            );
        }

        public String getEncodingCharsetName() {
            return "UTF-8";
        }
    }

    /**
     * For testing.
     */
    public static final ThreadLocal<Proxy> THREAD_PROXY =
        new ThreadLocal<Proxy>();

    /**
     * Properties supported by this driver.
     */
    public enum Property {
        UseThreadProxy(
            "If true, use the proxy object in the THREAD_PROXY field. "
                + "For testing. Default is false."),

        Server("URL of HTTP server"),
        Catalog("Catalog name"),
        Provider("Name of the datasource provider"),
        DataSource("Name of the datasource");

        Property(String description) {
        }
    }
}

// End XmlaOlap4jDriver.java
