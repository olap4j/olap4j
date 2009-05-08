/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import junit.framework.TestCase;
import org.olap4j.metadata.*;
import org.olap4j.test.TestContext;

import java.sql.*;
import java.util.*;

/**
 * Unit test for olap4j metadata methods.
 *
 * @version $Id$
 */
public class MetadataTest extends TestCase {
    private static final String NL = System.getProperty("line.separator");

    private final TestContext.Tester tester;
    private Connection connection;
    private String catalogName;
    private OlapConnection olapConnection;
    private OlapDatabaseMetaData olapDatabaseMetaData;
    private final String propertyNamePattern = null;
    private final String dataSourceName = "xx";

    private static final List<String> CUBE_COLUMN_NAMES = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME", "CUBE_NAME", "CUBE_TYPE", "CUBE_GUID", "CREATED_ON", "LAST_SCHEMA_UPDATE", "SCHEMA_UPDATED_BY", "LAST_DATA_UPDATE", "DATA_UPDATED_BY", "IS_DRILLTHROUGH_ENABLED", "IS_WRITE_ENABLED", "IS_LINKABLE", "IS_SQL_ENABLED", "DESCRIPTION");
    private static final List<String> LITERALS_COLUMN_NAMES = Arrays.asList("LITERAL_NAME", "LITERAL_VALUE", "LITERAL_INVALID_CHARS", "LITERAL_INVALID_STARTING_CHARS", "LITERAL_MAX_LENGTH");
    private static final List<String> SETS_COLUMN_NAMES = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME", "CUBE_NAME", "SET_NAME", "SCOPE");
    private static final List<String> PROPERTIES_COLUMN_NAMES = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME", "CUBE_NAME", "DIMENSION_UNIQUE_NAME", "HIERARCHY_UNIQUE_NAME", "LEVEL_UNIQUE_NAME", "MEMBER_UNIQUE_NAME", "PROPERTY_NAME", "PROPERTY_CAPTION", "PROPERTY_TYPE", "DATA_TYPE", "PROPERTY_CONTENT_TYPE", "DESCRIPTION");
    private static final List<String> MEMBERS_COLUMN_NAMES = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME", "CUBE_NAME", "DIMENSION_UNIQUE_NAME", "HIERARCHY_UNIQUE_NAME", "LEVEL_UNIQUE_NAME", "LEVEL_NUMBER", "MEMBER_ORDINAL", "MEMBER_NAME", "MEMBER_UNIQUE_NAME", "MEMBER_TYPE", "MEMBER_GUID", "MEMBER_CAPTION", "CHILDREN_CARDINALITY", "PARENT_LEVEL", "PARENT_UNIQUE_NAME", "PARENT_COUNT", "TREE_OP", "DEPTH");
    private static final List<String> MEASURES_COLUMN_NAMES = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME", "CUBE_NAME", "MEASURE_NAME", "MEASURE_UNIQUE_NAME", "MEASURE_CAPTION", "MEASURE_GUID", "MEASURE_AGGREGATOR", "DATA_TYPE", "MEASURE_IS_VISIBLE", "LEVELS_LIST", "DESCRIPTION");
    private static final List<String> LEVELS_COLUMN_NAMES = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME", "CUBE_NAME", "DIMENSION_UNIQUE_NAME", "HIERARCHY_UNIQUE_NAME", "LEVEL_NAME", "LEVEL_UNIQUE_NAME", "LEVEL_GUID", "LEVEL_CAPTION", "LEVEL_NUMBER", "LEVEL_CARDINALITY", "LEVEL_TYPE", "CUSTOM_ROLLUP_SETTINGS", "LEVEL_UNIQUE_SETTINGS", "LEVEL_IS_VISIBLE", "DESCRIPTION");
    private static final List<String> HIERARCHIES_COLUMN_NAMES = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME", "CUBE_NAME", "DIMENSION_UNIQUE_NAME", "HIERARCHY_NAME", "HIERARCHY_UNIQUE_NAME", "HIERARCHY_GUID", "HIERARCHY_CAPTION", "DIMENSION_TYPE", "HIERARCHY_CARDINALITY", "DEFAULT_MEMBER", "ALL_MEMBER", "DESCRIPTION", "STRUCTURE", "IS_VIRTUAL", "IS_READWRITE", "DIMENSION_UNIQUE_SETTINGS", "DIMENSION_IS_VISIBLE", "HIERARCHY_ORDINAL", "DIMENSION_IS_SHARED", "PARENT_CHILD");
    private static final List<String> FUNCTIONS_COLUMN_NAMES = Arrays.asList("FUNCTION_NAME", "DESCRIPTION", "PARAMETER_LIST", "RETURN_TYPE", "ORIGIN", "INTERFACE_NAME", "LIBRARY_NAME", "CAPTION");
    private static final List<String> DIMENSIONS_COLUMN_NAMES = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME", "CUBE_NAME", "DIMENSION_NAME", "DIMENSION_UNIQUE_NAME", "DIMENSION_GUID", "DIMENSION_CAPTION", "DIMENSION_ORDINAL", "DIMENSION_TYPE", "DIMENSION_CARDINALITY", "DEFAULT_HIERARCHY", "DESCRIPTION", "IS_VIRTUAL", "IS_READWRITE", "DIMENSION_UNIQUE_SETTINGS", "DIMENSION_MASTER_UNIQUE_NAME", "DIMENSION_IS_VISIBLE");
    private static final List<String> DATABASE_PROPERTIES_COLUMN_NAMES = Arrays.asList("PROPERTY_NAME", "PROPERTY_DESCRIPTION", "PROPERTY_TYPE", "PROPERTY_ACCESS_TYPE", "IS_REQUIRED", "PROPERTY_VALUE");
    private static final List<String> DATASOURCES_COLUMN_NAMES = Arrays.asList("DATA_SOURCE_NAME", "DATA_SOURCE_DESCRIPTION", "URL", "DATA_SOURCE_INFO", "PROVIDER_NAME", "PROVIDER_TYPE", "AUTHENTICATION_MODE");
    private static final List<String> CATALOGS_COLUMN_NAMES = Arrays.asList("TABLE_CAT");
    private static final List<String> SCHEMAS_COLUMN_NAMES = Arrays.asList("TABLE_SCHEM", "TABLE_CAT");
    private static final List<String> ACTIONS_COLUMN_NAMES = Arrays.asList("SCHEMA_NAME", "CUBE_NAME", "ACTION_NAME", "COORDINATE", "COORDINATE_TYPE");

