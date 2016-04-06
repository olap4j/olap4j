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

import java.util.Collections;
import java.util.Map;

/**
 * Unmodifiable map backed by an array.
 *
 * <p>Has the same benefits and limitations as {@link ArrayMap}. It is extremely
 * space-efficient but has poor performance for insert and lookup.
 *
 * <p>This structure is ideal if you are creating many maps with few elements.
 * The {@link #of(java.util.Map)} method will use
 * {@link java.util.Collections#emptyMap} and
 * {@link java.util.Collections#singletonMap(Object, Object)} if possible, and
 * these are even more space-efficient for maps of size 0 and 1.
 *
 * @author jhyde
 * @since Jan 16, 2010
 */
public class UnmodifiableArrayMap<K, V>
    extends ArrayMap<K, V>
{
    /**
     * Creates an UnmodifiableArrayMap.
     *
     * @param map Contents of map, copied on creation
     */
    public UnmodifiableArrayMap(Map<K, V> map) {
        super(map);
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an unmodifiable array map with given contents.
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
        if (keyValues.length == 0) {
            return Collections.singletonMap(key, value);
        }
        // Because UnmodifiableArrayMap is so bad at bulk inserts, it makes
        // sense to build another map just so we can populate the
        // UnmodifiableArrayMap in one shot. We require that the other map
        // preserves order; luckily mapOf uses LinkedHashMap.
        return new UnmodifiableArrayMap<K, V>(
            Olap4jUtil.mapOf(key, value, keyValues));
    }

    /**
     * Creates an unmodifiable map as a shallow copy of a map.
     *
     * <p>Future changes to the map will not be reflected in the contents
     * of the map.
     *
     * @param <K> Key type
     * @param <V> Value type
     * @return Unmodifiable map with same contents that the map had at
     * call time
     */
    public static <K, V> Map<K, V> of(
        Map<K, V> map)
    {
        switch (map.size()) {
        case 0:
            return Collections.emptyMap();
        case 1:
            final Entry<K, V> entry = map.entrySet().iterator().next();
            return Collections.singletonMap(entry.getKey(), entry.getValue());
        default:
            //noinspection unchecked
            return new UnmodifiableArrayMap<K, V>(map);
        }
    }
}

// End UnmodifiableArrayMap.java
