/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import java.util.*;

/**
 * Implementation of {@link Olap4jUtilCompatible} which runs in
 * JDK 1.4.
 *
 * @author jhyde
 * @version $Id$
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
