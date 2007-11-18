/*
// $Id: ParseTreeNode.java 20 2007-06-10 23:09:28Z jhyde $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx.parser;

import org.olap4j.mdx.ParseRegion;

/**
 * Exception thrown by an {@link org.olap4j.mdx.parser.MdxParser} to
 * indicate an error in parsing. Has a {@link org.olap4j.mdx.ParseRegion}.
 *
 * @author jhyde
 * @version $Id: $
 */
public class MdxParseException extends RuntimeException {
    private final ParseRegion region;

    /**
     * Creates an MdxParseException with a region of the source code and a
     * specified cause.
     *
     * @param region Region of source code which contains the error
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public MdxParseException(ParseRegion region, Throwable cause) {
        super(cause);
        this.region = region;
    }

    /**
     * Creates an MdxParseException with a region of the source code and a
     * specified detail message.
     *
     * @param region Region of source code which contains the error
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public MdxParseException(ParseRegion region, String message) {
        super(message);
        this.region = region;
    }

    public ParseRegion getRegion() {
        return region;
    }
}

// End MdxParseException.java
