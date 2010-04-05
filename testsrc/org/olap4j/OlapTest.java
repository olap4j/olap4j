/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.mdx.SelectNode;
import org.olap4j.metadata.*;
import org.olap4j.query.*;
import org.olap4j.query.QueryDimension.HierarchizeMode;
import org.olap4j.query.Selection.Operator;
import org.olap4j.test.TestContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Unit test illustrating sequence of calls to olap4j API from a graphical
 * client.
 *
 * @since  May 22, 2007
 * @author James Dixon
 * @version $Id$
 */
public class OlapTest extends TestCase {
    final TestContext.Tester tester = TestContext.instance().getTester();
    private Connection connection;

    public OlapTest() {
        super();
    }

    protected void tearDown() throws Exception {
        // Simple strategy to prevent connection leaks
        if (connection != null
            && !connection.isClosed())
        {
            connection.close();
            connection = null;
        }
    }

    public Cube getFoodmartCube(String cubeName) {
        try {
            connection = tester.createConnection();
            OlapConnection olapConnection =
                tester.getWrapper().unwrap(connection, OlapConnection.class);
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
            NamedList<Schema> schemas = catalog.getSchemas();
            if (schemas.size() == 0) {
                return null;
            }

            // Use the first schema
            Schema schema = schemas.get(0);

            // Get a list of cube objects and dump their names
            NamedList<Cube> cubes = schema.getCubes();

            if (cubes.size() == 0) {
                // no cubes where present
                return null;
            }

            // take the first cube
            return cubes.get(cubeName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void testModel() {
        try {
            if (false) {
            // define the connection information
            String schemaUri = "file:/open/mondrian/demo/FoodMart.xml";
            String schemaName = "FoodMart";
            String userName = "foodmartuser";
            String password = "foodmartpassword";
            String jdbc =
                "jdbc:mysql://localhost/foodmart?user=foodmartuser"
                + "&password=foodmartpassword";

            // Create a connection object to the specific implementation of an
            // olap4j source.  This is the only provider-specific code.
            Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
            connection = DriverManager.getConnection(
                "jdbc:mondrian:Jdbc=" + jdbc
                + ";User=" + userName
                + ";Password=" + password
                + ";Catalog=" + schemaUri);
            } else {
                connection = tester.createConnection();
            }
            OlapConnection olapConnection =
                tester.getWrapper().unwrap(connection, OlapConnection.class);

            // REVIEW: jhyde: Why do you want to name connections? We could add
            // a connect string property 'description', if that helps
//           connection.setName("First Connection");

            // The code from here on is generic olap4j stuff

            // Get a list of the schemas available from this connection and dump
            // their names.
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
            NamedList<Schema> schemas = catalog.getSchemas();

            if (schemas.size() == 0) {
                // No schemas were present
                return;
            }

            // Use the first schema
            Schema schema = schemas.get(0);

            // Get a list of cube objects and dump their names
            NamedList<Cube> cubes = schema.getCubes();

            if (cubes.size() == 0) {
                // no cubes where present
                return;
            }

            // take the "Sales" cube
            Cube cube = cubes.get("Sales");

            // The code from this point on is for the Foodmart schema

            // Create a new query
            Query query = new Query("my query", cube);

            QueryDimension productQuery = query.getDimension("Product");

            QueryDimension storeQuery = query.getDimension("Store");
            QueryDimension timeQuery =
                query.getDimension("Time"); //$NON-NLS-1$

            Member productMember = cube.lookupMember("Product", "Drink");

            // create some selections for Store
            storeQuery.include(
                    Selection.Operator.CHILDREN, "Store", "USA");

            // create some selections for Product
            productQuery.clearInclusions();
            productQuery.include(
                    Selection.Operator.CHILDREN, productMember);
            productQuery.include(
                    Selection.Operator.CHILDREN, "Product", "Food");

            // create some selections for Time
            timeQuery.include(Selection.Operator.CHILDREN, "Time", "1997");

            // place our dimensions on the axes
            query.getAxis(Axis.COLUMNS).addDimension(productQuery);
            assert productQuery.getAxis() == query.getAxis(Axis.COLUMNS);
            query.getAxis(Axis.ROWS).addDimension(storeQuery);
            query.getAxis(Axis.ROWS).addDimension(timeQuery);

            try {
                query.getAxis(Axis.ROWS).addDimension(storeQuery);
                fail("expected exception");
            } catch (Exception e) {
                assertTrue(
                    e.getMessage().contains("dimension already on this axis"));
            }

            query.validate();
            query.execute();

            // for shits and giggles we'll swap the axes over
            query.swapAxes();

            query.validate();
            query.execute();
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    public void testSelectionModes() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            Query query = new Query("my query", cube);

            // TEST CHILDREN SELECTION

            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                    Selection.Operator.CHILDREN, "Product", "Drink");

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Store Sales");

            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{[Product].[All Products].[Drink].Children} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // TEST ANCESTORS SELECTION

            productDimension.clearInclusions();
            productDimension.include(
                    Selection.Operator.ANCESTORS, "Product", "Drink");

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Ascendants([Product].[All Products].[Drink])} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // TEST DESCENDANTS SELECTION

            productDimension.clearInclusions();
            productDimension.include(
                    Selection.Operator.DESCENDANTS, "Product", "Drink");

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Descendants([Product].[All Products].[Drink])} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // TEST INCLUDE_CHILDREN SELECTION

            productDimension.clearInclusions();
            productDimension.include(
                    Selection.Operator.INCLUDE_CHILDREN, "Product", "Drink");

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{{[Product].[All Products].[Drink], [Product].[All Products].[Drink].Children}} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // TEST SIBLINGS SELECTION

            productDimension.clearInclusions();
            productDimension.include(
                    Selection.Operator.SIBLINGS, "Product", "Drink");

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{[Product].[All Products].[Drink].Siblings} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testMultipleDimensionSelections() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            Query query = new Query("my query", cube);

            // create selections

            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                    Selection.Operator.CHILDREN, "Product", "Drink");

            QueryDimension storeDimension = query.getDimension("Store");
            storeDimension.include(
                    Selection.Operator.INCLUDE_CHILDREN, "Store", "USA");

            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(Selection.Operator.CHILDREN, "Time", "1997");

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Store Sales");

            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.ROWS).addDimension(storeDimension);
            query.getAxis(Axis.ROWS).addDimension(timeDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "CrossJoin([Product].[All Products].[Drink].Children, "
                + "CrossJoin({[Store].[All Stores].[USA], "
                + "[Store].[All Stores].[USA].Children}, "
                + "[Time].[1997].Children)) ON ROWS\n"
                + "FROM [Sales]", mdxString);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testSwapAxes() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            Query query = new Query("my query", cube);

