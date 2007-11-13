/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import mondrian.tui.XmlaSupport;
import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.mdx.*;
import org.olap4j.mdx.parser.*;
import org.olap4j.metadata.*;
import org.olap4j.test.TestContext;
import org.olap4j.type.*;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Unit test for olap4j Driver and Connection classes.
 *
 * <p>The system property "org.olap4j.test.helperClassName" determines the
 * name of the helper class. By default, uses {@link MondrianHelper}, which
 * runs against mondrian; {@link XmlaHelper} is also available.
 *
 * @version $Id$
 */
public class ConnectionTest extends TestCase {
    private final Helper helper = createHelper();

    /**
     * Factory method for the {@link org.olap4j.ConnectionTest.Helper}
     * object which determines which driver to test.
     *
     * @return a new Helper
     */
    static Helper createHelper() {
        String helperClassName =
            System.getProperty("org.olap4j.test.helperClassName");
        if (helperClassName != null) {
            try {
                Class<?> clazz = Class.forName(helperClassName);
                return (Helper) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return new MondrianHelper();
    }

    private static final boolean IS_JDK_16 =
        System.getProperty("java.version").startsWith("1.6.");

    /**
     * Driver basics.
     */
    public void testDriver() throws ClassNotFoundException, SQLException {
        Class clazz = Class.forName(helper.getDriverClassName());
        assertNotNull(clazz);
        assertTrue(Driver.class.isAssignableFrom(clazz));

        // driver should have automatically registered itself
        Driver driver = DriverManager.getDriver(helper.getDriverUrlPrefix());
        assertNotNull(driver);

        // deregister driver
        DriverManager.deregisterDriver(driver);
        try {
            Driver driver2 = DriverManager.getDriver(helper.getDriverUrlPrefix());
            fail("expected error, got " + driver2);
        } catch (SQLException e) {
            assertEquals("No suitable driver", e.getMessage());
        }

        // register explicitly
        DriverManager.registerDriver(driver);
        Driver driver3 = DriverManager.getDriver(helper.getDriverUrlPrefix());
        assertNotNull(driver3);

        // test properties
        int majorVersion = driver.getMajorVersion();
        int minorVersion = driver.getMinorVersion();
        assertTrue(majorVersion > 0);
        assertTrue(minorVersion >= 0);

        // check that the getPropertyInfo method returns something sensible.
        // We can't test individual properties in this non-driver-specific test.
        DriverPropertyInfo[] driverPropertyInfos =
            driver.getPropertyInfo(
                helper.getDriverUrlPrefix(),
                new Properties());
        assertTrue(driverPropertyInfos.length > 0);
    }

    void assertIsValid(Connection connection, int timeout) {
        if (!IS_JDK_16) {
            return;
        }
        //  assertTrue(connection.isValid(0));
        try {
            java.lang.reflect.Method method =
                Connection.class.getMethod("isValid", int.class);
            Boolean b = (Boolean) method.invoke(connection, timeout);
            assertTrue(b);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks that the <code>isClosed</code> method of a Statement, ResultSet or
     * Connection object returns the expected result. Uses reflection because
     * the <code>isClosed</code> method only exists from JDBC 4.0 (JDK 1.6)
     * onwrds.
     *
     * @param o Connection, Statement or ResultSet object
     * @param b Expected result
     */
    void assertIsClosed(Object o, boolean b) {
        if (!IS_JDK_16) {
            return;
        }
        //  assertTrue(statment.isClosed());
        try {
            Class clazz;
            if (o instanceof Statement){
                clazz = Statement.class;
            } else if (o instanceof ResultSet) {
                clazz = ResultSet.class;
            } else if (o instanceof Connection) {
                clazz = Connection.class;
            } else {
                throw new AssertionFailedError(
                    "not a statement, resultSet or connection");
            }
            java.lang.reflect.Method method =
                clazz.getMethod("isClosed");
            Boolean closed = (Boolean) method.invoke(o);
            if (b) {
                assertTrue(closed);
            } else {
                assertFalse(closed);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void testConnection() throws ClassNotFoundException, SQLException {
        Class.forName(helper.getDriverClassName());

        // connect using properties and no username/password
        Connection connection = helper.createConnection();
        assertNotNull(connection);

        // check isClosed, isValid
        assertFalse(connection.isClosed());

        // check valid with no time limit
        assertIsValid(connection, 0);

        // check valid with one minute time limit; should be enough
        assertIsValid(connection, 60);

        connection.close();

        assertTrue(connection.isClosed());

        // it's ok to close twice
        connection.close();

        // connect using username/password
        connection = helper.createConnectionWithUserPassword();
        assertNotNull(connection);

        connection.close();
        assertTrue(connection.isClosed());

        // connect with URL only
        connection = DriverManager.getConnection(helper.getURL());
        assertNotNull(connection);

        connection.close();
        assertTrue(connection.isClosed());
    }

    public void testConnectionUnwrap() throws SQLException {
        java.sql.Connection connection = helper.createConnection();

        // Trivial unwrap
        assertTrue(((OlapWrapper) connection).isWrapperFor(Connection.class));
        Connection connection2 = ((OlapWrapper) connection).unwrap(Connection.class);
        assertEquals(connection2, connection);

        // Silly unwrap
        assertTrue(((OlapWrapper) connection).isWrapperFor(Object.class));
        Object object = ((OlapWrapper) connection).unwrap(Object.class);
        assertEquals(object, connection);

        // Invalid unwrap
        assertFalse(((OlapWrapper) connection).isWrapperFor(Writer.class));
        try {
            Writer writer = ((OlapWrapper) connection).unwrap(Writer.class);
            fail("expected exception, got writer" + writer);
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("does not implement"));
        }

        // Unwrap the mondrian connection.
        if (helper.isMondrian()) {
            final mondrian.olap.Connection mondrianConnection =
                ((OlapWrapper) connection).unwrap(mondrian.olap.Connection.class);
            assertNotNull(mondrianConnection);
        }
    }

    public void testStatement() throws SQLException {
        Connection connection = helper.createConnection();
        Statement statement = connection.createStatement();

        // Closing a statement is idempotent.
        assertIsClosed(statement, false);
        statement.close();
        assertIsClosed(statement, true);
        statement.close();
        assertIsClosed(statement, true);

        // Unwrap the statement to get the olap statement. Depending on the
        // driver, this may or may not be the same object.
        statement = connection.createStatement();
        OlapStatement olapStatement =
            ((OlapWrapper) statement).unwrap(OlapStatement.class);
        assertNotNull(olapStatement);

        // Execute a simple query.
        CellSet cellSet =
            olapStatement.executeOlapQuery(
                "SELECT FROM [Sales]");
        List<CellSetAxis> axesList = cellSet.getAxes();
        assertNotNull(axesList);
        assertEquals(0, axesList.size());

        // Executing another query implicitly closes the previous result set.
        assertIsClosed(statement, false);
        assertIsClosed(cellSet, false);
        CellSet cellSet2 =
            olapStatement.executeOlapQuery(
                "SELECT FROM [Sales]");
        assertIsClosed(statement, false);
        assertIsClosed(cellSet, true);

        // Close the statement; this closes the result set.
        assertIsClosed(cellSet2, false);
        statement.close();
        assertIsClosed(statement, true);
        assertIsClosed(cellSet2, true);
        cellSet.close();
        assertIsClosed(statement, true);
        assertIsClosed(cellSet2, true);
        assertIsClosed(cellSet, true);

        // Close the connection.
        connection.close();
    }

    // todo: test statement with no slicer

    private enum Method { ClassName, Mode, Type, TypeName, OlapType }

    public void testPreparedStatement() throws SQLException {
        Connection connection = helper.createConnection();
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);
        PreparedOlapStatement pstmt =
            olapConnection.prepareOlapStatement(
                "SELECT {\n" +
                    "   Parameter(\"P1\", [Store], [Store].[USA].[CA]).Parent,\n" +
                    "   ParamRef(\"P1\").Children} ON 0\n" +
                    "FROM [Sales]\n" +
                    "WHERE [Gender].[M]");
        OlapParameterMetaData parameterMetaData =
            pstmt.getParameterMetaData();
        int paramCount = parameterMetaData.getParameterCount();
        assertEquals(1, paramCount);
        int[] paramIndexes = {0, 1, 2};
        for (int paramIndex : paramIndexes) {
            for (Method method : Method.values()) {
                try {
                    switch (method) {
                    case ClassName:
                        String className =
                            parameterMetaData.getParameterClassName(paramIndex);
                        assertEquals("org.olap4j.metadata.Member", className);
                        break;
                    case Mode:
                        int mode =
                            parameterMetaData.getParameterMode(paramIndex);
                        assertEquals(ParameterMetaData.parameterModeIn, mode);
                        break;
                    case Type:
                        int type = parameterMetaData.getParameterType(paramIndex);
                        assertEquals(Types.OTHER, type);
                        break;
                    case TypeName:
                        String typeName =
                            parameterMetaData.getParameterTypeName(paramIndex);
                        assertEquals("MemberType<hierarchy=[Store]>", typeName);
                        break;
                    case OlapType:
                        Type olapType =
                            parameterMetaData.getParameterOlapType(paramIndex);
                        assertEquals(
                            "MemberType<hierarchy=[Store]>",
                            olapType.toString());
                        break;
                    }
                    if (paramIndex != 1) {
                        fail("expected exception");
                    }
                } catch (SQLException e) {
                    if (paramIndex == 1) {
                        throw e;
                    } else {
                        // ok - expecting exception
                    }
                }
            }
        }

        // Check metadata exists. (Support for this method is optional.)
        final CellSetMetaData metaData = pstmt.getMetaData();

        CellSet cellSet = pstmt.executeQuery();
        assertEquals(metaData, cellSet.getMetaData());
        String s = TestContext.toString(cellSet);
        TestContext.assertEqualsVerbose(
            TestContext.fold("Axis #0:\n" +
                "{[Gender].[All Gender].[M]}\n" +
                "Axis #1:\n" +
                "{[Store].[All Stores].[USA]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Alameda]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Beverly Hills]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Los Angeles]}\n" +
                "{[Store].[All Stores].[USA].[CA].[San Diego]}\n" +
                "{[Store].[All Stores].[USA].[CA].[San Francisco]}\n" +
                "Row #0: 135,215\n" +
                "Row #0: \n" +
                "Row #0: 10,562\n" +
                "Row #0: 13,574\n" +
                "Row #0: 12,800\n" +
                "Row #0: 1,053\n"),
            s);

        // Bind parameter and re-execute.
        final List<Position> positions =
            cellSet.getAxes().get(0).getPositions();
        final Member member =
            positions.get(positions.size() - 1).getMembers().get(0);
        pstmt.setObject(1, member);
        CellSet cellSet2 = pstmt.executeQuery();
        assertIsClosed(cellSet, true);
        assertIsClosed(cellSet2, false);
        s = TestContext.toString(cellSet2);
        TestContext.assertEqualsVerbose(
            TestContext.fold(
                "Axis #0:\n" +
                    "{[Gender].[All Gender].[M]}\n" +
                    "Axis #1:\n" +
                    "{[Store].[All Stores].[USA].[CA]}\n" +
                    "{[Store].[All Stores].[USA].[CA].[San Francisco].[Store 14]}\n" +
                    "Row #0: 37,989\n" +
                    "Row #0: 1,053\n"),
            s);

        // Re-execute with a new MDX string.
        CellSet cellSet3 = pstmt.executeOlapQuery("SELECT FROM [Sales] WHERE [Gender]");
        TestContext.assertEqualsVerbose(
            TestContext.fold("Axis #0:\n" +
                "{[Gender].[All Gender]}\n" +
                "266,773"),
            TestContext.toString(cellSet3));

        // Number of parameters has changed.
        OlapParameterMetaData parameterMetaData1 = pstmt.getParameterMetaData();
        assertEquals(0, parameterMetaData1.getParameterCount());

        // Try to bind non-existent parameter.
        try {
            pstmt.setInt(1, 100);
            fail("expected exception");
        } catch (SQLException e) {
            // ok
        }

        // Execute again.
        CellSet cellSet4 = pstmt.executeQuery();
        assertIsClosed(cellSet4, false);
        assertIsClosed(cellSet3, true);
        assertEquals(0, cellSet4.getAxes().size());
        assertEquals(266773.0, cellSet4.getCell(0).getValue());

        // Re-execute with a parse tree.
        MdxParser mdxParser =
            olapConnection.getParserFactory().createMdxParser(olapConnection);
        SelectNode select =
            mdxParser.parseSelect(
                "select {[Gender]} on columns from [sales]\n" +
                    "where [Time].[1997].[Q4]");
        CellSet cellSet5 = pstmt.executeOlapQuery(select);
        TestContext.assertEqualsVerbose(
            TestContext.fold(
                "Axis #0:\n" +
                    "{[Time].[1997].[Q4]}\n" +
                    "Axis #1:\n" +
                    "{[Gender].[All Gender]}\n" +
                    "Row #0: 72,024\n"),
            TestContext.toString(cellSet5));

        // Execute.
        CellSet cellSet6 = pstmt.executeQuery();
        assertIsClosed(cellSet6, false);
        assertIsClosed(cellSet5, true);
        assertEquals(1, cellSet6.getAxes().size());
        assertEquals(72024.0, cellSet6.getCell(0).getDoubleValue());

        // Close prepared statement.
        assertIsClosed(pstmt, false);
        pstmt.close();
        assertIsClosed(pstmt, true);
        assertIsClosed(cellSet, true);
        assertIsClosed(cellSet2, true);
        assertIsClosed(cellSet6, true);

        // todo: test all of the PreparedOlapStatement.setXxx methods
        if (false) pstmt.getCube();
    }

    public void testCellSetMetaData() throws SQLException {
        // Metadata of prepared statement
        Connection connection = helper.createConnection();
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);

        final String mdx =
            "select {[Gender]} on columns from [sales]\n" +
            "where [Time].[1997].[Q4]";
        PreparedOlapStatement pstmt =
            olapConnection.prepareOlapStatement(mdx);
        final CellSetMetaData cellSetMetaData = pstmt.getMetaData();
        checkCellSetMetaData(cellSetMetaData);

        // Metadata of its cellset
        final CellSet cellSet = pstmt.executeQuery();
        checkCellSetMetaData(cellSet.getMetaData());

        // Metadata of regular statement executing string.
        final OlapStatement stmt = olapConnection.createStatement();
        final CellSet cellSet1 = stmt.executeOlapQuery(mdx);
        checkCellSetMetaData(cellSet1.getMetaData());

        // Metadata of regular statement executing parse tree.
        MdxParser mdxParser =
            olapConnection.getParserFactory().createMdxParser(olapConnection);
        SelectNode select = mdxParser.parseSelect(mdx);
        final OlapStatement stmt2 = olapConnection.createStatement();
        CellSet cellSet2 = stmt2.executeOlapQuery(select);
        checkCellSetMetaData(cellSet2.getMetaData());
    }

    private void checkCellSetMetaData(CellSetMetaData cellSetMetaData) {
        assertNotNull(cellSetMetaData);
        assertEquals(1, cellSetMetaData.getAxesMetaData().size());
        assertEquals("Sales", cellSetMetaData.getCube().getName());
    }

    public void testCellSet() throws SQLException {
        Connection connection = helper.createConnection();
        Statement statement = connection.createStatement();
        final OlapStatement olapStatement =
            ((OlapWrapper) statement).unwrap(OlapStatement.class);
        final CellSet cellSet =
            olapStatement.executeOlapQuery(
                "SELECT\n" +
                    " {[Measures].[Unit Sales],\n" +
                    "    [Measures].[Store Sales]} ON COLUMNS\n," +
                    " Crossjoin({[Gender].[M]}, [Product].Children) ON ROWS\n" +
                    "FROM [Sales]\n" +
                    "WHERE [Time].[1997].[Q2]");
        String s = TestContext.toString(cellSet);
        assertEquals(
            TestContext.fold("Axis #0:\n" +
                "{[Time].[1997].[Q2]}\n" +
                "Axis #1:\n" +
                "{[Measures].[Unit Sales]}\n" +
                "{[Measures].[Store Sales]}\n" +
                "Axis #2:\n" +
                "{[Gender].[All Gender].[M], [Product].[All Products].[Drink]}\n" +
                "{[Gender].[All Gender].[M], [Product].[All Products].[Food]}\n" +
                "{[Gender].[All Gender].[M], [Product].[All Products].[Non-Consumable]}\n" +
                "Row #0: 3,023\n" +
                "Row #0: 6,004.80\n" +
                "Row #1: 22,558\n" +
                "Row #1: 47,869.17\n" +
                "Row #2: 6,037\n" +
                "Row #2: 12,935.16\n"),
            s);
    }

    public void testCell() {
        // todo: test Cell methods

        /*
             public CellSet getCellSet() {
    public int getOrdinal() {
    public List<Integer> getCoordinateList() {
    public Object getPropertyValue(Property property) {
    public boolean isEmpty() {
    public boolean isError() {
    public boolean isNull() {
    public double getDoubleValue() throws OlapException {
    public String getErrorText() {
    public Object getValue() {
    public String getFormattedValue() {
    public ResultSet drillThrough() {
*/
        // in particular, create a result set with null, empty and error cells,
        // and make sure they look different

        // todo: test CellSetAxis methods
        /*
    public int getOrdinal() {
    public CellSet getCellSet() {
    public CellSetAxisMetaData getAxisMetaData() {
    public List<Position> getPositions() {
    public int getPositionCount() {
    public ListIterator<Position> iterate() {

    todo: test OlapResultAxisMetaData methods

    public org.olap4j.Axis getAxis() {
    public List<Hierarchy> getHierarchies() {
    public List<Property> getProperties() {
         */
    }

    public void testProperty() {
        // todo: submit a query with cell and dimension properties, and make
        // sure the properties appear in the result set
    }

    /**
     * Tests different scrolling characteristics.
     *
     * <p>In one mode, you request that you get all of the positions on an axis.
     * You can call {@link CellSetAxis#getPositions()} and
     *  {@link CellSetAxis#getPositions()}.
     *
     * <p>In another mode, you can iterate over the positions, calling
     * {@link CellSetAxis#iterate()}. Note that this method returns a
     * {@link java.util.ListIterator}, which has
     * {@link java.util.ListIterator#nextIndex()}. We could maybe extend this
     * interface further, to allow to jump forwards/backwards by N.
     *
     * <p>This test should check that queries work correctly when you ask
     * for axes as lists and iterators. If you open the query as a list,
     * you can get it as an iterator, but if you open it as an iterator, you
     * cannot get it as a list. (Or maybe you can, but it is expensive.)
     *
     * <p>The existing JDBC method {@link Connection#createStatement(int, int)},
     * where the 2nd parameter has values such as
     * {@link ResultSet#TYPE_FORWARD_ONLY} and
     * {@link ResultSet#TYPE_SCROLL_INSENSITIVE}, might be the way to invoke
     * those two modes.
     *
     * <p>Note that cell ordinals are not well-defined until axis lengths are
     * known. Test that cell ordinal property is not available if the statement
     * is opened in this mode.
     *
     * <p>It was proposed that there would be an API to get blocks of cell
     * values. Not in the spec yet.
     */
    public void testScrolling() {
        // todo: submit a query where you ask for different scrolling
        // characteristics. maybe the values
        int x = ResultSet.TYPE_SCROLL_INSENSITIVE;
        int y = ResultSet.TYPE_FORWARD_ONLY;
        // are how to ask for these characteristics. also need to document this
        // behavior in the API and the spec. in one mode,

    }

    /**
     * Tests creation of an MDX parser, and converting an MDX statement into
     * a parse tree.
     *
     * <p>Also create a set of negative tests. Do we give sensible errors if
     * the MDX is misformed.
     *
     * <p>Also have a set of validator tests, which check that the functions
     * used exist, are applied to arguments of the correct type, and members
     * exist.
     */
    public void testParsing() throws SQLException {

        // parse

        Connection connection = helper.createConnection();
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);
        MdxParser mdxParser =
            olapConnection.getParserFactory().createMdxParser(olapConnection);
        SelectNode select =
            mdxParser.parseSelect(
                "with member [Measures].[Foo] as ' [Measures].[Bar] ', FORMAT_STRING='xxx'\n" +
            " select {[Gender]} on columns, {[Store].Children} on rows\n" +
            "from [sales]\n" +
            "where [Time].[1997].[Q4]");

        // unparse
        checkUnparsedMdx(select);

        // test that get error if axes do not have unique names
        select =
            mdxParser.parseSelect(
                "select {[Gender]} on columns, {[Store].Children} on columns\n" +
                    "from [sales]");
        MdxValidator validator =
            olapConnection.getParserFactory().createMdxValidator(
                olapConnection);
        try {
            select = validator.validateSelect(select);
            fail("expected exception, got " + select);
        } catch (Exception e) {
            assertTrue(
                getStackTrace(e).indexOf("Duplicate axis name 'COLUMNS'.") 
                >= 0);
        }
    }

    private void checkUnparsedMdx(SelectNode select) {
        StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        ParseTreeWriter parseTreeWriter = new ParseTreeWriter(pw);
        select.unparse(parseTreeWriter);
        pw.flush();
        String mdx = sw.toString();
        TestContext.assertEqualsVerbose(
            TestContext.fold("WITH\n" +
                "MEMBER [Measures].[Foo] AS '[Measures].[Bar]', FORMAT_STRING = \"xxx\"\n" +
                "SELECT\n" +
                "{[Gender]} ON COLUMNS,\n" +
                "{[Store].Children} ON ROWS\n" +
                "FROM [sales]\n" +
                "WHERE [Time].[1997].[Q4]"),
            mdx);

    }

    /**
     * Tests creation of an MDX query from a parse tree. Build the parse tree
     * programmatically.
     */
    public void testUnparsing() {
        // Note that the select statement constructed here is equivalent
        // to the one in testParsing.
        SelectNode select = new SelectNode(
            null,
            new ArrayList<ParseTreeNode>(),
            new ArrayList<AxisNode>(),
            new IdentifierNode(new IdentifierNode.Segment("sales")),
            new AxisNode(
                null,
                false,
                null,
                Axis.SLICER,
                new ArrayList<IdentifierNode>()),
            new ArrayList<IdentifierNode>());
        select.getWithList().add(
            new WithMemberNode(
                null,
                new IdentifierNode(
                    new IdentifierNode.Segment("Measures"),
                    new IdentifierNode.Segment("Foo")),
                new IdentifierNode(
                    new IdentifierNode.Segment("Measures"),
                    new IdentifierNode.Segment("Bar")),
                Arrays.asList(
                    new PropertyValueNode [] {
                        new PropertyValueNode(
                            null,
                            "FORMAT_STRING",
                            LiteralNode.createString(
                                null,
                                "xxx"))
                    })));
        select.getAxisList().add(
            new AxisNode(
                null,
                false,
                new CallNode(
                    null,
                    "{}",
                    Syntax.Braces,
                    Arrays.asList(
                    new ParseTreeNode [] {
                        new IdentifierNode(
                            new IdentifierNode.Segment("Gender"))
                    })),
                Axis.COLUMNS,
                new ArrayList<IdentifierNode>()));
        select.getAxisList().add(
            new AxisNode(
                null,
                false,
                new CallNode(
                    null,
                    "{}",
                    Syntax.Braces,
                    new CallNode(
                        null,
                        "Children",
                        Syntax.Property,
                        new IdentifierNode(
                            new IdentifierNode.Segment("Store"))
                        )),
                Axis.ROWS,
                new ArrayList<IdentifierNode>()));
        select.getSlicerAxis().setExpression(
            new IdentifierNode(
                new IdentifierNode.Segment("Time"),
                new IdentifierNode.Segment("1997"),
                new IdentifierNode.Segment("Q4")));
        
        checkUnparsedMdx(select);
    }

    /**
     * Tests the {@link Cube#lookupMember(String[])} method.
     */
    public void testCubeLookupMember() throws Exception {
        Class.forName(helper.getDriverClassName());
        Connection connection = helper.createConnection();
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);
        Cube cube = olapConnection.getSchema().getCubes().get("Sales");

        Member member =
            cube.lookupMember(
                "Time", "1997", "Q2");
        assertEquals("[Time].[1997].[Q2]", member.getUniqueName());

        member =
            cube.lookupMember(
                "Time", "1997", "Q5");
        assertNull(member);

        // arguably this should return [Customers].[All Customers]; but it
        // makes a bit more sense for it to return null
        member =
            cube.lookupMember(
                "Customers");
        assertNull(member);

        member =
            cube.lookupMember(
                "Customers", "All Customers");
        assertTrue(member.isAll());
    }

    /**
     * Tests the {@link Cube#lookupMembers(java.util.Set, String[])} method.
     */
    public void testCubeLookupMembers() throws Exception {
        Class.forName(helper.getDriverClassName());
        Connection connection = helper.createConnection();
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);
        Cube cube = olapConnection.getSchema().getCubes().get("Sales");

        List<Member> memberList =
            cube.lookupMembers(
                EnumSet.of(Member.TreeOp.ANCESTORS, Member.TreeOp.CHILDREN),
                "Time", "1997", "Q2");
        assertEquals(TestContext.fold("[Time].[1997]\n"
            + "[Time].[1997].[Q2].[4]\n"
            + "[Time].[1997].[Q2].[5]\n"
            + "[Time].[1997].[Q2].[6]\n"),
            memberListToString(memberList));

        // ask for non-existent member; list should be empty
        memberList =
            cube.lookupMembers(
                EnumSet.of(Member.TreeOp.ANCESTORS, Member.TreeOp.CHILDREN),
                "Time", "1997", "Q5");
        assertTrue(memberList.isEmpty());

        // ask for parent & ancestors; should not get duplicates
        memberList =
            cube.lookupMembers(
                EnumSet.of(Member.TreeOp.ANCESTORS, Member.TreeOp.PARENT),
                "Time", "1997", "Q2");
        assertEquals(
            TestContext.fold("[Time].[1997]\n"),
            memberListToString(memberList));

        // ask for parent of root member, should not get null member in list
        memberList =
            cube.lookupMembers(
                EnumSet.of(Member.TreeOp.ANCESTORS, Member.TreeOp.PARENT),
                "Product");
        assertTrue(memberList.isEmpty());

        // ask for siblings and children, and the results should be
        // hierarchically ordered (as always)
        memberList =
            cube.lookupMembers(
                EnumSet.of(Member.TreeOp.SIBLINGS, Member.TreeOp.CHILDREN),
                "Time", "1997", "Q2");
        assertEquals(
            TestContext.fold("[Time].[1997].[Q1]\n"
                + "[Time].[1997].[Q2].[4]\n"
                + "[Time].[1997].[Q2].[5]\n"
                + "[Time].[1997].[Q2].[6]\n"
                + "[Time].[1997].[Q3]\n"
                + "[Time].[1997].[Q4]\n"),
            memberListToString(memberList));

        // siblings of the root member - potentially tricky
        memberList =
            cube.lookupMembers(
                EnumSet.of(Member.TreeOp.SIBLINGS),
                "Time", "1997");
        assertEquals(
            TestContext.fold("[Time].[1998]\n"),
            memberListToString(memberList));

        memberList =
            cube.lookupMembers(
                EnumSet.of(Member.TreeOp.SIBLINGS, Member.TreeOp.SELF),
                "Customers", "USA", "OR");
        assertEquals(
            TestContext.fold("[Customers].[All Customers].[USA].[CA]\n"
                + "[Customers].[All Customers].[USA].[OR]\n"
                + "[Customers].[All Customers].[USA].[WA]\n"),
            memberListToString(memberList));        
    }

    /**
     * Tests metadata browsing.
     */
    public void testMetadata() throws Exception {
        Class.forName(helper.getDriverClassName());
        Connection connection = helper.createConnection();
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);
        Cube cube = olapConnection.getSchema().getCubes().get("Sales");

        for (Dimension dimension : cube.getDimensions()) {
            // Call every method of Dimension
            assertNotNull(dimension.getCaption(Locale.getDefault()));
            dimension.getDescription(Locale.getDefault());
            assertNotNull(dimension.getDefaultHierarchy());
            assertEquals(
                dimension.getName().equals("Time")
                    ? Dimension.Type.TIME 
                    : Dimension.Type.OTHER,
                dimension.getDimensionType());
            assertNotNull(dimension.getName());
            assertNotNull(dimension.getUniqueName());

            for (Hierarchy hierarchy : dimension.getHierarchies()) {
                // Call every method of Hierarchy
                final NamedList<Member> rootMemberList =
                    hierarchy.getRootMembers();
                if (hierarchy.hasAll()) {
                    assertEquals(1, rootMemberList.size());
                }
                for (Member rootMember : rootMemberList) {
                    assertNull(rootMember.getParentMember());
                }
                assertNotNull(hierarchy.getDefaultMember());
                assertNotNull(hierarchy.getName());
                assertNotNull(hierarchy.getUniqueName());
                hierarchy.getDescription(Locale.getDefault());
                assertNotNull(hierarchy.getCaption(Locale.getDefault()));
                assertEquals(dimension, hierarchy.getDimension());

                for (Level level : hierarchy.getLevels()) {
                    int k = 0;
                    for (Member member : level.getMembers()) {
                        assertNotNull(member.getName());
                        if (++k > 3) {
                            break;
                        }
                    }
                }
            }
        }

        cube = olapConnection.getSchema().getCubes().get("Warehouse");
        int count = 0;
        for (NamedSet namedSet : cube.getSets()) {
            ++count;
            assertNotNull(namedSet.getName());
            assertNotNull(namedSet.getUniqueName());
            assertNotNull(namedSet.getCaption(Locale.getDefault()));
            namedSet.getDescription(Locale.getDefault());
            assertTrue(namedSet.getExpression().getType() instanceof SetType);
        }
        assertTrue(count > 0);

        // ~ Member

        Member member = cube.lookupMember("Product", "Food", "Marshmallows");
        assertNull(member); // we don't sell marshmallows!
        member = cube.lookupMember("Product", "Food");
        assertNotNull(member);
        Member member2 = cube.lookupMember("Product", "All Products", "Food");
        assertEquals(member, member2);
        assertEquals("[Product].[All Products].[Food]", 
            member.getUniqueName());
        assertEquals("Food", member.getName());
        assertEquals("[Product].[Product Family]",
            member.getLevel().getUniqueName());
        assertEquals(Member.Type.REGULAR, member.getMemberType());
        // mondrian does not set ordinals correctly
        assertEquals(-1, member.getOrdinal());
        final NamedList<Property> propertyList = member.getProperties();
        assertEquals(22, propertyList.size());
        final Property property = propertyList.get("MEMBER_CAPTION");
        assertEquals("Food", member.getPropertyFormattedValue(property));
        assertEquals("Food", member.getPropertyValue(property));
        assertFalse(member.isAll());

        // All member
        final Member allProductsMember = member.getParentMember();
        assertEquals("[Product].[All Products]",
            allProductsMember.getUniqueName());
        assertEquals("(All)", allProductsMember.getLevel().getName());
        assertEquals("[Product].[(All)]", allProductsMember.getLevel().getUniqueName());
        assertEquals(1, allProductsMember.getLevel().getMembers().size());
        assertTrue(allProductsMember.isAll());
        assertNull(allProductsMember.getParentMember());

        // ~ Property

        assertEquals("MEMBER_CAPTION", property.getName());
        assertEquals("MEMBER_CAPTION", property.getUniqueName());
        assertEquals(Property.Scope.MEMBER, property.getScope());
        assertEquals(Property.Datatype.TYPE_STRING, property.getDatatype());
    }

