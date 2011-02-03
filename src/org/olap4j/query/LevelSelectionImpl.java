/*
// $Id: LevelSelectionImpl.java 399 2011-02-03 20:53:50Z pstoellberger $
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
 */
package org.olap4j.query;

import java.util.ArrayList;
import java.util.List;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;

/**
 * Abstract implementation of {@link Selection}.
 *
 * @author pstoellberger
 * @version $Id: LevelSelectionImpl.java 399 2011-02-03 20:53:50Z pstoellberger $
 * @since Feb 3, 2011
 */
class LevelSelectionImpl extends QueryNodeImpl implements Selection {

    protected Level level;
    protected String dimensionName;
    protected String hierarchyName;
    protected String levelName;
    protected Dimension dimension;
    protected Operator operator = Operator.MEMBER;
    protected List<Selection> selectionContext;

    /**
     * Creates a SelectionImpl.
     *
     * @pre operator != null
     */
    public LevelSelectionImpl(
            Level level,
            Dimension dimension,
            String hierarchyName,
            String levelName,
            Operator operator)
    {
        super();
        this.level = level;
        this.dimension = dimension;
        this.hierarchyName = hierarchyName;
        this.levelName = levelName;
        this.operator = operator;
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

    public String getName() {
        return levelName;
    }

    public void setName(String name) {
        levelName = name;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public Level getLevel() {
        return level;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        assert operator != null;
        this.operator = operator;
        notifyChange(this,-1);
    }

    void tearDown() {
    }

    public List<Selection> getSelectionContext() {
        return selectionContext;
    }

    public void addContext(Selection selection) {
        if (selectionContext == null) {
            selectionContext = new ArrayList<Selection>();
        }
        selectionContext.add(selection);
    }

    public void removeContext(Selection selection) {
        selectionContext.remove(selection);
    }

    public Member getMember() {
        // there is no member in this type of Selections
        return null;
    }
}

// End LevelSelectionImpl.java
