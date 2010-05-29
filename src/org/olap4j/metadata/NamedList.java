/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import java.util.List;

/**
 * Extension to {@link java.util.List} which allows access to members of the
 * list by name as well as by ordinal.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface NamedList<E> extends List<E> {
    /**
     * Retrieves a member by name.
     *
     * @param name name of the element to return
     *
     * @see #get(int)
     *
     * @return the element of the list with the specified name, or null if
     * there is no such element
     */
    E get(String name);

    /**
     * Returns the position where a member of a given name is found, or -1
     * if the member is not present.
     *
     * @param name name of the element to return
     *
     * @return the index of element of the list with the specified name, or -1
     * if there is no such element
     *
     * @see #indexOf(Object)
     */
    int indexOfName(String name);
}

// End NamedList.java
