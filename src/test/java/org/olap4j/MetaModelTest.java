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
package org.olap4j;

import org.olap4j.metadata.*;
import org.olap4j.xmla.*;
import org.olap4j.xmla.Enumeration;

import junit.framework.TestCase;

import java.io.PrintWriter;
import java.util.*;

/**
 * Defines the olap4j meta-meta-model, tests that the actual API is consistent
 * with it, and provides code-generation utilities to be used by olap4j
 * developers.
 *
 * <h2>TODO</h2>
 *
 * <p>1. Add support for DISCOVER_INSTANCES rowset
 *
 * <p>2. Enum: Instance state {Started, Stopped, Starting, Stopping, Paused}
 *
 * <p>3. Should enum constants be UPPER_CASE or UpperCamelCase as at present?
 *
 * <p>4. Should enum constants always be in {@link XmlaConstants}, or in a more
 * specific class if possible?
 *
 * <p>5. add test that every XmlaConstant class has a static DICTIONARY field
 *
 * <p>6. complete class Function
 *
 * <p>7. Add a way to get functions from Database (or similar)
 *
 * <p>8. fix {@link XmlaConstants.DBType#GUID},
 * fix {@link XmlaConstants.DBType#DBTIMESTAMP},
 * fix {@link XmlaConstants.DBType#I2},
 *
 * <p>10. Bring classes up to XMLA 2012
 *
 * <pre>
 * Class                   Updated
 * ======================= =======
 * XmlaAction              yes
 * XmlaCatalog             yes
 * XmlaColumn              no - but good enough
 * XmlaCube                yes
 * XmlaDatabaseProperty    yes
 * XmlaDatasource          yes
 * XmlaDimension           yes
 * XmlaEnumerator          yes
 * XmlaFunction            yes
 * XmlaHierarchy           yes
 * XmlaKeyword             yes
 * XmlaLevel               yes
 * XmlaLiteral             yes
 * XmlaMeasure             yes
 * XmlaMember              yes
 * XmlaProperty            yes
 * XmlaProviderType        no
 * XmlaSchema              yes
 * XmlaSchemaRowset        yes
 * XmlaSet                 yes
 * XmlaTableInfo           yes
 * XmlaTable               no
 * XmlaType                no
 * </pre>
 *
 * 11. Can we obsolete {@link XmlaMember#Depth}? It seems to have same function
 * as {@link XmlaMember#LevelNumber}.
 *
 * 12. Make sure every entity has an "annotation" attribute.
 *
 * 13. Measures of a measure group may be a strict subset of the cube's measures
 * but XMLA does not provide a way to find out which measures.
 *
 * <h2>Release notes</h2>
 *
 * <p>{@link org.olap4j.metadata.XmlaConstants.EnumWithDesc} is deprecated.
 * Use an enum that implements {@link XmlaConstant} instead.
 * (Should we remove?)</p>
 *
 * <h2>New entities</h2>
 *
 * <ul>
 *
 * <li>{@link XmlaKpi}
 * <li>{@link XmlaMeasureGroup}
 * <li>{@link XmlaMeasureGroupDimension}
 * <li>{@link XmlaInputDatasource}
 *
 * </ul>
 *
 * <h2>New attributes</h2>
 *
 * <ul>
 *
 * <li>{@link XmlaAction#ActionType}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaAction#ActionCaption}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaAction#Description}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaAction#Content}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaAction#Application}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaAction#Invocation}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaCube#BaseCubeName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaCube#Annotations}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaDimension#Annotations}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaLevel#Annotations}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaLevel#LevelOrderingProperty}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaLevel#LevelDbtype}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaLevel#LevelMasterUniqueName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaLevel#LevelNameSqlColumnName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaLevel#LevelKeySqlColumnName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaLevel#LevelUniqueNameSqlColumnName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaLevel#LevelKeyCardinality}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaLevel#LevelOrigin}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaHierarchy#DimensionMasterUniqueName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaHierarchy#HierarchyOrigin}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaHierarchy#HierarchyDisplayFolder}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaHierarchy#InstanceSelection}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaHierarchy#GroupingBehavior}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaHierarchy#Structure}
 *     {@link org.olap4j.metadata.Hierarchy#getStructure()}
 *     TODO: add test
 * <li>{@link XmlaHierarchy#StructureType}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasure#NumericPrecision}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasure#NumericScale}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasure#MeasureUnits}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasure#Expression}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasure#MeasureNameSqlColumnName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasure#MeasureUnqualifiedCaption}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasure#MeasuregroupName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasure#MeasureDisplayFolder}
 *     TODO: add method
 *     TODO: add test

 * <li>{@link XmlaMeasureGroup#CatalogName}
 *     via {@link org.olap4j.metadata.MeasureGroup#getCube()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroup#SchemaName}
 *     via {@link org.olap4j.metadata.MeasureGroup#getCube()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroup#CubeName}
 *     via {@link org.olap4j.metadata.MeasureGroup#getCube()}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasureGroup#Description}
 *     {@link org.olap4j.metadata.MeasureGroup#getDescription()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroup#MeasuregroupCaption}
 *     {@link org.olap4j.metadata.MeasureGroup#getCaption()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroup#MeasuregroupName}
 *     {@link org.olap4j.metadata.MeasureGroup#getName()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroup#IsWriteEnabled}
 *     {@link org.olap4j.metadata.MeasureGroup#isWriteEnabled()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#CatalogName}
 *     via {@link org.olap4j.metadata.MeasureGroupDimension#getMeasureGroup()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#SchemaName}
 *     via {@link org.olap4j.metadata.MeasureGroupDimension#getMeasureGroup()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#CubeName}
 *     via {@link org.olap4j.metadata.MeasureGroupDimension#getMeasureGroup()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#MeasuregroupName}
 *     via {@link org.olap4j.metadata.MeasureGroupDimension#getMeasureGroup()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#MeasuregroupCardinality}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#DimensionUniqueName}
 *     via {@link org.olap4j.metadata.MeasureGroupDimension#getDimension()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#DimensionCardinality}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#DimensionGranularity}
 *     {@link org.olap4j.metadata.MeasureGroupDimension#getGranularityHierarchy()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#DimensionIsFactDimension}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#DimensionIsVisible}
 *     via {@link org.olap4j.metadata.MeasureGroupDimension#getDimension()}
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#DimensionPath}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMeasureGroupDimension#DimensionVisibility}
 *     TODO: add restriction
 *     TODO: add test
 * <li>{@link XmlaMember#Description}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMember#Expression}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMember#MemberKey}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMember#IsPlaceholdermember}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMember#IsDatamember}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaMember#Scope}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#CharacterMaximumLength}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#CharacterOctetLength}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#NumericPrecision}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#NumericScale}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#SqlColumnName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#Language}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#PropertyOrigin}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#PropertyAttributeHierarchyName}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#PropertyCardinality}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#MimeType}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaProperty#PropertyIsVisible}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaSet#Annotations}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaSet#Expression}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaSet#Dimensions}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaSet#SetDisplayFolder}
 *     TODO: add method
 *     TODO: add test
 * <li>{@link XmlaSet#SetEvaluationContext}
 *     TODO: add method
 *     TODO: add test
 *
 * </ul>
 *
 * <h2>New restrictions</h2>
 *
 * <ul>
 *
 * <li>{@link XmlaCube#BaseCubeName}
 * <li>{@link XmlaCube#CubeSource}
 * <li>{@link XmlaDimension#CubeSource}
 * <li>{@link XmlaDimension#DimensionVisibility}
 * <li>{@link XmlaLevel#LevelOrigin}
 * <li>{@link XmlaLevel#CubeSource}
 * {@link XmlaLevel#LevelVisibility}
 * {@link XmlaSet#CubeSource}
 * {@link XmlaSet#HierarchyUniqueName}
 *
 * </ul>
 *
 * <h2>Tests to be written</h2>
 *
 * Other comments
 *
 * <ul>
 *
 * <li>{@link XmlaMember#MemberOrdinal} is deprecated -- always returns 0. (Good
 * news. Mondrian always had trouble generating this efficiently.)
 *
 * <li>{@link XmlaCube#CubeType} used to be UI2, is now WSTR.
 * But still a UI2 when used via {@link XmlaCube#CubeSource} or similar
 * restrictions.
 *
 * <li>Merged {@link XmlaConstants}.ProviderType with
 * {@link Database.ProviderType} and obsoleted the former.</li>
 *
 * </ul>
 */
