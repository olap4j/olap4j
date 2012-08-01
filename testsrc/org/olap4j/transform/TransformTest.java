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
package org.olap4j.transform;

import org.olap4j.*;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.test.TestContext;

import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Testcase for org.olap4j.transform package.
 *
 * @author etdub
 * @author jhyde
 * @version $Id$
 * @since Jul 28, 2008
 */
public class TransformTest extends TestCase {
    private TestContext testContext = TestContext.instance();
    private TestContext.Tester tester = testContext.getTester();
    private Connection connection = null;

    public TransformTest() {
        super();
    }

    /**
     * Simple strategy to prevent connection leaks: each test that needs a
     * connection assigns it to this field, and {@link #tearDown()} closes it
     * if it is not already closed.
     */
    protected void tearDown() throws Exception {
        // Simple strategy to prevent connection leaks
        if (connection != null
            && !connection.isClosed())
        {
            connection.close();
            connection = null;
        }
        testContext = null;
        tester = null;
    }

    protected OlapConnection getConnection() throws SQLException {
        if (connection == null) {
            connection = tester.createConnection();
        }
        return tester.getWrapper().unwrap(connection, OlapConnection.class);
    }

    protected OlapStatement getStatement() throws SQLException {
        return getConnection().createStatement();
    }

    /**
     * Asserts the functionality of a transformation
     *
     * @param initialMdx initial MDX query to be transformed
     * @param expectedMdx expected MDX query after applying transform; will
     *                    be compared against the transformed query
     * @param trans the transformation to apply to the initial query
     *
     * @throws java.sql.SQLException on error
     */
    public void assertTransformTo(
        String initialMdx,
        String expectedMdx,
        MdxQueryTransform trans) throws SQLException
    {
        OlapConnection olapConnection = getConnection();

        MdxParser mdxParser =
            olapConnection.getParserFactory().createMdxParser(olapConnection);

        SelectNode before = mdxParser.parseSelect(initialMdx);
        SelectNode after = trans.apply(before);

        // we also parse the expected MDX in order to normalize it
        // (eliminate any whitespace / capitalization differences)
        // note: CallNodes are not aware of function names, and as such
        // will be compared in a case-sensitive manner
        // (i.e. [SomeMember].Children vs [SomeMember].children are not
        // equal, even if they are equivalent in MDX)
        SelectNode expected = mdxParser.parseSelect(expectedMdx);

        // TODO: consider adding .equals() method to ParseTreeNode instead
        // of comparing strings (we could ignore case when comparing
        // function names in CallNodes ...)
        assertEquals(expected.toString(), after.toString());
    }

    /**
     * Unit test for DrillReplaceTransform.
     *
     * @throws java.sql.SQLException on error
     */
    public void testDrillReplaceTransform() throws SQLException {
        final String initialMdx =
            "SELECT {[Measures].[Unit Sales], "
            + "        [Measures].[Store Cost], "
            + "        [Measures].[Store Sales]} ON COLUMNS, "
            + "       {[Product].[All Products]} ON ROWS "
            + "FROM Sales "
            + "WHERE ([Time].[1997])";

        final String expectedMdx =
            "SELECT {[Measures].[Unit Sales], "
            + "        [Measures].[Store Cost], "
            + "        [Measures].[Store Sales]} ON COLUMNS, "
            + "       {[Product].[Product].[All Products].Children} ON ROWS "
            + "FROM Sales "
            + "WHERE ([Time].[1997])";

        CellSet cellSet = getStatement().executeOlapQuery(initialMdx);

        MdxQueryTransform transform =
            StandardTransformLibrary.createDrillReplaceTransform(
                Axis.ROWS,
                0, // position ordinal in axis
                0, // member ordinal in position
                cellSet);

        assertTransformTo(initialMdx, expectedMdx, transform);
    }

    /**
     * Unit test for RollUpLevelTransform.
     *
     * @throws java.sql.SQLException on error
     */
    public void testRollUpLevelTransform() throws SQLException {
        final String initialMdx =
            "SELECT {[Measures].[Unit Sales], "
            + "        [Measures].[Store Cost], "
            + "        [Measures].[Store Sales]} ON COLUMNS, "
            + "       {[Product].[All Products].[Food].[Deli]} ON ROWS "
            + "FROM Sales "
            + "WHERE ([Time].[1997])";

        final String expectedMdx =
            "SELECT {[Measures].[Unit Sales], "
            + "        [Measures].[Store Cost], "
            + "        [Measures].[Store Sales]} ON COLUMNS, "
            + "       {[Product].[Product].[Food].[Deli].Parent.Level.Members} ON ROWS "
            + "FROM Sales "
            + "WHERE ([Time].[1997])";

        CellSet cellSet = getStatement().executeOlapQuery(initialMdx);

        MdxQueryTransform transform =
            StandardTransformLibrary.createRollUpLevelTransform(
                Axis.ROWS,
                0, // position ordinal in axis
                0, // member ordinal in position
                cellSet);

        assertTransformTo(initialMdx, expectedMdx, transform);
    }

    /**
     * Unit test for DrillDownOnPositionTransform.
     *
     * @throws java.sql.SQLException on error
     */
    public void _testDrillDownOnPositionTransform() throws SQLException {
        // TODO: rewrite the initial and expected MDX once this transform
        // is written.
        // Will fail for now.

        final String initialMdx =
            "SELECT {[Measures].[Unit Sales], "
            + "        [Measures].[Store Cost], "
            + "        [Measures].[Store Sales]} ON COLUMNS, "
            + "       {[Product].[All Products]} ON ROWS "
            + "FROM Sales "
            + "WHERE ([Time].[1997])";

        final String expectedMdx =
            "SELECT {[Measures].[Unit Sales], "
            + "        [Measures].[Store Cost], "
            + "        [Measures].[Store Sales]} ON COLUMNS, "
            + "       {[Product].[All Products].Children} ON ROWS "
            + "FROM Sales "
            + "WHERE ([Time].[1997])";

        CellSet cellSet = getStatement().executeOlapQuery(initialMdx);

        MdxQueryTransform transform =
            StandardTransformLibrary.createDrillDownOnPositionTransform(
                Axis.ROWS,
                0, // position ordinal in axis
                0, // member ordinal in position
                cellSet);

        assertTransformTo(initialMdx, expectedMdx, transform);
    }
}

// End TransformTest.java
