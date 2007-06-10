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

    private final String name;
    private ParseTreeNode expression;

    public PropertyValueNode(String name, ParseTreeNode exp) {
        this.name = name;
        this.expression = exp;
    }

    public Type getType() {
        return expression.getType();
    }

    public ParseTreeNode getExpression() {
        return expression;
    }

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
}

// End PropertyValueNode.java
