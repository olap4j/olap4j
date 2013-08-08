/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.sample;

import org.olap4j.*;
import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.layout.RectangularCellSetFormatter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This class demonstrates how to connect the {@link XmlaOlap4jDriver}
 * to a Palo server. Thanks to Vladislav Malicevic for this
 * contribution.
 *
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

        // We use the utility formatter.
        RectangularCellSetFormatter formatter =
            new RectangularCellSetFormatter(false);

        // Print out.
        PrintWriter writer = new PrintWriter(System.out);
        formatter.format(cellSet, writer);
        writer.flush();

        statement.close();
        connection.close();
    }
}

// End PaloConnection.java
