/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.OlapException;
import org.olap4j.impl.IdentifierParser;
import org.olap4j.mdx.IdentifierSegment;
import org.olap4j.metadata.*;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.AbstractList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Usage of a dimension for an OLAP query.
 *
 * <p>It references an {@link org.olap4j.metadata.Dimension} and allows the
 * query creator to manage the member selections for the dimension.
 * The state of a QueryDimension does not affect the
 * Dimension object in any way so a single Dimension object
 * can be referenced by many QueryDimension objects.
 *
 * @author jdixon, jhyde, Luc Boudreau
 * @version $Id$
 * @since May 29, 2007
 */
public class QueryDimension extends QueryNodeImpl {
    protected QueryAxis axis;
    protected final List<Selection> inclusions = new SelectionList();
    protected final List<Selection> exclusions = new SelectionList();
    private final Query query;
    protected Dimension dimension;
    private SortOrder sortOrder = null;
    private HierarchizeMode hierarchizeMode = null;

    public QueryDimension(Query query, Dimension dimension) {
        super();
        this.query = query;
        this.dimension = dimension;
    }

    public Query getQuery() {
        return query;
    }

    public void setAxis(QueryAxis axis) {
        this.axis = axis;
    }

    public QueryAxis getAxis() {
        return axis;
    }

    public String getName() {
        return dimension.getName();
    }

    /**
     * Selects members and includes them in the query.
     *
     * <p>This method selects and includes a single member with the
     * {@link Selection.Operator#MEMBER} operator.
     *
     * @param nameParts Name of the member to select and include.
     * @throws OlapException If no member corresponding to the supplied
     * name parts could be resolved in the cube.
     */
    public Selection include(
        List<IdentifierSegment> nameParts)
        throws OlapException
    {
        return this.include(Selection.Operator.MEMBER, nameParts);
    }

    public Selection createSelection(
        List<IdentifierSegment> nameParts)
        throws OlapException
    {
        return this.createSelection(Selection.Operator.MEMBER, nameParts);
    }

    /**
     * Selects members and includes them in the query.
     *
     * <p>This method selects and includes a member along with its
     * relatives, depending on the supplied {@link Selection.Operator}
     * operator.
     *
     * @param operator Selection operator that defines what relatives of the
     * supplied member name to include along.
     * @param nameParts Name of the root member to select and include.
     * @throws OlapException If no member corresponding to the supplied
     * name parts could be resolved in the cube.
     */
    public Selection include(
        Selection.Operator operator,
        List<IdentifierSegment> nameParts) throws OlapException
    {
        Member member = this.getQuery().getCube().lookupMember(nameParts);
        if (member == null) {
            throw new OlapException(
                "Unable to find a member with name " + nameParts);
        }
        return this.include(
            operator,
            member);
    }

    public Selection createSelection(
        Selection.Operator operator,
        List<IdentifierSegment> nameParts) throws OlapException
    {
        Member member = this.getQuery().getCube().lookupMember(nameParts);
        if (member == null) {
            throw new OlapException(
                "Unable to find a member with name " + nameParts);
        }
        return this.createSelection(
            operator,
            member);
    }

    /**
     * Selects members and includes them in the query.
     * <p>This method selects and includes a single member with the
     * {@link Selection.Operator#MEMBER} selection operator.
     * @param member The member to select and include in the query.
     */
    public Selection include(Member member) {
        return include(Selection.Operator.MEMBER, member);
    }

    public Selection createSelection(Member member) {
        return createSelection(Selection.Operator.MEMBER, member);
    }

    /**
     * Selects members and includes them in the query.
     * <p>This method selects and includes a member along with it's
     * relatives, depending on the supplied {@link Selection.Operator}
     * operator.
     * @param operator Selection operator that defines what relatives of the
     * supplied member name to include along.
     * @param member Root member to select and include.
     */
    public Selection createSelection(
            Selection.Operator operator,
            Member member)
    {
        if (member.getDimension().equals(this.dimension)) {
            Selection selection =
                    query.getSelectionFactory().createMemberSelection(
                            member, operator);
            return selection;
        }
        return null;
    }

    /**
     * Selects members and includes them in the query.
     * <p>This method selects and includes a member along with it's
     * relatives, depending on the supplied {@link Selection.Operator}
     * operator.
     * @param operator Selection operator that defines what relatives of the
     * supplied member name to include along.
     * @param member Root member to select and include.
     */
    public Selection include(
            Selection.Operator operator,
            Member member)
    {
        if (member.getDimension().equals(this.dimension)) {
            Selection selection =
                    query.getSelectionFactory().createMemberSelection(
                            member, operator);
            this.include(selection);
            return selection;
        }
        return null;
    }

