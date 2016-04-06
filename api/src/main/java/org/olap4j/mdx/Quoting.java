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
package org.olap4j.mdx;

/**
 * Enumeration of styles by which the component of an identifier can be
 * quoted.
 *
 * @see org.olap4j.mdx.IdentifierSegment
 *
 * @author jhyde
 */
public enum Quoting {

    /**
     * Unquoted identifier, for example "Measures".
     */
    UNQUOTED,

    /**
     * Quoted identifier, for example "[Measures]".
     */
    QUOTED,

    /**
     * Identifier quoted with an ampersand and brackets to indicate a key
     * value, for example the second segment in "{@code [Employees].&[89]}".
     *
     * <p>Such a segment has one or more sub-segments. Each segment is
     * either quoted or unquoted. For example, the second segment in
     * "{@code [Employees].&[89]&[San Francisco]&CA&USA}" has four sub-segments,
     * two quoted and two unquoted.
     */
    KEY,
}

// End Quoting.java
