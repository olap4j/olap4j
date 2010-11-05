/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.impl.Olap4jUtil;

import java.util.List;

/**
 * Component in a compound identifier that describes the name of an object.
 * Optionally, the name is quoted in brackets.
 *
 * @see KeySegment
 *
 * @version $Id$
 * @author jhyde
 */
public class NameSegment implements IdentifierSegment {
    final String name;
    final Quoting quoting;
    private final ParseRegion region;

    /**
     * Creates a segment with the given quoting and region.
     *
     * @param region Region of source code
     * @param name Name
     * @param quoting Quoting style
     */
    public NameSegment(
        ParseRegion region,
        String name,
        Quoting quoting)
    {
        this.region = region;
        this.name = name;
        this.quoting = quoting;
        if (name == null) {
            throw new NullPointerException();
        }
        if (!(quoting == Quoting.QUOTED || quoting == Quoting.UNQUOTED)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a quoted segment, "[name]".
     *
     * @param name Name of segment
     */
    public NameSegment(String name) {
        this(null, name, Quoting.QUOTED);
    }

    public String toString() {
        switch (quoting) {
        case UNQUOTED:
            return name;
        case QUOTED:
            return IdentifierNode.quoteMdxIdentifier(name);
        default:
            throw Olap4jUtil.unexpected(quoting);
        }
    }

    public void toString(StringBuilder buf) {
        switch (quoting) {
        case UNQUOTED:
            buf.append(name);
            return;
        case QUOTED:
            IdentifierNode.quoteMdxIdentifier(name, buf);
            return;
        default:
            throw Olap4jUtil.unexpected(quoting);
        }
    }
    public ParseRegion getRegion() {
        return region;
    }

    public String getName() {
        return name;
    }

    public Quoting getQuoting() {
        return quoting;
    }

    public List<NameSegment> getKeyParts() {
        return null;
    }
}

// End NameSegment.java
