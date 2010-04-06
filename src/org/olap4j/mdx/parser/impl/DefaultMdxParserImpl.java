/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx.parser.impl;

import org.olap4j.mdx.parser.MdxParser;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.OlapConnection;

/**
 * Default implementation of {@link org.olap4j.mdx.parser.MdxParser MDX Parser}.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public class DefaultMdxParserImpl implements MdxParser {
    private boolean debug = false;
    private boolean load = false;
    private final FunTable funTable = new FunTable() {
        public boolean isProperty(String s) {
            return s.equals("CHILDREN");
        }
    };

    @Deprecated
    public DefaultMdxParserImpl(OlapConnection olapConnection) {
        super();
    }

    public DefaultMdxParserImpl() {
        super();
    }

    public SelectNode parseSelect(String mdx) {
        return new DefaultMdxParser().parseSelect(
            mdx,
            debug,
            funTable,
            load);
    }

    public ParseTreeNode parseExpression(String mdx) {
        return new DefaultMdxParser().parseExpression(
            mdx,
            debug,
            funTable);
    }

    interface FunTable {
        boolean isProperty(String s);
    }
}

// End DefaultMdxParserImpl.java
