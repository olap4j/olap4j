/*
// $Id: Datatype.java 253 2009-06-30 03:06:10Z jhyde $
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2010-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.metadata.XmlaConstant.Dictionary}.
 *
 * @author jhyde
 * @version $Id: $
 */
class DictionaryImpl<E extends Enum<E> & XmlaConstant>
    implements XmlaConstant.Dictionary<E>
{
    private final Class<E> clazz;
    private final Map<String, E> byName = new HashMap<String, E>();
    private final Map<Integer, E> byOrdinal = new HashMap<Integer, E>();
    private final List<E> values;

    private static final Map<Class, DictionaryImpl> map =
        new HashMap<Class, DictionaryImpl>();

    public DictionaryImpl(Class<E> clazz) {
        this.clazz = clazz;
        this.values =
            Collections.unmodifiableList(
                Arrays.asList(clazz.getEnumConstants()));
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
        return byOrdinal.get(xmlaOrdinal);
    }

    public E forName(String xmlaName)
    {
        return byName.get(xmlaName);
    }

    public Set<E> forMask(
        int xmlaOrdinalMask)
    {
        Set<E> set = EnumSet.noneOf(clazz);
        for (E e : clazz.getEnumConstants()) {
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
        return values;
    }

    public Class<E> getEnumClass() {
        return clazz;
    }
}

// End DictionaryImpl.java
