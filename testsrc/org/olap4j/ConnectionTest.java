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

import junit.framework.TestCase;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.List;
import java.util.Locale;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.olap4j.metadata.*;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Schema;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.ParseTreeWriter;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.test.TestContext;

/**
 * Unit test for olap4j Driver and Connection classes.
 *
 * @version $Id$
 */
public class ConnectionTest extends TestCase {
    private final Helper helper = new HelperImpl();

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

    public void testConnection() throws ClassNotFoundException, SQLException {
        Class.forName(helper.getDriverClassName());

        // connect using properties and no username/password
        Connection connection = helper.createConnection();
        assertNotNull(connection);

        // check isClosed, isValid
        assertFalse(connection.isClosed());

        // check valid with no time limit
        assertTrue(connection.isValid(0));

        // check valid with one minute time limit; should be enough
        assertTrue(connection.isValid(60));

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
        assertTrue(connection.isWrapperFor(Connection.class));
        Connection connection2 = connection.unwrap(Connection.class);
        assertEquals(connection2, connection);

        // Silly unwrap
        assertTrue(connection.isWrapperFor(Object.class));
        Object object = connection.unwrap(Object.class);
        assertEquals(object, connection);

        // Invalid unwrap
        assertFalse(connection.isWrapperFor(Writer.class));
        try {
            Writer writer = connection.unwrap(Writer.class);
            fail("expected exception, got writer" + writer);
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("does not implement"));
        }

