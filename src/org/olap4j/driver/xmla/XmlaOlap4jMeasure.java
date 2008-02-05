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
import org.olap4j.metadata.Datatype;
import org.olap4j.metadata.Measure;

/**
 * Implementation of {@link org.olap4j.metadata.Measure}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id: $
 * @since Dec 4, 2007
 */
class XmlaOlap4jMeasure
    extends XmlaOlap4jMember
    implements Measure, Named
{
    private final Aggregator aggregator;
    private final Datatype datatype;
    private final boolean visible;

    XmlaOlap4jMeasure(
        XmlaOlap4jLevel olap4jLevel,
        String uniqueName,
        String name,
        String caption,
        String description,
        String parentMemberUniqueName,
        Aggregator aggregator,
        Datatype datatype,
        boolean visible,
        int ordinal)
    {
        super(
            olap4jLevel, uniqueName, name, caption, description,
            parentMemberUniqueName, Type.MEASURE, 0, ordinal);
        this.aggregator = aggregator;
        this.datatype = datatype;
        this.visible = visible;
    }

    public Aggregator getAggregator() {
        return aggregator;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public boolean isVisible() {
        return visible;
    }
}

// End XmlaOlap4jMeasure.java
