/*
// $Id$
//
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
package org.olap4j.mdx;

import org.olap4j.impl.Spacer;

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
    private final Spacer spacer = new Spacer();

    private static final int INDENT = 4;

    /**
     * Creates a ParseTreeWriter.
     *
     * @param pw Underlying writer
     */
    public ParseTreeWriter(PrintWriter pw) {
        this((Writer)pw);
    }

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
                spacer.spaces(this);
            }
        };
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
        spacer.add(INDENT);
    }

    /**
     * Decreases the indentation level.
     */
    public void outdent() {
        spacer.subtract(INDENT);
    }
}

// End ParseTreeWriter.java
