/*
// $Id$
//
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
package org.olap4j.query;

import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.MetadataElement;

import java.util.List;

/**
 * A selection of members from an OLAP dimension hierarchy. The selection
 * is a conceptual list of members from a given hierarchy. Once a selection
 * object is created, one can decide to include or exclude this selection
 * of members from the resulting query.
 *
 * <p>Concrete subclasses of this represent a real selection.
 * Selections include things such as 'children of', 'siblings of',
 * 'descendents of' etc.
 *
 * <p>This class is different from a {@link org.olap4j.metadata.Member} because it represents an
 * abstract member selection (e.g. children of widget' that may not represent
 * any members whereas a Member represents a single member that is known to
 * exist.
 *
 * @author jdixon, jhyde, Luc Boudreau
 * @version $Id$
 * @since May 30, 2007
 */
public interface Selection extends QueryNode {

    /**
     * Unique name of the selection root.
     * @return The unique OLAP name of the selection root.
     */
    String getUniqueName();

    /**
     * Visitor pattern-like function to convert
     * the selection into a ParseTreeNode. Typical
     * implementation should be:<br/>
     * <code>Olap4jNodeConverter.toOlap4j(member, operator);</code>
     * @return A parse tree node of the selection.
     */
    ParseTreeNode visit();

    /**
     * Parent Dimension of the root selection element.
     * @return A dimension object.
     */
    Dimension getDimension();

    /**
     * Returns the root selection element of this selection.
     * @return The root metadata object.
     */
    MetadataElement getRootElement();

    /**
     * The selection context includes selections from other dimensions that
     * help determine the entire context of a selection, so drill down is
     * possible.
     *
     * @return list of selections
     */
    List<Selection> getSelectionContext();

    void addContext(Selection selection);

    void removeContext(Selection selection);

    Operator getOperator();

    /**
     * Set the selection operator to use.
     * @throws IllegalArgumentException if the operator cannot
     * be used on the root selection member.
     * @param operator Operator to apply on the selection.
     */
    void setOperator(Operator operator);

    /**
     * Defines which selection operators are allowed, relative to
     * a root member.
     */
    public enum Operator {
        /**
         * Only the root member will be selected.
         */
        MEMBER,
        /**
         * All members of Level will be selected (LevelSelection only)
         */
        MEMBERS,
        /**
         * Only the children of the root member will be selected.
         * This excludes the root member itself.
         * <p>Implemented via the MDX .Children member property.
         */
        CHILDREN,
        /**
         * The root member will be selected along with all it's
         * children.
         */
        INCLUDE_CHILDREN,
        /**
         * Will select the root member along with all it's siblings.
         * <p>Implemented via the MDX .Siblings member property.
         */
        SIBLINGS,
        /**
         * Selects the set of the ascendants of a specified member,
         * including the member itself.
         * <p>Implemented via the MDX Ascendants() function.
         */
        ANCESTORS,
        /**
         * Selects the set of the descendants of a specified member,
         * including the member itself.
         * <p>Implemented via the MDX Descendants() function.
         */
        DESCENDANTS;
    }
}

// End Selection.java
