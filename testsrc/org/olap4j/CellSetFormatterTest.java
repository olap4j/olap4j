/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2009-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import junit.framework.TestCase;

import org.olap4j.test.TestContext;
import org.olap4j.query.TraditionalCellSetFormatter;
import org.olap4j.query.RectangularCellSetFormatter;

import java.sql.SQLException;
import java.sql.Connection;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Unit test for converting MDX CellSets to text.
 *
 * @see org.olap4j.query.CellSetFormatter
 * @see org.olap4j.query.RectangularCellSetFormatter
 * @see org.olap4j.query.TraditionalCellSetFormatter
 *
 * @author jhyde
 * @version $Id$
 */
public class CellSetFormatterTest extends TestCase {
    final String query1 =
        "select\n"
        + "  crossjoin(\n"
        + "    {[Time].[1997].[Q1], [Time].[1997].[Q2].[4]},\n"
        + "    {[Measures].[Unit Sales], [Measures].[Store Sales]}) on 0,\n"
        + "  {[USA].[CA].[Los Angeles],\n"
        + "   [USA].[WA].[Seattle],\n"
        + "   [USA].[CA].[San Francisco]} on 1\n"
        + "FROM [Sales]";


    /**
     * Asserts that a query is formatted to an expected piece of text.
     *
     * @param queryString MDX query
     * @param format Desired format
     * @param expected Expected formatted text
     * @throws SQLException On error
     */
    private void assertFormat(
        String queryString,
        Format format,
        String expected) throws SQLException
    {
        final TestContext.Tester tester = TestContext.instance().getTester();
        Connection connection = null;
        try {
            connection = tester.createConnection();
            OlapConnection olapConnection =
                tester.getWrapper().unwrap(
                    connection,
                    OlapConnection.class);
            CellSet result =
                olapConnection.prepareOlapStatement(queryString).executeQuery();
            String resultString = toString(result, format);
            TestContext.assertEqualsVerbose(
                expected,
                resultString);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Converts a {@link CellSet} to text.
     *
     * @param cellSet Query result
     * @param format Format
     * @return Result as text
     */
    static String toString(CellSet cellSet, Format format) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        switch (format) {
        case TRADITIONAL:
            new TraditionalCellSetFormatter().format(cellSet, pw);
            break;
        case COMPACT_RECTANGULAR:
        case RECTANGULAR:
            new RectangularCellSetFormatter(
                format == Format.COMPACT_RECTANGULAR)
                .format(cellSet, pw);
            break;
        }
        pw.flush();
        return sw.toString();
    }

    public enum Format {
        /**
         * Traditional format, one row per cell.
         */
        TRADITIONAL,

        /**
         * Rectangular format that is similar to {@link #RECTANGULAR} but omits
         * vertical bars and is therefore more compact.
         */
        COMPACT_RECTANGULAR,

        /**
         * Rectangular format that uses vertical bars and hyphens to draw a
         * grid.
         */
        RECTANGULAR
    }

    // ~ Tests follow ==========================================================

    public void testQuery1Traditional() throws SQLException {
        assertFormat(
            query1,
            Format.TRADITIONAL,
            TestContext.fold(
                "Axis #0:\n"
                + "{[Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Product].[All Products], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n"
                + "Axis #1:\n"
                + "{[Time].[1997].[Q1], [Measures].[Unit Sales]}\n"
                + "{[Time].[1997].[Q1], [Measures].[Store Sales]}\n"
                + "{[Time].[1997].[Q2].[4], [Measures].[Unit Sales]}\n"
                + "{[Time].[1997].[Q2].[4], [Measures].[Store Sales]}\n"
                + "Axis #2:\n"
                + "{[Store].[All Stores].[USA].[CA].[Los Angeles]}\n"
                + "{[Store].[All Stores].[USA].[WA].[Seattle]}\n"
                + "{[Store].[All Stores].[USA].[CA].[San Francisco]}\n"
                + "Row #0: 6,373\n"
                + "Row #0: 13,736.97\n"
                + "Row #0: 1,865\n"
                + "Row #0: 3,917.49\n"
                + "Row #1: 6,098\n"
                + "Row #1: 12,760.64\n"
                + "Row #1: 2,121\n"
                + "Row #1: 4,444.06\n"
                + "Row #2: 439\n"
                + "Row #2: 936.51\n"
                + "Row #2: 149\n"
                + "Row #2: 327.33\n"));
    }

    public void testQuery1Compact() throws SQLException {
        assertFormat(
            query1,
            Format.COMPACT_RECTANGULAR,
            TestContext.fold(
                "                     1997                              \n"
                + "                     Q1                     Q2         \n"
                + "                                            4          \n"
                + "                     Unit Sales Store Sales Unit Sales Store Sales\n"
                + "=== == ============= ========== =========== ========== ===========\n"
                + "USA CA Los Angeles        6,373   13,736.97      1,865    3,917.49\n"
                + "    WA Seattle            6,098   12,760.64      2,121    4,444.06\n"
                + "    CA San Francisco        439      936.51        149      327.33\n"));
    }

    public void testQuery1Rectangular() throws SQLException {
        assertFormat(
            query1,
            Format.RECTANGULAR,
            TestContext.fold(
                "|                          | 1997                                                |\n"
                + "|                          | Q1                       | Q2                       |\n"
                + "|                          |                          | 4                        |\n"
                + "|                          | Unit Sales | Store Sales | Unit Sales | Store Sales |\n"
                + "+-----+----+---------------+------------+-------------+------------+-------------+\n"
                + "| USA | CA | Los Angeles   |      6,373 |   13,736.97 |      1,865 |    3,917.49 |\n"
                + "|     | WA | Seattle       |      6,098 |   12,760.64 |      2,121 |    4,444.06 |\n"
                + "|     | CA | San Francisco |        439 |      936.51 |        149 |      327.33 |\n"));
    }

    public void testQueryAllRectangular() throws SQLException {
        // Similar query with an 'all' member on rows. Need an extra column.
        assertFormat(
            "select\n"
            + "  crossjoin(\n"
            + "    {[Time].[1997].[Q1], [Time].[1997].[Q2].[4]},\n"
            + "    {[Measures].[Unit Sales], [Measures].[Store Sales]}) on 0,\n"
            + "  {[Store],\n"
            + "   [Store].[USA],\n"
            + "   [Store].[USA].[CA],\n"
            + "   [Store].[USA].[CA].[Los Angeles],\n"
            + "   [Store].[USA].[WA]} on 1\n"
            + "FROM [Sales]",
            Format.COMPACT_RECTANGULAR,
            TestContext.fold(
                "                              1997                              \n"
                + "                              Q1                     Q2         \n"
                + "                                                     4          \n"
                + "                              Unit Sales Store Sales Unit Sales Store Sales\n"
                + "========== === == =========== ========== =========== ========== ===========\n"
                + "All Stores                        66,291  139,628.35     20,179   42,878.25\n"
                + "           USA                    66,291  139,628.35     20,179   42,878.25\n"
                + "               CA                 16,890   36,175.20      6,382   13,605.89\n"
                + "                  Los Angeles      6,373   13,736.97      1,865    3,917.49\n"
                + "               WA                 30,114   63,282.86      9,896   20,926.37\n"));
    }

    public void testNonParentRepeatsRectangular() throws SQLException {
        // "Food.Dairy" follows "Drink.Dairy" but there should still be a '|'
        // because they are different members with the same caption.
        assertFormat(
            "select\n"
            + "  {[Measures].[Unit Sales], [Measures].[Store Sales]} on 1,\n"
            + "  {[Product],\n"
            + "   [Product].[Drink].[Dairy],\n"
            + "   [Product].[Food].[Dairy],\n"
            + "   [Product].[Food]} on 0\n"
            + "FROM [Sales]",
            Format.RECTANGULAR,
            TestContext.fold(
                "|             | All Products                                              |\n"
                + "|             |              | Drink        | Food                        |\n"
                + "|             |              | Dairy        | Dairy        |              |\n"
                + "+-------------+--------------+--------------+--------------+--------------+\n"
                + "| Unit Sales  |      266,773 |        4,186 |       12,885 |      191,940 |\n"
                + "| Store Sales |   565,238.13 |     7,058.60 |    30,508.85 |   409,035.59 |\n"));

        assertFormat(
            "select\n"
            + "  {[Measures].[Unit Sales], [Measures].[Store Sales]} on 1,\n"
            + "  {[Product],\n"
            + "   [Product].[Drink].[Dairy],\n"
            + "   [Product].[Food].[Dairy],\n"
            + "   [Product].[Food]} on 0\n"
            + "FROM [Sales]",
            Format.COMPACT_RECTANGULAR,
            TestContext.fold(
                "            All Products                           \n"
                + "                         Drink        Food         \n"
                + "                         Dairy        Dairy        \n"
                + "=========== ============ ============ ============ ============\n"
                + "Unit Sales       266,773        4,186       12,885      191,940\n"
                + "Store Sales   565,238.13     7,058.60    30,508.85   409,035.59\n"));

        // Passes the test: Dairy is repeated.
        // However, the empty last column, signifying [Food] with no child,
        // is not ideal. What would be better?
        assertFormat(
            "select\n"
            + "  {[Measures].[Unit Sales], [Measures].[Store Sales]} on 0,\n"
            + "  {[Product],\n"
            + "   [Product].[Drink].[Dairy],\n"
            + "   [Product].[Food].[Dairy],\n"
            + "   [Product].[Food]} on 1\n"
            + "FROM [Sales]",
            Format.COMPACT_RECTANGULAR,
            TestContext.fold(
                "                         Unit Sales Store Sales\n"
                + "============ ===== ===== ========== ===========\n"
                + "All Products                266,773  565,238.13\n"
                + "             Drink Dairy      4,186    7,058.60\n"
                + "             Food  Dairy     12,885   30,508.85\n"
                + "                            191,940  409,035.59\n"));
    }

    public void testEmptyRowsRectangular() throws SQLException {
        assertFormat(
            "select\n"
            + "  crossjoin(\n"
            + "    {[Time].[1997].[Q1], [Time].[1997].[Q2].[4]},\n"
            + "    {[Measures].[Unit Sales], [Measures].[Store Sales]}) on 0,\n"
            + "  {[Store].Parent} on 1\n"
            + "FROM [Sales]",
            Format.COMPACT_RECTANGULAR,
            TestContext.fold(
                "1997                              \n"
                + "Q1                     Q2         \n"
                + "                       4          \n"
                + "Unit Sales Store Sales Unit Sales Store Sales\n"
                + "========== =========== ========== ===========\n"));
    }

    public void testEmptyColumnsRectangular() throws SQLException {
        final
        String
            queryString =
            "select\n"
            + "  crossjoin(\n"
            + "    {[Product].Parent},\n"
            + "    {[Measures].[Unit Sales], [Measures].[Store Sales]}) on 0,\n"
            + "  {[Store],\n"
            + "   [Store].[USA],\n"
            + "   [Store].[USA].[CA],\n"
            + "   [Store].[USA].[CA].[Los Angeles],\n"
            + "   [Store].[USA].[WA]} on 1\n"
            + "FROM [Sales]";

        assertFormat(
            queryString,
            Format.COMPACT_RECTANGULAR,
            TestContext.fold(
                "All Stores        \n"
                + "           USA    \n"
                + "               CA \n"
                + "                  Los Angeles\n"
                + "               WA \n"));

        assertFormat(
            queryString,
            Format.RECTANGULAR,
            TestContext.fold(
                "| All Stores |     |    |             |\n"
                + "|            | USA |    |             |\n"
                + "|            |     | CA |             |\n"
                + "|            |     |    | Los Angeles |\n"
                + "|            |     | WA |             |\n"));
    }

    public void testZeroAxesRectangular() throws SQLException {
        final String mdx =
            "select\n"
            + "from [Sales]\n"
            + "where ([Measures].[Store Sales], [Time].[1997].[Q2])";
        assertFormat(
            mdx,
            Format.RECTANGULAR,
            TestContext.fold(
                "| 132,666.27 |\n"));
        assertFormat(
            mdx,
            Format.COMPACT_RECTANGULAR,
            TestContext.fold(
                "132,666.27\n"));
    }

    public void testOneAxisRectangular() throws SQLException {
        assertFormat(
            "select\n"
            + "  crossjoin(\n"
            + "    {[Time].[1997].[Q1], [Time].[1997].[Q2].[4]},\n"
            + "    {[Measures].[Unit Sales], [Measures].[Store Sales]}) on 0\n"
            + "FROM [Sales]",
            Format.RECTANGULAR,
            TestContext.fold(
                "| 1997                                                |\n"
                + "| Q1                       | Q2                       |\n"
                + "|                          | 4                        |\n"
                + "| Unit Sales | Store Sales | Unit Sales | Store Sales |\n"
                + "+------------+-------------+------------+-------------+\n"
                + "|     66,291 |  139,628.35 |     20,179 |   42,878.25 |\n"));
    }

    public void testThreeAxes() throws SQLException {
        assertFormat(
            "select\n"
            + "  crossjoin(\n"
            + "    {[Time].[1997].[Q1], [Time].[1997].[Q2].[4]},\n"
            + "    {[Measures].[Unit Sales], [Measures].[Store Sales]}) on 0,\n"
            + "  {[Store],\n"
            + "   [Store].[USA],\n"
            + "   [Store].[USA].[CA],\n"
            + "   [Store].[USA].[CA].[Los Angeles],\n"
            + "   [Store].[USA].[WA]} on 1,\n"
            + "  {[Product].[Drink], [Product].[Food].[Dairy]} on 2\n"
            + "FROM [Sales]",
            Format.RECTANGULAR,
            TestContext.fold(
                "\n"
                + "PAGES: [Product].[All Products].[Drink]\n"
                + "|                                     | 1997                                                |\n"
                + "|                                     | Q1                       | Q2                       |\n"
                + "|                                     |                          | 4                        |\n"
                + "|                                     | Unit Sales | Store Sales | Unit Sales | Store Sales |\n"
                + "+------------+-----+----+-------------+------------+-------------+------------+-------------+\n"
                + "| All Stores |     |    |             |      5,976 |   11,585.80 |      1,948 |    3,884.53 |\n"
                + "|            | USA |    |             |      5,976 |   11,585.80 |      1,948 |    3,884.53 |\n"
                + "|            |     | CA |             |      1,654 |    3,309.75 |        583 |    1,212.78 |\n"
                + "|            |     |    | Los Angeles |        650 |    1,267.04 |        141 |      289.16 |\n"
                + "|            |     | WA |             |      2,679 |    5,106.36 |      1,007 |    1,978.99 |\n"
                + "\n"
                + "PAGES: [Product].[All Products].[Food].[Dairy]\n"
                + "|                                     | 1997                                                |\n"
                + "|                                     | Q1                       | Q2                       |\n"
                + "|                                     |                          | 4                        |\n"
                + "|                                     | Unit Sales | Store Sales | Unit Sales | Store Sales |\n"
                + "+------------+-----+----+-------------+------------+-------------+------------+-------------+\n"
                + "| All Stores |     |    |             |      3,262 |    7,708.75 |      1,047 |    2,512.80 |\n"
                + "|            | USA |    |             |      3,262 |    7,708.75 |      1,047 |    2,512.80 |\n"
                + "|            |     | CA |             |        845 |    2,004.95 |        297 |      721.34 |\n"
                + "|            |     |    | Los Angeles |        288 |      708.69 |         82 |      196.59 |\n"
                + "|            |     | WA |             |      1,504 |    3,544.86 |        533 |    1,275.98 |\n"));
    }

    public void testFourAxes() throws SQLException {
        assertFormat(
            "select\n"
            + "  {[Time].[1997].[Q1], [Time].[1997].[Q2].[4]} on 0,\n"
            + "  {[Store].[USA],\n"
            + "   [Store].[USA].[OR]} on 1,\n"
            + "  {[Product].[Drink], [Product].[Food].[Dairy]} on 2,\n"
            + "  Crossjoin(\n"
            + "    {[Marital Status].Members},\n"
            + "    {[Gender].[F], [Gender].[M]}) on 3\n"
            + "FROM [Sales]\n"
            + "WHERE [Measures].[Store Sales]",
            Format.RECTANGULAR,
            TestContext.fold(
                "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status], [Gender].[All Gender].[F]\n"
                + "PAGES: [Product].[All Products].[Drink]\n"
                + "|          | 1997                |\n"
                + "|          | Q1       | Q2       |\n"
                + "|          |          | 4        |\n"
                + "+-----+----+----------+----------+\n"
                + "| USA |    | 5,676.21 | 1,836.80 |\n"
                + "|     | OR | 1,569.00 |   327.26 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status], [Gender].[All Gender].[F]\n"
                + "PAGES: [Product].[All Products].[Food].[Dairy]\n"
                + "|          | 1997                |\n"
                + "|          | Q1       | Q2       |\n"
                + "|          |          | 4        |\n"
                + "+-----+----+----------+----------+\n"
                + "| USA |    | 3,873.00 | 1,378.20 |\n"
                + "|     | OR | 1,069.88 |   284.91 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status], [Gender].[All Gender].[M]\n"
                + "PAGES: [Product].[All Products].[Drink]\n"
                + "|          | 1997                |\n"
                + "|          | Q1       | Q2       |\n"
                + "|          |          | 4        |\n"
                + "+-----+----+----------+----------+\n"
                + "| USA |    | 5,909.59 | 2,047.73 |\n"
                + "|     | OR | 1,600.69 |   365.50 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status], [Gender].[All Gender].[M]\n"
                + "PAGES: [Product].[All Products].[Food].[Dairy]\n"
                + "|          | 1997                |\n"
                + "|          | Q1       | Q2       |\n"
                + "|          |          | 4        |\n"
                + "+-----+----+----------+----------+\n"
                + "| USA |    | 3,835.75 | 1,134.60 |\n"
                + "|     | OR | 1,089.06 |   230.57 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status].[M], [Gender].[All Gender].[F]\n"
                + "PAGES: [Product].[All Products].[Drink]\n"
                + "|          | 1997              |\n"
                + "|          | Q1       | Q2     |\n"
                + "|          |          | 4      |\n"
                + "+-----+----+----------+--------+\n"
                + "| USA |    | 3,099.69 | 971.79 |\n"
                + "|     | OR |   767.62 | 134.02 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status].[M], [Gender].[All Gender].[F]\n"
                + "PAGES: [Product].[All Products].[Food].[Dairy]\n"
                + "|          | 1997              |\n"
                + "|          | Q1       | Q2     |\n"
                + "|          |          | 4      |\n"
                + "+-----+----+----------+--------+\n"
                + "| USA |    | 2,125.13 | 732.95 |\n"
                + "|     | OR |   581.31 | 160.55 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status].[M], [Gender].[All Gender].[M]\n"
                + "PAGES: [Product].[All Products].[Drink]\n"
                + "|          | 1997              |\n"
                + "|          | Q1       | Q2     |\n"
                + "|          |          | 4      |\n"
                + "+-----+----+----------+--------+\n"
                + "| USA |    | 2,874.11 | 914.70 |\n"
                + "|     | OR |   643.61 | 132.37 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status].[M], [Gender].[All Gender].[M]\n"
                + "PAGES: [Product].[All Products].[Food].[Dairy]\n"
                + "|          | 1997              |\n"
                + "|          | Q1       | Q2     |\n"
                + "|          |          | 4      |\n"
                + "+-----+----+----------+--------+\n"
                + "| USA |    | 1,920.54 | 528.54 |\n"
                + "|     | OR |   519.94 | 108.96 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status].[S], [Gender].[All Gender].[F]\n"
                + "PAGES: [Product].[All Products].[Drink]\n"
                + "|          | 1997              |\n"
                + "|          | Q1       | Q2     |\n"
                + "|          |          | 4      |\n"
                + "+-----+----+----------+--------+\n"
                + "| USA |    | 2,576.52 | 865.01 |\n"
                + "|     | OR |   801.38 | 193.24 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status].[S], [Gender].[All Gender].[F]\n"
                + "PAGES: [Product].[All Products].[Food].[Dairy]\n"
                + "|          | 1997              |\n"
                + "|          | Q1       | Q2     |\n"
                + "|          |          | 4      |\n"
                + "+-----+----+----------+--------+\n"
                + "| USA |    | 1,747.87 | 645.25 |\n"
                + "|     | OR |   488.57 | 124.36 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status].[S], [Gender].[All Gender].[M]\n"
                + "PAGES: [Product].[All Products].[Drink]\n"
                + "|          | 1997                |\n"
                + "|          | Q1       | Q2       |\n"
                + "|          |          | 4        |\n"
                + "+-----+----+----------+----------+\n"
                + "| USA |    | 3,035.48 | 1,133.03 |\n"
                + "|     | OR |   957.08 |   233.13 |\n"
                + "\n"
                + "CHAPTERS: [Marital Status].[All Marital Status].[S], [Gender].[All Gender].[M]\n"
                + "PAGES: [Product].[All Products].[Food].[Dairy]\n"
                + "|          | 1997              |\n"
                + "|          | Q1       | Q2     |\n"
                + "|          |          | 4      |\n"
                + "+-----+----+----------+--------+\n"
                + "| USA |    | 1,915.21 | 606.06 |\n"
                + "|     | OR |   569.12 | 121.61 |\n"));
    }
}

// End CellSetFormatterTest.java
