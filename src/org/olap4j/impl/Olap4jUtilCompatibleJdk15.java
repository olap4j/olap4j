/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

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
 * @version $Id$
 * @since Feb 5, 2007
 */
public class Olap4jUtilCompatibleJdk15 implements Olap4jUtilCompatible {
    public String quotePattern(String s) {
        return Pattern.quote(s);
    }
}

// End Olap4jUtilCompatibleJdk15.java
