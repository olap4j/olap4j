/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.metadata.Member;
import org.olap4j.type.MemberType;
import org.olap4j.type.Type;

/**
 * Usage of a {@link org.olap4j.metadata.Member} as an expression in an MDX
 * parse tree.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
public class MemberNode implements ParseTreeNode {
    private final ParseRegion region;
    private final Member member;

    /**
     * Creates a MemberNode.
     *
     * @param region Region of source code
     * @param member Member which is used in the expression
     */
    public MemberNode(
        ParseRegion region,
        Member member)
    {
        this.region = region;
        this.member = member;
    }

    public ParseRegion getRegion() {
        return region;
    }

    /**
     * Returns the Member used in this expression.
     *
     * @return member used in this expression
     */
    public Member getMember() {
        return member;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return new MemberType(
            member.getDimension(),
            member.getHierarchy(),
            member.getLevel(),
            member);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(member.getUniqueName());
    }

    public String toString() {
        return member.getUniqueName();
    }

    public MemberNode deepCopy() {
        // MemberNode is immutable
        return this;
    }
}

// End MemberNode.java
