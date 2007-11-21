/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.test.TestContext;

import java.sql.*;
import java.util.Properties;

/**
 * Implementation of {@link org.olap4j.test.TestContext.Tester} which speaks to
 * the mondrian olap4j driver.
 *
 * @author jhyde
 * @version $Id$
 */
public class MondrianTester implements TestContext.Tester {

    public Connection createConnection() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("oops", e);
        }
        return
            DriverManager.getConnection(
                getURL(),
                new Properties());
    }

    public Connection createConnectionWithUserPassword() throws SQLException {
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
        // This property is usually defined in build.properties. See
        // examples in that file.
        return TestContext.getTestProperties().getProperty(
            "org.olap4j.test.connectUrl");
    }

    public boolean isMondrian() {
        return true;
    }

    public static final String DRIVER_CLASS_NAME =
        "mondrian.olap4j.MondrianOlap4jDriver";

    public static final String DRIVER_URL_PREFIX = "jdbc:mondrian:";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";
}

// End MondrianTester.java
