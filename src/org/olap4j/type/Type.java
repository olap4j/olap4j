/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2005-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.type;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;

/**
 * Type of an MDX expression.
 *
 * <p>All type objects are immutable.
 *
 * @author jhyde
 * @since Feb 17, 2005
 * @version $Id$
 */
public interface Type {
    /**
     * Returns whether this type contains a given dimension.<p/>
     *
     * For example:
     * <ul>
     * <li><code>DimensionType([Gender])</code> uses only the
     *     <code>[Gender]</code> dimension.</li>
     * <li><code>TupleType(MemberType([Gender]), MemberType([Store]))</code>
     *     uses <code>[Gender]</code>  and <code>[Store]</code>
     *     dimensions.</li>
     * </ul><p/>
     *
     * The <code>maybe</code> parameter comes into play when the
     * dimensional information is incomplete. For example, when applied to
     * <code>TupleType(MemberType(null), MemberType([Store]))</code>,
     * <code>usesDimension([Gender], false)</code> returns true because it
     * is possible that the expression returns a member of the
     * <code>[Gender]</code> dimension.
     *
     * @param dimension Dimension
     * @param maybe If true, returns true only if this type definitely
     *    uses the dimension
     *
     * @return whether this type definitely (or if <code>maybe</code> is true,
     * possibly) uses the given dimension
     */
    boolean usesDimension(Dimension dimension, boolean maybe);

    /**
     * Returns the dimension of this type, or null if not known.
     *
     * @return dimension of this type
     */
    Dimension getDimension();

    /**
     * Returns the hierarchy of this type. If not applicable, throws.
     *
     * @return hierarchy of this type
     */
    Hierarchy getHierarchy();

    /**
     * Returns the level of this type, or null if not known.
     *
     * @return level of this type
     */
    Level getLevel();

}

// End Type.java
