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

import org.olap4j.metadata.Cube;
import org.olap4j.metadata.XmlaConstants;

import java.util.List;

import static org.olap4j.xmla.Column.Restriction;

/**
 * XML for Analysis entity representing an Action.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_ACTIONS} schema rowset.</p>
 */
public class XmlaAction extends Entity {
    public static final XmlaAction INSTANCE =
        new XmlaAction();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_ACTIONS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            ActionName,
            Coordinate,
            CoordinateType);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            ActionName);
    }

    @Override
    List<Column> restrictionColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            ActionName,
            ActionType,
            Coordinate,
            CoordinateType,
            Invocation,
            CubeSource);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the catalog to which this action belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the schema to which this action belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Restriction.MANDATORY,
            Column.REQUIRED,
            "The name of the cube to which this action belongs.");
    public final Column ActionName =
        new Column(
            "ACTION_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "The name of the action.");
    public final Column ActionType =
        new Column(
            "ACTION_TYPE",
            XmlaType.Integer.of(Enumeration.ACTION_TYPE),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "The name of the action.");
    public final Column Coordinate =
        new Column(
            "COORDINATE",
            XmlaType.String.scalar(),
            Restriction.MANDATORY,
            Column.REQUIRED,
            "A Multidimensional Expressions (MDX) expression that specifies an "
            + "object or a coordinate in the multidimensional space in which "
            + "the action is performed. It is the responsibility of the client "
            + "application to provide the value of this restriction column.\n"
            + "The CORDINATE must resolve to the object specified in "
            + "COORDINATE_TYPE.");
    public final Column CoordinateType =
        new Column(
            "COORDINATE_TYPE",
            XmlaType.Integer.of(Enumeration.COORDINATE_TYPE),
            Restriction.MANDATORY,
            Column.REQUIRED,
            "A bitmap that specifies how the COORDINATE restriction column is"
            + "interpreted.");
    public final Column ActionCaption =
        new Column(
            "ACTION_CAPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The action name if no caption was specified and no translations "
            + "were specified in the DDL.\n"
            + "If a caption or translations were specified, and CaptionIsMDX "
            + "is false, one of the following strings:\n"
            + "The translation for the appropriate language.\n"
            + "The specified caption if no translation was found for the "
            + "specified language.\n"
            + "The action name if no translation was found and the caption "
            + "was not specified in DDL.\n"
            + "If a caption or translations were specified, and CaptionIsMDX "
            + "is true, the string resulting from finding the appropriate "
            + "translation for the specified language or the specified "
            + "translation in the DDL caption, and calculating the formula "
            + "to create the string.\n"
            + "If the action was specified in MDX Script, there are no "
            + "translations and the caption is always treated as MDX "
            + "expression.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "A user-friendly description of the action.");
    public final Column Content =
        new Column(
            "CONTENT",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The expression or content of the action that is to be run.");
    public final Column Application =
        new Column(
            "APPLICATION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The name of the application that is to be used to run the "
            + "action.");
    public final Column Invocation =
        new Column(
            "INVOCATION",
            XmlaType.Integer.of(Enumeration.INVOCATION),
            Restriction.OPTIONAL.of(
                Enumeration.INVOCATION, XmlaConstants.Invocation.INTERACTIVE),
            Column.OPTIONAL,
            "A user-friendly description of the action.");

    // Only a restriction.
    public final Column CubeSource =
        new Column(
            "CUBE_SOURCE",
            XmlaType.UnsignedShort.scalar(),
            Column.Restriction.OPTIONAL.of(
                Enumeration.CUBE_TYPE, Cube.Type.CUBE),
            Column.OPTIONAL,
            null);

}

// End XmlaAction.java
