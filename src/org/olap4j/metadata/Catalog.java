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
import org.olap4j.OlapDatabaseMetaData;

/**
 * <code>Catalog</code> ...
 *
 * A <code>Catalog</code> is the root object in the hierarchy of metadata
 * objects:
 * <blockquote>
 * <ul>
 * <li type="circle">{@link org.olap4j.OlapConnection}<ul>
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
     * @throws OlapException if error occurs
     */
    NamedList<Schema> getSchemas() throws OlapException;

    /**
     * Returns the name of this Catalog.
     *
     * @return name of this Catalog
     */
    String getName();

    /**
     * Retrieves the metadata describing the OLAP server that this Catalog
     * belongs to.
     *
     * @return metadata describing the OLAP server
     */
    OlapDatabaseMetaData getMetaData();
}

// End Catalog.java
