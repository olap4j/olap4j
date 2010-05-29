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

/**
 * The type of a null expression.
 *
 * @author medstat
 * @version $Id$
 * @since Aug 21, 2006
 */
public class NullType extends ScalarType
{
    /**
     * Creates a null type.
     */
    public NullType() {
    }

    public String toString() {
        return "NULL";
    }
}
// End NullType.java
