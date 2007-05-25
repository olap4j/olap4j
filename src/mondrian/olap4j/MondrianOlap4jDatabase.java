/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap4j;

import org.olap4j.metadata.Database;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.NamedList;
import org.olap4j.OlapDatabaseMetaData;
import mondrian.olap.MondrianServer;

/**
 * <code>MondrianOlap4jDatabase</code> ...
 *
 * @author jhyde
 * @version $Id: $
 * @since May 23, 2007
 */
class MondrianOlap4jDatabase implements Database {
    final MondrianServer mondrianServer;
    final MondrianOlap4jDatabaseMetaData metaData;

    MondrianOlap4jDatabase(
        MondrianServer mondrianServer,
        MondrianOlap4jDatabaseMetaData metaData)
    {
        this.mondrianServer = mondrianServer;
        this.metaData = metaData;
    }

    public NamedList<Catalog> getCatalogs() {
        // A mondrian instance contains only one catalog.
        NamedListImpl<MondrianOlap4jCatalog> list =
            new NamedListImpl<MondrianOlap4jCatalog>();
        list.add(new MondrianOlap4jCatalog(this));
        return (NamedList) list;
    }

    public OlapDatabaseMetaData getMetaData() {
        return metaData;
    }
}

// End MondrianOlap4jDatabase.java