public class MetaModelTest extends TestCase {

    private static final FullType GUID = FullType.of(XmlaConstants.DBType.GUID);
    private static final FullType DBTIMESTAMP =
        FullType.of(XmlaConstants.DBType.DBTIMESTAMP);
    private static final FullType UI2 = FullType.of(XmlaConstants.DBType.UI2);
    private static final FullType I2 = FullType.of(XmlaConstants.DBType.I2);
    private static final FullType I4 = FullType.of(XmlaConstants.DBType.I4);
    private static final FullType INTODO =
        FullType.of(XmlaConstants.DBType.UI2);
    private static final FullType WSTR = FullType.of(XmlaConstants.DBType.WSTR);
    private static final FullType BOOL = FullType.of(XmlaConstants.DBType.BOOL);

    public void testInit() {
        System.out.println(XmlaAction.INSTANCE);
        System.out.println(XmlaMeasureGroup.INSTANCE);
        for (RowsetDefinition rowsetDefinition : RowsetDefinition.values()) {
            System.out.println(rowsetDefinition);
        }
        for (Enumeration enumeration : Enumeration.getValues()) {
            for (Object o : enumeration.getDictionary().getValues()) {
                System.out.println(o);
            }
        }
    }

    public void testGenerateMethods() {
        final PrintWriter out = new PrintWriter(System.out);
        for (Entity entity : Entities.ALL) {
            entityJavadoc(out, entity);
            entityMethod(out, entity);
        }
        out.flush();
    }

    private void entityMethod(PrintWriter out, Entity entity) {
        code(out, "    ResultSet get" + entity.pluralName() + "(");
        final List<Attribute> parameters = entity.parameters();
        for (Ord<Attribute> attribute : Ord.zip(parameters)) {
            code(
                out,
                "        " + attribute.e.type.java + " "
                + attribute.e.paramName()
                + (attribute.i == parameters.size() - 1
                   ? ") throws OlapException;" : ","));
        }
    }

