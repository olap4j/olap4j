/*
// $Id: $
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
import java.util.ArrayList;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.olap4j.metadata.*;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.Schema;
import mondrian.olap.*;

/**
 * Unit test for olap4j Driver and Connection classes.
 *
 * @version $Id: $
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

        Database database = olapDatabaseMetaData.getDatabase();
        assertNotNull(database);

        int k = 0;
        for (Catalog catalog : database.getCatalogs()) {
            ++k;
            assertEquals(catalog.getDatabase(), database);
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

    static class TestContext {
        private static final String NL = System.getProperty("line.separator");

        /**
         * Converts a string constant into locale-specific line endings.
         */
        public static String fold(String string) {
            if (!NL.equals("\n")) {
                string = Util.replace(string, "\n", NL);
            }
            return string;
        }

        /**
         * Formats a {@link org.olap4j.CellSet}.
         */
        public static String toString(CellSet cellSet) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            print(cellSet, pw);
            pw.flush();
            return sw.toString();
        }

        private static void print(CellSet cellSet, PrintWriter pw) {
            pw.println("Axis #0:");
            printAxis(pw, cellSet.getFilterAxis());
            final List<CellSetAxis> axes = cellSet.getAxes();
            int i = 0;
            for (CellSetAxis axis : axes) {
                pw.println("Axis #" + (++i) + ":");
                printAxis(pw, axis);
            }
            // Usually there are 3 axes: {slicer, columns, rows}. Position is a
            // {column, row} pair. We call printRows with axis=2. When it
            // recurses to axis=-1, it prints.
            List<Integer> pos = new ArrayList<Integer>(axes.size());
            for (CellSetAxis axis : axes) {
                pos.add(-1);
            }
            printRows(cellSet, pw, axes.size() - 1, pos);
        }

        private static void printRows(
            CellSet cellSet, PrintWriter pw, int axis, List<Integer> pos)
        {
            CellSetAxis _axis = axis < 0 ?
                cellSet.getFilterAxis() :
                cellSet.getAxes().get(axis);
            List<org.olap4j.Position> positions = _axis.getPositions();
            int i = 0;
            for (Position position : positions) {
                if (axis < 0) {
                    if (i > 0) {
                        pw.print(", ");
                    }
                    printCell(cellSet, pw, pos);
                } else {
                    pos.set(axis, i);
                    if (axis == 0) {
                        int row = axis + 1 < pos.size() ? pos.get(axis + 1) : 0;
                        pw.print("Row #" + row + ": ");
                    }
                    printRows(cellSet, pw, axis - 1, pos);
                    if (axis == 0) {
                        pw.println();
                    }
                }
                i++;
            }
        }

        private static void printAxis(PrintWriter pw, CellSetAxis axis) {
            List<Position> positions = axis.getPositions();
            for (Position position: positions) {
                boolean firstTime = true;
                pw.print("{");
                for (Member member : position.getMembers()) {
                    if (! firstTime) {
                        pw.print(", ");
                    }
                    pw.print(member.getUniqueName());
                    firstTime = false;
                }
                pw.println("}");
            }
        }

        private static void printCell(
            CellSet cellSet, PrintWriter pw, List<Integer> pos)
        {
            Cell cell = cellSet.getCell(pos);
            pw.print(cell.getFormattedValue());
        }
    }
}

// End ConnectionTest.java
