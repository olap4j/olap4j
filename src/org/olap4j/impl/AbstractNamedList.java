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

import java.util.AbstractList;

/**
 * Partial implementation of {@link org.olap4j.metadata.NamedList}.
 *
 * <p>Derived class must implement {@link #get(int)} and {@link #size()}, as
 * per {@link java.util.AbstractList}; and must implement
 * {@link #getName(Object)}, to indicate how elements are named.
 *
 * @see org.olap4j.impl.ArrayNamedListImpl
 *
 * @author jhyde
 * @version $Id$
 * @since May 25, 2007
 */
public abstract class AbstractNamedList<T>
    extends AbstractList<T>
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

// End AbstractNamedList.java
