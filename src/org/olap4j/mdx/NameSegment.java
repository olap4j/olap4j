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

import org.olap4j.impl.Olap4jUtil;

import java.util.List;

/**
 * Component in a compound identifier that describes the name of an object.
 * Optionally, the name is quoted in brackets.
 *
 * @see KeySegment
 *
 * @version $Id$
 * @author jhyde
 */
public class NameSegment implements IdentifierSegment {
    final String name;
    final Quoting quoting;
    private final ParseRegion region;

    /**
     * Creates a segment with the given quoting and region.
     *
     * @param region Region of source code
     * @param name Name
     * @param quoting Quoting style
     */
    public NameSegment(
        ParseRegion region,
        String name,
        Quoting quoting)
    {
        this.region = region;
        this.name = name;
        this.quoting = quoting;
        if (name == null) {
            throw new NullPointerException();
        }
        if (!(quoting == Quoting.QUOTED || quoting == Quoting.UNQUOTED)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a quoted segment, "[name]".
     *
     * @param name Name of segment
     */
    public NameSegment(String name) {
        this(null, name, Quoting.QUOTED);
    }

    public String toString() {
        switch (quoting) {
        case UNQUOTED:
            return name;
        case QUOTED:
            return IdentifierNode.quoteMdxIdentifier(name);
        default:
            throw Olap4jUtil.unexpected(quoting);
        }
    }

    public void toString(StringBuilder buf) {
        switch (quoting) {
        case UNQUOTED:
            buf.append(name);
            return;
        case QUOTED:
            IdentifierNode.quoteMdxIdentifier(name, buf);
            return;
        default:
            throw Olap4jUtil.unexpected(quoting);
        }
    }
    public ParseRegion getRegion() {
        return region;
    }

    public String getName() {
        return name;
    }

    public Quoting getQuoting() {
        return quoting;
    }

    public List<NameSegment> getKeyParts() {
        return null;
    }
}

// End NameSegment.java
