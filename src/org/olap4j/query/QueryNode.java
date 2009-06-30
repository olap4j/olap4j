/*
// $Id: $
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

/**
 * Describes what methods a Query Node must implement
 * in order to support listeners. Olap4j's query model
 * provides an abstract implementation of interface.
 *
 * @author Luc Boudreau
 */
interface QueryNode {

    /**
     * Registers a new listener for a {@link QueryNode}.
     *
     * @param listener Listener
     */
    public void addQueryNodeListener(QueryNodeListener listener);

    /**
     * De-registers a listener for a {@link QueryNode}.
     *
     * <p>REVIEW: Is it an error if the listener does not exist?
     *
     * @param listener Listener
     */
    public void removeQueryNodeListener(QueryNodeListener listener);
}

// End QueryNode.java
