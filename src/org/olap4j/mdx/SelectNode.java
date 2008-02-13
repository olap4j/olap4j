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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
     * @param from Name of cube
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
        ParseTreeWriter pw = new ParseTreeWriter(new PrintWriter(sw));
        unparse(pw);
        sw.flush();
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
        from.unparse(writer);
        if (filterAxis != null) {
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
     * Returns the filter axis defined by the WHERE clause of this SelectNode,
     * or null if there is no filter axis.
     *
     * @return filter axis
     */
    public AxisNode getFilterAxis() {
        return filterAxis;
    }

    /**
     * Returns the node representing the FROM clause of this SELECT statement.
     * The node is typically an {@link IdentifierNode} or a {@link CubeNode}.
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
     * @param fromNode FROM clause
     */
    public void setFrom(ParseTreeNode fromNode) {
        this.from = fromNode;
    }
}

// End SelectNode.java
