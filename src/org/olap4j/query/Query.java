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

import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Member;
import org.olap4j.*;
import org.olap4j.mdx.SelectNode;

import java.util.*;
import java.sql.SQLException;

/**
 * Query model.
 *
 * @author jhyde, jdixon
 * @version $Id: $
 * @since May 29, 2007
 */
public class Query {

    protected final String name;
    protected Map<Axis, QueryAxis> axes = new HashMap<Axis, QueryAxis>();
    protected QueryAxis across;
    protected QueryAxis down;
    protected QueryAxis slicer;
    protected QueryAxis unused;
    protected final Cube cube;
    protected Map<String, QueryDimension> dimensionMap =
        new HashMap<String, QueryDimension>();
    private final OlapConnection connection;
    private final SelectionFactory selectionFactory =
        new SelectionFactory(this);

    public Query(String name, Cube cube) throws SQLException {
        super();
        this.name = name;
        this.cube = cube;
        this.connection =
            cube.getSchema().getCatalog().getDatabase().getMetaData()
                .getConnection().unwrap(OlapConnection.class);
        this.unused = new QueryAxis(this, null);
        for (Dimension dimension : cube.getDimensions()) {
            QueryDimension queryDimension = new QueryDimension(
                this, dimension);
            unused.appendDimension(queryDimension);
            dimensionMap.put(queryDimension.getName(), queryDimension);
        }
        across = new QueryAxis(this, Axis.COLUMNS);
        down = new QueryAxis(this, Axis.ROWS);
        slicer = new QueryAxis(this, Axis.SLICER);
        axes.put(null, unused);
        axes.put(Axis.COLUMNS, across);
        axes.put(Axis.ROWS, down);
        axes.put(Axis.SLICER, slicer);
    }

    /**
     * Returns the MDX parse tree behind this Query.
     */
    public SelectNode getSelect() {
        throw new UnsupportedOperationException();
    }

    public Cube getCube() {
        return cube;
    }

    public QueryDimension getDimension(String name) {
        return dimensionMap.get(name);
    }

    /**
     * Swaps rows and columns axes. Only applicable if there are two axes.
     */
    public void swapAxes() {
        // Only applicable if there are two axes - plus slicer and unused.
        if (axes.size() != 4) {
            throw new IllegalArgumentException();
        }
        List<QueryDimension> tmp = new ArrayList<QueryDimension>();
        QueryAxis columnsAxis = axes.get(Axis.COLUMNS);
        QueryAxis rowsAxis = axes.get(Axis.ROWS);
        tmp.addAll(this.across.getDimensions());
        columnsAxis.clearDimensions();
        columnsAxis.appendDimensions(rowsAxis.getDimensions());
        rowsAxis.clearDimensions();
        rowsAxis.appendDimensions(tmp);
    }

    public Map<Axis, QueryAxis> getAxes() {
        return axes;
    }

    public QueryAxis getunusedAxis() {
        return unused;
    }

    public boolean validate() {
        for (Dimension dimension :  cube.getDimensions()) {
            QueryDimension queryDimension =
                getDimension(dimension.getName());
            if (queryDimension == null) {
                // TODO log this better
                return false;
            }
            Member member = dimension.getDefaultHierarchy().getDefaultMember();
            if (queryDimension.getAxis() == null ||
                queryDimension.getAxis().getLocation() == null) {
                queryDimension.clearSelections();
                queryDimension.addMemberSelection(member);
            } else {
                if (queryDimension.getSelections().size() == 0) {
                    queryDimension.addMemberSelection(member);
                }
            }
        }
        return true;
    }

    public CellSet execute() throws OlapException {
        SelectNode mdx = getSelect();
        String mdxString = mdx.toString();
        OlapStatement olapStatement = connection.createStatement();
        return olapStatement.executeOlapQuery(mdxString);
    }

    public String getName() {
        return name;
    }

    public Locale getLocale() {
        return Locale.getDefault(); // todo:
    }

    public SelectionFactory getSelectionFactory() {
        return selectionFactory;
    }
}

// End Query.java
