/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of QueryNode that
 * implements operations to support listeners.
 * @author Luc Boudreau
 *
 */
abstract class QueryNodeImpl implements QueryNode {

    private final List<QueryNodeListener> listeners =
        new ArrayList<QueryNodeListener>();

    public void addQueryNodeListener(QueryNodeListener l) {
        this.listeners.add(l);
    }

    public void removeQueryNodeListener(QueryNodeListener l) {
        this.listeners.remove(l);
    }

    /**
     * Subclasses should call this helper method to
     * notify it's listeners that a child was added.
     * @param child A reference to the child that was added.
     * @param index The index at which it was added.
     */
    protected void notifyAdd(QueryNode child, int index)
    {
        QueryEvent event = new QueryEvent(
                QueryEvent.Type.CHILDREN_ADDED,
                this,
                child,
                index);
        notifyAddInternal(event);
    }

    /**
     * Subclasses should call this helper method to
     * notify it's listeners that children were added.
     * @param children A map of indexes and children QueryNode
     * objects that were just added.
     */
    protected void notifyAdd(Map<Integer,QueryNode> children)
    {
        QueryEvent event = new QueryEvent(
                QueryEvent.Type.CHILDREN_ADDED,
                this,
                children);
        notifyAddInternal(event);
    }

    private void notifyAddInternal(QueryEvent event) {
        // Must count backwards to prevent an exception
        // if a child removes itself from the listeners list
        // while this call is active.
        for (int cpt = this.listeners.size() - 1; cpt >= 0; cpt--) {
            this.listeners.get(cpt).childrenAdded(event);
        }
    }

    /**
     * Subclasses should call this helper method to
     * notify it's listeners that a child was removed.
     * @param child A reference to the child that was removed.
     * @param index The index at which it was removed.
     */
    protected void notifyRemove(QueryNode child, int index)
    {
        QueryEvent event = new QueryEvent(
                QueryEvent.Type.CHILDREN_REMOVED,
                this,
                child,
                index);
        notifyRemoveInternal(event);
    }

    /**
     * Subclasses should call this helper method to
     * notify it's listeners that children were added.
     * @param children A map of indexes and children QueryNode
     * objects that were just added.
     */
    protected void notifyRemove(Map<Integer,QueryNode> children)
    {
        QueryEvent event = new QueryEvent(
                QueryEvent.Type.CHILDREN_REMOVED,
                this,
                children);
        notifyRemoveInternal(event);
    }

    private void notifyRemoveInternal(QueryEvent event) {
        // Must count backwards to prevent an exception
        // if a child removes itself from the listeners list
        // while this call is active.
        for (int cpt = this.listeners.size() - 1; cpt >= 0; cpt--) {
            this.listeners.get(cpt).childrenRemoved(event);
        }
    }

    /**
     * Subclasses should call this helper method to
     * notify it's listeners that a child selection
     * object has a new operator value.
     * @param child A reference to the child that was updated.
     * @param index The index of the updated object in its
     * parent's list.
     */
    protected void notifyChange(QueryNode child, int index)
    {
        QueryEvent event = new QueryEvent(
                QueryEvent.Type.SELECTION_CHANGED,
                this,
                child,
                index);
        notifyChangeInternal(event);
    }

    /**
     * Subclasses should call this helper method to
     * notify it's listeners that children selections
     * object has a new operator value.
     * @param child A reference to the child that was updated.
     * @param index The index of the updated object in its
     * parent's list.
     */
    protected void notifyChange(Map<Integer,QueryNode> children)
    {
        QueryEvent event = new QueryEvent(
                QueryEvent.Type.SELECTION_CHANGED,
                this,
                children);
        notifyChangeInternal(event);
    }

    private void notifyChangeInternal(QueryEvent event) {
        // Must count backwards to prevent an exception
        // if a child removes itself from the listeners list
        // while this call is active.
        for (int cpt = this.listeners.size() - 1; cpt >= 0; cpt--) {
            this.listeners.get(cpt).selectionChanged(event);
        }
    }

    void clearListeners() {
        this.listeners.clear();
    }

    abstract void tearDown();
}
// End QueryNodeImpl.java