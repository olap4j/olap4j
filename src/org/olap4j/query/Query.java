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
package org.olap4j.query;

import org.olap4j.*;
import org.olap4j.mdx.SelectNode;
import org.olap4j.metadata.*;

import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Base query model object.
 *
 * @author jhyde, jdixon, Luc Boudreau
 * @version $Id$
 * @since May 29, 2007
 */
public class Query extends QueryNodeImpl {

    protected final String name;
    protected Map<Axis, QueryAxis> axes = new HashMap<Axis, QueryAxis>();
    protected QueryAxis across;
    protected QueryAxis down;
    protected QueryAxis filter;
    protected QueryAxis unused;
    protected final Cube cube;
    protected Map<String, QueryDimension> dimensionMap =
        new HashMap<String, QueryDimension>();
    /**
     * Whether or not to select the default hierarchy and default
     * member on a dimension if no explicit selections were performed.
     */
    protected boolean selectDefaultMembers = true;
    private final OlapConnection connection;
    private final SelectionFactory selectionFactory = new SelectionFactory();

    /**
     * Constructs a Query object.
     * @param name Any arbitrary name to give to this query.
     * @param cube A Cube object against which to build a query.
     * @throws SQLException If an error occurs while accessing the
     * cube's underlying connection.
     */
    public Query(String name, Cube cube) throws SQLException {
        super();
        this.name = name;
        this.cube = cube;
        final Catalog catalog = cube.getSchema().getCatalog();
        this.connection =
            catalog.getMetaData().getConnection().unwrap(OlapConnection.class);
        this.connection.setCatalog(catalog.getName());
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
        Map<Integer, QueryNode> acrossChildList =
            new HashMap<Integer, QueryNode>();
        for (int cpt = 0; cpt < tmpAcross.size();cpt++) {
            acrossChildList.put(Integer.valueOf(cpt), tmpAcross.get(cpt));
        }
        across.notifyRemove(acrossChildList);

        down.getDimensions().clear();
        Map<Integer, QueryNode> downChildList =
            new HashMap<Integer, QueryNode>();
        for (int cpt = 0; cpt < tmpDown.size();cpt++) {
            downChildList.put(Integer.valueOf(cpt), tmpDown.get(cpt));
        }
        down.notifyRemove(downChildList);

        across.getDimensions().addAll(tmpDown);
        across.notifyAdd(downChildList);

        down.getDimensions().addAll(tmpAcross);
        down.notifyAdd(acrossChildList);
    }

    /**
     * Returns the query axis for a given axis type.
     *
     * <p>If you pass axis=null, returns a special axis that is used to hold
     * all unused hierarchies. (We may change this behavior in future.)
     *
     * @param axis Axis type
     * @return Query axis
     */
    public QueryAxis getAxis(Axis axis) {
        return this.axes.get(axis);
    }

    /**
     * Returns a map of the current query's axis.
     * <p>Be aware that modifications to this list might
     * have unpredictable consequences.</p>
     * @return A standard Map object that represents the
     * current query's axis.
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

    /**
     * Safely disposes of all underlying objects of this
     * query.
     * @param closeConnection Whether or not to call the
     * {@link OlapConnection#close()} method of the underlying
     * connection.
     */
    public void tearDown(boolean closeConnection) {
        for (Entry<Axis, QueryAxis> entry : this.axes.entrySet()) {
            entry.getValue().tearDown();
        }
        this.axes.clear();
        this.clearListeners();
        if (closeConnection) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Safely disposes of all underlying objects of this
     * query and closes the underlying {@link OlapConnection}.
     * <p>Equivalent of calling Query.tearDown(true).
     */
    public void tearDown() {
        this.tearDown(true);
    }

    /**
     * Validates the current query structure. If a dimension axis has
     * been placed on an axis but no selections were performed on it,
     * the default hierarchy and default member will be selected. This
     * can be turned off by invoking the
     * {@link Query#setSelectDefaultMembers(boolean)} method.
     * @throws OlapException If the query is not valid, an exception
     * will be thrown and it's message will describe exactly what to fix.
     */
    public void validate() throws OlapException {
        try {
            // First, perform default selections if needed.
            if (this.selectDefaultMembers) {
                // Perform default selection on the dimensions on the rows axis.
                for (QueryDimension dimension : this.getAxis(Axis.ROWS)
                    .getDimensions())
                {
                    if (dimension.getInclusions().size() == 0) {
                        Member defaultMember = dimension.getDimension()
                            .getDefaultHierarchy().getDefaultMember();
                        dimension.include(defaultMember);
                    }
                }
                // Perform default selection on the
                // dimensions on the columns axis.
                for (QueryDimension dimension : this.getAxis(Axis.COLUMNS)
                    .getDimensions())
                {
                    if (dimension.getInclusions().size() == 0) {
                        Member defaultMember = dimension.getDimension()
                            .getDefaultHierarchy().getDefaultMember();
                        dimension.include(defaultMember);
                    }
                }
                // Perform default selection on the dimensions
                // on the filter axis.
                for (QueryDimension dimension : this.getAxis(Axis.FILTER)
                    .getDimensions())
                {
                    if (dimension.getInclusions().size() == 0) {
                        Member defaultMember = dimension.getDimension()
                            .getDefaultHierarchy().getDefaultMember();
                        dimension.include(defaultMember);
                    }
                }
            }

            // We at least need a dimension on the rows and on the columns axis.
            if (this.getAxis(Axis.ROWS).getDimensions().size() == 0) {
                throw new OlapException(
                    "A valid Query requires at least one dimension on the rows axis.");
            }
            if (this.getAxis(Axis.COLUMNS).getDimensions().size() == 0) {
                throw new OlapException(
                    "A valid Query requires at least one dimension on the columns axis.");
            }

            // Try to build a select tree.
            this.getSelect();
        } catch (Exception e) {
            throw new OlapException("Query validation failed.", e);
        }
    }

    /**
     * Executes the query against the current OlapConnection and returns
     * a CellSet object representation of the data.
     *
     * @return A proper CellSet object that represents the query execution
     *     results.
     * @throws OlapException If something goes sour, an OlapException will
     *     be thrown to the caller. It could be caused by many things, like
     *     a stale connection. Look at the root cause for more details.
     */
    public CellSet execute() throws OlapException {
        SelectNode mdx = getSelect();
        final Catalog catalog = cube.getSchema().getCatalog();
        try {
            this.connection.setCatalog(catalog.getName());
        } catch (SQLException e) {
            throw new OlapException("Error while executing query", e);
        }
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
        // REVIEW Do queries really support locales?
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

    /**
     * Behavior setter for a query. By default, if a dimension is placed on
     * an axis but no selections are made, the default hierarchy and
     * the default member will be selected when validating the query.
     * This behavior can be turned off by this setter.
     * @param selectDefaultMembers Enables or disables the default
     * member and hierarchy selection upon validation.
     */
    public void setSelectDefaultMembers(boolean selectDefaultMembers) {
        this.selectDefaultMembers = selectDefaultMembers;
    }
}

// End Query.java
