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
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.Named;
import org.olap4j.impl.NamedListImpl;
import org.olap4j.metadata.NamedList;

import java.util.AbstractList;
import java.util.Map;

/**
 * Named list which instantiates itself on first use.
 *
 * <p><code>DeferredNamedListImpl</code> is useful way to load an object model
 * representing a hierarchical schema. If a catalog contains schemas, which
 * contain cubes, which contain dimensions, and so forth, and if all
 * collections loaded immediately, loading the catalog would immediately load
 * all sub-objects into memory, taking a lot of memory and time.
 *
 * <p>This class is not gc-friendly at present. Once populated,
 * <code>DeferredNamedListImpl</code> holds hard references
 * to the objects it contains, so they are not available to be
 * garbage-collected. Support for weak references might be a future enhancement
 * to this class.</p>
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 4, 2007
 */
class DeferredNamedListImpl<T extends Named>
    extends AbstractList<T>
    implements NamedList<T>
{
    private final NamedList<T> list = new NamedListImpl<T>();
    private State state = State.NEW;

    protected final XmlaOlap4jConnection.MetadataRequest metadataRequest;
    protected final XmlaOlap4jConnection.Context context;
    protected final XmlaOlap4jConnection.Handler<T> handler;
    protected final Object[] restrictions;

    DeferredNamedListImpl(
        XmlaOlap4jConnection.MetadataRequest metadataRequest,
        XmlaOlap4jConnection.Context context,
        XmlaOlap4jConnection.Handler<T> handler,
        Object[] restrictions)
    {
        this.metadataRequest = metadataRequest;
        this.context = context;
        this.handler = handler;
        this.restrictions = (restrictions == null)
            ? new Object[0] : restrictions;
    }

    /**
     * Flushes the contents of the list. Next access will re-populate.
     */
    void reset() {
        state = State.NEW;
        list.clear();
    }

    private NamedList<T> getList() {
        switch (state) {
        case POPULATING:
            throw new RuntimeException("recursive population");
        case NEW:
            try {
                state = State.POPULATING;
                populateList(list);
                state = State.POPULATED;
            } catch (OlapException e) {
                state = State.NEW;
                // TODO: fetch metadata on getCollection() method, so we
                // can't get an exception while traversing the list
                throw new RuntimeException(e);
            }
            // fall through
        case POPULATED:
        default:
            return list;
        }
    }

    public T get(int index) {
        return getList().get(index);
    }

    public int size() {
        return getList().size();
    }

    public T get(String name) {
        return getList().get(name);
    }

    public int indexOfName(String name) {
        return getList().indexOfName(name);
    }

    public String getName(T element) {
        return getList().getName(element);
    }

    public Map<String, T> asMap() {
        return getList().asMap();
    }

    protected void populateList(NamedList<T> list) throws OlapException {
        context.olap4jConnection.populateList(
            list, context, metadataRequest, handler, restrictions);
    }

    private enum State {
        NEW,
        POPULATING,
        POPULATED
    }
}

// End DeferredNamedListImpl.java
