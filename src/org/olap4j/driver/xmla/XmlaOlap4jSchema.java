/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.Named;
import org.olap4j.impl.Olap4jUtil;
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
    private final NamedList<XmlaOlap4jDimension> sharedDimensions;

    XmlaOlap4jSchema(
        XmlaOlap4jCatalog olap4jCatalog,
        String name)
        throws OlapException
    {
        if (olap4jCatalog == null) {
            throw new NullPointerException("Catalog cannot be null.");
        }
        if (name == null) {
            throw new NullPointerException("Name cannot be null.");
        }

        this.olap4jCatalog = olap4jCatalog;
        this.name = name;

        // Dummy cube to own shared dimensions.
        final XmlaOlap4jCube sharedCube =
            new XmlaOlap4jCube(this, "", "", "");

        final XmlaOlap4jConnection.Context context =
            new XmlaOlap4jConnection.Context(
                olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection,
                olap4jCatalog.olap4jDatabaseMetaData,
                olap4jCatalog,
                this,
                sharedCube, null, null, null);

        this.cubes = new DeferredNamedListImpl<XmlaOlap4jCube>(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_CUBES,
            context,
            new XmlaOlap4jConnection.CubeHandler(),
            null);

        String[] restrictions = {
            "CATALOG_NAME", olap4jCatalog.getName(),
            "SCHEMA_NAME", getName(),
            "CUBE_NAME", ""
        };

        this.sharedDimensions = new DeferredNamedListImpl<XmlaOlap4jDimension>(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_DIMENSIONS,
            context,
            new XmlaOlap4jConnection.DimensionHandler(sharedCube),
            restrictions);
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
        return Olap4jUtil.cast(sharedDimensions);
    }

    public Collection<Locale> getSupportedLocales() throws OlapException {
        return Collections.emptyList();
    }
}

// End XmlaOlap4jSchema.java
