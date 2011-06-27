/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.impl.Bug;
import org.olap4j.mdx.SelectNode;
import org.olap4j.metadata.*;
import org.olap4j.query.*;
import org.olap4j.query.QueryDimension.HierarchizeMode;
import org.olap4j.query.Selection.Operator;
import org.olap4j.test.TestContext;
import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

import static org.olap4j.test.TestContext.nameList;

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
            Catalog catalog = olapConnection.getOlapCatalogs().get("FoodMart");
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
            Catalog catalog = olapConnection.getOlapCatalogs().get("FoodMart");
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
            QueryDimension timeQuery = query.getDimension("Time");

            Member productMember =
                cube.lookupMember(nameList("Product", "Drink"));

            // create some selections for Store
            storeQuery.include(
                Selection.Operator.CHILDREN, nameList("Store", "USA"));

            // create some selections for Product
            productQuery.clearInclusions();
            productQuery.include(
                Selection.Operator.CHILDREN, productMember);
            productQuery.include(
                Selection.Operator.CHILDREN, nameList("Product", "Food"));

            // create some selections for Time
            timeQuery.include(
                Selection.Operator.CHILDREN, nameList("Time", "1997"));

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
                Selection.Operator.CHILDREN, nameList("Product", "Drink"));

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));

            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{[Product].[Drink].Children} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // TEST ANCESTORS SELECTION

            productDimension.clearInclusions();
            productDimension.include(
                Selection.Operator.ANCESTORS, nameList("Product", "Drink"));

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Ascendants([Product].[Drink])} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // TEST DESCENDANTS SELECTION

            productDimension.clearInclusions();
            productDimension.include(
                Selection.Operator.DESCENDANTS, nameList("Product", "Drink"));

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Descendants([Product].[Drink])} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // TEST INCLUDE_CHILDREN SELECTION

            productDimension.clearInclusions();
            productDimension.include(
                Selection.Operator.INCLUDE_CHILDREN,
                nameList("Product", "Drink"));

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{{[Product].[Drink], [Product].[Drink].Children}} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // TEST SIBLINGS SELECTION

            productDimension.clearInclusions();
            productDimension.include(
                Selection.Operator.SIBLINGS, nameList("Product", "Drink"));

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{[Product].[Drink].Siblings} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // TEST LEVEL MEMBERS SELECTION

            NamedList<Level> productLevels =
             productDimension.getDimension().getDefaultHierarchy().getLevels();

            Level productDepartments = productLevels.get("Product Department");
            productDimension.include(productDepartments);
            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{[Product].[Drink].Siblings, [Product].[Product Department].Members} ON ROWS\n"
                + "FROM [Sales]", mdxString);
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
                Selection.Operator.CHILDREN, nameList("Product", "Drink"));

            QueryDimension storeDimension = query.getDimension("Store");
            storeDimension.include(
                Selection.Operator.INCLUDE_CHILDREN, nameList("Store", "USA"));

            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(
                Selection.Operator.CHILDREN, nameList("Time", "1997"));

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));

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
                + "CrossJoin([Product].[Drink].Children, "
                + "CrossJoin({[Store].[USA], "
                + "[Store].[USA].Children}, "
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
                Selection.Operator.CHILDREN, nameList("Product", "Drink"));

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));

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
                + "{[Product].[Drink].Children} ON ROWS\n"
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
                + "{[Product].[Drink].Children} ON COLUMNS,\n"
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
                Selection.Operator.INCLUDE_CHILDREN,
                nameList("Product", "Drink"));

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));

            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(nameList("Time", "Year", "1997", "Q3", "7"));

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
                + "{{[Product].[Drink], [Product].[Drink].Children}} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE {[Time].[1997].[Q3].[7]}",
                mdxString);

            // Sort the products in ascending order.
            query.getDimension("Product").sort(SortOrder.DESC);

            SelectNode sortedMdx = query.getSelect();
            String sortedMdxString = sortedMdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Order({{[Product].[Drink], [Product].[Drink].Children}}, [Product].CurrentMember.Name, DESC)} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE {[Time].[1997].[Q3].[7]}",
                sortedMdxString);

            CellSet results = query.execute();
            String s = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Time].[1997].[Q3].[7]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Store Sales]}\n"
                + "Axis #2:\n"
                + "{[Product].[Drink]}\n"
                + "{[Product].[Drink].[Dairy]}\n"
                + "{[Product].[Drink].[Beverages]}\n"
                + "{[Product].[Drink].[Alcoholic Beverages]}\n"
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

    public void testSortMultipleDimension() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            Query query = new Query("my query", cube);

            // create selections

            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                Selection.Operator.CHILDREN, nameList("Product", "Drink"));

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));

            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(nameList("Time", "Year", "1997", "Q3", "7"));

            query.getAxis(Axis.ROWS).addDimension(timeDimension);
            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            assertEquals(
                Axis.ROWS,
                timeDimension.getAxis().getLocation());
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
                + "CrossJoin({[Time].[1997].[Q3].[7]}, [Product].[Drink].Children) ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // Sort the products in ascending order.
            query.getDimension("Product").sort(SortOrder.DESC);

            SelectNode sortedMdx = query.getSelect();
            String sortedMdxString = sortedMdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "CrossJoin({[Time].[1997].[Q3].[7]}, Order({[Product].[Drink].Children}, [Product].CurrentMember.Name, DESC)) ON ROWS\n"
                + "FROM [Sales]",
                sortedMdxString);

            CellSet results = query.execute();
            String s = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{}\n"
                + "Axis #1:\n"
                + "{[Measures].[Store Sales]}\n"
                + "Axis #2:\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Drink].[Dairy]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Drink].[Beverages]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Drink].[Alcoholic Beverages]}\n"
                + "Row #0: 629.69\n"
                + "Row #1: 2,477.02\n"
                + "Row #2: 1,302.87\n",
                s);

            // Just to be sure we execute the sort on a NON EMPTY axis again
            query.getAxis(Axis.ROWS).setNonEmpty(true);
            productDimension.clearInclusions();
            productDimension.include(
                Selection.Operator.CHILDREN, nameList("Product", "Food"));

            SelectNode sortedMdxNonEmpty = query.getSelect();
            String sortedMdxNonEmptyString = sortedMdxNonEmpty.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "NON EMPTY CrossJoin({[Time].[1997].[Q3].[7]}, Order({[Product].[Food].Children}, [Product].CurrentMember.Name, DESC)) ON ROWS\n"
                + "FROM [Sales]",
                sortedMdxNonEmptyString);

            CellSet results2 = query.execute();
            String s2 = TestContext.toString(results2);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{}\n"
                + "Axis #1:\n"
                + "{[Measures].[Store Sales]}\n"
                + "Axis #2:\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Starchy Foods]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Snacks]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Snack Foods]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Seafood]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Produce]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Meat]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Frozen Foods]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Eggs]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Deli]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Dairy]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Canned Products]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Canned Foods]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Breakfast Foods]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Baking Goods]}\n"
                + "{[Time].[1997].[Q3].[7], [Product].[Food].[Baked Goods]}\n"
                + "Row #0: 1,059.06\n"
                + "Row #1: 1,248.92\n"
                + "Row #2: 6,342.01\n"
                + "Row #3: 383.20\n"
                + "Row #4: 7,084.85\n"
                + "Row #5: 304.61\n"
                + "Row #6: 5,027.30\n"
                + "Row #7: 930.70\n"
                + "Row #8: 2,222.69\n"
                + "Row #9: 2,896.81\n"
                + "Row #10: 250.84\n"
                + "Row #11: 3,301.38\n"
                + "Row #12: 551.95\n"
                + "Row #13: 3,232.70\n"
                + "Row #14: 1,487.74\n",
                s2);
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
            Selection.Operator.INCLUDE_CHILDREN,
            nameList("Product", "All Products"));

        QueryDimension timeDimension = query.getDimension("Time");
        timeDimension.include(
            Selection.Operator.MEMBER, nameList("Time", "Year", "1997"));

        Selection selection =
            timeDimension.include(
                Selection.Operator.CHILDREN, nameList("Time", "Year", "1997"));
        selection.addContext(
            productDimension.createSelection(
                nameList("Product", "All Products", "Drink")));

        // [Store].[All Stores]
        QueryDimension storeDimension = query.getDimension("Store");
        storeDimension.include(
            Selection.Operator.MEMBER, nameList("Store", "All Stores"));

        Selection children =
            storeDimension.include(
                Selection.Operator.CHILDREN, nameList("Store", "All Stores"));
        children.addContext(
            productDimension.createSelection(
                nameList("Product", "All Products", "Drink")));
        children.addContext(
            timeDimension.createSelection(nameList("Time", "1997", "Q3")));

        QueryDimension measuresDimension = query.getDimension("Measures");
        measuresDimension.include(nameList("Measures", "Store Sales"));

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
            + "Hierarchize(Union(CrossJoin({[Product].[All Products], [Product].[All Products].Children}, CrossJoin({[Time].[1997]}, {[Store].[All Stores]})), Union(CrossJoin({[Product].[Drink]}, CrossJoin({[Time].[1997].[Q3]}, [Store].[All Stores].Children)), CrossJoin({[Product].[Drink]}, CrossJoin([Time].[1997].Children, {[Store].[All Stores]}))))) ON ROWS\n"
            + "FROM [Sales]",
            mdxString);

        // Sort the rows in ascending order.
        query.getAxis(Axis.ROWS).sort(
            SortOrder.ASC,
            nameList("Measures", "Store Sales"));

        SelectNode sortedMdx = query.getSelect();
        String sortedMdxString = sortedMdx.toString();
        TestContext.assertEqualsVerbose(
            "SELECT\n"
            + "{[Measures].[Store Sales]} ON COLUMNS,\n"
            + "Order(Hierarchize(Union(CrossJoin({[Product].[All Products], [Product].[All Products].Children}, CrossJoin({[Time].[1997]}, {[Store].[All Stores]})), Union(CrossJoin({[Product].[Drink]}, CrossJoin({[Time].[1997].[Q3]}, [Store].[All Stores].Children)), CrossJoin({[Product].[Drink]}, CrossJoin([Time].[1997].Children, {[Store].[All Stores]}))))), [Measures].[Store Sales], ASC) ON ROWS\n"
            + "FROM [Sales]",
            sortedMdxString);

        CellSet results = query.execute();
        String s = TestContext.toString(results);
        TestContext.assertEqualsVerbose(
            "Axis #0:\n"
            + "{}\n"
            + "Axis #1:\n"
            + "{[Measures].[Store Sales]}\n"
            + "Axis #2:\n"
            + "{[Product].[All Products], [Time].[1997], [Store].[All Stores]}\n"
            + "{[Product].[Drink], [Time].[1997], [Store].[All Stores]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q1], [Store].[All Stores]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q2], [Store].[All Stores]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q3], [Store].[All Stores]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q3], [Store].[Canada]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q3], [Store].[Mexico]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q3], [Store].[USA]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q4], [Store].[All Stores]}\n"
            + "{[Product].[Non-Consumable], [Time].[1997], [Store].[All Stores]}\n"
            + "{[Product].[Food], [Time].[1997], [Store].[All Stores]}\n"
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

    public void testComplexSelectionContext() throws Exception {
        Cube cube = getFoodmartCube("Sales");
        if (cube == null) {
            fail("Could not find Sales cube");
        }
        Query query = new Query("my query", cube);

        // create selections
        QueryDimension productDimension = query.getDimension("Product");


        productDimension.include(
            Selection.Operator.MEMBER, nameList("Product", "All Products"));
        productDimension.include(
            Selection.Operator.CHILDREN, nameList("Product", "All Products"));
        QueryDimension timeDimension = query.getDimension("Time");
        Selection selection = timeDimension.include(
            Selection.Operator.CHILDREN, nameList("Time", "Year", "1997"));
        selection.addContext(
            productDimension.createSelection(
                nameList("Product", "All Products")));

        QueryDimension measuresDimension = query.getDimension("Measures");
        measuresDimension.include(nameList("Measures", "Store Sales"));

        query.getAxis(Axis.ROWS).addDimension(productDimension);
        query.getAxis(Axis.ROWS).addDimension(timeDimension);

        query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

        query.validate();

        assertEquals(
            Axis.ROWS,
            productDimension.getAxis().getLocation());
        assertEquals(
            Axis.ROWS,
            timeDimension.getAxis().getLocation());
        assertEquals(
            Axis.COLUMNS,
            measuresDimension.getAxis().getLocation());

        if (!Bug.BugOlap4j3106220Fixed) {
            return;
        }

        SelectNode mdx = query.getSelect();
        String mdxString = mdx.toString();

        TestContext.assertEqualsVerbose(
            "SELECT\n"
            + "{[Measures].[Store Sales]} ON COLUMNS,\n"
            + "Hierarchize(Union(CrossJoin({[Product].[All Products]}, [Time].[1997].Children), CrossJoin([Product].[All Products].Children, [Time].[1997].Children))) ON ROWS\n"
            + "FROM [Sales]",
            mdxString);

        CellSet results = query.execute();
        String s = TestContext.toString(results);
        TestContext.assertEqualsVerbose(
            "Axis #0:\n"
            + "{}\n"
            + "Axis #1:\n"
            + "{[Measures].[Store Sales]}\n"
            + "Axis #2:\n"
            + "{[Product].[All Products], [Time].[1997].[Q1]}\n"
            + "{[Product].[All Products], [Time].[1997].[Q2]}\n"
            + "{[Product].[All Products], [Time].[1997].[Q3]}\n"
            + "{[Product].[All Products], [Time].[1997].[Q4]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q1]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q2]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q3]}\n"
            + "{[Product].[Drink], [Time].[1997].[Q4]}\n"
            + "{[Product].[Food], [Time].[1997].[Q1]}\n"
            + "{[Product].[Food], [Time].[1997].[Q2]}\n"
            + "{[Product].[Food], [Time].[1997].[Q3]}\n"
            + "{[Product].[Food], [Time].[1997].[Q4]}\n"
            + "{[Product].[Non-Consumable], [Time].[1997].[Q1]}\n"
            + "{[Product].[Non-Consumable], [Time].[1997].[Q2]}\n"
            + "{[Product].[Non-Consumable], [Time].[1997].[Q3]}\n"
            + "{[Product].[Non-Consumable], [Time].[1997].[Q4]}\n"
            + "Row #0: 139,628.35\n"
            + "Row #1: 132,666.27\n"
            + "Row #2: 140,271.89\n"
            + "Row #3: 152,671.62\n"
            + "Row #4: 11,585.80\n"
            + "Row #5: 11,914.58\n"
            + "Row #6: 11,994.00\n"
            + "Row #7: 13,341.83\n"
            + "Row #8: 101,261.32\n"
            + "Row #9: 95,436.00\n"
            + "Row #10: 101,807.60\n"
            + "Row #11: 110,530.67\n"
            + "Row #12: 26,781.23\n"
            + "Row #13: 25,315.69\n"
            + "Row #14: 26,470.29\n"
            + "Row #15: 28,799.12\n",
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
                Selection.Operator.INCLUDE_CHILDREN,
                nameList("Product", "Drink"));

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));

            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(nameList("Time", "Year", "1997", "Q3", "7"));

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
                + "{{[Product].[Drink], [Product].[Drink].Children}} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE {[Time].[1997].[Q3].[7]}",
                mdxString);

            // Sort the rows in ascending order.
            query.getAxis(Axis.ROWS).sort(
                SortOrder.BASC,
                nameList("Measures", "Store Sales"));

            SelectNode sortedMdx = query.getSelect();
            String sortedMdxString = sortedMdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "Order({{[Product].[Drink], [Product].[Drink].Children}}, [Measures].[Store Sales], BASC) ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE {[Time].[1997].[Q3].[7]}",
                sortedMdxString);

            CellSet results = query.execute();
            String s = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Time].[1997].[Q3].[7]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Store Sales]}\n"
                + "Axis #2:\n"
                + "{[Product].[Drink].[Dairy]}\n"
                + "{[Product].[Drink].[Alcoholic Beverages]}\n"
                + "{[Product].[Drink].[Beverages]}\n"
                + "{[Product].[Drink]}\n"
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
                Selection.Operator.CHILDREN, nameList("Product", "Drink"));

            QueryDimension storeDimension = query.getDimension("Store");
            storeDimension.include(
                Selection.Operator.INCLUDE_CHILDREN, nameList("Store", "USA"));

            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(
                Selection.Operator.CHILDREN, nameList("Time", "1997"));

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));


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
                + "CrossJoin([Product].[Drink].Children, "
                + "CrossJoin({[Store].[USA], "
                + "[Store].[USA].Children}, "
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
                + "CrossJoin({[Store].[USA], "
                + "[Store].[USA].Children}, "
                + "CrossJoin([Product].[Drink].Children, "
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
                + "CrossJoin({[Store].[USA], "
                + "[Store].[USA].Children}, "
                + "CrossJoin([Time].[1997].Children, "
                + "[Product].[Drink].Children)) ON ROWS\n"
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
                Selection.Operator.INCLUDE_CHILDREN, nameList("Store", "USA"));
            storeDimension.setHierarchizeMode(HierarchizeMode.POST);

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));

            query.getAxis(Axis.ROWS).addDimension(storeDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            SelectNode mdx = query.getSelect();
            String mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Hierarchize({{[Store].[USA], "
                + "[Store].[USA].Children}}, POST)} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            storeDimension.setHierarchizeMode(HierarchizeMode.PRE);

            query.validate();

            mdx = query.getSelect();
            mdxString = mdx.toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Store Sales]} ON COLUMNS,\n"
                + "{Hierarchize({{[Store].[USA], "
                + "[Store].[USA].Children}})} ON ROWS\n"
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
                Selection.Operator.INCLUDE_CHILDREN,
                nameList("Product", "Drink"));

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));

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
                + "{{[Product].[Drink], [Product].[Drink].Children}} ON ROWS\n"
                + "FROM [Sales]",
                originalMdxString);

            // change selections
            measuresDimension.include(
                Selection.Operator.SIBLINGS,
                nameList("Measures", "Customer Count"));
            productDimension.include(
                Selection.Operator.SIBLINGS,
                nameList(
                    "Product", "All Products", "Drink", "Alcoholic Beverages"));

            // Add something to crossjoin with
            query.getAxis(Axis.ROWS).addDimension(
                query.getDimension("Gender"));
            query.getDimension("Gender").include(
                Operator.CHILDREN, nameList("Gender", "All Gender"));

            query.getAxis(null).addDimension(
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
                nameList("Product", "Drink", "Beverages"));
            productDimension.include(
                Selection.Operator.CHILDREN,
                nameList("Product", "Food", "Frozen Foods"));
            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Sales Count"));
            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(nameList("Time", "Year", "1997", "Q3", "7"));
            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);
            query.getAxis(Axis.FILTER).addDimension(timeDimension);
            query.validate();

            // Validate the generated MDX
            String mdxString = query.getSelect().toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Sales Count]} ON COLUMNS,\n"
                + "{[Product].[Drink].[Beverages].Children, [Product].[Food].[Frozen Foods].Children} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE {[Time].[1997].[Q3].[7]}",
                mdxString);

            // Validate the returned results
            CellSet results = query.execute();
            String resultsString = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Time].[1997].[Q3].[7]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Sales Count]}\n"
                + "Axis #2:\n"
                + "{[Product].[Drink].[Beverages].[Carbonated Beverages]}\n"
                + "{[Product].[Drink].[Beverages].[Drinks]}\n"
                + "{[Product].[Drink].[Beverages].[Hot Beverages]}\n"
                + "{[Product].[Drink].[Beverages].[Pure Juice Beverages]}\n"
                + "{[Product].[Food].[Frozen Foods].[Breakfast Foods]}\n"
                + "{[Product].[Food].[Frozen Foods].[Frozen Desserts]}\n"
                + "{[Product].[Food].[Frozen Foods].[Frozen Entrees]}\n"
                + "{[Product].[Food].[Frozen Foods].[Meat]}\n"
                + "{[Product].[Food].[Frozen Foods].[Pizza]}\n"
                + "{[Product].[Food].[Frozen Foods].[Vegetables]}\n"
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
                nameList(
                    "Product", "Drink", "Beverages", "Carbonated Beverages"));

            // Validate the generated MDX
            query.validate();
            mdxString = query.getSelect().toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Sales Count]} ON COLUMNS,\n"
                + "{Except({[Product].[Drink].[Beverages].Children, [Product].[Food].[Frozen Foods].Children}, {[Product].[Drink].[Beverages].[Carbonated Beverages]})} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE {[Time].[1997].[Q3].[7]}",
                mdxString);

            // Validate the returned results
            results = query.execute();
            resultsString = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Time].[1997].[Q3].[7]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Sales Count]}\n"
                + "Axis #2:\n"
                + "{[Product].[Drink].[Beverages].[Drinks]}\n"
                + "{[Product].[Drink].[Beverages].[Hot Beverages]}\n"
                + "{[Product].[Drink].[Beverages].[Pure Juice Beverages]}\n"
                + "{[Product].[Food].[Frozen Foods].[Breakfast Foods]}\n"
                + "{[Product].[Food].[Frozen Foods].[Frozen Desserts]}\n"
                + "{[Product].[Food].[Frozen Foods].[Frozen Entrees]}\n"
                + "{[Product].[Food].[Frozen Foods].[Meat]}\n"
                + "{[Product].[Food].[Frozen Foods].[Pizza]}\n"
                + "{[Product].[Food].[Frozen Foods].[Vegetables]}\n"
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

    public void testExclusionMultipleDimensionModes() {
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
                nameList("Product", "Drink", "Beverages"));
            productDimension.include(
                Selection.Operator.CHILDREN,
                nameList("Product", "Food", "Frozen Foods"));
            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Sales Count"));
            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(nameList("Time", "Year", "1997", "Q3", "7"));
            QueryDimension storeDimension = query.getDimension("Store");
            storeDimension.include(
                Selection.Operator.MEMBER, nameList("Store", "USA"));
            query.getAxis(Axis.ROWS).addDimension(storeDimension);
            query.getAxis(Axis.ROWS).addDimension(productDimension);
            query.getAxis(Axis.FILTER).addDimension(timeDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            // Validate the generated MDX
            String mdxString = query.getSelect().toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Sales Count]} ON COLUMNS,\n"
                + "Hierarchize(Union(CrossJoin({[Store].[USA]}, [Product].[Drink].[Beverages].Children), CrossJoin({[Store].[USA]}, [Product].[Food].[Frozen Foods].Children))) ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE {[Time].[1997].[Q3].[7]}",
                mdxString);

            // Validate the returned results
            CellSet results = query.execute();
            String resultsString = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Time].[1997].[Q3].[7]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Sales Count]}\n"
                + "Axis #2:\n"
                + "{[Store].[USA], [Product].[Drink].[Beverages].[Carbonated Beverages]}\n"
                + "{[Store].[USA], [Product].[Drink].[Beverages].[Drinks]}\n"
                + "{[Store].[USA], [Product].[Drink].[Beverages].[Hot Beverages]}\n"
                + "{[Store].[USA], [Product].[Drink].[Beverages].[Pure Juice Beverages]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Breakfast Foods]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Frozen Desserts]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Frozen Entrees]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Meat]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Pizza]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Vegetables]}\n"
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
                nameList(
                    "Product", "Drink", "Beverages", "Carbonated Beverages"));

            // Validate the generated MDX
            query.validate();
            mdxString = query.getSelect().toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Sales Count]} ON COLUMNS,\n"
                + "Hierarchize(Union(CrossJoin({[Store].[USA]}, Except({[Product].[Drink].[Beverages].Children}, {[Product].[Drink].[Beverages].[Carbonated Beverages]})), CrossJoin({[Store].[USA]}, Except({[Product].[Food].[Frozen Foods].Children}, {[Product].[Drink].[Beverages].[Carbonated Beverages]})))) ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE {[Time].[1997].[Q3].[7]}",
                mdxString);

            // Validate the returned results
            results = query.execute();
            resultsString = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Time].[1997].[Q3].[7]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Sales Count]}\n"
                + "Axis #2:\n"
                + "{[Store].[USA], [Product].[Drink].[Beverages].[Drinks]}\n"
                + "{[Store].[USA], [Product].[Drink].[Beverages].[Hot Beverages]}\n"
                + "{[Store].[USA], [Product].[Drink].[Beverages].[Pure Juice Beverages]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Breakfast Foods]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Frozen Desserts]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Frozen Entrees]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Meat]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Pizza]}\n"
                + "{[Store].[USA], [Product].[Food].[Frozen Foods].[Vegetables]}\n"
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

    public void testCompoundFilter() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }

            // Setup a base query.
            Query query = new Query("my query", cube);
            QueryDimension productDimension = query.getDimension("Product");
            productDimension.include(
                Selection.Operator.MEMBER,
                nameList("Product", "Drink", "Beverages"));
            productDimension.include(
                Selection.Operator.MEMBER,
                nameList("Product", "Food", "Frozen Foods"));
            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Sales Count"));
            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.include(nameList("Time", "Year", "1997", "Q3", "7"));
            QueryDimension storeDimension = query.getDimension("Store");
            storeDimension.include(
                Selection.Operator.MEMBER, nameList("Store", "USA"));
            query.getAxis(Axis.ROWS).addDimension(storeDimension);
            query.getAxis(Axis.FILTER).addDimension(productDimension);
            query.getAxis(Axis.FILTER).addDimension(timeDimension);
            query.getAxis(Axis.COLUMNS).addDimension(measuresDimension);

            query.validate();

            // Validate the generated MDX
            String mdxString = query.getSelect().toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Measures].[Sales Count]} ON COLUMNS,\n"
                + "{[Store].[USA]} ON ROWS\n"
                + "FROM [Sales]\n"
                + "WHERE Hierarchize(Union(CrossJoin({[Product].[Drink].[Beverages]}, {[Time].[1997].[Q3].[7]}), CrossJoin({[Product].[Food].[Frozen Foods]}, {[Time].[1997].[Q3].[7]})))",
                mdxString);

            // Validate the returned results
            CellSet results = query.execute();
            String resultsString = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{[Product].[Drink].[Beverages], [Time].[1997].[Q3].[7]}\n"
                + "{[Product].[Food].[Frozen Foods], [Time].[1997].[Q3].[7]}\n"
                + "Axis #1:\n"
                + "{[Measures].[Sales Count]}\n"
                + "Axis #2:\n"
                + "{[Store].[USA]}\n"
                + "Row #0: 1,187\n",
                resultsString);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    public void testHierarchyConsistency() {
        try {
            Cube cube = getFoodmartCube("Sales");
            if (cube == null) {
                fail("Could not find Sales cube");
            }
            // Setup a base query.
            Query query = new Query("my query", cube);
            QueryDimension productDimension = query.getDimension("Product");
            productDimension.setHierarchyConsistent(true);
            NamedList<Level> productLevels =
                productDimension.getDimension()
                    .getDefaultHierarchy().getLevels();

            Level productLevel = productLevels.get("Product Category");
            productDimension.include(productLevel);

            productDimension.include(
                Selection.Operator.MEMBER,
                    nameList("Product", "Food", "Deli"));
            productDimension.include(
                Selection.Operator.MEMBER,
                    nameList("Product", "Food", "Dairy"));
            productDimension.include(
                Selection.Operator.MEMBER,
                    nameList("Product", "Product Family", "Food"));
            productDimension.include(
                Selection.Operator.MEMBER,
                    nameList("Product", "All Products"));
            QueryDimension timeDimension = query.getDimension("Time");
            timeDimension.setHierarchyConsistent(true);

            timeDimension.include(nameList("Time", "Year", "1997", "Q3", "7"));
            timeDimension.include(nameList("Time", "Year", "1997", "Q4", "11"));

            timeDimension.include(nameList("Time", "Year", "1997"));
            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Sales Count"));

            query.getAxis(Axis.COLUMNS).addDimension(productDimension);
            query.getAxis(Axis.ROWS).addDimension(timeDimension);

            query.validate();

            // Validate the generated MDX
            String mdxString = query.getSelect().toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{{[Product].[All Products]}, {[Product].[Food]}, Filter({{[Product].[Food].[Deli], [Product].[Food].[Dairy]}}, (Ancestor([Product].CurrentMember, [Product].[Product Family]) IN {[Product].[Food]})), Filter({{[Product].[Product Category].Members}}, ((Ancestor([Product].CurrentMember, [Product].[Product Family]) IN {[Product].[Food]}) AND (Ancestor([Product].CurrentMember, [Product].[Product Department]) IN {[Product].[Food].[Deli], [Product].[Food].[Dairy]})))} ON COLUMNS,\n"
                + "{{[Time].[1997]}, Filter({{[Time].[1997].[Q3].[7], [Time].[1997].[Q4].[11]}}, (Ancestor([Time].CurrentMember, [Time].[Year]) IN {[Time].[1997]}))} ON ROWS\n"
                + "FROM [Sales]",
                mdxString);

            // Validate the returned results
            CellSet results = query.execute();
            String resultsString = TestContext.toString(results);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{}\n"
                + "Axis #1:\n"
                + "{[Product].[All Products]}\n"
                + "{[Product].[Food]}\n"
                + "{[Product].[Food].[Deli]}\n"
                + "{[Product].[Food].[Dairy]}\n"
                + "{[Product].[Food].[Dairy].[Dairy]}\n"
                + "{[Product].[Food].[Deli].[Meat]}\n"
                + "{[Product].[Food].[Deli].[Side Dishes]}\n"
                + "Axis #2:\n"
                + "{[Time].[1997]}\n"
                + "{[Time].[1997].[Q3].[7]}\n"
                + "{[Time].[1997].[Q4].[11]}\n"
                + "Row #0: 266,773\n"
                + "Row #0: 191,940\n"
                + "Row #0: 12,037\n"
                + "Row #0: 12,885\n"
                + "Row #0: 12,885\n"
                + "Row #0: 9,433\n"
                + "Row #0: 2,604\n"
                + "Row #1: 23,763\n"
                + "Row #1: 17,036\n"
                + "Row #1: 1,050\n"
                + "Row #1: 1,229\n"
                + "Row #1: 1,229\n"
                + "Row #1: 847\n"
                + "Row #1: 203\n"
                + "Row #2: 25,270\n"
                + "Row #2: 18,278\n"
                + "Row #2: 1,312\n"
                + "Row #2: 1,232\n"
                + "Row #2: 1,232\n"
                + "Row #2: 1,033\n"
                + "Row #2: 279\n",
                resultsString);
            query.validate();

            query.getAxis(Axis.ROWS).addDimension(measuresDimension);
            productDimension.clearInclusions();
            productDimension.include(
                Selection.Operator.MEMBER,
                    nameList("Product", "Product Family", "Food"));

            // Validate the generated MDX
            String mdxString2 = query.getSelect().toString();
            TestContext.assertEqualsVerbose(
                "SELECT\n"
                + "{[Product].[Food]} ON COLUMNS,\n"
                + "Hierarchize(Union(CrossJoin(Filter({[Time].[1997].[Q3].[7]}, (Ancestor([Time].CurrentMember, [Time].[Year]) IN {[Time].[1997]})), {[Measures].[Sales Count]}), Union(CrossJoin(Filter({[Time].[1997].[Q4].[11]}, (Ancestor([Time].CurrentMember, [Time].[Year]) IN {[Time].[1997]})), {[Measures].[Sales Count]}), CrossJoin({[Time].[1997]}, {[Measures].[Sales Count]})))) ON ROWS\n"
                + "FROM [Sales]",
                mdxString2);

            // Validate the returned results
            CellSet results2 = query.execute();
            String resultsString2 = TestContext.toString(results2);
            TestContext.assertEqualsVerbose(
                "Axis #0:\n"
                + "{}\n"
                + "Axis #1:\n"
                + "{[Product].[Food]}\n"
                + "Axis #2:\n"
                + "{[Time].[1997], [Measures].[Sales Count]}\n"
                + "{[Time].[1997].[Q3].[7], [Measures].[Sales Count]}\n"
                + "{[Time].[1997].[Q4].[11], [Measures].[Sales Count]}\n"
                + "Row #0: 62,445\n"
                + "Row #1: 5,552\n"
                + "Row #2: 5,944\n",
                resultsString2);
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
                Selection.Operator.CHILDREN, nameList("Product", "Drink"));

            QueryDimension storeDimension = query.getDimension("Store");
            storeDimension.include(
                Selection.Operator.INCLUDE_CHILDREN, nameList("Store", "USA"));
            storeDimension.setHierarchizeMode(HierarchizeMode.POST);

            QueryDimension timeDimension = query.getDimension("Time");

            timeDimension.include(
                Selection.Operator.CHILDREN, nameList("Time", "1997"));

            QueryDimension measuresDimension = query.getDimension("Measures");
            measuresDimension.include(nameList("Measures", "Store Sales"));


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




