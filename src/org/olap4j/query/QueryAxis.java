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

import org.olap4j.Axis;
import org.olap4j.OlapException;
import org.olap4j.mdx.IdentifierSegment;
import org.olap4j.metadata.Measure;
import org.olap4j.metadata.Member;

import java.util.*;

/**
 * An axis within an OLAP {@link Query}.
 *
 * <p>An axis has a location (columns, rows, etc) and has zero or more
 * dimensions that are placed on it.
 *
 * @author jdixon, Luc Boudreau
 * @version $Id$
 * @since May 29, 2007
 */
public class QueryAxis extends QueryNodeImpl {

    protected final List<QueryDimension> dimensions = new DimensionList();

    private final Query query;
    protected Axis location = null;
    private boolean nonEmpty;
    private SortOrder sortOrder = null;
    private String sortEvaluationLiteral = null;

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
     * <p>Be aware that modifications to this list might
     * have unpredictable consequences.</p>
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
        Map<Integer, QueryNode> removed = new HashMap<Integer, QueryNode>();
        removed.put(Integer.valueOf(index), this.dimensions.get(index));
        Map<Integer, QueryNode> added = new HashMap<Integer, QueryNode>();
        added.put(Integer.valueOf(index - 1), this.dimensions.get(index));
        Collections.swap(this.dimensions, index, index - 1);
        this.notifyRemove(removed);
        this.notifyAdd(added);
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
        Map<Integer, QueryNode> removed = new HashMap<Integer,  QueryNode>();
        removed.put(Integer.valueOf(index), this.dimensions.get(index));
        Map<Integer, QueryNode> added = new HashMap<Integer, QueryNode>();
        added.put(Integer.valueOf(index + 1), this.dimensions.get(index));
        Collections.swap(this.dimensions, index, index + 1);
        this.notifyRemove(removed);
        this.notifyAdd(added);
    }

    /**
     * Places a {@link QueryDimension} object on this axis.
     * @param dimension The {@link QueryDimension} object to add
     * to this axis.
     */
    public void addDimension(QueryDimension dimension) {
        this.getDimensions().add(dimension);
        Integer index = Integer.valueOf(
            this.getDimensions().indexOf(dimension));
        this.notifyAdd(dimension, index);
    }

    /**
     * Places a {@link QueryDimension} object on this axis at
     * a specific index.
     * @param dimension The {@link QueryDimension} object to add
     * to this axis.
     * @param index The position (0 based) onto which to place
     * the QueryDimension
     */
    public void addDimension(int index, QueryDimension dimension) {
        this.getDimensions().add(index, dimension);
        this.notifyAdd(dimension, index);
    }

    /**
     * Removes a {@link QueryDimension} object on this axis.
     * @param dimension The {@link QueryDimension} object to remove
     * from this axis.
     */
    public void removeDimension(QueryDimension dimension) {
        Integer index = Integer.valueOf(
            this.getDimensions().indexOf(dimension));
        this.getDimensions().remove(dimension);
        this.notifyRemove(dimension, index);
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
                throw new IllegalStateException(
                    "dimension already on this axis");
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

    void tearDown() {
        for (QueryDimension node : this.getDimensions()) {
            node.tearDown();
        }
        this.clearListeners();
        this.getDimensions().clear();
    }

    /**
     * <p>Sorts the axis according to the supplied order. The sort evaluation
     * expression will be the default member of the default hierarchy of
     * the dimension named "Measures".
     * @param order The {@link SortOrder} to apply
     * @throws OlapException If an error occurs while resolving
     * the default measure of the underlying cube.
     */
    public void sort(SortOrder order) throws OlapException {
        sort(
            order,
            query.getCube().getDimensions().get("Measures")
                .getDefaultHierarchy().getDefaultMember());
    }

    /**
     * Sorts the axis according to the supplied order
     * and member unique name.
     *
     * <p>Using this method will try to resolve the supplied name
     * parts from the underlying cube and find the corresponding
     * member. This member will then be passed as a sort evaluation
     * expression.
     *
     * @param order The {@link SortOrder} in which to
     * sort the axis.
     * @param nameParts The unique name parts of the sort
     * evaluation expression.
     * @throws OlapException If the supplied member cannot be resolved
     *     with {@link org.olap4j.metadata.Cube#lookupMember(java.util.List)}
     */
    public void sort(SortOrder order, List<IdentifierSegment> nameParts)
        throws OlapException
    {
        assert order != null;
        assert nameParts != null;
        Member member = query.getCube().lookupMember(nameParts);
        if (member == null) {
            throw new OlapException("Cannot find member.");
        }
        sort(order, member);
    }

    /**
     * <p>Sorts the axis according to the supplied order
     * and member.
     * <p>This method is most commonly called by passing
     * it a {@link Measure}.
     * @param order The {@link SortOrder} in which to
     * sort the axis.
     * @param member The member that will be used as a sort
     * evaluation expression.
     */
    public void sort(SortOrder order, Member member) {
        assert order != null;
        assert member != null;
        sort(order, member.getUniqueName());
    }

    /**
     * <p>Sorts the axis according to the supplied order
     * and evaluation expression.
     * <p>The string value passed as the sortEvaluationLitteral
     * parameter will be used literally as a sort evaluator.
     * @param order The {@link SortOrder} in which to
     * sort the axis.
     * @param sortEvaluationLiteral The literal expression that
     * will be used to sort against.
     */
    public void sort(SortOrder order, String sortEvaluationLiteral) {
        assert order != null;
        assert sortEvaluationLiteral != null;
        this.sortOrder = order;
        this.sortEvaluationLiteral = sortEvaluationLiteral;
    }

    /**
     * Clears the sort parameters from this axis.
     */
    public void clearSort() {
        this.sortEvaluationLiteral = null;
        this.sortOrder = null;
    }

    /**
     * Returns the current sort order in which this
     * axis will be sorted. Might return null of none
     * is currently specified.
     * @return The {@link SortOrder}
     */
    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    /**
     * Returns the current sort evaluation expression,
     * or null if none are currently defined.
     * @return The string literal that will be used in the
     * MDX Order() function.
     */
    public String getSortIdentifierNodeName() {
        return sortEvaluationLiteral;
    }
}

// End QueryAxis.java


