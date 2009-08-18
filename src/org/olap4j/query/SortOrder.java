/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;
/**
 * Defines in what order to perform sort operations.
 * @author Luc Boudreau
 * @version $Id:$
 * @since 0.9.8
 */
public enum SortOrder {
    /**
     * Ascending sort order. Members of
     * the same hierarchy are still kept together.
     */
    ASC,
    /**
     * Descending sort order. Members of
     * the same hierarchy are still kept together.
     */
    DESC,
    /**
     * Sorts in ascending order, but does not
     * maintain members of a same hierarchy
     * together. This is known as a "break
     * hierarchy ascending sort".
     */
    BASC,
    /**
     * Sorts in descending order, but does not
     * maintain members of a same hierarchy
     * together. This is known as a "break
     * hierarchy descending sort".
     */
    BDESC
}
// End SortOrder.java
