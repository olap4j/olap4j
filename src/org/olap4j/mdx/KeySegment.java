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
package org.olap4j.mdx;

import org.olap4j.impl.UnmodifiableArrayList;

import java.util.List;

/**
 * Segment that represents a key or compound key.
 *
 * <p>Such a segment appears in an identifier with each component prefixed
 * with "&amp;". For example, in the identifier
 * "<code>[Customer].[State].&amp;[WA]&amp;[USA]</code>", the third segment is
 * a compound key whose parts are "{@code WA}" and "{@code USA}".
 *
 * @see org.olap4j.mdx.NameSegment
 *
 * @version $Id$
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
