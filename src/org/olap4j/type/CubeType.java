/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2005-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.type;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Cube;

/**
 * The type of an expression which represents a Cube or Virtual Cube.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public class CubeType implements Type {
    private final Cube cube;

    /**
     * Creates a type representing a cube.
     *
     * @param cube Cube
     */
    public CubeType(Cube cube) {
        this.cube = cube;
    }

    /**
     * Returns the cube.
     *
     * @return the cube
     */
    public Cube getCube() {
        return cube;
    }

    public boolean usesDimension(Dimension dimension, boolean maybe) {
        return false;
    }

    public Dimension getDimension() {
        return null;
    }

    public Hierarchy getHierarchy() {
        return null;
    }

    public Level getLevel() {
        return null;
    }

    public boolean equals(Object obj) {
        if (obj instanceof CubeType) {
            CubeType that = (CubeType) obj;
            return TypeUtil.equal(this.cube, that.cube);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return cube == null
            ? 0
            : cube.hashCode();
    }
}

// End CubeType.java
