/*
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

import java.util.*;

/**
 * Describes which changes were performed to the query model.
 *
 * @author Luc Boudreau
 */
public final class QueryEvent {

    /**
     * Describes the nature of the event.
     */
    public static enum Type {
        /**
         * Event where one or more children of a QueryNode were removed.
         */
        CHILDREN_REMOVED,
        /**
         * Event where one or more nodes were added as children of a QueryNode.
         */
        CHILDREN_ADDED,
        /**
         * Event where a Selection object operator was changed.
         */
        SELECTION_CHANGED
    }

    private final QueryNode source;
    private final QueryEvent.Type operation;
    private final Map<Integer, QueryNode> children;

    /**
     * Creates a QueryEvent with a single child.
     *
     * @param operation Even type
     * @param source Query node that generated this event
     * @param child Child node
     */
    QueryEvent(
        QueryEvent.Type operation,
        QueryNode source,
        QueryNode child,
        int index)
    {
        this.children = Collections.singletonMap(index, child);
        this.source = source;
        this.operation = operation;
    }

    /**
     * Creates a QueryEvent with multiple children.
     *
     * @param operation Even type
     * @param source Query node that generated this event
     * @param children Child nodes and their indexes within the parent
     */
    QueryEvent(
        QueryEvent.Type operation,
        QueryNode source,
        Map<Integer, QueryNode> children)
    {
        // copy the map, and make immutable
        this.children =
            Collections.unmodifiableMap(
                new HashMap<Integer, QueryNode>(children));
        this.source = source;
        this.operation = operation;
    }

    /**
     * Creates a QueryEvent with no children.
     *
     * @param operation Even type
     * @param source Query node that generated this event
     */
    QueryEvent(
        QueryEvent.Type operation,
        QueryNode source)
    {
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
    // REVIEW: consider renaming to 'getEventType', or rename enum Type to
    // Operation.
    public QueryEvent.Type getOperation() {
        return operation;
    }

    /**
     * Returns a map of objects affected by the event and
     * their index in the list of the source children.
     *
     * <p>If the event is of type {@link QueryEvent.Type#SELECTION_CHANGED},
     * this method will return null because the source object was affected
     * and not the children.
     */
    // REVIEW: 'children' is already plural. Consider renaming to 'getChildren'
    // or 'getChildNodes'.
    public Map<Integer, QueryNode> getChildrens() {
        return children;
    }
}

// End QueryEvent.java
