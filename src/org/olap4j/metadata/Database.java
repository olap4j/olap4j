/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

/**
 * <code>Database</code> ...
 *
 * @author jhyde
 * @version $Id: $
 * @since Oct 24, 2006
 */
public interface Database {
    /**
     * Retrieves a list of {@link Catalog} objects which
     * belong to this <code>Database</code>.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getDatabase
     * @return List of Catalogs in this <code>Database</code>
     */
    NamedList<Catalog> getCatalogs();
}

// End Database.java
