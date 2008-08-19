/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.mdx.*;
import org.olap4j.mdx.parser.*;
import org.olap4j.metadata.*;
import org.olap4j.test.TestContext;
import org.olap4j.type.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Unit test for olap4j Driver and Connection classes.
 *
 * <p>The system property "org.olap4j.test.helperClassName" determines the
 * name of the helper class. By default, uses {@link MondrianTester}, which
 * runs against mondrian; {@link XmlaTester} is also available.
 *
 * @author jhyde
 * @version $Id$
 */
public class ConnectionTest extends TestCase {
    private final TestContext.Tester tester = TestContext.instance().getTester();

    private static final boolean IS_JDK_16 =
        System.getProperty("java.version").startsWith("1.6.");

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
     * Driver basics.
     */
    public void testDriver() throws ClassNotFoundException, SQLException {
        Class clazz = Class.forName(tester.getDriverClassName());
        assertNotNull(clazz);
        assertTrue(Driver.class.isAssignableFrom(clazz));

        // driver should have automatically registered itself
        Driver driver = DriverManager.getDriver(tester.getDriverUrlPrefix());
        assertNotNull(driver);

        // deregister driver
        DriverManager.deregisterDriver(driver);
        try {
            Driver driver2 = DriverManager.getDriver(tester.getDriverUrlPrefix());
            fail("expected error, got " + driver2);
        } catch (SQLException e) {
            assertEquals("No suitable driver", e.getMessage());
        }

        // register explicitly
        DriverManager.registerDriver(driver);
        Driver driver3 = DriverManager.getDriver(tester.getDriverUrlPrefix());
        assertNotNull(driver3);

        // test properties
        int majorVersion = driver.getMajorVersion();
        int minorVersion = driver.getMinorVersion();
        assertTrue(majorVersion >= 0);
        assertTrue(minorVersion >= 0);
        assertTrue(majorVersion > 0 || minorVersion > 0);

        // check that the getPropertyInfo method returns something sensible.
        // We can't test individual properties in this non-driver-specific test.
        DriverPropertyInfo[] driverPropertyInfos =
            driver.getPropertyInfo(
                tester.getDriverUrlPrefix(),
                new Properties());
        switch (tester.getFlavor()) {
        case XMLA:
            break;
        default:
            assertTrue(driverPropertyInfos.length > 0);
        }
    }

