/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.mdx.parser.impl.DefaultMdxParserImpl;
import org.olap4j.test.TestContext;

import junit.framework.TestCase;

import java.util.*;

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
                    new NameSegment(
                        null, "Store", Quoting.QUOTED),
                    new NameSegment(
                        null, "USA", Quoting.QUOTED),
                    new NameSegment(
                        null, "California", Quoting.QUOTED))));
    }

    public void testImplode() {
        List<IdentifierSegment> fooBar =
            Arrays.<IdentifierSegment>asList(
                new NameSegment(
                    null, "foo", Quoting.UNQUOTED),
                new NameSegment(
                    null, "bar", Quoting.QUOTED));
        assertEquals(
            "foo.[bar]",
            IdentifierNode.unparseIdentifierList(fooBar));

        List<IdentifierSegment> empty = Collections.emptyList();
        assertEquals("", IdentifierNode.unparseIdentifierList(empty));

        List<IdentifierSegment> nasty =
            Arrays.<IdentifierSegment>asList(
                new NameSegment(
                    null, "string", Quoting.QUOTED),
                new NameSegment(
                    null, "with", Quoting.QUOTED),
                new NameSegment(
                    null, "a [bracket] in it", Quoting.QUOTED));
        assertEquals(
            "[string].[with].[a [bracket]] in it]",
            IdentifierNode.unparseIdentifierList(nasty));
    }

    public void testParseIdentifier() {
        List<IdentifierSegment> segments =
            IdentifierNode.parseIdentifier(
                "[string].[with].[a [bracket]] in it]").getSegmentList();
        assertEquals(3, segments.size());
        assertEquals(
            "a [bracket] in it",
            segments.get(2).getName());
        assertEquals(
            Quoting.QUOTED,
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
            Quoting.QUOTED,
            segments.get(0).getQuoting());
        assertEquals(
            Quoting.UNQUOTED,
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
     * Unit test for {@link org.olap4j.mdx.IdentifierNode#ofNames(String...)}.
     */
    public void testIdentifierOfNames() {
        IdentifierNode identifierNode =
            IdentifierNode.ofNames(
                "string", "with", "a [bracket] in it");
        List<IdentifierSegment> segments =
            identifierNode.getSegmentList();
        assertEquals(3, segments.size());
        assertEquals(
            "a [bracket] in it",
            segments.get(2).getName());
        assertEquals(
            Quoting.QUOTED,
            segments.get(2).getQuoting());

        assertEquals(
            "[string].[with].[a [bracket]] in it]",
            identifierNode.toString());

        // Empty array is invalid.
        try {
            identifierNode = IdentifierNode.ofNames();
            fail("expected error, got " + identifierNode);
        } catch (IllegalArgumentException e) {
            // ok
        }

        // Array containing null is not valid.
        try {
            identifierNode =
                IdentifierNode.ofNames("foo", null, "bar");
            fail("expected error, got " + identifierNode);
        } catch (NullPointerException e) {
            // ok
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
            + "    Filter(Bar.Members, (Instr(Name, \"'\") > 0))\n"
            + "SELECT\n"
            + "FROM [Cube]",
            rootNode.toString());
    }
}

// End MdxTest.java
