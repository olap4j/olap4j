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

import java.util.List;
import java.util.ArrayList;
import java.util.AbstractList;
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
public class QueryDimension {
    protected QueryAxis axis;
    protected final List<Selection> selections = new SelectionList();
    private final Query query;
    protected Dimension dimension;
    private SortOrder sortOrder = null;

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

    public Selection createSelection(Member member)
    {
        return createSelection(member, Selection.Operator.MEMBER);
    }

    public Selection createSelection(
        Member member,
        Selection.Operator operator)
    {
        return query.getSelectionFactory().createMemberSelection(
            member, operator);
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
            throw new OlapException("Error while resolving selection " + selection.toString(), e);
        }
    }

    /**
     * Returns a list of the selections within this
     * <code>QueryDimension</code>.
     *
     * <p>The list is mutable; you may call
     * <code>getSelections().clear()</code>,
     * or <code>getSelections().add(dimension)</code>, for instance.</p>
     *
     * @return list of selections
     */
    public List<Selection> getSelections() {
        return selections;
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
                throw new IllegalStateException("dimension already contains selection");
            }
            list.add(index, selection);
        }

        public Selection remove(int index) {
            return list.remove(index);
        }
    }

    public static enum SortOrder {
        /**
         * Ascending sort order.
         */
        ASC,
        /**
         * Descending sort order.
         */
        DESC
    }
}

// End QueryDimension.java
