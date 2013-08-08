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

import java.util.*;

/**
 * Implementation of {@link Olap4jUtilCompatible} which runs in
 * JDK 1.4.
 *
 * @author jhyde
 * @since Feb 5, 2007
 */
public class Olap4jUtilCompatibleJdk14 implements Olap4jUtilCompatible {
    public final String quotePattern(String s) {
        int slashEIndex = s.indexOf("\\E");
        if (slashEIndex == -1) {
            return "\\Q" + s + "\\E";
        }
        StringBuilder sb = new StringBuilder(s.length() * 2);
        sb.append("\\Q");
        int current = 0;
        while ((slashEIndex = s.indexOf("\\E", current)) != -1) {
            sb.append(s.substring(current, slashEIndex));
            current = slashEIndex + 2;
            sb.append("\\E\\\\E\\Q");
        }
        sb.append(s.substring(current, s.length()));
        sb.append("\\E");
        return sb.toString();
    }

    public final <E extends Enum<E>> Set<E> enumSetOf(E first, E... rest) {
        HashSet<E> set = new HashSet<E>();
        set.add(first);
        set.addAll(Arrays.asList(rest));
        return set;
    }

    public final <E extends Enum<E>> Set<E> enumSetNoneOf(Class<E> elementType)
    {
        return new HashSet<E>();
    }

    public final <E extends Enum<E>> Set<E> enumSetAllOf(Class<E> elementType) {
        return new HashSet<E>(Arrays.asList(elementType.getEnumConstants()));
    }
}

// End Olap4jUtilCompatibleJdk14.java
