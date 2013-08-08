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

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Implementation of {@link Olap4jUtilCompatible} which runs in
 * JDK 1.5 and later.
 *
 * <p>Prior to JDK 1.5, this class should never be loaded. Applications should
 * instantiate this class via {@link Class#forName(String)} or better, use
 * methods in {@link Olap4jUtil}, and not instantiate it at all.
 *
 * @author jhyde
 * @since Feb 5, 2007
 */
public class Olap4jUtilCompatibleJdk15 implements Olap4jUtilCompatible {
    public final String quotePattern(String s) {
        return Pattern.quote(s);
    }

    public final <E extends Enum<E>> Set<E> enumSetOf(E first, E... rest) {
        return EnumSet.of(first, rest);
    }

    public final <E extends Enum<E>> Set<E> enumSetNoneOf(Class<E> elementType)
    {
        return EnumSet.noneOf(elementType);
    }

    public final <E extends Enum<E>> Set<E> enumSetAllOf(Class<E> elementType) {
        return EnumSet.allOf(elementType);
    }
}

// End Olap4jUtilCompatibleJdk15.java
