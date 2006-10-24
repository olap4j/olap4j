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

import org.olap4j.OlapDatabaseMetaData;

/**
 *
 * A <code>Database</code> is the root object in the hierarchy of metadata
 * objects:
 * <blockquote>
 * <ul>
 * <li type="circle">{@link Database}<ul>
 *     <li type="circle">{@link Catalog}<ul>
 *         <li type="circle">{@link Schema}<ul>
 *             <li type="circle">{@link Cube}<ul>
 *                 <li type="circle">{@link Dimension}<ul>
 *                     <li type="circle">{@link Hierarchy}<ul>
 *                         <li type="circle">{@link Level}<ul>
 *                             <li type="circle">{@link Member}</li>
 *                             <li type="circle">{@link Property}</li>
 *                         </ul></li>
 *                     </ul></li>
 *                 </ul></li>
 *             <li type="circle">{@link NamedSet}</li>
 *             </ul></li>
 *         <li type="circle">Dimension (shared)</li>
 *         </ul></li>
 *     </ul></li>
 *  </ul>
 * </blockquote>
 *
 * <p>A <code>Database</code> is usually accessed from a connection via its
 * metadata:
 * <blockquote><code>
 * OlapConnection connection;<br/>
 * Database database = connection.getMetaData().getDatabase();
 * </code></blockquote>
 *
 * or from a schema:
 *
 * <blockquote><code>
 * OlapConnection connection;<br/>
 * Database database = connection.getSchema().getCatalog().getDatabase();
 * </code></blockquote>
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

    /**
     * Retrieves the metadata describing this <code>Database</code>.
     */
    OlapDatabaseMetaData getMetaData();
}

// End Database.java
