/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.sample;

import org.olap4j.*;
import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.mdx.parser.MdxParserFactory;
import org.olap4j.mdx.*;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Member;
import org.olap4j.type.MemberType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Collection of olap4j samples illustrating connections and statements.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public class SimpleQuerySample {
    public static void main(String[] args) {
        try {
            new SimpleQuerySample().simpleStatement();
        } catch (OlapException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simple example which connects to an OLAP server, executes an OLAP
     * statement and prints the result.
     */
    void simpleStatement()
        throws SQLException, ClassNotFoundException
    {
        // Register driver.
        Class.forName("mondrian.olap4j.Driver");

        // Create connection.
        Connection connection =
            DriverManager.getConnection("jdbc:mondrian:embedded");
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);

        // Execute a statement.
        OlapStatement statement = olapConnection.createStatement();
        CellSet result =
            statement.executeOlapQuery(
            "select {[Measures].[Unit Sales]} on columns,\n" +
                " CrossJoin([Store].Children, [Gender].Members) on rows\n" +
                "from [Sales]");

        List<CellSetAxis> cellSetAxes = result.getAxes();

        // Print headings.
        System.out.print("\t");
        CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.ordinal());
        for (Position position : columnsAxis.getPositions()) {
            Member measure = position.getMembers().get(0);
            System.out.print(measure.getName());
        }

        // Print rows.
        CellSetAxis rowsAxis = cellSetAxes.get(Axis.ROWS.axisOrdinal());
        int cellOrdinal = 0;
        for (Position rowPosition : rowsAxis.getPositions()) {
            boolean first = true;
            for (Member member : rowPosition.getMembers()) {
                if (first) {
                    first = false;
                } else {
                    System.out.print('\t');
                }
                System.out.print(member.getName());
            }

            // Print the value of the cell in each column.
            for (Position columnPosition : columnsAxis.getPositions()) {
                // Access the cell via its ordinal. The ordinal is kept in step
                // because we increment the ordinal once for each row and
                // column.
                Cell cell = result.getCell(cellOrdinal);

                // Just for kicks, convert the ordinal to a list of coordinates.
                // The list matches the row and column positions.
                List<Integer> coordList =
                    result.ordinalToCoordinates(cellOrdinal);
                assert coordList.get(0) == rowPosition.getOrdinal();
                assert coordList.get(1) == columnPosition.getOrdinal();

                ++cellOrdinal;

                System.out.print('\t');
                System.out.print(cell.getFormattedValue());
            }
            System.out.println();
        }

        // Close the statement and connection.
        statement.close();
        connection.close();
    }

    /**
     * Sample which prepares a statement, sets a parameter, and executes the
     * statement.
     */
    void preparedStatement() throws SQLException, ClassNotFoundException {
        // Register driver.
        Class.forName("mondrian.olap4j.Driver");

        // Create connection.
        OlapConnection connection = (OlapConnection)
                DriverManager.getConnection("jdbc:mondrian:embedded");

        // Prepare a statement.
        PreparedOlapStatement statement = connection.prepareOlapStatement(
            "select {[Measures].[Unit Sales]} on columns,\n" +
                "  {TopCount\n(" +
                "      Parameter(\"Store\", [Store].[USA].[CA]).Children,\n" +
                "      Parameter(\"Count\", INTEGER))} on rows\n" +
                "from [Sales]");

        // Describe the parameters.
        OlapParameterMetaData parameterMetaData =
            statement.getParameterMetaData();

        // Locate the member "[Store].[USA].[WA].[Seattle]".
        MemberType type = (MemberType) parameterMetaData.getParameterOlapType(1);
        Dimension dimension = type.getDimension();
        assert dimension.getName().equals("Store");
        Member allStores =
            dimension.getDefaultHierarchy().getRootMembers().get(0);
        Member memberUsa = allStores.getChildMembers().get("USA");
        Member memberWa = memberUsa.getChildMembers().get("WA");
        Member memberSeattle = memberWa.getChildMembers().get("Seattle");
        statement.setObject(1, memberSeattle);
        statement.setInt(2, 10);

        // Execute, and print result.
        CellSet result = statement.executeQuery();
        printResult(result);

        // Close the statement and connection.
        statement.close();
        connection.close();
    }

    /**
     * Sample which creates a statement from a parse tree.
     */
    void statementFromParseTree() throws ClassNotFoundException, SQLException {
        // Register driver.
        Class.forName("mondrian.olap4j.Driver");

        // Create connection.
        Connection connection =
            DriverManager.getConnection("jdbc:mondrian:embedded");
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);

        // Create a parser.
        MdxParserFactory parserFactory = olapConnection.getParserFactory();
        MdxParser parser = parserFactory.createMdxParser(olapConnection);
        SelectNode query = parser.parseSelect(
            "select {[Measures].[Unit Sales]} on columns\n" +
                "from [Sales]");
        query.getAxisList().get(0).setNonEmpty(false);

        // Create statement.
        OlapStatement statement = olapConnection.createStatement();
        CellSet cellSet = statement.executeOlapQuery(query);
        printResult(cellSet);
    }

    private void printResult(CellSet result) {
        List<CellSetAxis> cellSetAxes = result.getAxes();

        // Print headings.
        System.out.print("\t");
        CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
        for (Position position : columnsAxis.getPositions()) {
            Member measure = position.getMembers().get(0);
            System.out.print(measure.getName());
        }

        // Print rows.
        CellSetAxis rowsAxis = cellSetAxes.get(Axis.ROWS.axisOrdinal());
        List<Integer> coordList = new ArrayList<Integer>(2);
        int row = 0;
        for (Position rowPosition : rowsAxis.getPositions()) {
            assert rowPosition.getOrdinal() == row;
            coordList.set(0, row++);

            // Print the row label.
            int memberOrdinal = 0;
            for (Member member : rowPosition.getMembers()) {
                if (memberOrdinal++ > 0) {
                    System.out.print('\t');
                }
                System.out.print(member.getName());
            }

            // Print the value of the cell in each column.
            int column = 0;
            for (Position columnPosition : columnsAxis.getPositions()) {
                assert columnPosition.getOrdinal() == column;
                coordList.set(1, column++);
                Cell cell = result.getCell(coordList);
                System.out.print('\t');
                System.out.print(cell.getFormattedValue());
            }
            System.out.println();
        }
    }

    /**
     * Example in "MDX query model" section of olap4j_fs.html.
     *
     * @param connection Connection
     */
    void executeSelectNode(OlapConnection connection) {
        // Create a query model.
        SelectNode query = new SelectNode();
        query.setFrom(
            new IdentifierNode(
                new IdentifierNode.Segment("Sales")));
        query.getAxisList().add(
            new AxisNode(
                null,
                false,
                new CallNode(
                    null,
                    "{}",
                    Syntax.Braces,
                    new IdentifierNode(
                        new IdentifierNode.Segment("Measures"),
                        new IdentifierNode.Segment("Unit Sales"))),
                Axis.ROWS,
                new ArrayList<IdentifierNode>()));

        // Create a statement based upon the query model.
        OlapStatement stmt;
        try {
            stmt = connection.createStatement();
        } catch (OlapException e) {
            System.out.println("Validation failed: " + e);
            return;
        }

        // Execute the statement.
        CellSet cset;
        try {
            cset = stmt.executeOlapQuery(query);
        } catch (OlapException e) {
            System.out.println("Execution failed: " + e);
        }
    }
}

// End SimpleQuerySample.java
