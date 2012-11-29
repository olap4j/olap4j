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

import org.olap4j.metadata.NamedList;

import java.util.*;

/**
 * Map backed by a {@link org.olap4j.metadata.NamedList}.
 *
 * @author jhyde
 */
class NamedListMap<T> extends AbstractMap<String, T> {
    private final NamedList<T> namedList;

    /**
     * Creates a NamedListMap.
     *
     * @param namedList Named list
     */
    public NamedListMap(NamedList<T> namedList) {
        this.namedList = namedList;
    }

    public Set<Entry<String, T>> entrySet() {
        return new AbstractSet<Entry<String, T>>() {
            public Iterator<Entry<String, T>> iterator() {
                final Iterator<T> iterator = namedList.iterator();
                return new Iterator<Entry<String, T>>() {
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    public Entry<String, T> next() {
                        T x = iterator.next();
                        String name = namedList.getName(x);
                        return new Pair<String, T>(name, x);
                    }

                    public void remove() {
                        iterator.remove();
                    }
                };
            }

            public int size() {
                return namedList.size();
            }
        };
    }
}

// End NamedListMap.java
