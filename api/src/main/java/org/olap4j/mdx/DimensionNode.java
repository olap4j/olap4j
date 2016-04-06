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
package org.olap4j.mdx;

import org.olap4j.metadata.Dimension;
import org.olap4j.type.DimensionType;
import org.olap4j.type.Type;

/**
 * Usage of a {@link org.olap4j.metadata.Dimension} as an expression in an MDX
 * parse tree.
 *
 * @author jhyde
 * @since Jun 4, 2007
 */
public class DimensionNode implements ParseTreeNode {
    private final ParseRegion region;
    private final Dimension dimension;

    /**
     * Creates a DimensionNode.
     *
     * @param region Region of source code
     * @param dimension Dimension which is used in the expression
     */
    public DimensionNode(
        ParseRegion region,
        Dimension dimension)
    {
        this.region = region;
        this.dimension = dimension;
    }

    public ParseRegion getRegion() {
        return region;
    }

    /**
     * Returns the Dimension used in this expression.
     *
     * @return dimension used in this expression
     */
    public Dimension getDimension() {
        return dimension;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return new DimensionType(dimension);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(dimension.getUniqueName());
    }

    public String toString() {
        return dimension.getUniqueName();
    }

    public DimensionNode deepCopy() {
        // DimensionNode is immutable
        return this;
    }
}

// End DimensionNode.java
