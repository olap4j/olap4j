/*
// $Id: //open/mondrian/src/main/org.olap4j.type/LevelType.java#5 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2005 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.type;

import org.olap4j.metadata.Level;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.OlapException;

/**
 * The type of an expression which represents a level.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id: //open/mondrian/src/main/org.olap4j.type/LevelType.java#5 $
 */
public class LevelType implements Type {
    private final Dimension dimension;
    private final Hierarchy hierarchy;
    private final Level level;
    private final String digest;

    /**
     * Creates a type representing a level.
     *
     * @param dimension
     * @param hierarchy Hierarchy which values of this type must belong to, or
     *   null if not known
     * @param level Level which values of this type must belong to, or null if
     */
    public LevelType(Dimension dimension, Hierarchy hierarchy, Level level) {
        this.dimension = dimension;
        this.hierarchy = hierarchy;
        this.level = level;
        if (level != null) {
            assert hierarchy != null : "hierarchy != null";
            assert level.getHierarchy() == hierarchy :
                "level.getHierarchy() == hierarchy";
        }
        if (hierarchy != null) {
            assert dimension != null : "dimension != null";
            assert hierarchy.getDimension() == dimension :
                "hierarchy.getDimension() == dimension";
        }
        StringBuilder buf = new StringBuilder("LevelType<");
        if (level != null) {
            buf.append("level=").append(level.getUniqueName());
        } else if (hierarchy != null) {
            buf.append("hierarchy=").append(hierarchy.getUniqueName());
        } else if (dimension != null) {
            buf.append("dimension=").append(dimension.getUniqueName());
        }
        buf.append(">");
        this.digest = buf.toString();
    }

    public static LevelType forType(Type type) throws OlapException {
        return new LevelType(
                type.getDimension(),
                type.getHierarchy(),
                type.getLevel());

    }

    public static LevelType forLevel(Level level) {
        return new LevelType(
                level.getDimension(),
                level.getHierarchy(),
                level);
    }

    public boolean usesDimension(Dimension dimension, boolean maybe) {
        if (this.dimension == null) {
            return maybe;
        } else {
            return this.dimension == dimension;
        }
    }

    public Dimension getDimension() {
        return dimension;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public Level getLevel() {
        return level;
    }

    public String toString() {
        return digest;
    }
}

// End LevelType.java
