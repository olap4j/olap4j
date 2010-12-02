/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

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
     * <p>Name is never null. Unlike {@link #getCaption() caption} and
     * {@link #getDescription() description}, an element's name is the same in
     * all locales.
     *
     * @return name of this element
     */
    String getName();

    /**
     * Returns the unique name of this element within its schema.
     *
     * <p>The unique name is never null, and is unique among all elements in
     * this {@link Schema}.
     *
     * <p>Unlike {@link #getCaption() caption} and
     * {@link #getDescription() description}, an element's unique name is the
     * same in all locales.
     *
     * <p>The structure of the unique name is provider-specific and subject to
     * change between provider versions. Applications should not attempt to
     * reverse-engineer the structure of the name.
     *
     * @return unique name of this element
     */
    String getUniqueName();

    /**
     * Returns the caption of this element in the current connection's locale.
     *
     * <p>This method may return the empty string, but never returns null.
     * The rules for deriving an element's caption are provider-specific,
     * but generally if no caption is defined for the element in a given locale,
     * returns the name of the element.</p>
     *
     * @return caption of this element in the current locale; never null.
     *
     * @see org.olap4j.OlapConnection#getLocale()
     */
    String getCaption();

    /**
     * Returns the description of this element in the current connection's
     * {@link java.util.Locale}.
     *
     * <p>This method may return the empty string, but never returns null.
     * The rules for deriving an element's description are provider-specific,
     * but generally if no description is defined
     * for the element in a given locale, returns the description in base
     * locale.</p>
     *
     * @return description of this element in the current locale; never null.
     *
     * @see org.olap4j.OlapConnection#getLocale()
     */
    String getDescription();

    /*
     * Returns whether this element is visible to end-users.
     *
     * @return Whether this element is visible
     */
    boolean isVisible();
}

// End MetadataElement.java
