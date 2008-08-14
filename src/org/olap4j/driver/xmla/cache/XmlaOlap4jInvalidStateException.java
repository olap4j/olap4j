/*
// $Id: InvalidStateException.java 92 2008-07-17 07:41:10Z lucboudreau $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.cache;

/**
 * <p>Internal exception which gets thrown when operations to the cache
 * are performed but it hasn't been initialized.
 *
 * <p>It extends RuntimeException so it cannot be catched by the
 * regular catch(Exception) mechanism. Those exceptions should get right
 * to the system level since it's a programming error.
 *
 * @author Luc Boudreau
 * @version $Id: InvalidStateException.java 92 2008-07-17 07:41:10Z lucboudreau $
 */
public class XmlaOlap4jInvalidStateException extends RuntimeException {
    private static final long serialVersionUID = 7265273715459263740L;
}

// End XmlaOlap4jInvalidStateException.java
