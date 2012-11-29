/*
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
package org.olap4j;

import org.olap4j.metadata.*;
import org.olap4j.test.TestContext;

import junit.framework.TestCase;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests some particularities of the OLAP tree objects.
 * @author Luc Boudreau
 */
public class OlapTreeTest extends TestCase {

    private TestContext.Tester tester =
        TestContext.instance().getTester();

    /**
     * Simple strategy to prevent connection leaks: each test that needs a
     * connection assigns it to this field, and {@link #tearDown()} closes it
     * if it is not already closed.
     */
    private Connection connection;

    protected void tearDown() throws Exception {
        super.tearDown();
        // Simple strategy to prevent connection leaks
        if (connection != null
            && !connection.isClosed())
        {
            connection.close();
            connection = null;
        }
        tester = null;
    }

    /**
     * Tests if olap objects can be included in collections and
     * retrieved properly.
     */
    public void testHashCompatibility() throws Exception {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        Schema schema = olapConnection.getOlapSchema();
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
