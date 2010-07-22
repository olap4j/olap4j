/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.test;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.sql.*;

import org.olap4j.*;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.ParseTreeWriter;
import org.olap4j.layout.TraditionalCellSetFormatter;

import org.apache.commons.dbcp.*;
import junit.framework.ComparisonFailure;
import junit.framework.Assert;

/**
 * Context for olap4j tests.
 *
 * <p>Also provides static utility methods such as {@link #fold(String)}.
 *
 * <p>Properties used by the test framework are described in
 * {@link org.olap4j.test.TestContext.Property}.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 7, 2007
*/
public class TestContext {
    public static final String NL = System.getProperty("line.separator");
    private static final String indent = "                ";
    private static final TestContext INSTANCE = new TestContext();
    private static final String lineBreak2 = "\\\\n\"" + NL + indent + "+ \"";
    private static final String lineBreak3 = "\\n\"" + NL + indent + "+ \"";
    private static final Pattern LineBreakPattern =
        Pattern.compile("\r\n|\r|\n");
    private static final Pattern TabPattern = Pattern.compile("\t");
    public static Properties testProperties;

    private final Tester tester = createTester();

    /**
     * Intentionally private: use {@link #instance()}.
     */
    private TestContext() {
    }

    /**
     * Converts a string constant into platform-specific line endings.
     *
     * @param string String where line endings are represented as linefeed "\n"
     * @return String where all linefeeds have been converted to
     * platform-specific (CR+LF on Windows, LF on Unix/Linux)
     */
    public static SafeString fold(String string) {
        if (!NL.equals("\n")) {
            string = Olap4jUtil.replace(string, "\n", NL);
        }
        if (string == null) {
            return null;
        } else {
            return new SafeString(string);
        }
    }

