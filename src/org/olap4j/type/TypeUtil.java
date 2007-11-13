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

import org.olap4j.metadata.Hierarchy;
import org.olap4j.OlapException;

/**
 * Utility methods relating to types.
 *
 * <p>NOTE: This class is experimental. Not part of the public olap4j API.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public class TypeUtil {

    /**
     * Given a set type, returns the element type. Or its element type, if it
     * is a set type. And so on.
     */
    private static Type stripSetType(Type type) {
        while (type instanceof SetType) {
            type = ((SetType) type).getElementType();
        }
        return type;
    }

    /**
     * Converts a type to a member or tuple type.
     * If it cannot, returns null.
     */
    private static Type toMemberOrTupleType(Type type) throws OlapException {
        type = stripSetType(type);
        if (type instanceof TupleType) {
            return (TupleType) type;
        } else {
            return toMemberType(type);
        }
    }

    /**
     * Converts a type to a member type.
     * If it is a set, strips the set.
     * If it is a member type, returns the type unchanged.
     * If it is a dimension, hierarchy or level type, converts it to
     * a member type.
     * If it is a tuple, number, string, or boolean, returns null.
     */
    static MemberType toMemberType(Type type) throws OlapException {
        type = stripSetType(type);
        if (type instanceof MemberType) {
            return (MemberType) type;
        } else if (type instanceof DimensionType ||
                type instanceof HierarchyType ||
                type instanceof LevelType) {
            return MemberType.forType(type);
        } else {
            return null;
        }
    }

    /**
     * Returns whether this type is union-compatible with another.
     * In general, to be union-compatible, types must have the same
     * dimensionality.
     *
     * @param type1 First type
     * @param type2 Second type
     * @return Whether types are union-compatible
     * @throws OlapException on error
     */
    static boolean isUnionCompatible(
        Type type1,
        Type type2)
        throws OlapException
    {
        if (type1 instanceof TupleType) {
            return type2 instanceof TupleType
                && ((TupleType) type1).isUnionCompatibleWith(
                (TupleType) type2);
        } else {
            final MemberType memberType1 = toMemberType(type1);
            if (memberType1 == null) {
                return false;
            }
            final MemberType memberType2 = toMemberType(type2);
            if (memberType2 == null) {
                return false;
            }
            final Hierarchy hierarchy1 = memberType1.getHierarchy();
            final Hierarchy hierarchy2 = memberType2.getHierarchy();
            return equal(hierarchy1, hierarchy2);
        }
    }

    private static boolean equal(
            final Hierarchy hierarchy1, final Hierarchy hierarchy2) {
        if (hierarchy1 == null ||
                hierarchy2 == null ||
                hierarchy2.getUniqueName().equals(
                        hierarchy1.getUniqueName())) {
            // They are compatible.
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether a value of a given type can be evaluated to a scalar
     * value.
     *
     * <p>The rules are as follows:<ul>
     * <li>Clearly boolean, numeric and string expressions can be evaluated.
     * <li>Member and tuple expressions can be interpreted as a scalar value.
     *     The expression is evaluated to establish the context where a measure
     *     can be evaluated.
     * <li>Hierarchy and dimension expressions are implicitly
     *     converted into the current member, and evaluated as above.
     * <li>Level expressions cannot be evaluated
     * <li>Cube and Set (even sets with a single member) cannot be evaluated.
     * </ul>
     *
     * @param type Type
     * @return Whether an expression of this type can be evaluated to yield a
     *   scalar value.
     */
    public static boolean canEvaluate(Type type) {
        return ! (type instanceof SetType ||
                type instanceof CubeType ||
                type instanceof LevelType);
    }

    /**
     * Returns whether a type is a set type.
     *
     * @param type Type
     * @return Whether a value of this type can be evaluated to yield a set.
     */
    public static boolean isSet(Type type) {
        return type instanceof SetType;
    }

    private static boolean couldBeMember(Type type) {
        return type instanceof MemberType ||
                type instanceof HierarchyType ||
                type instanceof DimensionType;
    }

    static boolean equal(Object o, Object p) {
        return o == null ? p == null : p != null && o.equals(p);
    }
}

// End TypeUtil.java
