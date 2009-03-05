/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.impl.Olap4jUtil;
import org.olap4j.type.Type;

import java.io.Serializable;
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
    public IdentifierNode(List<IdentifierNode.Segment> segments) {
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
     * parseIdentifier("[Customers].USA.[South Dakota].[Sioux Falls].&[1245]")
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
            return Collections.singletonList(
                new Segment(null, identifier, Quoting.UNQUOTED));
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
                new Segment(
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
                if (i + 1 < s.length() && s.charAt(i + 1) == ']') { // found ]] => skip
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
     * @param ids List of segments
     * @return Segments as quoted string
     */
    static String unparseIdentifierList(List<Segment> ids) {
        StringBuilder sb = new StringBuilder(64);
        quoteMdxIdentifier(ids, sb);
        return sb.toString();
    }

    static void quoteMdxIdentifier(
        List<Segment> ids,
        StringBuilder sb)
    {
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                sb.append('.');
            }
            sb.append(ids.get(i).toString());
        }
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
     *
     * To parse an identifier into a list of segments, use the method
     * {@link IdentifierNode#parseIdentifier(String)}.</p>
     */
    public static class Segment implements Serializable {
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
         * Returns a string representation of this Segment.
         *
         * <p>For example, "[Foo]", "&[123]", "Abc".
         *
         * @return String representation of this Segment
         */
        public String toString() {
            switch (quoting) {
            case UNQUOTED:
                return name;
            case QUOTED:
                return quoteMdxIdentifier(name);
            case KEY:
                return '&' + quoteMdxIdentifier(name);
            default:
                throw Olap4jUtil.unexpected(quoting);
            }
        }

        /**
         * Appends this segment to a StringBuffer
         *
         * @param buf StringBuffer
         */
        void toString(StringBuilder buf) {
            switch (quoting) {
            case UNQUOTED:
                buf.append(name);
                return;
            case QUOTED:
                quoteMdxIdentifier(name, buf);
                return;
            case KEY:
                buf.append('&');
                quoteMdxIdentifier(name, buf);
                return;
            default:
                throw Olap4jUtil.unexpected(quoting);
            }
        }
        /**
         * Returns the region of the source code which this Segment was created
         * from, if it was created by parsing.
         *
         * @return region of source code
         */
        public ParseRegion getRegion() {
            return region;
        }

        /**
         * Returns the name of this Segment.
         *
         * @return name of this Segment
         */
        public String getName() {
            return name;
        }

        /**
         * Returns how this Segment is quoted.
         *
         * @return how this Segment is quoted
         */
        public Quoting getQuoting() {
            return quoting;
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
         * Identifier quoted with an ampersand to indicate a key value, for example
         * the second segment in "[Employees].&[89]".
         */
        KEY
    }
}

// End IdentifierNode.java