    /**
     * Tests the type-derivation for
     * {@link org.olap4j.mdx.SelectNode#getFrom()} and the {@link CubeType}
     * class.
     *
     * @throws Throwable on error
     */
    public void testCubeType() throws Throwable {
        Class.forName(helper.getDriverClassName());
        Connection connection = helper.createConnection();
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);

        final MdxParserFactory parserFactory =
            olapConnection.getParserFactory();
        MdxParser mdxParser =
            parserFactory.createMdxParser(olapConnection);
        MdxValidator mdxValidator =
            parserFactory.createMdxValidator(olapConnection);

        SelectNode select =
            mdxParser.parseSelect(
                "select {[Gender]} on columns from [sales]\n" +
                    "where [Time].[1997].[Q4]");

        // CubeType

        // Before validation, we cannot ask for type
        try {
            final ParseTreeNode from = select.getFrom();
            assertTrue(from instanceof IdentifierNode);
            Type type = from.getType();
            fail("expected error");
        } catch (UnsupportedOperationException e) {
            // ignore
        }

        select = mdxValidator.validateSelect(select);
        CubeType cubeType = (CubeType) select.getFrom().getType();
        assertEquals("Sales", cubeType.getCube().getName());
        assertNull(cubeType.getDimension());
        assertNull(cubeType.getHierarchy());
        assertNull(cubeType.getLevel());

