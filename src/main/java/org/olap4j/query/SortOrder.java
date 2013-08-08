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
 * Defines in what order to perform sort operations.
 *
 * @author Luc Boudreau
 * @since 0.9.8
 */
public enum SortOrder {
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
// End SortOrder.java
