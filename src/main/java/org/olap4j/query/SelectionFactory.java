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

import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;

/**
 * Contains factory methods for creating implementations of {@link Selection}.
 *
 * <p>Created using {@link Query#getSelectionFactory()}.
 *
 * @author jhyde
 * @since May 30, 2007
 */
class SelectionFactory {

    Selection createMemberSelection(
        Member member,
        Selection.Operator operator)
    {
        return
            new MemberSelectionImpl(
                member,
                member.getDimension(),
                operator);
    }

    Selection createLevelSelection(Level level)
    {
        return
            new LevelSelectionImpl(
                level,
                level.getDimension(),
                Selection.Operator.MEMBERS);
    }
}

// End SelectionFactory.java
