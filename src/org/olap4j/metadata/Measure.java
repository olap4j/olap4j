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
 * Data value of primary interest to the user browsing the cube.
 *
 * <p>A <code>Measure</code> provides the value of each cell, and is usually
 * numeric. Every measure is a member of a special dimension called "Measures".
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 13, 2006
 */
public interface Measure extends Member {
}

// End Measure.java
