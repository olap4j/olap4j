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
import java.io.StringWriter;
import java.util.*;

/**
 * Parse tree model for an MDX SELECT statement.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
public class SelectNode implements ParseTreeNode {
    private final ParseRegion region;
    private final List<ParseTreeNode> withList;
    private final List<AxisNode> axisList;
    private final AxisNode filterAxis;
    private final List<IdentifierNode> cellPropertyList;
    private ParseTreeNode from;

    /**
     * Creates a SelectNode.
     *
     * @param region Region of source code from which this node was created
     * @param withList List of members and sets defined in this query using
     *   a <code>WITH</code> clause
     * @param axisList List of axes
     * @param from Contents of FROM clause (name of cube, or subquery)
     * @param filterAxis Filter axis
     * @param cellPropertyList List of properties
     */
    public SelectNode(
        ParseRegion region,
        List<ParseTreeNode> withList,
        List<AxisNode> axisList,
        ParseTreeNode from,
        AxisNode filterAxis,
        List<IdentifierNode> cellPropertyList)
    {
        this.region = region;
        this.withList = withList;
        this.axisList = axisList;
        this.from = from;
        if (filterAxis == null) {
            filterAxis =
                new AxisNode(
                    null,
                    false,
                    Axis.FILTER,
                    Collections.<IdentifierNode>emptyList(),
                    null);
        }
        if (filterAxis.getAxis() != Axis.FILTER) {
            throw new IllegalArgumentException(
                "Filter axis must have type FILTER");
        }
        this.filterAxis = filterAxis;
        this.cellPropertyList = cellPropertyList;
    }

    /**
     * Creates an empty SelectNode.
     *
     * <p>The contents of the SelectNode, such as the axis list, can be
     * populated after construction.
     */
    public SelectNode() {
        this(
            null,
            new ArrayList<ParseTreeNode>(),
            new ArrayList<AxisNode>(),
            null,
            null,
            new ArrayList<IdentifierNode>());
    }

    public ParseRegion getRegion() {
        return region;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        // not an expression, so has no type
        return null;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        ParseTreeWriter pw = new ParseTreeWriter(sw);
        unparse(pw);
        return sw.toString();
    }

    public void unparse(ParseTreeWriter writer) {
        final PrintWriter pw = writer.getPrintWriter();
        if (!withList.isEmpty()) {
            pw.println("WITH");
            for (ParseTreeNode with : withList) {
                with.unparse(writer);
                pw.println();
            }
        }
        pw.print("SELECT");
        int k = 0;
        for (AxisNode axis : axisList) {
            if (k++ > 0) {
                pw.println(",");
            } else {
                pw.println();
            }
            axis.unparse(writer);
        }
        pw.println();
        pw.print("FROM ");
        if (from instanceof SelectNode) {
            writer.indent();
            pw.println("(");
            from.unparse(writer);
            pw.print(")");
            writer.outdent();
        } else {
            from.unparse(writer);
        }
        if (filterAxis.getExpression() != null) {
            pw.println();
            pw.print("WHERE ");
            filterAxis.unparse(writer);
        }
        if (!cellPropertyList.isEmpty()) {
            pw.println();
            pw.print("CELL PROPERTIES ");
            k = 0;
            for (IdentifierNode cellProperty : cellPropertyList) {
                if (k++ > 0) {
                    pw.print(", ");
                }
                cellProperty.unparse(writer);
            }
        }
    }

    /**
     * Returns a list of calculated members and sets defined as the WITH
     * clause of this SelectNode.
     *
     * <p>For example, the WITH clause of query
     *
     * <blockquote>
     * <code>WITH MEMBER [Measures].[Foo] AS ' [Measures].[Unit Sales] * 2 '
     *   SET [Customers].[Top] AS ' TopCount([Customers].Members, 10) '
     * SELECT FROM [Sales]</code>
     * </blockquote>
     *
     * contains one {@link org.olap4j.mdx.WithMemberNode} and one
     * {@link org.olap4j.mdx.WithSetNode}.
     *
     * <p>The returned list is mutable.
     *
     * @return list of calculated members and sets
     */
    public List<ParseTreeNode> getWithList() {
        return withList;
    }

    /**
     * Returns a list of axes in this SelectNode.
     *
     * <p>The returned list is mutable.
     *
     * @return list of axes
     */
    public List<AxisNode> getAxisList() {
        return axisList;
    }

    /**
     * Returns the filter axis defined by the WHERE clause of this SelectNode.
     *
     * <p>Never returns {@code null}. If there is no WHERE clause, returns an
     * AxisNode for which {@link org.olap4j.mdx.AxisNode#getExpression()}
     * returns null.
     *
     * <p>You can modify the filter expression by calling
     * {@link org.olap4j.mdx.AxisNode#getExpression()} on the filter AxisNode;
     * {@code null} means that there is no filter axis.
     *
     * @return filter axis
     */
    public AxisNode getFilterAxis() {
        return filterAxis;
    }

    /**
     * Returns the node representing the FROM clause of this SELECT statement.
     * The node is typically an {@link IdentifierNode}, a {@link CubeNode} or
     * a {@link SelectNode}.
     *
     * @return FROM clause
     */
    public ParseTreeNode getFrom() {
        return from;
    }

    /**
     * Sets the FROM clause of this SELECT statement.
     *
     * <p><code>fromNode</code> should typically by an
     * {@link org.olap4j.mdx.IdentifierNode} containing the cube name, or
     * a {@link org.olap4j.mdx.CubeNode} referencing an explicit
     * {@link org.olap4j.metadata.Cube} object.
     *
     * @param from FROM clause
     */
    public void setFrom(ParseTreeNode from) {
        this.from = from;
    }

    /**
     * Returns a list of cell properties in this SelectNode.
     *
     * <p>The returned list is mutable.
     *
     * @return list of cell properties
     */
    public List<IdentifierNode> getCellPropertyList() {
        return cellPropertyList;
    }

    public SelectNode deepCopy() {
        return new SelectNode(
            this.region,
            MdxUtil.deepCopyList(withList),
            MdxUtil.deepCopyList(axisList),
            this.from != null ? this.from.deepCopy() : null,
            this.filterAxis.deepCopy(),
            MdxUtil.deepCopyList(cellPropertyList));
    }
}

// End SelectNode.java
