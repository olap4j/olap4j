/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap4j;

import org.olap4j.metadata.NamedList;

import java.util.AbstractList;

/**
 * Partial implementation of {@link org.olap4j.metadata.NamedList}.
 *
 * <p>Derived class must implement {@link #get(int)} and {@link #size()}, as
 * per {@link java.util.AbstractList}.
 *
 * @see mondrian.olap4j.NamedListImpl
 *
 * @author jhyde
 * @version $Id$
 * @since May 25, 2007
 */
abstract class AbstractNamedList<T extends Named>
    extends AbstractList<T>
    implements NamedList<T>
{
    public T get(String name) {
        for (T t : this) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public int indexOfName(String name) {
        for (int i = 0; i < size(); ++i) {
            T t = get(i);
            if (t.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

}

// End AbstractNamedList.java