    void assertIsValid(Connection connection, int timeout) {
        if (!IS_JDK_16) {
            return;
        }
        // We would like to evaluate
        //    assertTrue(connection.isValid(0));
        // but this code would not compile on JDK 1.5 or lower. So, we invoke
        // the same code by reflection.
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
            if (e.getTargetException() instanceof AbstractMethodError) {
                // This happens in commons-dbcp. Somehow the method exists in
                // the connection class, but it fails later. Not the fault of
                // olap4j or the olapj driver, so ignore the error.
                Olap4jUtil.discard(e);
            } else {
                throw new RuntimeException(e);
            }
        } catch (AbstractMethodError e) {
            // This happens in commons-dbcp. Somehow the method exists in
            // the connection class, but it fails later. Not the fault of
            // olap4j or the olapj driver, so ignore the error.
            Olap4jUtil.discard(e);
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
        if (tester.getWrapper() == TestContext.Wrapper.DBCP) {
            // commons-dbcp 1.1 doesn't support isClosed
            return;
        }
        //  assertTrue(statment.isClosed());
        try {
            Class clazz;
            if (o instanceof Statement) {
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
        Class.forName(tester.getDriverClassName());

        // connect using properties and no username/password
        connection = tester.createConnection();
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
        switch (tester.getWrapper()) {
        case DBCP:
            // DBCP complains if you close a connection twice. Even though the
            // JDBC spec is clear that it is OK.
            break;
        default:
            connection.close();
            break;
        }

        switch (tester.getFlavor()) {
        case MONDRIAN:
            // connect using username/password
            connection = tester.createConnectionWithUserPassword();
            assertNotNull(connection);

            connection.close();
            assertTrue(connection.isClosed());

            // connect with URL only
            connection = DriverManager.getConnection(tester.getURL());
            assertNotNull(connection);

            connection.close();
            break;

        case XMLA:
            // in-process XMLA test does not support username/password
            break;
        }
        assertTrue(connection.isClosed());
    }

    public void testConnectionUnwrap() throws SQLException {
        // commons-dbcp 1.1 doesn't do wrapping very well
        switch (tester.getWrapper()) {
        case DBCP:
            return;
        }
        connection = tester.createConnection();

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

        // Unwrap and get locale
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        final Locale locale = olapConnection.getLocale();
        assertEquals(locale, Locale.getDefault());

        // Set locale to something else.
        olapConnection.setLocale(Locale.CANADA_FRENCH);
        assertEquals(olapConnection.getLocale(), Locale.CANADA_FRENCH);

        // Try to set locale to null, should get error.
        try {
            olapConnection.setLocale(null);
            fail("expected exception");
        } catch (IllegalArgumentException e) {
            // Set if back
            olapConnection.setLocale(Locale.getDefault());
        }

        // Unwrap the mondrian connection.
        switch (tester.getFlavor()) {
        case MONDRIAN:
            // mondrian.olap.Connection does not extend java.sql.Connection
            // but we should be able to unwrap it regardless
            final Class<?> mondrianConnectionClass;
            try {
                mondrianConnectionClass =
                    Class.forName("mondrian.olap.Connection");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            final Object mondrianConnection =
                ((OlapWrapper) connection).unwrap(
                    mondrianConnectionClass);
            assertNotNull(mondrianConnection);
            assert mondrianConnectionClass.isInstance(mondrianConnection);
        }
    }

    public void testStatement() throws SQLException {
        connection = tester.createConnection();
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
            tester.getWrapper().unwrap(statement, OlapStatement.class);
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

    public void testAxes() throws SQLException {
        connection = tester.createConnection();
        Statement statement = connection.createStatement();

        OlapStatement olapStatement =
            tester.getWrapper().unwrap(statement, OlapStatement.class);

        CellSet cellSet =
            olapStatement.executeOlapQuery(
                "SELECT {[Measures].[Unit Sales]} on 0,\n"
                    + "{[Store].Children} on 1\n"
                    + "FROM [Sales]");
        List<CellSetAxis> axesList = cellSet.getAxes();
        assertEquals(2, axesList.size());
        final Member rowsMember =
            axesList.get(0).getPositions().get(0).getMembers().get(0);
        assertTrue(
            rowsMember.getUniqueName(),
            rowsMember instanceof Measure);
        final Member columnsMember =
            axesList.get(1).getPositions().get(0).getMembers().get(0);
        assertTrue(
            columnsMember.getUniqueName(),
            !(columnsMember instanceof Measure));
    }

    public void testInvalidStatement() throws SQLException {
        connection = tester.createConnection();
        Statement statement = connection.createStatement();
        OlapStatement olapStatement =
            tester.getWrapper().unwrap(statement, OlapStatement.class);

        // Execute a query with a syntax error.
        try {
            CellSet cellSet =
                olapStatement.executeOlapQuery(
                    "SELECT an error FROM [Sales]");
            fail("expected error, got " + cellSet);
        } catch (OlapException e) {
            switch (tester.getFlavor()) {
            case XMLA:
                assertTrue(e.getMessage().indexOf(
                    "XMLA MDX parse failed") >= 0);
                break;
            default:
                assertTrue(
                    TestContext.getStackTrace(e).indexOf(
                        "Failed to parse query") >= 0);
                break;
            }
        }
        // Error does not cause statement to become closed.
        assertIsClosed(olapStatement, false);
        olapStatement.close();
        connection.close();
    }

    private enum Method {
        ClassName,
        Mode,
        Type,
        TypeName,
        OlapType
    }

    public void testPreparedStatement() throws SQLException {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
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

        // XMLA driver does not support parameters yet.
        switch (tester.getFlavor()) {
        case XMLA:
            assertEquals(0, paramCount);
            return;
        }

        // Mondrian driver supports parameters.
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
                "{[Measures].[Unit Sales], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time].[1997], [Product].[All Products], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender].[M], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n" +
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
                    "{[Measures].[Unit Sales], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time].[1997], [Product].[All Products], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender].[M], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n" +
                    "Axis #1:\n" +
                    "{[Store].[All Stores].[USA].[CA]}\n" +
                    "{[Store].[All Stores].[USA].[CA].[San Francisco].[Store 14]}\n" +
                    "Row #0: 37,989\n" +
                    "Row #0: 1,053\n"),
            s);

        // Re-execute with a new MDX string.
        CellSet cellSet3 = pstmt.executeOlapQuery("SELECT FROM [Sales] WHERE [Time.Weekly].[1997].[3]");
        TestContext.assertEqualsVerbose(
            TestContext.fold("Axis #0:\n" +
                "{[Measures].[Unit Sales], [Store].[All Stores], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time.Weekly].[All Time.Weeklys].[1997].[3], [Product].[All Products], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n" +
                "9,518"),
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
        assertEquals(9518.0, cellSet4.getCell(0).getValue());

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
                    "{[Measures].[Unit Sales], [Store].[All Stores], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time].[1997].[Q4], [Product].[All Products], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n" +
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
        if (false) {
            pstmt.getCube();
        }
    }

    public void testCellSetMetaData() throws SQLException {
        // Metadata of prepared statement
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);

        checkCellSetMetaData1(
            olapConnection,
            "select {[Gender]} on columns from [sales]\n" +
            "where [Time].[1997].[Q4]");

        // now a query with no explicit slicer
        checkCellSetMetaData1(
            olapConnection,
            "select {[Gender]} on columns from [sales]");
    }

    private void checkCellSetMetaData1(
        OlapConnection olapConnection, String mdx) throws SQLException
    {
        PreparedOlapStatement pstmt =
            olapConnection.prepareOlapStatement(mdx);
        final CellSetMetaData cellSetMetaData = pstmt.getMetaData();
        checkCellSetMetaData(cellSetMetaData, 1, null);

        // Metadata of its cellset
        final CellSet cellSet = pstmt.executeQuery();
        checkCellSetMetaData(cellSet.getMetaData(), 1, cellSet);

        // Metadata of regular statement executing string.
        final OlapStatement stmt = olapConnection.createStatement();
        final CellSet cellSet1 = stmt.executeOlapQuery(mdx);
        checkCellSetMetaData(cellSet1.getMetaData(), 1, cellSet1);

        // Metadata of regular statement executing parse tree.
        MdxParser mdxParser =
            olapConnection.getParserFactory().createMdxParser(olapConnection);
        SelectNode select = mdxParser.parseSelect(mdx);
        final OlapStatement stmt2 = olapConnection.createStatement();
        CellSet cellSet2 = stmt2.executeOlapQuery(select);
        checkCellSetMetaData(cellSet2.getMetaData(), 1, cellSet2);
    }

    private void checkCellSetMetaData(
        CellSetMetaData cellSetMetaData,
        int axesCount,
        CellSet cellSet) throws OlapException
    {
        assertNotNull(cellSetMetaData);
        assertEquals(axesCount, cellSetMetaData.getAxesMetaData().size());
        assertEquals("Sales", cellSetMetaData.getCube().getName());

        int k = -1;
        final Set<Dimension> unseenDimensions =
            new HashSet<Dimension>(cellSetMetaData.getCube().getDimensions());
        for (CellSetAxisMetaData axisMetaData
            : cellSetMetaData.getAxesMetaData())
        {
            ++k;
            assertEquals(Axis.forOrdinal(k), axisMetaData.getAxisOrdinal());
            assertEquals(k, axisMetaData.getAxisOrdinal().axisOrdinal());
            assertTrue(axisMetaData.getHierarchies().size() > 0);
            for (Hierarchy hierarchy : axisMetaData.getHierarchies()) {
                unseenDimensions.remove(hierarchy.getDimension());
            }
            assertTrue(axisMetaData.getProperties().size() == 0);
            if (cellSet != null) {
                final CellSetAxisMetaData cellSetAxisMetaData =
                    cellSet.getAxes().get(k).getAxisMetaData();
                assertEquals(cellSetAxisMetaData, axisMetaData);
            }
        }

        CellSetAxisMetaData axisMetaData =
            cellSetMetaData.getFilterAxisMetaData();
        assertNotNull(axisMetaData);
        assertEquals(Axis.FILTER, axisMetaData.getAxisOrdinal());
        assertTrue(axisMetaData.getHierarchies().size() >= 0);
        final Set<Hierarchy> unseenHierarchies = new HashSet<Hierarchy>();
        for (Dimension unseenDimension : unseenDimensions) {
            unseenHierarchies.add(unseenDimension.getDefaultHierarchy());
        }
        assertEquals(
            new HashSet<Hierarchy>(axisMetaData.getHierarchies()),
            unseenHierarchies);
        assertTrue(axisMetaData.getProperties().size() == 0);
        if (cellSet != null) {
            assertEquals(
                cellSet.getFilterAxis().getAxisMetaData(), axisMetaData);
        }
    }

    /**
     * Tests the {@link CellSetAxisMetaData} class, based on an example
     * in the javadoc of same class.
     *
     * @throws Exception on error
     */
    public void testCellSetAxisMetaData() throws Exception {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        final String mdx = "SELECT\n"
            + "   {[Measures].Members} ON COLUMNS,\n"
            + "   CrossJoin([Store].Members, [Gender].Children)\n"
            + "   DIMENSION PROPERTIES\n"
            + "      MEMBER_ORDINAL,\n"
            + "      MEMBER_UNIQUE_NAME,\n"
            + "      DISPLAY_INFO ON ROWS\n"
            + " FROM [Sales]";

        // first via prepared statement
        final PreparedOlapStatement preparedStmt =
            olapConnection.prepareOlapStatement(mdx);
        final CellSetMetaData cellSetMetaData = preparedStmt.getMetaData();
        checkAxisMetaData(cellSetMetaData.getAxesMetaData().get(1));

        // second via directly executed statement
        OlapStatement olapStatement = olapConnection.createStatement();
        final CellSet cellSet =
            olapStatement.executeOlapQuery(mdx);
        checkAxisMetaData(cellSet.getAxes().get(1).getAxisMetaData());

        // third via metadata of direct statement
        checkAxisMetaData(cellSet.getMetaData().getAxesMetaData().get(1));
    }

    private void checkAxisMetaData(CellSetAxisMetaData cellSetAxisMetaData) {
        final List<Hierarchy> hierarchies =
            cellSetAxisMetaData.getHierarchies();
        assertEquals(2, hierarchies.size());
        assertEquals("Store", hierarchies.get(0).getName());
        assertEquals("Gender", hierarchies.get(1).getName());
        final List<Property> properties = cellSetAxisMetaData.getProperties();
        switch (tester.getFlavor()) {
        case MONDRIAN:
            // todo: fix mondrian driver. If there are 3 properties and 2
            // hierarchies, that's 6 properties total
            assertEquals(3, properties.size());
            break;
        default:
            assertEquals(6, properties.size());
            break;
        }
        assertEquals("MEMBER_ORDINAL", properties.get(0).getName());
        assertEquals("MEMBER_UNIQUE_NAME", properties.get(1).getName());
        assertEquals("DISPLAY_INFO", properties.get(2).getName());
    }

    public void testCellSet() throws SQLException {
        connection = tester.createConnection();
        Statement statement = connection.createStatement();
        final OlapStatement olapStatement =
            tester.getWrapper().unwrap(statement, OlapStatement.class);
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
                "{[Store].[All Stores],"
                + " [Store Size in SQFT].[All Store Size in SQFTs],"
                + " [Store Type].[All Store Types],"
                + " [Time].[1997].[Q2],"
                + " [Promotion Media].[All Media],"
                + " [Promotions].[All Promotions],"
                + " [Customers].[All Customers],"
                + " [Education Level].[All Education Levels],"
                + " [Marital Status].[All Marital Status],"
                + " [Yearly Income].[All Yearly Incomes]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Unit Sales]}\n"
                + "{[Measures].[Store Sales]}\n"
                + "Axis #2:\n"
                + "{[Gender].[All Gender].[M], [Product].[All Products].[Drink]}\n"
                + "{[Gender].[All Gender].[M], [Product].[All Products].[Food]}\n"
                + "{[Gender].[All Gender].[M], [Product].[All Products].[Non-Consumable]}\n"
                + "Row #0: 3,023\n"
                + "Row #0: 6,004.80\n"
                + "Row #1: 22,558\n"
                + "Row #1: 47,869.17\n"
                + "Row #2: 6,037\n"
                + "Row #2: 12,935.16\n"),
            s);
    }

