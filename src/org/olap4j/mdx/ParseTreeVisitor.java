/*
// $Id: $
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
 * @version $Id: //open/mondrian/src/main/mondrian/mdx/ParseTreeVisitor.java#1 $
 * @since Jul 21, 2006
 */
public interface ParseTreeVisitor<T> {
    /**
     * Visits a {@link SelectNode}.
     *
     * @param selectNode Node representing a SELECT statement.
     * @return value returned by SelectNode#accept(ParseTreeVisitor)
     */
    T visit(SelectNode selectNode);

    /**
     * Visits an {@link AxisNode}.
     *
     * @see AxisNode#accept(ParseTreeVisitor)
     */
    T visit(AxisNode axis);

    /**
     * Visits a {@link WithMemberNode}.
     *
     * @see WithMemberNode#accept(ParseTreeVisitor)
     */
    T visit(WithMemberNode calcMemberNode);

    /**
     * Visits a {@link WithSetNode}.
     *
     * @see WithSetNode#accept(ParseTreeVisitor)
     */
    T visit(WithSetNode calcSetNode);

    /**
     * Visits a {@link CallNode}.
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
     * Visits a Parameter.
     *
     * @see ParameterNode#accept(ParseTreeVisitor)
     */
    T visit(ParameterNode parameterNode);

    /**
     * Visits an DimensionNode.
     *
     * @see DimensionNode#accept(ParseTreeVisitor)
     */
    T visit(DimensionNode dimensionNode);

    /**
     * Visits an HierarchyNode.
     *
     * @see HierarchyNode#accept(ParseTreeVisitor)
     */
    T visit(HierarchyNode hierarchyNode);

    /**
     * Visits an LevelNode.
     *
     * @see LevelNode#accept(ParseTreeVisitor)
     */
    T visit(LevelNode levelExpr);

    /**
     * Visits an MemberNode.
     *
     * @see MemberNode#accept(ParseTreeVisitor)
     */
    T visit(MemberNode memberNode);

    /**
     * Visits an MdxLiteral.
     *
     * @see LiteralNode#accept(ParseTreeVisitor)
     */
    T visit(LiteralNode literalNode);

    /**
     * Visits a {@link PropertyValueNode}.
     *
     * @see PropertyValueNode#accept(ParseTreeVisitor)
     */
    T visit(PropertyValueNode propertyValueNode);
}

// End ParseTreeVisitor.java
