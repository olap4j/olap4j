/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.sample;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapStatement;
import org.olap4j.OlapWrapper;
import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.layout.TraditionalCellSetFormatter;

/**
 * This class demonstrates how to connect the {@link XmlaOlap4jDriver}
 * to a Palo server. Thanks to Vladislav Malicevic for this
 * contribution.
 * @author Luc Boudreau
 */
public class PaloConnection {

    public static void main(String[] args) throws Exception {
        Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
        Connection connection =
            DriverManager.getConnection(
                "jdbc:xmla:Server=http://localhost:4242;"
                + "User='admin';"
                + "Password='admin';"
                + "Catalog=FoodMart2005Palo;"
                + "Cube=Budget");

        OlapWrapper wrapper = (OlapWrapper) connection;

        OlapConnection olapConnection = wrapper.unwrap(OlapConnection.class);

        OlapStatement statement = olapConnection.createStatement();

        CellSet cellSet =
            statement.executeOlapQuery(
                "SELECT {[store].[USA]} ON COLUMNS , {[Account].[1000]} ON ROWS\n"
                + "FROM [Budget]");

        TraditionalCellSetFormatter formatter =
            new TraditionalCellSetFormatter();

        formatter.format(cellSet, new PrintWriter(System.out));
    }
}

// End PaloConnection.java