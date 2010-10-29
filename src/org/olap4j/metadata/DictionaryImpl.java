/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2010-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import org.olap4j.impl.Olap4jUtil;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.metadata.XmlaConstant.Dictionary}.
 *
 * @author jhyde
 * @version $Id$
 */
class DictionaryImpl<E extends Enum<E> & XmlaConstant>
    implements XmlaConstant.Dictionary<E>
{
    private final Class<E> clazz;
    private final Map<String, E> byName = new HashMap<String, E>();
    private final Map<Integer, E> byOrdinal = new HashMap<Integer, E>();
    private List<E> values;

    private static final Map<Class, DictionaryImpl> map =
        new HashMap<Class, DictionaryImpl>();

    public DictionaryImpl(Class<E> clazz) {
        this.clazz = clazz;
        init();
    }

    private void init() {
        if (values != null) {
            // Already initialized.
            return;
        }
        // The following statement throws NullPointerException under JDK1.4
        // (that is, when retrowoven) if clazz has not finished loading. This
        // happens when a static member of clazz is a Dictionary. If this
        // happens, swallow the NullPointerException and return null. init will
        // be called later.
        final E[] constants;
        try {
            constants = clazz.getEnumConstants();
        } catch (NullPointerException e) {
            return;
        }
        this.values =
            Collections.unmodifiableList(
                Arrays.asList(constants));
        for (E e : values) {
            byName.put(e.xmlaName(), e);
            byOrdinal.put(e.xmlaOrdinal(), e);
        }
    }

    public static <E extends Enum<E> & XmlaConstant> DictionaryImpl<E> forClass(
        Class<E> clazz)
    {
        assert clazz != null;
        synchronized (map) {
            @SuppressWarnings({"unchecked"})
            DictionaryImpl<E> directory = map.get(clazz);
            if (directory == null) {
                directory = new DictionaryImpl<E>(clazz);
                map.put(clazz, directory);
            }
            return directory;
        }
    }

    public E forOrdinal(int xmlaOrdinal)
    {
        init();
        return byOrdinal.get(xmlaOrdinal);
    }

    public E forName(String xmlaName)
    {
        init();
        return byName.get(xmlaName);
    }

    public Set<E> forMask(
        int xmlaOrdinalMask)
    {
        init();
        Set<E> set = Olap4jUtil.enumSetNoneOf(clazz);
        for (E e : values) {
            if ((xmlaOrdinalMask & e.xmlaOrdinal()) != 0) {
                set.add(e);
            }
        }
        return set;
    }

    public int toMask(Set<E> set)
    {
        int mask = 0;
        for (E e : set) {
            mask |= e.xmlaOrdinal();
        }
        return mask;
    }

    public List<E> getValues() {
        init();
        return values;
    }

    public Class<E> getEnumClass() {
        return clazz;
    }
}

// End DictionaryImpl.java
