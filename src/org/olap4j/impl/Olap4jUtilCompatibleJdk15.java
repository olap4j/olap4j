/*
// $Id: //open/mondrian/src/main/mondrian/util/UtilCompatibleJdk15.java#3 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import java.util.regex.Pattern;

/**
 * Implementation of {@link mondrian.util.UtilCompatible} which runs in
 * JDK 1.5.
 *
 * <p>Prior to JDK 1.5, this class should never be loaded. Applications should
 * instantiate this class via {@link Class#forName(String)} or better, use
 * methods in {@link mondrian.olap.Util}, and not instantiate it at all.
 *
 * @author jhyde
 * @version $Id: //open/mondrian/src/main/mondrian/util/UtilCompatibleJdk15.java#3 $
 * @since Feb 5, 2007
 */
public class Olap4jUtilCompatibleJdk15 implements Olap4jUtilCompatible {
    public String quotePattern(String s) {
        return Pattern.quote(s);
    }
}

// End Olap4jUtilCompatibleJdk15.java
