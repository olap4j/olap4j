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

import java.util.List;

import static org.olap4j.xmla.Column.Restriction;

/**
 * XML for Analysis entity representing a Set.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_SETS} schema rowset.</p>
 */
public class XmlaSet extends Entity {
    public static final XmlaSet INSTANCE =
        new XmlaSet();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_SETS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            SetName,
            Scope,
            Description,
            Expression,
            Dimensions,
            SetCaption,
            SetDisplayFolder,
            SetEvaluationContext,
            Annotations);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName);
    }

    @Override
    List<Column> restrictionColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            SetName,
            Scope,
            HierarchyUniqueName,
            CubeSource);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Column.Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the database.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the schema.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "The name of the cube.");
    public final Column SetName =
        new Column(
            "SET_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "The name of the set.");
    public final Column SetCaption =
        new Column(
            "SET_CAPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "A label or caption associated with the set. The label or caption "
            + "is used primarily for display purposes.");
    public final Column Scope =
        new Column(
            "SCOPE",
            XmlaType.Integer.of(Enumeration.SET_SCOPE),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "The scope of the set.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "A human-readable description of the set.");
    public final Column Expression =
        new Column(
            "EXPRESSION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The expression for the set.");
    public final Column Dimensions =
        new Column(
            "DIMENSIONS",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "A comma delimited list of hierarchies included in the set.");
    public final Column SetDisplayFolder =
        new Column(
            "SET_DISPLAY_FOLDER",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "A string that identifies the path of the display folder that the "
            + "client application uses to show the set. The folder level "
            + "separator is defined by the client application. For the tools "
            + "and clients supplied by Analysis Services, the backslash (\\) "
            + "is the level separator. To provide multiple display folders, "
            + "use a semicolon (;) to separate the folders.");
    public final Column SetEvaluationContext =
        new Column(
            "SET_EVALUATION_CONTEXT",
            XmlaType.Integer.of(Enumeration.SET_RESOLUTION),
            Restriction.NO,
            Column.OPTIONAL,
            "The context for the set. The set can be static or dynamic.");

    /** See {@link org.olap4j.metadata.NamedSet#getAnnotations()}.
     *
     * @since olap4j 2.0; mondrian extension (not in XMLA spec) */
    public final Column Annotations =
        new Column(
            "ANNOTATIONS",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A set of notes, in XML format.").extension();

    // Only a restriction.
    public final Column CubeSource =
        new Column(
            "CUBE_SOURCE",
            XmlaType.UnsignedShort.scalar(),
            Column.Restriction.OPTIONAL.of(
                Enumeration.CUBE_TYPE, Cube.Type.CUBE),
            Column.OPTIONAL,
            null);

    // Only a restriction.
    public final Column HierarchyUniqueName =
        new Column(
            "HIERARCHY_UNIQUE_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            true,
            "Note: Only one hierarchy can be included, and only those named "
            + "sets whose hierarchies exactly match the restriction are "
            + "returned.");
}

// End XmlaSet.java
