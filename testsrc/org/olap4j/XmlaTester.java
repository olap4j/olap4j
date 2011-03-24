/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.driver.xmla.proxy.XmlaOlap4jProxy;
import org.olap4j.test.TestContext;

import java.sql.*;
import java.util.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
    final String cookie;
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
        this.cookie = XmlaOlap4jDriver.nextCookie();
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
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("oops", e);
        }
        Properties info = new Properties();
        info.setProperty(
            XmlaOlap4jDriver.Property.CATALOG.name(), "FoodMart");
        return DriverManager.getConnection(
            getURL(), USER, PASSWORD);
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
