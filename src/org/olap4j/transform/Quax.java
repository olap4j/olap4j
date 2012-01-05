/*
// $Id$
//
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
