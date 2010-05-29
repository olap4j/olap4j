/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
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
import org.olap4j.layout.RectangularCellSetFormatter;
import org.olap4j.type.MemberType;

import java.io.PrintWriter;
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
    /**
     * Main command-line entry.
     *
     * @param args Command line arguments
     */
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
     * statement and prints the resulting cell set.
     */
    void simpleStatement()
        throws SQLException, ClassNotFoundException
    {
        // Register driver.
        Class.forName("mondrian.olap4j.MondrianOlap4jDriver");

        // Create connection.
        Connection connection =
            DriverManager.getConnection("jdbc:mondrian:embedded");
        OlapConnection olapConnection =
            ((OlapWrapper) connection).unwrap(OlapConnection.class);

        // Execute a statement.
        OlapStatement statement = olapConnection.createStatement();
        CellSet cellSet =
            statement.executeOlapQuery(
                "select {[Measures].[Unit Sales]} on columns,\n"
                + " CrossJoin([Store].Children, [Gender].Members) on rows\n"
                + "from [Sales]");

        List<CellSetAxis> cellSetAxes = cellSet.getAxes();

        // Print headings.
        System.out.print("\t");
        CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
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
                Cell cell = cellSet.getCell(cellOrdinal);

                // Just for kicks, convert the ordinal to a list of coordinates.
                // The list matches the row and column positions.
                List<Integer> coordList =
                    cellSet.ordinalToCoordinates(cellOrdinal);
                assert coordList.get(0) == rowPosition.getOrdinal();
                assert coordList.get(1) == columnPosition.getOrdinal();

                ++cellOrdinal;

                System.out.print('\t');
                System.out.print(cell.getFormattedValue());
            }
            System.out.println();
        }

        // Now, nicely formatted.
        System.out.println();
        final PrintWriter pw = new PrintWriter(System.out);
        new RectangularCellSetFormatter(false).format(cellSet, pw);
        pw.flush();

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
            "select {[Measures].[Unit Sales]} on columns,\n"
            + "  {TopCount\n("
            + "      Parameter(\"Store\", [Store].[USA].[CA]).Children,\n"
            + "      Parameter(\"Count\", INTEGER))} on rows\n"
            + "from [Sales]");

        // Describe the parameters.
        OlapParameterMetaData parameterMetaData =
            statement.getParameterMetaData();

        // Locate the member "[Store].[USA].[WA].[Seattle]".
        MemberType type =
            (MemberType) parameterMetaData.getParameterOlapType(1);
        Dimension dimension = type.getDimension();
        assert dimension.getName().equals("Store");
        Member allStores =
            dimension.getDefaultHierarchy().getRootMembers().get(0);
        Member memberUsa = allStores.getChildMembers().get("USA");
        Member memberWa = memberUsa.getChildMembers().get("WA");
        Member memberSeattle = memberWa.getChildMembers().get("Seattle");
        statement.setObject(1, memberSeattle);
        statement.setInt(2, 10);

        // Execute, and print cell set.
        CellSet cellSet = statement.executeQuery();
        printCellSet(cellSet);

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
            "select {[Measures].[Unit Sales]} on columns\n"
            + "from [Sales]");
        query.getAxisList().get(0).setNonEmpty(false);

        // Create statement.
        OlapStatement statement = olapConnection.createStatement();
        CellSet cellSet = statement.executeOlapQuery(query);
        printCellSet(cellSet);
    }

    /**
     * Prints a cell set.
     *
     * <p>For more sophisticated printing of cell sets, see
     * {@link org.olap4j.layout.CellSetFormatter}.
     *
     * @param cellSet Cell set
     */
    private void printCellSet(CellSet cellSet) {
        List<CellSetAxis> cellSetAxes = cellSet.getAxes();

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
                Cell cell = cellSet.getCell(coordList);
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
                new IdentifierNode.NameSegment("Sales")));
        query.getAxisList().add(
            new AxisNode(
                null,
                false,
                Axis.ROWS,
                new ArrayList<IdentifierNode>(),
                new CallNode(
                    null,
                    "{}",
                    Syntax.Braces,
                    new IdentifierNode(
                        new IdentifierNode.NameSegment("Measures"),
                        new IdentifierNode.NameSegment("Unit Sales")))));

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
            printCellSet(cset);
        } catch (OlapException e) {
            System.out.println("Execution failed: " + e);
        }
    }
}

// End SimpleQuerySample.java
