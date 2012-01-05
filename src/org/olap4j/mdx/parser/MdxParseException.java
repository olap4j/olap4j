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

import org.olap4j.mdx.ParseRegion;

/**
 * Exception thrown by an {@link org.olap4j.mdx.parser.MdxParser} to
 * indicate an error in parsing. Has a {@link org.olap4j.mdx.ParseRegion}.
 *
 * @author jhyde
 * @version $Id$
 */
public class MdxParseException extends RuntimeException {
    private final ParseRegion region;

    /**
     * Creates an MdxParseException with a region of the source code and a
     * specified cause.
     *
     * @param region Region of source code which contains the error
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public MdxParseException(ParseRegion region, Throwable cause) {
        super(cause);
        this.region = region;
    }

    /**
     * Creates an MdxParseException with a region of the source code and a
     * specified detail message.
     *
     * @param region Region of source code which contains the error
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public MdxParseException(ParseRegion region, String message) {
        super(message);
        this.region = region;
    }

    public ParseRegion getRegion() {
        return region;
    }
}

// End MdxParseException.java
