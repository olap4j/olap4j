/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2005-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.type;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;

/**
 * The type of a numeric expression.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public class NumericType extends ScalarType {

    /**
     * Creates a numeric type.
     */
    public NumericType() {
    }

    public String toString() {
        return "NUMERIC";
    }
}

// End NumericType.java