        // Unwrap the mondrian connection.
        if (helper.isMondrian()) {
            final mondrian.olap.Connection mondrianConnection =
                connection.unwrap(mondrian.olap.Connection.class);
            assertNotNull(mondrianConnection);
        }
    }

    public void testDatabaseMetaData() throws SQLException {
        java.sql.Connection connection = helper.createConnection();
        String catalogName = connection.getCatalog();
        assertEquals("LOCALDB", catalogName);

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        assertTrue(databaseMetaData.getDatabaseMajorVersion() > 0);
        assertTrue(databaseMetaData.getDatabaseMinorVersion() >= 0);
        assertTrue(databaseMetaData.getDatabaseProductName() != null);
        assertTrue(databaseMetaData.getDatabaseProductVersion() != null);
        assertTrue(databaseMetaData.getDriverName() != null);
        assertTrue(databaseMetaData.getDriverVersion() != null);

        // mondrian-specific
        assertTrue(databaseMetaData.isReadOnly());
        assertNull(databaseMetaData.getUserName());
        assertNotNull(databaseMetaData.getURL());

        // unwrap connection; may or may not be the same object as connection;
        // check extended methods
        OlapConnection olapConnection = connection.unwrap(OlapConnection.class);
        OlapDatabaseMetaData olapDatabaseMetaData =
            olapConnection.getMetaData();

        // also unwrap metadata from regular connection
        assertTrue(databaseMetaData.isWrapperFor(OlapDatabaseMetaData.class));
        assertFalse(databaseMetaData.isWrapperFor(OlapStatement.class));
        OlapDatabaseMetaData olapDatabaseMetaData1 =
            databaseMetaData.unwrap(
                OlapDatabaseMetaData.class);
        assertTrue(
            olapDatabaseMetaData1.getDatabaseProductVersion().equals(
                olapDatabaseMetaData.getDatabaseProductVersion()));

        // check schema
        final Schema schema1 = olapConnection.getSchema();
        assertEquals(schema1.getName(), "FoodMart");

        checkResultSet(olapDatabaseMetaData.getActions());

        String dataSourceName = "xx";
        checkResultSet(olapDatabaseMetaData.getDatasources(dataSourceName));

        checkResultSet(olapDatabaseMetaData.getLiterals());

        checkResultSet(olapDatabaseMetaData.getDatabaseProperties(dataSourceName));

        checkResultSet(olapDatabaseMetaData.getProperties());

        String keywords = olapDatabaseMetaData.getMdxKeywords();
        assertNotNull(keywords);

        // todo: call getCubes with a pattern for cube name and schema name

        // todo: call getCubes with a different schema

        String schemaPattern = "xx";
        String cubeNamePattern = "SALES";
        checkResultSet(
            olapDatabaseMetaData.getCubes(
                catalogName,
                schemaPattern,
                cubeNamePattern));

        int k = 0;
        for (Catalog catalog : olapConnection.getCatalogs()) {
            ++k;
            assertEquals(catalog.getMetaData(), olapDatabaseMetaData);
            for (Schema schema : catalog.getSchemas()) {
                ++k;
                assertEquals(schema.getCatalog(), catalog);
                for (Cube cube : schema.getCubes()) {
                    ++k;
                    assertEquals(cube.getSchema(), schema);
                }
                for (Dimension dimension : schema.getSharedDimensions()) {
                    ++k;
                }
                for (Locale locale : schema.getSupportedLocales()) {
                    ++k;
                }
            }
        }
        assertTrue(k > 0);

        checkResultSet(
            olapDatabaseMetaData.getDatabaseProperties(dataSourceName));

        checkResultSet(
            olapDatabaseMetaData.getDatasources(dataSourceName));

        checkResultSet(
            olapDatabaseMetaData.getDimensions());

        checkResultSet(
            olapDatabaseMetaData.getFunctions());

        checkResultSet(
            olapDatabaseMetaData.getHierarchies());

        checkResultSet(
            olapDatabaseMetaData.getLevels());

        checkResultSet(
            olapDatabaseMetaData.getLiterals());

        checkResultSet(
            olapDatabaseMetaData.getMeasures());

        checkResultSet(
            olapDatabaseMetaData.getMembers());

        checkResultSet(
            olapDatabaseMetaData.getProperties());

        checkResultSet(
            olapDatabaseMetaData.getSets());

        // todo: More tests required for other methods on DatabaseMetaData
    }

    private void checkResultSet(ResultSet resultSet) throws SQLException {
        assertNotNull(resultSet);
        int k = 0;
        while (resultSet.next()) {
            ++k;
        }
        assertTrue(k >= 0);
    }

    public void testStatement() throws SQLException {
        Connection connection = helper.createConnection();
        Statement statement = connection.createStatement();

        // Closing a statement is idempotent.
        assertFalse(statement.isClosed());
        statement.close();
        assertTrue(statement.isClosed());
        statement.close();
        assertTrue(statement.isClosed());

        // Unwrap the statement to get the olap statement. Depending on the
        // driver, this may or may not be the same object.
        statement = connection.createStatement();
        OlapStatement olapStatement = statement.unwrap(OlapStatement.class);
        assertNotNull(olapStatement);

        // Execute a simple query.
        CellSet cellSet =
            olapStatement.executeOlapQuery(
                "SELECT FROM [Sales]");
        List<CellSetAxis> axesList = cellSet.getAxes();
        assertNotNull(axesList);
        assertEquals(0, axesList.size());

        // Executing another query implicitly closes the previous result set.
        assertFalse(statement.isClosed());
        assertFalse(cellSet.isClosed());
        CellSet cellSet2 =
            olapStatement.executeOlapQuery(
                "SELECT FROM [Sales]");
        assertFalse(statement.isClosed());
        assertTrue(cellSet.isClosed());

        // Close the statement; this closes the result set.
        assertFalse(cellSet2.isClosed());
        statement.close();
        assertTrue(statement.isClosed());
        assertTrue(cellSet2.isClosed());
        cellSet.close();
        assertTrue(statement.isClosed());
        assertTrue(cellSet2.isClosed());
        assertTrue(cellSet.isClosed());

        // Close the connection.
        connection.close();
    }

    public void testCellSet() throws SQLException {
        Connection connection = helper.createConnection();
        Statement statement = connection.createStatement();
        final OlapStatement olapStatement =
            statement.unwrap(OlapStatement.class);
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
        OlapConnection olapConnection = connection.unwrap(OlapConnection.class);
        MdxParser mdxParser =
            olapConnection.getParserFactory().createMdxParser(olapConnection);
        SelectNode select =
            mdxParser.parseSelect(
                "with member [Measures].[Foo] as ' [Measures].[Bar] ', FORMAT_STRING='xxx'\n" +
            " select {[Gender]} on columns, {[Store].Children} on rows\n" +
            "from [sales]\n" +
            "where [Time].[1997].[Q4]");

        // unparse

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

        // build parse tree (todo)

        // test that get error if axes do not have unique names (todo)
    }

    /**
     * Tests creation of an MDX query from a parse tree. Build the parse tree
     * programmatically.
     */
    public void testUnparsing() {

    }

    /**
     * Tests access control. The metadata (e.g. members & hierarchies) should
     * reflect what the current user/role can see. For example, USA.CA.SF has no children.
     */
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
    static class HelperImpl implements Helper {

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
                return "jdbc:mondrian:Jdbc='jdbc:odbc:MondrianFoodMart';Catalog='../mondrian/demo/FoodMart.xml';JdbcDrivers=sun.jdbc.odbc.JdbcOdbcDriver;";
        }

        public static final String DRIVER_CLASS_NAME = "mondrian.olap4j.MondrianOlap4jDriver";

        public static final String DRIVER_URL_PREFIX = "jdbc:mondrian:";
        private static final String USER = "user";
        private static final String PASSWORD = "password";
    }
}

// End ConnectionTest.java
