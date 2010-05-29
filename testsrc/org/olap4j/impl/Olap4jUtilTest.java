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
        final ArrayList<String> arrayList = new ArrayList<String>();
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
}

// End Olap4jUtilTest.java
