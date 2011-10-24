/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import org.olap4j.*;

/**
 * <p>Catalogs are the second element of the hierarchy of metadata objects.
 * A Catalog contains one or more {@link Schema}s and has a parent
 * {@link Database}.</p>
 *
 * <p>Some OLAP servers may only have one Catalog. Mondrian is one such
 * OLAP server; its sole catalog is called "LOCALDB".
 *
 * <p>To obtain the collection of catalogs in the current server, call the
 * {@link OlapConnection#getOlapCatalogs()} method.
 *
 * <p>The hierarchy of metadata objects, rooted at the connection from which
 * they are accessed, is as follows:
 * <blockquote>
 * <ul>
 * <li type="circle">{@link org.olap4j.OlapConnection}<ul>
 *     <li type="circle">{@link Database}<ul>
 *         <li type="circle">{@link Catalog}<ul>
 *             <li type="circle">{@link Schema}<ul>
 *                 <li type="circle">{@link Cube}<ul>
 *                     <li type="circle">{@link Dimension}<ul>
 *                         <li type="circle">{@link Hierarchy}<ul>
 *                             <li type="circle">{@link Level}<ul>
 *                                 <li type="circle">{@link Member}</li>
 *                                 <li type="circle">{@link Property}</li>
 *                             </ul></li>
 *                         </ul></li>
 *                     </ul></li>
 *                 <li type="circle">{@link NamedSet}</li>
 *                 </ul></li>
 *             <li type="circle">{@link Dimension} (shared)</li>
 *             </ul></li>
 *         </ul></li>
 *     </ul></li>
 *  </ul>
 * </blockquote>
 * </p>
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 24, 2006
 */
public interface Catalog {
    /**
     * Returns a list of {@link Schema} objects which belong to
     * this <code>Catalog</code>.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
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

    /**
     * Returns the parent database of this catalog.
     * @return A Database object to which this catalog belongs.
     */
    Database getDatabase();
}

// End Catalog.java
