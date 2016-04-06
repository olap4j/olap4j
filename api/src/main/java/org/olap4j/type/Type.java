/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.type;

import org.olap4j.metadata.*;

/**
 * Type of an MDX expression.
 *
 * <p>All type objects are immutable.
 *
 * @author jhyde
 * @since Feb 17, 2005
 */
public interface Type {
    /**
     * Returns whether this type contains a given dimension.
     *
     * <p>For example:
     * <ul>
     * <li><code>DimensionType([Gender])</code> uses only the
     *     <code>[Gender]</code> dimension.</li>
     * <li><code>TupleType(MemberType([Gender]), MemberType([Store]))</code>
     *     uses <code>[Gender]</code>  and <code>[Store]</code>
     *     dimensions.</li>
     * </ul>
     *
     * <p>The <code>maybe</code> parameter comes into play when the
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
