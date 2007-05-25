/*
// $Id: Position.java 15 2006-10-24 08:41:02Z jhyde $
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
 * Position on one of the {@link CellSetAxis} objects in a {@link CellSet}.
 *
 * @author jhyde
 * @version $Id: Position.java 15 2006-10-24 08:41:02Z jhyde $
 * @since Aug 22, 2006
 */
public interface Position {
    /**
     * Returns the list of Member objects at this position.
     *
     * <p>Recall that the {@link CellSetAxisMetaData#getHierarchies()}
     * method describes the hierarchies which occur on an axis. The positions on
     * that axis must conform. Suppose that the ROWS axis of a given statement
     * returns <code>{[Gender], [Store]}</code>. Then every Position on
     * that axis will have two members: the first a member of the [Gender]
     * dimension, the second a member of the [Store] dimension.</p>
     *
     * @return A list of Member objects at this Position.
     */
    public List<Member> getMembers();

    /**
     * Returns the zero-based ordinal of this Position on its
     * {@link CellSetAxis}.
     */
    int getOrdinal();
}

// End Position.java
