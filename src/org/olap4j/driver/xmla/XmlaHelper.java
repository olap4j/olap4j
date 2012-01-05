/*
// $Id$
//
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.driver.xmla;

import org.olap4j.Cell;
import org.olap4j.OlapException;

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
