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

import org.olap4j.metadata.Dimension;
import org.olap4j.type.Type;
import org.olap4j.type.DimensionType;

/**
 * Usage of a {@link org.olap4j.metadata.Dimension} as an expression in an MDX
 * parse tree.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
public class DimensionNode implements ParseTreeNode {
    private final Dimension dimension;


    public DimensionNode(Dimension dimension) {
        super();
        this.dimension = dimension;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return DimensionType.forDimension(dimension);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(dimension.getUniqueName());
    }

    public String toString() {
        return dimension.getUniqueName();
    }
}

// End DimensionNode.java
