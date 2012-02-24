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
package org.olap4j.metadata;

import java.util.List;
import java.util.Map;

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

    /**
     * Returns the name of a given element.
     *
     * @param element Element
     * @return Name of element
     */
    String elementName(Object element);

    /**
     * Returns a view of this named list as a {@link Map} whose key is the name
     * of each element.
     *
     * @return A view of this named list as a map
     */
    Map<String, E> asMap();
}

// End NamedList.java
