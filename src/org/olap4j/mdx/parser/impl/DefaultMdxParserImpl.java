/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx.parser.impl;

import org.olap4j.mdx.parser.MdxParser;
import mondrian.olap.Query;
import mondrian.olap.Exp;

/**
 * Default implementation of {@link org.olap4j.mdx.parser.MdxParser MDX Parser}.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public class DefaultMdxParserImpl implements MdxParser {
    public Query parseSelect(String mdx) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public Exp parseExpression(String mdx) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }
}

// End DefaultMdxParserImpl.java