    public MetadataTest() throws SQLException {
        tester = TestContext.instance().getTester();
    }

    protected void setUp() throws SQLException {
        connection = tester.createConnection();
        catalogName = connection.getCatalog();
        olapConnection =
            tester.getWrapper().unwrap(connection, OlapConnection.class);
        olapDatabaseMetaData  = olapConnection.getMetaData();
    }

    protected void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }

    // ~ Helper methods ----------

    private void assertContains(String seek, String s) {
        if (s.indexOf(seek) < 0) {
            fail("expected to find '" + seek + "' in '" + s + "'");
        }
    }

    private void assertContainsLine(String partial, String seek, String s) {
        if (partial == null) {
            partial = seek;
        }
        int i = s.indexOf(partial);
        if (i < 0) {
            fail("expected to find '" + seek + "' in '" + s + "'");
        }
        int start = i;
        while (start > 0
               && s.charAt(start - 1) != 0x0D
               && s.charAt(start - 1) != 0x0A)
        {
            --start;
        }
        int end = i;
        while (end < s.length()
               && s.charAt(end) != 0x0D
               && s.charAt(end) != 0x0A)
        {
            ++end;
        }
        String line = s.substring(start, end);
        assertEquals(seek, line);
    }

    private void assertNotContains(String seek, String s) {
        if (s.indexOf(seek) >= 0) {
            fail("expected not to find '" + seek + "' in '" + s + "'");
        }
    }

    private int linecount(String s) {
        int i = 0;
        int count = 0;
        while (i < s.length()) {
            int nl = s.indexOf('\n', i);
            if (nl < 0) {
                break;
            }
            i = nl + 1;
            ++count;
        }
        return count;
    }

    // ~ Tests follow -------------

    public void testDatabaseMetaData() throws SQLException {
        assertEquals("" + catalogName + "", catalogName);

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        switch (tester.getFlavor()) {
        case XMLA:
            // FIXME: implement getDatabaseXxxVersion in XMLA driver
            break;
        default:
            assertTrue(databaseMetaData.getDatabaseMajorVersion() > 0);
            assertTrue(databaseMetaData.getDatabaseMinorVersion() >= 0);
//            assertTrue(databaseMetaData.getDatabaseProductName() != null);
            assertTrue(databaseMetaData.getDatabaseProductVersion() != null);
            break;
        }
        assertTrue(databaseMetaData.getDriverName() != null);
        assertTrue(databaseMetaData.getDriverVersion() != null);

        // mondrian-specific
        switch (tester.getFlavor()) {
        case MONDRIAN:
            assertTrue(databaseMetaData.isReadOnly());
            assertNull(databaseMetaData.getUserName());
            assertNotNull(databaseMetaData.getURL());
            break;
        }

        // unwrap connection; may or may not be the same object as connection;
        // check extended methods

        // also unwrap metadata from regular connection
        assertTrue(
            ((OlapWrapper) databaseMetaData).isWrapperFor(
                OlapDatabaseMetaData.class));
        assertFalse(
            ((OlapWrapper) databaseMetaData).isWrapperFor(
                OlapStatement.class));
        OlapDatabaseMetaData olapDatabaseMetaData1 =
            ((OlapWrapper) databaseMetaData).unwrap(
                OlapDatabaseMetaData.class);
        assertTrue(
            olapDatabaseMetaData1.getDriverName().equals(
                olapDatabaseMetaData.getDriverName()));
        switch (tester.getFlavor()) {
        case XMLA:
            // FIXME: implement getDatabaseXxxVersion in XMLA driver
            break;
        default:
            assertTrue(
                olapDatabaseMetaData1.getDatabaseProductVersion().equals(
                    olapDatabaseMetaData.getDatabaseProductVersion()));
        }
    }

    public void testSchema() throws OlapException {
        // check schema
        final Schema schema1 = olapConnection.getSchema();
        assertEquals(schema1.getName(), "FoodMart");
    }

    public void testDatabaseMetaDataGetActions() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getActions(
                catalogName, null, null, null),
            ACTIONS_COLUMN_NAMES);
        assertEquals("", s); // mondrian has no actions
    }

    public void testDatabaseMetaDataGetDatasources() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getDatasources(),
            DATASOURCES_COLUMN_NAMES);
        switch (tester.getFlavor()) {
        case MONDRIAN:
            assertEquals(TestContext.fold("DATA_SOURCE_NAME=xxx,"
                + " DATA_SOURCE_DESCRIPTION=null,"
                + " URL=null,"
                + " DATA_SOURCE_INFO=xxx,"
                + " PROVIDER_NAME=null,"
                + " PROVIDER_TYPE=MDP,"
                + " AUTHENTICATION_MODE=null\n"),
                s);
            break;
        case XMLA:
            assertEquals(TestContext.fold("DATA_SOURCE_NAME=MondrianFoodMart,"
                + " DATA_SOURCE_DESCRIPTION=Mondrian FoodMart data source,"
                + " URL=http://localhost:8080/mondrian/xmla,"
                + " DATA_SOURCE_INFO=MondrianFoodMart,"
                + " PROVIDER_NAME=Mondrian,"
                + " PROVIDER_TYPE=MDP,"
                + " AUTHENTICATION_MODE=Unauthenticated\n"),
                s);
            break;
        }
    }

    public void testDatabaseMetaDataGetCatalogs() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getCatalogs(),
            CATALOGS_COLUMN_NAMES);
        assertEquals(TestContext.fold("TABLE_CAT=" + catalogName + "\n"), s);
    }

    public void testDatabaseMetaDataGetSchemas() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getSchemas(),
            SCHEMAS_COLUMN_NAMES);
        assertEquals(TestContext.fold("TABLE_SCHEM=FoodMart, TABLE_CAT=" + catalogName + "\n"), s);
    }

    public void testDatabaseMetaDataGetLiterals() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getLiterals(),
            LITERALS_COLUMN_NAMES);
        assertContains("LITERAL_NAME=DBLITERAL_QUOTE, LITERAL_VALUE=[, ", s);
    }

    public void testDatabaseMetaDataGetDatabaseProperties() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getDatabaseProperties(
                dataSourceName, propertyNamePattern),
            DATABASE_PROPERTIES_COLUMN_NAMES);
        assertContains("PROPERTY_NAME=ProviderName, ", s);
    }

    public void testDatabaseMetaDataGetProperties() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getProperties(
                catalogName, null, null, null, null, null, null, null),
            PROPERTIES_COLUMN_NAMES);
        assertContains("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Warehouse and Sales, DIMENSION_UNIQUE_NAME=[Store], HIERARCHY_UNIQUE_NAME=[Store], LEVEL_UNIQUE_NAME=[Store].[Store Name], MEMBER_UNIQUE_NAME=null, PROPERTY_NAME=Frozen Sqft, PROPERTY_CAPTION=Frozen Sqft, PROPERTY_TYPE=1, DATA_TYPE=5, PROPERTY_CONTENT_TYPE=0, DESCRIPTION=Warehouse and Sales Cube - Store Hierarchy - Store Name Level - Frozen Sqft Property", s);
        assertEquals(s, 70, linecount(s));

        s = checkResultSet(
            olapDatabaseMetaData.getProperties(
                catalogName, "FoodMart", "Sales",
                null, null, "[Store].[Store Name]",
                null, null),
            PROPERTIES_COLUMN_NAMES);
        assertContains("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Store], HIERARCHY_UNIQUE_NAME=[Store], LEVEL_UNIQUE_NAME=[Store].[Store Name], MEMBER_UNIQUE_NAME=null, PROPERTY_NAME=Has coffee bar, PROPERTY_CAPTION=Has coffee bar, PROPERTY_TYPE=1, DATA_TYPE=11, PROPERTY_CONTENT_TYPE=0, DESCRIPTION=Sales Cube - Store Hierarchy - Store Name Level - Has coffee bar Property", s);
        assertNotContains("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Warehouse and Sales, ", s);
        assertEquals(8, linecount(s));
    }

    public void testDatabaseMetaDataGetMdxKeywords() throws SQLException {
        String keywords = olapDatabaseMetaData.getMdxKeywords();
        assertNotNull(keywords);
        assertContains(",From,", keywords);
    }

    public void testDatabaseMetaDataGetCubes() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getCubes(
                catalogName,
                null,
                null),
            CUBE_COLUMN_NAMES);
        assertContains("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, ", s);

        s = checkResultSet(
            olapDatabaseMetaData.getCubes(
                catalogName,
                null,
                "Warehouse and Sales"),
            CUBE_COLUMN_NAMES);
        assertContains(", CUBE_NAME=Warehouse and Sales,", s);
        assertNotContains(", CUBE_NAME=Warehouse,", s);

        s = checkResultSet(
            olapDatabaseMetaData.getCubes(
                catalogName,
                null,
                "Warehouse%"),
            CUBE_COLUMN_NAMES);
        assertTrue(s.contains(", CUBE_NAME=Warehouse and Sales"));
        assertTrue(s.contains(", CUBE_NAME=Warehouse"));
    }

    public void testGetCatalogs() throws SQLException {
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
    }

    public void testDatabaseMetaDataGetDimensions() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getDimensions(
                catalogName, null, null, null),
            DIMENSIONS_COLUMN_NAMES);
        assertContains("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_NAME=Education Level, DIMENSION_UNIQUE_NAME=[Education Level], DIMENSION_GUID=null, DIMENSION_CAPTION=Education Level, DIMENSION_ORDINAL=9, DIMENSION_TYPE=3, DIMENSION_CARDINALITY=6, DEFAULT_HIERARCHY=[Education Level], DESCRIPTION=Sales Cube - Education Level Dimension, IS_VIRTUAL=false, IS_READWRITE=false, DIMENSION_UNIQUE_SETTINGS=0, DIMENSION_MASTER_UNIQUE_NAME=null, DIMENSION_IS_VISIBLE=true", s);
        assertEquals(62, linecount(s));
    }

    public void testDatabaseMetaDataGetFunctions() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getOlapFunctions(null),
            FUNCTIONS_COLUMN_NAMES);
        assertContains("FUNCTION_NAME=Name, DESCRIPTION=Returns the name of a member., PARAMETER_LIST=Member, RETURN_TYPE=8, ORIGIN=1, INTERFACE_NAME=, LIBRARY_NAME=null, CAPTION=Name", s);
        // Mondrian has 361 functions (as of 2008/1/23)
        final int functionCount = linecount(s);
        assertTrue(functionCount + " functions", functionCount > 360);

        // Mondrian has 13 variants of the Ascendants and Descendants functions
        s = checkResultSet(
            olapDatabaseMetaData.getOlapFunctions("%scendants"),
            FUNCTIONS_COLUMN_NAMES);
        assertEquals(s, 13, linecount(s));
    }

    public void testDatabaseMetaDataGetHierarchies() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getHierarchies(
                catalogName, null, null, null, null),
            HIERARCHIES_COLUMN_NAMES);
        assertContains("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=HR, DIMENSION_UNIQUE_NAME=[Employees], HIERARCHY_NAME=Employees, HIERARCHY_UNIQUE_NAME=[Employees], HIERARCHY_GUID=null, HIERARCHY_CAPTION=Employees, DIMENSION_TYPE=3, HIERARCHY_CARDINALITY=1156, DEFAULT_MEMBER=[Employees].[All Employees], ALL_MEMBER=[Employees].[All Employees], DESCRIPTION=HR Cube - Employees Hierarchy, STRUCTURE=0, IS_VIRTUAL=false, IS_READWRITE=false, DIMENSION_UNIQUE_SETTINGS=0, DIMENSION_IS_VISIBLE=true, HIERARCHY_ORDINAL=7, DIMENSION_IS_SHARED=true, PARENT_CHILD=true", s);

        s = checkResultSet(
            olapDatabaseMetaData.getHierarchies(
                catalogName, null, "Sales", null, "Store"),
            HIERARCHIES_COLUMN_NAMES);
        assertEquals(TestContext.fold("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Store], HIERARCHY_NAME=Store, HIERARCHY_UNIQUE_NAME=[Store], HIERARCHY_GUID=null, HIERARCHY_CAPTION=Store, DIMENSION_TYPE=3, HIERARCHY_CARDINALITY=63, DEFAULT_MEMBER=[Store].[All Stores], ALL_MEMBER=[Store].[All Stores], DESCRIPTION=Sales Cube - Store Hierarchy, STRUCTURE=0, IS_VIRTUAL=false, IS_READWRITE=false, DIMENSION_UNIQUE_SETTINGS=0, DIMENSION_IS_VISIBLE=true, HIERARCHY_ORDINAL=1, DIMENSION_IS_SHARED=true, PARENT_CHILD=false\n"), s);

        // With dimension unique name (bug 2527862).
        s = checkResultSet(
            olapDatabaseMetaData.getHierarchies(
                catalogName, null, "Sales", "[Store]", null),
            HIERARCHIES_COLUMN_NAMES);
        assertEquals(TestContext.fold("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Store], HIERARCHY_NAME=Store, HIERARCHY_UNIQUE_NAME=[Store], HIERARCHY_GUID=null, HIERARCHY_CAPTION=Store, DIMENSION_TYPE=3, HIERARCHY_CARDINALITY=63, DEFAULT_MEMBER=[Store].[All Stores], ALL_MEMBER=[Store].[All Stores], DESCRIPTION=Sales Cube - Store Hierarchy, STRUCTURE=0, IS_VIRTUAL=false, IS_READWRITE=false, DIMENSION_UNIQUE_SETTINGS=0, DIMENSION_IS_VISIBLE=true, HIERARCHY_ORDINAL=1, DIMENSION_IS_SHARED=true, PARENT_CHILD=false\n"), s);
    }

    public void testDatabaseMetaDataGetLevels() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getLevels(
                catalogName, null, null, null, null, null),
            LEVELS_COLUMN_NAMES);
        assertContainsLine(
            "LEVEL_NAME=Product Category,",
            "CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Product], HIERARCHY_UNIQUE_NAME=[Product], LEVEL_NAME=Product Category, LEVEL_UNIQUE_NAME=[Product].[Product Category], LEVEL_GUID=null, LEVEL_CAPTION=Product Category, LEVEL_NUMBER=3, LEVEL_CARDINALITY=55, LEVEL_TYPE=0, CUSTOM_ROLLUP_SETTINGS=0, LEVEL_UNIQUE_SETTINGS=0, LEVEL_IS_VISIBLE=true, DESCRIPTION=Sales Cube - Product Hierarchy - Product Category Level",
            s);

        s = checkResultSet(
            olapDatabaseMetaData.getLevels(
                catalogName, null, "Sales", null, "[Store]", null),
            LEVELS_COLUMN_NAMES);
        assertEquals(s, 5, linecount(s));
    }

    public void testDatabaseMetaDataGetLiterals2() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getLiterals(),
            LITERALS_COLUMN_NAMES);
        assertContains("LITERAL_NAME=DBLITERAL_QUOTE, LITERAL_VALUE=[, LITERAL_INVALID_CHARS=null, LITERAL_INVALID_STARTING_CHARS=null, LITERAL_MAX_LENGTH=-1", s);
        assertEquals(17, linecount(s));
    }

    public void testDatabaseMetaDataGetMeasures() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getMeasures(
                catalogName, null, null, null, null),
            MEASURES_COLUMN_NAMES);
        assertContains("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, MEASURE_NAME=Profit, MEASURE_UNIQUE_NAME=[Measures].[Profit], MEASURE_CAPTION=Profit, MEASURE_GUID=null, MEASURE_AGGREGATOR=127, DATA_TYPE=130, MEASURE_IS_VISIBLE=true, LEVELS_LIST=null, DESCRIPTION=Sales Cube - Profit Member", s);

        // wildcard match
        s = checkResultSet(
            olapDatabaseMetaData.getMeasures(
                catalogName, null, "Sales", "%Sales", null),
            MEASURES_COLUMN_NAMES);
        assertEquals(s, 3, linecount(s));

        // wildcard match
        s = checkResultSet(
            olapDatabaseMetaData.getMeasures(
                catalogName, null, "Sales", null, "[Measures].[Unit Sales]"),
            MEASURES_COLUMN_NAMES);
        assertEquals(s, 1, linecount(s));
    }

    public void testDatabaseMetaDataGetMembers() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getMembers(
                catalogName, "FoodMart", "Sales", null, "[Gender]", null, null,
                null),
            MEMBERS_COLUMN_NAMES);
        assertEquals(
            TestContext.fold("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Gender], HIERARCHY_UNIQUE_NAME=[Gender], LEVEL_UNIQUE_NAME=[Gender].[(All)], LEVEL_NUMBER=0, MEMBER_ORDINAL=0, MEMBER_NAME=All Gender, MEMBER_UNIQUE_NAME=[Gender].[All Gender], MEMBER_TYPE=2, MEMBER_GUID=null, MEMBER_CAPTION=All Gender, CHILDREN_CARDINALITY=2, PARENT_LEVEL=0, PARENT_UNIQUE_NAME=null, PARENT_COUNT=0, TREE_OP=null, DEPTH=0\n"
                + "CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Gender], HIERARCHY_UNIQUE_NAME=[Gender], LEVEL_UNIQUE_NAME=[Gender].[Gender], LEVEL_NUMBER=1, MEMBER_ORDINAL=1, MEMBER_NAME=F, MEMBER_UNIQUE_NAME=[Gender].[All Gender].[F], MEMBER_TYPE=1, MEMBER_GUID=null, MEMBER_CAPTION=F, CHILDREN_CARDINALITY=0, PARENT_LEVEL=0, PARENT_UNIQUE_NAME=[Gender].[All Gender], PARENT_COUNT=1, TREE_OP=null, DEPTH=1\n"
                + "CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Gender], HIERARCHY_UNIQUE_NAME=[Gender], LEVEL_UNIQUE_NAME=[Gender].[Gender], LEVEL_NUMBER=1, MEMBER_ORDINAL=2, MEMBER_NAME=M, MEMBER_UNIQUE_NAME=[Gender].[All Gender].[M], MEMBER_TYPE=1, MEMBER_GUID=null, MEMBER_CAPTION=M, CHILDREN_CARDINALITY=0, PARENT_LEVEL=0, PARENT_UNIQUE_NAME=[Gender].[All Gender], PARENT_COUNT=1, TREE_OP=null, DEPTH=1\n"),
            s);

        // by member unique name
        s = checkResultSet(
            olapDatabaseMetaData.getMembers(
                catalogName, "FoodMart", "Sales", null, null, null,
                "[Time].[1997].[Q2].[4]", null),
            MEMBERS_COLUMN_NAMES);
        assertEquals(TestContext.fold("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Time], HIERARCHY_UNIQUE_NAME=[Time], LEVEL_UNIQUE_NAME=[Time].[Month], LEVEL_NUMBER=2, MEMBER_ORDINAL=6, MEMBER_NAME=4, MEMBER_UNIQUE_NAME=[Time].[1997].[Q2].[4], MEMBER_TYPE=1, MEMBER_GUID=null, MEMBER_CAPTION=4, CHILDREN_CARDINALITY=0, PARENT_LEVEL=1, PARENT_UNIQUE_NAME=[Time].[1997].[Q2], PARENT_COUNT=1, TREE_OP=null, DEPTH=2\n"),
            s);

        // with treeop
        s = checkResultSet(
            olapDatabaseMetaData.getMembers(
                catalogName, "FoodMart", "Sales", null, null, null,
                "[Customers].[USA].[CA]",
                EnumSet.of(Member.TreeOp.ANCESTORS, Member.TreeOp.SIBLINGS)),
            MEMBERS_COLUMN_NAMES);
        switch (tester.getFlavor()) {
        case MONDRIAN:
            // TODO: fix mondrian driver so that members are returned sorted
            // by level depth
            assertEquals(
                TestContext.fold("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Customers], HIERARCHY_UNIQUE_NAME=[Customers], LEVEL_UNIQUE_NAME=[Customers].[State Province], LEVEL_NUMBER=2, MEMBER_ORDINAL=7235, MEMBER_NAME=OR, MEMBER_UNIQUE_NAME=[Customers].[All Customers].[USA].[OR], MEMBER_TYPE=1, MEMBER_GUID=null, MEMBER_CAPTION=OR, CHILDREN_CARDINALITY=11, PARENT_LEVEL=1, PARENT_UNIQUE_NAME=[Customers].[All Customers].[USA], PARENT_COUNT=1, TREE_OP=null, DEPTH=2\n"
                    + "CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Customers], HIERARCHY_UNIQUE_NAME=[Customers], LEVEL_UNIQUE_NAME=[Customers].[State Province], LEVEL_NUMBER=2, MEMBER_ORDINAL=8298, MEMBER_NAME=WA, MEMBER_UNIQUE_NAME=[Customers].[All Customers].[USA].[WA], MEMBER_TYPE=1, MEMBER_GUID=null, MEMBER_CAPTION=WA, CHILDREN_CARDINALITY=22, PARENT_LEVEL=1, PARENT_UNIQUE_NAME=[Customers].[All Customers].[USA], PARENT_COUNT=1, TREE_OP=null, DEPTH=2\n"
                    + "CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Customers], HIERARCHY_UNIQUE_NAME=[Customers], LEVEL_UNIQUE_NAME=[Customers].[Country], LEVEL_NUMBER=1, MEMBER_ORDINAL=2966, MEMBER_NAME=USA, MEMBER_UNIQUE_NAME=[Customers].[All Customers].[USA], MEMBER_TYPE=1, MEMBER_GUID=null, MEMBER_CAPTION=USA, CHILDREN_CARDINALITY=3, PARENT_LEVEL=0, PARENT_UNIQUE_NAME=[Customers].[All Customers], PARENT_COUNT=1, TREE_OP=null, DEPTH=1\n"
                    + "CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Customers], HIERARCHY_UNIQUE_NAME=[Customers], LEVEL_UNIQUE_NAME=[Customers].[(All)], LEVEL_NUMBER=0, MEMBER_ORDINAL=0, MEMBER_NAME=All Customers, MEMBER_UNIQUE_NAME=[Customers].[All Customers], MEMBER_TYPE=2, MEMBER_GUID=null, MEMBER_CAPTION=All Customers, CHILDREN_CARDINALITY=3, PARENT_LEVEL=0, PARENT_UNIQUE_NAME=null, PARENT_COUNT=0, TREE_OP=null, DEPTH=0\n"),
                s);
            break;
        default:
            assertEquals(
                TestContext.fold("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Customers], HIERARCHY_UNIQUE_NAME=[Customers], LEVEL_UNIQUE_NAME=[Customers].[(All)], LEVEL_NUMBER=0, MEMBER_ORDINAL=0, MEMBER_NAME=All Customers, MEMBER_UNIQUE_NAME=[Customers].[All Customers], MEMBER_TYPE=2, MEMBER_GUID=null, MEMBER_CAPTION=All Customers, CHILDREN_CARDINALITY=3, PARENT_LEVEL=0, PARENT_UNIQUE_NAME=null, PARENT_COUNT=0, TREE_OP=null, DEPTH=0\n"
                    + "CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Customers], HIERARCHY_UNIQUE_NAME=[Customers], LEVEL_UNIQUE_NAME=[Customers].[Country], LEVEL_NUMBER=1, MEMBER_ORDINAL=2966, MEMBER_NAME=USA, MEMBER_UNIQUE_NAME=[Customers].[All Customers].[USA], MEMBER_TYPE=1, MEMBER_GUID=null, MEMBER_CAPTION=USA, CHILDREN_CARDINALITY=3, PARENT_LEVEL=0, PARENT_UNIQUE_NAME=[Customers].[All Customers], PARENT_COUNT=1, TREE_OP=null, DEPTH=1\n"
                    + "CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Customers], HIERARCHY_UNIQUE_NAME=[Customers], LEVEL_UNIQUE_NAME=[Customers].[State Province], LEVEL_NUMBER=2, MEMBER_ORDINAL=7235, MEMBER_NAME=OR, MEMBER_UNIQUE_NAME=[Customers].[All Customers].[USA].[OR], MEMBER_TYPE=1, MEMBER_GUID=null, MEMBER_CAPTION=OR, CHILDREN_CARDINALITY=11, PARENT_LEVEL=1, PARENT_UNIQUE_NAME=[Customers].[All Customers].[USA], PARENT_COUNT=1, TREE_OP=null, DEPTH=2\n"
                    + "CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Sales, DIMENSION_UNIQUE_NAME=[Customers], HIERARCHY_UNIQUE_NAME=[Customers], LEVEL_UNIQUE_NAME=[Customers].[State Province], LEVEL_NUMBER=2, MEMBER_ORDINAL=8298, MEMBER_NAME=WA, MEMBER_UNIQUE_NAME=[Customers].[All Customers].[USA].[WA], MEMBER_TYPE=1, MEMBER_GUID=null, MEMBER_CAPTION=WA, CHILDREN_CARDINALITY=22, PARENT_LEVEL=1, PARENT_UNIQUE_NAME=[Customers].[All Customers].[USA], PARENT_COUNT=1, TREE_OP=null, DEPTH=2\n"),
                s);
            break;
        }
    }

    public void testDatabaseMetaDataGetSets() throws SQLException {
        String s = checkResultSet(
            olapDatabaseMetaData.getSets(
                catalogName, null, null, null),
            SETS_COLUMN_NAMES);
        assertEquals(TestContext.fold("CATALOG_NAME=" + catalogName + ", SCHEMA_NAME=FoodMart, CUBE_NAME=Warehouse, SET_NAME=[Top Sellers], SCOPE=1\n"), s);

        s = checkResultSet(
            olapDatabaseMetaData.getSets(
                catalogName, null, null, "non existent set"),
            SETS_COLUMN_NAMES);
        assertEquals("", s);
    }

    // todo: More tests required for other methods on DatabaseMetaData

    private String checkResultSet(
        ResultSet resultSet,
        List<String> columnNames) throws SQLException
    {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        final int columnCount = resultSetMetaData.getColumnCount();
        if (columnNames != null) {
            assertEquals(
                columnNames.size(),
                columnCount);
            int k = -1;
            for (String columnName : columnNames) {
                ++k;
                assertEquals(
                    columnName,
                    resultSetMetaData.getColumnName(k + 1));
            }
        }
        assertNotNull(resultSet);
        int k = 0;
        StringBuilder buf = new StringBuilder();
        while (resultSet.next()) {
            ++k;
            for (int i = 0; i < columnCount; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                String s = resultSet.getString(i + 1);
                buf.append(resultSetMetaData.getColumnName(i + 1))
                    .append('=')
                    .append(s);
            }
            buf.append(NL);
        }
        assertTrue(k >= 0);
        assertTrue(resultSet.isAfterLast());
        return buf.toString();
    }
}

// End MetadataTest.java
