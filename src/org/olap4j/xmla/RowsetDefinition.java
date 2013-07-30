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

import org.olap4j.metadata.DictionaryImpl;
import org.olap4j.metadata.XmlaConstant;

import java.util.*;

/**
 * <code>RowsetDefinition</code> defines a rowset, including the columns it
 * should contain.
 *
 * <p>See "XML for Analysis Rowsets", page 38 of the XML for Analysis
 * Specification, version 1.1.
 *
 * @author jhyde
 */
public enum RowsetDefinition implements XmlaConstant {
    /**
     * Returns a list of XML for Analysis data sources
     * available on the server or Web Service. (For an
     * example of how these may be published, see
     * "XML for Analysis Implementation Walkthrough"
     * in the XML for Analysis specification.)
     *
     *  http://msdn2.microsoft.com/en-us/library/ms126129(SQL.90).aspx
     *
     *
     * restrictions
     *
     * Not supported
     */
    DISCOVER_DATASOURCES(
        0,
        "Returns a list of XML for Analysis data sources available on the "
        + "server or Web Service.",
        XmlaDatasource.INSTANCE),

    /**
     * Note that SQL Server also returns the data-mining columns.
     *
     *
     * restrictions
     *
     * Not supported
     */
    DISCOVER_SCHEMA_ROWSETS(
        2,
        "Returns the names, values, and other information of all supported "
        + "RequestType enumeration values.",
        XmlaSchemaRowset.INSTANCE),

    /**
     *
     *
     *
     * restrictions
     *
     * Not supported
     */
    DISCOVER_ENUMERATORS(
        3,
        "Returns a list of names, data types, and enumeration values for "
        + "enumerators supported by the provider of a specific data source.",
        XmlaEnumerator.INSTANCE),

    /**
     *
     *
     *
     * restrictions
     *
     * Not supported
     */
    DISCOVER_PROPERTIES(
        1,
        "Returns a list of information and values about the requested "
        + "properties that are supported by the specified data source "
        + "provider.",
        XmlaDatabaseProperty.INSTANCE),

    /**
     *
     *
     *
     * restrictions
     *
     * Not supported
     */
    DISCOVER_KEYWORDS(
        4,
        "Returns an XML list of keywords reserved by the provider.",
        XmlaKeyword.INSTANCE),

    /**
     *
     *
     *
     * restrictions
     *
     * Not supported
     */
    DISCOVER_LITERALS(
        5,
        "Returns information about literals supported by the provider.",
        XmlaLiteral.INSTANCE),

    /**
     *
     *
     *
     * restrictions
     *
     * Not supported
     */
    DBSCHEMA_CATALOGS(
        6,
        "Identifies the physical attributes associated with catalogs "
        + "accessible from the provider.",
        XmlaCatalog.INSTANCE),

    /**
     *
     *
     *
     * restrictions
     *
     * Not supported
     *    COLUMN_OLAP_TYPE
     */
    DBSCHEMA_COLUMNS(
        7, null, XmlaColumn.INSTANCE),

    /**
     *
     *
     *
     * restrictions
     *
     * Not supported
     */
    DBSCHEMA_PROVIDER_TYPES(
        8, null, XmlaProviderType.INSTANCE),

