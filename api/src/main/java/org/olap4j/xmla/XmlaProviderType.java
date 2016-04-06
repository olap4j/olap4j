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
 * XML for Analysis entity representing a Provider Type.
 *
 * <p>Corresponds to the XML/A {@code DBSCHEMA_PROVIDER_TYPES} schema
 * rowset.</p>
 */
public class XmlaProviderType extends Entity {
    public static final XmlaProviderType INSTANCE =
        new XmlaProviderType();

    public RowsetDefinition def() {
        return RowsetDefinition.DBSCHEMA_PROVIDER_TYPES;
    }

    public List<Column> columns() {
        return list(
            TypeName,
            DataType,
            ColumnSize,
            LiteralPrefix,
            LiteralSuffix,
            IsNullable,
            CaseSensitive,
            Searchable,
            UnsignedAttribute,
            FixedPrecScale,
            AutoUniqueValue,
            IsLong,
            BestMatch);
    }

    public List<Column> sortColumns() {
        return list(
            DataType);
    }

    public final Column TypeName =
        new Column(
            "TYPE_NAME",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The provider-specific data type name.");
    public final Column DataType =
        new Column(
            "DATA_TYPE",
            XmlaType.UnsignedShort.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The indicator of the data type.");
    public final Column ColumnSize =
        new Column(
            "COLUMN_SIZE",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.REQUIRED,
            "The length of a non-numeric column. If the data type is "
            + "numeric, this is the upper bound on the maximum precision "
            + "of the data type.");
    public final Column LiteralPrefix =
        new Column(
            "LITERAL_PREFIX",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The character or characters used to prefix a literal of this "
            + "type in a text command.");
    public final Column LiteralSuffix =
        new Column(
            "LITERAL_SUFFIX",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The character or characters used to suffix a literal of this "
            + "type in a text command.");
    public final Column IsNullable =
        new Column(
            "IS_NULLABLE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether the data type is nullable. "
            + "NULL-- indicates that it is not known whether the data type "
            + "is nullable.");
    public final Column CaseSensitive =
        new Column(
            "CASE_SENSITIVE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether the data type is a "
            + "characters type and case-sensitive.");
    public final Column Searchable =
        new Column(
            "SEARCHABLE",
            XmlaType.UnsignedInteger.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "An integer indicating how the data type can be used in "
            + "searches if the provider supports ICommandText; otherwise, "
            + "NULL.");
    public final Column UnsignedAttribute =
        new Column(
            "UNSIGNED_ATTRIBUTE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether the data type is unsigned.");
    public final Column FixedPrecScale =
        new Column(
            "FIXED_PREC_SCALE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether the data type has a fixed "
            + "precision and scale.");
    public final Column AutoUniqueValue =
        new Column(
            "AUTO_UNIQUE_VALUE",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether the data type is "
            + "autoincrementing.");
    public final Column IsLong =
        new Column(
            "IS_LONG",
            XmlaType.Boolean.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether the data type is a binary "
            + "large object (BLOB) and has very long data.");
    public final Column BestMatch =
        new Column(
            "BEST_MATCH",
            XmlaType.Boolean.scalar(),
            Column.RESTRICTION,
            Column.OPTIONAL,
            "A Boolean that indicates whether the data type is a best "
            + "match.");
}

// End XmlaProviderType.java
