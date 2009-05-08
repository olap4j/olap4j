/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
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
    private final ParseRegion region;
    private final Dimension dimension;

    /**
     * Creates a DimensionNode.
     *
     * @param region Region of source code
     * @param dimension Dimension which is used in the expression
     */
    public DimensionNode(
        ParseRegion region,
        Dimension dimension)
    {
        this.region = region;
        this.dimension = dimension;
    }

    public ParseRegion getRegion() {
        return region;
    }

    /**
     * Returns the Dimension used in this expression.
     *
     * @return dimension used in this expression
     */
    public Dimension getDimension() {
        return dimension;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return new DimensionType(dimension);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(dimension.getUniqueName());
    }

    public String toString() {
        return dimension.getUniqueName();
    }

    public DimensionNode deepCopy() {
        // DimensionNode is immutable
        return this;
    }
}

// End DimensionNode.java
