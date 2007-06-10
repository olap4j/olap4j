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

    // Constants for commonly used literals.

    public static final LiteralNode NULL_VALUE =
        new LiteralNode(new NullType(), null);

    public static final LiteralNode EMPTY_STRING =
        new LiteralNode(new StringType(), "");

    public static final LiteralNode INTEGER_ZERO =
        new LiteralNode(new NumericType(), 0);

    public static final LiteralNode INTEGER_ONE =
        new LiteralNode(new NumericType(), 1);

    public static final LiteralNode INTEGER_NEGATIVE_ONE =
        new LiteralNode(new NumericType(), -1);

    public static final LiteralNode DOUBLE_ZERO =
        new LiteralNode(new NumericType(), 0.0);

    public static final LiteralNode DOUBLE_ONE =
        new LiteralNode(new NumericType(), 1.0);

    public static final LiteralNode DOUBLE_NEGATIVE_ONE =
        new LiteralNode(new NumericType(), -1.0);

    /**
     * Private constructor.
     *
     * <p>Use the creation methods {@link #createString(String)} etc.
     *
     * @param type Type of this literal; must not be null
     * @param value Value of this literal, must be null only if this is the
     *   null literal
     */
    private LiteralNode(Type type, Object value) {
        assert type != null;
        assert (type instanceof NullType) == (value == null);
        this.type = type;
        this.value = value;
    }

    /**
     * Creates a string literal.
     *
     * @see #createSymbol
     */
    public static LiteralNode createString(String value) {
        if (value.equals("")) {
            return EMPTY_STRING;
        } else {
            return new LiteralNode(new StringType(), value);
        }
    }

    /**
     * Creates a symbol literal.
     *
     * @see #createString
     */
    public static LiteralNode createSymbol(String value) {
        return new LiteralNode(new SymbolType(), value);
    }

    /**
     * Creates a floating-point numeric literal.
     *
     * @param value Value of literal; must not be null
     */
    public static LiteralNode create(Double value) {
        assert value != null;
        double dv = value; // unbox, so we compare by value not reference
        if (dv == 0.0) {
            return DOUBLE_ZERO;
        } else if (dv == 1.0) {
            return DOUBLE_ONE;
        } else if (dv == -1.0) {
            return DOUBLE_NEGATIVE_ONE;
        } else {
            return new LiteralNode(new NumericType(), value);
        }
    }

    /**
     * Creates an integer literal.
     *
     * @param value Value of literal; must not be null
     */
    public static LiteralNode create(Integer value) {
        assert value != null;
        switch (value) {
        case -1:
            return INTEGER_NEGATIVE_ONE;
        case 0:
            return INTEGER_ZERO;
        case 1:
            return INTEGER_ONE;
        default:
            return new LiteralNode(new NumericType(), value);
        }
    }


    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        return type;
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
