/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import junit.framework.TestCase;
import org.olap4j.mdx.*;

import java.util.*;

/**
 * Tests for methods in {@link org.olap4j.impl.Olap4jUtil}.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 12, 2007
 */
public class Olap4jUtilTest extends TestCase {

    //~ Helper methods =========================================================

    /**
     * Asserts that two integer arrays have equal length and contents.
     *
     * @param expected Expected integer array
     * @param actual Actual integer array
     */
    public void assertEqualsArray(int[] expected, int[] actual) {
        if (expected == null) {
            assertEquals(expected, actual);
        } else {
            List<Integer> expectedList = new ArrayList<Integer>();
            for (int i : expected) {
                expectedList.add(i);
            }
            List<Integer> actualList = new ArrayList<Integer>();
            for (int i : actual) {
                actualList.add(i);
            }
            assertEquals(expectedList, actualList);
        }
    }

    //~ Tests follow ===========================================================

    /**
     * Tests {@link Olap4jUtil#camelToUpper}.
     */
    public void testCamel() {
        assertEquals(
            "FOO_BAR",
            Olap4jUtil.camelToUpper("FooBar"));
        assertEquals(
            "FOO_BAR",
            Olap4jUtil.camelToUpper("fooBar"));
        assertEquals(
            "URL",
            Olap4jUtil.camelToUpper("URL"));
        assertEquals(
            "URLTO_CLICK_ON",
            Olap4jUtil.camelToUpper("URLtoClickOn"));
        assertEquals(
            "",
            Olap4jUtil.camelToUpper(""));
    }

    /**
     * Tests {@link Olap4jUtil#equal}.
     */
    public void testEqual() {
        assertTrue(Olap4jUtil.equal("x", "x"));
        assertFalse(Olap4jUtil.equal("x", "y"));
        assertTrue(Olap4jUtil.equal("xy", "x" + "y"));
        assertTrue(Olap4jUtil.equal(null, null));
        assertFalse(Olap4jUtil.equal(null, "x"));
        assertFalse(Olap4jUtil.equal("x", null));
        final Integer TWO = 1 + 1;
        assertTrue(Olap4jUtil.equal(2, TWO));
    }

    /**
     * Tests {@link Olap4jUtil#wildcardToRegexp(java.util.List)}.
     */
    public void testWildcard() {
        assertEquals(
            ".\\QFoo\\E.|\\QBar\\E.*\\QBAZ\\E",
            Olap4jUtil.wildcardToRegexp(
                Arrays.asList("_Foo_", "Bar%BAZ")));
    }

    /**
     * Tests {@link org.olap4j.impl.CoordinateIterator}.
     */
    public void testCoordinateIterator() {
        // no axes, should produce one result
        CoordinateIterator iter = new CoordinateIterator(new int[]{});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {});

