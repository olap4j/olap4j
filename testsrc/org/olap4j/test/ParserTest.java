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

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.mdx.parser.MdxParseException;
import org.olap4j.mdx.*;
import org.olap4j.OlapConnection;
import org.olap4j.Axis;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tests the MDX parser.
 *
 * @author gjohnson, jhyde
 * @version $Id$
 */
public class ParserTest extends TestCase {
    private static final Pattern lineColPattern =
        Pattern.compile("At line ([0-9]+), column ([0-9]+)");

    private static final Pattern lineColTwicePattern =
        Pattern.compile(
            "(?s)From line ([0-9]+), column ([0-9]+) to line ([0-9]+), column ([0-9]+): (.*)");

    public ParserTest(String name) {
        super(name);
    }

    private MdxParser createParser() {
        try {
            OlapConnection olapConnection = TestContext.instance().getConnection();
            return olapConnection.getParserFactory().createMdxParser(olapConnection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void testAddCarets()
    {
        assertEquals(
            "values (^foo^)",
            new ParseRegion(1, 9, 1, 11).annotate("values (foo)"));
        assertEquals(
            "abc^def",
            new ParseRegion(1, 4, 1, 4).annotate("abcdef"));
        assertEquals(
            "abcdef^",
            new ParseRegion(1, 7, 1, 7).annotate("abcdef"));

        assertEquals(
            "[1:9, 1:11]",
            ParseRegion.findPos("values (^foo^)").region.toString());

        assertEquals(
            "[1:4]",
            ParseRegion.findPos("abc^def").region.toString());

        assertEquals(
            "[1:7]",
            ParseRegion.findPos("abcdef^").region.toString());

        assertNull(ParseRegion.findPos("abc").region);
    }

    public void testAxisParsing() throws Exception {
        checkAxisAllWays(0, "COLUMNS");
        checkAxisAllWays(1, "ROWS");
        checkAxisAllWays(2, "PAGES");
        checkAxisAllWays(3, "CHAPTERS");
        checkAxisAllWays(4, "SECTIONS");
    }

    private void checkAxisAllWays(int axisOrdinal, String axisName) {
        checkAxis(axisOrdinal + "", axisName);
        checkAxis("AXIS(" + axisOrdinal + ")", axisName);
        checkAxis(axisName, axisName);
    }

    private void checkAxis(
            String s,
            String expectedName) {
        MdxParser p = createParser();
        String q = "select [member] on " + s + " from [cube]";
        SelectNode selectNode = p.parseSelect(q);
        List<AxisNode> axes = selectNode.getAxisList();

        assertEquals("Number of axes must be 1", 1, axes.size());
        assertEquals("Axis index name must be correct",
                expectedName, axes.get(0).getAxis().name());
    }

    public void testNegativeCases() throws Exception {
        assertParseQueryFails(
            "^s^ from sales",
            "Syntax error at \\[1:1\\], token 's'");

        assertParseQueryFails(
            "^seleg^ from sales",
            "Syntax error at \\[1:1, 1:5\\], token 'seleg'");

        assertParseQueryFails(
            "^seleg^   from sales",
            "Syntax error at \\[1:1, 1:5\\], token 'seleg'");

        assertParseQueryFails(
            "select [member] on ^axis(1.7)^ from sales",
            "(?s).*The axis number must be an integer.*");

        assertParseQueryFails(
            "select [member] on axis(-^ ^1) from sales",
            "Syntax error at \\[1:26\\], token '-'");

        assertParseQueryFails(
            "select [member] on axis(-^1^) from sales",
            "Syntax error at \\[1:26\\], token '-'");

        assertParseQueryFails(
            "select [member] on ^axis(5)^ from sales",
            "Invalid axis specification\\. The axis number must be an integer between 0 and 4, but it was 5\\.0\\.");

        assertParseQueryFails(
            "select [member] on axes(^0^) from sales",
            "Syntax error at \\[1:25\\], token '\\('");

        assertParseQueryFails(
            "select [member] on ^0.5^ from sales",
            "Invalid axis specification\\. The axis number must be an integer between 0 and 4, but it was 0\\.5\\.");

        assertParseQueryFails(
            "select [member] on ^555^ from sales",
            "Invalid axis specification\\. The axis number must be an integer between 0 and 4, but it was 555\\.0\\.");
    }

    public void testScannerPunc() {
        // '$' is OK inside brackets but not outside
        assertParseQuery(
            "select [measures].[$foo] on columns from sales",
            TestContext.fold(
                "SELECT\n" +
                    "[measures].[$foo] ON COLUMNS\n" +
                    "FROM sales"));

        assertParseQueryFails(
            "select [measures].$^f^oo on columns from sales", // todo: parser off by one
                "Unexpected character '\\$'");

        // ']' unexcpected
        assertParseQueryFails("select { Customers]^.^Children } on columns from [Sales]",
                "Unexpected character ']'");
    }

    public void testUnparse() {
        checkUnparse(
            TestContext.fold(
                "with member [Measures].[Foo] as ' 123 '\n" +
                    "select {[Measures].members} on columns,\n" +
                    " CrossJoin([Product].members, {[Gender].Children}) on rows\n" +
                    "from [Sales]\n" +
                    "where [Marital Status].[S]"),
            TestContext.fold(
                "WITH\n" +
                    "MEMBER [Measures].[Foo] AS '123.0'\n" +
                    "SELECT\n" +
                    "{[Measures].members} ON COLUMNS,\n" +
                    "CrossJoin([Product].members, {[Gender].Children}) ON ROWS\n" +
                    "FROM [Sales]\n" +
                    "WHERE [Marital Status].[S]"));
    }

    private void checkUnparse(String queryString, final String expected) {
        try {
            final TestContext testContext = TestContext.instance();

            OlapConnection olapConnection = testContext.getConnection();
            MdxParser mdxParser =
                olapConnection.getParserFactory()
                    .createMdxParser(olapConnection);
            final SelectNode query = mdxParser.parseSelect(queryString);
            String unparsedQueryString = TestContext.toString(query);
            TestContext.assertEqualsVerbose(expected, unparsedQueryString);
        } catch (SQLException e) {
            throw new RuntimeException("error during parse");
        }
    }

    private void assertParseQueryFails(String query, String expected) {
        checkFails(createParser(), query, expected);
    }

    private void assertParseExprFails(String expr, String expected) {
        checkFails(createParser(), wrapExpr(expr), expected);
    }

    private void checkFails(MdxParser p, String query, String expected) {
        final ParseRegion.RegionAndSource ras = ParseRegion.findPos(query);
        try {
            SelectNode selectNode = p.parseSelect(ras.source);
            fail("Must return an error");
        } catch (Exception e) {
            checkEx(e, expected, ras);
        }
    }

    /**
     * Checks whether an exception matches the pattern and expected position
     * expected.
     *
     * @param ex Exception thrown
     * @param expectedMsgPattern Expected pattern
     * @param ras Query and position in query
     */
    public static void checkEx(
        Throwable ex,
        String expectedMsgPattern,
        ParseRegion.RegionAndSource ras)
    {
        String NL = TestContext.NL;
        if (null == ex) {
            if (expectedMsgPattern == null) {
                // No error expected, and no error happened.
                return;
            } else {
                throw new AssertionFailedError(
                    "Expected query to throw exception, but it did not; "
                    + "query [" + ras.source
                    + "]; expected [" + expectedMsgPattern + "]");
            }
        }
        Throwable actualException = ex;
        String actualMessage = actualException.getMessage();
        int actualLine = -1;
        int actualColumn = -1;
        int actualEndLine = 100;
        int actualEndColumn = 99;

        // Search for a SqlParseException -- with its position set -- somewhere
        // in the stack.
        MdxParseException mpe = null;
        for (Throwable x = ex; x != null; x = x.getCause()) {
            if ((x instanceof MdxParseException)
                && (((MdxParseException) x).getRegion() != null))
            {
                mpe = (MdxParseException) x;
                break;
            }
            if (x.getCause() == x) {
                break;
            }
        }

        if (mpe != null) {
            final ParseRegion region = mpe.getRegion();
            actualLine = region.getStartLine();
            actualColumn = region.getStartColumn();
            actualEndLine = region.getEndLine();
            actualEndColumn = region.getEndColumn();
            actualException = mpe;
            actualMessage = actualException.getMessage();
        } else {
            final String message = ex.getMessage();

            if (message != null) {
                Matcher matcher = lineColTwicePattern.matcher(message);
                if (matcher.matches()) {
                    actualLine = Integer.parseInt(matcher.group(1));
                    actualColumn = Integer.parseInt(matcher.group(2));
                    actualEndLine = Integer.parseInt(matcher.group(3));
                    actualEndColumn = Integer.parseInt(matcher.group(4));
                    actualMessage = matcher.group(5);
                } else {
                    matcher = lineColPattern.matcher(message);
                    if (matcher.matches()) {
                        actualLine = Integer.parseInt(matcher.group(1));
                        actualColumn = Integer.parseInt(matcher.group(2));
                    }
                }
            }
        }

        if (null == expectedMsgPattern) {
            if (null != actualException) {
                actualException.printStackTrace();
                fail(
                    "Validator threw unexpected exception"
                    + "; query [" + ras.source
                    + "]; exception [" + actualMessage
                    + "]; pos [line " + actualLine
                    + " col " + actualColumn
                    + " thru line " + actualLine
                    + " col " + actualColumn + "]");
            }
        } else if (null != expectedMsgPattern) {
            if (null == actualException) {
                fail(
                    "Expected validator to throw "
                    + "exception, but it did not; query [" + ras.source
                    + "]; expected [" + expectedMsgPattern + "]");
            } else {
                if ((actualColumn <= 0)
                    || (actualLine <= 0)
                    || (actualEndColumn <= 0)
                    || (actualEndLine <= 0))
                {
                    throw new AssertionFailedError(
                        "Error did not have position: "
                        + " actual pos [line " + actualLine
                        + " col " + actualColumn
                        + " thru line " + actualEndLine
                        + " col " + actualEndColumn + "]");
                }
                String sqlWithCarets =
                    new ParseRegion(
                        actualLine,
                        actualColumn,
                        actualEndLine,
                        actualEndColumn).annotate(ras.source);
                if (ras.region == null) {
                    throw new AssertionFailedError(
                        "todo: add carets to sql: " + sqlWithCarets);
                }
                if ((actualMessage == null)
                    || !actualMessage.matches(expectedMsgPattern))
                {
                    actualException.printStackTrace();
                    final String actualJavaRegexp =
                        (actualMessage == null) ? "null"
                        : TestContext.toJavaString(
                            TestContext.quotePattern(actualMessage));
                    fail(
                        "Validator threw different "
                        + "exception than expected; query [" + ras.source
                        + "];" + NL
                        + " expected pattern [" + expectedMsgPattern
                        + "];" + NL
                        + " actual [" + actualMessage
                        + "];" + NL
                        + " actual as java regexp [" + actualJavaRegexp
                        + "]; pos [" + actualLine
                        + " col " + actualColumn
                        + " thru line " + actualEndLine
                        + " col " + actualEndColumn
                        + "]; sql [" + sqlWithCarets + "]");
                } else if (
                    (ras.region != null)
                    && ((actualLine != ras.region.getStartLine())
                        || (actualColumn != ras.region.getStartColumn())
                        || (actualEndLine != ras.region.getEndLine())
                        || (actualEndColumn != ras.region.getEndColumn())))
                {
                    fail(
                        "Validator threw expected "
                        + "exception [" + actualMessage
                        + "]; but at pos [line " + actualLine
                        + " col " + actualColumn
                        + " thru line " + actualEndLine
                        + " col " + actualEndColumn
                        + "]; sql [" + sqlWithCarets + "]");
                }
            }
        }
    }

    public void testMultipleAxes() throws Exception {
        MdxParser p = createParser();
        String query = "select {[axis0mbr]} on axis(0), "
                + "{[axis1mbr]} on axis(1) from cube";

        SelectNode select = p.parseSelect(query);
        assertNotNull(select);

        List<AxisNode> axes = select.getAxisList();

        assertEquals("Number of axes", 2, axes.size());
        assertEquals("Axis index name must be correct",
            Axis.forOrdinal(0), axes.get(0).getAxis());
        assertEquals("Axis index name must be correct",
            Axis.forOrdinal(1), axes.get(1).getAxis());

        // now a similar query with axes reversed

        query = "select {[axis1mbr]} on aXiS(1), "
                + "{[axis0mbr]} on AxIs(0) from cube";

        select = p.parseSelect(query);
        axes = select.getAxisList();

        assertEquals("Number of axes", 2, axes.size());
        assertEquals("Axis index name must be correct",
            Axis.forOrdinal(0), axes.get(0).getAxis());
        assertEquals("Axis index name must be correct",
            Axis.forOrdinal(1), axes.get(1).getAxis());

        ParseTreeNode colsSetExpr = axes.get(0).getExpression();
        assertNotNull("Column tuples", colsSetExpr);

        CallNode fun = (CallNode)colsSetExpr;
        IdentifierNode identifier = (IdentifierNode) (fun.getArgList().get(0));
        assertEquals(1, identifier.getSegmentList().size());
        assertEquals("Correct member on axis", "axis0mbr",
            identifier.getSegmentList().get(0).getName());

        ParseTreeNode rowsSetExpr = axes.get(1).getExpression();
        assertNotNull("Row tuples", rowsSetExpr);

        fun = (CallNode) rowsSetExpr;
        identifier = (IdentifierNode) (fun.getArgList().get(0));
        assertEquals(1, identifier.getSegmentList().size());
        assertEquals("Correct member on axis", "axis1mbr",
            identifier.getSegmentList().get(0).getName());
    }

    public void testCaseTest() {
        assertParseQuery(
            "with member [Measures].[Foo] as " +
                " ' case when x = y then \"eq\" when x < y then \"lt\" else \"gt\" end '" +
                "select {[foo]} on axis(0) from cube",
            TestContext.fold(
                "WITH\n" +
                    "MEMBER [Measures].[Foo] AS 'CASE WHEN (x = y) THEN \"eq\" WHEN (x < y) THEN \"lt\" ELSE \"gt\" END'\n" +
                    "SELECT\n" +
                    "{[foo]} ON COLUMNS\n" +
                    "FROM cube"));
    }

    public void testCaseSwitch() {
        assertParseQuery(
            "with member [Measures].[Foo] as " +
                " ' case x when 1 then 2 when 3 then 4 else 5 end '" +
                "select {[foo]} on axis(0) from cube",
            TestContext.fold(
                "WITH\n" +
                    "MEMBER [Measures].[Foo] AS 'CASE x WHEN 1.0 THEN 2.0 WHEN 3.0 THEN 4.0 ELSE 5.0 END'\n" +
                    "SELECT\n" +
                    "{[foo]} ON COLUMNS\n" +
                    "FROM cube"));
    }

    public void testDimensionProperties() {
        assertParseQuery(
                "select {[foo]} properties p1,   p2 on columns from [cube]",
                TestContext.fold(
                    "SELECT\n" +
                        "{[foo]} DIMENSION PROPERTIES p1, p2 ON COLUMNS\n" +
                        "FROM [cube]"));
    }

    public void testCellProperties() {
        assertParseQuery(
                "select {[foo]} on columns from [cube] CELL PROPERTIES FORMATTED_VALUE",
                TestContext.fold(
                    "SELECT\n" +
                        "{[foo]} ON COLUMNS\n" +
                        "FROM [cube]\n" +
                        "CELL PROPERTIES FORMATTED_VALUE"));
    }

    public void testIsEmpty() {
        assertParseExpr("[Measures].[Unit Sales] IS EMPTY",
            "([Measures].[Unit Sales] IS EMPTY)");

        assertParseExpr("[Measures].[Unit Sales] IS EMPTY AND 1 IS NULL",
            "(([Measures].[Unit Sales] IS EMPTY) AND (1.0 IS NULL))");

        // FIXME: "NULL" should associate as "IS NULL" rather than "NULL + 56.0"
        assertParseExpr("- x * 5 is empty is empty is null + 56",
            "(((((- x) * 5.0) IS EMPTY) IS EMPTY) IS (NULL + 56.0))");
    }

    public void testIs() {
        assertParseExpr("[Measures].[Unit Sales] IS [Measures].[Unit Sales] AND [Measures].[Unit Sales] IS NULL",
            "(([Measures].[Unit Sales] IS [Measures].[Unit Sales]) AND ([Measures].[Unit Sales] IS NULL))");
    }

    public void testIsNull() {
        assertParseExpr("[Measures].[Unit Sales] IS NULL",
            "([Measures].[Unit Sales] IS NULL)");

        assertParseExpr("[Measures].[Unit Sales] IS NULL AND 1 <> 2",
            "(([Measures].[Unit Sales] IS NULL) AND (1.0 <> 2.0))");

        assertParseExpr("x is null or y is null and z = 5",
            "((x IS NULL) OR ((y IS NULL) AND (z = 5.0)))");

        assertParseExpr("(x is null) + 56 > 6",
            "((((x IS NULL)) + 56.0) > 6.0)");

        // FIXME: Should be
        //  "(((((x IS NULL) AND (a = b)) OR ((c = (d + 5.0))) IS NULL) + 5.0)");
        assertParseExpr("x is null and a = b or c = d + 5 is null + 5",
            "(((x IS NULL) AND (a = b)) OR ((c = (d + 5.0)) IS (NULL + 5.0)))");
    }

    public void testNull() {
        assertParseExpr("Filter({[Measures].[Foo]}, Iif(1 = 2, NULL, 'X'))",
            "Filter({[Measures].[Foo]}, Iif((1.0 = 2.0), NULL, \"X\"))");
    }

    public void testCast() {
        assertParseExpr("Cast([Measures].[Unit Sales] AS Numeric)",
            "CAST([Measures].[Unit Sales] AS Numeric)");

        assertParseExpr("Cast(1 + 2 AS String)",
            "CAST((1.0 + 2.0) AS String)");
    }

    public void testId() {
        assertParseExpr("foo", "foo");
        assertParseExpr("fOo", "fOo");
        assertParseExpr("[Foo].[Bar Baz]", "[Foo].[Bar Baz]");
        assertParseExpr("[Foo].&[Bar]", "[Foo].&[Bar]");
    }

    // todo: enable this
    public void _testCloneQuery() throws SQLException {
        OlapConnection olapConnection = TestContext.instance().getConnection();
        MdxParser mdxParser =
            olapConnection.getParserFactory()
                .createMdxParser(olapConnection);
        final SelectNode query = mdxParser.parseSelect(
            "select {[Measures].Members} on columns,\n" +
                " {[Store].Members} on rows\n" +
                "from [Sales]\n" +
                "where ([Gender].[M])");

        SelectNode selectClone = null; // select.copy();
        assertTrue(selectClone instanceof SelectNode);
        assertEquals(TestContext.toString(selectClone), TestContext.toString(query));
    }

    /**
     * Tests parsing of numbers.
     */
    public void testNumbers() {
        // Number: [+-] <digits> [ . <digits> ] [e [+-] <digits> ]
        assertParseExpr("2", "2.0");

        // leading '-' is treated as an operator -- that's ok
        assertParseExpr("-3", "(- 3.0)");

        // leading '+' is ignored -- that's ok
        assertParseExpr("+45", "45.0");

        // space bad
        assertParseExprFails("4 ^5^",
            "Syntax error at \\[1:35\\], token '5\\.0'");

        assertParseExpr("3.14", "3.14");
        assertParseExpr(".12345", "0.12345");

        // lots of digits left and right of point
        assertParseExpr("31415926535.89793", "3.141592653589793E10");
        assertParseExpr("31415926535897.9314159265358979", "3.141592653589793E13");
        assertParseExpr("3.141592653589793", "3.141592653589793");
        assertParseExpr("-3141592653589793.14159265358979", "(- 3.141592653589793E15)");

        // exponents akimbo
        assertParseExpr("1e2", "100.0");
        assertParseExprFails("1e2e^3^", // todo: fix parser; should be "1e2^e3^"
            "Syntax error at .* token 'e3'");
        assertParseExpr("1.2e3", "1200.0");
        assertParseExpr("-1.2345e3", "(- 1234.5)");
        assertParseExprFails("1.2e3.^4^", // todo: fix parser; should be "1.2e3^.4^"
            "Syntax error at .* token '0.4'");
        assertParseExpr(".00234e0003", "2.34");
        assertParseExpr(".00234e-0067", "2.34E-70");
    }

    /**
     * Testcase for bug 1688645, "High precision number in MDX causes overflow".
     * The problem was that "5000001234" exceeded the precision of the int being
     * used to gather the mantissa.
     */
    public void testLargePrecision() {

        // Now, a query with several numeric literals. This is the original
        // testcase for the bug.
        assertParseQuery(
            "with member [Measures].[Small Number] as '[Measures].[Store Sales] / 9000'\n" +
                "select\n" +
                "{[Measures].[Small Number]} on columns,\n" +
                "{Filter([Product].[Product Department].members, [Measures].[Small Number] >= 0.3\n" +
                "and [Measures].[Small Number] <= 0.5000001234)} on rows\n" +
                "from Sales\n" +
                "where ([Time].[1997].[Q2].[4])",
            TestContext.fold(
                "WITH\n" +
                    "MEMBER [Measures].[Small Number] AS '([Measures].[Store Sales] / 9000.0)'\n" +
                    "SELECT\n" +
                    "{[Measures].[Small Number]} ON COLUMNS,\n" +
                    "{Filter([Product].[Product Department].members, (([Measures].[Small Number] >= 0.3) AND ([Measures].[Small Number] <= 0.5000001234)))} ON ROWS\n" +
                    "FROM Sales\n" +
                    "WHERE ([Time].[1997].[Q2].[4])"));
    }

    public void testIdentifier() {
        // must have at least one segment
        IdentifierNode id;
        try {
            id = new IdentifierNode();
            fail("expected exception");
        } catch (IllegalArgumentException e) {
            // ok
        }

        id = new IdentifierNode(
            new IdentifierNode.Segment("foo"));
        assertEquals("[foo]", id.toString());

        // append does not mutate
        IdentifierNode id2 = id.append(
            new IdentifierNode.Segment(
                null, "bar", IdentifierNode.Quoting.KEY));
        assertTrue(id != id2);
        assertEquals("[foo]", id.toString());
        assertEquals("[foo].&[bar]", id2.toString());

        // cannot mutate segment list
        final List<IdentifierNode.Segment> segments = id.getSegmentList();
        try {
            segments.remove(0);
            fail("expected exception");
        } catch (UnsupportedOperationException e) {
            // ok
        }
        try {
            segments.clear();
            fail("expected exception");
        } catch (UnsupportedOperationException e) {
            // ok
        }
        try {
            segments.add(
                new IdentifierNode.Segment("baz"));
            fail("expected exception");
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

    /**
     * Parses an MDX query and asserts that the result is as expected when
     * unparsed.
     *
     * @param mdx MDX query
     * @param expected Expected result of unparsing
     */
    private void assertParseQuery(String mdx, final String expected) {
        MdxParser p = createParser();
        final SelectNode selectNode = p.parseSelect(mdx);
        final String actual = TestContext.toString(selectNode);
        TestContext.assertEqualsVerbose(expected, actual);
    }

    /**
     * Parses an MDX expression and asserts that the result is as expected when
     * unparsed.
     *
     * @param expr MDX query
     * @param expected Expected result of unparsing
     */
    private void assertParseExpr(String expr, final String expected) {
        MdxParser p = createParser();
        final String mdx = wrapExpr(expr);
        final SelectNode selectNode = p.parseSelect(mdx);
        assertEquals(1, selectNode.getWithList().size());
        WithMemberNode withMember =
            (WithMemberNode) selectNode.getWithList().get(0);
        final String actual = TestContext.toString(withMember.getExpression());
        TestContext.assertEqualsVerbose(expected, actual);
    }

    private String wrapExpr(String expr) {
        return "with member [Measures].[Foo] as " +
            expr +
            "\n select from [Sales]";
    }
}

// End ParserTest.java