    public void testCell() throws Exception {
        connection = tester.createConnection();
        Statement statement = connection.createStatement();
        final OlapStatement olapStatement =
            tester.getWrapper().unwrap(statement, OlapStatement.class);
        CellSet cellSet =
            olapStatement.executeOlapQuery(
                "SELECT\n" +
                    " {[Measures].[Unit Sales],\n" +
                    "    [Measures].[Store Sales]} ON COLUMNS\n," +
                    " Crossjoin({[Gender].[M]}, [Product].Children) ON ROWS\n" +
                    "FROM [Sales]\n" +
                    "WHERE [Time].[1997].[Q2]");

        // cell column#1, row#2
        // cellOrdinal = colOrdinal + rowOrdinal * columnCount
        //     = 1 + 2 * 2
        //     = 5

        // access method 1
        Cell cell = cellSet.getCell(5);
        assertEquals(5, cell.getOrdinal());
        if (tester.getFlavor() != TestContext.Tester.Flavor.XMLA) // FIXME
        assertEquals(12935.16, cell.getValue());
        assertEquals(12935.16, cell.getDoubleValue());
        assertEquals("12,935.16", cell.getFormattedValue());
        assertEquals(cellSet, cell.getCellSet());

        // access method 2
        cell = cellSet.getCell(Arrays.asList(1, 2));
        assertEquals(5, cell.getOrdinal());

        // access method 3
        cell = cellSet.getCell(
            cellSet.getAxes().get(0).getPositions().get(1),
            cellSet.getAxes().get(1).getPositions().get(2));
        assertEquals(5, cell.getOrdinal());

        assertEquals(Arrays.asList(1, 2), cell.getCoordinateList());
        assertEquals("#,###.00",
            cell.getPropertyValue(Property.StandardCellProperty.FORMAT_STRING));
        assertFalse(cell.isEmpty());
        assertFalse(cell.isError());
        assertFalse(cell.isNull());
        assertNull(cell.getErrorText());

        switch (tester.getFlavor()) {
        case XMLA:
            // TODO: implement drill-through in XMLA driver
            break;
        default:
            final ResultSet resultSet = cell.drillThrough();
            final ResultSetMetaData metaData = resultSet.getMetaData();
            // Most databases return 5 columns. Derby returns 9 because of
            // 4 columns in the ORDER BY clause.
            assertTrue(metaData.getColumnCount() >= 5);
            assertEquals("Year", metaData.getColumnLabel(1));
            assertEquals("Store Sales", metaData.getColumnLabel(5));
            resultSet.close();
            break;
        }

        // cell out of range using getCell(int)
        try {
            Cell cell2 = cellSet.getCell(-5);
            fail("expected exception, got " + cell2);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }

        // cell out of range using getCell(int)
        try {
            Cell cell2 = cellSet.getCell(105);
            fail("expected exception, got " + cell2);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }

        // cell out of range using getCell(List<Integer>)
        try {
            Cell cell2 = cellSet.getCell(Arrays.asList(2, 1));
            fail("expected exception, got " + cell2);
        } catch (IndexOutOfBoundsException e) {
            // ok
        }

        // cell out of range using getCell(Position...) is not possible; but
        // number of positions might be wrong
        try {
            // too few dimensions
            Cell cell2 =
                cellSet.getCell(cellSet.getAxes().get(0).getPositions().get(0));
            fail("expected exception, got " + cell2);
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            // too many dimensions
            Cell cell2 =
                cellSet.getCell(
                    cellSet.getAxes().get(0).getPositions().get(0),
                    cellSet.getAxes().get(1).getPositions().get(0),
                    cellSet.getAxes().get(0).getPositions().get(0));
            fail("expected exception, got " + cell2);
        } catch (IllegalArgumentException e) {
            // ok
        }

        // We provide positions from the wrong axes, but the provider doesn't
        // notice that they're wrong. That's OK.
        cell =
            cellSet.getCell(
                cellSet.getAxes().get(1).getPositions().get(1),
                cellSet.getAxes().get(0).getPositions().get(1));
        assertEquals(3, cell.getOrdinal());

        // Null cell
        cellSet =
            olapStatement.executeOlapQuery(
                "with member [Measures].[X] as 'IIF([Measures].[Store Sales]>10000,[Measures].[Store Sales],Null)'\n" +
                    "select\n" +
                    "{[Measures].[X]} on columns,\n" +
                    "{[Product].[Product Department].members} on rows\n" +
                    "from Sales");
        cell = cellSet.getCell(0);
        assertFalse(cell.isNull());
        cell = cellSet.getCell(2);
        assertTrue(cell.isNull());

        // Empty cell
        cellSet =
            olapStatement.executeOlapQuery(
                "select from [Sales]\n"
                    + "where ([Time].[1997].[Q4].[12],\n"
                    + "  [Product].[All Products].[Drink].[Alcoholic Beverages].[Beer and Wine].[Beer].[Portsmouth].[Portsmouth Imported Beer],\n"
                    + "  [Store].[All Stores].[USA].[WA].[Bellingham])");
        cell = cellSet.getCell(0);
        assertTrue(cell.isEmpty());

        // Error cell
        cellSet =
            olapStatement.executeOlapQuery(
                "with member [Measures].[Foo] as ' Dimensions(-1).Name '\n"
                    + "select {[Measures].[Foo]} on columns from [Sales]");
        cell = cellSet.getCell(0);
        switch (tester.getFlavor()) {
        case XMLA:
            // FIXME: mondrian's XMLA provider doesn't indicate that a cell is
            // an error
            break;
        default:
            assertTrue(cell.isError());
            assertEquals("Index '-1' out of bounds", cell.getErrorText());
            break;
        }

        // todo: test CellSetAxis methods
        /*
    public int getAxisOrdinal() {
    public CellSet getCellSet() {
    public CellSetAxisMetaData getAxisMetaData() {
    public List<Position> getPositions() {
    public int getPositionCount() {
    public ListIterator<Position> iterate() {

    todo: test OlapResultAxisMetaData methods

    public org.olap4j.Axis getAxisOrdinal() {
    public List<Hierarchy> getHierarchies() {
    public List<Property> getProperties() {
         */
    }

