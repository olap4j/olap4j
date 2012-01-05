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
package org.olap4j.driver.xmla;

import org.olap4j.impl.Named;
import org.olap4j.metadata.Datatype;
import org.olap4j.metadata.Property;

import java.util.Set;

/**
 * Implementation of {@link org.olap4j.metadata.Property}
 * for a cell in a cellset
 * from XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 8, 2007
 */
class XmlaOlap4jCellProperty implements Property, Named {
    final String tag;
    final String propertyName;

    XmlaOlap4jCellProperty(
        String tag, String propertyName)
    {
        this.tag = tag;
        this.propertyName = propertyName;
    }

    public Datatype getDatatype() {
        return Datatype.STRING;
    }

    public Set<TypeFlag> getType() {
        return TypeFlag.CELL_TYPE_FLAG;
    }

    public String getName() {
        return propertyName;
    }

    public String getUniqueName() {
        return propertyName;
    }

    public String getCaption() {
        return propertyName;
    }

    public String getDescription() {
        return "";
    }

    public ContentType getContentType() {
        return ContentType.REGULAR;
    }

    public boolean isVisible() {
        return true;
    }
}

// End XmlaOlap4jCellProperty.java
