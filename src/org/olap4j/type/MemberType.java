/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2005-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.type;

import org.olap4j.metadata.Member;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Dimension;
import org.olap4j.OlapException;

/**
 * The type of an expression which represents a member.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public class MemberType implements Type {
    private final Hierarchy hierarchy;
    private final Dimension dimension;
    private final Level level;
    private final Member member;
    private final String digest;

    // not part of public olap4j public API
    private static final MemberType Unknown =
        new MemberType(null, null, null, null);

    /**
     * Creates a type representing a member.
     *
     * @param dimension Dimension the member belongs to, or null if not known.
     *
     * @param hierarchy Hierarchy the member belongs to, or null if not known.
     *
     * @param level Level the member belongs to, or null if not known
     *
     * @param member The precise member, or null if not known
     */
    public MemberType(
        Dimension dimension,
        Hierarchy hierarchy,
        Level level,
        Member member)
    {
        this.dimension = dimension;
        this.hierarchy = hierarchy;
        this.level = level;
        this.member = member;
        if (member != null) {
            assert level != null;
            assert member.getLevel().equals(level);
        }
        if (level != null) {
            assert hierarchy != null;
            assert level.getHierarchy().equals(hierarchy);
        }
        if (hierarchy != null) {
            assert dimension != null;
            assert hierarchy.getDimension().equals(dimension);
        }
        StringBuilder buf = new StringBuilder("MemberType<");
        if (member != null) {
            buf.append("member=").append(member.getUniqueName());
        } else if (level != null) {
            buf.append("level=").append(level.getUniqueName());
        } else if (hierarchy != null) {
            buf.append("hierarchy=").append(hierarchy.getUniqueName());
        } else if (dimension != null) {
            buf.append("dimension=").append(dimension.getUniqueName());
        }
        buf.append(">");
        this.digest = buf.toString();
    }

    public String toString() {
        return digest;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public Level getLevel() {
        return level;
    }

    /**
     * Returns the member of this type, or null if not known.
     *
     * @return member of this type
     */
    public Member getMember() {
        return member;
    }

    public boolean usesDimension(Dimension dimension, boolean maybe) {
        if (this.dimension == null) {
            return maybe;
        } else {
            return this.dimension.equals(dimension);
        }
    }

    // not part of public olap4j API
    Type getValueType() {
        // todo: when members have more type information (double vs. integer
        // vs. string), return better type if member != null.
        return new ScalarType();
    }

    public Dimension getDimension() {
        return dimension;
    }

    // not part of public olap4j API
    static MemberType forType(Type type) throws OlapException {
        if (type instanceof MemberType) {
            return (MemberType) type;
        } else {
            return new MemberType(
                    type.getDimension(),
                    type.getHierarchy(),
                    type.getLevel(),
                    null);
        }
    }
}

// End MemberType.java
