/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.metadata.Dimension;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of a selection.
 * @author LBoudreau
 * @version $Id:$
 */
abstract class AbstractSelection extends QueryNodeImpl implements Selection {

    Operator operator;
    Dimension dimension;
    List<Selection> selectionContext;

    public AbstractSelection(
        Dimension dimension,
        Operator operator)
    {
        this.dimension = dimension;
        this.operator = operator;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        assert operator != null;
        this.operator = operator;
        notifyChange(this,-1);
    }

    void tearDown() {
    }

    public List<Selection> getSelectionContext() {
        return selectionContext;
    }

    public void addContext(Selection selection) {
        if (selectionContext == null) {
            selectionContext = new ArrayList<Selection>();
        }
        selectionContext.add(selection);
    }

    public void removeContext(Selection selection) {
        selectionContext.remove(selection);
    }

    public String getUniqueName() {
        return getRootElement().getUniqueName();
    }
}

// End AbstractSelection.java
