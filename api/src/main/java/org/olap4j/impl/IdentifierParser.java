/*
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
package org.olap4j.impl;

import org.olap4j.mdx.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for parsing fully-qualified member names, tuples, member lists,
 * and tuple lists.
 *
 * <p><b>NOTE</b>: Like other classes in the org.olap4j.impl package, this class
 * is not part of the public olap4j API. It is subject to change or removal
 * without notice. It is provided in the hope that it will be useful to
 * implementors of olap4j drivers.
 *
 * @author jhyde
 */
public class IdentifierParser {
    // lexical states
    private static final int START = 0;
    private static final int BEFORE_SEG = 1;
    private static final int IN_BRACKET_SEG = 2;
    private static final int AFTER_SEG = 3;
    private static final int IN_SEG = 4;

    private static char charAt(String s, int pos) {
        return pos < s.length() ? s.charAt(pos) : 0;
    }

    /**
     * Parses a list of tuples (or a list of members).
     *
     * @param builder Builder, called back when each segment, member and tuple
     *     is complete.
     * @param string String to parse
     */
    public static void parseTupleList(
        Builder builder,
        String string)
    {
        int i = 0;
        char c;
        while ((c = charAt(string, i++)) == ' ') {
        }
        if (c != '{') {
            throw fail(string, i, "{");
        }
        while (true) {
            i = parseTuple(builder, string, i);
            while ((c = charAt(string, i++)) == ' ') {
            }
            if (c == ',') {
                // fine
            } else if (c == '}') {
                // we're done
                return;
            } else {
                throw fail(string, i, ", or }");
            }
        }
    }

    /**
     * Parses a tuple, of the form '(member, member, ...)', and calls builder
     * methods when finding a segment, member or tuple.
     *
     * @param builder Builder
     * @param string String to parse
     * @param i Position to start parsing in string
     * @return Position where parsing ended in string
     */
    public static int parseTuple(
        Builder builder,
        String string,
        int i)
    {
        char c;
        while ((c = charAt(string, i++)) == ' ') {
        }
        if (c != '(') {
            throw fail(string, i, "(");
        }
        while (true) {
            i = parseMember(builder, string, i);
            while ((c = charAt(string, i++)) == ' ') {
            }
            if (c == ',') {
                // fine
            } else if (c == ')') {
                builder.tupleComplete();
                break;
            }
        }
        return i;
    }

    public static void parseMemberList(
        Builder builder,
        String string)
    {
        int i = 0;
        char c = charAt(string, i);
        while (c > 0 && c <= ' ') {
            c = charAt(string, ++i);
        }
        boolean leadingBrace = false;
        boolean trailingBrace = false;
        if (c == '{') {
            leadingBrace = true;
            ++i;
        }
        w:
        while (true) {
            i = parseMember(builder, string, i);
            c = charAt(string, i);
            while (c > 0 && c <= ' ') {
                c = charAt(string, ++i);
            }
            switch (c) {
            case 0:
                break w;
            case ',':
                // fine
                ++i;
                break;
            case '}':
                // we're done
                trailingBrace = true;
                break w;
            default:
                throw fail(string, i, ", or }");
            }
        }
        if (leadingBrace != trailingBrace) {
            throw new IllegalArgumentException(
                "mismatched '{' and '}' in '" + string + "'");
        }
    }

