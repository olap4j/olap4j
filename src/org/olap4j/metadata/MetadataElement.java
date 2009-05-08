/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import java.util.Locale;

/**
 * An element which describes the structure of an OLAP schema.
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 13, 2006
 */
public interface MetadataElement {
    /**
     * Returns the name of this element.
     *
     * @return name
     */
    String getName();

    /**
     * Returns the unique name of this element within its schema.
     *
     * @return unique name of this element
     */
    String getUniqueName();

    /**
     * Returns the caption of this element in the given locale.
     *
     * <p>If <code>locale</code> is null or if no caption has been defined for
     * the element in that locale, returns the caption in base locale.</p>
     *
     * <p>This method may return the empty string, but never returns null.</p>
     *
     * @param locale Locale
     * @return Caption of this element in the given locale, or the base locale;
     *         never null.
     */
    String getCaption(Locale locale);

    /**
     * Returns the description of this element in the given locale.
     *
     * <p>If <code>locale</code> is null or if no description has been defined
     * for the element in that locale, returns the description in base
     * locale.</p>
     *
     * <p>This method may return the empty string, but never returns null.</p>
     *
     * @param locale Locale
     * @return description of this element in the given locale, or the base
     *         locale; never null.
     */
    String getDescription(Locale locale);
}

// End MetadataElement.java
