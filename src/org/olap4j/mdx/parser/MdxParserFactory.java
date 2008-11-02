/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx.parser;

import org.olap4j.OlapConnection;

/**
 * Factory for MDX parsers.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface MdxParserFactory {
    /**
     * Creates an MDX parser.
     *
     * @param connection Connection in which to resolve identifiers
     * @return MDX parser
     */
    MdxParser createMdxParser(OlapConnection connection);

    /**
     * Creates an MDX validator.
     *
     * @param connection Connection in which to resolve identifiers
     * @return MDX validator
     */
    MdxValidator createMdxValidator(OlapConnection connection);
}

// End MdxParserFactory.java
