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

import org.olap4j.OlapException;

import java.util.Collection;
import java.util.Locale;

/**
 * A collection of database objects that contain structural information, or
 * metadata, about a database.
 *
 * <p>A Schema belongs to a {@link Catalog} and contains a number of
 * {@link Cube}s and shared {@link Dimension}s.
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 13, 2006
 */
public interface Schema {
    /**
     * Returns the {@link Catalog} this <code>Schema</code> belongs to.
     *
     * @return catalog this schema belongs to
     */
    Catalog getCatalog();

    /**
     * Returns the name of this Schema.
     *
     * @return name of this Schema
     */
    String getName();

    /**
     * Returns a list of cubes in this <code>Schema</code>.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getCubes
     * @return List of cubes in this Schema
     *
     * @throws OlapException if database error occurs
     */
    NamedList<Cube> getCubes() throws OlapException;

    /**
     * Returns a list of shared {@link Dimension} objects in this
     * <code>Schema</code>.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getDimensions(String,String,String,String)
     *
     * @return list of shared dimensions
     *
     * @throws OlapException if database error occurs
     */
    NamedList<Dimension> getSharedDimensions() throws OlapException;

    /**
     * Returns a collection of {@link java.util.Locale} objects for which this
     * <code>Schema</code> has been localized.
     *
     * <p>Consider the following use case. Suppose one cube is available in
     * English and French, and in French and Spanish, and both are shown in same
     * portal. Clients typically say that seeing reports in a mixture of
     * languages is confusing; the portal would figure out the best common
     * language, in this case French. This method allows the client to choose
     * the most appropriate locale.</p>
     *
     * <p>The list is advisory: a client is free to choose another locale,
     * in which case, the server will probably revert to the base locale for
     * locale-specific behavior such as captions and formatting.
     *
     * @see Cube#getSupportedLocales
     *
     * @return List of locales for which this <code>Schema</code> has been
     * localized
     *
     * @throws OlapException if database error occurs
     */
    Collection<Locale> getSupportedLocales() throws OlapException;
}

// End Schema.java
