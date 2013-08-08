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

import static org.olap4j.xmla.Column.Restriction;

/**
 * XML for Analysis entity representing a Measure Group.
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_MEASUREGROUPS} schema rowset.</p>
 */
public class XmlaMeasureGroup extends Entity {
    public static final XmlaMeasureGroup INSTANCE =
        new XmlaMeasureGroup();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_MEASUREGROUPS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            MeasuregroupName,
            Description,
            IsWriteEnabled,
            MeasuregroupCaption);
    }

    public List<Column> sortColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            MeasuregroupName);
    }

    @Override
    List<Column> restrictionColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            MeasuregroupName);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the catalog to which this measure group belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the schema to which this measure group belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "Name of the cube to which this measure group belongs.");
    public final Column MeasuregroupName =
        new Column(
            "MEASUREGROUP_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "The name of the measure group.");
    public final Column Description =
        new Column(
            "DESCRIPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.REQUIRED,
            "A human-readable description of the measure group.");
    public final Column IsWriteEnabled =
        new Column(
            "IS_WRITE_ENABLED",
            XmlaType.Boolean.scalar(),
            Restriction.NO,
            Column.REQUIRED,
            "A Boolean that indicates whether the measure group is "
            + "write-enabled. Returns true if the measure group is write "
            + "enabled.");
    public final Column MeasuregroupCaption =
        new Column(
            "MEASUREGROUP_CAPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.REQUIRED,
            "The display caption for the measure group.");
}

// End XmlaMeasureGroup.java
