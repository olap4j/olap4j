/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.metadata.Dimension;

/**
 * A selection of members from an OLAP dimension hierarchy.
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
 * @author jdixon, jhyde
 * @version $Id: $
 * @since May 30, 2007
 */
public interface Selection {
    String getName();

    void setName(String name);

    Dimension getDimension();

    String getHierarchyName();

    String getLevelName();

    Operator getOperator();

    // @pre operator != null
    void setOperator(Operator operator);

    public enum Operator {
        MEMBER, CHILDREN, INCLUDE_CHILDREN, SIBLINGS;
    }
}

// End Selection.java
