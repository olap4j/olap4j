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

import java.util.Locale;

/**
 * Common interface for
 *
 * @author jhyde
 * @version $Id: $
 * @since Oct 13, 2006
 */
public interface MetadataElement {
    String getName();

    String getUniqueName();
    
    /**
     * Returns the caption of this element in the given locale.
     *
     * If <code>locale</code> is null or if no caption has been defined for the
     * element in that locale, returns the caption in base locale.
     *
     * @param locale Locale
     * @return Caption of this element in the given locale, or the base locale;
     *         never null.
     */
    String getCaption(Locale locale);

    /**
     * Returns the description of this element in the given locale.
     *
     * If <code>locale</code> is null or if no description has been defined for
     * the element in that locale, returns the description in base locale.
     *
     * @param locale Locale
     * @return description of this element in the given locale, or the base
     *         locale; never null.
     */
    String getDescription(Locale locale);
}

// End MetadataElement.java
