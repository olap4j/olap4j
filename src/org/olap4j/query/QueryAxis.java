/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.Axis;

import java.util.ArrayList;
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

    public String getName() {
        return location.getCaption(query.getLocale());
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
            if (dimension.getAxis() != null) {
                dimension.getAxis().getDimensions().remove(dimension);
            }
            dimension.setAxis(QueryAxis.this);
            return list.set(index, dimension);
        }

        public void add(int index, QueryDimension dimension) {
            if (this.contains(dimension)) {
                throw new IllegalStateException("dimension already on this axis");
            }
            if (dimension.getAxis() != null) {
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
