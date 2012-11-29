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

import org.olap4j.impl.Named;
import org.olap4j.metadata.Datatype;
import org.olap4j.metadata.Property;

import java.util.Set;

/**
 * Implementation of {@link org.olap4j.metadata.Property}
 * for properties defined as part of the definition of a level or measure
 * from XML/A providers.
 *
 * @see org.olap4j.driver.xmla.XmlaOlap4jCellProperty
 * @see org.olap4j.driver.xmla.XmlaOlap4jCellSetMemberProperty
 *
 * @author jhyde
 * @since Dec 9, 2007
 */
class XmlaOlap4jProperty
    extends XmlaOlap4jElement
    implements Property, Named
{
    private final Datatype datatype;
    private final Set<TypeFlag> type;
    private final ContentType contentType;

    XmlaOlap4jProperty(
        String uniqueName,
        String name,
        String caption,
        String description,
        Datatype datatype,
        Set<TypeFlag> type,
        ContentType contentType)
    {
        super(uniqueName, name, caption, description);
        this.contentType = contentType;
        assert datatype != null;
        assert type != null;
        this.datatype = datatype;
        this.type = type;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public Set<TypeFlag> getType() {
        return type;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public boolean equals(Object obj) {
        return (obj instanceof XmlaOlap4jProperty)
            && this.uniqueName.equals(
                ((XmlaOlap4jProperty) obj).getUniqueName());
    }
}

// End XmlaOlap4jProperty.java
