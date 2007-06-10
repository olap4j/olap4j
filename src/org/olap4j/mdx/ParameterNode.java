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

import java.io.PrintWriter;

/**
 * A parameter to an MDX query.
 *
 * <p>Not all dialects of MDX support parameters. If a dialect supports
 * parameters, the driver for that dialect should extend the parser to
 * introduce a ParameterNode into the parse tree wherever a parameter is
 * encountered.
 *
 * <p>For example, in Mondrian's dialect of MDX, a call to the <code>Param(name,
 * type, defaultValueExpr)</code> function introduces a parameter, and
 * <code>ParamRef(name)</code> creates a reference to a parameter defined
 * elsewhere in the query.
 *
 * @version $Id$
 */
public class ParameterNode implements ParseTreeNode {
    private String name;
    private Type type;
    private ParseTreeNode defaultValueExpression;

    /**
     * Creates a ParameterNode.
     *
     * <p>The <code>name</code> must not be null, and the
     * <code>defaultValueExpression</code> must be consistent with the
     * <code>type</code>.
     *
     * @param name Name of parameter
     * @param type Type of parameter
     * @param defaultValueExpression Expression which yields the default value
     * of the parameter
     */
    public ParameterNode(
        String name,
        Type type,
        ParseTreeNode defaultValueExpression)
    {
        assert name != null;
        assert type != null;
        assert defaultValueExpression != null;
        this.name = name;
        this.type = type;
        this.defaultValueExpression = defaultValueExpression;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        final T t = visitor.visit(this);
        defaultValueExpression.accept(visitor);
        return t;
    }

    public void unparse(ParseTreeWriter writer) {
        PrintWriter pw = writer.getPrintWriter();
        pw.print("Param(");
        pw.print(MdxUtil.quoteForMdx(name));
        pw.print(", ");
        pw.print(type);
        pw.print(", ");
        defaultValueExpression.unparse(writer);
        pw.print(")");
    }

    public Type getType() {
        // not an expression
        return null;
    }

    /**
     * Returns the name of this parameter.
     *
     * @return name of this parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this parameter.
     *
     * @param name Parameter name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the type of this parameter.
     *
     * @param type Type
     */
    public void setType(Type type) {
        this.type = type;
    }


    /**
     * Returns the expression which yields the default value of this parameter.
     *
     * @return expression which yields the default value of this parameter
     */
    public ParseTreeNode getDefaultValueExpression() {
        return defaultValueExpression;
    }

    /**
     * Sets the expression which yields the default value of this parameter.
     *
     * @param defaultValueExpression default value expression
     */
    public void setDefaultValueExpression(ParseTreeNode defaultValueExpression) {
        this.defaultValueExpression = defaultValueExpression;
    }

}

// End ParameterNode.java
