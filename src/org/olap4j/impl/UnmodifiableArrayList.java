/*
// $Id: AbstractNamedList.java 229 2009-05-08 19:11:29Z jhyde $
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2009-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import java.util.*;

/**
 * Unmodifiable list backed by an array.
 *
 * <p>The traditional solution to this problem is to call
 * {@link java.util.Arrays#asList(Object[])} followed by
 * {@link java.util.Collections#unmodifiableList(java.util.List)}, but this
 * class has one fewer wrapper object, saving space and indirection effort.
 *
 * @author jhyde
 * @version $Id: AbstractNamedList.java 229 2009-05-08 19:11:29Z jhyde $
 * @since May 7, 2009
 */
public class UnmodifiableArrayList<T>
    extends AbstractList<T>
    implements List<T>
{
    private final T[] elements;

    /**
     * Creates an UnmodifiableArrayList.
     *
     * <p>Does not create a copy of the array. Future changes to the array will
     * be reflected in the contents of the list.
     *
     * @param elements Array
     */
    public UnmodifiableArrayList(T... elements) {
        this.elements = elements;
    }

    public T get(int index) {
        return elements[index];
    }

    public int size() {
        return elements.length;
    }

    /**
     * Creates an unmodifable list as a shallow copy of an array.
     *
     * <p>Future changes to the array will not be reflected in the contents
     * of the list.
     *
     * @param elements Elements of list
     * @param <T> Type of elements
     * @return Unmodifiable list with same contents that the array had at call
     * time
     */
    public static <T> UnmodifiableArrayList<T> asCopyOf(T... elements) {
        return new UnmodifiableArrayList<T>(elements.clone());
    }

    /**
     * Creates an unmodifable list as a shallow copy of a collection.
     *
     * <p>Future changes to the collection will not be reflected in the contents
     * of the list.
     *
     * @param collection Elements of list
     * @param <T> Type of elements
     * @return Unmodifiable list with same contents that the collection had at
     * call time
     */
    public static <T> UnmodifiableArrayList<T> of(
        Collection<? extends T> collection)
    {
        //noinspection unchecked
        return new UnmodifiableArrayList<T>(
            (T[]) collection.toArray());
    }
}

// End UnmodifiableArrayList.java
