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

import java.util.List;
import java.util.Arrays;

import org.olap4j.type.Type;

/**
 * A parse tree node representing a call to a function or operator.
 *
 * <p>Examples of calls include:<ul>
 * <li><code>5 + 2</code>, a call to the infix arithmetic operator '+'</li>
 * <li><code>[Measures].[Unit Sales] IS NULL</code>, a call applying the
 *   {@link Syntax#Postfix postfix} operator
 *   <code>IS NULL</code> to a member expression</li>
 * <li><code>CrossJoin({[Gender].Children}, {[Store]})</code>, a call to the
 *   <code>CrossJoin</code> function</li>
 * <li><code>[Gender].Children</code>, a call to the <code>Children</code>
 *   operator, which has {@link Syntax#Property property syntax}</li>
 * <li><code>[Gender].Properties("FORMAT_STRING")</code>, a call to the
 *   <code>Properties</code> operator, which has
 *   {@link Syntax#Method method syntax}</li>
 * </ul>
 *
 * @author jhyde
 * @version $Id: //open/mondrian/src/main/mondrian/olap/FunCall.java#22 $
 * @since Jan 6, 2006
 */
public class CallNode implements ParseTreeNode {

    private final String name;
    private final Syntax syntax;
    private final List<ParseTreeNode> argList;

    /**
     * Creates an CallNode.
     *
     * <p>The <code>syntax</code> argument determines whether this is a prefix,
     * infix or postfix operator, a function call, and so forth.
     *
     * <p>The list of arguuments <code>args</code> must be specified, even if
     * there are zero arguments, and each argument must be not null.
     *
     * @param name Name of operator or function
     * @param syntax Syntax of call
     * @param args List of zero or more arguments
     */
    public CallNode(
        String name,
        Syntax syntax,
        List<ParseTreeNode> args)
    {
        assert name != null;
        assert syntax != null;
        assert args != null;
        this.name = name;
        this.syntax = syntax;
        this.argList = args;

        // Check special syntaxes.
        switch (syntax) {
        case Braces:
            assert name.equals("{}");
            break;
        case Parentheses:
            assert name.equals("()");
            break;
        case Internal:
            assert name.startsWith("$");
            break;
        default:
            assert !name.startsWith("$") &&
                !name.equals("{}") &&
                !name.equals("()");
            break;
        }
    }

    /**
     * Creates an CallNode using a variable number of arguments.
     *
     * <p>The <code>syntax</code> argument determines whether this is a prefix,
     * infix or postfix operator, a function call, and so forth.
     *
     * <p>The list of arguuments <code>args</code> must be specified, even if
     * there are zero arguments, and each argument must be not null.
     *
     * @param name Name of operator or function
     * @param syntax Syntax of call
     * @param args List of zero or more arguments
     */
    public CallNode(
        String name,
        Syntax syntax,
        ParseTreeNode... args)
    {
        this(name, syntax, Arrays.asList(args));
    }

    public Type getType() {
        throw new UnsupportedOperationException();
    }

    public void unparse(ParseTreeWriter writer) {
        syntax.unparse(name, argList, writer);
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        final T o = visitor.visit(this);
        // visit the call's arguments
        for (ParseTreeNode arg : argList) {
            arg.accept(visitor);
        }
        return o;
    }

    /**
     * Returns the name of the function or operator.
     *
     * @return name of the function or operator
     */
    public String getOperatorName() {
        return name;
    }

    /**
     * Returns the syntax of this call.
     *
     * @return the syntax of the call
     */
    public Syntax getSyntax() {
        return syntax;
    }

    /**
     * Returns the list of arguments to this call.
     *
     * @return list of arguments
     */
    public List<ParseTreeNode> getArgList() {
        return argList;
    }
}

// End CallNode.java
