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

import java.io.PrintWriter;
import java.util.List;

import static org.olap4j.mdx.MdxUtil.unparseList;

/**
 * Enumerated values describing the syntax of an expression.
 *
 * @author jhyde
 * @since 21 July, 2003
 * @version $Id$
 */
public enum Syntax {
    /**
     * Defines syntax for expression invoked <code>FUNCTION()</code> or
     * <code>FUNCTION(args)</code>.
     */
    Function {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            unparseList(writer, argList, operatorName + "(", ", ", ")");
        }
    },

    /**
     * Defines syntax for expression invoked as <code>object.PROPERTY</code>.
     */
    Property {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            assert argList.size() == 1;
            argList.get(0).unparse(writer); // 'this'
            writer.getPrintWriter().print(".");
            writer.getPrintWriter().print(operatorName);
        }
    },

    /**
     * Defines syntax for expression invoked invoked as
     * <code>object.METHOD()</code> or
     * <code>object.METHOD(args)</code>.
     */
    Method {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            assert argList.size() >= 1;
            argList.get(0).unparse(writer); // 'this'
            final PrintWriter pw = writer.getPrintWriter();
            pw.print(".");
            pw.print(operatorName);
            pw.print("(");
            for (int i = 1; i < argList.size(); i++) {
                if (i > 1) {
                    pw.print(", ");
                }
                argList.get(i).unparse(writer);
            }
            pw.print(")");
        }
    },

    /**
     * Defines syntax for expression invoked as <code>arg OPERATOR arg</code>
     * (like '+' or 'AND').
     */
    Infix {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            if (needParen(argList)) {
                unparseList(
                    writer,
                    argList,
                    "(",
                    " " + operatorName + " ",
                    ")");
            } else {
                unparseList(
                    writer,
                    argList,
                    "",
                    " " + operatorName + " ",
                    "");
            }
        }
    },

    /**
     * Defines syntax for expression invoked as <code>OPERATOR arg</code>
     * (like unary '-').
     */
    Prefix {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            if (needParen(argList)) {
                unparseList(
                    writer,
                    argList,
                    "(" + operatorName + " ",
                    null,
                    ")");
            } else {
                unparseList(
                    writer,
                    argList,
                    operatorName + " ",
                    null,
                    "");
            }
        }
    },

    /**
     * Defines syntax for expression invoked as <code>arg OPERATOR</code>
     * (like <code>IS EMPTY</code>).
     */
    Postfix {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            if (needParen(argList)) {
                unparseList(
                    writer,
                    argList,
                    "(",
                    null,
                    " " + operatorName + ")");
            } else {
                unparseList(
                    writer,
                    argList,
                    "",
                    null,
                    " " + operatorName);
            }
        }
    },

    /**
     * Defines syntax for expression invoked as
     * <code>{ARG, &#46;&#46;&#46;}</code>; that
     * is, the set construction operator.
     */
    Braces {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            unparseList(
                writer,
                argList,
                "{",
                ", ",
                "}");
        }
    },

    /**
     * Defines syntax for expression invoked as <code>(ARG)</code> or
     * <code>(ARG, &#46;&#46;&#46;)</code>; that is, parentheses for grouping
     * expressions, and the tuple construction operator.
     */
    Parentheses {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            if (argList.size() == 1
                && argList.get(0) instanceof CallNode
                && needParen(((CallNode) argList.get(0)).getArgList()))
            {
                // The parenthesized expression is going to defensively
                // parenthesize itself. So, don't add another layer.
                argList.get(0).unparse(writer);
            } else {
                unparseList(
                    writer,
                    argList,
                    "(",
                    ", ",
                    ")");
            }
        }
    },

    /**
     * Defines syntax for expression invoked as <code>CASE ... END</code>.
     */
    Case {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            final PrintWriter pw = writer.getPrintWriter();
            if (operatorName.equals("_CaseTest")) {
                pw.print("CASE");
                int j = 0;
                int clauseCount = (argList.size() - j) / 2;
                for (int i = 0; i < clauseCount; i++) {
                    pw.print(" WHEN ");
                    argList.get(j++).unparse(writer);
                    pw.print(" THEN ");
                    argList.get(j++).unparse(writer);
                }
                if (j < argList.size()) {
                    pw.print(" ELSE ");
                    argList.get(j++).unparse(writer);
                }
                assert j == argList.size();
                pw.print(" END");
            } else {
                assert operatorName.equals("_CaseMatch");

                pw.print("CASE ");
                int j = 0;
                argList.get(j++).unparse(writer);
                int clauseCount = (argList.size() - j) / 2;
                for (int i = 0; i < clauseCount; i++) {
                    pw.print(" WHEN ");
                    argList.get(j++).unparse(writer);
                    pw.print(" THEN ");
                    argList.get(j++).unparse(writer);
                }
                if (j < argList.size()) {
                    pw.print(" ELSE ");
                    argList.get(j++).unparse(writer);
                }
                assert j == argList.size();
                pw.print(" END");
            }
        }
    },

    /**
     * Defines syntax for expression generated by the system which
     * cannot be specified syntactically.
     */
    Internal,

    /**
     * Defines syntax for a CAST expression
     * <code>CAST(expression AS type)</code>.
     */
    Cast {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            writer.getPrintWriter().print("CAST(");
            argList.get(0).unparse(writer);
            writer.getPrintWriter().print(" AS ");
            argList.get(1).unparse(writer);
            writer.getPrintWriter().print(")");
        }
    },

    /**
     * Defines syntax for expression invoked <code>object&#46;&PROPERTY</code>
     * (a variant of {@link #Property}).
     */
    QuotedProperty {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            assert argList.size() == 1;
            argList.get(0).unparse(writer); // 'this'
            final PrintWriter pw = writer.getPrintWriter();
            pw.print(".[");
            pw.print(operatorName);
            pw.print("]");
        }
    },

    /**
     * Defines syntax for expression invoked <code>object&#46;[&PROPERTY]</code>
     * (a variant of {@link #Property}).
     */
    AmpersandQuotedProperty {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            assert argList.size() == 1;
            argList.get(0).unparse(writer); // 'this'
            final PrintWriter pw = writer.getPrintWriter();
            pw.print(".&[");
            pw.print(operatorName);
            pw.print("]");
        }
    },

    /**
     * Defines the syntax for an empty expression. Empty expressions can occur
     * within function calls, and are denoted by a pair of commas with only
     * whitespace between them, for example
     *
     * <blockquote>
     * <code>DrillDownLevelTop({[Product].[All Products]}, 3, ,
     *  [Measures].[Unit Sales])</code>
     * </blockquote>
     */
    Empty {
        public void unparse(
            String operatorName,
            List<ParseTreeNode> argList,
            ParseTreeWriter writer)
        {
            assert argList.size() == 0;
        }
    };

    /**
     * Converts a call to a function of this syntax into source code.
     *
     * @param operatorName Operator name
     * @param argList List of arguments
     * @param writer Writer
     */
    public void unparse(
        String operatorName,
        List<ParseTreeNode> argList,
        ParseTreeWriter writer)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns whether a collection of parse tree nodes need to be enclosed
     * in parentheses.
     *
     * @param args Parse tree nodes
     * @return Whether nodes need to be enclosed in parentheses
     */
    private static boolean needParen(List<ParseTreeNode> args) {
        return !(args.size() == 1
                 && args.get(0) instanceof CallNode
                 && ((CallNode) args.get(0)).getSyntax() == Parentheses);
    }
}

// End Syntax.java
