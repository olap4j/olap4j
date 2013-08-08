/*
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
import javax.sql.DataSource;

/**
 * <p>A factory for connections to the physical OLAP data source that this
 * <code>OlapDataSource</code> object represents.
 *
 * <p><code>OlapDataSource</code> is a refinement of
 * {@link javax.sql.DataSource} whose <code>getConnection</code> methods
 * return {@link org.olap4j.OlapConnection} objects rather than mere
 * {@link java.sql.Connection}s.
 *
 * @author jhyde
 * @since Mar 25, 2008
 */
public interface OlapDataSource extends DataSource {

    // override with more specific return type
    OlapConnection getConnection() throws SQLException;

    // override with more specific return type
    OlapConnection getConnection(
        String username,
        String password)
        throws SQLException;
}

// End OlapDataSource.java