    /**
     * Tests different scrolling characteristics.
     *
     * <p>In one mode, you request that you get all of the positions on an axis.
     * You can call {@link CellSetAxis#getPositions()} and
     *  {@link CellSetAxis#getPositions()}.
     *
     * <p>In another mode, you can iterate over the positions, calling
     * {@link org.olap4j.CellSetAxis#iterator()}. Note that this method returns
     * a {@link java.util.ListIterator}, which has
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

        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
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

        if (tester.getFlavor() == TestContext.Tester.Flavor.XMLA) {
            // This test requires validator support.
            return;
        }
        MdxValidator validator =
            olapConnection.getParserFactory().createMdxValidator(
                olapConnection);
        try {
            select = validator.validateSelect(select);
            fail("expected exception, got " + select);
        } catch (Exception e) {
            assertTrue(
                TestContext.getStackTrace(e).indexOf("Duplicate axis name 'COLUMNS'.")
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
                Axis.FILTER,
                new ArrayList<IdentifierNode>(),
                null),
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
                    new PropertyValueNode(
                        null,
                        "FORMAT_STRING",
                        LiteralNode.createString(
                            null,
                            "xxx")))));
        select.getAxisList().add(
            new AxisNode(
                null,
                false,
                Axis.COLUMNS,
                new ArrayList<IdentifierNode>(),
                new CallNode(
                    null,
                    "{}",
                    Syntax.Braces,
                    Arrays.asList(
                        (ParseTreeNode)
                        new IdentifierNode(
                            new IdentifierNode.Segment("Gender"))))));
        select.getAxisList().add(
            new AxisNode(
                null,
                false,
                Axis.ROWS,
                new ArrayList<IdentifierNode>(),
                new CallNode(
                    null,
                    "{}",
                    Syntax.Braces,
                    new CallNode(
                        null,
                        "Children",
                        Syntax.Property,
                        new IdentifierNode(
                            new IdentifierNode.Segment("Store"))))));
        select.getFilterAxis().setExpression(
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
        Class.forName(tester.getDriverClassName());
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        Cube cube = olapConnection.getSchema().getCubes().get("Sales Ragged");

        Member member =
            cube.lookupMember(
                "Time", "1997", "Q2");
        assertEquals("[Time].[1997].[Q2]", member.getUniqueName());

        // Member.getChildMemberCount
        assertEquals(3, member.getChildMemberCount());

        // Member.getChildMembers
        final NamedList<? extends Member> childMembers =
            member.getChildMembers();
        assertEquals(3, childMembers.size());
        assertEquals(
            "[Time].[1997].[Q2].[4]", childMembers.get(0).getUniqueName());
        assertEquals(0, childMembers.get(0).getChildMemberCount());
        assertEquals(
            "[Time].[1997].[Q2].[6]", childMembers.get("6").getUniqueName());
        assertNull(childMembers.get("1"));

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
        Class.forName(tester.getDriverClassName());
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        Cube cube = olapConnection.getSchema().getCubes().get("Sales");

        List<Member> memberList =
            cube.lookupMembers(
                EnumSet.of(Member.TreeOp.ANCESTORS, Member.TreeOp.CHILDREN),
                "Time", "1997", "Q2");
        String expected;
        switch (tester.getFlavor()) {
        case XMLA:
            // TODO: Fix mondrian's XMLA driver to return members ordered by
            // level then by ordinal as per XMLA spec
            expected = TestContext.fold("[Time].[1997].[Q2].[4]\n"
                + "[Time].[1997].[Q2].[5]\n"
                + "[Time].[1997].[Q2].[6]\n"
                + "[Time].[1997]\n");
            break;
        default:
            expected = TestContext.fold("[Time].[1997]\n"
                + "[Time].[1997].[Q2].[4]\n"
                + "[Time].[1997].[Q2].[5]\n"
                + "[Time].[1997].[Q2].[6]\n");
        }
        assertEquals(expected, memberListToString(memberList));

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
        switch (tester.getFlavor()) {
        case XMLA:
            // TODO: fix mondrian's XMLA driver to return members ordered by
            // level then ordinal
            expected = TestContext.fold("[Time].[1997].[Q2].[4]\n"
                + "[Time].[1997].[Q2].[5]\n"
                + "[Time].[1997].[Q2].[6]\n"
                + "[Time].[1997].[Q1]\n"
                + "[Time].[1997].[Q3]\n"
                + "[Time].[1997].[Q4]\n");
            break;
        default:
            expected = TestContext.fold("[Time].[1997].[Q1]\n"
                + "[Time].[1997].[Q2].[4]\n"
                + "[Time].[1997].[Q2].[5]\n"
                + "[Time].[1997].[Q2].[6]\n"
                + "[Time].[1997].[Q3]\n"
                + "[Time].[1997].[Q4]\n");
            break;
        }
        assertEquals(
            expected,
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
        Class.forName(tester.getDriverClassName());
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);

        // Schema
        boolean found = false;
        for (Catalog catalog : olapConnection.getCatalogs()) {
            for (Schema schema : catalog.getSchemas()) {
                if (schema.equals(olapConnection.getSchema())) {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(found);

        Cube cube = olapConnection.getSchema().getCubes().get("Sales");

        int hierarchyCount = 0;
        for (Dimension dimension : cube.getDimensions()) {
            // Call every method of Dimension
            assertNotNull(dimension.getCaption(Locale.getDefault()));
            dimension.getDescription(Locale.getDefault());
            assertNotNull(dimension.getDefaultHierarchy());
            assertEquals(
                dimension.getName().equals("Time")
                    ? Dimension.Type.TIME
                    : dimension.getName().equals("Measures")
                    ? Dimension.Type.MEASURE
                    : Dimension.Type.OTHER,
                dimension.getDimensionType());
            assertNotNull(dimension.getName());
            assertNotNull(dimension.getUniqueName());

            for (Hierarchy hierarchy : dimension.getHierarchies()) {
                ++hierarchyCount;

                // Call every method of Hierarchy
                final NamedList<Member> rootMemberList =
                    hierarchy.getRootMembers();
                if (hierarchy.hasAll()) {
                    assertEquals(1, rootMemberList.size());
                }
                for (Member rootMember : rootMemberList) {
                    assertNull(rootMember.getParentMember());
                }
                assertEquals(
                    rootMemberList,
                    hierarchy.getLevels().get(0).getMembers());
                assertNotNull(hierarchy.getDefaultMember());
                assertNotNull(hierarchy.getName());
                assertNotNull(hierarchy.getUniqueName());
                hierarchy.getDescription(Locale.getDefault());
                assertNotNull(hierarchy.getCaption(Locale.getDefault()));
                assertEquals(dimension, hierarchy.getDimension());

                for (Level level : hierarchy.getLevels()) {
                    if (level.getCardinality() >= 100) {
                        continue;
                    }
                    int k = 0;
                    for (Member member : level.getMembers()) {
                        assertNotNull(member.getName());
                        assertEquals(level, member.getLevel());
                        if (dimension.getDimensionType()
                            == Dimension.Type.MEASURE) {
                            assertTrue(member instanceof Measure);
                        }
                        if (++k > 3) {
                            break;
                        }
                    }
                }
            }
        }

        // Make sure every hierarchy which came out through
        // cube.getDimensions().getHierarchies() also comes out through
        // cube.getHierarchies().
        for (Hierarchy hierarchy : cube.getHierarchies()) {
            --hierarchyCount;
            assertNotNull(hierarchy.getName());
        }
        assertEquals(0, hierarchyCount);

        // Look for the Time.Weekly hierarchy, the 2nd hierarchy in the Time
        // dimension.
        final Hierarchy timeWeeklyHierarchy =
            cube.getHierarchies().get("Time.Weekly");
        assertNotNull(timeWeeklyHierarchy);
        assertEquals("Time", timeWeeklyHierarchy.getDimension().getName());
        assertEquals(
            2, timeWeeklyHierarchy.getDimension().getHierarchies().size());

        Cube warehouseCube =
            olapConnection.getSchema().getCubes().get("Warehouse");
        int count = 0;
        for (NamedSet namedSet : warehouseCube.getSets()) {
            ++count;
            assertNotNull(namedSet.getName());
            assertNotNull(namedSet.getUniqueName());
            assertNotNull(namedSet.getCaption(Locale.getDefault()));
            namedSet.getDescription(Locale.getDefault());
            switch (tester.getFlavor()) {
            case XMLA:
                // FIXME: implement getExpression in XMLA driver
                break;
            default:
                assertTrue(namedSet.getExpression().getType() instanceof SetType);
            }
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
        switch (tester.getFlavor()) {
        case MONDRIAN:
            // mondrian does not set ordinals correctly
            assertEquals(-1, member.getOrdinal());
            break;
        default:
            assertEquals(204, member.getOrdinal());
            break;
        }
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
        assertEquals(EnumSet.of(Property.TypeFlag.MEMBER), property.getType());
        assertEquals(Datatype.STRING, property.getDatatype());

        // Measures
        int k = -1;
        Set<String> measureNameSet = new HashSet<String>();
        for (Measure measure : cube.getMeasures()) {
            ++k;
            // The first measure is [Unit Sales], because the list must be
            // sorted by ordinal.
            if (k == 0) {
                assertEquals("Unit Sales", measure.getName());
            }
            if (measure.getName().equals("Profit Growth")
                || measure.getName().equals("Profit last Period")
                || measure.getName().equals("Profit")) {
                assertEquals(Member.Type.FORMULA, measure.getMemberType());
                assertTrue(measure.isCalculated());
            } else {
                assertEquals(Member.Type.MEASURE, measure.getMemberType());
                assertFalse(measure.isCalculated());
            }
            assertNotNull(measure.getName());
            assertNotNull(measure.getAggregator());
            assertTrue(measure.getDatatype() != null);
            measureNameSet.add(measure.getName());
        }
        assertEquals(
            new HashSet<String>(
                Arrays.asList(
                    "Unit Sales",
                    "Customer Count",
                    "Profit last Period",
                    "Profit",
                    "Profit Growth",
                    "Promotion Sales",
                    "Sales Count",
                    "Store Sales",
                    "Store Cost")),
            measureNameSet);
    }

    /**
     * Tests the type-derivation for
     * {@link org.olap4j.mdx.SelectNode#getFrom()} and the {@link CubeType}
     * class.
     *
     * @throws Throwable on error
     */
    public void testCubeType() throws Throwable {
        if (tester.getFlavor() == TestContext.Tester.Flavor.XMLA) {
            // This test requires validator support.
            return;
        }
        Class.forName(tester.getDriverClassName());
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);

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
            fail("expected error, got " + type);
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
     * {@link org.olap4j.mdx.SelectNode#getFilterAxis()}), and the
     * {@link org.olap4j.type.SetType},
     * {@link org.olap4j.type.TupleType},
     * {@link org.olap4j.type.MemberType} type subclasses.
     *
     * @throws Throwable on error
     */
    public void testAxisType() throws Throwable {
        if (tester.getFlavor() == TestContext.Tester.Flavor.XMLA) {
            // This test requires validator support.
            return;
        }
        Class.forName(tester.getDriverClassName());

        // connect using properties and no username/password
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);

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

        // Filter

        final AxisNode filterAxis = select.getFilterAxis();
        assertNull(filterAxis.getType());
        final Type filterType = filterAxis.getExpression().getType();
        assertTrue(filterType instanceof TupleType);
        assertEquals(
            "TupleType<MemberType<member=[Time].[1997].[Q4]>, MemberType<member=[Marital Status].[All Marital Status].[S]>>",
            filterType.toString());
    }

