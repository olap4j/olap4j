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
    private final AxisNode slicerAxis;
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
     * @param slicerAxis Slicer axis
     * @param cellPropertyList List of properties
     */
    public SelectNode(
        ParseRegion region,
        List<ParseTreeNode> withList,
        List<AxisNode> axisList,
        ParseTreeNode from,
        AxisNode slicerAxis,
        List<IdentifierNode> cellPropertyList)
    {
        this.region = region;
        this.withList = withList;
        this.axisList = axisList;
        this.from = from;
        this.slicerAxis = slicerAxis;
        this.cellPropertyList = cellPropertyList;
    }

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
        if (slicerAxis != null) {
            pw.println();
            pw.print("WHERE ");
            slicerAxis.unparse(writer);
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

    public List<ParseTreeNode> getWithList() {
        return withList;
    }

    public List<AxisNode> getAxisList() {
        return axisList;
    }

    public AxisNode getSlicerAxis() {
        return slicerAxis;
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
