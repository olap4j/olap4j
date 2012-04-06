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
package org.olap4j.test;

import org.olap4j.Axis;
import org.olap4j.OlapConnection;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.mdx.*;
import org.olap4j.mdx.parser.MdxParseException;
import org.olap4j.mdx.parser.MdxParser;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
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

    final TestContext testContext = TestContext.instance();
    private Connection connection;

    public ParserTest(String name) {
        super(name);
    }

    protected OlapConnection getOlapConnection() throws SQLException {
        if (connection == null) {
            connection = testContext.getTester().createConnection();
        }
        return testContext.getTester().getWrapper().unwrap(
            connection, OlapConnection.class);
    }

    protected void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }

    private MdxParser createParser() {
        try {
            OlapConnection olapConnection = getOlapConnection();
            return olapConnection.getParserFactory()
                .createMdxParser(olapConnection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertParseExpr(String expr, String expected, boolean old) {
        // See mondrian.olap.ParserTest.assertParseExpr(String, String, boolean)
        if (old) {
            return;
        }
        assertParseExpr(expr, expected);
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
        String expectedName)
    {
        MdxParser p = createParser();
        String q = "select [member] on " + s + " from [cube]";
        SelectNode selectNode = p.parseSelect(q);
        List<AxisNode> axes = selectNode.getAxisList();

        assertEquals("Number of axes must be 1", 1, axes.size());
        assertEquals(
            "Axis index name must be correct",
            expectedName, axes.get(0).getAxis().name());
    }

    public void testNegativeCases() throws Exception {
        assertParseQueryFails(
            "^s^ from sales",
            "Syntax error at line 1, column 1, token 's'");

        assertParseQueryFails(
            "^seleg^ from sales",
            "Syntax error at line 1, column 1, token 'seleg'");

        assertParseQueryFails(
            "^seleg^   from sales",
            "Syntax error at line 1, column 1, token 'seleg'");

        assertParseQueryFails(
            "select [member] on ^axis(1.7)^ from sales",
            "(?s).*The axis number must be a non-negative integer, but it was 1.7.");

        assertParseQueryFails(
            "select [member] on ^foobar^ from sales",
            "Syntax error at line 1, column 20, token 'foobar'");

        assertParseQueryFails(
            "select [member] on axis(^-^ 1) from sales",
            "Syntax error at line 1, column 25, token '-'");

        assertParseQueryFails(
            "select [member] on axis(^-^1) from sales",
            "Syntax error at line 1, column 25, token '-'");

        // used to be an error, but no longer
        assertParseQuery(
            "select [member] on axis(5) from sales",
            "SELECT\n"
            + "[member] ON AXIS(5)\n"
            + "FROM sales");

        assertParseQueryFails(
            "select [member] on ^axes^(0) from sales",
            "Syntax error at line 1, column 20, token 'axes'");

        assertParseQueryFails(
            "select [member] on ^0.5^ from sales",
            "Invalid axis specification\\. The axis number must be a non-negative integer, but it was 0\\.5\\.");

        assertParseQuery(
            "select [member] on 555 from sales",
            "SELECT\n"
            + "[member] ON AXIS(555)\n"
            + "FROM sales");
    }

    /**
     * Test case for bug <a href="http://jira.pentaho.com/browse/MONDRIAN-831">
     * MONDRIAN-831, "Failure parsing queries with member identifiers beginning
     * with '_' and not expressed between brackets"</a>.
     *
     * <p>According to the spec
     * <a href="http://msdn.microsoft.com/en-us/library/ms145572.aspx">
     * Identifiers (MDX)</a>, the first character of a regular identifier
     * must be a letter (per the unicode standard 2.0) or underscore. Subsequent
     * characters must be a letter, and underscore, or a digit.
     */
    public void testScannerPunc() {
        assertParseQuery(
            "with member [Measures].__Foo as 1 + 2\n"
            + "select __Foo on 0\n"
            + "from _Bar_Baz",
            "WITH\n"
            + "MEMBER [Measures].__Foo AS\n"
            + "    (1 + 2)\n"
            + "SELECT\n"
            + "__Foo ON COLUMNS\n"
            + "FROM _Bar_Baz");

        // # is not allowed
        assertParseQueryFails(
            "with member [Measures].^#_Foo as 1 + 2\n"
            + "select __Foo on 0\n"
            + "from _Bar#Baz",
            "Lexical error at line 1, column 24.  Encountered: \"#\" \\(35\\), after : \"\"");
        assertParseQueryFails(
            "with member [Measures].Foo as 1 + 2\n"
            + "select Foo on 0\n"
            + "from Bar^#Baz",
            "Lexical error at line 3, column 9\\.  Encountered: \"#\" \\(35\\), after : \"\"");

        // The spec doesn't allow $ but SSAS allows it so we allow it too.
        assertParseQuery(
            "with member [Measures].$Foo as 1 + 2\n"
            + "select $Foo on 0\n"
            + "from Bar$Baz",
            "WITH\n"
            + "MEMBER [Measures].$Foo AS\n"
            + "    (1 + 2)\n"
            + "SELECT\n"
            + "$Foo ON COLUMNS\n"
            + "FROM Bar$Baz");
        // '$' is OK inside brackets too
        assertParseQuery(
            "select [measures].[$foo] on columns from sales",
            "SELECT\n"
            + "[measures].[$foo] ON COLUMNS\n"
            + "FROM sales");

        // ']' unexpected
        assertParseQueryFails(
            "select { Customers^]^.Children } on columns from [Sales]",
            "Lexical error at line 1, column 19\\.  Encountered: \"\\]\" \\(93\\), after : \"\"");
    }

    public void testUnparse() {
        checkUnparse(
            "with member [Measures].[Foo] as ' 123 '\n"
            + "select {[Measures].members} on columns,\n"
            + " CrossJoin([Product].members, {[Gender].Children}) on rows\n"
            + "from [Sales]\n"
            + "where [Marital Status].[S]",
            "WITH\n"
            + "MEMBER [Measures].[Foo] AS\n"
            + "    123\n"
            + "SELECT\n"
            + "{[Measures].members} ON COLUMNS,\n"
            + "CrossJoin([Product].members, {[Gender].Children}) ON ROWS\n"
            + "FROM [Sales]\n"
            + "WHERE [Marital Status].[S]");
    }

    private void checkUnparse(String queryString, final String expected) {
        try {
            OlapConnection olapConnection = getOlapConnection();
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
        checkFails(createParser(), query, regexpChecker(expected));
    }

    private void assertParseExprFails(String expr, String expected) {
        checkFails(createParser(), wrapExpr(expr), regexpChecker(expected));
    }

    private void checkFails(MdxParser p, String query, Checker checker) {
        final ParseRegion.RegionAndSource ras = ParseRegion.findPos(query);
        try {
            SelectNode selectNode = p.parseSelect(ras.source);
            fail("Must return an error, got " + selectNode);
        } catch (Exception e) {
            checkEx(e, checker, ras);
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
        Checker expectedMsgPattern,
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
                if (!expectedMsgPattern.apply(actualException)) {
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
        assertEquals(
            "Axis index name must be correct",
            Axis.Factory.forOrdinal(0),
            axes.get(0).getAxis());
        assertEquals(
            "Axis index name must be correct",
            Axis.Factory.forOrdinal(1),
            axes.get(1).getAxis());

        // now a similar query with axes reversed

        query = "select {[axis1mbr]} on aXiS(1), "
                + "{[axis0mbr]} on AxIs(0) from cube";

        select = p.parseSelect(query);
        axes = select.getAxisList();

        assertEquals("Number of axes", 2, axes.size());
        assertEquals(
            "Axis index name must be correct",
            Axis.Factory.forOrdinal(0),
            axes.get(0).getAxis());
        assertEquals(
            "Axis index name must be correct",
            Axis.Factory.forOrdinal(1),
            axes.get(1).getAxis());

        ParseTreeNode colsSetExpr = axes.get(0).getExpression();
        assertNotNull("Column tuples", colsSetExpr);

        CallNode fun = (CallNode)colsSetExpr;
        IdentifierNode identifier = (IdentifierNode) (fun.getArgList().get(0));
        assertEquals(1, identifier.getSegmentList().size());
        assertEquals(
            "Correct member on axis",
            "axis0mbr",
            identifier.getSegmentList().get(0).getName());

        ParseTreeNode rowsSetExpr = axes.get(1).getExpression();
        assertNotNull("Row tuples", rowsSetExpr);

        fun = (CallNode) rowsSetExpr;
        identifier = (IdentifierNode) (fun.getArgList().get(0));
        assertEquals(1, identifier.getSegmentList().size());
        assertEquals(
            "Correct member on axis",
            "axis1mbr",
            identifier.getSegmentList().get(0).getName());
    }

    /**
     * If an axis expression is a member, implicitly convert it to a set.
     */
    public void testMemberOnAxis() {
        assertParseQuery(
            "select [Measures].[Sales Count] on 0, non empty [Store].[Store State].members on 1 from [Sales]",
            "SELECT\n"
            + "[Measures].[Sales Count] ON COLUMNS,\n"
            + "NON EMPTY [Store].[Store State].members ON ROWS\n"
            + "FROM [Sales]");
    }

    public void testCaseTest() {
        assertParseQuery(
            "with member [Measures].[Foo] as "
            + " ' case when x = y then \"eq\" when x < y then \"lt\" else \"gt\" end '"
            + "select {[foo]} on axis(0) from cube",
            "WITH\n"
            + "MEMBER [Measures].[Foo] AS\n"
            + "    CASE WHEN (x = y) THEN \"eq\" WHEN (x < y) THEN \"lt\" ELSE \"gt\" END\n"
            + "SELECT\n"
            + "{[foo]} ON COLUMNS\n"
            + "FROM cube");
    }

    public void testCaseSwitch() {
        assertParseQuery(
            "with member [Measures].[Foo] as "
            + " ' case x when 1 then 2 when 3 then 4 else 5 end '"
            + "select {[foo]} on axis(0) from cube",
            "WITH\n"
            + "MEMBER [Measures].[Foo] AS\n"
            + "    CASE x WHEN 1 THEN 2 WHEN 3 THEN 4 ELSE 5 END\n"
            + "SELECT\n"
            + "{[foo]} ON COLUMNS\n"
            + "FROM cube");
    }

    /**
     * Test case for bug <a href="http://jira.pentaho.com/browse/MONDRIAN-306">
     * MONDRIAN-306, "Parser should not require braces around range op in WITH
     * SET"</a>.
     */
    public void testSetExpr() {
        assertParseQuery(
            "with set [Set1] as '[Product].[Drink]:[Product].[Food]' \n"
            + "select [Set1] on columns, {[Measures].defaultMember} on rows \n"
            + "from Sales",
            "WITH\n"
            + "SET [Set1] AS\n"
            + "    ([Product].[Drink] : [Product].[Food])\n"
            + "SELECT\n"
            + "[Set1] ON COLUMNS,\n"
            + "{[Measures].defaultMember} ON ROWS\n"
            + "FROM Sales");

        // set expr in axes
        assertParseQuery(
            "select [Product].[Drink]:[Product].[Food] on columns,\n"
            + " {[Measures].defaultMember} on rows \n"
            + "from Sales",
            "SELECT\n"
            + "([Product].[Drink] : [Product].[Food]) ON COLUMNS,\n"
            + "{[Measures].defaultMember} ON ROWS\n"
            + "FROM Sales");
    }

    public void testDimensionProperties() {
        assertParseQuery(
            "select {[foo]} properties p1,   p2 on columns from [cube]",
            "SELECT\n"
            + "{[foo]} DIMENSION PROPERTIES p1, p2 ON COLUMNS\n"
            + "FROM [cube]");
    }

    public void testCellProperties() {
        assertParseQuery(
            "select {[foo]} on columns "
            + "from [cube] CELL PROPERTIES FORMATTED_VALUE",
            "SELECT\n"
            + "{[foo]} ON COLUMNS\n"
            + "FROM [cube]\n"
            + "CELL PROPERTIES FORMATTED_VALUE");
    }

    public void testIsEmpty() {
        assertParseExpr(
            "[Measures].[Unit Sales] IS EMPTY",
            "([Measures].[Unit Sales] IS EMPTY)");

        assertParseExpr(
            "[Measures].[Unit Sales] IS EMPTY AND 1 IS NULL",
            "(([Measures].[Unit Sales] IS EMPTY) AND (1 IS NULL))");

        // FIXME: "NULL" should associate as "IS NULL" rather than "NULL + 56"
        // FIXME: Gives error at token '+' with new parser.
        assertParseExpr(
            "- x * 5 is empty is empty is null + 56",
            "(((((- x) * 5) IS EMPTY) IS EMPTY) IS (NULL + 56))",
            true);
    }

    public void testIs() {
        assertParseExpr(
            "[Measures].[Unit Sales] IS [Measures].[Unit Sales] "
            + "AND [Measures].[Unit Sales] IS NULL",
            "(([Measures].[Unit Sales] IS [Measures].[Unit Sales]) "
            + "AND ([Measures].[Unit Sales] IS NULL))");
    }

    public void testIsNull() {
        assertParseExpr(
            "[Measures].[Unit Sales] IS NULL",
            "([Measures].[Unit Sales] IS NULL)");

        assertParseExpr(
            "[Measures].[Unit Sales] IS NULL AND 1 <> 2",
            "(([Measures].[Unit Sales] IS NULL) AND (1 <> 2))");

        assertParseExpr(
            "x is null or y is null and z = 5",
            "((x IS NULL) OR ((y IS NULL) AND (z = 5)))");

        assertParseExpr(
            "(x is null) + 56 > 6",
            "(((x IS NULL) + 56) > 6)");

        // FIXME: Should be:
        //  "(((((x IS NULL) AND (a = b)) OR ((c = (d + 5))) IS NULL) + 5)"
        // FIXME: Gives error at token '+' with new parser.
        assertParseExpr(
            "x is null and a = b or c = d + 5 is null + 5",
            "(((x IS NULL) AND (a = b)) OR ((c = (d + 5)) IS (NULL + 5)))",
            true);
    }

    public void testNull() {
        assertParseExpr(
            "Filter({[Measures].[Foo]}, Iif(1 = 2, NULL, 'X'))",
            "Filter({[Measures].[Foo]}, Iif((1 = 2), NULL, \"X\"))");
    }

    public void testCast() {
        assertParseExpr(
            "Cast([Measures].[Unit Sales] AS Numeric)",
            "CAST([Measures].[Unit Sales] AS Numeric)");

        assertParseExpr(
            "Cast(1 + 2 AS String)",
            "CAST((1 + 2) AS String)");
    }

    /**
     * Verifies that calculated measures made of several '*' operators
     * can resolve them correctly.
     */
    public void testMultiplication() {
        MdxParser p = createParser();
        final String mdx =
            wrapExpr(
                "([Measures].[Unit Sales]"
                + " * [Measures].[Store Cost]"
                + " * [Measures].[Store Sales])");

        assertParseQuery(
            mdx,
            "WITH\n"
            + "MEMBER [Measures].[Foo] AS\n"
            + "    (([Measures].[Unit Sales] * [Measures].[Store Cost]) * [Measures].[Store Sales])\n"
            + "SELECT\n"
            + "FROM [Sales]");
    }

    public void testBangFunction() {
        // Parser accepts '<id> [! <id>] *' as a function name, but ignores
        // all but last name.
        assertParseExpr("foo!bar!Exp(2.0)", "Exp(2.0)");
        assertParseExpr("1 + VBA!Exp(2.0 + 3)", "(1 + Exp((2.0 + 3)))");
    }

    public void testId() {
        assertParseExpr("foo", "foo");
        assertParseExpr("fOo", "fOo");
        assertParseExpr("[Foo].[Bar Baz]", "[Foo].[Bar Baz]");
        assertParseExpr("[Foo].&[Bar]", "[Foo].&[Bar]");
    }

    public void testIdWithKey() {
        // two segments each with a compound key
        final String mdx = "[Foo].&Key1&Key2.&[Key3]&Key4&[5]";
        assertParseExpr(mdx, mdx);

        MdxParser p = createParser();
        final String mdxQuery = wrapExpr(mdx);
        final SelectNode selectNode = p.parseSelect(mdxQuery);
        assertEquals(1, selectNode.getWithList().size());
        WithMemberNode withMember =
            (WithMemberNode) selectNode.getWithList().get(0);
        final ParseTreeNode expr = withMember.getExpression();
        IdentifierNode id = (IdentifierNode) expr;
        assertNotNull(id.getRegion());
        assertEquals(3, id.getSegmentList().size());

        final IdentifierSegment seg0 = id.getSegmentList().get(0);
        assertNotNull(seg0.getRegion());
        assertEquals("Foo", seg0.getName());
        assertEquals(Quoting.QUOTED, seg0.getQuoting());

        final IdentifierSegment seg1 = id.getSegmentList().get(1);
        assertEquals(Quoting.KEY, seg1.getQuoting());
        assertNull(seg1.getName());
        List<NameSegment> keyParts = seg1.getKeyParts();
        assertNotNull(keyParts);
        assertEquals(2, keyParts.size());
        assertEquals("Key1", keyParts.get(0).getName());
        assertEquals(
            Quoting.UNQUOTED, keyParts.get(0).getQuoting());
        assertEquals("Key2", keyParts.get(1).getName());
        assertEquals(
            Quoting.UNQUOTED, keyParts.get(1).getQuoting());

        final IdentifierSegment seg2 = id.getSegmentList().get(2);
        assertNotNull(seg2.getRegion());
        assertEquals(Quoting.KEY, seg2.getQuoting());
        List<NameSegment> keyParts2 = seg2.getKeyParts();
        assertNotNull(keyParts2);
        assertEquals(3, keyParts2.size());
        assertEquals(
            Quoting.QUOTED, keyParts2.get(0).getQuoting());
        assertEquals(
            Quoting.UNQUOTED, keyParts2.get(1).getQuoting());
        assertEquals(
            Quoting.QUOTED, keyParts2.get(2).getQuoting());
        assertEquals("5", keyParts2.get(2).getName());
        assertNotNull(keyParts2.get(2).getRegion());

        final String actual = TestContext.toString(expr);
        TestContext.assertEqualsVerbose(mdx, actual);
    }

    public void testIdComplex() {
        // simple key
        assertParseExpr(
            "[Foo].&[Key1]&[Key2].[Bar]",
            "[Foo].&[Key1]&[Key2].[Bar]");
        // compound key
        assertParseExpr(
            "[Foo].&[1]&[Key 2]&[3].[Bar]",
            "[Foo].&[1]&[Key 2]&[3].[Bar]");
        // compound key sans brackets
        assertParseExpr(
            "[Foo].&Key1&Key2 + 4",
            "([Foo].&Key1&Key2 + 4)");
        // brackets are required for numbers
        assertParseExprFails(
            "[Foo].&[1]&[Key2]&^3.[Bar]",
            "Lexical error at line 1, column 51\\.  Encountered: \"3\" \\(51\\), after : \"&\"");
        // space between ampersand and key is unacceptable
        assertParseExprFails(
            "[Foo].&^ [Key2].[Bar]",
            "Lexical error at line 1, column 40\\.  Encountered: \" \" \\(32\\), after : \"&\"");
        // underscore after ampersand is unacceptable
        assertParseExprFails(
            "[Foo].&^_Key2.[Bar]",
            "Lexical error at line 1, column 40\\.  Encountered: \"_\" \\(95\\), after : \"&\"");
        // but underscore is OK within brackets
        assertParseExpr(
            "[Foo].&[_Key2].[Bar]",
            "[Foo].&[_Key2].[Bar]");
    }

    // todo: enable this
    public void _testCloneQuery() throws SQLException {
        OlapConnection olapConnection = getOlapConnection();
        MdxParser mdxParser =
            olapConnection.getParserFactory()
                .createMdxParser(olapConnection);
        final SelectNode query = mdxParser.parseSelect(
            "select {[Measures].Members} on columns,\n"
            + " {[Store].Members} on rows\n"
            + "from [Sales]\n"
            + "where ([Gender].[M])");

        SelectNode selectClone = null; // select.copy();
        assertTrue(selectClone instanceof SelectNode);
        assertEquals(
            TestContext.toString(selectClone),
            TestContext.toString(query));
    }

    /**
     * Tests parsing of numbers.
     */
    public void testNumbers() {
        // Number: [+-] <digits> [ . <digits> ] [e [+-] <digits> ]
        assertParseExpr("2", "2");

        // leading '-' is treated as an operator -- that's ok
        assertParseExpr("-3", "(- 3)");

        // leading '+' is ignored -- that's ok
        assertParseExpr("+45", "45");

        // space bad
        assertParseExprFails(
            "4 ^5^",
            "Syntax error at line 1, column 35, token '5'");

        assertParseExpr("3.14", "3.14");
        assertParseExpr(".12345", "0.12345");

        // lots of digits left and right of point
        assertParseExpr("31415926535.89793", "31415926535.89793");
        assertParseExpr(
            "31415926535897.9314159265358979",
            "31415926535897.9314159265358979");
        assertParseExpr("3.141592653589793", "3.141592653589793");
        assertParseExpr(
            "-3141592653589793.14159265358979",
            "(- 3141592653589793.14159265358979)");

        // exponents akimbo
        assertParseExpr("1e2", "100", true);
        assertParseExpr("1e2", Olap4jUtil.PreJdk15 ? "100" : "1E+2", false);

        assertParseExprFails(
            "1e2^e3^",
            "Syntax error at .* token 'e3'");

        assertParseExpr("1.2e3", "1200", true);
        assertParseExpr(
            "1.2e3",
            Olap4jUtil.PreJdk15 ? "1200" : "1.2E+3", false);

        assertParseExpr("-1.2345e3", "(- 1234.5)");
        assertParseExprFails(
            "1.2e3^.4^",
            "Syntax error at .* token '\\.4'");
        assertParseExpr(".00234e0003", "2.34");
        assertParseExpr(
            ".00234e-0067",
            Olap4jUtil.PreJdk15
                ? "0.00000000000000000000000000000000000000000000000000000000"
                     + "0000000000000234"
                : "2.34E-70");
    }

    /**
     * Testcase for bug <a href="http://jira.pentaho.com/browse/MONDRIAN-272">
     * MONDRIAN-272, "High precision number in MDX causes overflow"</a>.
     * The problem was that "5000001234" exceeded the precision of the int being
     * used to gather the mantissa.
     */
    public void testLargePrecision() {
        // Now, a query with several numeric literals. This is the original
        // testcase for the bug.
        assertParseQuery(
            "with member [Measures].[Small Number] as '[Measures].[Store Sales] / 9000'\n"
            + "select\n"
            + "{[Measures].[Small Number]} on columns,\n"
            + "{Filter([Product].[Product Department].members, [Measures].[Small Number] >= 0.3\n"
            + "and [Measures].[Small Number] <= 0.5000001234)} on rows\n"
            + "from Sales\n"
            + "where ([Time].[1997].[Q2].[4])",
            "WITH\n"
            + "MEMBER [Measures].[Small Number] AS\n"
            + "    ([Measures].[Store Sales] / 9000)\n"
            + "SELECT\n"
            + "{[Measures].[Small Number]} ON COLUMNS,\n"
            + "{Filter([Product].[Product Department].members, (([Measures].[Small Number] >= 0.3) AND ([Measures].[Small Number] <= 0.5000001234)))} ON ROWS\n"
            + "FROM Sales\n"
            + "WHERE ([Time].[1997].[Q2].[4])");
    }

    public void testIdentifier() {
        // must have at least one segment
        IdentifierNode id;
        try {
            id = new IdentifierNode();
            fail("expected exception, got " + id);
        } catch (IllegalArgumentException e) {
            // ok
        }

        id = new IdentifierNode(
            new NameSegment("foo"));
        assertEquals("[foo]", id.toString());

        // append does not mutate
        IdentifierNode id2 = id.append(
            new KeySegment(
                new NameSegment(
                    null, "bar", Quoting.QUOTED)));
        assertTrue(id != id2);
        assertEquals("[foo]", id.toString());
        assertEquals("[foo].&[bar]", id2.toString());

        // cannot mutate segment list
        final List<IdentifierSegment> segments = id.getSegmentList();
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
                new NameSegment("baz"));
            fail("expected exception");
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

    /**
     * Test case for empty expressions. Test case for <a href=
  "http://sf.net/tracker/?func=detail&aid=3030772&group_id=168953&atid=848534"
     * > bug 3030772, "DrilldownLevelTop parser error"</a>.
     */
    public void testEmptyExpr() {
        assertParseQuery(
            "select NON EMPTY HIERARCHIZE(\n"
            + "  {DrillDownLevelTop(\n"
            + "     {[Product].[All Products]},3,,[Measures].[Unit Sales])}"
            + "  ) ON COLUMNS\n"
            + "from [Sales]\n",
            "SELECT\n"
            + "NON EMPTY HIERARCHIZE({DrillDownLevelTop({[Product].[All Products]}, 3, , [Measures].[Unit Sales])}) ON COLUMNS\n"
            + "FROM [Sales]");

        // more advanced; the actual test case in the bug
        assertParseQuery(
            "SELECT {[Measures].[NetSales]}"
            + " DIMENSION PROPERTIES PARENT_UNIQUE_NAME ON COLUMNS ,"
            + " NON EMPTY HIERARCHIZE(AddCalculatedMembers("
            + "{DrillDownLevelTop({[ProductDim].[Name].[All]}, 10, ,"
            + " [Measures].[NetSales])}))"
            + " DIMENSION PROPERTIES PARENT_UNIQUE_NAME ON ROWS "
            + "FROM [cube]",
            "SELECT\n"
            + "{[Measures].[NetSales]} DIMENSION PROPERTIES PARENT_UNIQUE_NAME ON COLUMNS,\n"
            + "NON EMPTY HIERARCHIZE(AddCalculatedMembers({DrillDownLevelTop({[ProductDim].[Name].[All]}, 10, , [Measures].[NetSales])})) DIMENSION PROPERTIES PARENT_UNIQUE_NAME ON ROWS\n"
            + "FROM [cube]");
    }

    /**
     * Test case for SELECT in the FROM clause.
     */
    public void testInnerSelect() {
        assertParseQuery(
            "SELECT FROM "
            + "(SELECT ({[ProductDim].[Product Group].&[Mobile Phones]}) "
            + "ON COLUMNS FROM [cube]) CELL PROPERTIES VALUE",
            "SELECT\n"
            + "FROM (\n"
            + "    SELECT\n"
            + "    {[ProductDim].[Product Group].&[Mobile Phones]} ON COLUMNS\n"
            + "    FROM [cube])\n"
            + "CELL PROPERTIES VALUE");
    }

    /**
     * Test case for adding to WITH clause.
     */
    public void testWithAdd() {
        SelectNode selectNode = new SelectNode();
        IdentifierNode startDate =
            new IdentifierNode(
                new NameSegment("Date"),
                new NameSegment("2010-01-03"));
        IdentifierNode endDate =
            new IdentifierNode(
                new NameSegment("Date"),
                new NameSegment("2010-10-03"));
        IdentifierNode name =
            new IdentifierNode(
                new NameSegment("Date"),
                new NameSegment("Date Range"));
        CallNode cn = new CallNode(null, ":", Syntax.Infix, startDate, endDate);
        ParseTreeNode exp =
            new CallNode(
                null,
                "Aggregate",
                Syntax.Function,
                new CallNode(
                    null,
                    "{}",
                    Syntax.Braces,
                    cn));
        WithMemberNode withMemberNode =
            new WithMemberNode(
                null, name, exp, Collections.<PropertyValueNode>emptyList());
        selectNode.setFrom(
            IdentifierNode.parseIdentifier("Sales"));
        selectNode.getWithList().add(withMemberNode);
        final String queryString = selectNode.toString();
        TestContext.assertEqualsVerbose(
            "WITH\n"
            + "MEMBER [Date].[Date Range] AS\n"
            + "    Aggregate({([Date].[2010-01-03] : [Date].[2010-10-03])})\n"
            + "SELECT\n"
            + "FROM Sales",
            queryString);
        // check that unparsed string is valid
        assertParseQuery(queryString, TestContext.unfold(queryString));
    }

    /**
     * Test case for bug
     * <a href="http://sourceforge.net/tracker/?func=detail&aid=3515404&group_id=168953&atid=848534">3515404</a>,
     * "Inconsistent parsing behavior('.CHILDREN' and '.Children')".
     */
    public void testChildren() {
        MdxParser p = createParser();
        ParseTreeNode node = p.parseExpression("[Store].[USA].CHILDREN");
        checkChildren(node, "CHILDREN");

        ParseTreeNode node2 = p.parseExpression("[Store].[USA].Children");
        checkChildren(node2, "Children");

        ParseTreeNode node3 = p.parseExpression("[Store].[USA].children");
        checkChildren(node3, "children");
    }

    private void checkChildren(ParseTreeNode node, String name) {
        assertTrue(node instanceof CallNode);
        CallNode call = (CallNode) node;
        assertEquals(name, call.getOperatorName());
        assertTrue(call.getArgList().get(0) instanceof IdentifierNode);
        assertEquals("[Store].[USA]", call.getArgList().get(0).toString());
        assertEquals(1, call.getArgList().size());
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
        return
            "with member [Measures].[Foo] as "
            + expr
            + "\n select from [Sales]";
    }

    static Checker regexpChecker(String pattern) {
        return new RegexpChecker(pattern);
    }

    interface Checker {
        boolean apply(Throwable e);
    }

    static class RegexpChecker implements Checker {
        private final String pattern;

        public RegexpChecker(String pattern) {
            this.pattern = pattern;
        }

        public String toString() {
            return "regex(" + pattern + ")";
        }

        public boolean apply(Throwable e) {
            return e.getMessage() != null
                   && e.getMessage().matches(pattern);
        }
    }
}

// End ParserTest.java
