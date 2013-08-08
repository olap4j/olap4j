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

/**
 * Objects that want to be notified of changes to the Query Model structure
 * have to implement this interface.
 *
 * @author Luc Boudreau
 */
public interface QueryNodeListener {
    /**
     * Invoked when one or more children of a QueryNode are removed
     * from its list.
     *
     * @param event Describes in detail the actual event that just happened.
     */
    public void childrenRemoved(QueryEvent event);

    /**
     * Invoked when one or more children are added to a QueryNode
     * list of children.
     *
     * @param event Describes in detail the actual event that just happened.
     */
    public void childrenAdded(QueryEvent event);

    /**
     * Invoked when a selection operator has changed. This does not mean
     * that a Selection object was either added or removed from a Dimension,
     * it only means that its operator value was modified.
     *
     * @param event Describes in detail the actual event that just happened.
     * @see org.olap4j.query.Selection
     **/
    public void selectionChanged(QueryEvent event);
}

// End QueryNodeListener.java
