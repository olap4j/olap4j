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
 * <p>Together with the
 * {@link org.olap4j.mdx.ParseTreeNode#accept(ParseTreeVisitor)} method, an
 * class implementing this interface implements a visitor pattern, to allow
 * an algorithm to efficiently traverse a parse tree and perform an action at
 * each node dependent upon the type of each node.
 *  
 * @author jhyde
 * @version $Id$
 * @since Jul 21, 2006
 */
public interface ParseTreeVisitor<T> {
    /**
     * Visits a select statement.
     *
     * @param selectNode Node representing a select statement
     *
     * @return value yielded by visiting the node
     *
     * @see SelectNode#accept(ParseTreeVisitor)
     */
    T visit(SelectNode selectNode);

    /**
     * Visits an axis of a select statement.
     *
     * @param axis Node representing an axis
     *
     * @return value yielded by visiting the node
     *
     * @see AxisNode#accept(ParseTreeVisitor)
     */
    T visit(AxisNode axis);

    /**
     * Visits a member declaration.
     *
     * @param calcMemberNode Node representing a member declaration
     *
     * @return value yielded by visiting the node
     *
     * @see WithMemberNode#accept(ParseTreeVisitor)
     */
    T visit(WithMemberNode calcMemberNode);

    /**
     * Visits a set declaration.
     *
     * @param calcSetNode Node representing a set declaration
     *
     * @return value yielded by visiting the node
     *
     * @see WithSetNode#accept(ParseTreeVisitor)
     */
    T visit(WithSetNode calcSetNode);

    /**
     * Visits a call to an operator or function.
     *
     * @param call Node representing a call to an operator or function
     *
     * @see CallNode#accept(ParseTreeVisitor)
     *
     * @return value yielded by visiting the node
     */
    T visit(CallNode call);

    /**
     * Visits an identifier.
     *
     * @param id Node representing an identifier
     *
     * @return value yielded by visiting the node
     *
     * @see IdentifierNode#accept(ParseTreeVisitor)
     */
    T visit(IdentifierNode id);

    /**
     * Visits a parameter.
     *
     * @param parameterNode Node representing use of a parameter
     *
     * @return value yielded by visiting the node
     *
     * @see ParameterNode#accept(ParseTreeVisitor)
     */
    T visit(ParameterNode parameterNode);

    /**
     * Visits a use of a {@link org.olap4j.metadata.Cube}
     * in a select statement.
     *
     * @param cubeNode Node representing a use of a Cube
     *
     * @return value yielded by visiting the node
     *
     * @see CubeNode#accept(ParseTreeVisitor)
     */
    T visit(CubeNode cubeNode);

    /**
     * Visits a use of a {@link org.olap4j.metadata.Dimension}
     * in a select statement.
     *
     * @param dimensionNode Node representing a use of a Dimension
     *
     * @return value yielded by visiting the node
     *
     * @see DimensionNode#accept(ParseTreeVisitor)
     */
    T visit(DimensionNode dimensionNode);

    /**
     * Visits a use of a {@link org.olap4j.metadata.Hierarchy}
     * in a select statement.
     *
     * @param hierarchyNode Node representing a use of a Hierarchy
     *
     * @return value yielded by visiting the node
     *
     * @see HierarchyNode#accept(ParseTreeVisitor)
     */
    T visit(HierarchyNode hierarchyNode);

    /**
     * Visits a use of a {@link org.olap4j.metadata.Level}
     * in a select statement.
     *
     * @param levelNode Node representing a use of a Level
     *
     * @return value yielded by visiting the node
     *
     * @see LevelNode#accept(ParseTreeVisitor)
     */
    T visit(LevelNode levelNode);

    /**
     * Visits a use of a {@link org.olap4j.metadata.Member}
     * in a select statement.
     *
     * @param memberNode Node representing a use of a Member
     *
     * @return value yielded by visiting the node
     *
     * @see MemberNode#accept(ParseTreeVisitor)
     */
    T visit(MemberNode memberNode);

    /**
     * Visits a literal.
     *
     * @param literalNode Node representing a Literal
     *
     * @return value yielded by visiting the node
     *
     * @see LiteralNode#accept(ParseTreeVisitor)
     */
    T visit(LiteralNode literalNode);

    /**
     * Visits a property-value pair.
     *
     * @param propertyValueNode Node representing a property-value pair
     *
     * @return value yielded by visiting the node
     *
     * @see PropertyValueNode#accept(ParseTreeVisitor)
     */
    T visit(PropertyValueNode propertyValueNode);
}

// End ParseTreeVisitor.java
