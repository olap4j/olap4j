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

    public MdxParseException(ParseRegion region, Throwable cause) {
        super(cause);
        this.region = region;
    }

    public MdxParseException(ParseRegion region, String message) {
        super(message);
        this.region = region;
    }

    public ParseRegion getRegion() {
        return region;
    }
}

// End MdxParseException.java
