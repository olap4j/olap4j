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

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;

/**
 * Parse tree model for an MDX SELECT statement.
 *
 * @author jhyde
 * @version $Id: $
 * @since Jun 4, 2007
 */
public class SelectNode implements ParseTreeNode {
    private final List<ParseTreeNode> withList;
    private final List<AxisNode> axisList;
    private final AxisNode slicerAxis;
    private final List<IdentifierNode> cellPropertyList;
    private IdentifierNode cubeName;

    /**
     * Creates a SelectNode.
     *
     * @param withList List of members and sets defined in this query using
     *   a <code>WITH</code> clause
     * @param axisList List of axes
     * @param cubeName Name of cube
     * @param slicerAxis Slicer axis
     */
    public SelectNode(
        List<ParseTreeNode> withList,
        List<AxisNode> axisList,
        IdentifierNode cubeName,
        AxisNode slicerAxis,
        List<IdentifierNode> cellPropertyList)
    {
        this.withList = withList;
        this.axisList = axisList;
        this.cubeName = cubeName;
        this.slicerAxis = slicerAxis;
        this.cellPropertyList = cellPropertyList;
    }

    public SelectNode() {
        this(
            new ArrayList<ParseTreeNode>(),
            new ArrayList<AxisNode>(),
            null,
            null,
            new ArrayList<IdentifierNode>());
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        throw new UnsupportedOperationException();
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
        cubeName.unparse(writer);
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

    public IdentifierNode getCubeName() {
        return cubeName;
    }

    public void setCubeName(IdentifierNode cubeName) {
        this.cubeName = cubeName;
    }
}

// End SelectNode.java
