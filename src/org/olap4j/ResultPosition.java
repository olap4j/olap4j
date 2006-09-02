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

import org.olap4j.metadata.Member;

import java.util.List;

/**
 * <code>ResultPosition</code> ...
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface ResultPosition {
    public List<Member> getMembers();

    /**
     * Returns the ordinal of this position on its {@link ResultAxis}.
     * Zero-based.
     */
    int getOrdinal();
}

// End ResultPosition.java
