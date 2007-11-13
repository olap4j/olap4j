/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2005 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.type;

/**
 * The type of a symbolic expression.
 *
 * <p>Symbols are identifiers which occur in particular function calls,
 * generally to indicate an option for how the function should be executed.
 * They are similar to an enumerated type in other
 * languages.
 *
 * <p>For example, the optional 3rd argument to the <code>Order</code> function
 * can be one of the symbols <code>ASC</code>, <code>DESC</code>,
 * <code>BASC</code>, <code>BDESC</code>. The signature of the
 * <code>Order</code> function is therefore
 *
 * <blockquote>
 * <code>Order(&lt;Set&gt;, &lt;Scalar expression&gt; [, &lt;Symbol&gt;])</code>
 * </blockquote>
 *
 * and
 *
 * <blockquote>
 * <code>Order([Store].Members, [Measures].[Unit Sales], BDESC)</code>
 * </blockquote>
 *
 * would be a valid call to the function.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public class SymbolType extends ScalarType {

    /**
     * Creates a symbol type.
     */
    public SymbolType() {
    }

}

// End SymbolType.java