    public static int parseMember(
        Builder builder,
        String string,
        int i)
    {
        int k = string.length();
        int state = START;
        int start = 0;
        Builder.Syntax syntax = Builder.Syntax.NAME;
        char c;

        loop:
        while (i < k + 1) {
            c = charAt(string, i);
            switch (state) {
            case START:
            case BEFORE_SEG:
                switch (c) {
                case '[':
                    ++i;
                    start = i;
                    state = IN_BRACKET_SEG;
                    break;

                case ' ':
                    // Skip whitespace, don't change state.
                    ++i;
                    break;

                case ',':
                case '}':
                case 0:
                    break loop;

                case '.':
                    // TODO: test this, case: ".abc"
                    throw new IllegalArgumentException("unexpected: '.'");

                case '&':
                    // Note that we have seen ampersand, but don't change state.
                    ++i;
                    if (syntax != Builder.Syntax.NAME) {
                        throw new IllegalArgumentException("unexpected: '&'");
                    }
                    syntax = Builder.Syntax.FIRST_KEY;
                    break;

                default:
                    // Carry on reading.
                    state = IN_SEG;
                    start = i;
                    break;
                }
                break;

            case IN_SEG:
                switch (c) {
                case ',':
                case ')':
                case '}':
                case 0:
                    builder.segmentComplete(
                        null,
                        string.substring(start, i).trim(),
                        Quoting.UNQUOTED,
                        syntax);
                    state = AFTER_SEG;
                    break loop;
                case '.':
                    builder.segmentComplete(
                        null,
                        string.substring(start, i).trim(),
                        Quoting.UNQUOTED,
                        syntax);
                    syntax = Builder.Syntax.NAME;
                    state = BEFORE_SEG;
                    ++i;
                    break;
                case '&':
                    builder.segmentComplete(
                        null,
                        string.substring(start, i).trim(),
                        Quoting.UNQUOTED,
                        syntax);
                    syntax = Builder.Syntax.NEXT_KEY;
                    state = BEFORE_SEG;
                    ++i;
                    break;
                default:
                    ++i;
                }
                break;

            case IN_BRACKET_SEG:
                switch (c) {
                case 0:
                    throw new IllegalArgumentException(
                        "Expected ']', in member identifier '" + string + "'");
                case ']':
                    if (charAt(string, i + 1) == ']') {
                        ++i;
                        // fall through
                    } else {
                        builder.segmentComplete(
                            null,
                            Olap4jUtil.replace(
                                string.substring(start, i), "]]", "]"),
                            Quoting.QUOTED,
                            syntax);
                        ++i;
                        state = AFTER_SEG;
                        break;
                    }
                default:
                    // Carry on reading.
                    ++i;
                }
                break;

            case AFTER_SEG:
                switch (c) {
                case ' ':
                    // Skip over any spaces
                    // TODO: test this case: '[foo]  .  [bar]'
                    ++i;
                    break;
                case '.':
                    state = BEFORE_SEG;
                    syntax = Builder.Syntax.NAME;
                    ++i;
                    break;
                case '&':
                    state = BEFORE_SEG;
                    // Roll the syntax - NAME=>FIRST_KEY=>NEXT_KEY=>NEXT_KEY...
                    if (syntax == Builder.Syntax.NAME) {
                        syntax = Builder.Syntax.FIRST_KEY;
                    } else {
                        syntax = Builder.Syntax.NEXT_KEY;
                    }
                    ++i;
                    break;
                default:
                    // We're not looking at the start of a segment. Parse
                    // the member we've seen so far, then return.
                    break loop;
                }
                break;

            default:
                throw new AssertionError("unexpected state: " + state);
            }
        }

        switch (state) {
        case START:
            return i;
        case BEFORE_SEG:
            throw new IllegalArgumentException(
                "Expected identifier after '.', in member identifier '" + string
                + "'");
        case IN_BRACKET_SEG:
            throw new IllegalArgumentException(
                "Expected ']', in member identifier '" + string + "'");
        }
        // End of member.
        builder.memberComplete();
        return i;
    }

    private static IllegalArgumentException fail(
        String string,
        int i,
        String expecting)
    {
        throw new IllegalArgumentException(
            "expected '" + expecting + "' at position " + i + " in '"
            + string + "'");
    }

