/*
// $Id: $
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

/**
 * An axis within an OLAP {@link Query}.
 *
 * <p>An axis has a location (columns, rows, etc) and has zero or more
 * dimensions that are placed on it.
 *
 * @author jdixon
 * @version $Id: $
 * @since May 29, 2007
 */
public class QueryAxis {

    protected final List<QueryDimension> dimensions =
        new ArrayList<QueryDimension>();

    private final Query query;
    protected Axis location = null;

    public QueryAxis(Query query, Axis location) {
        super();
        this.query = query;
        this.location = location;
    }

    /**
     * Returns the location of this axis in the query; null if unused.
     */
    public Axis getLocation() {
        return location;
    }

    /**
     * Returns a list of the dimensions placed on this QueryAxis.
     *
     * <p>The list is mutable; you may call <code>getDimensions().clear</code>
     * or <code>getDimensions().add
     *
     * @return list of dimensions
     */
    public List<QueryDimension> getDimensions() {
        return dimensions;
    }

    // todo: make list mutable, and remove this method
    public void clearDimensions() {
        dimensions.clear();
    }

    // todo: make list mutable, and remove this method
    public void appendDimensions(List<QueryDimension> dimensionList) {
        for (QueryDimension queryDimension : dimensionList) {
            queryDimension.setAxis(this);
            dimensions.add(queryDimension);
        }
    }

    // todo: make list mutable, and remove this method
    public void appendDimension(QueryDimension dim) {
        if (dim.getAxis() != null) {
            dim.getAxis().removeDimension(dim);
        }
        dim.setAxis(this);
        dimensions.add(dim);
    }

    // todo: make list mutable, and remove this method
    public void insertDimension(int index, QueryDimension dim) {
        if (dim.getAxis() != null) {
            dim.getAxis().removeDimension(dim);
        }
        dim.setAxis(this);
        dimensions.add(index, dim);
    }

    // todo: make list mutable, and remove this method
    public void removeDimension(QueryDimension dim) {
        dim.setAxis(null);
        dimensions.remove(dim);
    }

    public String getName() {
        return location.getCaption(query.getLocale());
    }
}

// End QueryAxis.java
