/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.type.Type;
import org.olap4j.type.MemberType;
import org.olap4j.metadata.Member;

/**
 * Usage of a {@link org.olap4j.metadata.Member} as an expression in an MDX
 * parse tree.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
public class MemberNode implements ParseTreeNode {
    private final Member member;


    public MemberNode(Member member) {
        super();
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return MemberType.forMember(member);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(member.getUniqueName());
    }

    public String toString() {
        return member.getUniqueName();
    }
}

// End MemberNode.java
