/*
// $Id$
//
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

import java.util.List;
import java.util.ListIterator;

/**
 * Axis of a CellSet.
 *
 * <p>A cell set has the same number of axes as the MDX statement which was
 * executed to produce it. For example, a typical cell set, resulting from an
 * MDX query with COLUMNS and ROWS expressions is two-dimensional, and
 * therefore has two axes.</p>
 *
 * <p>Each axis is an ordered collection of members or tuples. Each member or
 * tuple on an axis is called a {@link Position}.</p>
 *
 * <p>The positions on the cell set axis can be accessed sequentially or
 * random-access. Use the {@link #getPositions()} method to return a list for
 * random access, or the {@link #iterator()} method to obtain an iterator for
 * sequential access.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface CellSetAxis extends Iterable<Position> {
    /**
     * Returns the axis ordinal of this <code>CellSetAxis</code>.
     *
     * <p>The first axis in a CellSet will return {@link Axis#COLUMNS},
     * the second {@link Axis#ROWS}, and so forth, as described by the
     * {@link Axis#axisOrdinal()} method of the {@link Axis} enumeration.</p>
     *
     * @return the ordinal of this axis
     */
    Axis getAxisOrdinal();

    /**
     * Returns the {@link CellSet} which this <code>CellSetAxis</code>
     * belongs to.
     *
     * @return the CellSet
     */
    CellSet getCellSet();

    /**
     * Returns a description of the type (e.g. {@link Axis#ROWS}) of this
     * axis, and the hierarchies and properties which will be found on it.
     *
     * <p>The result is identical to evaluating
     * <blockquote>
     * <code>getCellSet().getMetaData().getSlicerAxisMetaData()</code>
     * </blockquote>
     * for a filter axis, and
     * <blockquote>
     * <code>getCellSet().getMetaData().getAxesMetaData().get(
     * getAxisOrdinal().axisOrdinal())</code>
     * </blockquote>
     * for other axes.
     *
     * @return metadata describing this CellSetAxis
     */
    CellSetAxisMetaData getAxisMetaData();

    /**
     * Returns a list of <code>Position</code> objects on this CellSetAxis.
     *
     * @return List of positions on this axis (never null)
     */
    List<Position> getPositions();

    /**
     * Returns the number of positions on this CellSetAxis.
     *
     * <p>This method can be called at any time. In particular, it is not
     * necessary to complete an iteration through all positions before calling
     * this method.</p>
     *
     * <p>The number of positions on an axis is important when computing the
     * ordinal of a cell.</p>
     *
     * @return the number of positions
     */
    int getPositionCount();

    /**
     * Opens an iterator over the positions on this CellSetAxis.
     *
     * <p>If this axis has very many positions, this method may be more
     * efficient than {@link #getPositions()}.
     *
     * <p>This method allows CellSetAxis to implement the {@link Iterable}
     * interface, so one might use it in a foreach construct, for example:
     *
     * <blockquote>
     * <pre>
     * CellSet cellSet;
     * for (Position rowPos : cellSet.getAxes().get(0)) {
     *     for (Position colPos : cellSet.getAxes().get(1)) {
     *          Cell cell = cellSet.getCell(colPos, rowPos);
     *          ....
     *     }
     * }</pre></blockquote>
     *
     * @return iterator over the collection of positions
     */
    ListIterator<Position> iterator();

}

// End CellSetAxis.java