    /**
     * Includes a selection of members in the query.
     * @param selection The selection of members to include.
     */
    private void include(Selection selection) {
        this.getInclusions().add(selection);
        Integer index = Integer.valueOf(
                this.getInclusions().indexOf(selection));
        this.notifyAdd(selection, index);
    }

    /**
     * Clears the current member inclusions from this query dimension.
     */
    public void clearInclusions() {
        Map<Integer, QueryNode> removed = new HashMap<Integer, QueryNode>();
        for (Selection node : this.inclusions) {
            removed.put(
                Integer.valueOf(this.inclusions.indexOf(node)),
                node);
            ((QueryNodeImpl) node).clearListeners();
        }
        this.inclusions.clear();
        this.notifyRemove(removed);
    }

    /**
     * Selects members and excludes them from the query.
     *
     * <p>This method selects and excludes a single member with the
     * {@link Selection.Operator#MEMBER} operator.
     *
     * @param nameParts Name of the member to select and exclude.
     * @throws OlapException If no member corresponding to the supplied
     * name parts could be resolved in the cube.
     */
    public void exclude(
        List<IdentifierSegment> nameParts)
        throws OlapException
    {
        this.exclude(Selection.Operator.MEMBER, nameParts);
    }

    /**
     * Selects members and excludes them from the query.
     *
     * <p>This method selects and excludes a member along with its
     * relatives, depending on the supplied {@link Selection.Operator}
     * operator.
     *
     * @param operator Selection operator that defines what relatives of the
     * supplied member name to exclude along.
     * @param nameParts Name of the root member to select and exclude.
     * @throws OlapException If no member corresponding to the supplied
     * name parts could be resolved in the cube.
     */
    public void exclude(
        Selection.Operator operator,
        List<IdentifierSegment> nameParts) throws OlapException
    {
        Member rootMember = this.getQuery().getCube().lookupMember(nameParts);
        if (rootMember == null) {
            throw new OlapException(
                "Unable to find a member with name " + nameParts);
        }
        this.exclude(
            operator,
            rootMember);
    }

    /**
     * Selects members and excludes them from the query.
     * <p>This method selects and excludes a single member with the
     * {@link Selection.Operator#MEMBER} selection operator.
     * @param member The member to select and exclude from the query.
     */
    public void exclude(Member member) {
        exclude(Selection.Operator.MEMBER, member);
    }

    /**
     * Selects members and excludes them from the query.
     * <p>This method selects and excludes a member along with it's
     * relatives, depending on the supplied {@link Selection.Operator}
     * operator.
     * @param operator Selection operator that defines what relatives of the
     * supplied member name to exclude along.
     * @param member Root member to select and exclude.
     */
    public void exclude(
            Selection.Operator operator,
            Member member)
    {
        if (member.getDimension().equals(this.dimension)) {
            Selection selection =
                    query.getSelectionFactory().createMemberSelection(
                            member, operator);
            this.exclude(selection);
        }
    }

    /**
     * Excludes a selection of members from the query.
     * @param selection The selection of members to exclude.
     */
    private void exclude(Selection selection) {
        this.getExclusions().add(selection);
        Integer index = Integer.valueOf(
                this.getExclusions().indexOf(selection));
        this.notifyAdd(selection, index);
    }

    /**
     * Clears the current member inclusions from this query dimension.
     */
    public void clearExclusions() {
        Map<Integer, QueryNode> removed = new HashMap<Integer, QueryNode>();
        for (Selection node : this.exclusions) {
            removed.put(
                Integer.valueOf(this.exclusions.indexOf(node)),
                node);
            ((QueryNodeImpl) node).clearListeners();
        }
        this.exclusions.clear();
        this.notifyRemove(removed);
    }

