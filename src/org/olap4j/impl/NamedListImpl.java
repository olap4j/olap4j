/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import java.util.Collection;

/**
 * Implementation of {@link org.olap4j.metadata.NamedList} which uses
 * {@link java.util.ArrayList} for storage and assumes that elements implement
 * the {@link org.olap4j.impl.Named} interface.
 *
 * @author jhyde
 * @version $Id$
 * @since May 23, 2007
 */
public class NamedListImpl<T extends Named>
    extends ArrayNamedListImpl<T>
{
    /**
     * Creates an empty list with the specified initial capacity.
     *
     * @param   initialCapacity   the initial capacity of the list
     * @exception IllegalArgumentException if the specified initial capacity
     *            is negative
     */
    public NamedListImpl(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates an empty list.
     */
    public NamedListImpl() {
        super();
    }

    /**
     * Creates a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public NamedListImpl(Collection<? extends T> c) {
        super(c);
    }

    protected final String getName(T t) {
        return t.getName();
    }
}

// End NamedListImpl.java
