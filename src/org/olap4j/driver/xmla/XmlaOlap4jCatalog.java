/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapDatabaseMetaData;
import org.olap4j.OlapException;
import org.olap4j.impl.Named;
import org.olap4j.metadata.*;

/**
 * Implementation of {@link org.olap4j.metadata.Catalog}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since May 23, 2007
 */
class XmlaOlap4jCatalog implements Catalog, Named {
    final XmlaOlap4jDatabaseMetaData olap4jDatabaseMetaData;
    private final String name;
    private final DeferredNamedListImpl<XmlaOlap4jSchema> schemas;

    XmlaOlap4jCatalog(
        XmlaOlap4jDatabaseMetaData olap4jDatabaseMetaData,
        String name)
    {
        this.olap4jDatabaseMetaData = olap4jDatabaseMetaData;
        this.name = name;
        this.schemas =
            new DeferredNamedListImpl<XmlaOlap4jSchema>(
                XmlaOlap4jConnection.MetadataRequest.DBSCHEMA_CATALOGS,
                new XmlaOlap4jConnection.Context(
                    olap4jDatabaseMetaData.olap4jConnection,
                    olap4jDatabaseMetaData,
                    this,
                    null, null, null, null, null),
                new XmlaOlap4jConnection.SchemaHandler());
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof XmlaOlap4jCatalog) {
            XmlaOlap4jCatalog that = (XmlaOlap4jCatalog) obj;
            return this.name.equals(that.name);
        }
        return false;
    }

    public NamedList<Schema> getSchemas() throws OlapException {
        return (NamedList) schemas;
    }

    public String getName() {
        return name;
    }

    public OlapDatabaseMetaData getMetaData() {
        return olap4jDatabaseMetaData;
    }
}

// End XmlaOlap4jCatalog.java
