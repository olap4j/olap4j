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
package org.olap4j.impl;

/**
 * Interface which describes an object which has a name, for the purposes of
 * creating an implementation, {@link NamedListImpl} of
 * {@link org.olap4j.metadata.NamedList} which works on such objects.
 *
 * @author jhyde
 * @version $Id$
 * @since May 23, 2007
 */
public interface Named {
    /**
     * Returns the name of this object.
     *
     * @return name of this object
     */
    String getName();
}

// End Named.java
