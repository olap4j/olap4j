/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import java.util.List;
import java.sql.ResultSet;

/**
 * Result of executing an OLAP Statement.
 *
 * <p>An <codeOlapResultSet</code> consists of a set of (typically two) axes,
 * each populated with a sequence of members, and a collection of cells at the
 * intersection of these axes.
 *
 * <p><b>Cell ordinals and coordinates</b></p>
 *
 * <p>There are two ways to identify a particular cell: ordinal and coordinates.
 * Suppose that there are <code>p</code> axes, and each axis <code>k</code>
 * (<code>k</code> between 0 and <code>p - 1</code>) has
 * <code>U<sub>k</sub></code> positions.
 * There are <code>U</code>
 * = <code>U<sub>0</sub> * ... * U<sub>p - 1</sub></code> cells in total.
 * Then:<ul>
 * <li>A cell's <code>ordinal</code> is an integer between 0 and
 *     <code>U - 1</code>.</li>
 * <li>A cell's <code>coordinates</code> are a list of <code>p</code> integers,
 *     indicating the cell's position on each axis.
 *     Each integer is between 0 and <code>U<sub>p</sub>-1</code>.</li>
 * </ul>
 *
 * <p>The ordinal number of a cell whose tuple ordinals are
 * <code>(S<sub>0</sub>, S<sub>1</sub>, ... S<sub>p-1</sub>)</code> is
 * <blockquote>
 * <code>
 * &#931;<sub>i=0</sub><sup>p-1</sup> S<sub>i</sub> . E<sub>i</sub>
 * </code>
 * where
 * <code>E<sub>0</sub> = 1</code>
 * and
 * <code>
 * E<sub>i</sub> = &#928;<sub>i=0</sub><sup>p-1</sup> U<sub>k</sub>
 * </code>
 * </blockquote></p>
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface OlapResultSet extends ResultSet {
    /**
     * Retrieves the description of this <code>OlapResultSet</code>'s axes
     * and cells.
     *
     * @return the description of this <code>OlapResultSet</code>'s axes
     * and cells
     * @exception OlapException if a database access error occurs
     */
    OlapResultSetMetaData getMetaData() throws OlapException;

    /**
     * Retrieves a list of ResultAxis objects containing the result.
     *
     * <p>The list contains axes according to their ordinal: 0 is the columns
     * axis, 1 the rows axis, and so forth.
     */
    List<ResultAxis> getAxes();

    /**
     * Returns the ResultCell at a given set of coordinates.
     *
     * @param coordinates List of 0-based coordinates of the cell
     * @return Cell
     */
    ResultCell getCell(List<Integer> coordinates);

    /**
     * Returns the ResultCell at a ordinal.
     *
     * <p>Equivalent to
     * <blockquote><code>
     * getCell(ordinalToCoordinates(ordinal)
     * </code></blockquote>
     *
     * @param ordinal 0-based ordinal of the cell
     * @return Cell
     */
    ResultCell getCell(int ordinal);

    /**
     * Converts a cell ordinal to a list of cell coordinates.
     *
     * @param ordinal Cell ordinal
     * @return Cell coordinates
     */
    List<Integer> ordinalToCoordinates(int ordinal);

    /**
     * Converts a list of cell coordinates to a cell ordinal.
     *
     * <p>The mapping
     * @param coordinates Cell coordinates
     * @return Cell ordinal
     */
    int coordinatesToOrdinal(List<Integer> coordinates);

}

// End OlapResultSet.java
