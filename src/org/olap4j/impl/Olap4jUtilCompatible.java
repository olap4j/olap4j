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

import java.util.Set;

/**
 * Interface containing methods which are implemented differently in different
 * versions of the JDK.
 *
 * <p>The methods should not be called directly, only via the corresponding
 * static methods in {@link Olap4jUtil}, namely:<ul>
 * <li>{@link org.olap4j.impl.Olap4jUtil#quotePattern(String)}</li>
 * </ul></p>
 *
 * <p>This interface could in principle be extended to allow native
 * implementations of methods, or to serve as a factory for entire classes
 * which have different implementations in different environments.</p>
 *
 * @author jhyde
 * @since Feb 5, 2007
 */
public interface Olap4jUtilCompatible {
    /**
     * Returns a literal pattern String for the specified String.
     *
     * <p>Specification as for {@link java.util.regex.Pattern#quote(String)},
     * which was introduced in JDK 1.5.
     *
     * @param s The string to be literalized
     * @return A literal string replacement
     */
    String quotePattern(String s);

    /**
     * See {@link org.olap4j.impl.Olap4jUtil#enumSetOf(Enum, Enum[])}.
     */
    <E extends Enum<E>> Set<E> enumSetOf(E first, E... rest);

    /**
     * See {@link org.olap4j.impl.Olap4jUtil#enumSetNoneOf(Class)}.
     */
    <E extends Enum<E>> Set<E> enumSetNoneOf(Class<E> elementType);

    /**
     * See {@link org.olap4j.impl.Olap4jUtil#enumSetAllOf(Class)}.
     */
    <E extends Enum<E>> Set<E> enumSetAllOf(Class<E> elementType);
}

// End Olap4jUtilCompatible.java
