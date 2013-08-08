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
import org.olap4j.metadata.DictionaryImpl;
import org.olap4j.metadata.XmlaConstant;

import java.util.List;

import static org.olap4j.xmla.Column.Restriction;

/**
 * XML for Analysis entity representing a key performance indicator (KPI).
 *
 * <p>Corresponds to the XML/A {@code MDSCHEMA_KPIS} schema rowset.</p>
 */
public class XmlaKpi extends Entity {
    public static final XmlaKpi INSTANCE =
        new XmlaKpi();

    public RowsetDefinition def() {
        return RowsetDefinition.MDSCHEMA_KPIS;
    }

    public List<Column> columns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            MeasuregroupName,
            KpiName,
            KpiCaption,
            KpiDescription,
            KpiDisplayFolder,
            KpiValue,
            KpiGoal,
            KpiStatus,
            KpiTrend,
            KpiStatusGraphic,
            KpiTrendGraphic,
            KpiWeight,
            KpiCurrentTimeMember,
            KpiParentKpiName,
            Scope,
            Annotations);
    }

    public List<Column> sortColumns() {
        return list(); // not sorted
    }

    @Override
    List<Column> restrictionColumns() {
        return list(
            CatalogName,
            SchemaName,
            CubeName,
            KpiName,
            CubeSource);
    }

    public final Column CatalogName =
        new Column(
            "CATALOG_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the catalog to which this cube belongs.");
    public final Column SchemaName =
        new Column(
            "SCHEMA_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.OPTIONAL,
            "The name of the schema to which this cube belongs.");
    public final Column CubeName =
        new Column(
            "CUBE_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "Name of the cube or dimension. Dimension names are prefaced by a "
            + "dollar sign ($) symbol.\n"
            + "Note: Only server and database administrators have permissions "
            + "to see cubes created from a dimension.");
    public final Column MeasuregroupName =
        new Column(
            "MEASUREGROUP_NAME",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The associated measure group for the KPI.\n"
            + "You can use this column to determine the dimensionality of the "
            + "KPI. If NULL, the KPI will be dimensioned by all measure "
            + "groups.\n"
            + "The default value is NULL.\n");
    public final Column KpiName =
        new Column(
            "KPI_NAME",
            XmlaType.String.scalar(),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "The name of the KPI.");
    public final Column KpiCaption =
        new Column(
            "KPI_CAPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.REQUIRED,
            "A label or caption associated with the KPI. Used primarily for "
            + "display purposes. If a caption does not exist, KPI_NAME is "
            + "returned.");
    public final Column KpiDescription =
        new Column(
            "KPI_DESCRIPTION",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "A human-readable description of the KPI.");
    public final Column KpiDisplayFolder =
        new Column(
            "KPI_DISPLAY_FOLDER",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "A string that identifies the path of the display folder that the "
            + "client application uses to show the member. The folder level "
            + "separator is defined by the client application. For the tools "
            + "and clients supplied by Analysis Services, the backslash "
            + "(\\) is the level separator. To provide multiple display "
            + "folders, use a semicolon (;) to separate the folders.");
    public final Column KpiValue =
        new Column(
            "KPI_VALUE",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The unique name of the member in the measures dimension for the "
            + "KPI Value.");
    public final Column KpiGoal =
        new Column(
            "KPI_GOAL",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The unique name of the member in the measures dimension for the "
            + "KPI Goal.\n"
            + "Returns NULL if there is no Goal defined.");
    public final Column KpiStatus =
        new Column(
            "KPI_STATUS",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The unique name of the member in the measures dimension for the KPI Status.\n"
            + "Returns NULL if there is no Status defined.");
    public final Column KpiTrend =
        new Column(
            "KPI_TREND",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The unique name of the member in the measures dimension for the KPI Trend.\n"
            + "Returns NULL if there is no Trend defined.");
    public final Column KpiStatusGraphic =
        new Column(
            "KPI_STATUS_GRAPHIC",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The default graphical representation of the KPI.");
    public final Column KpiTrendGraphic =
        new Column(
            "KPI_TREND_GRAPHIC",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The default graphical representation of the KPI.");
    public final Column KpiWeight =
        new Column(
            "KPI_WEIGHT",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The unique name of the member in the measures dimension for the KPI Weight.");
    public final Column KpiCurrentTimeMember =
        new Column(
            "KPI_CURRENT_TIME_MEMBER",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The unique name of the member in the time dimension that defines the temporal context of the KPI.\n"
            + "Returns NULL if there is no Time member defined.");
    public final Column KpiParentKpiName =
        new Column(
            "KPI_PARENT_KPI_NAME",
            XmlaType.String.scalar(),
            Restriction.NO,
            Column.OPTIONAL,
            "The name of the parent KPI.");
    public final Column Scope =
        new Column(
            "SCOPE",
            XmlaType.Integer.of(Enumeration.KPI_SCOPE),
            Restriction.OPTIONAL,
            Column.REQUIRED,
            "The scope of the KPI. The KPI can be a session KPI or global KPI.");
    public final Column Annotations =
        new Column(
            "ANNOTATIONS",
            XmlaType.String.scalar(),
            Column.NOT_RESTRICTION,
            Column.OPTIONAL,
            "A set of notes, in XML format.");

    // Only a restriction.
    public final Column CubeSource =
        new Column(
            "CUBE_SOURCE",
            XmlaType.UnsignedShort.scalar(),
            Column.Restriction.OPTIONAL.of(
                Enumeration.CUBE_TYPE, Cube.Type.CUBE),
            Column.OPTIONAL,
            null);

    /** The scope of a KPI. */
    enum ScopeEnum implements XmlaConstant {
        /**
         * The KPI has global scope.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MDKPI_SCOPE_GLOBAL</code> (1).</p>
         */
        GLOBAL(1),

        /**
         * The KPI has session scope.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MDKPI_SCOPE_SESSION</code> (2).</p>
         */
        SESSION(2);

        private final int xmlaOrdinal;

        /** Per {@link XmlaConstant}. */
        public static final Dictionary<ScopeEnum> DICTIONARY =
            DictionaryImpl.forClass(ScopeEnum.class);

        ScopeEnum(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MDKPI_SCOPE_" + name();
        }

        public String getDescription() {
            return null;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }
}

// End XmlaKpi.java
