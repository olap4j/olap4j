/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.metadata.*;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Schema;
import org.olap4j.OlapException;

import java.util.Locale;
import java.util.Collection;

import mondrian.olap.Util;

/**
 * Implementation of {@link org.olap4j.metadata.Schema}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since May 24, 2007
 */
class XmlaOlap4jSchema implements Schema, Named {
//    final MondrianOlap4jCatalog olap4jCatalog;
//    private final mondrian.olap.Schema schema;

    XmlaOlap4jSchema(
//        MondrianOlap4jCatalog olap4jCatalog,
//        SchemaReader schemaReader,
//        mondrian.olap.Schema schema
    )
    {
//        this.olap4jCatalog = olap4jCatalog;
//        this.schemaReader = schemaReader;
//        this.schema = schema;
    }

    public Catalog getCatalog() {
        throw Util.needToImplement(this);
    }

    public String getName() {
        throw Util.needToImplement(this);
    }

    public NamedList<Cube> getCubes() throws OlapException {
        throw Util.needToImplement(this);
    }

    public NamedList<Dimension> getSharedDimensions() throws OlapException {
        throw Util.needToImplement(this);
    }

    public Collection<Locale> getSupportedLocales() throws OlapException {
        throw Util.needToImplement(this);
    }
}

// End XmlaOlap4jSchema.java
