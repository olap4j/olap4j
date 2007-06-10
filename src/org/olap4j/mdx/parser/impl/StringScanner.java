/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx.parser.impl;

/**
 * Lexical analyzer whose input is a string.
 *
 * <p>NOTE: This class is not part of the public olap4j API.
 *
 * @version $Id$
 * @author jhyde
 */
class StringScanner extends Scanner {
    private final String s;
    private int i;

    /**
     * Creates a StringScanner.
     *
     * @param s Input string
     * @param debug Whether to emit debug messages
     */
    StringScanner(String s, boolean debug) {
        super(debug);
        this.s = s;
        i = 0;
    }

    protected int getChar() {
        return (i >= s.length())
            ? -1
            : s.charAt(i++);
    }
}

// End StringScanner.java
