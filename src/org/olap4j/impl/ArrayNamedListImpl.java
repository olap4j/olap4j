/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import org.olap4j.metadata.NamedList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of {@link org.olap4j.metadata.NamedList} which uses
 * {@link java.util.ArrayList} for storage.
 *
 * <p>Derived class must implement {@link #getName(Object)}, to indicate how
 * elements are named.
 *
 * @see NamedListImpl
 *
 * @author jhyde
 * @version $Id$
 * @since Nov 12, 2007
 */
public abstract class ArrayNamedListImpl<T>
    extends ArrayList<T>
    implements NamedList<T>
{
    /**
     * Creates an empty list with the specified initial capacity.
     *
     * @param   initialCapacity   the initial capacity of the list
     * @exception IllegalArgumentException if the specified initial capacity
     *            is negative
     */
    public ArrayNamedListImpl(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates an empty list.
     */
    public ArrayNamedListImpl() {
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
    public ArrayNamedListImpl(Collection<? extends T> c) {
        super(c);
    }

    protected abstract String getName(T t);

    public T get(String name) {
        for (T t : this) {
            if (getName(t).equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int indexOfName(String name) {
        for (int i = 0; i < size(); ++i) {
            T t = get(i);
            if (getName(t).equals(name)) {
                return i;
            }
        }
        return -1;
    }
}

// End ArrayNamedListImpl.java
