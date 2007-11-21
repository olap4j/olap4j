/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap4j;

import org.olap4j.metadata.Property;

import java.util.Locale;

/**
 * Implementation of {@link org.olap4j.metadata.Property}
 * for the Mondrian OLAP engine,
 * as a wrapper around a mondrian
 * {@link mondrian.olap.Property}.
 *
 * @author jhyde
 * @version $Id$
 * @since Nov 12, 2007
 */
class MondrianOlap4jProperty implements Property, Named {
    private final mondrian.olap.Property property;

    MondrianOlap4jProperty(mondrian.olap.Property property) {
        this.property = property;
    }

    public Datatype getDatatype() {
        return Datatype.valueOf(property.getType().name());
    }

    public Scope getScope() {
        return property.isCellProperty()
            ? Scope.CELL
            : Scope.MEMBER;
    }

    public String getName() {
        return property.name;
    }

    public String getUniqueName() {
        return property.name;
    }

    public String getCaption(Locale locale) {
        // todo: i18n
        return property.getCaption();
    }

    public String getDescription(Locale locale) {
        // todo: i18n
        return property.getDescription();
    }
}

// End MondrianOlap4jProperty.java
