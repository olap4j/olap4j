/*
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
package org.olap4j.metadata;

import org.olap4j.impl.Olap4jUtil;
import org.olap4j.impl.UnmodifiableArrayList;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.metadata.XmlaConstant.Dictionary}.
 *
 * @author jhyde
 */
public class DictionaryImpl<E extends Enum<E> & XmlaConstant>
    implements XmlaConstant.Dictionary<E>
{
    private final Class<E> clazz;
    private final Map<String, E> byName = new HashMap<String, E>();
    private final Map<Integer, E> byOrdinal = new HashMap<Integer, E>();
    private List<E> values;

    private static final Map<Class, DictionaryImpl> map =
        new HashMap<Class, DictionaryImpl>();

    private DictionaryImpl(Class<E> clazz) {
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
        //noinspection unchecked
        this.values = UnmodifiableArrayList.of(constants);
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
