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

import org.olap4j.metadata.Level;
import org.olap4j.type.Type;
import org.olap4j.type.LevelType;

/**
 * Usage of a {@link org.olap4j.metadata.Member} as an expression in an MDX
 * parse tree.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
public class LevelNode implements ParseTreeNode {
    private final ParseRegion region;
    private final Level level;

    public LevelNode(
        ParseRegion region,
        Level level)
    {
        this.region = region;
        this.level = level;
    }

    public ParseRegion getRegion() {
        return region;
    }

    /**
     * Returns the Level used in this expression.
     *
     * @return level used in this expression
     */
    public Level getLevel() {
        return level;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return LevelType.forLevel(level);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(level.getUniqueName());
    }

    public String toString() {
        return level.getUniqueName();
    }
}

// End LevelNode.java
