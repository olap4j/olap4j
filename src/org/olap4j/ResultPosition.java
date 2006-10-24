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
 * Position on one of the {@link ResultAxis} objects in a {@link OlapResultSet}.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface ResultPosition {
    /**
     * Returns the list of Member objects at this position.
     *
     * <p>Recall that the {@link OlapResultSetAxisMetaData#getHierarchies()}
     * method describes the hierarchies which occur on an axis. The positions on
     * that axis must conform. Suppose that the ROWS axis of a given statement
     * returns <code>{[Gender], [Store]}</code>. Then every ResultPosition on
     * that axis will have two members: the first a member of the [Gender]
     * dimension, the second a member of the [Store] dimension.</p>
     *
     * @return A list of Member objects at this ResultPosition.
     */
    public List<Member> getMembers();

    /**
     * Returns the zero-based ordinal of this ResultPosition on its
     * {@link ResultAxis}.
     */
    int getOrdinal();
}

// End ResultPosition.java
