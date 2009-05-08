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
 * @version $Id$
 * @since Feb 5, 2007
 */
public interface Olap4jUtilCompatible {
    String quotePattern(String s);
}

// End Olap4jUtilCompatible.java
