/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import java.sql.SQLException;

/**
 * Sugar class to help create OlapExceptions.
 * @author Luc Boudreau
 * @version $Id: $
 */
public class OlapExceptionHelper {
    
    public static OlapException createException(String msg) {
        return new OlapException(msg);
    }

    public static OlapException createException(Throwable cause) {
        return new OlapException(cause.getMessage(), cause);
    }

    public static OlapException createException(String msg, Throwable cause) {
        return new OlapException(msg, cause);
    }

    public static OlapException createException(Cell context, String msg) {
        OlapException exception = new OlapException(msg);
        exception.setContext(context);
        return exception;
    }

    public static OlapException createException(
        Cell context, String msg, Throwable cause)
    {
        OlapException exception = new OlapException(msg, cause);
        exception.setContext(context);
        return exception;
    }

    public static OlapException toOlapException(SQLException e) {
        if (e instanceof OlapException) {
            return (OlapException) e;
        } else {
            return new OlapException(null, e);
        }
    }
}

//End ExceptionHelper.java