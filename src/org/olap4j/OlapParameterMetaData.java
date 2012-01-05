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
