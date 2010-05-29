/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.metadata.Property;

import java.util.List;
import java.sql.ResultSet;

/**
 * Cell returned from a {@link CellSet}.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface Cell {
    /**
     * Returns the {@link CellSet} that this Cell belongs to.
     *
     * @return CellSet, never null
     */
    CellSet getCellSet();

    /**
     * Returns the ordinal of this Cell.
     *
     * <p>The formula is the sequence, zero-based, which the cell would be
     * visited in a raster-scan through all of the cells of this
     * {@link CellSet}. The ordinal of the first cell is zero, and the
     * ordinal of the last cell is the product of the lengths of the axes, minus
     * 1. For example, if a result has 10 columns and 20
     * rows, then:<ul>
     * <li>(row 0, column 0) has ordinal 0,</li>
     * <li>(row 0, column 1) has ordinal 1,</li>
     * <li>(row 1, column 0) has ordinal 10,</li>
     * <li>(row 19, column 9) has ordinal 199.</li>
     * </ul>
     *
     * @return Ordinal of this Cell
     */
    int getOrdinal();

    /**
     * Returns the coordinates of this Cell in its {@link CellSetAxis}.
     *
     * <p>This method is provided for convenience. It is equivalent to the
     * following code:
     * <blockquote>
     * <code>
     *    getResult().ordinalToCoordinateList(getOrdinal())
     * </code>
     * </blockquote>
     *
     * @return Coordinates of this Cell
     */
    List<Integer> getCoordinateList();

    /**
     * Returns the value of a given property for this Cell.
     *
     * <p>The list of allowable properties may be obtained by calling
     * {@link org.olap4j.CellSet#getMetaData()} followed by
     * {@link CellSetMetaData#getCellProperties()}.</p>
     *
     * <p>Every cell has certain system properties such as "VALUE" and
     * "FORMAT_STRING" (the full list is described in the
     * {@link org.olap4j.metadata.Property.StandardCellProperty}
     * enumeration), as well as extra properties defined by the query.</p>
     *
     * @param property Property whose value to retrieve
     *
     * @return Value of the given property for this Cell; if the property is
     * not set, returns null
     */
    Object getPropertyValue(Property property);

    /**
     * Returns whether this cell is empty.
     *
     * @return Whether this cell is empty.
     */
    boolean isEmpty();

    /**
     * Returns whether an error occurred while evaluating this cell.
     *
     * @return Whether an error occurred while evaluating this cell.
     */
    boolean isError();

    /**
     * Returns whether the value of this cell is NULL.
     *
     * @return Whether the value of this cell is NULL.
     */
    boolean isNull();

    /**
     * Returns the value of this cell as a <code>double</code> value.
     *
     * <p>Not all values can be represented as using the Java
     * <code>double</code>, therefore for some providers, {@link #getValue()}
     * may return a more accurate result.
     *
     * @return The value of this cell; if the cell is null, the
     * returns <code>0</code>
     *
     * @throws OlapException if this cell does not have a numeric value
     */
    double getDoubleValue() throws OlapException;

    /**
     * Returns the error message of this Cell, or null if the cell is not
     * in error.
     *
     * <p>If the cell is an error, the value will be an {@link OlapException}.
     * (This value is returned, not thrown.)
     *
     * @return value of this Cell
     */
    String getErrorText();

    /**
     * Returns the value of this Cell.
     *
     * <p>If the cell is an error, the value will be an {@link OlapException}.
     * (This value is returned, not thrown.)
     *
     * <p>If the cell has a numeric value, returns an object which implements
     * the {@link Number} interface.
     *
     * @see #getDoubleValue()
     *
     * @return value of this Cell
     */
    Object getValue();

    /**
     * Returns the value of this Cell, formatted according to the
     * FORMAT_STRING property and using the numeric formatting tokens the
     * current locale.
     *
     * <p>The formatted value is never null. In particular, when the cell
     * contains the MDX NULL value, {@link #getValue()} will return the Java
     * <code>null</code> value but this method will return the empty string
     * <code>""</code>.
     *
     * @return Formatted value of this Cell
     */
    String getFormattedValue();

    /**
     * Drills through from this cell to the underlying fact table data,
     * and returns a {@link java.sql.ResultSet} of the results.
     *
     * <p>If drill-through is not possible, returns null.
     *
     * @return result set of the fact rows underlying this Cell
     *
     * @throws OlapException if a database error occurs
     */
    ResultSet drillThrough() throws OlapException;

    /**
     * Sets the value of a cell.
     *
     * <p>When this method may be called depends on the provider. But typically,
     * the connection must at least have an active scenario; see
     * {@link OlapConnection#setScenario(Scenario)}.
     *
     * <p>The number and type of additional arguments specified in the
     * {@code allocationArgs} parameter depends on the allocation policy chosen.
     * Some policies, such as {@link AllocationPolicy#EQUAL_ALLOCATION}, do not
     * require any additional arguments, in which case {@code allocationArgs}
     * may be {@code null}.
     *
     * @param value Cell value
     * @param allocationPolicy Allocation policy
     * @param allocationArgs Allocation policy arguments
     */
    void setValue(
        Object value,
        AllocationPolicy allocationPolicy,
        Object... allocationArgs);
}

// End Cell.java
