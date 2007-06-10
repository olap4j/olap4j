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

import java.util.regex.Pattern;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Utility methods for MDX parsing.
 *
 * <p>NOTE: Package protected. Not part of the public olap4j API.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 6, 2007
 */
class MdxUtil {
    private static final Pattern QUOT_PATTERN =
        Pattern.compile("\"", Pattern.LITERAL);

    /**
     * Converts a string into a double-quoted string.
     */
    static String quoteForMdx(String val) {
        StringBuilder buf = new StringBuilder(val.length() + 20);
        buf.append("\"");
        buf.append(QUOT_PATTERN.matcher(val).replaceAll("\"\""));
        buf.append("\"");
        return buf.toString();
    }

    static String toString(ParseTreeNode node) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ParseTreeWriter parseTreeWriter = new ParseTreeWriter(pw);
        node.unparse(parseTreeWriter);
        pw.flush();
        return sw.toString();
    }

    /**
     * Encodes string for MDX (escapes ] as ]] inside a name).
     */
    static String mdxEncodeString(String st) {
        StringBuilder retString = new StringBuilder(st.length() + 20);
        for (int i = 0; i < st.length(); i++) {
            char c = st.charAt(i);
            if ((c == ']') &&
                ((i+1) < st.length()) &&
                (st.charAt(i+1) != '.')) {

                retString.append(']'); //escaping character
            }
            retString.append(c);
        }
        return retString.toString();
    }
}

// End MdxUtil.java
