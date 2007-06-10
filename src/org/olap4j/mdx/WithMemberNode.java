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

    /** name of set or member */
    private final IdentifierNode name;

    /** defining expression */
    private ParseTreeNode expression;

    // properties of member, such as SOLVE_ORDER
    private final List<PropertyValueNode> memberPropertyList;

    /**
     * Constructs a formula specifying a member.
     */
    public WithMemberNode(
        IdentifierNode name,
        ParseTreeNode exp,
        List<PropertyValueNode> memberPropertyList)
    {
        this.name = name;
        this.expression = exp;
        this.memberPropertyList = memberPropertyList;
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
     */
    public IdentifierNode getName() {
        return name;
    }

    public ParseTreeNode getExpression() {
        return expression;
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

    public void setExpression(ParseTreeNode expression) {
        this.expression = expression;
    }

    public List<PropertyValueNode> getMemberPropertyList() {
        return memberPropertyList;
    }
}

// End WithMemberNode.java
