/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.metadata.Property;
import org.olap4j.metadata.Cube;

import java.sql.ResultSetMetaData;
import java.util.List;

/**
 * <code>OlapResultSetMetaData</code> ...
 *
 * @author jhyde
 * @version $Id: $
 * @since Oct 23, 2006
 */
public interface OlapResultSetMetaData extends ResultSetMetaData {
    /**
     * Returns a list of properties which each ResultCell may have.
     */
    List<Property> getCellProperties();

    /**
     * Returns the Cube which was referenced in this statement.
     */
    Cube getCube();

    /**
     * Returns a list of metadata describing each result axis.
     */
    List<OlapResultSetAxisMetaData> getAxesMetaData();
}

// End OlapResultSetMetaData.java
