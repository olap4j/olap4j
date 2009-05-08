/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.test;

import junit.framework.TestCase;
import org.olap4j.impl.ArrayMap;

import java.util.*;

/**
 * Unit test for {@link org.olap4j.impl.ArrayMap}.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 9, 2007
 */
public class ArrayMapTest extends TestCase {
    public void testArrayMap() {
        final ArrayMap<String, Integer> map =
            new ArrayMap<String, Integer>();

        // clear empty map
        map.clear();
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());

        map.put("Paul", 4);
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        assertTrue(map.containsKey("Paul"));
        assertFalse(map.containsKey("Keith"));

        Integer value = map.put("Paul", 5);
        assertEquals(1, map.size());
        assertEquals(4, value.intValue());
        assertEquals(5, map.get("Paul").intValue());

        // null values are allowed
        map.put("Paul", null);
        assertNull(map.get("Paul"));
        assertTrue(map.containsKey("Paul"));

        // null keys are allowed
        map.put(null, -99);
        assertEquals(-99, map.get(null).intValue());
        assertTrue(map.containsKey(null));
        assertEquals(-99, map.remove(null).intValue());

        final Map<String, Integer> beatles =
            new ArrayMap<String, Integer>();
        beatles.put("John", 4);
        beatles.put("Paul", 4);
        beatles.put("George", 6);
        beatles.put("Ringo", 5);
        map.putAll(beatles);
        assertEquals(4, map.size());
        assertEquals(
            new HashSet<String>(
                Arrays.asList("John", "Paul", "George", "Ringo")),
            map.keySet());
        assertEquals(Arrays.asList(4, 4, 6, 5), map.values());

        String keys = "";
        int valueTotal = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            valueTotal += entry.getValue();
            keys += entry.getKey();
        }
        assertEquals(19, valueTotal);
        assertEquals("PaulJohnGeorgeRingo", keys);

        value = map.remove("Mick");
        assertNull(value);
        assertEquals(4, map.size());

        // corner case: remove last value
        value = map.remove("Ringo");
        assertEquals(5, value.intValue());
        assertEquals(3, map.size());

        // corner case: remove first value
        value = map.remove("Paul");
        assertEquals(4, value.intValue());
        assertEquals(2, map.size());

        // add key back in and it becomes last value
        map.put("Paul", 27);
        assertEquals(Arrays.asList(4, 6, 27), map.values());

        // remove an interior value
        map.remove("George");
        assertEquals(2, map.size());
    }
}

// End ArrayMapTest.java
