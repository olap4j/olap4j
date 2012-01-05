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
 * The type of an expression which represents a hierarchy.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public class HierarchyType implements Type {
    private final Dimension dimension;
    private final Hierarchy hierarchy;
    private final String digest;

    /**
     * Creates a type representing a hierarchy.
     *
     * @param dimension Dimension which values of this type must belong to, or
     *   null if not known
     *
     * @param hierarchy Hierarchy which values of this type must belong to, or
     *   null if not known
     */
    public HierarchyType(
        Dimension dimension,
        Hierarchy hierarchy)
    {
        this.dimension = dimension;
        this.hierarchy = hierarchy;
        StringBuilder buf = new StringBuilder("HierarchyType<");
        if (hierarchy != null) {
            buf.append("hierarchy=").append(hierarchy.getUniqueName());
        } else if (dimension != null) {
            buf.append("dimension=").append(dimension.getUniqueName());
        }
        buf.append(">");
        this.digest = buf.toString();
    }

    // not part of public olap4j API
    private static HierarchyType forType(Type type) throws OlapException {
        return new HierarchyType(type.getDimension(), type.getHierarchy());
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
        return null;
    }

    public String toString() {
        return digest;
    }
}

// End HierarchyType.java
