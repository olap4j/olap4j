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

/**
 * Node in a parse tree representing a parsed MDX statement.
 *
 * <p>To convert a parse tree to an MDX string, use an {@link ParseTreeWriter} as
 * follows:
 * <blockquote>
 * <pre>
 * SelectNode select = null;
 * StringWriter sw = new StringWriter();
 * PrintWriter pw = new PrintWriter(sw);
 * ParseTreeWriter mdxWriter = new ParseTreeWriter(pw);
 * select.unparse(mdxWriter);
 * pw.flush();
 * String mdx = sw.toString();
 * </pre>
 * </blockquote>
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
public interface ParseTreeNode {
    /**
     * Accepts a visitor to this MDX parse tree node.
     *
     * <p>The implementation should generally dispatches to the
     * {@link ParseTreeVisitor#visit} method appropriate to the type of expression.
     *
     * @param visitor Visitor
     * @return T, the specific return type of the visitor
     */
    <T> T accept(ParseTreeVisitor<T> visitor);

    /**
     * Returns the type of this expression.
     *
     * <p>Returns null if this is not an expression, for instance a
     * <code>SELECT</code> node.
     *
     * @return type of this expression
     */
    Type getType();

    void unparse(ParseTreeWriter writer);
}

// End ParseTreeNode.java
