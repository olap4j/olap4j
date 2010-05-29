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

import java.io.PrintWriter;

/**
 * Writer for MDX parse tree.
 *
 * <p>Typical use is with the {@link ParseTreeNode#unparse(ParseTreeWriter)}
 * method as follows:
 *
 * <blockquote>
 * <pre>
 * ParseTreeNode node;
 * StringWriter sw = new StringWriter();
 * PrintWriter pw = new PrintWriter(sw);
 * ParseTreeWriter mdxWriter = new ParseTreeWriter(pw);
 * node.unparse(mdxWriter);
 * pw.flush();
 * String mdx = sw.toString();
 * </pre>
 * </blockquote>
 *
 *
 * @see org.olap4j.mdx.ParseTreeNode#unparse(ParseTreeWriter)
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
public class ParseTreeWriter {
    private final PrintWriter pw;

    /**
     * Creates a ParseTreeWriter.
     *
     * @param pw Underlying writer
     */
    public ParseTreeWriter(PrintWriter pw) {
        this.pw = pw;
    }

    /**
     * Returns the underlying writer.
     *
     * @return underlying writer
     */
    public PrintWriter getPrintWriter() {
        return pw;
    }
}

// End ParseTreeWriter.java
