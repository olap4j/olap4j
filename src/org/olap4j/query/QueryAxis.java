/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.Axis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.AbstractList;

/**
 * An axis within an OLAP {@link Query}.
 *
 * <p>An axis has a location (columns, rows, etc) and has zero or more
 * dimensions that are placed on it.
 *
 * @author jdixon
 * @version $Id$
 * @since May 29, 2007
 */
public class QueryAxis {

    protected final List<QueryDimension> dimensions = new DimensionList();

    private final Query query;
    protected Axis location = null;
    private boolean nonEmpty;

    /**
     * Creates a QueryAxis.
     *
     * @param query Query that the axis belongs to
     * @param location Location of axis (e.g. ROWS, COLUMNS)
     */
    public QueryAxis(Query query, Axis location) {
        super();
        this.query = query;
        this.location = location;
    }

    /**
     * Returns the location of this <code>QueryAxis</code> in the query;
     * <code>null</code> if unused.
     *
     * @return location of this axis in the query
     */
    public Axis getLocation() {
        return location;
    }

    /**
     * Returns a list of the dimensions placed on this QueryAxis.
     *
     * <p>The list is mutable; you may call
     * <code>getDimensions().clear()</code>,
     * or <code>getDimensions().add(dimension)</code>, for instance.
     * When a dimension is added to an axis, it is automatically removed from
     * its previous axis.</p>
     *
     * @return list of dimensions
     */
    public List<QueryDimension> getDimensions() {
        return dimensions;
    }

    /**
     * Returns the name of this QueryAxis.
     *
     * @return the name of this axis, for example "ROWS", "COLUMNS".
     */
    public String getName() {
        return location.getCaption(query.getLocale());
    }

    /**
     * Places a QueryDimension object one position before in the
     * list of current dimensions. Uses a 0 based index.
     * For example, to place the 5th dimension on the current axis
     * one position before, one would need to call pullUp(4),
     * so the dimension would then use axis index 4 and the previous
     * dimension at that position gets pushed down one position.
     * @param index The index of the dimension to move up one notch.
     * It uses a zero based index.
     */
    public void pullUp(int index) {
        Collections.swap(this.dimensions, index, index - 1);
    }

    /**
     * Places a QueryDimension object one position lower in the
     * list of current dimensions. Uses a 0 based index.
     * For example, to place the 4th dimension on the current axis
     * one position lower, one would need to call pushDown(3),
     * so the dimension would then use axis index 4 and the previous
     * dimension at that position gets pulled up one position.
     * @param index The index of the dimension to move down one notch.
     * It uses a zero based index.
     */
    public void pushDown(int index) {
        Collections.swap(this.dimensions, index, index + 1);
    }

    /**
     * Returns whether this QueryAxis filters out empty rows.
     * If true, axis filters out empty rows, and the MDX to evaluate the axis
     * will be generated with the "NON EMPTY" expression.
     *
     * @return Whether this axis should filter out empty rows
     *
     * @see #setNonEmpty(boolean)
     */
    public boolean isNonEmpty() {
        return nonEmpty;
    }

    /**
     * Sets whether this QueryAxis filters out empty rows.
     *
     * @param nonEmpty Whether this axis should filter out empty rows
     *
     * @see #isNonEmpty()
     */
    public void setNonEmpty(boolean nonEmpty) {
        this.nonEmpty = nonEmpty;
    }

    /**
     * List of QueryDimension objects. The list is active: when a dimension
     * is added to the list, it is removed from its previous axis.
     */
    private class DimensionList extends AbstractList<QueryDimension> {
        private final List<QueryDimension> list =
            new ArrayList<QueryDimension>();

        public QueryDimension get(int index) {
            return list.get(index);
        }

        public int size() {
            return list.size();
        }

        public QueryDimension set(int index, QueryDimension dimension) {
            if (dimension.getAxis() != null
                && dimension.getAxis() != QueryAxis.this)
            {
                dimension.getAxis().getDimensions().remove(dimension);
            }
            dimension.setAxis(QueryAxis.this);
            return list.set(index, dimension);
        }

        public void add(int index, QueryDimension dimension) {
            if (this.contains(dimension)) {
                throw new IllegalStateException("dimension already on this axis");
            }
            if (dimension.getAxis() != null
                && dimension.getAxis() != QueryAxis.this)
            {
                // careful! potential for loop
                dimension.getAxis().getDimensions().remove(dimension);
            }
            dimension.setAxis(QueryAxis.this);
            if (index >= list.size()) {
                list.add(dimension);
            } else {
                list.add(index, dimension);
            }
        }

        public QueryDimension remove(int index) {
            QueryDimension dimension = list.remove(index);
            dimension.setAxis(null);
            return dimension;
        }
    }
}

// End QueryAxis.java
