/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.impl.Olap4jUtil;
import org.olap4j.impl.UnmodifiableArrayList;
import org.olap4j.type.Type;

import java.util.*;

/**
 * Multi-part identifier.
 *
 * <p>An identifier is immutable.
 *
 * <p>An identifer consists of one or more {@link Segment}s. A segment is
 * either:<ul>
 * <li>An unquoted value such as '{@code CA}',
 * <li>A value quoted in brackets, such as '{@code [San Francisco]}', or
 * <li>A key of one or more parts, each of which is prefixed with '&amp;',
 *     such as '{@code &amp;[Key 1]&amp;Key2&amp;[5]}'.
 * </ul>
 *
 * <p>Segment types are indicated by the {@link Quoting} enumeration.
 *
 * <p>A key segment is of type {@link Quoting#KEY}, and has one or more
 * component parts accessed via the
 * {@link Segment#getKeyParts()} method. The parts
 * are of type {@link Quoting#UNQUOTED} or {@link Quoting#QUOTED}.
 *
 * <p>A simple example is the identifier {@code Measures.[Unit Sales]}. It
 * has two segments:<ul>
 * <li>Segment #0 is
 *     {@link org.olap4j.mdx.IdentifierNode.Quoting#UNQUOTED UNQUOTED},
 *     name "Measures"</li>
 * <li>Segment #1 is
 *     {@link org.olap4j.mdx.IdentifierNode.Quoting#QUOTED QUOTED},
 *     name "Unit Sales"</li>
 * </ul>
 *
 * <p>A more complex example illustrates a compound key. The identifier {@code
 * [Customers].[City].&amp;[San Francisco]&amp;CA&amp;USA.&amp;[cust1234]}
 * contains four segments as follows:
 * <ul>
 * <li>Segment #0 is QUOTED, name "Customers"</li>
 * <li>Segment #1 is QUOTED, name "City"</li>
 * <li>Segment #2 is a {@link org.olap4j.mdx.IdentifierNode.Quoting#KEY KEY}.
 *     It has 3 sub-segments:
 *     <ul>
 *     <li>Sub-segment #0 is QUOTED, name "San Francisco"</li>
 *     <li>Sub-segment #1 is UNQUOTED, name "CA"</li>
 *     <li>Sub-segment #2 is UNQUOTED, name "USA"</li>
 *     </ul>
 * </li>
 * <li>Segment #3 is a KEY. It has 1 sub-segment:
 *     <ul>
 *     <li>Sub-segment #0 is QUOTED, name "cust1234"</li>
 *     </ul>
 * </li>
 * </ul>
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
        if (segments.length < 1) {
            throw new IllegalArgumentException();
        }
        this.segments = UnmodifiableArrayList.asCopyOf(segments);
    }

    /**
     * Creates an identifier containing a list of segments.
     *
     * @param segments List of segments
     */
    public IdentifierNode(List<IdentifierNode.Segment> segments) {
        if (segments.size() < 1) {
            throw new IllegalArgumentException();
        }
        this.segments =
            new UnmodifiableArrayList<Segment>(
                segments.toArray(
                    new Segment[segments.size()]));
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
        return sumSegmentRegions(segments);
    }

    /**
     * Returns a region encompassing the regions of the first through the last
     * of a list of segments.
     *
     * @param segments List of segments
     * @return Region encompassed by list of segments
     */
    private static ParseRegion sumSegmentRegions(
        final List<? extends Segment> segments)
    {
        return ParseRegion.sum(
            new AbstractList<ParseRegion>() {
                public ParseRegion get(int index) {
                    return segments.get(index).getRegion();
                }

                public int size() {
                    return segments.size();
                }
            });
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
        return unparseIdentifierList(segments);
    }

    public IdentifierNode deepCopy() {
        // IdentifierNode is immutable
        return this;
    }

    /**
     * Parses an MDX identifier into a list of segments.
     *
     * <p>Each segment is a name combined with a description of how the name
     * was {@link Quoting quoted}. For example,
     *
     * <blockquote><code>
     * parseIdentifier("[Customers].USA.[South Dakota].[Sioux Falls].&amp;[1245]")
     * </code></blockquote>
     *
     * returns
     *
     * <blockquote><code>
     * { Segment("Customers", QUOTED),
     * Segment("USA", UNQUOTED),
     * Segment("South Dakota", QUOTED),
     * Segment("Sioux Falls", QUOTED),
     * Segment("1245", KEY) }
     * </code></blockquote>
     *
     * @see org.olap4j.metadata.Cube#lookupMember(String[])
     *
     * @param identifier MDX identifier string
     *
     * @return List of name segments
     *
     * @throws IllegalArgumentException if the format of the identifier is
     * invalid
     */
    public static List<Segment> parseIdentifier(String identifier)  {
        if (!identifier.startsWith("[")) {
            return Collections.<Segment>singletonList(
                new NameSegment(null, identifier, Quoting.UNQUOTED));
        }

        List<Segment> list = new ArrayList<Segment>();
        int i = 0;
        Quoting type;
        while (i < identifier.length()) {
            if (identifier.charAt(i) != '&' && identifier.charAt(i) != '[') {
                throw new IllegalArgumentException(
                    "Invalid member identifier '" + identifier + "'");
            }

            if (identifier.charAt(i) ==  '&') {
                i++;
                type = Quoting.KEY;
            } else {
                type = Quoting.QUOTED;
            }

            if (identifier.charAt(i) != '[') {
                throw new IllegalArgumentException(
                    "Invalid member identifier '" + identifier + "'");
            }

            int j = getEndIndex(identifier, i + 1);
            if (j == -1) {
                throw new IllegalArgumentException(
                    "Invalid member identifier '" + identifier + "'");
            }

            list.add(
                new NameSegment(
                    null,
                    Olap4jUtil.replace(
                        identifier.substring(i + 1, j), "]]", "]"),
                    type));

            i = j + 2;
        }
        return list;
    }

    /**
     * Returns the end of the current segment.
     *
     * @param s Identifier string
     * @param i Start of identifier segment
     * @return End of segment
     */
    private static int getEndIndex(String s, int i) {
        while (i < s.length()) {
            char ch = s.charAt(i);
            if (ch == ']') {
                if (i + 1 < s.length() && s.charAt(i + 1) == ']') {
                    // found ]] => skip
                    i += 2;
                } else {
                    return i;
                }
            } else {
                i++;
            }
        }
        return -1;
    }

    /**
     * Returns string quoted in [...].
     *
     * <p>For example, "San Francisco" becomes
     * "[San Francisco]"; "a [bracketed] string" becomes
     * "[a [bracketed]] string]".
     *
     * @param id Unquoted name
     * @return Quoted name
     */
    static String quoteMdxIdentifier(String id) {
        StringBuilder buf = new StringBuilder(id.length() + 20);
        quoteMdxIdentifier(id, buf);
        return buf.toString();
    }

    /**
     * Returns a string quoted in [...], writing the results to a
     * {@link StringBuilder}.
     *
     * @param id Unquoted name
     * @param buf Builder to write quoted string to
     */
    static void quoteMdxIdentifier(String id, StringBuilder buf) {
        buf.append('[');
        int start = buf.length();
        buf.append(id);
        Olap4jUtil.replace(buf, start, "]", "]]");
        buf.append(']');
    }

    /**
     * Converts a sequence of identifiers to a string.
     *
     * <p>For example, {"Store", "USA",
     * "California"} becomes "[Store].[USA].[California]".
     *
     * @param segments List of segments
     * @return Segments as quoted string
     */
    static String unparseIdentifierList(List<? extends Segment> segments) {
        final StringBuilder buf = new StringBuilder(64);
        for (int i = 0; i < segments.size(); i++) {
            Segment segment = segments.get(i);
            if (i > 0) {
                buf.append('.');
            }
            segment.toString(buf);
        }
        return buf.toString();
    }

    /**
     * Component in a compound identifier. It is described by its name and how
     * the name is quoted.
     *
     * <p>For example, the identifier
     * <code>[Store].USA.[New Mexico].&amp;[45]</code> has four segments:<ul>
     * <li>"Store", {@link IdentifierNode.Quoting#QUOTED}</li>
     * <li>"USA", {@link IdentifierNode.Quoting#UNQUOTED}</li>
     * <li>"New Mexico", {@link IdentifierNode.Quoting#QUOTED}</li>
     * <li>"45", {@link IdentifierNode.Quoting#KEY}</li>
     * </ul>
     *
     * <p>QUOTED and UNQUOTED segments are represented using a
     * {@link org.olap4j.mdx.IdentifierNode.NameSegment NameSegment};
     * KEY segments are represented using a
     * {@link org.olap4j.mdx.IdentifierNode.KeySegment KeySegment}.
     *
     * <p>To parse an identifier into a list of segments, use the method
     * {@link IdentifierNode#parseIdentifier(String)}.</p>
     */
    public interface Segment {

        /**
         * Returns a string representation of this Segment.
         *
         * <p>For example, "[Foo]", "&amp;[123]", "Abc".
         *
         * @return String representation of this Segment
         */
        String toString();

        /**
         * Appends a string representation of this Segment to a StringBuffer.
         *
         * @param buf StringBuffer
         */
        void toString(StringBuilder buf);

        /**
         * Returns the region of the source code which this Segment was created
         * from, if it was created by parsing.
         *
         * @return region of source code
         */
        ParseRegion getRegion();

        /**
         * Returns how this Segment is quoted.
         *
         * @return how this Segment is quoted
         */
        Quoting getQuoting();

        /**
         * Returns the name of this Segment.
         * Returns {@code null} if this Segment represents a key.
         *
         * @return name of this Segment
         */
        String getName();

        /**
         * Returns the key components, if this Segment is a key. (That is,
         * if {@link #getQuoting()} returns
         * {@link org.olap4j.mdx.IdentifierNode.Quoting#KEY}.)
         *
         * Returns null otherwise.
         *
         * @return Components of key, or null if this Segment is not a key
         */
        List<NameSegment> getKeyParts();
    }

    /**
     * Component in a compound identifier that describes the name of an object.
     * Optionally, the name is quoted in brackets.
     *
     * @see org.olap4j.mdx.IdentifierNode.KeySegment
     */
    public static class NameSegment implements Segment {
        final String name;
        final IdentifierNode.Quoting quoting;
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
            IdentifierNode.Quoting quoting)
        {
            this.region = region;
            this.name = name;
            this.quoting = quoting;
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
                return quoteMdxIdentifier(name);
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
                quoteMdxIdentifier(name, buf);
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

    /**
     * Segment that represents a key or compound key.
     *
     * <p>Such a segment appears in an identifier with each component prefixed
     * with '&amp;'. For example, in the identifier
     * '{@code [Customer].[State].&amp;[WA]&amp;[USA]}', the third segment is a
     * compound key whose parts are "@{code WA}" and "{@code USA}".
     *
     * @see org.olap4j.mdx.IdentifierNode.NameSegment
     */
    public static class KeySegment implements Segment {
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
            for (Segment segment : subSegmentList) {
                buf.append('&');
                segment.toString(buf);
            }
        }

        public ParseRegion getRegion() {
            return sumSegmentRegions(subSegmentList);
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

    /**
     * Enumeration of styles by which the component of an identifier can be
     * quoted.
     */
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
         * Identifier quoted with an ampersand and brackets to indicate a key
         * value, for example the second segment in "[Employees].&[89]".
         *
         * <p>Such a segment has one or more sub-segments. Each segment is
         * either quoted or unquoted. For example, the second segment in
         * "[Employees].&[89]&[San Francisco]&CA&USA" has four sub-segments,
         * two quoted and two unquoted.
         */
        KEY,
    }
}

// End IdentifierNode.java
