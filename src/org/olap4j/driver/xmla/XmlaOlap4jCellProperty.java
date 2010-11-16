/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
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
}

// End XmlaOlap4jCellProperty.java
