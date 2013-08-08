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
 * Describes what methods a Query Node must implement
 * in order to support listeners. Olap4j's query model
 * provides an abstract implementation of interface.
 *
 * @author Luc Boudreau
 */
interface QueryNode {

    /**
     * Registers a new listener for a QueryNode.
     * @param l The new listener object, implementation of QueryNodeListener
     * @see org.olap4j.query.QueryNodeListener
     */
    public void addQueryNodeListener(QueryNodeListener l);

    /**
     * De-registers a new listener for a QueryNode.
     * If the listener object passed as a parameter was not registered,
     * the method will return silently.
     * @param l The listener object to de-register.
     * @see org.olap4j.query.QueryNodeListener
     */
    public void removeQueryNodeListener(QueryNodeListener l);
}
// End QueryNode.java
