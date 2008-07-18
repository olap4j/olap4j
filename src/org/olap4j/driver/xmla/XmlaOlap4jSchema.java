/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.*;
import org.olap4j.metadata.*;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.metadata.Schema}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since May 24, 2007
 */
class XmlaOlap4jSchema implements Schema, Named {
    final XmlaOlap4jCatalog olap4jCatalog;
    private final String name;
    final NamedList<XmlaOlap4jCube> cubes;

    XmlaOlap4jSchema(
        XmlaOlap4jCatalog olap4jCatalog,
        String name)
    {
        if (olap4jCatalog == null) {
            throw new NullPointerException("Catalog cannot be null.");
        }
        if (name == null) {
            throw new NullPointerException("Name cannot be null.");
        }
        
        this.olap4jCatalog = olap4jCatalog;
        this.name = name;
        this.cubes = new DeferredNamedListImpl<XmlaOlap4jCube>(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_CUBES,
            new XmlaOlap4jConnection.Context(
                olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection,
                olap4jCatalog.olap4jDatabaseMetaData,
                olap4jCatalog,
                this,
                null, null, null, null),
            new XmlaOlap4jConnection.CubeHandler());
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof XmlaOlap4jSchema) {
            XmlaOlap4jSchema that = (XmlaOlap4jSchema) obj;
            return this.name.equals(that.name)
                && this.olap4jCatalog.equals(that.olap4jCatalog);
        }
        return false;
    }

    public Catalog getCatalog() {
        return olap4jCatalog;
    }

    public String getName() {
        return name;
    }

    public NamedList<Cube> getCubes() throws OlapException {
        return Olap4jUtil.cast(cubes);
    }

    public NamedList<Dimension> getSharedDimensions() throws OlapException {
        // No shared dimensions
        return Olap4jUtil.cast(new NamedListImpl<XmlaOlap4jDimension>());
    }

    public Collection<Locale> getSupportedLocales() throws OlapException {
        return Collections.emptyList();
    }
}

// End XmlaOlap4jSchema.java
