/*
// $Id$
//
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.impl;

import org.olap4j.metadata.NamedList;

import java.util.*;

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

    public Map<String, T> asMap() {
        return new NamedListMap<T>(this);
    }
}

// End ArrayNamedListImpl.java
