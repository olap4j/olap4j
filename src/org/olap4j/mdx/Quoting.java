/*
// $Id: IdentifierNode.java 359 2010-10-14 21:24:51Z jhyde $
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

/**
 * Enumeration of styles by which the component of an identifier can be
 * quoted.
 *
 * @see org.olap4j.mdx.IdentifierSegment
 *
 * @version $Id: IdentifierNode.java 359 2010-10-14 21:24:51Z jhyde $
 * @author jhyde
 */
public enum Quoting {

    /**
     * Unquoted identifier, for example "Measures".
     */
    UNQUOTED,

    /**
     * Quoted identifier, for example "[Measures]".
     */
    QUOTED,

    /**
     * Identifier quoted with an ampersand and brackets to indicate a key
     * value, for example the second segment in "[Employees].&[89]".
     *
     * <p>Such a segment has one or more sub-segments. Each segment is
     * either quoted or unquoted. For example, the second segment in
     * "[Employees].&[89]&[San Francisco]&CA&USA" has four sub-segments,
     * two quoted and two unquoted.
     */
    KEY,
}

// End Quoting.java
