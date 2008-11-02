/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2008-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.transform;

import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

/**
 * Representation of member expressions on a query axis, derived from
 * CellSetAxis objects.
 *
 * <p>Quaxes are used by MDX axis query transforms, to construct and use
 * an internal tree-like representation of positions and members from the
 * result CellSetAxis objects of a previous MDX query. This is needed
 * for OLAP navigation operators like drill-down on a position.
 *
 * <p>Inspired from the JPivot Quax class.
 *
 * <p>NOTE: not exactly sure how to implement this, to be completed...
 *
 * @author etdub
 * @version $Id$
 * @since Aug 7, 2008
 */
public class Quax {

    private final CellSetAxis cellSetAxis;

    private TreeNode<Member> memberTree;

    public Quax(CellSetAxis cellSetAxis) {
        this.cellSetAxis = cellSetAxis;

        for (Position p : cellSetAxis.getPositions()) {
            p.getMembers();
        }

    }

}

// End Quax.java