    /**
     * Parses an MDX identifier such as
     * <code>[Foo].[Bar].Baz.&amp;Key&amp;Key2</code>
     * and returns the result as a list of segments.
     *
     * @param s MDX identifier
     * @return List of segments
     */
    public static List<IdentifierSegment> parseIdentifier(String s)  {
        final MemberBuilder builder = new MemberBuilder();
        int i = parseMember(builder, s, 0);
        if (i < s.length()) {
            throw new IllegalArgumentException(
                "Invalid member identifier '" + s + "'");
        }
        return builder.segmentList;
    }

    /**
     * Parses a string consisting of a sequence of MDX identifiers and returns
     * the result as a list of compound identifiers, each of which is a list
     * of segments.
     *
     * <p>For example, parseIdentifierList("{foo.bar, baz}") returns
     * { {"foo", "bar"}, {"baz"} }.
     *
     * <p>The braces around the list are optional;
     * parseIdentifierList("foo.bar, baz") returns the same result as the
     * previous example.
     *
     * @param s MDX identifier list
     * @return List of lists of segments
     */
    public static List<List<IdentifierSegment>> parseIdentifierList(
        String s)
    {
        final MemberListBuilder builder = new MemberListBuilder();
        parseMemberList(builder, s);
        return builder.list;
    }

    /**
     * Callback that is called on completion of a structural element like a
     * member or tuple.
     *
     * <p>Implementations might create a list of members or just create a list
     * of unresolved names.
     */
    public interface Builder {
        /**
         * Called when a tuple is complete.
         */
        void tupleComplete();

        /**
         * Called when a member is complete.
         */
        void memberComplete();

        /**
         * Called when a segment is complete.
         *
         * <p>For example, the identifier {@code [Time].1997.[Jan].&31} contains
         * four name segments: "[Time]", "1997", "[Jan]" and "31". The first
         * and third are quoted; the last has an ampersand signifying that it
         * is a key.
         *
         * @param region Region of source code
         * @param name Name
         * @param quoting Quoting style
         * @param syntax Whether this is a name segment, first part of a key
         *     segment, or continuation of a key segment
         */
        void segmentComplete(
            ParseRegion region,
            String name,
            Quoting quoting,
            Syntax syntax);

        enum Syntax {
            NAME,
            FIRST_KEY,
            NEXT_KEY
        }
    }

    /**
     * Implementation of {@link org.olap4j.impl.IdentifierParser.Builder}
     * that collects the segments that make up the name of a member in a list.
     * It cannot handle tuples or lists of members.
     */
    public static class MemberBuilder implements Builder {
        protected final List<NameSegment> subSegments;
        protected final List<IdentifierSegment> segmentList;

        public MemberBuilder() {
            segmentList = new ArrayList<IdentifierSegment>();
            subSegments = new ArrayList<NameSegment>();
        }

        public void tupleComplete() {
            throw new UnsupportedOperationException();
        }

        public void memberComplete() {
            flushSubSegments();
        }

        private void flushSubSegments() {
            if (!subSegments.isEmpty()) {
                segmentList.add(new KeySegment(subSegments));
                subSegments.clear();
            }
        }

        public void segmentComplete(
            ParseRegion region,
            String name,
            Quoting quoting,
            Syntax syntax)
        {
            final NameSegment segment =
                new NameSegment(
                    region, name, quoting);
            if (syntax != Syntax.NEXT_KEY) {
                // If we were building a previous key, write it out.
                // E.g. [Foo].&1&2.&3&4&5.
                flushSubSegments();
            }
            if (syntax == Syntax.NAME) {
                segmentList.add(segment);
            } else {
                subSegments.add(segment);
            }
        }
    }

    /**
     * Implementation of {@link org.olap4j.impl.IdentifierParser.Builder}
     * that collects segments into members,
     * then collects members into lists.
     */
    public static class MemberListBuilder extends MemberBuilder {
        final List<List<IdentifierSegment>> list =
            new ArrayList<List<IdentifierSegment>>();

        public void memberComplete() {
            super.memberComplete();
            list.add(
                new ArrayList<IdentifierSegment>(segmentList));
            segmentList.clear();
        }
    }
}

// End IdentifierParser.java
