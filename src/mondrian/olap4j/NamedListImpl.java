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

import java.util.ArrayList;

/**
 * Implementation of {@link org.olap4j.metadata.NamedList} which uses
 * {@link java.util.ArrayList} for storage and assumes that elements implement
 * the {@link Named} interface.
 *
 * @author jhyde
 * @version $Id$
 * @since May 23, 2007
 */
class NamedListImpl<T extends Named>
    extends ArrayList<T>
    implements NamedList<T> {

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

// End NamedListImpl.java
