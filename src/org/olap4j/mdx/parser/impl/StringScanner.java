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

/**
 * Lexical analyzer whose input is a string.
 *
 * <p>NOTE: This class is not part of the public olap4j API.
 *
 * @version $Id$
 * @author jhyde
 */
class StringScanner extends Scanner {
    private final String s;
    private int i;

    /**
     * Creates a StringScanner.
     *
     * @param s Input string
     * @param debug Whether to populate debug messages
     */
    StringScanner(String s, boolean debug) {
        super(debug);
        this.s = s;
        i = 0;
    }

    protected int getChar() {
        return (i >= s.length())
            ? -1
            : s.charAt(i++);
    }
}

// End StringScanner.java
