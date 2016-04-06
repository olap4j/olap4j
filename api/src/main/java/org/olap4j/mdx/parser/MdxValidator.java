/*
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
package org.olap4j.mdx.parser;

import org.olap4j.OlapException;
import org.olap4j.mdx.SelectNode;

/**
 * Validator for the MDX query language.
 *
 * <p>A validator is reusable but not reentrant: you can call
 * {@link #validateSelect(org.olap4j.mdx.SelectNode)} several times, but not at
 * the same time from different threads.
 *
 * <p>To create a validator, use the
 * {@link MdxParserFactory#createMdxValidator(org.olap4j.OlapConnection)}
 * method.
 *
 * @see MdxParserFactory
 * @see MdxParser
 *
 * @author jhyde
 * @since Aug 22, 2006
 */
public interface MdxValidator {
    /**
     * Validates an MDX SELECT statement.
     *
     * <p>The SelectNode representing the SELECT statement may have been
     * created by an {@link MdxParser}, or it may have been built
     * programmatically.
     *
     * <p>If the parse tree is invalid, throws an {@link OlapException}.
     *
     * <p>If it is valid, returns a parse tree. This parse tree may or may not
     * be the same parse tree passed as an argument. After validation, you can
     * ascertain the type of each node of the parse tree by calling its
     * {@link org.olap4j.mdx.ParseTreeNode#getType()} method.
     *
     * @param selectNode Parse tree node representing a SELECT statement
     *
     * @return Validated parse tree
     *
     * @throws OlapException if node is invalid
     */
    SelectNode validateSelect(SelectNode selectNode) throws OlapException;
}

// End MdxValidator.java
