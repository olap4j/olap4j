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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.olap4j.impl.*;
import org.olap4j.type.Type;

/**
 * Multi-part identifier.
 *
 * <p>An identifier is immutable.
 *
 * <p>An identifer consists of one or more {@link IdentifierSegment}s. A segment
 * is either:<ul>
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
 * {@link IdentifierSegment#getKeyParts()} method. The parts
 * are of type {@link Quoting#UNQUOTED} or {@link Quoting#QUOTED}.
 *
 * <p>A simple example is the identifier {@code Measures.[Unit Sales]}. It
 * has two segments:<ul>
 * <li>Segment #0 is
 *     {@link Quoting#UNQUOTED UNQUOTED},
 *     name "Measures"</li>
 * <li>Segment #1 is
 *     {@link Quoting#QUOTED QUOTED},
 *     name "Unit Sales"</li>
 * </ul>
 *
 * <p>A more complex example illustrates a compound key. The identifier {@code
 * [Customers].[City].&amp;[San Francisco]&amp;CA&amp;USA.&amp;[cust1234]}
 * contains four segments as follows:
 * <ul>
 * <li>Segment #0 is QUOTED, name "Customers"</li>
 * <li>Segment #1 is QUOTED, name "City"</li>
 * <li>Segment #2 is a {@link Quoting#KEY KEY}.
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
    implements ParseTreeNode
{
    private final List<IdentifierSegment> segments;

    /**
     * Creates an identifier containing one or more segments.
     *
     * @param segments Array of Segments, each consisting of a name and quoting
     * style
     */
    public IdentifierNode(IdentifierSegment... segments) {
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
    public IdentifierNode(List<IdentifierSegment> segments) {
        if (segments.size() < 1) {
            throw new IllegalArgumentException();
        }
        this.segments =
            new UnmodifiableArrayList<IdentifierSegment>(
                segments.toArray(
                    new IdentifierSegment[segments.size()]));
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
    public List<IdentifierSegment> getSegmentList() {
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
    static ParseRegion sumSegmentRegions(
        final List<? extends IdentifierSegment> segments)
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
    public IdentifierNode append(IdentifierSegment segment) {
        List<IdentifierSegment> newSegments =
            new ArrayList<IdentifierSegment>(segments);
        newSegments.add(segment);
        return new IdentifierNode(newSegments);
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(this);
    }

    public String toString() {
        return unparseIdentifierList(segments);
    }

    public IdentifierNode deepCopy() {
        // IdentifierNode is immutable
        return this;
    }

    /**
     * Parses an MDX identifier string into an
     * {@link org.olap4j.mdx.IdentifierNode}.
     *
     * <p>It contains a list of {@link IdentifierSegment segments}, each
     * of which is a name combined with a description of how the name
     * was {@link Quoting quoted}. For example,
     *
     * <blockquote><code>
     * parseIdentifier(
     * "[Customers].USA.[South Dakota].[Sioux Falls].&amp;[1245]")
     * </code></blockquote>
     *
     * returns an IdentifierNode consisting of the following segments:
     *
     * <code><ul>
     * <li>NameSegment("Customers", quoted=true),
     * <li>NameSegment("USA", quoted=false),
     * <li>NameSegment("South Dakota", quoted=true),
     * <li>NameSegment("Sioux Falls", quoted=true),
     * <li>KeySegment( { NameSegment("1245", quoted=true) } )
     * </ul></code>
     *
     * @see #ofNames(String...)
     *
     * @param identifier MDX identifier string
     *
     * @return Identifier parse tree node
     *
     * @throws IllegalArgumentException if the format of the identifier is
     * invalid
     */
    public static IdentifierNode parseIdentifier(String identifier)  {
        return new IdentifierNode(IdentifierParser.parseIdentifier(identifier));
    }

    /**
     * Converts an array of quoted name segments into an identifier.
     *
     * <p>For example,
     *
     * <blockquote><code>
     * IdentifierNode.ofNames("Store", "USA", "CA")</code></blockquote>
     *
     * returns an IdentifierNode consisting of the following segments:
     *
     * <code><ul>
     * <li>NameSegment("Customers", quoted=true),
     * <li>NameSegment("USA", quoted=false),
     * <li>NameSegment("South Dakota", quoted=true),
     * <li>NameSegment("Sioux Falls", quoted=true),
     * <li>KeySegment( { NameSegment("1245", quoted=true) } )
     * </ul></code>
     *
     * @see #parseIdentifier(String)
     *
     * @param names Array of names
     *
     * @return Identifier parse tree node
     */
    public static IdentifierNode ofNames(String... names) {
        final List<IdentifierSegment> list =
            new ArrayList<IdentifierSegment>();
        for (String name : names) {
            list.add(new NameSegment(null, name, Quoting.QUOTED));
        }
        return new IdentifierNode(list);
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
    static String unparseIdentifierList(
        List<? extends IdentifierSegment> segments)
    {
        final StringBuilder buf = new StringBuilder(64);
        for (int i = 0; i < segments.size(); i++) {
            IdentifierSegment segment = segments.get(i);
            if (i > 0) {
                buf.append('.');
            }
            segment.toString(buf);
        }
        return buf.toString();
    }
}

// End IdentifierNode.java
