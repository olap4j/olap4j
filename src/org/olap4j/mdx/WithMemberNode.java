/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
 */
package org.olap4j.mdx;

import org.olap4j.type.Type;

import java.util.List;
import java.io.PrintWriter;

/**
 * Parse tree node which declares a calculated member. Represented as the
 * <code>WITH MEMBER</code> clause of an MDX <code>SELECT</code> statement.
 *
 * @version $Id$
 * @author jhyde
 */
public class WithMemberNode implements ParseTreeNode {

    private final ParseRegion region;

    /** name of set or member */
    private final IdentifierNode name;

    /** defining expression */
    private ParseTreeNode expression;

    // properties of member, such as SOLVE_ORDER
    private final List<PropertyValueNode> memberPropertyList;

    /**
     * Constructs a formula specifying a member.
     *
     * @param region Source code region
     * @param name   Name of member being declared
     * @param exp    Expression for value of member
     * @param memberPropertyList Collection of properties of member
     */
    public WithMemberNode(
        ParseRegion region,
        IdentifierNode name,
        ParseTreeNode exp,
        List<PropertyValueNode> memberPropertyList)
    {
        this.region = region;
        this.name = name;
        this.expression = exp;
        this.memberPropertyList = memberPropertyList;
    }

    public ParseRegion getRegion() {
        return region;
    }

    public void unparse(ParseTreeWriter writer) {
        PrintWriter pw = writer.getPrintWriter();
        pw.print("MEMBER ");
        name.unparse(writer);
        pw.print(" AS '");
        expression.unparse(writer);
        pw.print("'");
        if (memberPropertyList != null) {
            for (PropertyValueNode memberProperty : memberPropertyList) {
                pw.print(", ");
                memberProperty.unparse(writer);
            }
        }
    }

    /**
     * Returns the name of the member declared.
     *
     * <p>The name is as specified in the parse tree; it may not be identical
     * to the unique name of the member.
     *
     * @return Name of member
     */
    public IdentifierNode getIdentifier() {
        return name;
    }

    /**
     * Returns the expression to evaluate to calculate the member.
     *
     * @return expression
     */
    public ParseTreeNode getExpression() {
        return expression;
    }

    /**
     * Sets the expression to evaluate to calculate the member.
     *
     * @param expression Expression
     */
    public void setExpression(ParseTreeNode expression) {
        this.expression = expression;
    }


    public <T> T accept(ParseTreeVisitor<T> visitor) {
        T t = visitor.visit(this);
        name.accept(visitor);
        expression.accept(visitor);
        return t;
    }

    public Type getType() {
        // not an expression
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the list of properties of this member.
     *
     * <p>The list may be empty, but is never null.
     * Each entry is a (name, expression) pair.
     *
     * @return list of properties
     */
    public List<PropertyValueNode> getMemberPropertyList() {
        return memberPropertyList;
    }

    public WithMemberNode deepCopy() {
        return new WithMemberNode(
            this.region, // immutable
            this.name.deepCopy(),
            this.expression.deepCopy(),
            MdxUtil.deepCopyList(memberPropertyList));
    }
}

// End WithMemberNode.java
