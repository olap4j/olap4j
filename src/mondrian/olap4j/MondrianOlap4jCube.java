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

import org.olap4j.metadata.*;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.NamedSet;
import org.olap4j.metadata.Schema;
import org.olap4j.metadata.Member;

import java.util.List;
import java.util.Locale;
import java.util.Collection;

import mondrian.olap.*;

/**
 * Implementation of {@link Cube}
 * for the Mondrian OLAP engine.
 *
 * @author jhyde
 * @version $Id$
 * @since May 24, 2007
 */
class MondrianOlap4jCube implements Cube, Named {
    private final mondrian.olap.Cube cube;
    private final MondrianOlap4jSchema olap4jSchema;

    MondrianOlap4jCube(
        mondrian.olap.Cube cube,
        MondrianOlap4jSchema olap4jSchema)
    {
        this.cube = cube;
        this.olap4jSchema = olap4jSchema;
    }

    public Schema getSchema() {
        return olap4jSchema;
    }

    public NamedList<Dimension> getDimensions() {
        List<MondrianOlap4jDimension> list =
            new NamedListImpl<MondrianOlap4jDimension>();
        for (mondrian.olap.Dimension dimension : cube.getDimensions()) {
            list.add(
                new MondrianOlap4jDimension(
                    olap4jSchema, dimension));
        }
        return (NamedList) list;
    }

    public List<Measure> getMeasures() {
        throw new UnsupportedOperationException();
    }

    public NamedList<NamedSet> getSets() {
        throw new UnsupportedOperationException();
    }

    public Collection<Locale> getSupportedLocales() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return cube.getName();
    }

    public String getUniqueName() {
        return cube.getUniqueName();
    }

    public String getCaption(Locale locale) {
        // todo: i81n
        return cube.getCaption();
    }

    public String getDescription(Locale locale) {
        // todo: i81n
        return cube.getDescription();
    }

    public Member lookupMember(String... nameParts) {
        throw new UnsupportedOperationException();
    }
}

// End MondrianOlap4jCube.java
