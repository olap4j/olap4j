/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2009 Julian Hyde
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
}

// End Olap4jUtilTest.java
