/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.type.Type;

import java.io.PrintWriter;

/**
 * Parse tree node which declares a calculated member. Represented as the
 * <code>WITH SET</code> clause of an MDX <code>SELECT</code> statement.
 *
 * @version $Id: $
 * @author jhyde
 */
public class WithSetNode implements ParseTreeNode {

    /** name of set */
    private final IdentifierNode name;

    /** defining expression */
    private ParseTreeNode expression;

    /**
     * Constructs formula specifying a set.
     */
    public WithSetNode(IdentifierNode name, ParseTreeNode expression) {
        this.name = name;
        this.expression = expression;
    }

    public void unparse(ParseTreeWriter writer) {
        PrintWriter pw = writer.getPrintWriter();
        pw.print("SET ");
        name.unparse(writer);
        pw.print(" AS '");
        expression.unparse(writer);
        pw.print("'");
    }

    public IdentifierNode getIdentifier() {
        return name;
    }

    public ParseTreeNode getExpression() {
        return expression;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        final T t = visitor.visit(this);
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
}

// End WithSetNode.java
