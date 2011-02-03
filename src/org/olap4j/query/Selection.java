/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import java.util.List;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Member;

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

    String getName();

    void setName(String name);

    Member getMember();

    Dimension getDimension();

    String getHierarchyName();

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

    String getLevelName();

    Operator getOperator();

    // @pre operator != null
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
