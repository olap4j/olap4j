/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import org.olap4j.metadata.NamedList;

import java.util.ArrayList;

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
