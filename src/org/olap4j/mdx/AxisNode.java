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

import java.io.PrintWriter;
import java.util.List;

import org.olap4j.Axis;
import org.olap4j.type.Type;

/**
 * An axis in an MDX query. For example, the typical MDX query has two axes,
 * which appear as the "ON COLUMNS" and "ON ROWS" clauses.
 *
 * @version $Id$
 */
public class AxisNode implements ParseTreeNode {

    private final ParseRegion region;
    private boolean nonEmpty;
    private ParseTreeNode expression;
    private final Axis axis;

    private final List<IdentifierNode>  dimensionProperties;

    /**
     * Creates an axis.
     *
     * @param nonEmpty Whether to filter out members of this axis whose cells
     *    are all empty
     * @param expression Expression to populate the axis
     * @param axisDef Which axis (ROWS, COLUMNS, etc.)
     * @param dimensionProperties List of dimension properties
     */
    public AxisNode(
        ParseRegion region,
        boolean nonEmpty,
        ParseTreeNode expression,
        Axis axisDef,
        List<IdentifierNode> dimensionProperties)
    {
        this.region = region;
        assert dimensionProperties != null;
        this.nonEmpty = nonEmpty;
        this.expression = expression;
        this.axis = axisDef;
        this.dimensionProperties = dimensionProperties;
    }

    public ParseRegion getRegion() {
        return region;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        final T o = visitor.visit(this);

        // visit the expression which forms the axis
        expression.accept(visitor);

        return o;
    }

    /**
     * Returns the name of the axis this axis expression is populating.
     *
     * @return axis name
     */
    public Axis getAxis() {
        return axis;
    }

    /**
     * Returns whether the axis has the <code>NON EMPTY</code> property set.
     */
    public boolean isNonEmpty() {
        return nonEmpty;
    }

    /**
     * Sets whether the axis has the <code>NON EMPTY</code> property set.
     *
     * See {@link #isNonEmpty()}.
     */
    public void setNonEmpty(boolean nonEmpty) {
        this.nonEmpty = nonEmpty;
    }

    /**
     * Returns the expression which is used to compute the value of this axis.
     */
    public ParseTreeNode getExpression() {
        return expression;
    }

    /**
     * Sets the expression which is used to compute the value of this axis.
     * See {@link #getExpression()}.
     */
    public void setExpression(ParseTreeNode expr) {
        this.expression = expr;
    }

    public void unparse(ParseTreeWriter writer) {
        PrintWriter pw = writer.getPrintWriter();
        if (nonEmpty) {
            pw.print("NON EMPTY ");
        }
        if (expression != null) {
            expression.unparse(writer);
        }
        if (dimensionProperties.size() > 0) {
            pw.print(" DIMENSION PROPERTIES ");
            for (int i = 0; i < dimensionProperties.size(); i++) {
                IdentifierNode dimensionProperty = dimensionProperties.get(i);
                if (i > 0) {
                    pw.print(", ");
                }
                dimensionProperty.unparse(writer);
            }
        }
        if (axis != Axis.SLICER) {
            pw.print(" ON " + axis);
        }
    }

    /**
     * Returns the list of dimension properties of this axis.
     *
     * @return list of dimension properties
     */
    public List<IdentifierNode> getDimensionProperties() {
        return dimensionProperties;
    }

    public Type getType() {
        // not an expression
        return null;
    }
}

// End AxisNode.java
