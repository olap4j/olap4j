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

import org.olap4j.impl.Olap4jUtil;
import org.olap4j.type.*;

import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * Represents a constant value, such as a string or number, in a parse tree.
 *
 * <p>Symbols, such as the <code>ASC</code> keyword in
 * <code>Order([Store].Members, [Measures].[Unit Sales], ASC)</code>, are
 * also represented as Literals.
 *
 * <p>A LiteralNode is immutable.
 *
 * @version $Id$
 * @author jhyde
 */
public class LiteralNode implements ParseTreeNode {

    // Data members.

    private final Object value;
    private final Type type;
    private final ParseRegion region;

    /**
     * Private constructor.
     *
     * <p>Use the creation methods {@link #createString} etc.
     *
     * @param region Region of source code
     * @param type Type of this literal; must not be null
     * @param value Value of this literal, must be null only if this is the
     *   null literal
     */
    private LiteralNode(
        ParseRegion region,
        Type type,
        Object value)
    {
        assert type != null;
        assert (type instanceof NullType) == (value == null);
        assert (type instanceof StringType || type instanceof SymbolType)
               == (value instanceof String);
        assert (type instanceof NumericType) == (value instanceof BigDecimal);
        this.region = region;
        this.type = type;
        this.value = value;
    }

    /**
     * Creates a literal with the NULL value.
     *
     * @param region Region of source code
     * @return literal representing the NULL value
     */
    public static LiteralNode createNull(ParseRegion region) {
        return new LiteralNode(region, new NullType(), null);
    }

    /**
     * Creates a string literal.
     *
     * @param region Region of source code
     * @param value String value
     *
     * @return literal representing the string value
     *
     * @see #createSymbol
     */
    public static LiteralNode createString(
        ParseRegion region,
        String value)
    {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        return new LiteralNode(region, new StringType(), value);
    }

    /**
     * Creates a symbol literal.
     *
     * @param region Region of source code
     * @param value Name of symbol
     *
     * @return literal representing the symbol value
     *
     * @see #createString
     */
    public static LiteralNode createSymbol(
        ParseRegion region,
        String value)
    {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        return new LiteralNode(region, new SymbolType(), value);
    }

    /**
     * Creates a numeric literal.
     *
     * @param region Region of source code
     * @param value Value of literal; must not be null
     * @param approximate Whether the literal is approximate
     *
     * @return literal representing the integer value
     */
    public static LiteralNode createNumeric(
        ParseRegion region,
        BigDecimal value,
        boolean approximate)
    {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        Olap4jUtil.discard(approximate); // reserved for future use
        return new LiteralNode(region, new NumericType(), value);
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return type;
    }

    public ParseRegion getRegion() {
        return region;
    }

    /**
     * Returns the value of this literal.
     *
     * <p>Value is always of type {@link String} (if the literal is a string or
     * a symbol), of type {@link java.math.BigDecimal} (if the literal is
     * numeric), or null (if the literal is of null type).
     *
     * @return value
     */
    public Object getValue() {
        return value;
    }

    public void unparse(ParseTreeWriter writer) {
        PrintWriter pw = writer.getPrintWriter();
        if (value == null) {
            pw.print("NULL");
        } else if (type instanceof SymbolType) {
            pw.print(value);
        } else if (type instanceof NumericType) {
            pw.print(value);
        } else if (type instanceof StringType) {
            pw.print(MdxUtil.quoteForMdx((String) value));
        } else {
            throw new AssertionError("unexpected literal type " + type);
        }
    }

    public LiteralNode deepCopy() {
        // No need to copy: literal nodes are immutable.
        return this;
    }

}

// End LiteralNode.java
