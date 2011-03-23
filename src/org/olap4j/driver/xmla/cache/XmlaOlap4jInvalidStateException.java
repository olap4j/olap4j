/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.cache;

import org.olap4j.OlapException;

/**
 * <p>Internal exception which gets thrown when operations to the cache
 * are performed but it hasn't been initialized.
 *
 * @author Luc Boudreau
 * @version $Id$
 */
public class XmlaOlap4jInvalidStateException extends OlapException {
    private static final long serialVersionUID = 7265273715459263740L;
}

// End XmlaOlap4jInvalidStateException.java