    /**
     * Converts an MDX parse tree to an MDX string
     *
     * @param node Parse tree
     * @return MDX string
     */
    public static String toString(ParseTreeNode node) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ParseTreeWriter parseTreeWriter = new ParseTreeWriter(pw);
        node.unparse(parseTreeWriter);
        pw.flush();
        return sw.toString();
    }

    /**
     * Formats a {@link org.olap4j.CellSet}.
     */
    public static String toString(CellSet cellSet) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
            new TraditionalCellSetFormatter().format(cellSet, pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * The default instance of TestContext.
     *
     * @return default TestContext
     */
    public static TestContext instance() {
        return INSTANCE;
    }

    /**
     * Checks that an actual string matches an expected string. If they do not,
     * throws a {@link junit.framework.ComparisonFailure} and prints the
     * difference, including the actual string as an easily pasted Java string
     * literal.
     *
     * @param expected Expected string
     * @param actual Actual string returned by test case
     */
    public static void assertEqualsVerbose(
        String expected,
        String actual)
    {
        assertEqualsVerbose(expected, actual, true, null);
    }

    /**
     * Checks that an actual string matches an expected string.
     *
     * <p>If they do not, throws a {@link ComparisonFailure} and prints the
     * difference, including the actual string as an easily pasted Java string
     * literal.
     *
     * @param expected Expected string
     * @param actual Actual string
     * @param java Whether to generate actual string as a Java string literal
     * if the values are not equal
     * @param message Message to display, optional
     */
    public static void assertEqualsVerbose(
        String expected,
        String actual,
        boolean java,
        String message)
    {
        assertEqualsVerbose(
            fold(expected), actual, java, message);
    }

    /**
     * Checks that an actual string matches an expected string. If they do not,
     * throws a {@link junit.framework.ComparisonFailure} and prints the
     * difference, including the actual string as an easily pasted Java string
     * literal.
     *
     * @param safeExpected Expected string, where all line endings have been
     * converted into platform-specific line endings
     * @param actual Actual string returned by test case
     * @param java Whether to print the actual value as a Java string literal
     * if the strings differ
     * @param message Message to print if the strings differ
     */
    public static void assertEqualsVerbose(
        SafeString safeExpected,
        String actual,
        boolean java,
        String message)
    {
        String expected = safeExpected == null ? null : safeExpected.s;
        if ((expected == null) && (actual == null)) {
            return;
        }
        if ((expected != null) && expected.equals(actual)) {
            return;
        }
        if (message == null) {
            message = "";
        } else {
            message += NL;
        }
        message +=
            "Expected:" + NL + expected + NL
            + "Actual:" + NL + actual + NL;
        if (java) {
            message += "Actual java:" + NL + toJavaString(actual) + NL;
        }
        throw new ComparisonFailure(message, expected, actual);
    }

    /**
     * Converts a string (which may contain quotes and newlines) into a java
     * literal.
     *
     * <p>For example, <code>
     * <pre>string with "quotes" split
     * across lines</pre>
     * </code> becomes <code>
     * <pre>"string with \"quotes\" split" + NL +
     *  "across lines"</pre>
     * </code>
     */
    static String toJavaString(String s) {
        // Convert [string with "quotes" split
        // across lines]
        // into ["string with \"quotes\" split\n"
        //                 + "across lines
        //
        s = Olap4jUtil.replace(s, "\\", "\\\\");
        s = Olap4jUtil.replace(s, "\"", "\\\"");
        s = LineBreakPattern.matcher(s).replaceAll(lineBreak2);
        s = TabPattern.matcher(s).replaceAll("\\\\t");
        s = "\"" + s + "\"";
        String spurious = NL + indent + "+ \"\"";
        if (s.endsWith(spurious)) {
            s = s.substring(0, s.length() - spurious.length());
        }
        if (s.indexOf(lineBreak3) >= 0) {
            s = "fold(" + NL + indent + s + ")";
        }
        return s;
    }

    /**
     * Quotes a pattern.
     */
    public static String quotePattern(String s)
    {
        s = s.replaceAll("\\\\", "\\\\");
        s = s.replaceAll("\\.", "\\\\.");
        s = s.replaceAll("\\+", "\\\\+");
        s = s.replaceAll("\\{", "\\\\{");
        s = s.replaceAll("\\}", "\\\\}");
        s = s.replaceAll("\\|", "\\\\||");
        s = s.replaceAll("[$]", "\\\\\\$");
        s = s.replaceAll("\\?", "\\\\?");
        s = s.replaceAll("\\*", "\\\\*");
        s = s.replaceAll("\\(", "\\\\(");
        s = s.replaceAll("\\)", "\\\\)");
        s = s.replaceAll("\\[", "\\\\[");
        s = s.replaceAll("\\]", "\\\\]");
        return s;
    }

    /**
     * Factory method for the {@link Tester}
     * object which determines which driver to test.
     *
     * @return a new Tester
     */
    private static Tester createTester() {
        Properties testProperties = getTestProperties();
        String helperClassName =
            testProperties.getProperty(Property.HELPER_CLASS_NAME.path);
        if (helperClassName == null) {
            helperClassName = "org.olap4j.MondrianTester";
        }
        Tester tester;
        try {
            Class<?> clazz = Class.forName(helperClassName);
            tester = (Tester) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }

        // Apply a wrapper, if the "org.olap4j.test.wrapper" property is
        // specified.
        String wrapperName = testProperties.getProperty(Property.WRAPPER.path);
        Wrapper wrapper;
        if (wrapperName == null || wrapperName.equals("")) {
            wrapper = Wrapper.NONE;
        } else {
            try {
                wrapper = Enum.valueOf(Wrapper.class, wrapperName);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Unknown wrapper value '" + wrapperName + "'");
            }
        }
        switch (wrapper) {
        case NONE:
            break;
        case DBCP:
            final BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(tester.getDriverClassName());
            dataSource.setUrl(tester.getURL());
            // need access to underlying connection so that we can call
            // olap4j-specific methods
            dataSource.setAccessToUnderlyingConnectionAllowed(true);
            tester = new DelegatingTester(tester) {
                public Connection createConnection()
                    throws SQLException
                {
                    return dataSource.getConnection();
                }

                public Wrapper getWrapper() {
                    return Wrapper.DBCP;
                }
            };
            break;
        }
        return tester;
    }

    /**
     * Enumeration of valid values for the
     * {@link org.olap4j.test.TestContext.Property#WRAPPER} property.
     */
    public enum Wrapper {
        /**
         * No wrapper.
         */
        NONE {
            public <T extends Statement> T unwrap(
                Statement statement,
                Class<T> clazz) throws SQLException
            {
                return ((OlapWrapper) statement).unwrap(clazz);
            }

            public <T extends Connection> T unwrap(
                Connection connection,
                Class<T> clazz) throws SQLException
            {
                return ((OlapWrapper) connection).unwrap(clazz);
            }
        },
        /**
         * Instructs the olap4j testing framework to wrap connections using
         * the Apache commons-dbcp connection-pooling framework.
         */
        DBCP {
            public <T extends Statement> T unwrap(
                Statement statement,
                Class<T> clazz) throws SQLException
            {
                return clazz.cast(
                    ((DelegatingStatement) statement).getInnermostDelegate());
            }

            public <T extends Connection> T unwrap(
                Connection connection,
                Class<T> clazz) throws SQLException
            {
                return clazz.cast(
                    ((DelegatingConnection) connection).getInnermostDelegate());
            }
        };

        /**
         * Removes wrappers from a statement.
         *
         * @param statement Statement
         * @param clazz Desired result type
         * @return Unwrapped object
         * @throws SQLException
         */
        public abstract <T extends Statement> T unwrap(
            Statement statement,
            Class<T> clazz) throws SQLException;

        /**
         * Removes wrappers from a connection.
         *
         * @param connection Connection
         * @param clazz Desired result type
         * @return Unwrapped object
         * @throws SQLException
         */
        public abstract <T extends Connection> T unwrap(
            Connection connection,
            Class<T> clazz) throws SQLException;
    }

    /**
     * Returns an object containing all properties needed by the test suite.
     *
     * <p>Consists of system properties, overridden by the contents of
     * "test.properties" in the current directory, if it exists, and
     * in any parent or ancestor directory. This allows you to invoke the
     * test from any sub-directory of the source root and still pick up the
     * right test parameters.
     *
     * @return object containing properties needed by the test suite
     */
    public static synchronized Properties getTestProperties() {
        if (testProperties == null) {
            testProperties = new Properties(System.getProperties());

            File dir = new File(System.getProperty("user.dir"));
            while (dir != null) {
                File file = new File(dir, "test.properties");
                if (file.exists()) {
                    try {
                        testProperties.load(new FileInputStream(file));
                    } catch (IOException e) {
                        // ignore
                    }
                }

                file = new File(new File(dir, "olap4j"), "test.properties");
                if (file.exists()) {
                    try {
                        testProperties.load(new FileInputStream(file));
                    } catch (IOException e) {
                        // ignore
                    }
                }

                dir = dir.getParentFile();
            }
        }
        return testProperties;
    }

    /**
     * Converts a {@link Throwable} to a stack trace.
     */
    public static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * Checks that an exception is not null and the stack trace contains a
     * given string. Fails otherwise.
     *
     * @param throwable Stack trace
     * @param pattern Seek string
     */
    public static void checkThrowable(Throwable throwable, String pattern) {
        if (throwable == null) {
            Assert.fail("query did not yield an exception");
        }
        String stackTrace = getStackTrace(throwable);
        if (stackTrace.indexOf(pattern) < 0) {
            Assert.fail(
                "error does not match pattern '" + pattern
                + "'; error is [" + stackTrace + "]");
        }
    }

    /**
     * Returns this context's tester.
     *
     * @return a tester
     */
    public Tester getTester() {
        return tester;
    }

    /**
     * Abstracts the information about specific drivers and database instances
     * needed by this test. This allows the same test suite to be used for
     * multiple implementations of olap4j.
     */
    public interface Tester {
        /**
         * Creates a connection
         *
         * @return connection
         * @throws SQLException on error
         */
        Connection createConnection() throws SQLException;

        /**
         * Returns the prefix of URLs recognized by this driver, for example
         * "jdbc:mondrian:"
         *
         * @return URL prefix
         */
        String getDriverUrlPrefix();

        /**
         * Returns the class name of the driver, for example
         * "mondrian.olap4j.MondrianOlap4jDriver".
         *
         * @return driver class name
         */
        String getDriverClassName();

        /**
         * Creates a connection using the
         * {@link java.sql.DriverManager#getConnection(String, String, String)}
         * method.
         *
         * @return connection
         * @throws SQLException on error
         */
        Connection createConnectionWithUserPassword() throws SQLException;

        /**
         * Returns the URL of the FoodMart database.
         *
         * @return URL of the FoodMart database
         */
        String getURL();

        /**
         * Returns an enumeration indicating the driver (or strictly, the family
         * of drivers) supported by this Tester. Allows the test suite to
         * disable tests or expect slightly different results if the
         * capabilities of OLAP servers are different.
         *
         * @return Flavor of driver/OLAP engine we are connecting to
         */
        Flavor getFlavor();

        /**
         * Returns a description of the wrapper, if any, around this connection.
         */
        Wrapper getWrapper();

        enum Flavor {
            MONDRIAN,
            XMLA,
            REMOTE_XMLA
        }
    }

    /**
     * Implementation of {@link Tester} that delegates to an underlying tester.
     */
    public static abstract class DelegatingTester implements Tester {
        protected final Tester tester;

        /**
         * Creates a DelegatingTester.
         *
         * @param tester Underlying tester to which calls are delegated
         */
        protected DelegatingTester(Tester tester) {
            this.tester = tester;
        }

        public Connection createConnection() throws SQLException {
            return tester.createConnection();
        }

        public String getDriverUrlPrefix() {
            return tester.getDriverUrlPrefix();
        }

        public String getDriverClassName() {
            return tester.getDriverClassName();
        }

        public Connection createConnectionWithUserPassword()
            throws SQLException
        {
            return tester.createConnectionWithUserPassword();
        }

        public String getURL() {
            return tester.getURL();
        }

        public Flavor getFlavor() {
            return tester.getFlavor();
        }

        public Wrapper getWrapper() {
            return tester.getWrapper();
        }
    }

    /**
     * Enumeration of system properties that mean something to the olap4j
     * testing framework.
     */
    public enum Property {

        /**
         * Name of the class used by the test infrastructure to make connections
         * to the olap4j data source and perform other housekeeping operations.
         * Valid values include "org.olap4j.MondrianTester" (the default)
         * and "org.olap4j.XmlaTester".
         */
        HELPER_CLASS_NAME("org.olap4j.test.helperClassName"),

        /**
         * Test property that provides the value of returned by the
         * {@link Tester#getURL} method.
         */
        CONNECT_URL("org.olap4j.test.connectUrl"),

        /**
         * Test property that provides the URL name of the catalog for the XMLA
         * driver.
         */
        XMLA_CATALOG_URL("org.olap4j.XmlaTester.CatalogUrl"),

        /**
         * Test property related to the remote XMLA tester.
         * Must be a valid XMLA driver URL.
         */
        REMOTE_XMLA_URL("org.olap4j.RemoteXmlaTester.JdbcUrl"),

        /**
         * Test property related to the remote XMLA tester.
         * User name to use.
         */
        REMOTE_XMLA_USERNAME("org.olap4j.RemoteXmlaTester.Username"),

        /**
         * Test property related to the remote XMLA tester.
         * Password to use.
         */
        REMOTE_XMLA_PASSWORD("org.olap4j.RemoteXmlaTester.Password"),

        /**
         * Test property that indicates the wrapper to place around the
         * connection. Valid values are defined by the {@link Wrapper}
         * enumeration, such as "Dbcp". If not specified, the connection is used
         * without a connection pool.
         */
        WRAPPER("org.olap4j.test.wrapper"),
        ;

        public final String path;

        /**
         * Creates a Property enum value.
         *
         * @param path Full name of property, e.g. "org.olap4.foo.Bar".
         */
        private Property(String path) {
            this.path = path;
        }
    }

    /**
     * Wrapper around a string that indicates that all line endings have been
     * converted to platform-specific line endings.
     *
     * @see TestContext#fold
     */
    public static class SafeString {

        public final String s;

        /**
         * Creates a SafeString.
         * @param s String
         */
        private SafeString(String s) {
            this.s = s;
        }
    }
}

// End TestContext.java
