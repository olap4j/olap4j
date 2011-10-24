/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import java.io.StringWriter;
import java.util.*;
import java.util.regex.Pattern;

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
     *
     * @param val String
     * @return String enclosed in double-quotes
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
        ParseTreeWriter parseTreeWriter = new ParseTreeWriter(sw);
        node.unparse(parseTreeWriter);
        return sw.toString();
    }

    /**
     * Encodes string for MDX (escapes ] as ]] inside a name).
     *
     * @param st String
     * @return String escaped for inclusion in an MDX identifier
     */
    static String mdxEncodeString(String st) {
        StringBuilder retString = new StringBuilder(st.length() + 20);
        for (int i = 0; i < st.length(); i++) {
            char c = st.charAt(i);
            if ((c == ']')
                && ((i + 1) < st.length())
                && (st.charAt(i + 1) != '.'))
            {
                retString.append(']'); // escaping character
            }
            retString.append(c);
        }
        return retString.toString();
    }

    /**
     * Creates a deep copy of a list.
     *
     * @param list List to be copied
     * @return Copy of list, with each element deep copied
     */
    @SuppressWarnings({"unchecked"})
    static <E extends ParseTreeNode> List<E> deepCopyList(List<E> list) {
        // Don't make a copy of the system empty list. '==' is intentional.
        if (list == Collections.EMPTY_LIST) {
            return list;
        }
        final ArrayList<E> listCopy = new ArrayList<E>(list.size());
        for (E e : list) {
            listCopy.add((E) e.deepCopy());
        }
        return listCopy;
    }
}

// End MdxUtil.java
