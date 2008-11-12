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

import org.olap4j.Axis;
import org.olap4j.mdx.AxisNode;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.SelectNode;

/**
 * Abstract representation of an MDX query transform acting on
 * a single query axis (e.g. drill-down on member, roll-up, ...)
 *
 * @author etdub
 * @version $Id$
 * @since Aug 7, 2008
 *
 */
public abstract class AxisTransform implements MdxQueryTransform {

    protected final Axis axis;

    protected AxisTransform(Axis axis) {
        this.axis = axis;
    }

    public SelectNode apply(SelectNode sn) {
        // do a deep copy of the existing query SelectNode before
        // modifying it:
        SelectNode newSelectNode = sn.deepCopy();

        for (AxisNode an : newSelectNode.getAxisList()) {
            if (an.getAxis() == axis) {
                // this is the axis we're drilling

                ParseTreeNode initialAxisExp = an.getExpression();

                // apply the drill operation
                ParseTreeNode newAxisExp =
                    processAxisExp(initialAxisExp);

                // replace the expression in the axis by the new generated one
                an.setExpression(newAxisExp);
            }
        }
        return newSelectNode;
    }

    protected abstract ParseTreeNode processAxisExp(ParseTreeNode axisExp);

}

// End AxisTransform.java
