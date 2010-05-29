/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.metadata.*;

import java.util.List;

/**
 * Description of structure of a particular axis of an {@link CellSet}.
 *
 * <p>For example, in the MDX statement</p>
 *
 * <blockquote>
 * <pre>
 * SELECT
 *   {[Measures].Members} ON COLUMNS,
 *   CrossJoin([Store].Members, [Gender].Children)
 *   DIMENSION PROPERTIES
 *      MEMBER_ORDINAL,
 *      MEMBER_UNIQUE_NAME,
 *      DISPLAY_INFO ON ROWS
 * FROM [Sales]
 * </pre>
 * </blockquote>
 *
 * <p>the ROWS axis is described by the following metadata:</p>
 *
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Value</th>
 * </tr>
 * <tr>
 * <td>hierarchies</td>
 * <td>{[Store], [Gender]}</td>
 * </tr>
 * <tr>
 * <td>properties</td>
 * <td>{MEMBER_ORDINAL, MEMBER_UNIQUE_NAME, DISPLAY_INFO}</td>
 * </tr>
 * </table>
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 23, 2006
 */
public interface CellSetAxisMetaData {
    /**
     * Returns the definition of the axis. Typical values are
     * ({@link Axis#FILTER}, {@link Axis#COLUMNS}, {@link Axis#ROWS}, and so
     * forth.)
     *
     * @return the Axis
     */
    Axis getAxisOrdinal();

    /**
     * Returns the hierarchies which are mapped onto this axis.
     *
     * @return list of hierarchies on this Axis
     */
    List<Hierarchy> getHierarchies();

    /**
     * Returns the member properties which are returned on this axis.
     *
     * <p>This method does not return a {@link NamedList} because the names of
     * the properties are not necessarily unique; for example, there might be
     * two hierarchies on the axis, each of which returns the DISPLAY_INFO
     * property.</p>
     *
     * @return list of member properties on this Axis
     */
    List<Property> getProperties();
}

// End CellSetAxisMetaData.java
