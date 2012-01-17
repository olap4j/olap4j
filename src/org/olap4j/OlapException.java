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
package org.olap4j;

import java.sql.*;

/**
 * <p>An exception describing an error accessing an OLAP database.</p>
 *
 * <p>Since olap4j extends JDBC, it is natural that <code>OlapException</code>
 * should extend JDBC's {@link SQLException}. The implementation by an olap4j
 * driver of a JDBC method which is declared to throw a SQLException may, if the
 * driver chooses, throw instead an OlapException.</p>
 *
 * <p>OlapException provides some additional information to help an OLAP client
 * identify the location of the error. This information is
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 23, 2006
 */
public class OlapException extends SQLException {
    private Region region;
    private Object context;

    /**
     * Constructs an <code>OlapException</code> object with a given
     * <code>reason</code>, <code>SQLState</code>  and
     * <code>vendorCode</code>.
     *
     * @param reason a description of the exception
     * @param sqlState an XOPEN or SQL 99 code identifying the exception
     * @param vendorCode a database vendor-specific exception code
     */
    public OlapException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    /**
     * Constructs an <code>OlapException</code> object with the given reason and
     * SQLState; the <code>vendorCode</code> field defaults to 0.
     *
     * @param reason a description of the exception
     * @param sqlState an XOPEN or SQL 99 code identifying the exception
     */
    public OlapException(String reason, String sqlState) {
        super(reason, sqlState);
    }

    /**
     * Constructs an <code>OlapException</code> object with a reason;
     * the <code>sqlState</code> field defaults to <code>null</code>, and
     * the <code>vendorCode</code> field defaults to 0.
     *
     * @param reason a description of the exception
     */
    public OlapException(String reason) {
        super(reason);
    }

    /**
     * Constructs an <code>OlapException</code> object;
     * the <code>reason</code> field defaults to null,
     * the <code>sqlState</code> field defaults to <code>null</code>, and
     * the <code>vendorCode</code> field defaults to 0.
     */
    public OlapException() {
        super();
    }

    /**
     * Constructs an <code>OlapException</code> object with a given
     * <code>cause</code>.
     * The <code>SQLState</code> is initialized
     * to <code>null</code> and the vendor code is initialized to 0.
     * The <code>reason</code>  is initialized to <code>null</code> if
     * <code>cause==null</code> or to <code>cause.toString()</code> if
     * <code>cause!=null</code>.
     * <p>
     * @param cause the underlying reason for this <code>OlapException</code>
     * (which is saved for later retrieval by the <code>getCause()</code>
     * method); may be null indicating the cause is non-existent or unknown.
     */
    public OlapException(Throwable cause) {
        super();
        initCause(cause);
    }

    /**
     * Constructs an <code>OlapException</code> object with a given
     * <code>reason</code> and <code>cause</code>.
     *
     * @param  reason the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public OlapException(String reason, Throwable cause) {
        // Cannot call super(reason, cause) because
        // SQLException(String, Throwable) only exists from JDK 1.6.
        super(reason);
        initCause(cause);
    }

    /**
     * Constructs an <code>OlapException</code> object with a given
     * <code>reason</code>, <code>SQLState</code> and  <code>cause</code>.
     * The vendor code is initialized to 0.
     *
     * @param reason a description of the exception.
     * @param sqlState an XOPEN or SQL:2003 code identifying the exception
     * @param cause the underlying reason for this <code>OlapException</code>
     * (which is saved for later retrieval by the
     * <code>getCause()</code> method); may be null indicating
     *     the cause is non-existent or unknown.
     */
    public OlapException(String reason, String sqlState, Throwable cause) {
        // Cannot call SQLException(String, String, Throwable); it only
        // exists from JDK 1.6
        super(reason, sqlState);
        initCause(cause);
    }

    /**
     * Constructs an <code>OlapException</code> object with a given
     * <code>reason</code>, <code>SQLState</code>, <code>vendorCode</code>
     * and  <code>cause</code>.
     *
     * @param reason a description of the exception
     * @param sqlState an XOPEN or SQL:2003 code identifying the exception
     * @param vendorCode a database vendor-specific exception code
     * @param cause the underlying reason for this <code>OlapException</code>
     * (which is saved for later retrieval by the <code>getCause()</code>
     * method);
     * may be null indicating the cause is non-existent or unknown.
     */
    public OlapException(
        String reason,
        String sqlState,
        int vendorCode,
        Throwable cause)
    {
        // Cannot call SQLException(String, String, int, Throwable); it only
        // exists from JDK 1.6
        super(reason, sqlState, vendorCode);
        initCause(cause);
    }

    /**
     * Sets the textual region where the exception occurred.
     *
     * @param region Textual region
     */
    public void setRegion(Region region) {
        this.region = region;
    }

    /**
     * Returns the textual region where the exception occurred, or null if no
     * region can be identified.
     *
     * @return Region where the exception occurred
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Sets the context where the exception occurred.
     *
     * @param context Context where the exception occurred
     * @throws IllegalArgumentException If context is not a {@link Cell}
     *   or a {@link Position}
     */
    public void setContext(Object context) {
        if (!(context instanceof Cell)
            && !(context instanceof Position))
        {
            throw new IllegalArgumentException(
                "expected Cell or Position");
        }
        this.context = context;
    }

    /**
     * Returns the context where the exception occurred.
     * Typically a {@link Cell} or a {@link Position}, or null.
     *
     * @return context where the exception occurred, or null
     */
    public Object getContext() {
        return context;
    }

    /**
     * Description of the position of a syntax or validation error in the source
     * MDX string.
     *
     * <p>Row and column positions are 1-based and inclusive. For example,
     * in</p>
     *
     * <blockquote>
     * <pre>
     * SELECT { [Measures].MEMBERS } ON COLUMNS,
     *    { } ON ROWS
     * FROM [Sales]
     * </pre>
     * </blockquote>
     *
     * <p>the <code>SELECT</code> keyword occupies positions (1, 1) through
     * (1, 6), and would have a <code>Region(startLine=1, startColumn=1,
     * endColumn=1, endLine=6)</code>.</p>
     */
    public static final class Region {
        public final int startLine;
        public final int startColumn;
        public final int endLine;
        public final int endColumn;

        protected Region(
            int startLine,
            int startColumn,
            int endLine,
            int endColumn)
        {
            this.startLine = startLine;
            this.startColumn = startColumn;
            this.endColumn = endLine;
            this.endLine = endColumn;
        }

        public String toString() {
            if (startLine == endColumn && startColumn == endLine) {
                return "line " + startLine + ", column " + startColumn;
            } else {
                return "line " + startLine + ", column " + startColumn
                    + " through line " + endLine + ", column " + endColumn;
            }
        }
    }
}

// End OlapException.java
