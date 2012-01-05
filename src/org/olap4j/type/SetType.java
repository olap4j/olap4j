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

import org.olap4j.metadata.*;

/**
 * Set type.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public class SetType implements Type {

    private final Type elementType;

    /**
     * Creates a type representing a set of elements of a given type.
     *
     * @param elementType The type of the elements in the set, or null if not
     *   known
     */
    public SetType(Type elementType) {
        assert elementType instanceof MemberType
            || elementType instanceof TupleType;
        this.elementType = elementType;
    }

    /**
     * Returns the type of the elements of this set.
     *
     * @return element type
     */
    public Type getElementType() {
        return elementType;
    }

    public boolean usesDimension(Dimension dimension, boolean maybe) {
        if (elementType == null) {
            return maybe;
        }
        return elementType.usesDimension(dimension, maybe);
    }

    public Dimension getDimension() {
        return elementType == null
            ? null
            : elementType.getDimension();
    }

    public Hierarchy getHierarchy() {
        return elementType == null
            ? null
            : elementType.getHierarchy();
    }

    public Level getLevel() {
        return elementType == null
            ? null
            : elementType.getLevel();
    }
}

// End SetType.java
