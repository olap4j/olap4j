/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2008-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
 */
package org.olap4j.transform;

import java.sql.Connection;
import java.sql.SQLException;

import org.olap4j.Axis;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapStatement;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.test.TestContext;

import junit.framework.TestCase;

/**
 * Testcase for org.olap4j.transform package.
 *
 * @author etdub
 * @author jhyde
 * @version $Id$
 * @since Jul 28, 2008
 */
public class TransformTest extends TestCase {
    final TestContext.Tester tester = TestContext.instance().getTester();
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
            + "       {[Product].[All Products].Children} ON ROWS "
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
            + "       {[Product].[All Products].[Food].[Deli].Parent.Level.Members} ON ROWS "
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
