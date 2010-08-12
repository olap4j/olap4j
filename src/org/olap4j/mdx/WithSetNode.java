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

import java.io.PrintWriter;

/**
 * Parse tree node which declares a calculated set. Represented as the
 * <code>WITH SET</code> clause of an MDX <code>SELECT</code> statement.
 *
 * @version $Id$
 * @author jhyde
 */
public class WithSetNode implements ParseTreeNode {

    private final ParseRegion region;
    /** name of set */
    private final IdentifierNode name;

    /** defining expression */
    private ParseTreeNode expression;

    /**
     * Creates a declaration of a named set.
     *
     * @param region Region of source code
     * @param name Name of set
     * @param expression Expression to calculate set
     */
    public WithSetNode(
        ParseRegion region,
        IdentifierNode name,
        ParseTreeNode expression)
    {
        this.region = region;
        this.name = name;
        this.expression = expression;
    }

    public ParseRegion getRegion() {
        return region;
    }

    public void unparse(ParseTreeWriter writer) {
        PrintWriter pw = writer.getPrintWriter();
        pw.print("SET ");
        name.unparse(writer);
        writer.indent();
        pw.println(" AS");
        expression.unparse(writer);
        writer.outdent();
    }

    /**
     * Returns the name of the set.
     *
     * @return name of the set
     */
    public IdentifierNode getIdentifier() {
        return name;
    }

    /**
     * Returns the expression which calculates the set.
     *
     * @return expression which calculates the set
     */
    public ParseTreeNode getExpression() {
        return expression;
    }

    /**
     * Sets the expression which calculates the set.
     *
     * @param expression expression which calculates the set
     */
    public void setExpression(ParseTreeNode expression) {
        this.expression = expression;
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

    public WithSetNode deepCopy() {
        return new WithSetNode(
            this.region,
            this.name.deepCopy(),
            this.expression.deepCopy());
    }
}

// End WithSetNode.java
