/*
// $Id: Cube.java 16 2006-10-24 22:48:56Z jhyde $
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
 * <code>Cube</code> ...
 *
 * @author jhyde
 * @version $Id: Cube.java 16 2006-10-24 22:48:56Z jhyde $
 * @since Aug 22, 2006
 */
public interface Cube extends MetadataElement {
    /**
     * Returns the {@link Schema} this Cube belongs to.
     */
    Schema getSchema();

    /**
     * Returns a list of {@link Dimension} objects in this Cube.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getDimensions()
     */
    NamedList<Dimension> getDimensions();

    /**
     * Returns a list of {@link Measure} objects in this Cube.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getMeasures()
     */
    List<Measure> getMeasures();

    /**
     * Returns a list of {@link NamedSet} objects in this Cube.
     *
     * @see org.olap4j.OlapDatabaseMetaData#getSets()
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
