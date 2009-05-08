/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
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
 * @version $Id$
 * @since May 29, 2007
 */
public class Query {

    protected final String name;
    protected Map<Axis, QueryAxis> axes = new HashMap<Axis, QueryAxis>();
    protected QueryAxis across;
    protected QueryAxis down;
    protected QueryAxis filter;
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
            cube.getSchema().getCatalog().getMetaData()
                .getConnection().unwrap(OlapConnection.class);
        this.unused = new QueryAxis(this, null);
        for (Dimension dimension : cube.getDimensions()) {
            QueryDimension queryDimension = new QueryDimension(
                this, dimension);
            unused.getDimensions().add(queryDimension);
            dimensionMap.put(queryDimension.getName(), queryDimension);
        }
        across = new QueryAxis(this, Axis.COLUMNS);
        down = new QueryAxis(this, Axis.ROWS);
        filter = new QueryAxis(this, Axis.FILTER);
        axes.put(null, unused);
        axes.put(Axis.COLUMNS, across);
        axes.put(Axis.ROWS, down);
        axes.put(Axis.FILTER, filter);
    }

    /**
     * Returns the MDX parse tree behind this Query. The returned object is
     * generated for each call to this function. Altering the returned
     * SelectNode object won't affect the query itself.
     * @return A SelectNode object representing the current query structure.
     */
    public SelectNode getSelect() {
        return Olap4jNodeConverter.toOlap4j(this);
    }

    /**
     * Returns the underlying cube object that is used to query against.
     * @return The Olap4j's Cube object.
     */
    public Cube getCube() {
        return cube;
    }

    /**
     * Returns the Olap4j's Dimension object according to the name
     * given as a parameter. If no dimension of the given name is found,
     * a null value will be returned.
     * @param name The name of the dimension you want the object for.
     * @return The dimension object, null if no dimension of that
     * name can be found.
     */
    public QueryDimension getDimension(String name) {
        return dimensionMap.get(name);
    }

    /**
     * Swaps rows and columns axes. Only applicable if there are two axes.
     */
    public void swapAxes() {
        // Only applicable if there are two axes - plus filter and unused.
        if (axes.size() != 4) {
            throw new IllegalArgumentException();
        }
        List<QueryDimension> tmpAcross = new ArrayList<QueryDimension>();
        tmpAcross.addAll(across.getDimensions());

        List<QueryDimension> tmpDown = new ArrayList<QueryDimension>();
        tmpDown.addAll(down.getDimensions());

        across.getDimensions().clear();
        down.getDimensions().clear();

        across.getDimensions().addAll(tmpDown);
        down.getDimensions().addAll(tmpAcross);
    }

    /**
     * Returns a map of the current query's axis.
     * @return A standard Map object that represents the current query's axis.
     */
    public Map<Axis, QueryAxis> getAxes() {
        return axes;
    }

    /**
     * Returns the fictional axis into which all unused dimensions are stored.
     * All dimensions included in this axis will not be part of the query.
     * @return The QueryAxis representing dimensions that are currently not
     * used inside the query.
     */
    public QueryAxis getUnusedAxis() {
        return unused;
    }

    public boolean validate() throws OlapException {
        /*
         * FIXME What's the exact purpose of this validation process?
         * To start with, it doesn't even really validate... it iterates
         * over the cube dimensions, but it should iterate over the selected
         * query dimensions instead, thus saving computation time.
         * Second, it doesn't actually validate. It just selects default
         * dimension members in the default hierarchy. Do we really want
         * to add such arbitrary behavior in a query layer?
         * This needs work. I'll put something in the tracker.
         */
        for (Dimension dimension :  cube.getDimensions()) {
            QueryDimension queryDimension =
                getDimension(dimension.getName());
            if (queryDimension == null) {
                return false;
            }
            Member member = dimension.getDefaultHierarchy().getDefaultMember();
            if (queryDimension.getAxis() == null ||
                queryDimension.getAxis().getLocation() == null) {
                queryDimension.getSelections().clear();
                queryDimension.getSelections()
                    .add(queryDimension.createSelection(member));
            } else {
                if (queryDimension.getSelections().size() == 0) {
                    queryDimension.getSelections()
                        .add(queryDimension.createSelection(member));
                }
            }
        }
        return true;
    }

    /**
     * Executes the query against the current OlapConnection and returns
     * a CellSet object representation of the data.
     * @return A proper CellSet object that represents the query execution
     * results.
     * @throws OlapException If something goes sour, an OlapException will
     * be thrown to the caller. It could be caused by many things, like
     * a stale connection. Look at the root cause for more details.
     */
    public CellSet execute() throws OlapException {
        SelectNode mdx = getSelect();
        OlapStatement olapStatement = connection.createStatement();
        return olapStatement.executeOlapQuery(mdx);
    }

    /**
     * Returns this query's name. There is no guarantee that it is unique
     * and is set at object instanciation.
     * @return This query's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current locale with which this query is expressed.
     * @return A standard Locale object.
     */
    public Locale getLocale() {
        // TODO Do queries really support locales?
        return Locale.getDefault();
    }

    /**
     * Package restricted method to access this query's selection factory.
     * Usually used by query dimensions who wants to perform selections.
     * @return The underlying SelectionFactory implementation.
     */
    SelectionFactory getSelectionFactory() {
        return selectionFactory;
    }
}

// End Query.java