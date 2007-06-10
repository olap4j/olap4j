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

/**
 * Usage of a dimension for an OLAP query.
 *
 * <p>It references an {@link org.olap4j.metadata.Dimension} and allows the
 * query creator to manage the member selections for the dimension.
 * The state of a QueryDimension does not affect the
 * Dimension object in any way so a single Dimension object
 * can be referenced by many QueryDimension objects.
 *
 * <p>TODO: Make the list returned from {@link #getSelections()} mutable,
 * and obsolete methods such as {@link #addSelection(Selection)}. But
 * must still remember to test all of the ways selections can be added/removed.
 *
 * @author jdixon, jhyde
 * @version $Id$
 * @since May 29, 2007
 */
public class QueryDimension {
    protected QueryAxis axis;
    protected List<Selection> selections = new ArrayList<Selection>();
    private final Query query;
    protected Dimension dimension;

    public QueryDimension(Query query, Dimension dimension) {
        super();
        this.query = query;
        this.dimension = dimension;
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


    public void addSelection(Selection selection) {
        if (selection.getDimension() != dimension) {
            // TODO raise an exception
            return;
        }
        selections.add(selection);
    }

    public void addMemberSelection(Member member) {
        assert member.getDimension() == dimension : "pre";
        selections.add(query.getSelectionFactory().createMemberSelection(member));
    }

    // todo: make selection list mutable and remove this method
    public void addSelections(List<Selection> selections) {
        this.selections.addAll(selections);
    }

    public void addMemberSelections(List<Member> members) {
        for (Member member : members) {
            addMemberSelection(member);
        }
    }

    // todo: make selection list mutable and remove this method
    public void clearSelections() {
        selections.clear();
    }

    public Selection createSelection(String hierarchyName, String levelName, String memberName) {
        return createSelection(hierarchyName, levelName, memberName, Selection.Operator.MEMBER);
    }

    public Selection createSelection(
        String hierarchyName,
        String levelName,
        String memberName,
        Selection.Operator operator)
    {
        Selection selection = null;
        Hierarchy hierarchy = dimension.getHierarchies().get(hierarchyName);
        if (hierarchy != null) {
            Level level = hierarchy.getLevels().get(levelName);
            if (level != null) {
                Member member = level.findMember(memberName);
                if (member != null) {
                    selection = query.getSelectionFactory().createMemberSelection(member);
                }
            }
        }

        if (selection != null) {
            selection.setOperator(operator);
        }
        return selection;
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

    public List<Selection> getSelections() {
        return selections;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }
}

// End QueryDimension.java
