/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import org.olap4j.mdx.ParseTreeNode;

/**
 * Metadata object describing a named set defined against a {@link Cube}.
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 24, 2006
 */
public interface NamedSet extends MetadataElement {
    /**
     * Returns the <code>Cube</code> that this <code>NamedSet</code> belongs
     * to.
     *
     * @return cube this named set belongs to
     */
    Cube getCube();

    /**
     * Returns the expression which gives the value of this NamedSet.
     *
     * @return expression
     */
    ParseTreeNode getExpression();
}

// End NamedSet.java