            // create selections

            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                    Selection.Operator.CHILDREN, "Product", "Drink");

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Store Sales");

            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            assertEquals(
                Axis.ROWS,
                productDimension.getAxis().getLocation());
            assertEquals(
                Axis.COLUMNS,
                measuresDimension.getAxis().getLocation());

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{[Product].[All Products].[Drink].Children} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            query.swapAxes();

            assertEquals(
                Axis.COLUMNS,
                productDimension.getAxis().getLocation());
            assertEquals(
                Axis.ROWS,
                measuresDimension.getAxis().getLocation());

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Product].[All Products].[Drink].Children} ON COLUMNS,\n"
                + "{[Measures].[Store Sales]} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            query.swapAxes();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testSortDimension() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            Query query = new Query("my query", cube);

            // create selections

            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                    Selection.Operator.INCLUDE_CHILDREN, "Product", "Drink");

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Store Sales");

            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include("Time", "Year", "1997", "Q3", "7");

            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);
            query.getAxis(Axis.FILTER).addDimension(timeDimension);

            query.validate();

            assertEquals(
                Axis.ROWS,
                productDimension.getAxis().getLocation());
            assertEquals(
                Axis.COLUMNS,
                measuresDimension.getAxis().getLocation());

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{{[Product].[All Products].[Drink], [Product].[All Products].[Drink].Children}} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE ([Time].[1997].[Q3].[7])",
                mdxString);

            // Sort the products in ascending order.
            query.getDimension("Product").sort(SortOrder.DESC);

            SelectNode sortedMdx = query.getSelect();
            String sortedMdxString = sortedMdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Order({{[Product].[All Products].[Drink], [Product].[All Products].[Drink].Children}}, [Product].CurrentMember.Name, DESC)} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE ([Time].[1997].[Q3].[7])",
                sortedMdxString);

            CellSet results = query.execute();
            String s = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Store].[All Stores], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time].[1997].[Q3].[7], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Store Sales]}\n"
                + "Axis #2:\n"
                + "{[Product].[All Products].[Drink]}\n"
                + "{[Product].[All Products].[Drink].[Dairy]}\n"
                + "{[Product].[All Products].[Drink].[Beverages]}\n"
                + "{[Product].[All Products].[Drink].[Alcoholic Beverages]}\n"
                + "Row #0: 4,409.58\n"
                + "Row #1: 629.69\n"
                + "Row #2: 2,477.02\n"
                + "Row #3: 1,302.87\n",
                s);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testSelectionContext() throws Exception {
        Cube cube = getFoodmartCube("Sales");
        if (cube == null) {
            fail("Could not find Sales cube");
        }
        Query query = new Query("my query", cube);

        // create selections
        QueryDimension productDimension = query.getDimension("Product");
        productDimension.include(
            Selection.Operator.INCLUDE_CHILDREN, "Product", "All Products");

        QueryDimension timeDimension = query.getDimension("Time");
        timeDimension.include(
            Selection.Operator.MEMBER, "Time", "Year", "1997");

        Selection selection =
            timeDimension.include(
                Selection.Operator.CHILDREN, "Time", "Year", "1997");
        selection.addContext(
            productDimension.createSelection(
                "Product", "All Products", "Drink"));

        // [Store].[All Stores]
        QueryDimension storeDimension = query.getDimension("Store");
        storeDimension.include(
            Selection.Operator.MEMBER, "Store", "All Stores");

        Selection children =
            storeDimension.include(
                Selection.Operator.CHILDREN, "Store", "All Stores");
        children.addContext(
            productDimension.createSelection(
                "Product", "All Products", "Drink"));
        children.addContext(
            timeDimension.createSelection("Time", "1997", "Q3"));

        QueryDimension measuresDimension = query.getDimension("Measures");
        measuresDimension.include("Measures", "Store Sales");

        query.getAxis(Axis.ROWS).addDimension(productDimension);
        query.getAxis(Axis.ROWS).addDimension(timeDimension);
        query.getAxis(Axis.ROWS).addDimension(storeDimension);

        query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

        query.validate();

        assertEquals(
            Axis.ROWS,
            productDimension.getAxis().getLocation());
        assertEquals(
            Axis.COLUMNS,
            measuresDimension.getAxis().getLocation());

        SelectNode mdx = query.getSelect();
        String mdxString = mdx.toString();
        TestContext.assertEqualsVerbose(
            "SELECT\n"
            + "{[Measures].[Store Sales]} ON COLUMNS,\n"
            + "Hierarchize(Union(CrossJoin({[Product].[All Products], [Product].[All Products].Children}, CrossJoin({[Time].[1997]}, {[Store].[All Stores]})), Union(CrossJoin({[Product].[All Products].[Drink]}, CrossJoin({[Time].[1997].[Q3]}, [Store].[All Stores].Children)), CrossJoin({[Product].[All Products].[Drink]}, CrossJoin([Time].[1997].Children, {[Store].[All Stores]}))))) ON ROWS\n"
            + "FROM [Sales]",
            mdxString);

        // Sort the rows in ascending order.
        query.getAxis(Axis.ROWS).sort(
            SortOrder.ASC,
            "Measures",
            "Store Sales");

        SelectNode sortedMdx = query.getSelect();
        String sortedMdxString = sortedMdx.toString();
        TestContext.assertEqualsVerbose(
            "SELECT\n"
            + "{[Measures].[Store Sales]} ON COLUMNS,\n"
            + "Order(Hierarchize(Union(CrossJoin({[Product].[All Products], [Product].[All Products].Children}, CrossJoin({[Time].[1997]}, {[Store].[All Stores]})), Union(CrossJoin({[Product].[All Products].[Drink]}, CrossJoin({[Time].[1997].[Q3]}, [Store].[All Stores].Children)), CrossJoin({[Product].[All Products].[Drink]}, CrossJoin([Time].[1997].Children, {[Store].[All Stores]}))))), [Measures].[Store Sales], ASC) ON ROWS\n"
            + "FROM [Sales]",
            sortedMdxString);

        CellSet results = query.execute();
        String s = TestContext.toString(results);
        TestContext.assertEqualsVerbose(
            "Axis #0:\n"
            + "{[Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n"
            + "Axis #1:\n"
            + "{[Measures].[Store Sales]}\n"
            + "Axis #2:\n"
            + "{[Product].[All Products], [Time].[1997], [Store].[All Stores]}\n"
            + "{[Product].[All Products].[Drink], [Time].[1997], [Store].[All Stores]}\n"
            + "{[Product].[All Products].[Drink], [Time].[1997].[Q1], [Store].[All Stores]}\n"
            + "{[Product].[All Products].[Drink], [Time].[1997].[Q2], [Store].[All Stores]}\n"
            + "{[Product].[All Products].[Drink], [Time].[1997].[Q3], [Store].[All Stores]}\n"
            + "{[Product].[All Products].[Drink], [Time].[1997].[Q3], [Store].[All Stores].[Canada]}\n"
            + "{[Product].[All Products].[Drink], [Time].[1997].[Q3], [Store].[All Stores].[Mexico]}\n"
            + "{[Product].[All Products].[Drink], [Time].[1997].[Q3], [Store].[All Stores].[USA]}\n"
            + "{[Product].[All Products].[Drink], [Time].[1997].[Q4], [Store].[All Stores]}\n"
            + "{[Product].[All Products].[Non-Consumable], [Time].[1997], [Store].[All Stores]}\n"
            + "{[Product].[All Products].[Food], [Time].[1997], [Store].[All Stores]}\n"
            + "Row #0: 565,238.13\n"
            + "Row #1: 48,836.21\n"
            + "Row #2: 11,585.80\n"
            + "Row #3: 11,914.58\n"
            + "Row #4: 11,994.00\n"
            + "Row #5: \n"
            + "Row #6: \n"
            + "Row #7: 11,994.00\n"
            + "Row #8: 13,341.83\n"
            + "Row #9: 107,366.33\n"
            + "Row #10: 409,035.59\n",
            s);
    }

    public void testSortAxis() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            Query query = new Query("my query", cube);

            // create selections

            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                    Selection.Operator.INCLUDE_CHILDREN, "Product", "Drink");

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Store Sales");

            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include("Time", "Year", "1997", "Q3", "7");

            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);
            query.getAxis(Axis.FILTER).addDimension(timeDimension);

            query.validate();

            assertEquals(
                Axis.ROWS,
                productDimension.getAxis().getLocation());
            assertEquals(
                Axis.COLUMNS,
                measuresDimension.getAxis().getLocation());

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{{[Product].[All Products].[Drink], [Product].[All Products].[Drink].Children}} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE ([Time].[1997].[Q3].[7])",
                mdxString);

            // Sort the rows in ascending order.
            query.getAxis(Axis.ROWS).sort(
                SortOrder.BASC,
                "Measures",
                "Store Sales");

            SelectNode sortedMdx = query.getSelect();
            String sortedMdxString = sortedMdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "Order({{[Product].[All Products].[Drink], [Product].[All Products].[Drink].Children}}, [Measures].[Store Sales], BASC) ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE ([Time].[1997].[Q3].[7])",
                sortedMdxString);

            CellSet results = query.execute();
            String s = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Store].[All Stores], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time].[1997].[Q3].[7], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Store Sales]}\n"
                + "Axis #2:\n"
                + "{[Product].[All Products].[Drink].[Dairy]}\n"
                + "{[Product].[All Products].[Drink].[Alcoholic Beverages]}\n"
                + "{[Product].[All Products].[Drink].[Beverages]}\n"
                + "{[Product].[All Products].[Drink]}\n"
                + "Row #0: 629.69\n"
                + "Row #1: 1,302.87\n"
                + "Row #2: 2,477.02\n"
                + "Row #3: 4,409.58\n",
                s);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDimensionsOrder() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            Query query = new Query("my query", cube);

            // create selections

            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                    Selection.Operator.CHILDREN, "Product", "Drink");

            QueryDimension storeDimension = query.getDimension("Store");
            storeDimension.include(
                    Selection.Operator.INCLUDE_CHILDREN, "Store", "USA");

            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(Selection.Operator.CHILDREN, "Time", "1997");

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Store Sales");


            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.ROWS).addDimension(storeDimension);
            query.getAxis(Axis.ROWS).addDimension(timeDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "CrossJoin([Product].[All Products].[Drink].Children, "
                + "CrossJoin({[Store].[All Stores].[USA], "
                + "[Store].[All Stores].[USA].Children}, "
                + "[Time].[1997].Children)) ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // Push down the Products dimension.
            query.getAxis(Axis.ROWS).pushDown(0);

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "CrossJoin({[Store].[All Stores].[USA], "
                + "[Store].[All Stores].[USA].Children}, "
                + "CrossJoin([Product].[All Products].[Drink].Children, "
                + "[Time].[1997].Children)) ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // Pull Up the Time dimension.
            query.getAxis(Axis.ROWS).pullUp(2);

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "CrossJoin({[Store].[All Stores].[USA], "
                + "[Store].[All Stores].[USA].Children}, "
                + "CrossJoin([Time].[1997].Children, "
                + "[Product].[All Products].[Drink].Children)) ON ROWS\n"
                + "FROM [Sales]",
                mdxString);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Note: hierarchize mode only works when a single dimension is selected.
     */
    public void testDimensionsHierarchize() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            Query query = new Query("my query", cube);

            // create selections

            QueryDimension storeDimension = query.getDimension("Store");
            storeDimension.include(
                    Selection.Operator.INCLUDE_CHILDREN, "Store", "USA");
            storeDimension.setHierarchizeMode(HierarchizeMode.POST);

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Store Sales");

            query.getAxis(Axis.ROWS).addDimension(storeDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Hierarchize({{[Store].[All Stores].[USA], "
                + "[Store].[All Stores].[USA].Children}}, POST)} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            storeDimension.setHierarchizeMode(HierarchizeMode.PRE);

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Hierarchize({{[Store].[All Stores].[USA], "
                + "[Store].[All Stores].[USA].Children}})} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * This test makes sure that the generated MDX model is not affected
     * by subsequent operations performed on the source query model.
     */
    public void testQueryVersusParseTreeIndependence() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }

            // Setup a base query.
            Query query = new Query("my query", cube);

            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                    Selection.Operator.INCLUDE_CHILDREN, "Product", "Drink");

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Store Sales");

            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            assertEquals(
                Axis.ROWS,
                productDimension.getAxis().getLocation());
            assertEquals(
                Axis.COLUMNS,
                measuresDimension.getAxis().getLocation());

            // These variables are important. We will evaluate those
            // to decide if there are links between the MDX and the Query
            SelectNode originalMdx = query.getSelect();
            String originalMdxString = originalMdx.toString();

            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{{[Product].[All Products].[Drink], [Product].[All Products].[Drink].Children}} ON ROWS\n"
                + "FROM [Sales]",
                originalMdxString);

            // change selections
            measuresDimension.include(
                Selection.Operator.SIBLINGS, "Measures", "Customer Count");
            productDimension.include(
                Selection.Operator.SIBLINGS,
                "Product", "All Products", "Drink", "Alcoholic Beverages");

            // Add something to crossjoin with
            query.getAxis(Axis.ROWS).addDimension(
                query.getDimension("Gender"));
            query.getDimension("Gender").include(
                Operator.CHILDREN,
                "Gender",
                "All Gender");

            query.getAxis(Axis.UNUSED).addDimension(
                query.getDimension("Product"));

            query.validate();

            // Check if the MDX object tree is still the same
            assertEquals(originalMdxString, originalMdx.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testExclusionModes() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }

            // Setup a base query.
            Query query = new Query("my query", cube);
            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                Selection.Operator.CHILDREN,
                "Product", "Drink",
                "Beverages");
            productDimension.include(
                Selection.Operator.CHILDREN,
                "Product",
                "Food",
                "Frozen Foods");
            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Sales Count");
            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include("Time", "Year", "1997", "Q3", "7");
            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);
            query.getAxis(Axis.FILTER).addDimension(timeDimension);
            query.validate();

            // Validate the generated MDX
            String mdxString = query.getSelect().toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Sales Count]} ON COLUMNS,\n"
                + "{[Product].[All Products].[Drink].[Beverages].Children, [Product].[All Products].[Food].[Frozen Foods].Children} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE ([Time].[1997].[Q3].[7])",
                mdxString);

            // Validate the returned results
            CellSet results = query.execute();
            String resultsString = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Store].[All Stores], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time].[1997].[Q3].[7], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Sales Count]}\n"
                + "Axis #2:\n"
                + "{[Product].[All Products].[Drink].[Beverages].[Carbonated Beverages]}\n"
                + "{[Product].[All Products].[Drink].[Beverages].[Drinks]}\n"
                + "{[Product].[All Products].[Drink].[Beverages].[Hot Beverages]}\n"
                + "{[Product].[All Products].[Drink].[Beverages].[Pure Juice Beverages]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Breakfast Foods]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Frozen Desserts]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Frozen Entrees]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Meat]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Pizza]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Vegetables]}\n"
                + "Row #0: 103\n"
                + "Row #1: 65\n"
                + "Row #2: 125\n"
                + "Row #3: 100\n"
                + "Row #4: 143\n"
                + "Row #5: 185\n"
                + "Row #6: 68\n"
                + "Row #7: 81\n"
                + "Row #8: 105\n"
                + "Row #9: 212\n",
                resultsString);

            // Exclude the Carbonated Beverages because they are not good
            // for your health.
            query.getDimension("Product").exclude(
                "Product",
                "Drink",
                "Beverages",
                "Carbonated Beverages");

            // Validate the generated MDX
            query.validate();
            mdxString = query.getSelect().toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Sales Count]} ON COLUMNS,\n"
                + "{Except({[Product].[All Products].[Drink].[Beverages].Children, [Product].[All Products].[Food].[Frozen Foods].Children}, {[Product].[All Products].[Drink].[Beverages].[Carbonated Beverages]})} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE ([Time].[1997].[Q3].[7])",
                mdxString);

            // Validate the returned results
            results = query.execute();
            resultsString = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Store].[All Stores], [Store Size in SQFT].[All Store Size in SQFTs], [Store Type].[All Store Types], [Time].[1997].[Q3].[7], [Promotion Media].[All Media], [Promotions].[All Promotions], [Customers].[All Customers], [Education Level].[All Education Levels], [Gender].[All Gender], [Marital Status].[All Marital Status], [Yearly Income].[All Yearly Incomes]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Sales Count]}\n"
                + "Axis #2:\n"
                + "{[Product].[All Products].[Drink].[Beverages].[Drinks]}\n"
                + "{[Product].[All Products].[Drink].[Beverages].[Hot Beverages]}\n"
                + "{[Product].[All Products].[Drink].[Beverages].[Pure Juice Beverages]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Breakfast Foods]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Frozen Desserts]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Frozen Entrees]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Meat]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Pizza]}\n"
                + "{[Product].[All Products].[Food].[Frozen Foods].[Vegetables]}\n"
                + "Row #0: 65\n"
                + "Row #1: 125\n"
                + "Row #2: 100\n"
                + "Row #3: 143\n"
                + "Row #4: 185\n"
                + "Row #5: 68\n"
                + "Row #6: 81\n"
                + "Row #7: 105\n"
                + "Row #8: 212\n",
                resultsString);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testNonMandatoryQueryAxis() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            Query query = new Query("my query", cube);

            // create selections

            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                    Selection.Operator.CHILDREN, "Product", "Drink");

            QueryDimension storeDimension = query.getDimension("Store");
            storeDimension.include(
                    Selection.Operator.INCLUDE_CHILDREN, "Store", "USA");
            storeDimension.setHierarchizeMode(HierarchizeMode.POST);

            QueryDimension timeDimension = query.getDimension("Time");

            timeDimension.include(Selection.Operator.CHILDREN, "Time", "1997");

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include("Measures", "Store Sales");


            //query.getAxis(Axis.ROWS).addDimension(productDimension);
            //query.getAxis(Axis.ROWS).addDimension(storeDimension);
            //query.getAxis(Axis.ROWS).addDimension(timeDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            //query.validate();

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS\n"
                + "FROM [Sales]",
                mdxString);

            try {
                query.validate();
                fail();
            } catch (OlapException e) {
                assertEquals(0, e.getCause().getMessage().indexOf(
                    "A valid Query requires at least one "
                    + "dimension on the rows axis."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public static void main(String args[]) {
        OlapTest olapTest = new OlapTest();

        olapTest.testModel();
    }
}

// End OlapTest.java
