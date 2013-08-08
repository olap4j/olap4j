/*
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

import org.olap4j.Axis;
import org.olap4j.mdx.*;

/**
 * Abstract representation of an MDX query transform acting on
 * a single query axis (e.g. drill-down on member, roll-up, ...)
 *
 * @author etdub
 * @since Aug 7, 2008
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
