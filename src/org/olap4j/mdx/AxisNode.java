/*
// $Id$
//
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.mdx;

import org.olap4j.Axis;
import org.olap4j.type.Type;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

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

    private final List<IdentifierNode> dimensionProperties;

    /**
     * Creates an axis.
     *
     * @param region Region of source code
     * @param nonEmpty Whether to filter out members of this axis whose cells
     *    are all empty
     * @param axis Which axis (ROWS, COLUMNS, etc.)
     * @param dimensionProperties List of dimension properties; if null,
     *   empty list is assumed
     * @param expression Expression to populate the axis
     */
    public AxisNode(
        ParseRegion region,
        boolean nonEmpty,
        Axis axis,
        List<IdentifierNode> dimensionProperties,
        ParseTreeNode expression)
    {
        this.region = region;
        this.nonEmpty = nonEmpty;
        this.expression = expression;
        this.axis = axis;
        if (axis == null) {
            throw new IllegalArgumentException("Axis type must not be null");
        }
        if (dimensionProperties == null) {
            dimensionProperties = Collections.emptyList();
        }
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
     *
     * @return whether the axis is NON EMPTY
     */
    public boolean isNonEmpty() {
        return nonEmpty;
    }

    /**
     * Sets whether the axis has the <code>NON EMPTY</code> property set.
     *
     * See {@link #isNonEmpty()}.
     *
     * @param nonEmpty whether the axis is NON EMPTY
     */
    public void setNonEmpty(boolean nonEmpty) {
        this.nonEmpty = nonEmpty;
    }

    /**
     * Returns the expression which is used to compute the value of this axis.
     *
     * @return the expression which is used to compute the value of this axis
     */
    public ParseTreeNode getExpression() {
        return expression;
    }

    /**
     * Sets the expression which is used to compute the value of this axis.
     * See {@link #getExpression()}.
     *
     * @param expr the expression which is used to compute the value of this
     * axis
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
        if (axis != Axis.FILTER) {
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
        // An axis is not an expression, so does not have a type.
        // Try AxisNode.getExpression().getType() instead.
        return null;
    }

    public AxisNode deepCopy() {
        return new AxisNode(
            this.region,
            this.nonEmpty,
            this.axis,
            MdxUtil.deepCopyList(dimensionProperties),
            this.expression != null ? this.expression.deepCopy() : null);
    }
}

// End AxisNode.java
