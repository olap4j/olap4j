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
import org.olap4j.metadata.Member;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import mondrian.olap.Query;
import mondrian.olap.QueryAxis;

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
    void simpleStatement() throws SQLException, ClassNotFoundException {
        // Register driver.
        Class.forName("mondrian.olap4j.Driver");

        // Create connection.
        OlapConnection connection = (OlapConnection)
                DriverManager.getConnection("jdbc:mondrian:embedded");

        // Execute a statement.
        OlapStatement statement = connection.createOlapStatement();
        OlapResult result = statement.executeOlapQuery(
            "select {[Measures].[Unit Sales]} on columns,\n" +
                " CrossJoin([Store].Children, [Gender].Members) on rows\n" +
                "from [Sales]");

        List<ResultAxis> resultAxes = result.getAxes();

        // Print headings.
        System.out.print("\t");
        ResultAxis columnsAxis = resultAxes.get(ResultAxis.COLUMNS);
        for (ResultPosition position : columnsAxis.getPositions()) {
            Member measure = position.getMembers().get(0);
            System.out.print(measure.getName());
        }

        // Print rows.
        ResultAxis rowsAxis = resultAxes.get(ResultAxis.ROWS);
        for (ResultPosition position : rowsAxis.getPositions()) {
            boolean first = true;
            for (Member member : position.getMembers()) {
                if (first) {
                    first = false;
                } else {
                    System.out.print('\t');
                }
                System.out.print(member.getName());
            }

            // Print the value of the cell in each column.
            for (ResultPosition columnPosition : columnsAxis.getPositions()) {
                ResultCell cell = result.getCell(
                    new int[] {
                        columnPosition.getOrdinal(),
                        position.getOrdinal(),
                });
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

        // Execute a statement.
        PreparedOlapStatement statement = connection.prepareOlapStatement(
            "select {[Measures].[Unit Sales]} on columns,\n" +
                "  {TopCount\n(" +
                "      Parameter(\"Store\", [Store].[USA].[CA]).Children,\n" +
                "      Parameter(\"Count\", INTEGER))} on rows\n" +
                "from [Sales]");

        // Locate the member "[Store].[USA].[WA].[Seattle]".
        Cube cube = statement.getCube();
        Dimension dimension = cube.getDimensions().get("Store");
        Member allStores = dimension.getRootMembers().get(0);
        Member memberUsa = allStores.getChildMembers().get("USA");
        Member memberWa = memberUsa.getChildMembers().get("WA");
        Member memberSeattle = memberWa.getChildMembers().get("Seattle");
        statement.setMember(1, memberSeattle);
        statement.setInt(2, 10);

        // Execute, and print result.
        OlapResult result = statement.executeOlapQuery();
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
        OlapConnection connection = (OlapConnection)
                DriverManager.getConnection("jdbc:mondrian:embedded");

        // Create a parser.
        MdxParserFactory parserFactory = connection.getParserFactory();
        MdxParser parser = parserFactory.createMdxParser(connection);
        Query query = parser.parseSelect(
            "select {[Measures].[Unit Sales]} on columns\n" +
                "from [Sales]");
        query.axes[0].nonEmpty = false;

        // Create statement.
        OlapStatement statement = connection.createOlapStatement();
        statement.executeOlapQuery(query);
    }

    private void printResult(OlapResult result) {
        List<ResultAxis> resultAxes = result.getAxes();

        // Print headings.
        System.out.print("\t");
        ResultAxis columnsAxis = resultAxes.get(ResultAxis.COLUMNS);
        for (ResultPosition position : columnsAxis.getPositions()) {
            Member measure = position.getMembers().get(0);
            System.out.print(measure.getName());
        }

        // Print rows.
        ResultAxis rowsAxis = resultAxes.get(ResultAxis.ROWS);
        for (ResultPosition position : rowsAxis.getPositions()) {
            boolean first = true;
            for (Member member : position.getMembers()) {
                if (first) {
                    first = false;
                } else {
                    System.out.print('\t');
                }
                System.out.print(member.getName());
            }

            // Print the value of the cell in each column.
            for (ResultPosition columnPosition : columnsAxis.getPositions()) {
                ResultCell cell = result.getCell(
                    new int[] {
                        columnPosition.getOrdinal(),
                        position.getOrdinal(),
                });
                System.out.print('\t');
                System.out.print(cell.getFormattedValue());
            }
            System.out.println();
        }
    }
}

// End SimpleQuerySample.java
