/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import org.olap4j.type.*;

import java.io.PrintWriter;

/**
 * Represents a constant value, such as a string or number, in a parse tree.
 *
 * <p>Symbols, such as the <code>ASC</code> keyword in
 * <code>Order([Store].Members, [Measures].[Unit Sales], ASC)</code>, are
 * also represented as Literals.
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
     * @param region Region
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
        this.region = region;
        this.type = type;
        this.value = value;
    }

    /**
     * Creates a literal with the NULL value.
     *
     * @param region Region
     * @return literal representing the NULL value
     */
    public static LiteralNode createNull(ParseRegion region) {
        return new LiteralNode(region, new NullType(), null);
    }

    /**
     * Creates a string literal.
     *
     * @see #createSymbol
     */
    public static LiteralNode createString(
        ParseRegion region,
        String value)
    {
        return new LiteralNode(region, new StringType(), value);
    }

    /**
     * Creates a symbol literal.
     *
     * @see #createString
     */
    public static LiteralNode createSymbol(
        ParseRegion region,
        String value)
    {
        return new LiteralNode(region, new SymbolType(), value);
    }

    /**
     * Creates a floating-point numeric literal.
     *
     * @param value Value of literal; must not be null
     */
    public static LiteralNode create(
        ParseRegion region,
        Double value)
    {
        assert value != null;
        return new LiteralNode(region, new NumericType(), value);
    }

    /**
     * Creates an integer literal.
     *
     * @param value Value of literal; must not be null
     */
    public static LiteralNode create(
        ParseRegion region,
        Integer value)
    {
        assert value != null;
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
}

// End LiteralNode.java
