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

import org.olap4j.metadata.XmlaConstants;

import java.util.List;

/**
 * XML for Analysis entity representing a Literal.
 *
 * <p>Corresponds to the XML/A {@code DISCOVER_LITERALS} method.</p>
*/
public class XmlaLiteral extends Entity {
    public static final XmlaLiteral INSTANCE =
        new XmlaLiteral();

    public RowsetDefinition def() {
        return RowsetDefinition.DISCOVER_LITERALS;
    }

    List<Column> columns() {
        return list(
            LiteralName,
            LiteralValue,
            LiteralInvalidChars,
            LiteralInvalidStartingChars,
            LiteralMaxLength,
            LiteralNameEnumValue);
    }

    List<Column> sortColumns() {
        return list(); // not sorted
    }

    public final Column LiteralName =
        new Column(
            "LiteralName",
            XmlaType.StringSometimesArray.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "The name of the literal described in the row.\n"
            + "Example: DBLITERAL_LIKE_PERCENT");
    public final Column LiteralValue =
        new Column(
            "LiteralValue",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "Contains the actual literal value.\n"
            + "Example, if LiteralName is DBLITERAL_LIKE_PERCENT and the "
            + "percent character (%) is used to match zero or more "
            + "characters in a LIKE clause, this column's value would be "
            + "\"%\".");
    public final Column LiteralInvalidChars =
        new Column(
            "LiteralInvalidChars",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The characters, in the literal, that are not valid.\n"
            + "For example, if table names can contain anything other than "
            + "a numeric character, this string would be \"0123456789\".");

    public final Column LiteralInvalidStartingChars =
        new Column(
            "LiteralInvalidStartingChars",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The characters that are not valid as the first character of "
            + "the literal. If the literal can start with any valid "
            + "character, this is null.");

    public final Column LiteralMaxLength =
        new Column(
            "LiteralMaxLength",
            XmlaType.Integer.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "The maximum number of characters in the literal. If there is "
            + "no maximum or the maximum is unknown, the value is -1.");

    public final Column LiteralNameEnumValue =
        new Column(
            "LiteralNameEnumValue",
            XmlaType.Integer.of(XmlaConstants.Literal.class),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            null);

}

// End XmlaLiteral.java
