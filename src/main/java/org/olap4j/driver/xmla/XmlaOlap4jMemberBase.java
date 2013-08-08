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
package org.olap4j.driver.xmla;

import org.olap4j.metadata.Member;
import org.olap4j.metadata.Property;

import java.util.Map;

/**
 * Core interface shared by all implementations of {@link Member} in the XMLA
 * driver.
 *
 * <p>This interface is private within the {@code org.olap4j.driver.xmla}
 * package. The methods in this interface are NOT part of the public olap4j API.
 *
 * @author jhyde
 * @since Nov 1, 2008
 */
interface XmlaOlap4jMemberBase
    extends Member
{
    /**
     * Returns the cube this member belongs to.
     */
    XmlaOlap4jCube getCube();

    /**
     * Returns the connection that created this member.
     */
    XmlaOlap4jConnection getConnection();

    /**
     * Returns the catalog that this member belongs to.
     */
    XmlaOlap4jCatalog getCatalog();

    /**
     * Returns the set of property values, keyed by property.
     */
    Map<Property, Object> getPropertyValueMap();
}

// End XmlaOlap4jMemberBase.java
