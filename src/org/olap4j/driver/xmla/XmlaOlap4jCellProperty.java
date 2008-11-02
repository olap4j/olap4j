/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.impl.Named;
import org.olap4j.metadata.Datatype;
import org.olap4j.metadata.Property;

import java.util.Locale;
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
        return TypeFlag.forMask(TypeFlag.CELL.xmlaOrdinal);
    }

    public String getName() {
        return propertyName;
    }

    public String getUniqueName() {
        return propertyName;
    }

    public String getCaption(Locale locale) {
        return propertyName;
    }

    public String getDescription(Locale locale) {
        return "";
    }

    public ContentType getContentType() {
        return ContentType.REGULAR;
    }
}

// End XmlaOlap4jCellProperty.java
