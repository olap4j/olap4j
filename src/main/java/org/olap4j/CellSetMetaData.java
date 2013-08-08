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
package org.olap4j;

import org.olap4j.metadata.*;

import java.sql.ResultSetMetaData;

/**
 * An object that can be used to get information about the axes
 * and cells in a <code>CellSet</code> object.
 *
 * <p>The following code fragment creates the <code>CellSet</code> object cs,
 * creates the <code>CellSetMetaData</code> object csmd, and uses csmd
 * to find out how many axes cs has and the name of the cube.
 *
 * <blockquote>
 * <pre>
 * CellSet cs = stmt.executeOlapQuery(
 *     "SELECT {[Measures].[Unit Sales] ON COLUMNS,\n" +
 *     "   Crossjoin([Time].Children, [Store].Children) ON ROWS\n" +
 *     "FROM [Sales]");
 * CellSetMetaData csmd = cs.getMetaData();
 * int numberOfAxes = csmd.getAxesMetaData().size();
 * String cubeName = csmd.getCube().getName();
 * </pre>
 * </blockquote>
 *
 * @author jhyde
 * @since Oct 23, 2006
 */
public interface CellSetMetaData extends ResultSetMetaData, OlapWrapper {
    /**
     * Returns a list of Property objects which each Cell may have.
     *
     * @return list of cell properties
     */
    NamedList<Property> getCellProperties();

    /**
     * Returns the Cube which was referenced in this statement.
     *
     * @return cube referenced in this statement
     */
    Cube getCube();

    /**
     * Returns a list of CellSetAxisMetaData describing each result axis.
     *
     * @return list of metadata describing each result axis
     */
    NamedList<CellSetAxisMetaData> getAxesMetaData();

    /**
     * Returns a CellSetAxisMetaData describing the filter axis. Never returns
     * null; if the MDX statement contains no WHERE clause, the description of
     * the filter contains no hierarchies.
     *
     * @return metadata describing filter axis
     */
    CellSetAxisMetaData getFilterAxisMetaData();
}

// End CellSetMetaData.java
