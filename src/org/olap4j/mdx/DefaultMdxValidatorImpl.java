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

import org.olap4j.OlapException;
import org.olap4j.mdx.parser.MdxValidator;
import org.olap4j.type.Type;
import org.olap4j.type.TypeUtil;

import java.util.List;
import java.util.Stack;

/**
 * Visitor which passes over a tree of MDX nodes, checks that they are valid,
 * and assigns a type to each.
 *
 * <p>NOTE: This class is experimental. Not part of the public olap4j API.
 *
 * @author jhyde
 * @version $Id$
 * @since Jun 4, 2007
 */
class DefaultMdxValidatorImpl
    implements ParseTreeVisitor<ParseTreeNode>, MdxValidator
{
    private Stack<Boolean> scalarStack = new Stack<Boolean>();
    private final SelectNode selectNode;

    /**
     * Creates a DefaultMdxValidatorImpl.
     *
     * @param selectNode Root of parse tree
     */
    protected DefaultMdxValidatorImpl(SelectNode selectNode) {
        this.selectNode = selectNode;
    }

    public SelectNode validateSelect(SelectNode selectNode)
        throws OlapException
    {
        return null;
    }

    public ParseTreeNode visit(SelectNode selectNode) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(AxisNode axis) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(WithMemberNode withMemberNode) {
        ParseTreeNode expression = acceptScalar(withMemberNode.getExpression());
        withMemberNode.setExpression(expression);
        final Type type = expression.getType();
        if (!TypeUtil.canEvaluate(type)) {
            throw new RuntimeException(
                "'Member expression '" + MdxUtil.toString(expression)
                + "' must not be a set'");
        }
        for (PropertyValueNode prop : withMemberNode.getMemberPropertyList()) {
            prop.accept(this);
        }
        return withMemberNode;
    }

    public ParseTreeNode visit(WithSetNode withSetNode) {
        ParseTreeNode expression = acceptScalar(withSetNode.getExpression());
        withSetNode.setExpression(expression);
        final Type type = expression.getType();
        if (!TypeUtil.isSet(type)) {
            final IdentifierNode id = withSetNode.getIdentifier();
            final String idString = MdxUtil.toString(id);
            throw new RuntimeException(
                "Set expression '" + idString + "' must be a set");
        }
        return withSetNode;
    }

    public ParseTreeNode visit(CallNode call) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(IdentifierNode id) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(ParameterNode parameterNode) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(CubeNode cubeNode) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(DimensionNode dimensionNode) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(HierarchyNode hierarchyNode) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(LevelNode levelExpr) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(MemberNode memberNode) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(LiteralNode literalNode) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(PropertyValueNode propertyValueNode) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode visit(DrillThroughNode drillThroughNode) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public void accept(AxisNode axis) {
        ParseTreeNode exp = axis.getExpression().accept(this);
        final Type type = exp.getType();
        if (!TypeUtil.isSet(type)) {
            throw new RuntimeException(
                "Axis '" + axis.getAxis().name() + "' expression is not a set");
        }
    }

    public ParseTreeNode acceptScalar(ParseTreeNode node) {
        scalarStack.push(Boolean.TRUE);
        try {
            return node.accept(this);
        } finally {
            scalarStack.pop();
        }
    }

    // from IdentifierNode
    public ParseTreeNode accept(IdentifierNode identifier) {
        if (identifier.getSegmentList().size() == 1) {
            final IdentifierSegment s = identifier.getSegmentList().get(0);
            if (s.getQuoting() == Quoting.UNQUOTED
                && isReserved(s.getName()))
            {
                return LiteralNode.createSymbol(
                    s.getRegion(),
                    s.getName().toUpperCase());
            }
        }
        final ParseTreeNode element =
            lookup(selectNode, identifier.getSegmentList(), true);
        if (element == null) {
            return null;
        }
        return element.accept(this);
    }

    public ParseTreeNode accept(LiteralNode literalNode) {
        return literalNode;
    }

    public boolean isReserved(String name) {
        // todo: implement
        throw new UnsupportedOperationException();
    }

    private ParseTreeNode lookup(
        SelectNode select,
        List<IdentifierSegment> segments,
        boolean allowProp)
    {
        // todo: something like
     /*
        final Exp element = Util.lookup(select, names, true);
        */
        throw new UnsupportedOperationException();
    }
}

// End DefaultMdxValidatorImpl.java
