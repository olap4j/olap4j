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

import java.util.List;
import java.util.Locale;
import java.util.Collection;

/**
 * Central metadata object for representation of multidimensional data.
 *
 * <p>A Cube belongs to a {@link Schema}, and is described by a list of
 * {@link Dimension}s and a list of {@link Measure}s. It may also have one or
 * more {@link NamedSet}s.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface Cube extends MetadataElement {
    /**
     * Returns the {@link Schema} this Cube belongs to.
     *
     * @return Schema this Cube belongs to
     */
    Schema getSchema();

    /**
     * Returns a list of {@link Dimension} objects in this Cube.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getDimensions(String,String,String,String)
     *
     * @return list of Dimensions
     */
    NamedList<Dimension> getDimensions();

    /**
     * Returns a list of {@link Measure} objects in this Cube.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getMeasures(String,String,String,String,String)
     *
     * @return list of Measures
     */
    List<Measure> getMeasures();

    /**
     * Returns a list of {@link NamedSet} objects in this Cube.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getSets(String,String,String,String)
     *
     * @return list of NamedSets
     */
    NamedList<NamedSet> getSets();

    /**
     * Returns a collection of {@link java.util.Locale} objects for which this
     * <code>Cube</code> has been localized.
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
     * locale-specific behavior such as captions and formatting.</p>
     *
     * @see Schema#getSupportedLocales
     *
     * @return List of locales for which this <code>Cube</code> has been
     * localized
     */
    Collection<Locale> getSupportedLocales();

}

// End Cube.java
