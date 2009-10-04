/*
// $Id: Cell.java 229 2009-05-08 19:11:29Z jhyde $
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2009-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

/**
 * Enumeration of the policies that can be used to modify the values of
 * child cells when their parent cell is modified in a writeback operation.
 *
 * @see Cell#setValue
 *
 * @author jhyde
 * @version $Id: Cell.java 229 2009-05-08 19:11:29Z jhyde $
 * @since Aug 22, 2006
 */
public enum AllocationPolicy {
    /**
     * Every atomic cell that contributes to the updated cell will be
     * assigned an equal value that is:
     *
     * <blockquote>
     * &lt;atomic cell value&gt; =
     * &lt;value&gt; / Count(atomic cells contained in &lt;tuple&gt;)
     * </blockquote>
     */
    EQUAL_ALLOCATION,

    /**
     * Every atomic cell that contributes to the updated cell will be
     * changed according to:
     *
     * <blockquote>
     * &lt;atomic cell value&gt; = &lt;atomic cell value&gt; +
     * (&lt;value&gt; - &lt;existing value&gt;)  /
     * Count(atomic cells contained in &lt;tuple&gt;)
     * </blockquote>
     */
    EQUAL_INCREMENT,

    /**
     * Every atomic cell that contributes to the updated cell will be
     * assigned an equal value that is:
     *
     * <blockquote>
     * &lt;atomic cell value&gt; =
     * &lt;value&gt; * &lt;weight value expression&gt;
     * </blockquote>
     *
     * <p>Takes an optional argument, {@code weight_value_expression}.
     * If {@code weight_value_expression} is not provided, the following
     * expression is assigned to it by default:
     *
     * <blockquote>
     * &lt;weight value expression&gt; =
     * &lt;atomic cell value&gt; / &lt;existing value&gt;
     * <blockquote>
     *
     * <p>The value of {@code weight value expression} should be expressed
     * as a value between 0 and 1. This value specifies the ratio of the
     * allocated value you want to assign to the atomic cells that are
     * affected by the allocation. It is the client application programmer's
     * responsibilffity to create expressions whose rollup aggregate values
     * will equal the allocated value of the expression.
     */
    WEIGHTED_ALLOCATION,

    /**
     * Every atomic cell that contributes to the updated cell will be
     * changed according to:
     *
     * <blockquote>
     * &lt;atomic cell value&gt; = &lt;atomic cell value&gt; +
     * (&lt;value&gt; - &lt;existing value&gt;)  *
     * &lt;weight value expression&gt;
     * </blockquote>
     *
     * <p>Takes an optional argument, {@code weight_value_expression}.
     * If {@code weight_value_expression} is not provided, the following
     * expression is assigned to it by default:
     *
     * <blockquote>
     * &lt;weight value expression&gt; =
     * &lt;atomic cell value&gt; / &lt;existing value&gt;
     * <blockquote>
     *
     * <p>The value of {@code weight value expression} should be expressed
     * as a value between 0 and 1. This value specifies the ratio of the
     * allocated value you want to assign to the atomic cells that are
     * affected by the allocation. It is the client application programmer's
     * responsibility to create expressions whose rollup aggregate values
     * will equal the allocated value of the expression.
     */
    WEIGHTED_INCREMENT,
}

// End AllocationPolicy.java
