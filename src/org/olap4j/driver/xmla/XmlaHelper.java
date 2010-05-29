/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.Cell;

import java.sql.SQLException;

/**
 * Helper class which encapsulates policies which are
 * common throughout a driver. These policies include exception handling
 * and factory methods.
 *
 * @author Luc Boudreau
 * @version $Id$
 */
public class XmlaHelper {

    public OlapException createException(String msg) {
        return new OlapException(msg);
    }

    public OlapException createException(Throwable cause) {
        return new OlapException(cause.getMessage(), cause);
    }

    public OlapException createException(String msg, Throwable cause) {
        return new OlapException(msg, cause);
    }

    public OlapException createException(Cell context, String msg) {
        OlapException exception = new OlapException(msg);
        exception.setContext(context);
        return exception;
    }

    public OlapException createException(
        Cell context, String msg, Throwable cause)
    {
        OlapException exception = new OlapException(msg, cause);
        exception.setContext(context);
        return exception;
    }

    public OlapException toOlapException(SQLException e) {
        if (e instanceof OlapException) {
            return (OlapException) e;
        } else {
            return new OlapException(null, e);
        }
    }
}

// End XmlaHelper.java