    public void testParseQueryWithNoFilter() throws Exception {
        if (tester.getFlavor() == TestContext.Tester.Flavor.XMLA) {
            // This test requires validator support.
            return;
        }
        Class.forName(tester.getDriverClassName());
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);

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
                    + "from [sales]");
        select = mdxValidator.validateSelect(select);
        AxisNode filterAxis = select.getFilterAxis();
        assertNull(filterAxis);

        try {
            select =
                mdxParser.parseSelect(
                    "select ([Gender], [Store]) on columns\n,"
                        + "{[Customers].[City].Members} on rows\n"
                        + "from [sales]\n"
                        + "where ()");
            fail("expected parse error, got " + select);
        } catch (RuntimeException e) {
            assertTrue(
                TestContext.getStackTrace(e).indexOf(
                    "Syntax error at [4:10], token ')'") >= 0);
        }
    }

    public void testValidateError() throws Exception {
        if (tester.getFlavor() == TestContext.Tester.Flavor.XMLA) {
            // This test requires validator support.
            return;
        }
        Class.forName(tester.getDriverClassName());
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);

        final MdxParserFactory parserFactory =
            olapConnection.getParserFactory();
        MdxParser mdxParser =
            parserFactory.createMdxParser(olapConnection);
        MdxValidator mdxValidator =
            parserFactory.createMdxValidator(olapConnection);

        SelectNode select =
            mdxParser.parseSelect(
                "select ([Gender], [Store]) on columns\n,"
                    + "crossjoin([Customers].[City].Members, [Gender].members) on rows\n"
                    + "from [sales]");
        AxisNode filterAxis = select.getFilterAxis();
        assertNull(filterAxis);

        try {
            select = mdxValidator.validateSelect(select);
            fail("expected parse error, got " + select);
        } catch (OlapException e) {
            assertEquals("Validation error", e.getMessage());
            assertTrue(
                TestContext.getStackTrace(e).contains(
                    "Dimension '[Gender]' appears in more than one independent axis."));
        }
    }

    // TODO: test for HierarchyType
    // TODO: test for DimensionType
    // TODO: test for LevelType

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

    public void testStatementCancel() throws Throwable {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        final OlapStatement olapStatement = olapConnection.createStatement();
        final Throwable[] exceptions = {null};
        new Thread(
            new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                        olapStatement.cancel();
                    } catch (Throwable e) {
                        exceptions[0] = e;
                    }
                }
            }
        ).start();
        try {
            final CellSet cellSet = olapStatement.executeOlapQuery(
                "SELECT [Customers].Members * \n"
                    + " [Time].Members on columns\n"
                    + "from [Sales]");
            fail("expected exception indicating stmt had been canceled,"
                + " got cellSet " + cellSet);
        } catch (OlapException e) {
            assertTrue(e.getMessage().indexOf("The statement execution was canceled") >= 0);
        }
        if (exceptions[0] != null) {
            throw exceptions[0];
        }
    }

    public void testStatementTimeout() throws Throwable {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        final OlapStatement olapStatement = olapConnection.createStatement();

        try {
            olapStatement.setQueryTimeout(-1);
            fail("expected exception");
        } catch (SQLException e) {
            assertTrue(e.getMessage().indexOf("The timeout value you provided") >= 0
                    && e.getMessage().indexOf("is not valid. It needs to be a positive non-zero integer") >= 0);
        }
        olapStatement.setQueryTimeout(1);
        try {
            final CellSet cellSet =
                olapStatement.executeOlapQuery(
                    "SELECT [Store].Members * \n"
                        + " [Customers].Members * \n"
                        + " [Time].Members on columns\n"
                        + "from [Sales]");
            fail("expected exception indicating timeout,"
                + " got cellSet " + cellSet);
        } catch (OlapException e) {
            assertTrue(e.getMessage().indexOf("Query timeout of ") >= 0);
        }
    }

    public void testCellSetBug() throws SQLException {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        final OlapStatement olapStatement = olapConnection.createStatement();
        // Note: substitute [Sales Ragged] for [Sales] below and the query
        // takes a very long time against mondrian's XMLA driver, because
        // mondrian has a performance bug assigning ordinals to ragged
        // hierarchies, and XMLA requests ask for member ordinals along with
        // the other attributes of members.
        CellSet cellSet =
            olapStatement.executeOlapQuery(
                    "SELECT " +
                    "{[Product].[All Products].[Drink].[Alcoholic Beverages].Children, [Product].[All Products].[Food].[Baked Goods].Children} ON COLUMNS, " +
                    "CrossJoin([Store].[All Stores].[USA].[CA].Children, [Time].[1997].[Q1].Children) ON ROWS " +
                    "FROM [Sales]");
        TestContext.assertEqualsVerbose(
            TestContext.fold("Axis #0:\n" +
                "{[Measures].[Unit Sales], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n" +
                "Axis #1:\n" +
                "{[Product].[All Products].[Drink].[Alcoholic Beverages].[Beer and Wine]}\n" +
                "{[Product].[All Products].[Food].[Baked Goods].[Bread]}\n" +
                "Axis #2:\n" +
                "{[Store].[All Stores].[USA].[CA].[Alameda], [Time].[1997].[Q1].[1]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Alameda], [Time].[1997].[Q1].[2]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Alameda], [Time].[1997].[Q1].[3]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Beverly Hills], [Time].[1997].[Q1].[1]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Beverly Hills], [Time].[1997].[Q1].[2]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Beverly Hills], [Time].[1997].[Q1].[3]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Los Angeles], [Time].[1997].[Q1].[1]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Los Angeles], [Time].[1997].[Q1].[2]}\n" +
                "{[Store].[All Stores].[USA].[CA].[Los Angeles], [Time].[1997].[Q1].[3]}\n" +
                "{[Store].[All Stores].[USA].[CA].[San Diego], [Time].[1997].[Q1].[1]}\n" +
                "{[Store].[All Stores].[USA].[CA].[San Diego], [Time].[1997].[Q1].[2]}\n" +
                "{[Store].[All Stores].[USA].[CA].[San Diego], [Time].[1997].[Q1].[3]}\n" +
                "{[Store].[All Stores].[USA].[CA].[San Francisco], [Time].[1997].[Q1].[1]}\n" +
                "{[Store].[All Stores].[USA].[CA].[San Francisco], [Time].[1997].[Q1].[2]}\n" +
                "{[Store].[All Stores].[USA].[CA].[San Francisco], [Time].[1997].[Q1].[3]}\n" +
                "Row #0: \n" +
                "Row #0: \n" +
                "Row #1: \n" +
                "Row #1: \n" +
                "Row #2: \n" +
                "Row #2: \n" +
                "Row #3: 22\n" +
                "Row #3: 63\n" +
                "Row #4: 28\n" +
                "Row #4: 59\n" +
                "Row #5: 28\n" +
                "Row #5: 39\n" +
                "Row #6: 70\n" +
                "Row #6: 51\n" +
                "Row #7: 89\n" +
                "Row #7: 51\n" +
                "Row #8: 27\n" +
                "Row #8: 54\n" +
                "Row #9: 54\n" +
                "Row #9: 51\n" +
                "Row #10: 38\n" +
                "Row #10: 48\n" +
                "Row #11: 64\n" +
                "Row #11: 55\n" +
                "Row #12: 6\n" +
                "Row #12: 2\n" +
                "Row #13: 3\n" +
                "Row #13: 7\n" +
                "Row #14: 2\n" +
                "Row #14: 10\n"),
            TestContext.toString(cellSet));
    }

    public void testCellSetWithCalcMember() throws SQLException {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        final OlapStatement olapStatement = olapConnection.createStatement();
        CellSet cellSet =
            olapStatement.executeOlapQuery(
                "WITH MEMBER [Measures].[Average Profit] AS" +
                    "'[Measures].[Profit] / [Measures].[Sales Count]'" +
                    "SELECT {[Measures].[Average Profit]} ON 0,\n"
                    + "{[Product].Children} ON 1\n"
                    + "FROM [Sales]");
        TestContext.assertEqualsVerbose(
            TestContext.fold("Axis #0:\n" +
                "{[Store].[All Stores], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time].[1997], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n" +
                "Axis #1:\n" +
                "{[Measures].[Average Profit]}\n" +
                "Axis #2:\n" +
                "{[Product].[All Products].[Drink]}\n" +
                "{[Product].[All Products].[Food]}\n" +
                "{[Product].[All Products].[Non-Consumable]}\n" +
                "Row #0: $3.68\n" +
                "Row #1: $3.94\n" +
                "Row #2: $3.93\n"), TestContext.toString(cellSet));
    }

    public void testBuildQuery() throws SQLException {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        buildQuery(olapConnection, true);
        buildQuery(olapConnection, false);
    }

    private void buildQuery(
        OlapConnection olapConnection,
        boolean useCubeObject)
        throws OlapException
    {
        final String catalogName;
        switch (tester.getFlavor()) {
        case MONDRIAN:
            catalogName = "LOCALDB";
            break;
        case XMLA:
        default:
            catalogName = "FoodMart";
            break;
        }
        Catalog catalog = olapConnection.getCatalogs().get(catalogName);
        Schema schema = catalog.getSchemas().get("FoodMart");
        Cube cube = schema.getCubes().get("Sales");
        SelectNode query = new SelectNode();
        ParseTreeNode cubeNode;
        if (useCubeObject) {
            cubeNode = new IdentifierNode(
                IdentifierNode.parseIdentifier(cube.getUniqueName()));
        } else {
            cubeNode = new CubeNode(null, cube);
        }
        query.setFrom(cubeNode);
        AxisNode columnAxis =
            new AxisNode(
                null, false, Axis.COLUMNS, null,
                new CallNode(
                    null, "MEMBERS", Syntax.Property,
                    new IdentifierNode(
                        IdentifierNode.parseIdentifier("[Gender]"))));
        AxisNode rowAxis =
            new AxisNode(
                null, false, Axis.ROWS, null,
                new CallNode(
                    null, "CHILDREN", Syntax.Property,
                    new IdentifierNode(
                        IdentifierNode.parseIdentifier("[Customers].[USA]"))));
        query.getAxisList().add(columnAxis);
        query.getAxisList().add(rowAxis);
        OlapStatement statement = olapConnection.createStatement();
        CellSet cellSet = statement.executeOlapQuery(query);
        TestContext.assertEqualsVerbose(TestContext.fold(
            "Axis #0:\n" +
                "{[Measures].[Unit Sales], [Store].[All Stores], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time].[1997], [Product].[All Products], [Promotion Media].[All Media], [Promotions].[All Promotions], [Education Level].[All Education Levels], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n" +
                "Axis #1:\n" +
                "{[Gender].[All Gender]}\n" +
                "{[Gender].[All Gender].[F]}\n" +
                "{[Gender].[All Gender].[M]}\n" +
                "Axis #2:\n" +
                "{[Customers].[All Customers].[USA].[CA]}\n" +
                "{[Customers].[All Customers].[USA].[OR]}\n" +
                "{[Customers].[All Customers].[USA].[WA]}\n" +
                "Row #0: 74,748\n" +
                "Row #0: 36,759\n" +
                "Row #0: 37,989\n" +
                "Row #1: 67,659\n" +
                "Row #1: 33,036\n" +
                "Row #1: 34,623\n" +
                "Row #2: 124,366\n" +
                "Row #2: 61,763\n" +
                "Row #2: 62,603\n"),
            TestContext.toString(cellSet));
    }

    public void testBuildQuery2() throws ClassNotFoundException, SQLException {
        connection = tester.createConnection();
        OlapConnection olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);

        Schema schema = olapConnection.getSchema();
        Cube cube = schema.getCubes().get("Sales");
        Measure measure = cube.getMeasures().get(0);
        assertEquals("Unit Sales", measure.getName());
        Dimension dimPromotionMedia = cube.getDimensions().get("Promotion Media");
        //
        // IdentifierNode cubeNode = new IdentifierNode(new IdentifierNode.Segment(cube.getUniqueName()));
        CubeNode cubeNode = new CubeNode(null, cube);
        MemberNode measuresQuantity = new MemberNode(null, measure);
        HierarchyNode promotionHierarchyNode =
            new HierarchyNode(null, dimPromotionMedia.getDefaultHierarchy());
        CallNode promotionChildren =
            new CallNode(
                null, "children", Syntax.Property, promotionHierarchyNode);
        //
        List<IdentifierNode> columnDimensionProperties =
            new ArrayList<IdentifierNode>();
        AxisNode columnAxis =
            new AxisNode(
                null, false,
                Axis.COLUMNS,
                columnDimensionProperties,
                new CallNode(null, "{}", Syntax.Braces, measuresQuantity));
        List<IdentifierNode> rowDimensionProperties =
            new ArrayList<IdentifierNode>();
        AxisNode rowAxis =
            new AxisNode(
                null, false,
                Axis.ROWS,
                rowDimensionProperties,
                new CallNode(null, "{}", Syntax.Braces, promotionChildren));
        //
        SelectNode query = new SelectNode();
        query.setFrom(cubeNode);
        query.getAxisList().add(columnAxis);
        query.getAxisList().add(rowAxis);
        //
        OlapStatement statement = olapConnection.createStatement();
        CellSet cellSet = statement.executeOlapQuery(query);
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        for (Position row : cellSet.getAxes().get(1)) {
            for (Position column : cellSet.getAxes().get(0)) {
                pw.print("ROW:");
                for (Member member : row.getMembers()) {
                    pw.print("[" + member.getName() + "]");
                }
                pw.print(" COL:");
                for (Member member : column.getMembers()) {
                    pw.print("[" + member.getName() + "]");
                }
                Cell cell = cellSet.getCell(column, row);
                pw.println(" CELL:" + cell.getFormattedValue());
            }
        }
        pw.flush();
        TestContext.assertEqualsVerbose(
            TestContext.fold(
                "ROW:[Bulk Mail] COL:[Unit Sales] CELL:4,320\n" +
                    "ROW:[Cash Register Handout] COL:[Unit Sales] CELL:6,697\n" +
                    "ROW:[Daily Paper] COL:[Unit Sales] CELL:7,738\n" +
                    "ROW:[Daily Paper, Radio] COL:[Unit Sales] CELL:6,891\n" +
                    "ROW:[Daily Paper, Radio, TV] COL:[Unit Sales] CELL:9,513\n" +
                    "ROW:[In-Store Coupon] COL:[Unit Sales] CELL:3,798\n" +
                    "ROW:[No Media] COL:[Unit Sales] CELL:195,448\n" +
                    "ROW:[Product Attachment] COL:[Unit Sales] CELL:7,544\n" +
                    "ROW:[Radio] COL:[Unit Sales] CELL:2,454\n" +
                    "ROW:[Street Handout] COL:[Unit Sales] CELL:5,753\n" +
                    "ROW:[Sunday Paper] COL:[Unit Sales] CELL:4,339\n" +
                    "ROW:[Sunday Paper, Radio] COL:[Unit Sales] CELL:5,945\n" +
                    "ROW:[Sunday Paper, Radio, TV] COL:[Unit Sales] CELL:2,726\n" +
                    "ROW:[TV] COL:[Unit Sales] CELL:3,607\n"),
            sw.toString());
    }
}

// End ConnectionTest.java
