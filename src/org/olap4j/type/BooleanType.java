/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.type;

/**
 * The type of a boolean (logical) expression.
 *
 * <p>An example of a boolean expression is the predicate
 *
 * <blockquote>
 * <code>[Measures].[Unit Sales] &gt; 1000</code>
 * </blockquote>
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public class BooleanType extends ScalarType {
    /**
     * Creates a boolean type.
     */
    public BooleanType() {
    }

    public String toString() {
        return "BOOLEAN";
    }
}

// End BooleanType.java
