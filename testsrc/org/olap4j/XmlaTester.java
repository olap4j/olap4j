/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.driver.xmla.XmlaOlap4jDriver;
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
    final XmlaOlap4jDriver.Proxy proxy;
    private Connection connection;

    /**
     * Creates an XmlaTester
     *
     * @throws ClassNotFoundException on error
     * @throws IllegalAccessException on error
     * @throws InstantiationException on error
     * @throws NoSuchMethodException on error
     * @throws InvocationTargetException on error
     */
    public XmlaTester()
        throws ClassNotFoundException, IllegalAccessException,
        InstantiationException, NoSuchMethodException,
        InvocationTargetException
    {
        final Properties properties = TestContext.getTestProperties();
        final String catalogUrl =
            properties.getProperty(
                TestContext.Property.XMLA_CATALOG_URL.path);
        Map<String, String> catalogNameUrls =
            new HashMap<String, String>();
        catalogNameUrls.put("FoodMart", catalogUrl);
        String urlString =
            properties.getProperty(TestContext.Property.CONNECT_URL.path);

        final Class<?> clazz = Class.forName("mondrian.olap4j.MondrianInprocProxy");
        final Constructor<?> constructor =
            clazz.getConstructor(Map.class, String.class);
        this.proxy =
            (XmlaOlap4jDriver.Proxy) constructor.newInstance(
                catalogNameUrls, urlString);
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
        try {
            XmlaOlap4jDriver.THREAD_PROXY.set(proxy);
            Properties info = new Properties();
            info.setProperty(
                XmlaOlap4jDriver.Property.UseThreadProxy.name(), "true");
            info.setProperty(
                XmlaOlap4jDriver.Property.Catalog.name(), "FoodMart");
            connection =
                DriverManager.getConnection(
                    getURL(),
                    info);
            return connection;
        } finally {
            XmlaOlap4jDriver.THREAD_PROXY.set(null);
        }
    }

    public Connection createConnectionWithUserPassword() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("oops", e);
        }
        try {
            XmlaOlap4jDriver.THREAD_PROXY.set(proxy);
            Properties info = new Properties();
            info.setProperty("UseThreadProxy", "true");
            return DriverManager.getConnection(
                getURL(), USER, PASSWORD);
        } finally {
            XmlaOlap4jDriver.THREAD_PROXY.set(null);
        }
    }

    public String getDriverUrlPrefix() {
        return DRIVER_URL_PREFIX;
    }

    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    public String getURL() {
        return "jdbc:xmla:Server=http://foo;UseThreadProxy=true";
    }

    public Flavor getFlavor() {
        return Flavor.XMLA;
    }

    public TestContext.Wrapper getWrapper() {
        return TestContext.Wrapper.NONE;
    }

    public static final String DRIVER_CLASS_NAME =
         "org.olap4j.driver.xmla.XmlaOlap4jDriver";

    public static final String DRIVER_URL_PREFIX = "jdbc:xmla:";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
}

// End XmlaTester.java
