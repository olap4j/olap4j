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
import java.io.Writer;

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
    private int linePrefixLength;
    private String linePrefix;

    private static final int INDENT = 4;
    private static String bigString = "                ";

    /**
     * Creates a ParseTreeWriter.
     *
     * @param w Underlying writer
     */
    public ParseTreeWriter(Writer w) {
        this.pw = new PrintWriter(w) {
            @Override
            public void println() {
                super.println();
                print(linePrefix);
            }
        };
        this.linePrefixLength = 0;
        setPrefix();
    }

    /**
     * Returns the print writer.
     *
     * @return print writer
     */
    public PrintWriter getPrintWriter() {
        return pw;
    }

    /**
     * Increases the indentation level.
     */
    public void indent() {
        linePrefixLength += INDENT;
        setPrefix();
    }

    private void setPrefix() {
        linePrefix = spaces(linePrefixLength);
    }

    /**
     * Decreases the indentation level.
     */
    public void outdent() {
        linePrefixLength -= INDENT;
        setPrefix();
    }

    /**
     * Returns a string of N spaces.
     * @param n Number of spaces
     * @return String of N spaces
     */
    private static synchronized String spaces(int n)
    {
        while (n > bigString.length()) {
            bigString = bigString + bigString;
        }
        return bigString.substring(0, n);
    }
}

// End ParseTreeWriter.java
