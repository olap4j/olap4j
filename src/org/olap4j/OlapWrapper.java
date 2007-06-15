/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import java.sql.SQLException;

/**
 * <code>OlapWrapper</code> ...
 *
 * <p>In JDBC 3.0 (JDK 1.5) and earlier, the <code>OlapWrapper</code> interface
 * is used to convert a JDBC class to the corresponding olap4j class. For
 * instance, write
 *
 * <blockquote>
 * <pre>
 * import java.sql.Connection;
 * import java.sql.DriverManager;
 * import org.olap4j.OlapConnection;
 * import org.olap4j.OlapWrapper;
 *
 * Connection connection = DriverManager.getConnection("jdbc: ...");
 * OlapWrapper wrapper = (OlapWrapper) connection;
 * OlapConnection olapConnection = wrapper.unwrap(OlapConnection.class);
 * </pre>
 * </blockquote>
 *
 * to create a JDBC 3.0 connection and convert it to an olap4j connection.
 *
 * <p>In JDBC 4.0 (JDK 1.6) and later, you don't need to use this class. All of
 * the key JDBC classes implement <code>java.sql.Wrapper</code> interface, so
 * you can use its <code>isWrapper</code> and <code>unwrap</code> methods
 * without casting. For instance, write
 *
 * <blockquote>
 * <pre>
 * import java.sql.Connection;
 * import java.sql.DriverManager;
 * import org.olap4j.OlapConnection;
 *
 * Connection connection = DriverManager.getConnection("jdbc: ...");
 * OlapConnection olapConnection = connection.unwrap(OlapConnection.class);
 * </pre>
 * </blockquote>
 *
 * to create a JDBC 4.0 connection and convert it to an olap4j connection.
 *
 * @author jhyde
 * @version $Id: $
 * @since Jun 14, 2007
 */
public interface OlapWrapper {
    // duplicate method from java.sql.Wrapper (JDBC 4.0), so method is available
    // in JDBC 3.0
    <T> T unwrap(Class<T> iface) throws SQLException;

    // duplicate method from java.sql.Wrapper (JDBC 4.0), so method is available
    // in JDBC 3.0
    boolean isWrapperFor(Class<?> iface) throws SQLException;
}

// End OlapWrapper.java
