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

import org.olap4j.OlapException;

/**
 * <code>Catalog</code> ...
 *
 * @author jhyde
 * @version $Id: $
 * @since Oct 24, 2006
 */
public interface Catalog {
    /**
     * Returns a list of {@link Schema} objects which belong to
     * this <code>Catalog</code>.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getSchemas
     * @return List of Schema in this <code>Catalog</code>
     */
    NamedList<Schema> getSchemas() throws OlapException;
}

// End Catalog.java
