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
     * Registers a new listener for a QueryNode.
     * @param l The listener object. Must implement
     * {@see QueryNodeListener}
     */
    public void addQueryNodeListener(QueryNodeListener l);

    /**
     * De-registers a new listener for a QueryNode.
     * @param l The listener object. Must implement
     * {@see QueryNodeListener}
     */
    public void removeQueryNodeListener(QueryNodeListener l);
}
// End QueryNode.java