/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import java.util.*;

/**
 * Implementation of {@link java.util.Map} which uses an array and is therefore
 * not very fast but very space-efficient.
 *
 * <p>This implementation uses very little space but is inefficient. The
 * operations {@link #put}, {@link #remove}, {@link #get} all take time
 * proportional to the number of keys.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 9, 2007
 */
public class ArrayMap<K, V>
    implements Map<K, V>
{
    private static final Object[] EMPTY_OBJECTS = new Object[0];
    private Object[] keyValues;

    /**
     * Creates an empty <tt>ArrayMap</tt>.
     */
    public ArrayMap() {
        keyValues = EMPTY_OBJECTS;
    }

    /**
     * Creates an <tt>ArrayMap</tt> whose contents the same as the given
     * map.
     *
     * <p>This method is a more efficient way to build a large array than
     * repeatly calling {@link #put} or even calling {@link #putAll}.
     *
     * @param map Map
     */
    public ArrayMap(Map<K, V> map) {
        keyValues = new Object[map.size() * 2];
        int i = 0;
        for (Entry<K, V> entry : map.entrySet()) {
            keyValues[i++] = entry.getKey();
            keyValues[i++] = entry.getValue();
        }
    }

    public int size() {
        return keyValues.length / 2;
    }

    public boolean isEmpty() {
        return keyValues.length == 0;
    }

    public boolean containsKey(Object key) {
        return indexOfKey(key) >= 0;
    }

    private int indexOfKey(Object key) {
        for (int i = 0; i < keyValues.length; i += 2) {
            if (Olap4jUtil.equal(keyValues[i], key)) {
                return i;
            }
        }
        return -1;
    }

    public boolean containsValue(Object value) {
        for (int i = 1; i < keyValues.length; i += 2) {
            if (Olap4jUtil.equal(keyValues[i], value)) {
                return true;
            }
        }
        return false;
    }

    public V get(Object key) {
        final int i = indexOfKey(key);
        if (i >= 0) {
            return (V) keyValues[i + 1];
        }
        return null;
    }

    public V put(K key, V value) {
        final int i = indexOfKey(key);
        if (i >= 0) {
            V v = (V) keyValues[i + 1];
            keyValues[i + 1] = value;
            return v;
        } else {
            Object[] old = keyValues;
            keyValues = new Object[keyValues.length + 2];
            System.arraycopy(old, 0, keyValues, 0, old.length);
            keyValues[old.length] = key;
            keyValues[old.length + 1] = value;
            return null;
        }
    }

    public V remove(Object key) {
        final int i = indexOfKey(key);
        if (i >= 0) {
            V v = (V) keyValues[i + 1];
            removeInternal(i);
            return v;
        } else {
            // not present
            return null;
        }
    }

    private void removeInternal(int i) {
        if (keyValues.length == 2) {
            keyValues = EMPTY_OBJECTS;
        }
        Object[] old = keyValues;
        keyValues = new Object[keyValues.length - 2];
        System.arraycopy(old, 0, keyValues, 0, i);
        System.arraycopy(old, i + 2, keyValues, i, old.length - i - 2);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        keyValues = EMPTY_OBJECTS;
    }

    public Set<K> keySet() {
        return new KeySet();
    }

    public Collection<V> values() {
        return new ValueList();
    }

    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    private class KeySet extends AbstractSet<K> {
        public Iterator<K> iterator() {
            return new Iterator<K>() {
                int i = 0;
                public boolean hasNext() {
                    return i < keyValues.length;
                }

                public K next() {
                    K k = (K) keyValues[i];
                    i += 2;
                    return k;
                }

                public void remove() {
                    removeInternal(i);
                }
            };
        }

        public int size() {
            return ArrayMap.this.size();
        }
    }

    private class ValueList extends AbstractList<V> {
        public V get(int index) {
            return (V) keyValues[index * 2 + 1];
        }

        public int size() {
            return keyValues.length / 2;
        }
    }

    private class EntrySet extends AbstractSet<Entry<K, V>> {
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                int i = 0;
                public boolean hasNext() {
                    return i < keyValues.length;
                }

                public Entry<K, V> next() {
                    // We would use AbstractMap.SimpleEntry but it is not public
                    // until JDK 1.6.
                    final Entry<K, V> entry =
                        new Pair<K,V>(
                            (K) keyValues[i], (V) keyValues[i + 1]);
                    i += 2;
                    return entry;
                }

                public void remove() {
                    removeInternal(i);
                }
            };
        }

        public int size() {
            return ArrayMap.this.size();
        }
    }
}

// End ArrayMap.java
