/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2005 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.type;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.OlapException;

/**
 * Tuple type.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
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
                if (memberType.getDimension().getDimensionType() == Dimension.Type.MEASURE) {
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
                    that.elementTypes[i])) {
                return false;
            }
        }
        return true;
    }
}

// End TupleType.java
