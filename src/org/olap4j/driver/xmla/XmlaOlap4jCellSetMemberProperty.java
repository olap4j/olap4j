/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.impl.Named;
import org.olap4j.metadata.*;

import java.util.Locale;
import java.util.Set;

/**
 * Implementation of {@link org.olap4j.metadata.Property}
 * for a member returned on an axis in a cellset
 * from an XML/A provider.
 *
 * @author jhyde
 * @version $Id: $
 * @since Dec 7, 2007
 */
class XmlaOlap4jCellSetMemberProperty implements Property, Named {
    private final String propertyUniqueName;
    final Hierarchy hierarchy;
    final String tag;

    XmlaOlap4jCellSetMemberProperty(
        String propertyUniqueName,
        Hierarchy hierarchy,
        String tag)
    {
        this.propertyUniqueName = propertyUniqueName;
        this.hierarchy = hierarchy;
        this.tag = tag;
    }

    public Datatype getDatatype() {
        return Datatype.STRING;
    }

    public Set<TypeFlag> getType() {
        return TypeFlag.forMask(TypeFlag.MEMBER.xmlaOrdinal);
    }

    public String getName() {
        return tag;
    }

    public String getUniqueName() {
        return propertyUniqueName;
    }

    public String getCaption(Locale locale) {
        return propertyUniqueName;
    }

    public String getDescription(Locale locale) {
        return "";
    }

    public ContentType getContentType() {
        return ContentType.REGULAR;
    }
}

// End XmlaOlap4jCellSetMemberProperty.java
