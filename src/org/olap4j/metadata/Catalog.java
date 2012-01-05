/*
// $Id$
//
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
