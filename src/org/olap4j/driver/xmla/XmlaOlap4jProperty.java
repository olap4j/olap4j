/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
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
 * for properties defined as part of the definition of a level or measure
 * from XML/A providers.
 *
 * @see org.olap4j.driver.xmla.XmlaOlap4jCellProperty
 * @see org.olap4j.driver.xmla.XmlaOlap4jCellSetMemberProperty
 *
 * @author jhyde
 * @version $Id$
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
}

// End XmlaOlap4jProperty.java
