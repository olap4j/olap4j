/*
// $Id: $
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes which changes were performed to the query model.
 * @author Luc Boudreau
 */
public final class QueryEvent {

    /**
     * Describes the nature of the event.
     */
    public static enum Type {
        /**
         * Some choldren of a QueryNode were removed from
         * it's list.
         */
        CHILDREN_REMOVED,
        /**
         * Some children of a QueryNode were added to
         * it's list.
         */
        CHILDREN_ADDED,
        /**
         * A Selection object operator was changed.
         */
        SELECTION_CHANGED
    }

    private final QueryNode source;
    private final QueryEvent.Type operation;
    private Map<Integer,QueryNode> children =
        new HashMap<Integer, QueryNode>();

    QueryEvent(QueryEvent.Type operation,
            QueryNode source,
            QueryNode child,
            int index)
    {
        this.children.put(Integer.valueOf(index),child);
        this.source = source;
        this.operation = operation;
    }

    QueryEvent(QueryEvent.Type operation,
            QueryNode source,
            Map<Integer,QueryNode> children) {
        this.children.putAll(children);
        this.source = source;
        this.operation = operation;
    }

    QueryEvent(QueryEvent.Type operation,
            QueryNode source) {
        this.children = null;
        this.source = source;
        this.operation = operation;
    }

    /**
     * Returns the object that generated this event.
     */
    public QueryNode getSource() {
        return source;
    }

    /**
     * Returns the event type.
     */
    public QueryEvent.Type getOperation() {
        return operation;
    }

    /**
     * Returns a map of objects affected by the event and
     * their index in the list of the source children.
     * If the event is of type {@link QueryEvent.Type.SELECTION_CHANGED},
     * this method will return null because the source object was affected
     * and not the children.
     */
    public Map<Integer,QueryNode> getChildrens() {
        return children;
    }
}
// End QueryEvent.java