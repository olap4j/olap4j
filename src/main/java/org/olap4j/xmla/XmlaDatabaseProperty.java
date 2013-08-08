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

import java.util.List;

/**
 * XML for Analysis entity representing a Property of a data provider.
 *
 * <p>Corresponds to the XML/A {@code DISCOVER_PROPERTIES} schema rowset.</p>
 */
public class XmlaDatabaseProperty extends Entity {
    public static final XmlaDatabaseProperty INSTANCE =
        new XmlaDatabaseProperty();

    public RowsetDefinition def() {
        return RowsetDefinition.DISCOVER_PROPERTIES;
    }

    List<Column> columns() {
        return list(
            PropertyName,
            PropertyDescription,
            PropertyType,
            PropertyAccessType,
            IsRequired,
            Value);
    }

    List<Column> sortColumns() {
        return list(); // not sorted
    }

    public final Column PropertyName =
        new Column(
            "PropertyName",
            XmlaType.StringSometimesArray.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the property.");
    public final Column PropertyDescription =
        new Column(
            "PropertyDescription",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A localizable text description of the property.");
    public final Column PropertyType =
        new Column(
            "PropertyType",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The XML data type of the property.");
    public final Column PropertyAccessType =
        new Column(
            "PropertyAccessType",
            XmlaType.EnumString.of(Enumeration.ACCESS),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The access for the property. The value can be Read, Write, or "
            + "ReadWrite.");
    public final Column IsRequired =
        new Column(
            "IsRequired",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether a property is required. "
            + "True if a property is required; false if it is not required.");
    public final Column Value =
        new Column(
            "Value",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The current value of the property.");
}

// End XmlaDatabaseProperty.java
