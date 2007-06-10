/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

/**
 * Metadata object describing a named set defined against a {@link Cube}.
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 24, 2006
 */
public interface NamedSet extends MetadataElement {
    /**
     * Returns the <code>Cube</code> that this <code>NamedSet</code> belongs to.
     */
    Cube getCube();
}

// End NamedSet.java
