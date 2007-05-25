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

import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.NamedList;
import org.olap4j.metadata.Schema;
import org.olap4j.metadata.Database;
import org.olap4j.OlapException;

/**
 * <code>MondrianOlap4jCatalog</code> ...
 *
 * @author jhyde
 * @version $Id: $
 * @since May 23, 2007
 */
class MondrianOlap4jCatalog implements Catalog, Named {
    final MondrianOlap4jDatabase olap4jDatabase;

    MondrianOlap4jCatalog(
        MondrianOlap4jDatabase olap4jDatabase) {
        this.olap4jDatabase = olap4jDatabase;
    }

    public NamedList<Schema> getSchemas() throws OlapException {
        // A mondrian catalog contains one schema, so implicitly it contains
        // one catalog
        NamedList<MondrianOlap4jSchema> list =
            new NamedListImpl<MondrianOlap4jSchema>();
        list.add(
            new MondrianOlap4jSchema(
                this,
                olap4jDatabase.metaData.connection.getSchemaReader(),
                olap4jDatabase.metaData.connection.getSchema()));
        return (NamedList) list;
    }

    public Database getDatabase() {
        return olap4jDatabase;
    }

    public String getName() {
        return "LOCALDB";
    }
}

// End MondrianOlap4jCatalog.java
