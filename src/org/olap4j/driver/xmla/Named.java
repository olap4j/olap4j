/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

/**
 * Interface which describes an object which has a name, for the purposes of
 * creating an implementation, {@link org.olap4j.driver.xmla.NamedListImpl} of
 * {@link org.olap4j.metadata.NamedList} which works on such objects.
 *
 * @author jhyde
 * @version $Id$
 * @since May 23, 2007
 */
interface Named {
    /**
     * Returns the name of this object.
     *
     * @return name of this object
     */
    String getName();
}

// End Named.java
