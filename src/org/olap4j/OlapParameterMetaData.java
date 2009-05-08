/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.type.Type;

import java.sql.ParameterMetaData;

/**
 * Extension to {@link ParameterMetaData} for parameters of OLAP statements.
 *
 * <p>Chief differences:
 * <ul>
 * <li>An OLAP statement parameter has a name.
 * <li>An OLAP statement parameter may be a member. If this is the case,
 *     the {@link #getParameterType(int)} method returns
 *     {@link java.sql.Types#OTHER}.
 * <li>An additional method {@link #getParameterOlapType(int)} provides extra
 *     type information; in particular, the hierarchy that a member parameter
 *     belongs to.
 * </ul>
 *
 * <p>Parameters to an OLAP statement must have default values, and therefore
 * it is not necessary to set every parameter.
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 12, 2006
 */
public interface OlapParameterMetaData extends ParameterMetaData {
    /**
     * Returns the name of this parameter.
     *
     * @param param the first parameter is 1, the second is 2, ...
     * @return parameter name
     * @exception OlapException if a database access error occurs
     */
    String getParameterName(int param) throws OlapException;

    /**
     * Retrieves the designated parameter's OLAP type.
     *
     * @param param the first parameter is 1, the second is 2, ...
     * @return OLAP type
     * @exception OlapException if a database access error occurs
     */
    Type getParameterOlapType(int param) throws OlapException;
}

// End OlapParameterMetaData.java
