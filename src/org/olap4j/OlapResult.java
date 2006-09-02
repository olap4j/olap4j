/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import java.util.List;

/**
 * Result of executing an OLAP Statement.
 *
 * <p>It consists of a set of (typically two) axes, each populated with a
 * sequence of members, and a collection of cells at the intersection of these
 * axes.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface OlapResult {
    List<ResultAxis> getAxes();

    ResultCell getCell(int[] coordinates);
}

// End OlapResult.java
