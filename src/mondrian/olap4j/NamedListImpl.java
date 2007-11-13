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
    extends ArrayNamedListImpl<T>
{
    protected final String getName(T t) {
        return t.getName();
    }
}

// End NamedListImpl.java
