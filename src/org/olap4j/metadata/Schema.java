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
 * <code>Schema</code> ...
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 13, 2006
 */
public interface Schema {
    /**
     * Returns the {@link Catalog} this <code>Schema</code> belongs to.
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
     * @see org.olap4j.OlapDatabaseMetaData#getCubes
     * @return List of cubes in this Schema
     */
    NamedList<Cube> getCubes() throws OlapException;

    /**
     * Returns a list of shared {@link Dimension} objects in this
     * <code>Schema</code>.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getDimensions()
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
     */
    Collection<Locale> getSupportedLocales() throws OlapException;
}

// End Schema.java