    DBSCHEMA_SCHEMATA(
        8, null, XmlaSchema.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126299(SQL.90).aspx
     *
     * restrictions:
     *   TABLE_CATALOG Optional
     *   TABLE_SCHEMA Optional
     *   TABLE_NAME Optional
     *   TABLE_TYPE Optional
     *   TABLE_OLAP_TYPE Optional
     *
     * Not supported
     */
    DBSCHEMA_TABLES(
        9, null, XmlaTable.INSTANCE),

    /**
     * http://msdn.microsoft.com/library/en-us/oledb/htm/
     * oledbtables_info_rowset.asp
     *
     *
     * restrictions
     *
     * Not supported
     */
    DBSCHEMA_TABLES_INFO(
        10, null, XmlaTableInfo.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126032(SQL.90).aspx
     *
     * restrictions
     *   CATALOG_NAME Optional
     *   SCHEMA_NAME Optional
     *   CUBE_NAME Mandatory
     *   ACTION_NAME Optional
     *   ACTION_TYPE Optional
     *   COORDINATE Mandatory
     *   COORDINATE_TYPE Mandatory
     *   INVOCATION
     *      (Optional) The INVOCATION restriction column defaults to the
     *      value of MDACTION_INVOCATION_INTERACTIVE. To retrieve all
     *      actions, use the MDACTION_INVOCATION_ALL value in the
     *      INVOCATION restriction column.
     *   CUBE_SOURCE
     *      (Optional) A bitmap with one of the following valid values:
     *
     *      1 CUBE
     *      2 DIMENSION
     *
     *      Default restriction is a value of 1.
     *
     * Not supported
     */
    MDSCHEMA_ACTIONS(
        11, null, XmlaAction.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126271(SQL.90).aspx
     *
     * restrictions
     *   CATALOG_NAME Optional.
     *   SCHEMA_NAME Optional.
     *   CUBE_NAME Optional.
     *   CUBE_TYPE
     *      (Optional) A bitmap with one of these valid values:
     *      1 CUBE
     *      2 DIMENSION
     *     Default restriction is a value of 1.
     *   BASE_CUBE_NAME Optional.
     *
     * Not supported
     *   CREATED_ON
     *   LAST_SCHEMA_UPDATE
     *   SCHEMA_UPDATED_BY
     *   LAST_DATA_UPDATE
     *   DATA_UPDATED_BY
     *   ANNOTATIONS
     */
    MDSCHEMA_CUBES(
        12, null, XmlaCube.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126180(SQL.90).aspx
     * http://msdn2.microsoft.com/en-us/library/ms126180.aspx
     *
     * restrictions
     *    CATALOG_NAME Optional.
     *    SCHEMA_NAME Optional.
     *    CUBE_NAME Optional.
     *    DIMENSION_NAME Optional.
     *    DIMENSION_UNIQUE_NAME Optional.
     *    CUBE_SOURCE (Optional) A bitmap with one of the following valid
     *    values:
     *      1 CUBE
     *      2 DIMENSION
     *    Default restriction is a value of 1.
     *
     *    DIMENSION_VISIBILITY (Optional) A bitmap with one of the following
     *    valid values:
     *      1 Visible
     *      2 Not visible
     *    Default restriction is a value of 1.
     */
    MDSCHEMA_DIMENSIONS(
        13, null, XmlaDimension.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126257(SQL.90).aspx
     *
     * restrictions
     *   LIBRARY_NAME Optional.
     *   INTERFACE_NAME Optional.
     *   FUNCTION_NAME Optional.
     *   ORIGIN Optional.
     *
     * Not supported
     *  DLL_NAME
     *    Optional
     *  HELP_FILE
     *    Optional
     *  HELP_CONTEXT
     *    Optional
     *    - SQL Server xml schema says that this must be present
     *  OBJECT
     *    Optional
     *  CAPTION The display caption for the function.
     */
    MDSCHEMA_FUNCTIONS(
        14, null, XmlaFunction.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126062(SQL.90).aspx
     *
     * restrictions
     *    CATALOG_NAME Optional.
     *    SCHEMA_NAME Optional.
     *    CUBE_NAME Optional.
     *    DIMENSION_UNIQUE_NAME Optional.
     *    HIERARCHY_NAME Optional.
     *    HIERARCHY_UNIQUE_NAME Optional.
     *    HIERARCHY_ORIGIN
     *       (Optional) A default restriction is in effect
     *       on MD_USER_DEFINED and MD_SYSTEM_ENABLED.
     *    CUBE_SOURCE
     *      (Optional) A bitmap with one of the following valid values:
     *      1 CUBE
     *      2 DIMENSION
     *      Default restriction is a value of 1.
     *    HIERARCHY_VISIBILITY
     *      (Optional) A bitmap with one of the following valid values:
     *      1 Visible
     *      2 Not visible
     *      Default restriction is a value of 1.
     *
     * Not supported
     *  HIERARCHY_ORIGIN
     *  HIERARCHY_DISPLAY_FOLDER
     *  INSTANCE_SELECTION
     */
    MDSCHEMA_HIERARCHIES(
        15, null, XmlaHierarchy.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126038(SQL.90).aspx
     *
     * restriction
     *   CATALOG_NAME Optional.
     *   SCHEMA_NAME Optional.
     *   CUBE_NAME Optional.
     *   DIMENSION_UNIQUE_NAME Optional.
     *   HIERARCHY_UNIQUE_NAME Optional.
     *   LEVEL_NAME Optional.
     *   LEVEL_UNIQUE_NAME Optional.
     *   LEVEL_ORIGIN
     *       (Optional) A default restriction is in effect
     *       on MD_USER_DEFINED and MD_SYSTEM_ENABLED
     *   CUBE_SOURCE
     *       (Optional) A bitmap with one of the following valid values:
     *       1 CUBE
     *       2 DIMENSION
     *       Default restriction is a value of 1.
     *   LEVEL_VISIBILITY
     *       (Optional) A bitmap with one of the following values:
     *       1 Visible
     *       2 Not visible
     *       Default restriction is a value of 1.
     *
     * Not supported
     *  CUSTOM_ROLLUP_SETTINGS
     *  LEVEL_UNIQUE_SETTINGS
     *  LEVEL_ORDERING_PROPERTY
     *  LEVEL_DBTYPE
     *  LEVEL_MASTER_UNIQUE_NAME
     *  LEVEL_NAME_SQL_COLUMN_NAME Customers:(All)!NAME
     *  LEVEL_KEY_SQL_COLUMN_NAME Customers:(All)!KEY
     *  LEVEL_UNIQUE_NAME_SQL_COLUMN_NAME Customers:(All)!UNIQUE_NAME
     *  LEVEL_ATTRIBUTE_HIERARCHY_NAME
     *  LEVEL_KEY_CARDINALITY
     *  LEVEL_ORIGIN
     *
     */
    MDSCHEMA_LEVELS(
        16, null, XmlaLevel.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126250(SQL.90).aspx
     *
     * restrictions
     *   CATALOG_NAME Optional.
     *   SCHEMA_NAME Optional.
     *   CUBE_NAME Optional.
     *   MEASURE_NAME Optional.
     *   MEASURE_UNIQUE_NAME Optional.
     *   CUBE_SOURCE
     *     (Optional) A bitmap with one of the following valid values:
     *     1 CUBE
     *     2 DIMENSION
     *     Default restriction is a value of 1.
     *   MEASURE_VISIBILITY
     *     (Optional) A bitmap with one of the following valid values:
     *     1 Visible
     *     2 Not Visible
     *     Default restriction is a value of 1.
     *
     * Not supported
     *  MEASURE_GUID
     *  NUMERIC_PRECISION
     *  NUMERIC_SCALE
     *  MEASURE_UNITS
     *  EXPRESSION
     *  MEASURE_NAME_SQL_COLUMN_NAME
     *  MEASURE_UNQUALIFIED_CAPTION
     *  MEASUREGROUP_NAME
     *  MEASURE_DISPLAY_FOLDER
     *  DEFAULT_FORMAT_STRING
     */
    MDSCHEMA_MEASURES(
        17, null, XmlaMeasure.INSTANCE),

    /**
     *
     * http://msdn2.microsoft.com/es-es/library/ms126046.aspx
     *
     *
     * restrictions
     *   CATALOG_NAME Optional.
     *   SCHEMA_NAME Optional.
     *   CUBE_NAME Optional.
     *   DIMENSION_UNIQUE_NAME Optional.
     *   HIERARCHY_UNIQUE_NAME Optional.
     *   LEVEL_UNIQUE_NAME Optional.
     *   LEVEL_NUMBER Optional.
     *   MEMBER_NAME Optional.
     *   MEMBER_UNIQUE_NAME Optional.
     *   MEMBER_CAPTION Optional.
     *   MEMBER_TYPE Optional.
     *   TREE_OP (Optional) Only applies to a single member:
     *      MDTREEOP_ANCESTORS (0x20) returns all of the ancestors.
     *      MDTREEOP_CHILDREN (0x01) returns only the immediate children.
     *      MDTREEOP_SIBLINGS (0x02) returns members on the same level.
     *      MDTREEOP_PARENT (0x04) returns only the immediate parent.
     *      MDTREEOP_SELF (0x08) returns itself in the list of
     *                 returned rows.
     *      MDTREEOP_DESCENDANTS (0x10) returns all of the descendants.
     *   CUBE_SOURCE (Optional) A bitmap with one of the
     *      following valid values:
     *        1 CUBE
     *        2 DIMENSION
     *      Default restriction is a value of 1.
     *
     * Not supported
     */
    MDSCHEMA_MEMBERS(
        18, null, XmlaMember.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126309(SQL.90).aspx
     *
     * restrictions
     *    CATALOG_NAME Mandatory
     *    SCHEMA_NAME Optional
     *    CUBE_NAME Optional
     *    DIMENSION_UNIQUE_NAME Optional
     *    HIERARCHY_UNIQUE_NAME Optional
     *    LEVEL_UNIQUE_NAME Optional
     *
     *    MEMBER_UNIQUE_NAME Optional
     *    PROPERTY_NAME Optional
     *    PROPERTY_TYPE Optional
     *    PROPERTY_CONTENT_TYPE
     *       (Optional) A default restriction is in place on MDPROP_MEMBER
     *       OR MDPROP_CELL.
     *    PROPERTY_ORIGIN
     *       (Optional) A default restriction is in place on MD_USER_DEFINED
     *       OR MD_SYSTEM_ENABLED
     *    CUBE_SOURCE
     *       (Optional) A bitmap with one of the following valid values:
     *       1 CUBE
     *       2 DIMENSION
     *       Default restriction is a value of 1.
     *    PROPERTY_VISIBILITY
     *       (Optional) A bitmap with one of the following valid values:
     *       1 Visible
     *       2 Not visible
     *       Default restriction is a value of 1.
     *
     * Not supported
     *    PROPERTY_ORIGIN
     *    CUBE_SOURCE
     *    PROPERTY_VISIBILITY
     *    CHARACTER_MAXIMUM_LENGTH
     *    CHARACTER_OCTET_LENGTH
     *    NUMERIC_PRECISION
     *    NUMERIC_SCALE
     *    DESCRIPTION
     *    SQL_COLUMN_NAME
     *    LANGUAGE
     *    PROPERTY_ATTRIBUTE_HIERARCHY_NAME
     *    PROPERTY_CARDINALITY
     *    MIME_TYPE
     *    PROPERTY_IS_VISIBLE
     */
    MDSCHEMA_PROPERTIES(
        19, null, XmlaProperty.INSTANCE),

    /**
     * http://msdn2.microsoft.com/en-us/library/ms126290(SQL.90).aspx
     *
     * restrictions
     *    CATALOG_NAME Optional.
     *    SCHEMA_NAME Optional.
     *    CUBE_NAME Optional.
     *    SET_NAME Optional.
     *    SCOPE Optional.
     *    HIERARCHY_UNIQUE_NAME Optional.
     *    CUBE_SOURCE Optional.
     *        Note: Only one hierarchy can be included, and only those named
     *        sets whose hierarchies exactly match the restriction are
     *        returned.
     *
     * Not supported
     *    EXPRESSION
     *    DIMENSIONS
     *    SET_DISPLAY_FOLDER
     */
    MDSCHEMA_SETS(
        20, null, XmlaSet.INSTANCE),

    MDSCHEMA_INPUT_DATASOURCES(
        21, null, XmlaInputDatasource.INSTANCE),

    MDSCHEMA_KPIS(
        22, null, XmlaKpi.INSTANCE),

    MDSCHEMA_MEASUREGROUP_DIMENSIONS(
        23, null, XmlaMeasureGroupDimension.INSTANCE),

    MDSCHEMA_MEASUREGROUPS(
        24, null, XmlaMeasureGroup.INSTANCE);

    /** Per {@link XmlaConstant}. */
    public static final Dictionary<RowsetDefinition> DICTIONARY =
        DictionaryImpl.forClass(RowsetDefinition.class);

    public transient final List<Column> columns;
    public transient final List<Column> sortColumns;
    public transient final List<Column> restrictionColumns;

    private final int xmlaOrdinal;
    private final String description;

    /**
     * Creates a rowset definition.
     *
     * @param xmlaOrdinal Rowset ordinal, per OLE DB for OLAP
     * @param description Description
     */
    RowsetDefinition(
        int xmlaOrdinal,
        String description,
        Entity entity)
    {
        this.xmlaOrdinal = xmlaOrdinal;
        this.description = description;
        this.columns = entity.columns();
        this.sortColumns = entity.sortColumns();
        this.restrictionColumns = entity.restrictionColumns();
    }

    public Column lookupColumn(String name) {
        for (Column columnDefinition : columns) {
            if (columnDefinition.name.equals(name)) {
                return columnDefinition;
            }
        }
        return null;
    }

    public String xmlaName() {
        return name();
    }

    public String getDescription() {
        return description;
    }

    public int xmlaOrdinal() {
        return xmlaOrdinal;
    }
}

// End RowsetDefinition.java
