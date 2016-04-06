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
 * Defines the function used to limit an optionally sorted set.
 *
 * @author Paul Stoellberger
 * @since 1.0.1
 */
public enum LimitFunction {
    /**
     * Returns a specified number of items from the
     * top of a set, optionally ordering the set first.
     */
    TopCount,
    /**
     * Sorts a set and returns the top N elements
     * whose cumulative total is at least a specified percentage.
     */
    TopPercent,
    /**
     * Sorts a set and returns the top N elements
     * whose cumulative total is at least a specified value.
     */
    TopSum,
    /**
     * Returns a specified number of items from the
     * bottom of a set, optionally ordering the set first.
     */
    BottomCount,
    /**
     * Sorts a set and returns the bottom N elements
     * whose cumulative total is at least a specified percentage.
     */
    BottomPercent,
    /**
     * Sorts a set and returns the bottom N elements
     * whose cumulative total is at least a specified value.
     */
    BottomSum
}

// End LimitFunction.java
