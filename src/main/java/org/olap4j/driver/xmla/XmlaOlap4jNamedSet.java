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
package org.olap4j.driver.xmla;

import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.NamedSet;

/**
 * Implementation of {@link org.olap4j.metadata.NamedSet}
 * for XML/A providers.
 *
 * @author jhyde
 * @since Dec 4, 2007
 */
class XmlaOlap4jNamedSet
    extends XmlaOlap4jElement
    implements NamedSet
{
    private final XmlaOlap4jCube olap4jCube;

    XmlaOlap4jNamedSet(
        XmlaOlap4jCube olap4jCube,
        String name)
    {
        super(name, name, name, "");
        this.olap4jCube = olap4jCube;
    }

    public Cube getCube() {
        return olap4jCube;
    }

    public ParseTreeNode getExpression() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this
        || obj instanceof XmlaOlap4jNamedSet
            && olap4jCube.equals(((XmlaOlap4jNamedSet) obj).olap4jCube)
            && name.equals(((XmlaOlap4jNamedSet) obj).name);
    }
}

// End XmlaOlap4jNamedSet.java
