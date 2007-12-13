/*
// $Id: //open/mondrian/src/main/mondrian/util/UtilCompatibleJdk14.java#3 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

/**
 * Implementation of {@link mondrian.util.UtilCompatible} which runs in
 * JDK 1.4.
 *
 * <p>The code uses JDK 1.5 constructs such as generics and for-each loops,
 * but retroweaver can convert these. It does not use
 * <code>java.util.EnumSet</code>, which is important, because retroweaver has
 * trouble with this.
 *
 * @author jhyde
 * @version $Id: //open/mondrian/src/main/mondrian/util/UtilCompatibleJdk14.java#3 $
 * @since Feb 5, 2007
 */
public class Olap4jUtilCompatibleJdk14 implements Olap4jUtilCompatible {
    public String quotePattern(String s) {
        int slashEIndex = s.indexOf("\\E");
        if (slashEIndex == -1)
            return "\\Q" + s + "\\E";

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
}

// End Olap4jUtilCompatibleJdk14.java
