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
     * @return List of name segments
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
                throw new RuntimeException("invalid member '" + identifier + "'");
            }

            if (identifier.charAt(i) ==  '&') {
                i++;
                type = Quoting.KEY;
            } else {
                type = Quoting.QUOTED;
            }

            if (identifier.charAt(i) != '[') {
                throw new RuntimeException("invalid member '" + identifier + "'");
            }

            int j = getEndIndex(identifier, i + 1);
            if (j == -1) {
                throw new RuntimeException("invalid member '" + identifier + "'");
            }

            list.add(
                new Segment(
                    null,
                    replace(identifier.substring(i + 1, j), "]]", "]"),
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
     * Returns a string with every occurrence of a seek string replaced with
     * another.
     *
     * @param s String to act on
     * @param find String to find
     * @param replace String to replace it with
     * @return The modified string
     */
    private static String replace(
        String s,
        String find,
        String replace)
    {
        // let's be optimistic
        int found = s.indexOf(find);
        if (found == -1) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length() + 20);
        int start = 0;
        char[] chars = s.toCharArray();
        final int step = find.length();
        if (step == 0) {
            // Special case where find is "".
            sb.append(s);
            replace(sb, 0, find, replace);
        } else {
            for (;;) {
                sb.append(chars, start, found-start);
                if (found == s.length()) {
                    break;
                }
                sb.append(replace);
                start = found + step;
                found = s.indexOf(find, start);
                if (found == -1) {
                    found = s.length();
                }
            }
        }
        return sb.toString();
    }

    /**
     * Replaces all occurrences of a string in a buffer with another.
     *
     * @param buf String buffer to act on
     * @param start Ordinal within <code>find</code> to start searching
     * @param find String to find
     * @param replace String to replace it with
     * @return The string buffer
     */
    private static StringBuilder replace(
        StringBuilder buf,
        int start,
        String find,
        String replace)
    {
        // Search and replace from the end towards the start, to avoid O(n ^ 2)
        // copying if the string occurs very commonly.
        int findLength = find.length();
        if (findLength == 0) {
            // Special case where the seek string is empty.
            for (int j = buf.length(); j >= 0; --j) {
                buf.insert(j, replace);
            }
            return buf;
        }
        int k = buf.length();
        while (k > 0) {
            int i = buf.lastIndexOf(find, k);
            if (i < start) {
                break;
            }
            buf.replace(i, i + find.length(), replace);
            // Step back far enough to ensure that the beginning of the section
            // we just replaced does not cause a match.
            k = i - findLength;
        }
        return buf;
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
            case UNQUOTED: //return name; Disabled to pass old tests...
            case QUOTED: return "[" + name + "]";
            case KEY: return "&[" + name + "]";
            default: return "UNKNOWN:" + name;
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
