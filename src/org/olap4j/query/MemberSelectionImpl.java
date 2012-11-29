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

import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.*;

/**
 * Abstract implementation of {@link Selection}.
 *
 * @author jhyde
 * @since May 30, 2007
 */
class MemberSelectionImpl extends AbstractSelection {

    private Member member;

    /**
     * Creates a SelectionImpl.
     *
     * @pre operator != null
     */
    public MemberSelectionImpl(
        Member member,
        Dimension dimension,
        Operator operator)
    {
        super(dimension, operator);
        this.member = member;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((member == null) ? 0 : member.getUniqueName().hashCode());
        result = prime * result
            + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result
            + ((selectionContext == null) ? 0 : selectionContext.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MemberSelectionImpl)) {
            return false;
        }
        MemberSelectionImpl other = (MemberSelectionImpl) obj;
        if (member == null) {
            if (other.member != null) {
                return false;
            }
        } else if (!member.getUniqueName().equals(
                other.member.getUniqueName()))
        {
            return false;
        }
        if (operator == null) {
            if (other.operator != null) {
                return false;
            }
        } else if (!operator.equals(other.operator)) {
            return false;
        }
        if (selectionContext == null) {
            if (other.selectionContext != null) {
                return false;
            }
        } else if (!selectionContext.equals(other.selectionContext)) {
            return false;
        }
        return true;
    }

    public MetadataElement getRootElement() {
        return member;
    }

    public ParseTreeNode visit() {
        return Olap4jNodeConverter.toOlap4j(member, operator);
    }

    @Override
    public void setOperator(Operator operator) {
        if (operator.equals(Operator.MEMBERS)) {
            throw new IllegalArgumentException(
                "Selections based on a Members cannot be of Operator MEMBERS.");
        }
        super.setOperator(operator);
    }
}

// End MemberSelectionImpl.java



