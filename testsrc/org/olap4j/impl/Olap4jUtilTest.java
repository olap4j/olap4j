/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Tests for methods in {@link org.olap4j.impl.Olap4jUtil}.
 *
 * @author jhyde
 * @version $Id: $
 * @since Dec 12, 2007
 */
public class Olap4jUtilTest extends TestCase {
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
}

// End Olap4jUtilTest.java
