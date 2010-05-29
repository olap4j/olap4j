/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2009-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

/**
 * Objects that want to be notified of changes to the Query Model structure
 * have to implement this interface.
 *
 * @author Luc Boudreau
 * @version $Id: $
 */
public interface QueryNodeListener {
    /**
     * Invoked when one or more children of a QueryNode are removed
     * from its list.
     *
     * @param event Describes in detail the actual event that just happened.
     */
    public void childrenRemoved(QueryEvent event);

    /**
     * Invoked when one or more children are added to a QueryNode
     * list of children.
     *
     * @param event Describes in detail the actual event that just happened.
     */
    public void childrenAdded(QueryEvent event);

    /**
     * Invoked when a selection operator has changed. This does not mean
     * that a Selection object was either added or removed from a Dimension,
     * it only means that its operator value was modified.
     *
     * @param event Describes in detail the actual event that just happened.
     * @see org.olap4j.query.Selection
     **/
    public void selectionChanged(QueryEvent event);
}

// End QueryNodeListener.java
