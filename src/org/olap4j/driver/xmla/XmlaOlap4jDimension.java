/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.*;
import org.olap4j.metadata.*;

/**
 * Implementation of {@link org.olap4j.metadata.Dimension}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 4, 2007
 */
class XmlaOlap4jDimension
    extends XmlaOlap4jElement
    implements Dimension, Named
{
    final XmlaOlap4jCube olap4jCube;
    final Type type;
    final NamedList<XmlaOlap4jHierarchy> hierarchies =
        new NamedListImpl<XmlaOlap4jHierarchy>();
    private final String defaultHierarchyUniqueName;

    XmlaOlap4jDimension(
        XmlaOlap4jCube olap4jCube,
        String uniqueName,
        String name,
        String caption,
        String description,
        Type type,
        String defaultHierarchyUniqueName)
    {
        super(uniqueName, name, caption, description);
        this.defaultHierarchyUniqueName = defaultHierarchyUniqueName;
        assert olap4jCube != null;
        this.olap4jCube = olap4jCube;
        this.type = type;
    }

    public NamedList<Hierarchy> getHierarchies() {
        return Olap4jUtil.cast(hierarchies);
    }

    public Type getDimensionType() throws OlapException {
        return type;
    }

    public Hierarchy getDefaultHierarchy() {
        for (XmlaOlap4jHierarchy hierarchy : hierarchies) {
            if (hierarchy.getUniqueName().equals(defaultHierarchyUniqueName)) {
                return hierarchy;
            }
        }
        return hierarchies.get(0);
    }
}

// End XmlaOlap4jDimension.java
