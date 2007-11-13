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

/**
 * Interface for a visitor to an MDX parse tree.
 *
 * @author jhyde
 * @version $Id$
 * @since Jul 21, 2006
 */
public interface ParseTreeVisitor<T> {
    /**
     * Visits a select statement.
     *
     * @param selectNode Node representing a SELECT statement
     * @see SelectNode#accept(ParseTreeVisitor)
     */
    T visit(SelectNode selectNode);

    /**
     * Visits an axis of a select statement.
     *
     * @see AxisNode#accept(ParseTreeVisitor)
     */
    T visit(AxisNode axis);

    /**
     * Visits a member declaration.
     *
     * @see WithMemberNode#accept(ParseTreeVisitor)
     */
    T visit(WithMemberNode calcMemberNode);

    /**
     * Visits a set declaration.
     *
     * @see WithSetNode#accept(ParseTreeVisitor)
     */
    T visit(WithSetNode calcSetNode);

    /**
     * Visits a call to an operator or function.
     *
     * @see CallNode#accept(ParseTreeVisitor)
     */
    T visit(CallNode call);

    /**
     * Visits an identifier.
     *
     * @see IdentifierNode#accept(ParseTreeVisitor)
     */
    T visit(IdentifierNode id);

    /**
     * Visits a parameter.
     *
     * @see ParameterNode#accept(ParseTreeVisitor)
     */
    T visit(ParameterNode parameterNode);

    /**
     * Visits a usage of a {@link org.olap4j.metadata.Cube} in a query.
     *
     * @see CubeNode#accept(ParseTreeVisitor)
     */
    T visit(CubeNode cubeNode);

    /**
     * Visits a usage of a {@link org.olap4j.metadata.Dimension} in a query.
     *
     * @see DimensionNode#accept(ParseTreeVisitor)
     */
    T visit(DimensionNode dimensionNode);

    /**
     * Visits a usage of a {@link org.olap4j.metadata.Hierarchy} in a query.
     *
     * @see HierarchyNode#accept(ParseTreeVisitor)
     */
    T visit(HierarchyNode hierarchyNode);

    /**
     * Visits a usage of a {@link org.olap4j.metadata.Level} in a query.
     *
     * @see LevelNode#accept(ParseTreeVisitor)
     */
    T visit(LevelNode levelExpr);

    /**
     * Visits a usage of a {@link org.olap4j.metadata.Member} in a query.
     *
     * @see MemberNode#accept(ParseTreeVisitor)
     */
    T visit(MemberNode memberNode);

    /**
     * Visits a literal.
     *
     * @see LiteralNode#accept(ParseTreeVisitor)
     */
    T visit(LiteralNode literalNode);

    /**
     * Visits a property-value pair.
     *
     * @see PropertyValueNode#accept(ParseTreeVisitor)
     */
    T visit(PropertyValueNode propertyValueNode);
}

// End ParseTreeVisitor.java
