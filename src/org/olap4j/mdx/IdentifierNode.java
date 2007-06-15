/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.type.Type;

import java.util.*;

/**
 * Multi-part identifier.
 *
 * <p>An identifier is immutable.
 *
 * @version $Id$
 * @author jhyde
 */
public class IdentifierNode
    implements ParseTreeNode {

    private final List<Segment> segments;

    /**
     * Creates an identifier containing one or more segments.
     *
     * @param segments Array of Segments, each consisting of a name and quoting
     * style
     */
    public IdentifierNode(IdentifierNode.Segment... segments) {
        this(Arrays.asList(segments));
    }

    /**
     * Creates an identifier containing a list of segments.
     *
     * @param segments List of segments
     */
    private IdentifierNode(List<IdentifierNode.Segment> segments) {
        if (segments.size() < 1) {
            throw new IllegalArgumentException();
        }
        this.segments =
            Collections.unmodifiableList(
                new ArrayList<Segment>(segments));
    }

    public Type getType() {
        // Can't give the type until we have resolved.
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the list of segments which consistitute this identifier.
     *
     * @return list of constituent segments
     */
    public List<Segment> getSegmentList() {
        return segments;
    }

    public ParseRegion getRegion() {
        // Region is the span from the first segment to the last.
        return ParseRegion.sum(
            new Iterable<ParseRegion>() {
                public Iterator<ParseRegion> iterator() {
                    final Iterator<Segment> segmentIter = segments.iterator();
                    return new Iterator<ParseRegion>() {
                        public boolean hasNext() {
                            return segmentIter.hasNext();
                        }

                        public ParseRegion next() {
                            return segmentIter.next().region;
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            }
        );
    }

    /**
     * Returns a new Identifier consisting of this one with another segment
     * appended. Does not modify this Identifier.
     *
     * @param segment Name of segment
     * @return New identifier
     */
    public IdentifierNode append(IdentifierNode.Segment segment) {
        List<IdentifierNode.Segment> newSegments =
            new ArrayList<Segment>(segments);
        newSegments.add(segment);
        return new IdentifierNode(newSegments);
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(toString());
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder();
        int k = 0;
        for (IdentifierNode.Segment s : segments) {
            if (k++ > 0) {
                buf.append('.');
            }
            switch (s.quoting) {
            case UNQUOTED:
                buf.append(s.name);
                break;
            case KEY:
                buf.append("&[");
                buf.append(MdxUtil.mdxEncodeString(s.name));
                buf.append("]");
                break;
            case QUOTED:
                buf.append("[");
                buf.append(MdxUtil.mdxEncodeString(s.name));
                buf.append("]");
                break;
            }
        }
        return buf.toString();
    }

    /**
     * Component in a compound identifier. It is described by its name and how
     * the name is quoted.
     *
     * <p>For example, the identifier
     * <code>[Store].USA.[New Mexico].&[45]</code> has four segments:<ul>
     * <li>"Store", {@link IdentifierNode.Quoting#QUOTED}</li>
     * <li>"USA", {@link IdentifierNode.Quoting#UNQUOTED}</li>
     * <li>"New Mexico", {@link IdentifierNode.Quoting#QUOTED}</li>
     * <li>"45", {@link IdentifierNode.Quoting#KEY}</li>
     * </ul>
     */
    public static class Segment {
        public final String name;
        public final IdentifierNode.Quoting quoting;
        private final ParseRegion region;

        /**
         * Creates a segment with the given quoting and region.
         *
         * @param name Name
         * @param quoting Quoting style
         */
        public Segment(
            ParseRegion region,
            String name,
            IdentifierNode.Quoting quoting)
        {
            this.region = region;
            this.name = name;
            this.quoting = quoting;
        }

        /**
         * Creates a quoted segment, "[name]".
         *
         * @param name Name of segment
         */
        public Segment(String name) {
            this(null, name, Quoting.QUOTED);
        }

        /**
         * Returns the region of the source code which this Segment was created
         * from, if it was created by parsing.
         */
        public ParseRegion getRegion() {
            return region;
        }
    }

    public enum Quoting {

        /**
         * Unquoted identifier, for example "Measures".
         */
        UNQUOTED,

        /**
         * Quoted identifier, for example "[Measures]".
         */
        QUOTED,

        /**
         * Identifier quoted with an ampersand to indicate a key value, for example
         * the second segment in "[Employees].&[89]".
         */
        KEY
    }
}

// End IdentifierNode.java
