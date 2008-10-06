/*
// $Id:$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.proxy;

/**
 * Gets thrown whenever an exception is encountered during the querying
 * of an XmlaOlap4jProxy subclass.
 *
 * @author Luc Boudreau
 * @version $Id:$
 */
public class XmlaOlap4jProxyException extends Exception {
    private static final long serialVersionUID = 1729906649527317997L;
    public XmlaOlap4jProxyException(String message, Throwable cause) {
        super(message, cause);
    }
}
//End XmlaOlap4jProxyException.java