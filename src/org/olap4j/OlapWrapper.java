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

import java.sql.SQLException;

/**
 * Interface for olap4j classes which provide the ability to retrieve the
 * delegate instance when the instance in question is in fact a proxy class.
 *
 * <p><code>OlapWrapper</code> duplicates the functionality of the
 * <code>java.sql.Wrapper</code> interface (introduced in JDBC 4.0), making
 * this functionality available to olap4j clients running in a JDBC 3.0
 * environment. For code which will run only on JDBC 4.0 and later, Wrapper can
 * be used, and OlapWrapper can be ignored.</p>
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
 * @version $Id$
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