    /**
     * Resolves a selection of members into an actual list
     * of the root member and it's relatives selected by the Selection object.
     * @param selection The selection of members to resolve.
     * @return A list of the actual members selected by the selection object.
     * @throws OlapException If resolving the selections triggers an exception
     * while looking up members in the underlying cube.
     */
    public List<Member> resolve(Selection selection) throws OlapException
    {
        assert selection != null;
        final Member.TreeOp op;
        Member.TreeOp secondOp = null;
        switch (selection.getOperator()) {
        case CHILDREN:
            op = Member.TreeOp.CHILDREN;
            break;
        case SIBLINGS:
            op = Member.TreeOp.SIBLINGS;
            break;
        case INCLUDE_CHILDREN:
            op = Member.TreeOp.SELF;
            secondOp = Member.TreeOp.CHILDREN;
            break;
        case MEMBER:
            op = Member.TreeOp.SELF;
            break;
        default:
            throw new OlapException(
                "Operation not supported: " + selection.getOperator());
        }
        Set<Member.TreeOp> set = new TreeSet<Member.TreeOp>();
        set.add(op);
        if (secondOp != null) {
            set.add(secondOp);
        }
        try {
            return
                query.getCube().lookupMembers(
                    set,
                    IdentifierParser.parseIdentifier(selection.getName()));
        } catch (Exception e) {
            throw new OlapException(
                "Error while resolving selection " + selection.toString(),
                e);
        }
    }

    /**
     * Returns a list of the inclusions within this dimension.
     *
     * <p>Be aware that modifications to this list might
     * have unpredictable consequences.</p>
     *
     * @return list of inclusions
     */
    public List<Selection> getInclusions() {
        return inclusions;
    }

    /**
     * Returns a list of the exclusions within this dimension.
     *
     * <p>Be aware that modifications to this list might
     * have unpredictable consequences.</p>
     *
     * @return list of exclusions
     */
    public List<Selection> getExclusions() {
        return exclusions;
    }

    /**
     * Returns the underlying dimension object onto which
     * this query dimension is based.
     * <p>Returns a mutable object so operations on it have
     * unpredictable consequences.
     * @return The underlying dimension representation.
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Forces a change onto which dimension is the current
     * base of this QueryDimension object.
     * <p>Forcing a change in the duimension assignment has
     * unpredictable consequences.
     * @param dimension The new dimension to assign to this
     * query dimension.
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    /**
     * Sorts the dimension members by name in the
     * order supplied as a parameter.
     * @param order The {@link SortOrder} to use.
     */
    public void sort(SortOrder order) {
        this.sortOrder = order;
    }

    /**
     * Returns the current order in which the
     * dimension members are sorted.
     * @return A value of {@link SortOrder}
     */
    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    /**
     * Clears the current sorting settings.
     */
    public void clearSort() {
        this.sortOrder = null;
    }

    /**
     * Returns the current mode of hierarchization, or null
     * if no hierarchization is currently performed.
     *
     * <p>This capability is only available when a single dimension is
     * selected on an axis
     *
     * @return Either a hierarchization mode value or null
     *     if no hierarchization is currently performed.
     */
    public HierarchizeMode getHierarchizeMode() {
        return hierarchizeMode;
    }

    /**
     * Triggers the hierarchization of the included members within this
     * QueryDimension.
     *
     * <p>The dimension inclusions will be wrapped in an MDX Hierarchize
     * function call.
     *
     * <p>This capability is only available when a single dimension is
     * selected on an axis.
     *
     * @param hierarchizeMode If parents should be included before or after
     * their children. (Equivalent to the POST/PRE MDX literal for the
     * Hierarchize() function)
     * inside the Hierarchize() MDX function call.
     */
    public void setHierarchizeMode(HierarchizeMode hierarchizeMode) {
        this.hierarchizeMode = hierarchizeMode;
    }

    /**
     * Tells the QueryDimension not to hierarchize its included
     * selections.
     *
     * <p>This capability is only available when a single dimension is
     * selected on an axis.
     */
    public void clearHierarchizeMode() {
        this.hierarchizeMode = null;
    }

    private class SelectionList extends AbstractList<Selection> {
        private final List<Selection> list = new ArrayList<Selection>();

        public Selection get(int index) {
            return list.get(index);
        }

        public int size() {
            return list.size();
        }

        public Selection set(int index, Selection selection) {
            return list.set(index, selection);
        }

        public void add(int index, Selection selection) {
            if (this.contains(selection)) {
                throw new IllegalStateException(
                    "dimension already contains selection");
            }
            list.add(index, selection);
        }

        public Selection remove(int index) {
            return list.remove(index);
        }
    }

    /**
     * Defines in which way the hierarchize operation
     * should be performed.
     */
    public static enum HierarchizeMode {
        /**
         * Parents are placed before children.
         */
        PRE,
        /**
         * Parents are placed after children
         */
        POST
    }

    void tearDown() {
        for (Selection node : this.inclusions) {
            ((QueryNodeImpl)node).clearListeners();
        }
        for (Selection node : this.exclusions) {
            ((QueryNodeImpl)node).clearListeners();
        }
        this.inclusions.clear();
        this.exclusions.clear();
    }
}

// End QueryDimension.java
