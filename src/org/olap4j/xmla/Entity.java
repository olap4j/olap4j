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
package org.olap4j.xmla;

import java.util.*;

/**
 * Entity in the XML for Analysis meta-model.
 *
 * <p>An entity generally has an associated schema rowset, and a method by
 * which to query it. For example, the {@link XmlaCube} entity corresponds
 * to the {@code MDSCHEMA_CUBES} schema rowset and the olap4j metadata class
 * {@link org.olap4j.metadata.Cube}.</p>
*/
public abstract class Entity {
    protected static List<Column> list(Column... columns) {
        switch (columns.length) {
        case 0:
            return Collections.emptyList();
        case 1:
            return Collections.singletonList(columns[0]);
        default:
            return Collections.unmodifiableList(
                Arrays.asList(columns));
        }
    }

    // These methods create a new list each time; they should be called
    // only when constructing static objects, not at runtime.

    abstract List<Column> columns();
    abstract List<Column> sortColumns();
    public abstract RowsetDefinition def();
}

// End Entity.java
