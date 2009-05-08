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
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.NamedSet;

import java.util.Locale;

/**
 * Implementation of {@link org.olap4j.metadata.NamedSet}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 4, 2007
 */
class XmlaOlap4jNamedSet
    implements NamedSet, Named
{
    private final XmlaOlap4jCube olap4jCube;
    private final String name;

    XmlaOlap4jNamedSet(
        XmlaOlap4jCube olap4jCube,
        String name)
    {
        this.olap4jCube = olap4jCube;
        this.name = name;
    }

    public Cube getCube() {
        return olap4jCube;
    }

    public ParseTreeNode getExpression() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return name;
    }

    public String getUniqueName() {
        return name;
    }

    public String getCaption(Locale locale) {
        return name;
    }

    public String getDescription(Locale locale) {
        return "";
    }
}

// End XmlaOlap4jNamedSet.java
