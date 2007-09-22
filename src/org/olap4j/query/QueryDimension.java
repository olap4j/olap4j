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

import org.olap4j.metadata.*;

import java.util.List;
import java.util.ArrayList;
import java.util.AbstractList;

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

    public List<Member> resolve(Selection selection)
    {
        assert selection != null;

        switch (selection.getOperator()) {
        case CHILDREN:
            /*
            * TODO: implement CHILDREN operator.
            *
            * need to implement getChildren method or something similar - maybe
            * generate MDX
            return dimension.getChildren(selection.getHierarchyName(), selection.getLevelName(), selection.getName());
            */
            throw new UnsupportedOperationException();
        default:
            // TODO implement other operators
            throw new UnsupportedOperationException();
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
            if (selection.getDimension() != dimension) {
                // TODO raise an exception
                return;
            }
            list.add(index, selection);
        }

        public Selection remove(int index) {
            return list.remove(index);
        }
    }
}

// End QueryDimension.java
