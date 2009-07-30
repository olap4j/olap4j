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

import org.olap4j.OlapException;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.mdx.IdentifierNode.Segment;
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
 * @author jdixon, jhyde
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

    @Deprecated
    public void select(String... nameParts) {
        this.include(nameParts);
    }

    @Deprecated
    public void select(
        Selection.Operator operator,
        String... nameParts)
    {
        this.include(operator, nameParts);
    }

    @Deprecated
    public void select(Member member) {
        this.include(member);
    }

    @Deprecated
    public void select(
            Selection.Operator operator,
            Member member)
    {
        this.include(operator, member);
    }

    /**
     * Clears the current member inclusions from this query dimension.
     * @deprecated This method is deprecated in favor of
     * {@link QueryDimension#clearInclusions()}
     */
    @Deprecated
    public void clearSelection() {
        this.clearInclusions();
    }

    public void include(String... nameParts) {
        this.include(Selection.Operator.MEMBER, nameParts);
    }

    public void include(
        Selection.Operator operator,
        String... nameParts)
    {
        try {
            this.include(
                operator,
                this.getQuery().getCube().lookupMember(nameParts));
        } catch (OlapException e) {
            // Nothing to do, but we'll still log the exception.
            e.printStackTrace();
        }
    }

    public void include(Member member) {
        include(Selection.Operator.MEMBER, member);
    }

    public void include(
            Selection.Operator operator,
            Member member)
    {
        if (member.getDimension().equals(this.dimension)) {
            Selection selection =
                    query.getSelectionFactory().createMemberSelection(
                            member, operator);
            this.include(selection);
        }
    }

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

    public void exclude(String... nameParts) {
        this.exclude(Selection.Operator.MEMBER, nameParts);
    }

    public void exclude(
        Selection.Operator operator,
        String... nameParts)
    {
        try {
            this.exclude(
                operator,
                this.getQuery().getCube().lookupMember(nameParts));
        } catch (OlapException e) {
            // Nothing to do, but we'll still log the exception.
            e.printStackTrace();
        }
    }

    public void exclude(Member member) {
        exclude(Selection.Operator.MEMBER, member);
    }

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

    public static String[] getNameParts(String sel) {
        List<Segment> list = IdentifierNode.parseIdentifier(sel);
        String nameParts[] = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            nameParts[i] = list.get(i).getName();
        }
        return nameParts;
    }

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
                    getNameParts(selection.getName()));
        } catch (Exception e) {
            throw new OlapException(
                "Error while resolving selection " + selection.toString(),
                e);
        }
    }

    /**
     * Returns a list of the inclusions within this dimension.
     * <p>Be aware that modifications to this list might
     * have unpredictable consequences.</p>
     * @deprecated Use {@link QueryDimension#getInclusions()}
     * @return list of inclusions
     */
    @Deprecated
    public List<Selection> getSelections() {
        return this.getInclusions();
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

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public void setSortOrder(SortOrder order) {
        this.sortOrder = order;
    }

    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    public HierarchizeMode getHierarchizeMode() {
        return hierarchizeMode;
    }

    /**
     * Triggers the hierarchization of the included members within this
     * QueryDimension.
     * @param hierarchizeMode Whether or not to include the POST litteral
     * inside the Hierarchize() MDX function call.
     */
    public void setHierarchizeMode(HierarchizeMode hierarchizeMode) {
        this.hierarchizeMode = hierarchizeMode;
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
     * Defines in what order to perform the sort.
     */
    public static enum SortOrder {
        /**
         * Ascending sort order. Members of
         * the same hierarchy are still kept together.
         */
        ASC,
        /**
         * Descending sort order. Members of
         * the same hierarchy are still kept together.
         */
        DESC,
        /**
         * Sorts in ascending order, but does not
         * maintain members of a same hierarchy
         * together. This is known as a "break
         * hierarchy ascending sort".
         */
        BASC,
        /**
         * Sorts in descending order, but does not
         * maintain members of a same hierarchy
         * together. This is known as a "break
         * hierarchy descending sort".
         */
        BDESC
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
