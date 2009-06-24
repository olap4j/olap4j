/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

/**
 * Objects that want to be notified of changes to the Query Model structure
 * have to implement this interface.
 * @author Luc Boudreau
 */
public interface QueryNodeListener {
    /**
     * Gets invoked when one or more children of a QueryNode gets removed
     * from it's list.
     * @param event Describes in detail the actual event that just happened.
     */
    public void childrenRemoved(QueryEvent event);
    /**
     * Gets invoked when one or more children get added to a QueryNode
     * list f children.
     * @param event Describes in detail the actual event that just happened.
     */
    public void childrenAdded(QueryEvent event);
    /**
     * Gets invoked when a selection operator has changed. This does not mean
     * that a Selection object was either added or removed from a Dimension,
     * it only means that it's operator value was modified.
     * @param event Describes in detail the actual event that just happened.
     * @see {@link Selection}
     */
    public void selectionChanged(QueryEvent event);
}
// End QueryNodeListener.java