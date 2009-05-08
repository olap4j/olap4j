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

import org.olap4j.metadata.Hierarchy;
import org.olap4j.type.Type;
import org.olap4j.type.HierarchyType;

/**
 * Usage of a {@link org.olap4j.metadata.Hierarchy} as an expression in an MDX
 * parse tree.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
public class HierarchyNode implements ParseTreeNode {
    private final ParseRegion region;
    private final Hierarchy hierarchy;

    /**
     * Creates a HierarchyNode.
     *
     * @param region Region of source code
     * @param hierarchy Hierarchy which is used in the expression
     */
    public HierarchyNode(
        ParseRegion region,
        Hierarchy hierarchy)
    {
        this.region = region;
        this.hierarchy = hierarchy;
    }

    public ParseRegion getRegion() {
        return region;
    }

    /**
     * Returns the Hierarchy used in this expression.
     *
     * @return hierarchy used in this expression
     */
    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return new HierarchyType(
            hierarchy.getDimension(),
            hierarchy);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(hierarchy.getUniqueName());
    }

    public String toString() {
        return hierarchy.getUniqueName();
    }

    public HierarchyNode deepCopy() {
        // HierarchyNode is immutable
        return this;
    }
}

// End HierarchyNode.java
