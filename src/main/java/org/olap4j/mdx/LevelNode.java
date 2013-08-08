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

import org.olap4j.metadata.Level;
import org.olap4j.type.LevelType;
import org.olap4j.type.Type;

/**
 * Usage of a {@link org.olap4j.metadata.Level} as an expression in an MDX
 * parse tree.
 *
 * @author jhyde
 * @since Jun 4, 2007
 */
public class LevelNode implements ParseTreeNode {
    private final ParseRegion region;
    private final Level level;

    /**
     * Creates a LevelNode.
     *
     * @param region Region of source code
     * @param level Level which is used in the expression
     */
    public LevelNode(
        ParseRegion region,
        Level level)
    {
        this.region = region;
        this.level = level;
    }

    public ParseRegion getRegion() {
        return region;
    }

    /**
     * Returns the Level used in this expression.
     *
     * @return level used in this expression
     */
    public Level getLevel() {
        return level;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return new LevelType(
            level.getDimension(),
            level.getHierarchy(),
            level);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(level.getUniqueName());
    }

    public String toString() {
        return level.getUniqueName();
    }

    public LevelNode deepCopy() {
        // LevelNode is immutable
        return this;
    }

}

// End LevelNode.java
