/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.test;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;
import java.sql.SQLException;
import java.sql.DriverManager;

import org.olap4j.metadata.Member;
import org.olap4j.*;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.ParseTreeWriter;
import junit.framework.ComparisonFailure;
import mondrian.olap.Util;

/**
 * Context for olap4j tests.
 *
 * <p>Also provides static utility methods such as {@link #fold(String)}.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 7, 2007
*/
public class TestContext {
    public static final String NL = System.getProperty("line.separator");
    private static final TestContext INSTANCE = new TestContext();
    private static final String lineBreak2 = "\\\\n\" +" + NL + "\"";
    private static final String lineBreak3 = "\\n\" +" + NL + "\"";
    private static final Pattern LineBreakPattern =
        Pattern.compile("\r\n|\r|\n");
    private static final Pattern TabPattern = Pattern.compile("\t");

    /**
     * Converts a string constant into environment-specific line endings.
     * Use this method when specifying the string result expected from a test.
     *
     * @param string String to convert
     * @return String with newline characters converted into
     */
    public static String fold(String string) {
        if (!NL.equals("\n")) {
            string = string.replace("\n", NL);
        }
        return string;
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
        print(cellSet, pw);
        pw.flush();
        return sw.toString();
    }

    private static void print(CellSet cellSet, PrintWriter pw) {
        pw.println("Axis #0:");
        printAxis(pw, cellSet.getFilterAxis());
        final List<CellSetAxis> axes = cellSet.getAxes();
        int i = 0;
        for (CellSetAxis axis : axes) {
            pw.println("Axis #" + (++i) + ":");
            printAxis(pw, axis);
        }
        // Usually there are 3 axes: {filter, columns, rows}. Position is a
        // {column, row} pair. We call printRows with axis=2. When it
        // recurses to axis=-1, it prints.
        List<Integer> pos = new ArrayList<Integer>(axes.size());
        for (CellSetAxis axis : axes) {
            pos.add(-1);
        }
        printRows(cellSet, pw, axes.size() - 1, pos);
    }

    private static void printRows(
        CellSet cellSet, PrintWriter pw, int axis, List<Integer> pos)
    {
        CellSetAxis _axis = axis < 0 ?
            cellSet.getFilterAxis() :
            cellSet.getAxes().get(axis);
        List<Position> positions = _axis.getPositions();
        int i = 0;
        for (Position position : positions) {
            if (axis < 0) {
                if (i > 0) {
                    pw.print(", ");
                }
                printCell(cellSet, pw, pos);
            } else {
                pos.set(axis, i);
                if (axis == 0) {
                    int row = axis + 1 < pos.size() ? pos.get(axis + 1) : 0;
                    pw.print("Row #" + row + ": ");
                }
                printRows(cellSet, pw, axis - 1, pos);
                if (axis == 0) {
                    pw.println();
                }
            }
            i++;
        }
    }

    private static void printAxis(PrintWriter pw, CellSetAxis axis) {
        List<Position> positions = axis.getPositions();
        for (Position position: positions) {
            boolean firstTime = true;
            pw.print("{");
            for (Member member : position.getMembers()) {
                if (! firstTime) {
                    pw.print(", ");
                }
                pw.print(member.getUniqueName());
                firstTime = false;
            }
            pw.println("}");
        }
    }

    private static void printCell(
        CellSet cellSet, PrintWriter pw, List<Integer> pos)
    {
        Cell cell = cellSet.getCell(pos);
        pw.print(cell.getFormattedValue());
    }

    public static TestContext instance() {
        return INSTANCE;
    }

    public OlapConnection getConnection() throws SQLException {
        java.sql.Connection connection = createConnection();
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);
        return olapConnection;
    }

    public java.sql.Connection createConnection() throws SQLException {
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

    public java.sql.Connection createConnectionWithUserPassword() throws SQLException {
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
        return getDefaultConnectString();
    }

    public boolean isMondrian() {
        return true;
    }

    public static String getDefaultConnectString() {
//            return "jdbc:mondrian:Jdbc='jdbc:odbc:MondrianFoodMart';Catalog='../mondrian/demo/FoodMart.xml';JdbcDrivers=sun.jdbc.odbc.JdbcOdbcDriver;";
        return "jdbc:mondrian:Jdbc='jdbc:oracle:thin:foodmart/foodmart@//marmalade.hydromatic.net:1521/XE';Catalog='../mondrian/demo/FoodMart.xml';JdbcDrivers=oracle.jdbc.OracleDriver;";
    }

    public static final String DRIVER_CLASS_NAME = "mondrian.olap4j.MondrianOlap4jDriver";

    public static final String DRIVER_URL_PREFIX = "jdbc:mondrian:";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

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
     * Checks that an actual string matches an expected string. If they do not,
     * throws a {@link junit.framework.ComparisonFailure} and prints the
     * difference, including the actual string as an easily pasted Java string
     * literal.
     *
     * @param expected Expected string
     * @param actual Actual string returned by test case
     * @param java Whether to print the actual value as a Java string literal
     * if the strings differ
     * @param message Message to print if the strings differ
     */
    public static void assertEqualsVerbose(
            String expected,
            String actual,
            boolean java,
            String message)
    {
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
                "Expected:" + NL + expected + NL +
                "Actual:" + NL + actual + NL;
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
        // into ["string with \"quotes\" split\n" +
        // "across lines
        //
        s = Util.replace(s, "\\", "\\\\");
        s = Util.replace(s, "\"", "\\\"");
        s = LineBreakPattern.matcher(s).replaceAll(lineBreak2);
        s = TabPattern.matcher(s).replaceAll("\\\\t");
        s = "\"" + s + "\"";
        String spurious = " +" + NL + "\"\"";
        if (s.endsWith(spurious)) {
            s = s.substring(0, s.length() - spurious.length());
        }
        if (s.indexOf(lineBreak3) >= 0) {
            s = "fold(" + NL + s + ")";
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
}

// End TestContext.java
