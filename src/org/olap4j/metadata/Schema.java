/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import java.util.Locale;
import java.util.Collection;

import org.olap4j.OlapException;

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
