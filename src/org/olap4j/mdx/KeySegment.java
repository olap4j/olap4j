/*
// $Id: IdentifierNode.java 359 2010-10-14 21:24:51Z jhyde $
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.impl.UnmodifiableArrayList;

import java.util.List;

/**
 * Segment that represents a key or compound key.
 *
 * <p>Such a segment appears in an identifier with each component prefixed
 * with '&amp;'. For example, in the identifier
 * '{@code [Customer].[State].&amp;[WA]&amp;[USA]}', the third segment is a
 * compound key whose parts are "@{code WA}" and "{@code USA}".
 *
 * @see org.olap4j.mdx.NameSegment
 *
 * @version $Id: IdentifierNode.java 359 2010-10-14 21:24:51Z jhyde $
 * @author jhyde
 */
public class KeySegment implements IdentifierSegment {
    private final List<NameSegment> subSegmentList;

    /**
     * Creates a KeySegment with one or more sub-segments.
     *
     * @param subSegments Array of sub-segments
     */
    public KeySegment(NameSegment... subSegments) {
        if (subSegments.length < 1) {
            throw new IllegalArgumentException();
        }
        this.subSegmentList = UnmodifiableArrayList.asCopyOf(subSegments);
    }

    /**
     * Creates a KeySegment a list of sub-segments.
     *
     * @param subSegmentList List of sub-segments
     */
    public KeySegment(List<NameSegment> subSegmentList) {
        if (subSegmentList.size() < 1) {
            throw new IllegalArgumentException();
        }
        this.subSegmentList =
            new UnmodifiableArrayList<NameSegment>(
                subSegmentList.toArray(
                    new NameSegment[subSegmentList.size()]));
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        toString(buf);
        return buf.toString();
    }

    public void toString(StringBuilder buf) {
        for (IdentifierSegment segment : subSegmentList) {
            buf.append('&');
            segment.toString(buf);
        }
    }

    public ParseRegion getRegion() {
        return IdentifierNode.sumSegmentRegions(subSegmentList);
    }

    public Quoting getQuoting() {
        return Quoting.KEY;
    }

    public String getName() {
        return null;
    }

    public List<NameSegment> getKeyParts() {
        return subSegmentList;
    }
}

// End KeySegment.java
