/*
// $Id: NamedList.java 17 2007-05-25 22:34:38Z jhyde $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
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
 * @version $Id: NamedList.java 17 2007-05-25 22:34:38Z jhyde $
 * @since Aug 22, 2006
 */
public interface NamedList<E> extends List<E> {
    /**
     * Retrieves a member by name.
     *
     * @see #get(int)
     */
    E get(String name);

    /**
     * Returns the position where a member of a given name is found, or -1
     * if the member is not present.
     */
    int indexOfName(String name);
}

// End NamedList.java
