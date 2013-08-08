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
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.metadata.Member;

import java.util.*;

/**
 * Can read metadata, in particular members.
 *
 * @author jhyde
 * @since Jan 14, 2008
 */
interface MetadataReader {
    /**
     * Looks up a member by its unique name.
     *
     * <p>Not part of public olap4j API.
     *
     * @param memberUniqueName Unique name of member
     * @return Member, or null if not found
     * @throws org.olap4j.OlapException if error occurs
     */
    XmlaOlap4jMember lookupMemberByUniqueName(
        String memberUniqueName)
        throws OlapException;

    /**
     * Looks up a list of members by their unique name and writes the results
     * into a map.
     *
     * <p>Not part of public olap4j API.
     *
     * @param memberUniqueNames List of unique names of member
     *
     * @param memberMap Map to populate with members
     *
     * @throws org.olap4j.OlapException if error occurs
     */
    void lookupMembersByUniqueName(
        List<String> memberUniqueNames,
        Map<String, XmlaOlap4jMember> memberMap)
        throws OlapException;

    /**
     * Looks a member by its unique name and returns members related by
     * the specified tree-operations.
     *
     * <p>Not part of public olap4j API.
     *
     * @param memberUniqueName Unique name of member
     *
     * @param treeOps Collection of tree operations to travel relative to
     * given member in order to create list of members
     *
     * @param list list to be populated with members related to the given
     * member, or empty set if the member is not found
     *
     * @throws org.olap4j.OlapException if error occurs
     */
    void lookupMemberRelatives(
        Set<Member.TreeOp> treeOps,
        String memberUniqueName,
        List<XmlaOlap4jMember> list) throws OlapException;

    /**
     * Looks up members of a given level.
     *
     * @param level Level
     *
     * @throws org.olap4j.OlapException if error occurs
     *
     * @return list of members at in the level
     */
    List<XmlaOlap4jMember> getLevelMembers(XmlaOlap4jLevel level)
        throws OlapException;
}

// End MetadataReader.java
