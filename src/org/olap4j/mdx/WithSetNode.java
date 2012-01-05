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

import org.olap4j.type.Type;

import java.io.PrintWriter;

/**
 * Parse tree node which declares a calculated set. Represented as the
 * <code>WITH SET</code> clause of an MDX <code>SELECT</code> statement.
 *
 * @version $Id$
 * @author jhyde
 */
public class WithSetNode implements ParseTreeNode {

    private final ParseRegion region;
    /** name of set */
    private final IdentifierNode name;

    /** defining expression */
    private ParseTreeNode expression;

    /**
     * Creates a declaration of a named set.
     *
     * @param region Region of source code
     * @param name Name of set
     * @param expression Expression to calculate set
     */
    public WithSetNode(
        ParseRegion region,
        IdentifierNode name,
        ParseTreeNode expression)
    {
        this.region = region;
        this.name = name;
        this.expression = expression;
    }

    public ParseRegion getRegion() {
        return region;
    }

    public void unparse(ParseTreeWriter writer) {
        PrintWriter pw = writer.getPrintWriter();
        pw.print("SET ");
        name.unparse(writer);
        writer.indent();
        pw.println(" AS");
        expression.unparse(writer);
        writer.outdent();
    }

    /**
     * Returns the name of the set.
     *
     * @return name of the set
     */
    public IdentifierNode getIdentifier() {
        return name;
    }

    /**
     * Returns the expression which calculates the set.
     *
     * @return expression which calculates the set
     */
    public ParseTreeNode getExpression() {
        return expression;
    }

    /**
     * Sets the expression which calculates the set.
     *
     * @param expression expression which calculates the set
     */
    public void setExpression(ParseTreeNode expression) {
        this.expression = expression;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        final T t = visitor.visit(this);
        name.accept(visitor);
        expression.accept(visitor);
        return t;
    }

    public Type getType() {
        // not an expression
        throw new UnsupportedOperationException();
    }

    public WithSetNode deepCopy() {
        return new WithSetNode(
            this.region,
            this.name.deepCopy(),
            this.expression.deepCopy());
    }
}

// End WithSetNode.java
