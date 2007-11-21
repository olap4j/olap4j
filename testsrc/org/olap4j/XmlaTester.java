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
import org.xml.sax.SAXException;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.URL;

import mondrian.tui.XmlaSupport;

import javax.servlet.ServletException;

/**
 * Implementation of {@link org.olap4j.test.TestContext.Tester} which speaks
 * to the olap4j driver for XML/A.
 *
 * @author jhyde
 * @version $Id$
 */
public class XmlaTester implements TestContext.Tester {
    XmlaOlap4jDriver.Proxy proxy =
        new MondrianInprocProxy();

    public Connection createConnection() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("oops", e);
        }
        try {
            XmlaOlap4jDriver.THREAD_PROXY.set(proxy);
            Properties info = new Properties();
            info.setProperty("UseThreadProxy", "true");
            return
                DriverManager.getConnection(
                    getURL(),
                    info);
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

    public boolean isMondrian() {
        return false;
    }

    public static final String DRIVER_CLASS_NAME =
         "org.olap4j.driver.xmla.XmlaOlap4jDriver";

    public static final String DRIVER_URL_PREFIX = "jdbc:xmla:";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    /**
     * Proxy which implements XMLA requests by talking to mondrian
     * in-process. This is more convenient to debug than an inter-process
     * request using HTTP.
     */
    private static class MondrianInprocProxy implements XmlaOlap4jDriver.Proxy {
        public InputStream get(URL url, String request) throws IOException {
            try {
                Map<String, String> map = new HashMap<String, String>();
                String urlString = url.toString();
                byte[] bytes = XmlaSupport.processSoapXmla(
                    request, urlString, map, null);
                return new ByteArrayInputStream(bytes);
            } catch (ServletException e) {
                throw new RuntimeException(
                    "Error while reading '" + url + "'", e);
            } catch (SAXException e) {
                throw new RuntimeException(
                    "Error while reading '" + url + "'", e);
            }
        }
    }
}

// End XmlaTester.java
