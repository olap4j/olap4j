/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import junit.framework.TestCase;

import java.util.*;

import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.mdx.parser.impl.DefaultMdxParserImpl;
import org.olap4j.test.TestContext;

/**
 * Testcase for org.olap4j.mdx package.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 12, 2007
 */
public class MdxTest extends TestCase {
    /**
     * Tests methods {@link IdentifierNode#quoteMdxIdentifier(String)},
     * {@link IdentifierNode#unparseIdentifierList(java.util.List)}.
     */
    public void testQuoteMdxIdentifier() {
        assertEquals(
            "[San Francisco]",
            IdentifierNode.quoteMdxIdentifier("San Francisco"));

        assertEquals(
            "[a [bracketed]] string]",
            IdentifierNode.quoteMdxIdentifier("a [bracketed] string"));

        assertEquals(
            "[Store].[USA].[California]",
            IdentifierNode.unparseIdentifierList(
                Arrays.asList(
                    new IdentifierNode.NameSegment(
                        null, "Store", IdentifierNode.Quoting.QUOTED),
                    new IdentifierNode.NameSegment(
                        null, "USA", IdentifierNode.Quoting.QUOTED),
                    new IdentifierNode.NameSegment(
                        null, "California", IdentifierNode.Quoting.QUOTED))));
    }

    public void testImplode() {
        List<IdentifierNode.Segment> fooBar =
            Arrays.<IdentifierNode.Segment>asList(
                new IdentifierNode.NameSegment(
                    null, "foo", IdentifierNode.Quoting.UNQUOTED),
                new IdentifierNode.NameSegment(
                    null, "bar", IdentifierNode.Quoting.QUOTED));
        assertEquals(
            "foo.[bar]",
            IdentifierNode.unparseIdentifierList(fooBar));

        List<IdentifierNode.Segment> empty = Collections.emptyList();
        assertEquals("", IdentifierNode.unparseIdentifierList(empty));

        List<IdentifierNode.Segment> nasty =
            Arrays.<IdentifierNode.Segment>asList(
                new IdentifierNode.NameSegment(
                    null, "string", IdentifierNode.Quoting.QUOTED),
                new IdentifierNode.NameSegment(
                    null, "with", IdentifierNode.Quoting.QUOTED),
                new IdentifierNode.NameSegment(
                    null, "a [bracket] in it", IdentifierNode.Quoting.QUOTED));
        assertEquals(
            "[string].[with].[a [bracket]] in it]",
            IdentifierNode.unparseIdentifierList(nasty));
    }

    public void testParseIdentifier() {
        List<IdentifierNode.Segment> segments =
            IdentifierNode.parseIdentifier(
                "[string].[with].[a [bracket]] in it]").getSegmentList();
        assertEquals(3, segments.size());
        assertEquals(
            "a [bracket] in it",
            segments.get(2).getName());
        assertEquals(
            IdentifierNode.Quoting.QUOTED,
            segments.get(2).getQuoting());

        segments = IdentifierNode.parseIdentifier(
            "[Worklog].[All].[calendar-[LANGUAGE]].js]").getSegmentList();
        assertEquals(3, segments.size());
        assertEquals(
            "calendar-[LANGUAGE].js",
            segments.get(2).getName());

        segments = IdentifierNode.parseIdentifier("[foo].bar").getSegmentList();
        assertEquals(2, segments.size());
        assertEquals(
            IdentifierNode.Quoting.QUOTED,
            segments.get(0).getQuoting());
        assertEquals(
            IdentifierNode.Quoting.UNQUOTED,
            segments.get(1).getQuoting());

        try {
            segments =
                IdentifierNode.parseIdentifier("[foo].[bar").getSegmentList();
            fail("expected exception, got " + segments);
        } catch (RuntimeException e) {
            assertEquals(
                "Expected ']', in member identifier '[foo].[bar'",
                e.getMessage());
        }
    }

    /**
     * Tests that escaped single quotes ('') nested inside a quoted
     * part of a query are handled correctly. The MDX language allows
     * expressions for calculated members and sets to be specified with and
     * without single quotes; the unparser generates expressions without quotes.
     */
    public void testQuoteEscaping() {
        String query =
            "WITH\n"
            + "MEMBER [CustomerDim].[CustomerName].[XL_QZX] AS 'Aggregate"
            + "({[CustomerDim].[CustomerName].&[ABC INT''L],"
            + " [CustomerDim].[CustomerName].&[XYZ]})'\n"
            + "SELECT\n"
            + "{[Measures].[Sales]} ON COLUMNS\n"
            + "FROM [cube]\n"
            + "WHERE ([CustomerDim].[CustomerName].[XL_QZX])";
        final MdxParser parser = new DefaultMdxParserImpl();
        SelectNode rootNode = parser.parseSelect(query);
        TestContext.assertEqualsVerbose(
            "WITH\n"
            + "MEMBER [CustomerDim].[CustomerName].[XL_QZX] AS\n"
            + "    Aggregate({[CustomerDim].[CustomerName].&[ABC INT'L], [CustomerDim].[CustomerName].&[XYZ]})\n"
            + "SELECT\n"
            + "{[Measures].[Sales]} ON COLUMNS\n"
            + "FROM [cube]\n"
            + "WHERE ([CustomerDim].[CustomerName].[XL_QZX])",
            rootNode.toString());

        // Now named set
        query =
            "WITH SET Foo as Filter(Bar.Members, Instr(Name, \"'\") > 0)\n"
            + "SELECT FROM [Cube]";
        rootNode = parser.parseSelect(query);
        TestContext.assertEqualsVerbose(
            "WITH\n"
            + "SET Foo AS\n"
            + "    Filter(Bar.Members, (Instr(Name, \"'\") > 0.0))\n"
            + "SELECT\n"
            + "FROM [Cube]",
            rootNode.toString());
    }
}

// End MdxTest.java
