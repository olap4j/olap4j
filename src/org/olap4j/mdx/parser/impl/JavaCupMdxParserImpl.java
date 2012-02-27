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
package org.olap4j.mdx.parser.impl;

import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.parser.MdxParser;

/**
 * Default implementation of {@link org.olap4j.mdx.parser.MdxParser MDX Parser}.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public class JavaCupMdxParserImpl implements MdxParser {
    private boolean debug = false;
    private final FunTable funTable = new FunTable() {
        public boolean isProperty(String s) {
            return s.equals("CHILDREN");
        }
    };

    /**
     * Creates a DefaultMdxParserImpl.
     */
    public JavaCupMdxParserImpl() {
        super();
    }

    public SelectNode parseSelect(String mdx) {
        return new DefaultMdxParser().parseSelect(
            mdx,
            debug,
            funTable);
    }

    public ParseTreeNode parseExpression(String mdx) {
        return new DefaultMdxParser().parseExpression(
            mdx,
            debug,
            funTable);
    }

    interface FunTable {
        boolean isProperty(String s);
    }
}

// End DefaultMdxParserImpl.java