        // Different query based on same cube should have equal CubeType
        select =
            mdxParser.parseSelect(
                "select from [sales]");
        select = mdxValidator.validateSelect(select);
        assertEquals(cubeType, select.getFrom().getType());

        // Different query based on different cube should have different
        // CubeType
        select =
            mdxParser.parseSelect(
                "select from [warehouse and sales]");
        select = mdxValidator.validateSelect(select);
        assertNotSame(cubeType, select.getFrom().getType());
    }

    /**
     * Tests the type-derivation for query axes 
     * ({@link org.olap4j.mdx.SelectNode#getAxisList()} and
     * {@link org.olap4j.mdx.SelectNode#getSlicerAxis()}), and the
     * {@link org.olap4j.type.SetType},
     * {@link org.olap4j.type.TupleType},
     * {@link org.olap4j.type.MemberType} type subclasses.
     *
     * @throws Throwable on error
     */
    public void testAxisType() throws Throwable {
        Class.forName(helper.getDriverClassName());

        // connect using properties and no username/password
        Connection connection = helper.createConnection();
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);

        final MdxParserFactory parserFactory =
            olapConnection.getParserFactory();
        MdxParser mdxParser =
            parserFactory.createMdxParser(olapConnection);
        MdxValidator mdxValidator =
            parserFactory.createMdxValidator(olapConnection);

        SelectNode select =
            mdxParser.parseSelect(
                "select ([Gender], [Store]) on columns\n,"
                    + "{[Customers].[City].Members} on rows\n"
                    + "from [sales]\n" +
                    "where ([Time].[1997].[Q4], [Marital Status].[S])");
        select = mdxValidator.validateSelect(select);

        // a query is not an expression, so does not have a type
        assertNull(select.getType());

        final AxisNode columnsAxis = select.getAxisList().get(0);
        // an axis is not an expression, so does not have a type
        assertNull(columnsAxis.getType());

        // ~ SetType

        final SetType setType = (SetType) columnsAxis.getExpression().getType();
        assertNull(setType.getDimension());
        assertNull(setType.getHierarchy());
        assertNull(setType.getLevel());
        assertNotNull(setType.toString());

        final Type elementType = setType.getElementType();

        // ~ TupleType

        assertTrue(elementType instanceof TupleType);
        TupleType tupleType = (TupleType) elementType;
        assertNotNull(tupleType.toString());
        assertNull(tupleType.getDimension());
        assertNull(tupleType.getHierarchy());
        assertNull(tupleType.getLevel());
        final Cube cube = ((CubeType) select.getFrom().getType()).getCube();
        final Dimension storeDimension = cube.getDimensions().get("Store");
        final Dimension genderDimension = cube.getDimensions().get("Gender");
        final Dimension measuresDimension = cube.getDimensions().get("Measures");
        final Dimension customersDimension = cube.getDimensions().get("Customers");
        assertTrue(tupleType.usesDimension(storeDimension, false));
        assertTrue(tupleType.usesDimension(genderDimension, false));
        assertFalse(tupleType.usesDimension(measuresDimension, false));

        // Other axis is a set of members

        // ~ MemberType
        final AxisNode rowsAxis = select.getAxisList().get(1);
        final Type rowsType = rowsAxis.getExpression().getType();
        assertTrue(rowsType instanceof SetType);
        MemberType memberType = (MemberType) ((SetType) rowsType).getElementType();
        assertNotNull(memberType.toString());
        // MemberType.getMember is null because we know it belongs to the City
        // level, but no particular member of that level.
        assertNull("Customers", memberType.getMember());
        assertEquals("City", memberType.getLevel().getName());
        assertEquals("Customers", memberType.getHierarchy().getName());
        assertEquals("Customers", memberType.getDimension().getName());
        assertFalse(memberType.usesDimension(storeDimension, false));
        assertTrue(memberType.usesDimension(customersDimension, false));
        assertTrue(memberType.usesDimension(customersDimension, true));
        
        // Slicer

        final AxisNode slicerAxis = select.getSlicerAxis();
        assertNull(slicerAxis.getType());
        final Type slicerType = slicerAxis.getExpression().getType();
        assertTrue(slicerType instanceof TupleType);
        assertEquals(
            "TupleType<MemberType<member=[Time].[1997].[Q4]>, MemberType<member=[Marital Status].[All Marital Status].[S]>>",
            slicerType.toString());
    }

    // TODO: test for HierarchyType
    // TODO: test for DimensionType
    // TODO: test for LevelType

    /**
     * Converts a {@link Throwable} to a stack trace.
     */
    static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * Converts a list of members to a string, one per line.
     */
    static String memberListToString(List<Member> list) {
        final StringBuilder buf = new StringBuilder();
        for (Member member : list) {
            buf.append(member.getUniqueName()).append(TestContext.NL);
        }
        return buf.toString();
    }

    /**
     * Abstracts the information about specific drivers and database instances
     * needed by this test. This allows the same test suite to be used for
     * multiple implementations of olap4j.
     */
    interface Helper {
        Connection createConnection() throws SQLException;

        String getDriverUrlPrefix();

        String getDriverClassName();

        Connection createConnectionWithUserPassword() throws SQLException;

        String getURL();

        boolean isMondrian();
    }

    /**
     * Implementation of {@link Helper} which speaks to the mondrian olap4j
     * driver.
     */
    public static class MondrianHelper implements Helper {

        public Connection createConnection() throws SQLException {
            try {
                Class.forName(DRIVER_CLASS_NAME);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("oops", e);
            }
            return
                DriverManager.getConnection(
                    getURL(),
                    new Properties());
        }

        public Connection createConnectionWithUserPassword() throws SQLException {
            return DriverManager.getConnection(
                getURL(), USER, PASSWORD);
        }

        public String getDriverUrlPrefix() {
            return DRIVER_URL_PREFIX;
        }

        public String getDriverClassName() {
            return DRIVER_CLASS_NAME;
        }

        public String getURL() {
            return getDefaultConnectString();
        }

        public boolean isMondrian() {
            return true;
        }

        public static String getDefaultConnectString() {
            if (false) {
                return "jdbc:mondrian:Jdbc='jdbc:derby:/home/jvs/open/mondrian/demo/derby/foodmart';JdbcUser=sa;JdbcPassword=sa;Catalog='file:///home/jvs/open/mondrian/demo/FoodMart.xml';JdbcDrivers=org.apache.derby.jdbc.EmbeddedDriver;";
            }
            if (true) {
                return "jdbc:mondrian:Jdbc='jdbc:odbc:MondrianFoodMart';Catalog='file://c:/open/mondrian/demo/FoodMart.xml';JdbcDrivers=sun.jdbc.odbc.JdbcOdbcDriver;";
            } else {
                return "jdbc:mondrian:Jdbc=jdbc:oracle:thin:foodmart/foodmart@//marmalade.hydromatic.net:1521/XE;JdbcUser=foodmart;JdbcPassword=foodmart;Catalog=../mondrian/demo/FoodMart.xml;JdbcDrivers=oracle.jdbc.OracleDriver;";
            }
        }

        public static final String DRIVER_CLASS_NAME = "mondrian.olap4j.MondrianOlap4jDriver";

        public static final String DRIVER_URL_PREFIX = "jdbc:mondrian:";
        private static final String USER = "sa";
        private static final String PASSWORD = "sa";
    }

    /**
     * Implementation of {@link Helper} which speaks to the XML/A olap4j
     * driver.
     */
    public static class XmlaHelper implements Helper {
        XmlaOlap4jDriver.Proxy proxy =
            new MondrianInprocProxy();

        public Connection createConnection() throws SQLException {
            try {
                Class.forName(DRIVER_CLASS_NAME);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("oops", e);
            }
            try {
                XmlaOlap4jDriver.THREAD_PROXY.set(proxy);
                Properties info = new Properties();
                info.setProperty("UseThreadProxy", "true");
                return
                    DriverManager.getConnection(
                        getURL(),
                        info);
            } finally {
                XmlaOlap4jDriver.THREAD_PROXY.set(null);
            }
        }

        public Connection createConnectionWithUserPassword() throws SQLException {
            try {
                Class.forName(DRIVER_CLASS_NAME);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("oops", e);
            }
            try {
                XmlaOlap4jDriver.THREAD_PROXY.set(proxy);
                Properties info = new Properties();
                info.setProperty("UseThreadProxy", "true");
                return DriverManager.getConnection(
                    getURL(), USER, PASSWORD);
            } finally {
                XmlaOlap4jDriver.THREAD_PROXY.set(null);
            }
        }

        public String getDriverUrlPrefix() {
            return DRIVER_URL_PREFIX;
        }

        public String getDriverClassName() {
            return DRIVER_CLASS_NAME;
        }

        public String getURL() {
            return "jdbc:xmla:Server=http://foo;UseThreadProxy=true";
        }

        public boolean isMondrian() {
            return false;
        }

        public static final String DRIVER_CLASS_NAME =
             "org.olap4j.driver.xmla.XmlaOlap4jDriver";

        public static final String DRIVER_URL_PREFIX = "jdbc:xmla:";
        private static final String USER = "user";
        private static final String PASSWORD = "password";

        /**
         * Proxy which implements XMLA requests by talking to mondrian
         * in-process. This is more convenient to debug than an inter-process
         * request using HTTP.
         */
        private static class MondrianInprocProxy implements XmlaOlap4jDriver.Proxy {
            public InputStream get(URL url, String request) throws IOException {
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    String urlString = url.toString();
                    byte[] bytes = XmlaSupport.processSoapXmla(
                        request, urlString, map, null);
                    return new ByteArrayInputStream(bytes);
                } catch (ServletException e) {
                    throw new RuntimeException(
                        "Error while reading '" + url + "'", e);
                } catch (SAXException e) {
                    throw new RuntimeException(
                        "Error while reading '" + url + "'", e);
                }
            }
        }
    }
}

// End ConnectionTest.java
