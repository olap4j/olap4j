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

import org.olap4j.type.Type;

/**
 * Parse tree node representing a property-value pair.
 *
 * <p>Property-value pairs are used to define properties of calculated members.
 * For example, in
 *
 * <blockquote>
 * <code>WITH MEMBER [Measures].[Foo] AS ' [Measures].[Unit Sales] ',<br>
 * &nbsp;&nbsp;FORMAT_STRING = 'Bold',<br>
 * &nbsp;&nbsp;SOLVE_ORDER = 2<br>
 * SELECT ...</code>
 * </blockquote>
 *
 * there are two property-value pairs FORMAT_STRING and SOLVE_ORDER.
 *
 * @author jhyde
 */
public class PropertyValueNode implements ParseTreeNode {

    private final ParseRegion region;
    private final String name;
    private ParseTreeNode expression;

    /**
     * Creates a PropertyValueNode.
     *
     * @param region Region of source code
     * @param name Name of property
     * @param expression Expression for value of property (often a literal)
     */
    public PropertyValueNode(
        ParseRegion region,
        String name,
        ParseTreeNode expression)
    {
        this.region = region;
        this.name = name;
        this.expression = expression;
    }

    public ParseRegion getRegion() {
        return region;
    }

    public Type getType() {
        return expression.getType();
    }

    /**
     * Returns the expression by which the value of the property is derived.
     *
     * @return the expression by which the value of the property is derived
     */
    public ParseTreeNode getExpression() {
        return expression;
    }

    /**
     * Returns the name of the property
     *
     * @return name of the property
     */
    public String getName() {
        return name;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void unparse(ParseTreeWriter writer) {
        writer.getPrintWriter().print(name + " = ");
        expression.unparse(writer);
    }

    public PropertyValueNode deepCopy() {
        return new PropertyValueNode(
            this.region,
            this.name,
            this.expression.deepCopy());
    }
}

// End PropertyValueNode.java
