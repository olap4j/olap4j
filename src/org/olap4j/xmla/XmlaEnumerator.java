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

import java.util.Arrays;
import java.util.List;

/**
 * XML for Analysis entity representing an Enumerator.
 *
 * <p>Corresponds to the XML/A {@code DISCOVER_ENUMERATORS} schema rowset.</p>
 */
public class XmlaEnumerator extends Entity {
    public static final XmlaEnumerator INSTANCE =
        new XmlaEnumerator();

    public RowsetDefinition def() {
        return RowsetDefinition.DISCOVER_ENUMERATORS;
    }

    List<Column> columns() {
        return list(
            EnumName,
            EnumDescription,
            EnumType,
            ElementName,
            ElementDescription,
            ElementValue);
    }

    List<Column> sortColumns() {
        return list(); // not sorted
    }

    public final Column EnumName =
        new Column(
            "EnumName",
            XmlaType.StringArray,
            null,
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the enumerator that contains a set of values.");
    public final Column EnumDescription =
        new Column(
            "EnumDescription",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A localizable description of the enumerator.");
    public final Column EnumType =
        new Column(
            "EnumType",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The data type of the Enum values.");
    public final Column ElementName =
        new Column(
            "ElementName",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The name of one of the value elements in the enumerator set.\n"
            + "Example: TDP");
    public final Column ElementDescription =
        new Column(
            "ElementDescription",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A localizable description of the element (optional).");
    public final Column ElementValue =
        new Column(
            "ElementValue",
            XmlaType.String,
            null,
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The value of the element.\n" + "Example: 01");
}

// End XmlaEnumerator.java
