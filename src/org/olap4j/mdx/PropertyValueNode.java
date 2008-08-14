/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.type.Type;

/**
 * Parse tree node representing a property-value pair.
 *
 * <p>Property-value pairs are used to define properties of calculated members.
 * For example, in <code>WITH MEMBER [Measures].[Foo] AS ' [Measures].[Unit Sales] ',
 * FORMAT_STRING = 'Bold',
 * SOLVE_ORDER = 2
 * SELECT ...</code>
 * there are two property-value pairs FORMAT_STRING and SOLVE_ORDER.
 *
 * @version $Id$
 * @author jhyde
 */
public class PropertyValueNode implements ParseTreeNode {

    private final ParseRegion region;
    private final String name;
    private ParseTreeNode expression;

    /**
     * Creates a PropertyValueNode.
     *
     * @param region Region of source code
     * @param name Name of property
     * @param expression Expression for value of property (often a literal)
     */
    public PropertyValueNode(
        ParseRegion region,
        String name,
        ParseTreeNode expression)
    {
        this.region = region;
        this.name = name;
        this.expression = expression;
    }

    public ParseRegion getRegion() {
        return region;
    }

    public Type getType() {
        return expression.getType();
    }

    /**
     * Returns the expression by which the value of the property is derived.
     *
     * @return the expression by which the value of the property is derived
     */
    public ParseTreeNode getExpression() {
        return expression;
    }

    /**
     * Returns the name of the property
     *
     * @return name of the property
     */
    public String getName() {
        return name;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(name + " = ");
        expression.unparse(writer);
    }

    public PropertyValueNode deepCopy() {
        return new PropertyValueNode(
            this.region,
            this.name,
            this.expression.deepCopy());
    }
}

// End PropertyValueNode.java
