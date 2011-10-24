/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.test.TestContext;
import org.olap4j.test.TestContext.Tester;
import org.olap4j.test.TestContext.Wrapper;

import java.sql.*;
import java.util.Properties;

/**
 * Implementation of {@link org.olap4j.test.TestContext.Tester} which speaks
 * to remote XML/A servers.
 *
 * @author Luc Boudreau
 * @version $Id$
 */
public class RemoteXmlaTester implements Tester {

    public static final String DRIVER_URL_PREFIX = "jdbc:xmla:";
    public static final String DRIVER_CLASS_NAME =
        "org.olap4j.driver.xmla.XmlaOlap4jDriver";

    private String url = null;
    private String user = null;
    private String password = null;
    private final TestContext testContext;

    /**
     * Creates a RemoteXmlaTester.
     *
     * <p>The {@link org.olap4j.test.TestContext.Tester} API requires a public
     * constructor with a {@link org.olap4j.test.TestContext} parameter.
     *
     * @param testContext Test context
     */
    public RemoteXmlaTester(TestContext testContext) {
        this.testContext = testContext;
        final Properties properties = testContext.getProperties();
        this.url =
            properties.getProperty(
                TestContext.Property.REMOTE_XMLA_URL.path);
        if (url == null) {
            throw new RuntimeException(
                "Property " + TestContext.Property.REMOTE_XMLA_URL
                + " must be specified");
        }
        this.user =
            properties.getProperty(
                TestContext.Property.REMOTE_XMLA_USERNAME.path);
        this.password =
            properties.getProperty(
                TestContext.Property.REMOTE_XMLA_PASSWORD.path);
    }

    public TestContext getTestContext() {
        return testContext;
    }

    public Connection createConnection() throws SQLException {
        return this.createConnection(url, null, null);
    }

    public Connection createConnectionWithUserPassword() throws SQLException {
        return this.createConnection(url, user, password);
    }

    private Connection createConnection(
        String url, String user, String password)
    {
        try {
            Class.forName(DRIVER_CLASS_NAME);
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    public String getDriverUrlPrefix() {
        return DRIVER_URL_PREFIX;
    }

    public Flavor getFlavor() {
        return Flavor.REMOTE_XMLA;
    }

    public String getURL() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url  = url;
    }

    public Wrapper getWrapper() {
        return TestContext.Wrapper.NONE;
    }
}
// End RemoteXmlaTester.java

