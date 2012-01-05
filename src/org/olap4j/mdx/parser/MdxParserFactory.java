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
package org.olap4j.mdx.parser;

import org.olap4j.OlapConnection;

/**
 * Factory for MDX parsers.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface MdxParserFactory {
    /**
     * Creates an MDX parser.
     *
     * @param connection Connection in which to resolve identifiers
     * @return MDX parser
     */
    MdxParser createMdxParser(OlapConnection connection);

    /**
     * Creates an MDX validator.
     *
     * @param connection Connection in which to resolve identifiers
     * @return MDX validator
     */
    MdxValidator createMdxValidator(OlapConnection connection);
}

// End MdxParserFactory.java
