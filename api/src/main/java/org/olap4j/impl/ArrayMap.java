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

    /**
     * Returns an array map with given contents.
     *
     * @param key First key
     * @param value First value
     * @param keyValues Second and sequent key/value pairs
     * @param <K> Key type
     * @param <V> Value type
     * @return Map with given contents
     */
    public static <K, V> Map<K, V> of(
        K key,
        V value,
        Object... keyValues)
    {
        // Because ArrayMap is so bad at bulk inserts, it makes sense to build
        // another map (HashMap) just so we can populate the ArrayMap in one
        // shot.
        return new ArrayMap<K, V>(Olap4jUtil.mapOf(key, value, keyValues));
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        Map<K, V> m = (Map<K, V>) o;
        if (m.size() != size()) {
            return false;
        }
        try {
            for (Entry<K, V> e : entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(m.get(key) == null && m.containsKey(key))) {
                        return false;
                    }
                } else {
                    if (!value.equals(m.get(key))) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int h = 0;
        Iterator<Entry<K, V>> i = entrySet().iterator();
        while (i.hasNext()) {
            h += i.next().hashCode();
        }
        return h;
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
        if (keyValues.length == 0) {
            // Efficient implementation of common case where map is initially
            // empty; otherwise we have O(n^2) reallocs.
            keyValues = new Object[m.size() * 2];
            int i = 0;
            for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
                keyValues[i++] = entry.getKey();
                keyValues[i++] = entry.getValue();
            }
        } else {
            // This algorithm is O(n^2): not great if m is large. But it's
            // difficult to preallocate the array if we don't know how many
            // keys overlap between this and m.
            for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
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

    public String toString() {
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (! i.hasNext()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (! i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(", ");
        }
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
                        new Pair<K, V>(
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