    private void code(PrintWriter out, String s) {
        out.println(s);
    }

    private void entityJavadoc(PrintWriter out, Entity entity) {
        out.println();
        javadoc(
            out,
            entity.javadocDescription()
            + "\n"
            + "\n"
            + "<p>Specification as for XML/A " + entity.xmlaRowsetName()
            + " schema rowset.\n"
            + "\n"
            + "<p>Each " + entity.name.toLowerCase()
            + " description has the following columns:\n<ol>");
        for (Attribute attribute : entity.attributes) {
            boolean nullable = attribute.nullable;
            if  (attribute.description.equals(
                    "Always returns <code>null</code>."))
            {
                // if attribute is always null, don't state the obvious
                nullable = false;
            }
            javadoc(
                out,
                "<li><b>" + up(attribute.name) + "</b> "
                + typeString(attribute.type, nullable)
                + " => " + attribute.description);
        }
        int patternCount = 0;
        for (Attribute attribute : entity.attributes) {
            if (attribute.param == Param.PATTERN) {
                ++patternCount;
            }
            if (attribute.param == Param.NO) {
                continue;
            }
            javadoc(
                out,
                "\n"
                + "@param " + attribute.paramName()
                + " " + attribute.paramDescription());
        }
        if (patternCount > 0) {
            javadoc(out, "@see #getSearchStringEscape");
        }
        javadoc(
            out,
            "@return a <code>ResultSet</code> object in which each row is an OLAP "
            + entity.name + " description");
        javadoc(out, "@throws OlapException if a database access error occurs");
    }

    private String typeString(FullType type, boolean nullable) {
        String s = type.java;
        if (nullable) {
            s += " (may be <code>null</code>)";
        }
        return s;
    }

