/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.Named;
import org.olap4j.impl.NamedListImpl;
import org.olap4j.metadata.NamedList;

import java.util.AbstractList;

/**
 * Named list which instantiates itself on first use.
 *
 * <p><code>DeferredNamedListImpl</code> is useful way to load an object model
 * representing a hierarchical schema. If a catalog contains schemas, which
 * contain cubes, which contain dimensions, and so forth, and if all
 * collections loaded immediately, loading the catalog would immediately load
 * all sub-objects into memory, taking a lot of memory and time.
 *
 * <p>(The above description is only intended to be illustrative. The XMLA
 * driver schema does not use deferred lists at every level; in particular,
 * it loads each cube in one swoop, fetching all dimensions, hierarchies and
 * levels of that cube, in order to reduce the number of metadata requests
 * submitted.)</p>
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

    DeferredNamedListImpl(
        XmlaOlap4jConnection.MetadataRequest metadataRequest,
        XmlaOlap4jConnection.Context context,
        XmlaOlap4jConnection.Handler<T> handler)
    {
        this.metadataRequest = metadataRequest;
        this.context = context;
        this.handler = handler;
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

    protected void populateList(NamedList<T> list) throws OlapException {
        context.olap4jConnection.populateList(
            list, context, metadataRequest, handler, new Object[0]);
    }

    private enum State {
        NEW,
        POPULATING,
        POPULATED
    }
}

// End DeferredNamedListImpl.java
