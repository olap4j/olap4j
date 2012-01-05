/*
// $Id$
//
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.type;

import org.olap4j.OlapException;
import org.olap4j.metadata.*;

/**
 * The type of an expression which represents a level.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public class LevelType implements Type {
    private final Dimension dimension;
    private final Hierarchy hierarchy;
    private final Level level;
    private final String digest;

    /**
     * Creates a type representing a level.
     *
     * @param dimension Dimension which values of this type must belong to, or
     *   null if not known
     *
     * @param hierarchy Hierarchy which values of this type must belong to, or
     *   null if not known
     *
     * @param level Level which values of this type must belong to, or null if
     *   not known
     */
    public LevelType(
        Dimension dimension,
        Hierarchy hierarchy,
        Level level)
    {
        this.dimension = dimension;
        this.hierarchy = hierarchy;
        this.level = level;
        if (level != null) {
            assert hierarchy != null : "hierarchy != null";
            assert level.getHierarchy() == hierarchy
                : "level.getHierarchy() == hierarchy";
        }
        if (hierarchy != null) {
            assert dimension != null : "dimension != null";
            assert hierarchy.getDimension() == dimension
                : "hierarchy.getDimension() == dimension";
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

    // not part of public olap4j API
    private static LevelType forType(Type type) throws OlapException {
        return new LevelType(
            type.getDimension(),
            type.getHierarchy(),
            type.getLevel());
    }

    public boolean usesDimension(Dimension dimension, boolean maybe) {
        if (this.dimension == null) {
            return maybe;
        } else {
            return this.dimension.equals(dimension);
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

