/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2008-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import javax.sql.DataSource;
import java.sql.SQLException;

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
 * @version $Id: $
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
