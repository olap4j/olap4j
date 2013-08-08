/*
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
 * Tuple type.
 *
 * @author jhyde
 * @since Feb 17, 2005
 */
public class TupleType implements Type {
    final Type[] elementTypes;
    private final String digest;

    /**
     * Creates a type representing a tuple whose fields are the given types.
     *
     * @param elementTypes Array of field types
     */
    public TupleType(Type[] elementTypes) {
        assert elementTypes != null;
        this.elementTypes = elementTypes.clone();

        final StringBuilder buf = new StringBuilder("TupleType<");
        for (int i = 0; i < elementTypes.length; i++) {
            Type elementType = elementTypes[i];
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(elementType.toString());
        }
        buf.append(">");
        digest = buf.toString();
    }

    public String toString() {
        return digest;
    }

    public boolean usesDimension(Dimension dimension, boolean maybe) {
        for (Type elementType : elementTypes) {
            if (elementType.usesDimension(dimension, maybe)) {
                return true;
            }
        }
        return false;
    }

    public Dimension getDimension() {
        return null;
    }

    public Hierarchy getHierarchy() {
        return null;
    }

    public Level getLevel() {
        return null;
    }

    // not part of public olap4j API
    private Type getValueType() throws OlapException {
        for (Type elementType : elementTypes) {
            if (elementType instanceof MemberType) {
                MemberType memberType = (MemberType) elementType;
                if (memberType.getDimension().getDimensionType()
                    == Dimension.Type.MEASURE)
                {
                    return memberType.getValueType();
                }
            }
        }
        return new ScalarType();
    }

    // not part of public olap4j API
    boolean isUnionCompatibleWith(TupleType that) throws OlapException {
        if (this.elementTypes.length != that.elementTypes.length) {
            return false;
        }
        for (int i = 0; i < this.elementTypes.length; i++) {
            if (!TypeUtil.isUnionCompatible(
                    this.elementTypes[i],
                    that.elementTypes[i]))
            {
                return false;
            }
        }
        return true;
    }
}

// End TupleType.java
