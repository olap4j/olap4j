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

import org.olap4j.OlapException;
import org.olap4j.mdx.SelectNode;

/**
 * Validator for the MDX query language.
 *
 * <p>A validator is reusable but not reentrant: you can call
 * {@link #validateSelect(org.olap4j.mdx.SelectNode)} several times, but not at
 * the same time from different threads.
 *
 * <p>To create a validator, use the
 * {@link MdxParserFactory#createMdxValidator(org.olap4j.OlapConnection)}
 * method.
 *
 * @see MdxParserFactory
 * @see MdxParser
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface MdxValidator {
    /**
     * Validates an MDX SELECT statement.
     *
     * <p>The SelectNode representing the SELECT statement may have been
     * created by an {@link MdxParser}, or it may have been built
     * programmatically.
     *
     * <p>If the parse tree is invalid, throws an {@link OlapException}.
     *
     * <p>If it is valid, returns a parse tree. This parse tree may or may not
     * be the same parse tree passed as an argument. After validation, you can
     * ascertain the type of each node of the parse tree by calling its
     * {@link org.olap4j.mdx.ParseTreeNode#getType()} method.
     *
     * @param selectNode Parse tree node representing a SELECT statement
     *
     * @return Validated parse tree
     *
     * @throws OlapException if node is invalid
     */
    SelectNode validateSelect(SelectNode selectNode) throws OlapException;
}

// End MdxValidator.java
