/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.impl.Named;
import org.olap4j.metadata.MetadataElement;

import java.util.Locale;

/**
 * Abstract implementation of {@link MetadataElement}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 5, 2007
 */
abstract class XmlaOlap4jElement implements MetadataElement, Named {
    protected final String uniqueName;
    protected final String name;
    protected final String caption;
    protected final String description;
    private int hash = 0;

    XmlaOlap4jElement(
        String uniqueName,
        String name,
        String caption,
        String description)
    {
        assert uniqueName != null;
        assert description != null;
        assert name != null;
        assert caption != null;
        this.description = description;
        this.uniqueName = uniqueName;
        this.caption = caption;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public String getCaption(Locale locale) {
        return caption;
    }

    public String getDescription(Locale locale) {
        return description;
    }

    public int hashCode() {
        // By the book implementation of a hash code identifier.
        if (this.hash == 0) {
            hash = (getClass().hashCode() << 8) ^ getUniqueName().hashCode();
        }
        return hash;
    }

    // Keep this declaration abstract as a reminder to
    // overriding classes.
    abstract public boolean equeals(Object obj);
}

// End XmlaOlap4jElement.java
