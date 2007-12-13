/*
// $Id: //open/mondrian/src/main/mondrian/util/UtilCompatible.java#3 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

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
 * @version $Id: //open/mondrian/src/main/mondrian/util/UtilCompatible.java#3 $
 * @since Feb 5, 2007
 */
public interface Olap4jUtilCompatible {
    String quotePattern(String s);
}

// End Olap4jUtilCompatible.java
