/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Schema;
import org.olap4j.test.TestContext;

import junit.framework.TestCase;

/**
 * Tests some particularities of the OLAP tree objects.
 * @author Luc Boudreau
 */
public class OlapTreeTest extends TestCase {

    private final TestContext.Tester tester =
        TestContext.instance().getTester();

    /**
     * Simple strategy to prevent connection leaks: each test that needs a
     * connection assigns it to this field, and {@link #tearDown()} closes it
     * if it is not already closed.
     */
    private Connection connection;

    protected void tearDown() throws Exception {
        // Simple strategy to prevent connection leaks
        if (connection != null
            && !connection.isClosed())
        {
            connection.close();
            connection = null;
        }
    }

    /**
     * Tests if olap objects can be included in collections and
     * retrieved properly.
     */
    public void testHashCompatibility() throws Exception {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        Schema schema = olapConnection.getSchema();
        Cube cube = schema.getCubes().get("Sales");

        Map<Dimension, String> dimensionMap =
            new HashMap<Dimension, String>();
        Dimension dim1 = cube.getDimensions().get("Promotion Media");
        dimensionMap.put(dim1, "Test1");
        assertTrue(dimensionMap.containsKey(dim1));
        assertEquals("Test1", dimensionMap.get(dim1));

        Map<Hierarchy, String> hierarchyMap =
            new HashMap<Hierarchy, String>();
        Hierarchy hchy1 = dim1.getDefaultHierarchy();
        hierarchyMap.put(hchy1, "Test2");
        assertTrue(hierarchyMap.containsKey(hchy1));
        assertEquals("Test2", hierarchyMap.get(hchy1));
        assertTrue(dimensionMap.containsKey(hchy1.getDimension()));
    }
}
// End OlapTreeTest.java