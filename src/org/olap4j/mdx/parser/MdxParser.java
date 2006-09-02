/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx.parser;

import mondrian.olap.Exp;
import mondrian.olap.Query;

/**
 * Parser for the MDX query language.
 *
 * @see MdxParserFactory
 * @see org.olap4j.mdx.parser.impl.DefaultMdxParserImpl
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface MdxParser {
    /**
     * Parses an MDX Select statement.
     *
     * {@link org.olap4j.Todo} change return type
     */
    Query parseSelect(String mdx);

    /**
     * Parses an expression.
     *
     * {@link org.olap4j.Todo} change return type
     */
    Exp parseExpression(String mdx);
}

// End MdxParser.java
