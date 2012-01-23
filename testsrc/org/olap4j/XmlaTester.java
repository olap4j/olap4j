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
package org.olap4j;

import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;
import org.olap4j.test.TestContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Implementation of {@link org.olap4j.test.TestContext.Tester} which speaks
 * to the olap4j driver for XML/A.
 *
 * @author jhyde
 * @version $Id$
 */
public class XmlaTester implements TestContext.Tester {
    private final TestContext testContext;
    final XmlaOlap4jProxy proxy;
    static final String cookie = XmlaOlap4jDriver.nextCookie();
    private Connection connection;

    /**
     * Creates an XmlaTester.
     *
     * <p>The {@link org.olap4j.test.TestContext.Tester} API requires a public
     * constructor with a {@link org.olap4j.test.TestContext} parameter.
     *
     * @param testContext Test context
     *
     * @throws ClassNotFoundException on error
     * @throws IllegalAccessException on error
     * @throws InstantiationException on error
     * @throws NoSuchMethodException on error
     * @throws InvocationTargetException on error
     */
    public XmlaTester(TestContext testContext)
        throws ClassNotFoundException, IllegalAccessException,
        InstantiationException, NoSuchMethodException,
        InvocationTargetException
    {
        this.testContext = testContext;
        final Properties properties = testContext.getProperties();
        final String catalogUrl =
            properties.getProperty(
                TestContext.Property.XMLA_CATALOG_URL.path, "http://foo");

        // Include the same catalog URL twice with different catalog names. This
        // allows us to detect whether operations are restricting to the current
        // catalog. (Some should, most should not.)
        Map<String, String> catalogNameUrls =
            new HashMap<String, String>();
        catalogNameUrls.put("FoodMart", catalogUrl);
        catalogNameUrls.put("FoodMart2", catalogUrl);
        String urlString =
            properties.getProperty(
                TestContext.Property.CONNECT_URL.path, "jdbc:mondrian:");

        final Class<?> clazz = Class.forName(getProxyClassName());
        final Constructor<?> constructor =
            clazz.getConstructor(Map.class, String.class);
        this.proxy =
            (XmlaOlap4jProxy) constructor.newInstance(
                catalogNameUrls, urlString);
        XmlaOlap4jDriver.PROXY_MAP.put(cookie, proxy);
    }

    public TestContext getTestContext() {
        return testContext;
    }

    public Connection createConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("oops", e);
        }
        Properties info = new Properties();
        info.setProperty(
            XmlaOlap4jDriver.Property.CATALOG.name(), "FoodMart");
        connection =
            DriverManager.getConnection(
                getURL(),
                info);
        return connection;
    }

    public Connection createConnectionWithUserPassword() throws SQLException {
        final Properties props = new Properties();
        return createConnectionWithUserPassword(props);
    }

    public Connection createConnectionWithUserPassword(
        Properties props)
        throws SQLException
    {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("oops", e);
        }
        props.setProperty(
            XmlaOlap4jDriver.Property.CATALOG.name(), "FoodMart");
        if (USER != null) {
            props.put("user", USER);
        }
        if (PASSWORD != null) {
            props.put("password", PASSWORD);
        }
        return DriverManager.getConnection(getURL(), props);
    }

    public String getDriverUrlPrefix() {
        return DRIVER_URL_PREFIX;
    }

    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    public String getURL() {
        return "jdbc:xmla:Server=http://foo;Catalog=FoodMart;TestProxyCookie="
            + cookie;
    }

    public Flavor getFlavor() {
        return Flavor.XMLA;
    }

    public TestContext.Wrapper getWrapper() {
        return TestContext.Wrapper.NONE;
    }

    public static void setProxyClassName(String clazz) {
        PROXY_CLASS_NAME = clazz;
    }

    public static String getProxyClassName() {
        return PROXY_CLASS_NAME;
    }

    public static final String DRIVER_CLASS_NAME =
         "org.olap4j.driver.xmla.XmlaOlap4jDriver";

    public static final String DRIVER_URL_PREFIX = "jdbc:xmla:";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static String PROXY_CLASS_NAME =
        "mondrian.olap4j.MondrianInprocProxy";
}

// End XmlaTester.java
