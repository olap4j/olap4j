/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.type.Type;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.PrintWriter;

/**
 * Multi-part identifier.
 */
public class IdentifierNode
    implements ParseTreeNode {

    private final List<Segment> segments;

    /**
     * Creates an identifier containing a single part.
     *
     * @param segment Segment, consisting of a name and quoting style
     */
    public IdentifierNode(IdentifierNode.Segment segment) {
        segments = Collections.singletonList(segment);
    }

    private IdentifierNode(List<IdentifierNode.Segment> segments) {
        this.segments = segments;
    }

    public Type getType() {
        // Can't give the type until we have resolved.
        throw new UnsupportedOperationException();
    }

    public List<Segment> getSegmentList() {
        return segments;
    }

    /**
     * Returns a new Identifier consisting of this one with another segment
     * appended. Does not modify this Identifier.
     *
     * @param segment Name of segment
     * @return New identifier
     */
    public IdentifierNode append(IdentifierNode.Segment segment) {
        List<IdentifierNode.Segment> newSegments = new ArrayList<Segment>(segments);
        newSegments.add(segment);
        return new IdentifierNode(newSegments);
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void unparse(ParseTreeWriter writer) {
        PrintWriter pw = writer.getPrintWriter();
        int k = 0;
        for (IdentifierNode.Segment s : segments) {
            if (k++ > 0) {
                pw.print(".");
            }
            switch (s.quoting) {
            case UNQUOTED:
                pw.print(s.name);
                break;
            case KEY:
                pw.print("&[" + MdxUtil.mdxEncodeString(s.name) + "]");
                break;
            case QUOTED:
                pw.print("[" + MdxUtil.mdxEncodeString(s.name) + "]");
                break;
            }
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
     */
    public static class Segment {
        public final String name;
        public final IdentifierNode.Quoting quoting;

        public Segment(String name, IdentifierNode.Quoting quoting) {
            this.name = name;
            this.quoting = quoting;
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
