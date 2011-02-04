/*
// $Id: LevelSelectionImpl.java 399 2011-02-03 20:53:50Z pstoellberger $
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
 */
package org.olap4j.query;

import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.MetadataElement;

/**
 * Abstract implementation of {@link Selection}.
 *
 * @author pstoellberger
 * @version $Id: LevelSelectionImpl.java 399 2011-02-03 20:53:50Z pstoellberger $
 * @since Feb 3, 2011
 */
class LevelSelectionImpl extends AbstractSelection {

    protected Level level;

    public LevelSelectionImpl(
            Level level,
            Dimension dimension,
            Operator operator)
    {
        super(dimension, operator);
        this.level = level;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((level == null) ? 0 : level.getUniqueName().hashCode());
        result = prime * result
            + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result
            + ((selectionContext == null) ? 0 : selectionContext.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LevelSelectionImpl)) {
            return false;
        }
        LevelSelectionImpl other = (LevelSelectionImpl) obj;
        if (level == null) {
            if (other.level != null) {
                return false;
            }
        } else if (!level.getUniqueName().equals(
                        other.level.getUniqueName()))
        {
            return false;
        }
        if (operator == null) {
            if (other.operator != null) {
                return false;
            }
        } else if (!operator.equals(other.operator)) {
            return false;
        }
        if (selectionContext == null) {
            if (other.selectionContext != null) {
                return false;
            }
        } else if (!selectionContext.equals(other.selectionContext)) {
            return false;
        }
        return true;
    }

    public MetadataElement getRootElement() {
        return level;
    }

    public ParseTreeNode visit() {
        return Olap4jNodeConverter.toOlap4j(level, operator);
    }

    @Override
    public void setOperator(Operator operator) {
        if (!operator.equals(Operator.MEMBERS)) {
            throw new IllegalArgumentException(
                "Selections based on a Level have to be of Operator MEMBERS.");
        }
        super.setOperator(operator);
    }
}

// End LevelSelectionImpl.java
