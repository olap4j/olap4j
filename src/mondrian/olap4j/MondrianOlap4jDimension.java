/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap4j;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.NamedList;
import org.olap4j.metadata.Member;
import org.olap4j.OlapException;

import java.util.Locale;

/**
 * Implementation of {@link org.olap4j.metadata.Dimension}
 * for the Mondrian OLAP engine.
 *
 * @author jhyde
 * @version $Id$
 * @since May 24, 2007
 */
class MondrianOlap4jDimension implements Dimension, Named {
    private final MondrianOlap4jSchema olap4jSchema;
    private final mondrian.olap.Dimension dimension;

    MondrianOlap4jDimension(
        MondrianOlap4jSchema olap4jSchema,
        mondrian.olap.Dimension dimension)
    {
        this.olap4jSchema = olap4jSchema;
        this.dimension = dimension;
    }

    public boolean equals(Object obj) {
        return obj instanceof MondrianOlap4jDimension &&
            dimension.equals(((MondrianOlap4jDimension) obj).dimension);
    }

    public int hashCode() {
        return dimension.hashCode();
    }

    public NamedList<Hierarchy> getHierarchies() {
        throw new UnsupportedOperationException();
    }

    public Hierarchy getDefaultHierarchy() {
        return getHierarchies().get(0);
    }

    public NamedList<Member> getRootMembers() throws OlapException {
        throw new UnsupportedOperationException();
    }

    public Type getDimensionType() throws OlapException {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public String getUniqueName() {
        throw new UnsupportedOperationException();
    }

    public String getCaption(Locale locale) {
        throw new UnsupportedOperationException();
    }

    public String getDescription(Locale locale) {
        throw new UnsupportedOperationException();
    }
}

// End MondrianOlap4jDimension.java
