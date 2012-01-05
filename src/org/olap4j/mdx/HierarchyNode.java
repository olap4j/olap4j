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
package org.olap4j.mdx;

import org.olap4j.metadata.Hierarchy;
import org.olap4j.type.HierarchyType;
import org.olap4j.type.Type;

/**
 * Usage of a {@link org.olap4j.metadata.Hierarchy} as an expression in an MDX
 * parse tree.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
public class HierarchyNode implements ParseTreeNode {
    private final ParseRegion region;
    private final Hierarchy hierarchy;

    /**
     * Creates a HierarchyNode.
     *
     * @param region Region of source code
     * @param hierarchy Hierarchy which is used in the expression
     */
    public HierarchyNode(
        ParseRegion region,
        Hierarchy hierarchy)
    {
        this.region = region;
        this.hierarchy = hierarchy;
    }

    public ParseRegion getRegion() {
        return region;
    }

    /**
     * Returns the Hierarchy used in this expression.
     *
     * @return hierarchy used in this expression
     */
    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return new HierarchyType(
            hierarchy.getDimension(),
            hierarchy);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(hierarchy.getUniqueName());
    }

    public String toString() {
        return hierarchy.getUniqueName();
    }

    public HierarchyNode deepCopy() {
        // HierarchyNode is immutable
        return this;
    }
}

// End HierarchyNode.java