        // one axis of length n, should produce n elements
        iter = new CoordinateIterator(new int[]{2});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {0});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {1});
        assertFalse(iter.hasNext());

        // one axis of length 0, should produce 0 elements
        iter = new CoordinateIterator(new int[]{0});
        assertFalse(iter.hasNext());

        // two axes of length 0, should produce 0 elements
        iter = new CoordinateIterator(new int[]{0, 0});
        assertFalse(iter.hasNext());

        // five axes of length 0, should produce 0 elements
        iter = new CoordinateIterator(new int[]{0, 0, 0, 0, 0});
        assertFalse(iter.hasNext());

        // two axes, neither empty
        iter = new CoordinateIterator(new int[]{2, 3});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {0, 0});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {0, 1});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {0, 2});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {1, 0});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {1, 1});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {1, 2});
        assertFalse(iter.hasNext());

        // three axes, one of length 0, should produce 0 elements
        iter = new CoordinateIterator(new int[]{10, 0, 2});
        assertFalse(iter.hasNext());
        iter = new CoordinateIterator(new int[]{0, 10, 2});
        assertFalse(iter.hasNext());

        // if any axis has negative length, produces 0 elements
        iter = new CoordinateIterator(new int[]{3, 4, 5, -6, 7});
        assertFalse(iter.hasNext());
        iter = new CoordinateIterator(new int[]{3, 4, 5, 6, -7});
        assertFalse(iter.hasNext());
        iter = new CoordinateIterator(new int[]{-3, 4, 5, 6, 7});
        assertFalse(iter.hasNext());
    }

    /**
     * Tests a little-endian {@link org.olap4j.impl.CoordinateIterator}.
     */
    public void testCoordinateIteratorLittleEndian() {
        // two axes, neither empty
        CoordinateIterator iter =
            new CoordinateIterator(new int[]{2, 3}, true);
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {0, 0});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {1, 0});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {0, 1});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {1, 1});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {0, 2});
        assertTrue(iter.hasNext());
        assertEqualsArray(iter.next(), new int[] {1, 2});
        assertFalse(iter.hasNext());
    }

    /**
     * Tests {@link org.olap4j.impl.UnmodifiableArrayList}.
     */
    public void testUnmodifiableArrayList() {
        String[] a = {"x", "y"};
        final UnmodifiableArrayList<String> list =
            new UnmodifiableArrayList<String>(a);
        final UnmodifiableArrayList<String> copyList =
            UnmodifiableArrayList.asCopyOf(a);

        assertEquals(2, list.size());
        assertEquals("x", list.get(0));
        assertEquals("y", list.get(1));
        try {
            final String s = list.get(2);
            fail("expected error, got " + s);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }

        // check various equality relations
        assertTrue(list.equals(copyList));
        assertTrue(copyList.equals(list));
        assertTrue(list.equals(list));
        assertEquals(list.hashCode(), copyList.hashCode());
        assertEquals(Arrays.asList(a), list);
        assertEquals(list, Arrays.asList(a));

        String sum = "";
        for (String s : list) {
            sum = sum + s;
        }
        assertEquals("xy", sum);
        final Iterator<String> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("x", iterator.next());
        try {
            iterator.remove();
            fail("expected error");
        } catch (UnsupportedOperationException e) {
            // ok
        }
        a[1] = "z";
        assertTrue(iterator.hasNext());
        assertEquals("z", iterator.next());
        assertFalse(iterator.hasNext());

        // modifying the array modifies the list, but not the clone list
        assertEquals("z", list.get(1));
        assertEquals("y", copyList.get(1));

        // test the of(Collection) method
        final List<String> arrayList = new ArrayList<String>();
        arrayList.add("foo");
        arrayList.add("bar");
        final UnmodifiableArrayList<String> list3 =
            UnmodifiableArrayList.of(arrayList);
        assertEquals(2, list3.size());
        assertEquals(arrayList, list3);
        assertEquals(arrayList.hashCode(), list3.hashCode());
    }

    /**
     * Unit test for {@link Olap4jUtil#parseUniqueName(String)}.
     */
    public void testUniqueNameToStringArray() {
        List<String> a;

        a = Olap4jUtil.parseUniqueName("foo.bar");
        assertEquals(2, a.size());
        assertEquals("foo", a.get(0));
        assertEquals("bar", a.get(1));

        // with spaces
        a = Olap4jUtil.parseUniqueName("[foo bar].[baz]");
        assertEquals(2, a.size());
        assertEquals("foo bar", a.get(0));
        assertEquals("baz", a.get(1));

        // with dots
        a = Olap4jUtil.parseUniqueName("[foo.bar].[baz]");
        assertEquals(3, a.size());
        assertEquals("foo", a.get(0));
        assertEquals("bar", a.get(1));
        assertEquals("baz", a.get(2));

        // Unique names can have '&'s in them. I'm not sure that this is the
        // behavior we want, but this test at least documents the current
        // behavior.
        a = Olap4jUtil.parseUniqueName("[customers].&[baz]&[2]");
        assertEquals(5, a.size());
        assertEquals("customers", a.get(0));
        assertEquals("&", a.get(1));
        assertEquals("baz", a.get(2));
        assertEquals("&", a.get(3));
        assertEquals("2", a.get(4));
    }

    public void testParseFormattedCellValue() {
        // no formatting
        checkParseFormattedCellValue("123", "123", "{}");

        // no formatting, value contains '|'
        checkParseFormattedCellValue("12|3", "12|3", "{}");

        // empty string
        checkParseFormattedCellValue("", "", "{}");

        // one property
        checkParseFormattedCellValue("|123|style=red|", "123", "{style=red}");

        // multiple properties
        checkParseFormattedCellValue(
            "|123|style=red|arrow=up|", "123", "{arrow=up, style=red}");

        // invalid property value -- we don't care
        checkParseFormattedCellValue(
            "|123|style=red|arrow=asdas|", "123", "{arrow=asdas, style=red}");

        // empty value
        checkParseFormattedCellValue("||style=red|", "", "{style=red}");

        // empty property value
        checkParseFormattedCellValue(
            "|abc|style=|foo=bar|", "abc", "{foo=bar, style=}");

        // REVIEW: spaces in property value cause it not to be recognized as a
        // property
        checkParseFormattedCellValue(
            "|abc|style=xx|foo=bar  baz|", "abcfoo=bar  baz", "{style=xx}");

        // spaces in property value recognized, provided property value is
        // enclosed in quotes
        checkParseFormattedCellValue(
            "|abc|style=xx|foo='bar  baz'|", "abc", "{foo=bar  baz, style=xx}");

        // '=' in property value
        checkParseFormattedCellValue(
            "|abc|style=xx|foo=baz=zz|", "abc", "{foo=baz=zz, style=xx}");

        // missing '|' terminator
        checkParseFormattedCellValue(
            "|abc|foo=bar", "abc", "{foo=bar}");

        // null value
        try {
            String s =
                Olap4jUtil.parseFormattedCellValue(
                    null, new HashMap<String, String>());
            fail("expected NPE, got " + s);
        } catch (NullPointerException e) {
            // ok
        }
    }

    private void checkParseFormattedCellValue(
        String formattedCellValue,
        String expectedCellValue,
        String expectedProperties)
    {
        final TreeMap<String, String> map = new TreeMap<String, String>();
        final String cellValue =
            Olap4jUtil.parseFormattedCellValue(formattedCellValue, map);
        assertEquals("cell value", expectedCellValue, cellValue);
        assertEquals("properties", expectedProperties, map.toString());
    }

    /**
     * Tests the {@link IdentifierNode#parseIdentifier} method.
     */
    public void testParseIdentifier() {
        List<IdentifierSegment> segments =
            IdentifierParser.parseIdentifier(
                "[string].[with].[a [bracket]] in it]");
        assertEquals(3, segments.size());
        assertEquals("a [bracket] in it", segments.get(2).getName());

        segments =
            IdentifierParser.parseIdentifier(
                "[Worklog].[All].[calendar-[LANGUAGE]].js]");
        assertEquals(3, segments.size());
        assertEquals("calendar-[LANGUAGE].js", segments.get(2).getName());

        // allow spaces before, after and between
        segments = IdentifierParser.parseIdentifier("  [foo] . [bar].[baz]  ");
        assertEquals(3, segments.size());
        assertEquals("foo", segments.get(0).getName());

        // first segment not quoted
        segments = IdentifierParser.parseIdentifier("Time.1997.[Q3]");
        assertEquals(3, segments.size());
        assertEquals("Time", segments.get(0).getName());
        assertEquals("1997", segments.get(1).getName());
        assertEquals("Q3", segments.get(2).getName());

        // spaces ignored after unquoted segment
        segments =
            IdentifierParser.parseIdentifier("[Time . Weekly ] . 1997 . [Q3]");
        assertEquals(3, segments.size());
        assertEquals("Time . Weekly ", segments.get(0).getName());
        assertEquals("1997", segments.get(1).getName());
        assertEquals("Q3", segments.get(2).getName());

        // identifier ending in '.' is invalid
        try {
            segments = IdentifierParser.parseIdentifier("[foo].[bar].");
            fail("expected exception, got " + segments);
        } catch (IllegalArgumentException e) {
            assertEquals(
                "Expected identifier after '.', in member identifier "
                + "'[foo].[bar].'",
                e.getMessage());
        }

        try {
            segments = IdentifierParser.parseIdentifier("[foo].[bar");
            fail("expected exception, got " + segments);
        } catch (IllegalArgumentException e) {
            assertEquals(
                "Expected ']', in member identifier '[foo].[bar'",
                e.getMessage());
        }

        try {
            segments = IdentifierParser.parseIdentifier("[Foo].[Bar], [Baz]");
            fail("expected exception, got " + segments);
        } catch (IllegalArgumentException e) {
            assertEquals(
                "Invalid member identifier '[Foo].[Bar], [Baz]'",
                e.getMessage());
        }

        // test case for bug 3036629, "Patch 328 breaks test".
        segments = IdentifierParser.parseIdentifier(
            "[ProductFilterDim].[Product Main Group Name].&[Maingroup (xyz)]");
        assertEquals(3, segments.size());
        final IdentifierSegment s0 = segments.get(0);
        assertEquals("ProductFilterDim", s0.getName());
        assertEquals(Quoting.QUOTED, s0.getQuoting());
        final IdentifierSegment s1 = segments.get(1);
        assertEquals("Product Main Group Name", s1.getName());
        assertEquals(Quoting.QUOTED, s1.getQuoting());
        assertTrue(segments.get(2) instanceof KeySegment);
        KeySegment s2 =
            (KeySegment) segments.get(2);
        assertEquals(1, s2.getKeyParts().size());
        final NameSegment s2k0 = s2.getKeyParts().get(0);
        assertEquals("Maingroup (xyz)", s2k0.getName());
        assertEquals(Quoting.QUOTED, s2k0.getQuoting());
    }

    /**
     * Advanced test for the {@link IdentifierNode#parseIdentifier} method.
     */
    public void testParseIdentifierAdvanced() {
        List<IdentifierSegment> segments;

        // detailed example, per javadoc
        //
        // A more complex example illustrates a compound key. The identifier
        // [Customers].[City].&[San Francisco]&CA&USA.&[cust1234]
        // contains four segments as follows:
        //
        // * Segment #0 is QUOTED, name "Customers"
        // * Segment #1 is QUOTED, name "City"
        // * Segment #2 is a KEY. It has 3 sub-segments:
        //    ** Sub-segment #0 is QUOTED, name "San Francisco"
        //    ** Sub-segment #1 is UNQUOTED, name "CA"
        //    ** Sub-segment #2 is UNQUOTED, name "USA"
        // * Segment #3 is a KEY. It has 1 sub-segment:
        //    ** Sub-segment #0 is QUOTED, name "cust1234"</li>
        segments = IdentifierParser.parseIdentifier(
            "[Customers].[City].&[San Francisco]&CA&USA.&[cust1234]");
        assertEquals(4, segments.size());
        final IdentifierSegment s0 = segments.get(0);
        assertEquals("Customers", s0.getName());
        assertEquals(Quoting.QUOTED, s0.getQuoting());
        final IdentifierSegment s1 = segments.get(1);
        assertEquals("City", s1.getName());
        assertEquals(Quoting.QUOTED, s1.getQuoting());
        assertTrue(segments.get(2) instanceof KeySegment);
        KeySegment s2 =
            (KeySegment) segments.get(2);
        assertEquals(3, s2.getKeyParts().size());
        final NameSegment s2k0 = s2.getKeyParts().get(0);
        assertEquals("San Francisco", s2k0.getName());
        assertEquals(Quoting.QUOTED, s2k0.getQuoting());
        final NameSegment s2k1 = s2.getKeyParts().get(1);
        assertEquals("CA", s2k1.getName());
        assertEquals(Quoting.QUOTED, s2k0.getQuoting());
        final NameSegment s2k2 = s2.getKeyParts().get(0);
        assertEquals("San Francisco", s2k2.getName());
        assertEquals(Quoting.QUOTED, s2k2.getQuoting());
        KeySegment s3 =
            (KeySegment) segments.get(3);
        assertNull(s3.getName());
        assertEquals(1, s3.getKeyParts().size());
        final NameSegment s3k0 = s3.getKeyParts().get(0);
        assertEquals("cust1234", s3k0.getName());
        assertEquals(Quoting.QUOTED, s3k0.getQuoting());
    }

    /**
     * Tests the {@link IdentifierParser#parseIdentifierList(String)} method.
     */
    public void testParseIdentifierList() {
        List<List<IdentifierSegment>> list;

        list = IdentifierParser.parseIdentifierList("{foo, baz.baz}");
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).size());
        assertEquals(2, list.get(1).size());

        // now without braces
        list = IdentifierParser.parseIdentifierList("foo, baz.baz");
        assertEquals(2, list.size());

        // now with spaces
        list = IdentifierParser.parseIdentifierList(" {  foo , baz.baz }   ");
        assertEquals(2, list.size());

        // now with spaces & without braces
        list = IdentifierParser.parseIdentifierList(" {  foo , baz.baz }   ");
        assertEquals(2, list.size());

        // now with keys
        list = IdentifierParser.parseIdentifierList(
            "{foo , baz.&k0&k1.&m0 . boo}");
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).size());
        assertEquals(4, list.get(1).size());
        assertEquals("baz", list.get(1).get(0).getName());
        final IdentifierSegment id1s1 = list.get(1).get(1);
        assertEquals(2, id1s1.getKeyParts().size());
        assertEquals("k0", id1s1.getKeyParts().get(0).getName());
        assertEquals("k1", id1s1.getKeyParts().get(1).getName());
        final IdentifierSegment id1s2 = list.get(1).get(2);
        assertEquals(1, id1s2.getKeyParts().size());
        assertEquals("m0", id1s2.getKeyParts().get(0).getName());
        assertEquals("boo", list.get(1).get(3).getName());
        assertEquals("[baz, &k0&k1, &m0, boo]", list.get(1).toString());

        // now with mismatched braces
        try {
            list = IdentifierParser.parseIdentifierList(" {  foo , baz.baz ");
            fail("expected error, got " + list);
        } catch (RuntimeException e) {
            assertEquals(
                "mismatched '{' and '}' in ' {  foo , baz.baz '",
                e.getMessage());
        }

        // now with mismatched braces
        try {
            list = IdentifierParser.parseIdentifierList("  foo , baz.baz } ");
            fail("expected error, got " + list);
        } catch (RuntimeException e) {
            assertEquals(
                "mismatched '{' and '}' in '  foo , baz.baz } '",
                e.getMessage());
        }

        // empty string yields empty list
        list = IdentifierParser.parseIdentifierList("{}");
        assertEquals(0, list.size());
        list = IdentifierParser.parseIdentifierList(" {  } ");
        assertEquals(0, list.size());
        list = IdentifierParser.parseIdentifierList("");
        assertEquals(0, list.size());
        list = IdentifierParser.parseIdentifierList(" \t\n");
        assertEquals(0, list.size());
    }

    public void testParseTupleList() {
        final StringBuilder buf = new StringBuilder();
        final IdentifierParser.Builder builder =
            new IdentifierParser.Builder() {
                public void tupleComplete() {
                    buf.append("<tuple>");
                }

                public void memberComplete() {
                    buf.append("<member>");
                }

                public void segmentComplete(
                    ParseRegion region,
                    String name,
                    Quoting quoting,
                    Syntax syntax)
                {
                    if (quoting == Quoting.QUOTED) {
                        buf.append("[").append(name).append("]");
                    } else {
                        buf.append(name);
                    }
                    buf.append("<").append(syntax).append(">");
                }
            };

        // Set of tuples.
        buf.setLength(0);
        IdentifierParser.parseTupleList(
            builder, "{([Foo]), ([Bar].[Baz].&k0&[k1].&[k2])}");
        assertEquals(
            "[Foo]<NAME><member><tuple>"
            + "[Bar]<NAME>[Baz]<NAME>"
            + "k0<FIRST_KEY>[k1]<NEXT_KEY>[k2]<FIRST_KEY>"
            + "<member><tuple>",
            buf.toString());

        // Set of members.
        buf.setLength(0);
        try {
            IdentifierParser.parseTupleList(builder, "{[Foo], [Bar].[Baz]}");
            fail("expected error");
        } catch (IllegalArgumentException e) {
            assertEquals(
                "expected '(' at position 2 in '{[Foo], [Bar].[Baz]}'",
                e.getMessage());
        }

        // Empty set.
        // TODO: this shouldn't fail
        buf.setLength(0);
        try {
            IdentifierParser.parseTupleList(builder, "{ }");
            fail("expected error");
        } catch (IllegalArgumentException e) {
            assertEquals(
                "expected '(' at position 3 in '{ }'",
                e.getMessage());
        }

        // Empty set (no brackets).
        // TODO: this shouldn't fail
        buf.setLength(0);
        try {
            IdentifierParser.parseTupleList(builder, "");
            fail("expected error");
        } catch (IllegalArgumentException e) {
            assertEquals(
                "expected '{' at position 1 in ''",
                e.getMessage());
        }

        // Set of mixed tuples & members.
        // TODO: this shouldn't fail
        buf.setLength(0);
        try {
            IdentifierParser.parseTupleList(
                builder, "{([A], [Tuple]), [A].Member}");
        } catch (IllegalArgumentException e) {
            assertEquals(
                "expected '(' at position 18 in '{([A], [Tuple]), [A].Member}'",
                e.getMessage());
        }

        // Same, but no braces.
        // TODO: this shouldn't fail
        buf.setLength(0);
        try {
            IdentifierParser.parseTupleList(
                builder, "([A], [Tuple]), [A].Member");
        } catch (IllegalArgumentException e) {
            assertEquals(
                "expected '{' at position 1 in '([A], [Tuple]), [A].Member'",
                e.getMessage());
        }
    }
}

// End Olap4jUtilTest.java