    private static String up(String name) {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
             char c = name.charAt(i);
            if (i != 0 && Character.isUpperCase(c)) {
                buf.append('_');
            }
            buf.append(Character.toUpperCase(c));
        }
        return buf.toString();
    }

    private void javadoc(PrintWriter out, String s) {
        out.println(s);
    }

    public static class Entities {
        public static final Entity ACTION = new Entity("Action", null) {
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "catalogName", WSTR, true, Param.PARAM, "The name of the database."),
                    new Attribute(this, "schemaName",     WSTR,  true,  Param.PATTERN, "The name of the schema to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "cubeName",       WSTR,  true,  Param.PATTERN, "The name of the cube to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "actionName",     WSTR,  false, Param.PATTERN, "The name of the ${entity.name.lower}."),
                    new Attribute(this, "coordinate",     WSTR,  true,  Param.NO, "Always returns <code>null</code>."),
                    new Attribute(this, "coordinateType", INTODO, true,  Param.NO, "Always returns <code>null</code>."),

                    // new in 2.0
                    new Attribute(this, "actionType",     asI4(XmlaConstants.ActionType.class), false, Param.NO, "A bitmap that is used to specify the action's triggering method."));
            }
        };

        public static final Entity CUBE = new Entity("Cube", org.olap4j.metadata.Cube.class) {
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "catalogName", WSTR, true, Param.PARAM, "The name of the database."),
                    new Attribute(this, "schemaName",     WSTR,  true,  Param.PATTERN, "The name of the schema to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "cubeName",       WSTR,  false,  Param.PATTERN, "The name of the ${entity.name.lower}."),
                    new Attribute(this, "cubeType",     WSTR,  false, Param.NO, "Cube type."),
                    new Attribute(this, "cubeGuid",     GUID,  true,  Param.NO, "Cube UUID."),
                    new Attribute(this, "createdOn", DBTIMESTAMP, true,  Param.NO, "Date and time of cube creation."),
                    new Attribute(this, "lastSchemaUpdate", DBTIMESTAMP, true, Param.NO, "Date and time of last schema update."),
                    new Attribute(this, "schemaUpdatedBy", WSTR, true, Param.NO, "User ID of the person who last updated the schema."),
                    new Attribute(this, "lastDataUpdate", DBTIMESTAMP, true, Param.NO, "Date and time of last data update."),
                    new Attribute(this, "dataUpdatedBy", WSTR, true, Param.NO, "User ID of the person who last updated the data."),
                    new Attribute(this, "isDrillthroughEnabled", BOOL, false, Param.NO, "Describes whether DRILLTHROUGH can be performed on the members of a cube."),
                    new Attribute(this, "isWriteEnabled", BOOL, false, Param.NO, "Describes whether a cube is write-enabled"),
                    new Attribute(this, "isLinkable", BOOL, false, Param.NO, "Describes whether a cube can be used in a linked cube"),
                    new Attribute(this, "isSqlEnabled", BOOL, false, Param.NO, "Describes whether or not SQL can be used on the cube"),
                    new Attribute(this, "description", WSTR, false, Param.NO, "A user-friendly description of the cube."),
                    new Attribute(this, "cubeCaption", WSTR, true, Param.NO, "The caption of the cube."),
                    new Attribute(this, "baseCubeName", WSTR, true, Param.NO, "The name of the source cube if this cube is a perspective cube."));
            }
        };

        public static final Entity DATABASE = new Entity("Database", Database.class) {
                @Override protected String javadocDescription() {
                    return "Retrieves a row set describing the databases that are available on the server.";
                }
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "dataSourceName", WSTR, false, Param.NO, "The name of the data source, such as FoodMart 2000."),
                    new Attribute(this, "dataSourceDescription", WSTR, true, Param.NO, "A description of the data source, as entered by the publisher."),
                    new Attribute(this, "url", WSTR, true, Param.NO, "The unique path that shows where to invoke the XML for Analysis methods for that data source."),
                    new Attribute(this, "dataSourceInfo", WSTR, true, Param.NO, "A string containing any additional information required to connect to the data source. This can include the Initial Catalog property or other information for the provider.<br/>Example: \"Provider=MSOLAP;Data Source=Local;\" (may be <code>null</code>)"),
                    new Attribute(this, "providerName", WSTR, true, Param.NO, "The name of the provider behind the data source.<br/>Example: \"MSDASQL\"."),
                    new Attribute(this, "providerType", I4, false, Param.NO, "The types of data supported by the provider. May include one or more of the following types. Example follows this table.<br/>TDP: tabular data provider.<br/>MDP: multidimensional data provider.<br/>DMP: data mining provider. A DMP provider implements the OLE DB for Data Mining specification."),
                    new Attribute(this, "authenticationMode", WSTR, false, Param.NO, "Specification of what type of security mode the data source uses. Values can be one of the following:<br/>Unauthenticated: no user ID or password needs to be sent.<br/>Authenticated: User ID and Password must be included in the information required for the connection.<br/>Integrated: the data source uses the underlying security to determine authorization, such as Integrated Security provided by Microsoft Internet Information Services (IIS)."));
            }
        };

        public static final Entity LITERAL = new Entity("Literal", null) {
            @Override protected String javadocDescription() {
                return "Retrieves a list of information on supported literals, including data types and values.";
            }

            @Override public String xmlaRowsetName() {
                return "DISCOVER_LITERALS";
            }
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "literalName", WSTR, false, Param.NO, "The name of the literal described in the row.<br/>Example: DBLITERAL_LIKE_PERCENT"),
                    new Attribute(this, "literalValue", WSTR, false, Param.NO, "Contains the actual literal value.<br/>Example, if LiteralName is DBLITERAL_LIKE_PERCENT and the percent character (%) is used to match zero or more characters in a LIKE clause, this column's value would be \"%\"."),
                    new Attribute(this, "literalInvalidChars", WSTR, true, Param.NO, "The characters, in the literal, that are not valid.<br/>For example, if table names can contain anything other than a numeric character, this string would be \"0123456789\"."), // REVIEW
                    new Attribute(this, "literalInvalidStartingChars", WSTR, true, Param.NO, "The characters that are not valid as the first character of the literal. If the literal can start with any valid character, this is null."),
                    new Attribute(this, "literalMaxlength", INTODO, false, Param.NO, "The maximum number of characters in the literal. If there is no maximum or the maximum is unknown, the value is -1."));
            }
        };

        public static final Entity DATABASE_PROPERTY = new Entity("Database Property", Property.class) {
            @Override protected List<Parameter> extraParams() {
                return Collections.singletonList(
                    new Parameter("dataSourceName", WSTR, "Name of data source"));
            }
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "propertyName", WSTR, false, Param.PATTERN, "The name of the property."),
                    new Attribute(this, "propertyDescription", WSTR, true, Param.NO, "A localizable text description of the property."),
                    new Attribute(this, "propertyType", WSTR, false, Param.NO, "The XML data type of the property."), // REVIEW
                    new Attribute(this, "propertyAccessType", enumOf(XmlaConstants.Access.class, XmlaConstants.DBType.WSTR), false, Param.NO, "Access for the property. The value can be Read, Write, or ReadWrite."),
                    new Attribute(this, "isRequired", BOOL, false, Param.NO, "Whether a a property is required."),
                    new Attribute(this, "propertyValue", WSTR, true, Param.NO, "The current value of the property."));
            }
        };

        public static final Entity PROPERTY = new Entity("Property", Cube.class) {
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "catalogName", WSTR, false, Param.PARAM, "The name of the database to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "schemaName",  WSTR,  true,  Param.PATTERN, "The name of the schema to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "cubeName", WSTR, false, Param.PATTERN, "The name of the cube to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "dimensionUniqueName", WSTR, false, Param.PARAM, "The unique name of the dimension."),
                    new Attribute(this, "hierarchyUniqueName", WSTR, false, Param.PARAM, "The unique name of the hierarchy."),
                    new Attribute(this, "levelUniqueName", WSTR, true, Param.PARAM, "The unique name of the level to which this property belongs."),
                    new Attribute(this, "memberUniqueName", WSTR, false, Param.PARAM, "The unique name of the member to which the property belongs."),
                    new Attribute(this, "propertyName", WSTR, false, Param.PATTERN, "Name of the property."),
                    new Attribute(this, "propertyCaption", WSTR, false, Param.NO, "A label or caption associated with the property, used primarily for display purposes."),
                    new Attribute(this, "propertyType", enumOf(Property.TypeFlag.class, XmlaConstants.DBType.I2), false, Param.NO, "A bitmap that specifies the type of the property"),
                    new Attribute(this, "dataType", UI2, false, Param.NO, "Data type of the property."),
                    new Attribute(this, "propertyContentType", enumOf(Property.ContentType.class, XmlaConstants.DBType.I2), false, Param.NO, "The type of the property. "),
                    new Attribute(this, "description", WSTR, false, Param.NO, "A human-readable description of the measure. "));
            }
        };

        public static final Entity DIMENSION = new Entity("Dimension", Cube.class) {
                @Override protected String javadocDescription() {
                    return "Retrieves a row set describing the databases that are available on the server.";
                }
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "catalogName", WSTR, false, Param.PARAM, "The name of the database."),
                    new Attribute(this, "schemaName",  WSTR,  true,  Param.PATTERN, "The name of the schema to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "cubeName", WSTR, false, Param.PATTERN, "The name of the cube to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "dimensionName", WSTR, false, Param.PATTERN, "The name of the dimension."),
                    new Attribute(this, "dimensionUniqueName", WSTR, false, Param.NO, "The unique name of the dimension."),
                    new Attribute(this, "dimensionGuid", WSTR, true, Param.NO, "Not supported."),
                    new Attribute(this, "dimensionCaption", WSTR, false, Param.NO, "The caption of the dimension."),
                    new Attribute(this, "dimensionOrdinal", INTODO, false, Param.NO, "The position of the dimension within the cube."),
                    new Attribute(this, "dimensionType", enumOf(Dimension.Type.class, XmlaConstants.DBType.I2), false, Param.NO, "The type of the dimension."),
                    new Attribute(this, "dimensionCardinality", INTODO, false, Param.NO, "The number of members in the key attribute."),
                    new Attribute(this, "defaultHierarchy", WSTR, false, Param.NO, "A hierarchy from the dimension. Preserved for backwards compatibility."), // REVIEW
                    new Attribute(this, "description", WSTR, false, Param.NO, "A user-friendly description of the dimension."),
                    new Attribute(this, "isVirtual", BOOL, false, Param.NO, "Always <code>false</code>."),
                    new Attribute(this, "isReadwrite", BOOL, false, Param.NO, "Whether the dimension is write-enabled."),
                    new Attribute(this, "dimensionUniqueSettings", INTODO, false, Param.NO, "A bitmap that specifies which columns contain unique values if the dimension contains only members with unique names."),
                    new Attribute(this, "dimensionMasterUniqueName", WSTR,  true, Param.NO, "Always returns <code>null</code>."),
                    new Attribute(this, "dimensionIsVisible", BOOL, true, Param.NO, "Always <code>true</code>."));
            }

            @Override
            protected List<Parameter> extraParams() {
                return Arrays.asList(
                    new Parameter("cubeSource", UI2, "The default restriction is a value of  {@link Cube.Type#CUBE} (1)."),
                    new Parameter("dimensionVisibility", UI2, "The default restriction is a value of 1."));
            }
        };

        public static final Entity FUNCTION = new Entity("Function", Cube.class) {
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "functionName", WSTR, false, Param.PATTERN, "The name of the function."),
                    new Attribute(this, "description", WSTR, false, Param.NO, "A description of the function."),
                    new Attribute(this, "parameterList", WSTR, false, Param.NO, "A comma delimited list of parameters."),
                    new Attribute(this, "returnType", INTODO, false, Param.NO, "The VARTYPE of the return data type of the function."), // REVIEW
                    new Attribute(this, "origin", INTODO, false, Param.NO, "The origin of the function:  1 for MDX functions.  2 for user-defined functions."), // REVIEW
                    new Attribute(this, "interfaceName", WSTR, true, Param.NO, "The name of the interface for user-defined functions"),
                    new Attribute(this, "libraryName", WSTR, true, Param.NO, "The name of the type library for user-defined functions. <code>null</code> for MDX functions."),
                    new Attribute(this, "caption", WSTR, false, Param.NO, "The display caption for the function."));
            }
        };

        public static final Entity HIERARCHY = new Entity("Hierarchy", org.olap4j.metadata.Hierarchy.class) {
            @Override
            public String pluralName() {
                return "Hierarchies";
            }

            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "catalogName", WSTR, false, Param.PARAM, "The name of the catalog to which this hierarchy belongs."),
                    new Attribute(this, "schemaName",  WSTR,  true,  Param.PATTERN, "The name of the schema to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "cubeName", WSTR, false, Param.PATTERN, "The name of the cube to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "dimensionUniqueName", WSTR, false, Param.PARAM, "The unique name of the dimension to which this hierarchy belongs."),
                    new Attribute(this, "hierarchyName", WSTR, true, Param.PATTERN, "The name of the hierarchy. Blank if there is only a single hierarchy in the dimension. This will always have a value in Microsoft SQL Server Analysis Services."), // REVIEW
                    new Attribute(this, "hierarchyUniqueName", WSTR, false, Param.NO, "The unique name of the hierarchy."),
                    new Attribute(this, "hierarchyGuid", WSTR, false, Param.NO, "Not supported."),
                    new Attribute(this, "hierarchyCaption", WSTR, false, Param.NO, "A label or a caption associated with the hierarchy. If a caption does not exist, HIERARCHY_NAME is returned. If the dimension either does not contain a hierarchy or has just one hierarchy, this column will contain the name of the dimension."),
                    new Attribute(this, "dimensionType", asI2(Dimension.Type.class), false, Param.NO, "The type of the dimension."),
                    new Attribute(this, "hierarchyCardinality", INTODO, false, Param.NO, "The number of members in the hierarchy."),
                    new Attribute(this, "defaultMember", WSTR, false, Param.NO, "The default member for this hierarchy."),
                    new Attribute(this, "allMember", WSTR, false, Param.NO, "The member at the highest level of rollup in the hierarchy."),
                    new Attribute(this, "description", WSTR, false, Param.NO, "A human-readable description of the hierarchy. <code>null</code> if no description exists."),
                    new Attribute(this, "structure", I2, false, Param.NO, "The structure of the hierarchy."),
                    new Attribute(this, "isVirtual", BOOL, false, Param.NO, "Always false."),
                    new Attribute(this, "isReadwrite", BOOL, false, Param.NO, "A Boolean that indicates whether the Write Back to dimension column is enabled."),
                    new Attribute(this, "dimensionUniqueSettings", INTODO, false, Param.NO, "Always returns MDDIMENSIONS_MEMBER_KEY_UNIQUE (1)."),
                    new Attribute(this, "dimensionIsVisible", BOOL, false, Param.NO, "Always returns true."),
                    new Attribute(this, "hierarchyOrdinal", INTODO, false, Param.NO, "The ordinal number of the hierarchy across all hierarchies of the cube."),
                    new Attribute(this, "dimensionIsShared", BOOL, false, Param.NO, "Always returns <code>true</code>."),
                    new Attribute(this, "parentChild", BOOL, false, Param.NO, "Is hierarchy a parent."), // REVIEW:

                    // new
                    new Attribute(this, "origin", UI2, false, Param.NO, "Is hierarchy a parent."), // REVIEW:
                    new Attribute(this, "structureType", I2, false, Param.NO, "The structure of the hierarchy."),
                    new Attribute(this, "structureType", I2, false, Param.NO, "The structure of the hierarchy."));
            }
        };

        public static final Entity LEVEL = new Entity("Level", org.olap4j.metadata.Level.class) {
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "catalogName", WSTR, false, Param.PARAM, "The name of the catalog to which this level belongs."),
                    new Attribute(this, "schemaName",  WSTR,  true,  Param.PATTERN, "The name of the schema to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "cubeName", WSTR, false, Param.PATTERN, "The name of the cube to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "dimensionUniqueName", WSTR, false, Param.PARAM, "The unique name of the dimension to which this level belongs."),
                    new Attribute(this, "hierarchyUniqueName", WSTR, false, Param.PARAM, "The unique name of the hierarchy."),
                    new Attribute(this, "levelName", WSTR, false, Param.PATTERN, "The name of the level."),
                    new Attribute(this, "levelUniqueName", WSTR, false, Param.NO, "The properly escaped unique name of the level."),
                    new Attribute(this, "levelGuid", WSTR, false, Param.NO, "Level GUID."),
                    new Attribute(this, "levelCaption", WSTR, false, Param.NO, "A label or caption associated with the hierarchy."),
                    new Attribute(this, "levelNumber", INTODO, false, Param.NO, "The distance of the level from the root of the hierarchy. Root level is zero (0)."),
                    new Attribute(this, "levelCardinality", INTODO, false, Param.NO, "The number of members in the level. This value can be an approximation of the real cardinality."),
                    new Attribute(this, "levelType", INTODO, false, Param.NO, "Type of the level"),
                    new Attribute(this, "customRollupSettings", INTODO, false, Param.NO, "A bitmap that specifies the custom rollup options."),
                    new Attribute(this, "levelUniqueSettings", INTODO, false, Param.NO, "A bitmap that specifies which columns contain unique values, if the level only has members with unique names or keys."),
                    new Attribute(this, "levelIsVisible", BOOL, false, Param.NO, "A Boolean that indicates whether the level is visible."),
                    new Attribute(this, "description", WSTR, false, Param.NO, "A human-readable description of the level. <code>null</code> if no description exists."));
            }
        };

        public static final Entity MEASURE = new Entity("Measure", org.olap4j.metadata.Measure.class) {
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "catalogName", WSTR, false, Param.PARAM, "The name of the catalog to which this measure belongs."),
                    new Attribute(this, "schemaName",  WSTR,  true,  Param.PATTERN, "The name of the schema to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "cubeName", WSTR, false, Param.PATTERN, "The name of the cube to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "measureName", WSTR, false, Param.PATTERN, "The name of the measure."),
                    new Attribute(this, "measureUniqueName", WSTR, false, Param.PARAM, "The Unique name of the measure."),
                    new Attribute(this, "measureCaption", WSTR, false, Param.NO, "A label or caption associated with the measure."),
                    new Attribute(this, "measureGuid", WSTR, false, Param.NO, "Measure GUID."),
                    new Attribute(this, "measureAggregator", asI4(Measure.Aggregator.class), false, Param.NO, "How a measure was derived."),
                    new Attribute(this, "dataType", enumOf(XmlaConstants.DBType.class, XmlaConstants.DBType.UI2), false, Param.NO, "Data type of the measure."),
                    new Attribute(this, "measureIsVisible", BOOL, false, Param.NO, "Always returns <code>true</code>. (If the measure is not visible, it will not be included in the schema rowset.)"),
                    new Attribute(this, "levelsList", WSTR, false, Param.NO, "Always returns <code>null</code>. (Except that SQL Server returns non-null values!)"),
                    new Attribute(this, "description", WSTR, false, Param.NO, "A human-readable description of the measure."));
            }
        };

        public static final Entity MEMBER = new Entity("Member", org.olap4j.metadata.Member.class) {
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "catalogName", WSTR, false, Param.PARAM, "The name of he catalog to which this member belongs."),
                    new Attribute(this, "schemaName",  WSTR,  true,  Param.PATTERN, "The name of the schema to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "cubeName", WSTR, false, Param.PATTERN, "The name of the cube to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "dimensionUniqueName", WSTR, false, Param.PARAM, "Unique name of the dimension to which this member belongs."),
                    new Attribute(this, "hierarchyUniqueName", WSTR, false, Param.PARAM, "Unique name of the hierarchy. If the member belongs to more than one hierarchy, there is one row for each hierarchy to which it belongs."),
                    new Attribute(this, "levelUniqueName", WSTR, false, Param.PARAM, "Unique name of the level to which the member belongs."),
                    new Attribute(this, "levelNumber", INTODO, false, Param.NO, "The distance of the member from the root of the hierarchy."),
                    new Attribute(this, "memberOrdinal", INTODO, false, Param.NO, "Ordinal number of the member. Sort rank of the member when members of this dimension are sorted in their natural sort order. If providers do not have the concept of natural ordering, this should be the rank when sorted by MEMBER_NAME."),
                    new Attribute(this, "memberName", WSTR, false, Param.NO, "Name of the member."),
                    new Attribute(this, "memberUniqueName", WSTR, false, Param.PARAM, " Unique name of the member."),
                    new Attribute(this, "memberType", INTODO, false, Param.NO, "Type of the member."),
                    new Attribute(this, "memberGuid", WSTR, false, Param.NO, "Memeber GUID."),
                    new Attribute(this, "memberCaption", WSTR, false, Param.NO, "A label or caption associated with the member."),
                    new Attribute(this, "childrenCardinality", INTODO, false, Param.NO, "Number of children that the member has."),
                    new Attribute(this, "parentLevel", INTODO, false, Param.NO, "The distance of the member's parent from the root level of the hierarchy."),
                    new Attribute(this, "parentUniqueName", WSTR, true, Param.NO, "Unique name of the member's parent."),
                    new Attribute(this, "parentCount", INTODO, false, Param.NO, "Number of parents that this member has."),
                    new Attribute(this, "treeOp", enumOf(Member.TreeOp.class, XmlaConstants.DBType.I4), false, Param.PARAM, "Tree Operation"),
                    new Attribute(this, "depth", INTODO, false, Param.NO, "depth")); // REVIEW
            }
            protected List<Parameter> extraParams() {
                return Arrays.asList(
                    new Parameter("treeOps", array(Member.TreeOp.class, XmlaConstants.DBType.I4), "set of tree operations to retrieve members relative to the member whose unique name was specified; or null to return just the member itself. Ignored if <code>memberUniqueName</code> is not specified."));
            }
        };

        public static final Entity SET = new Entity("Set", org.olap4j.metadata.NamedSet.class) {
            protected List<Attribute> attributes() {
                return Arrays.asList(
                    new Attribute(this, "catalogName", WSTR, false, Param.PARAM, "The name of he catalog to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "schemaName",  WSTR,  true,  Param.PATTERN, "The name of the schema to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "cubeName", WSTR, true, Param.PATTERN, "The name of the cube to which this ${entity.name.lower} belongs."),
                    new Attribute(this, "setName", WSTR, false, Param.PATTERN, "The name of this ${entity.name.lower}."),
                    new Attribute(this, "scope", INTODO, false, Param.NO, "Always returns <code>null</code>.")); // REVIEW
            }
        };

        public static final List<Entity> ALL = Arrays.asList(
            ACTION,
            CUBE,
            DATABASE,
            DATABASE_PROPERTY,
            DIMENSION,
            FUNCTION,
            HIERARCHY,
            LEVEL,
            MEASURE,
            MEMBER,
            SET);
    }

    public static abstract class Entity {
        public final List<Attribute> attributes;
        private final String name;

        Entity(String name, Class clazz) {
            this.name = name;
            this.attributes = attributes();
        }

        protected abstract List<Attribute> attributes();

        // Returns e.g. "Actions"
        public String pluralName() {
            return name + "s";
        }

        // Returns e.g. "MDSCHEMA_ACTIONS"
        public String xmlaRowsetName() {
            return "MDSCHEMA_" + up(pluralName());
        }

        public List<Attribute> parameters() {
            final ArrayList<Attribute> list = new ArrayList<Attribute>();
            for (Attribute attribute : attributes) {
                if (attribute.param == Param.NO) {
                    continue;
                }
                list.add(attribute);
            }
            return list;
        }

        protected String javadocDescription() {
            return "Retrieves a result set describing the " + pluralName()
                + " in this database.";
        }

        protected List<Parameter> extraParams() {
            return Collections.emptyList();
        }
    }

    public static class Attribute {
        private final Entity entity;
        private final String name;
        private final FullType type;
        private final boolean nullable;
        private final Param param;
        private final String description;

        public Attribute(Entity entity, String name, FullType type, boolean nullable, Param param, String description) {
            this.entity = entity;
            this.name = getString(name);
            this.type = type;
            this.nullable = nullable;
            this.param = param;
            this.description = getString(description);
        }

        private String getString(String name) {
            return name.replaceAll("\\$\\{entity\\.name\\.lower\\}", entity.name.toLowerCase());
        }

        public String paramName() {
            assert param != Param.NO;
            return name + (param == Param.PATTERN ? "Pattern" : "");
        }
        public String paramDescription() {
            String e = "catalog"; // TODO figure out from name
            switch (param) {
            case PARAM:
            return (name.contains("Unique")
                 ? "unique name of a " + e + " (not a pattern); "
                 : "a " + e + " name;")
                + "must match the " + e + " name as it is stored in the database; "
                + (e.equals("database") || e.equals("schema") || e.equals("cube")
                  ?  "\"\" retrieves those without a " + e + "; "
                   : "")
                + "<code>null</code> means that the " + e + " name should not be used to narrow the search";
            case PATTERN:
            return "a " + e + " name pattern; "
                + "must match the " + e + " name as it is stored in the database; "
                + (e.equals("database") || e.equals("schema") || e.equals("cube")
                  ?  "\"\" retrieves those without a " + e + "; "
                   : "")
                + "<code>null</code> means that the " + e + " name should not be used to narrow the search";
            case NO:
            default:
                throw new AssertionError(this);
            }
        }
    }

    enum Param {
        PARAM,
        PATTERN,
        NO
    }

    /**
     * Pair of an element and an ordinal.
     */
    static class Ord<E> implements Map.Entry<Integer, E> {
        public final int i;
        public final E e;

        /**
         * Creates an Ord.
         */
        public Ord(int i, E e) {
            this.i = i;
            this.e = e;
        }

        /**
         * Creates an Ord.
         */
        public static <E> Ord<E> of(int n, E e) {
            return new Ord<E>(n, e);
        }

        /**
         * Creates an iterable of {@code Ord}s over an iterable.
         */
        public static <E> Iterable<Ord<E>> zip(final Iterable<E> iterable) {
            return new Iterable<Ord<E>>() {
                public Iterator<Ord<E>> iterator() {
                    return zip(iterable.iterator());
                }
            };
        }

        /**
         * Creates an iterator of {@code Ord}s over an iterator.
         */
        public static <E> Iterator<Ord<E>> zip(final Iterator<E> iterator) {
            return new Iterator<Ord<E>>() {
                int n = 0;

                public boolean hasNext() {
                    return iterator.hasNext();
                }

                public Ord<E> next() {
                    return Ord.of(n++, iterator.next());
                }

                public void remove() {
                    iterator.remove();
                }
            };
        }

        /**
         * Returns a numbered list based on an array.
         */
        public static <E> List<Ord<E>> zip(final E[] elements) {
            return zip(Arrays.asList(elements));
        }

        /**
         * Returns a numbered list.
         */
        public static <E> List<Ord<E>> zip(final List<E> elements) {
            return new AbstractList<Ord<E>>() {
                public Ord<E> get(int index) {
                    return of(index, elements.get(index));
                }

                public int size() {
                    return elements.size();
                }
            };
        }

        public Integer getKey() {
            return i;
        }

        public E getValue() {
            return e;
        }

        public E setValue(E value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Parameter {
        public Parameter(String name, FullType type, String description) {
        }
    }

    private static Class stringOf(Class<? extends XmlaConstant> enumClass) {
        return null;
    }

    private static FullType asI4(Class<? extends XmlaConstant> aClass) {
        return FullType.of(aClass, XmlaConstants.DBType.I4);
    }

    private static FullType asI2(Class<? extends XmlaConstant> aClass) {
        return FullType.of(aClass, XmlaConstants.DBType.I2);
    }

    private static FullType enumSet(Class<? extends XmlaConstant> clazz, XmlaConstants.DBType dbType) {
        return FullType.of(clazz, dbType);
    }

    private static FullType enumOf(Class<? extends XmlaConstant> clazz, XmlaConstants.DBType dbType) {
        return FullType.of(clazz, dbType);
    }

    private static FullType array(Class<? extends XmlaConstant> enumClass, XmlaConstants.DBType i4) {
        return FullType.of(enumClass, i4);
    }

    static class FullType {
        static FullType of(XmlaConstants.DBType dbType) {
            return null;
        }

        static FullType of(Class<? extends XmlaConstant> enumClass, XmlaConstants.DBType dbType) {
            return null;
        }

        private FullType(String java) {
            this.java = java;
        }

        private final String java;
    }
}

// End MetaModelTest.java
