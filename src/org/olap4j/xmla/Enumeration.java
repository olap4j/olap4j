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

import org.olap4j.metadata.*;

import java.util.List;

/**
 * Contains inner classes which define enumerations used in XML for Analysis.
 */
public class Enumeration {
    public final String name;
    public final String description;
    public final XmlaType type;
    private final XmlaConstant.Dictionary<?> dictionary;

    public static final Enumeration TREE_OP =
        new Enumeration(
            "TREE_OP",
            "Bitmap which controls which relatives of a member are "
            + "returned",
            XmlaType.Integer,
            Member.TreeOp.DICTIONARY);

    public static final Enumeration VISUAL_MODE =
        new Enumeration(
            "VisualMode",
            "This property determines the default behavior for visual "
            + "totals.",
            XmlaType.Integer,
            XmlaConstants.VisualMode.DICTIONARY);

    public static final Enumeration METHODS =
        new Enumeration(
            "Methods",
            "Set of methods for which a property is applicable",
            XmlaType.Enumeration,
            XmlaConstants.Method.DICTIONARY);

    public static final Enumeration ACCESS =
        new Enumeration(
            "Access",
            "The read/write behavior of a property",
            XmlaType.Enumeration,
            XmlaConstants.Access.DICTIONARY);

    public static final Enumeration AUTHENTICATION_MODE =
        new Enumeration(
            "AuthenticationMode",
            "Specification of what type of security mode the data source "
            + "uses.",
            XmlaType.EnumString,
            XmlaConstants.AuthenticationMode.DICTIONARY);

    public static final Enumeration PROVIDER_TYPE =
        new Enumeration(
            "ProviderType",
            "The types of data supported by the provider.",
            XmlaType.Array,
            Database.ProviderType.DICTIONARY);

    public static final Enumeration REQUEST_TYPES =
        new Enumeration(
            "RequestTypes",
            "The types of data supported by the provider.",
            XmlaType.Array,
            RowsetDefinition.DICTIONARY);

    public static final Enumeration DIMENSION_TYPE =
        new Enumeration(
            "DimensionType",
            "The type of a dimension.",
            XmlaType.Short,
            Dimension.Type.DICTIONARY);

    public static final Enumeration DIMENSION_KEY_UNIQUENESS =
        new Enumeration(
            "DimensionKeyUniqueness",
            "Key uniqueness.",
            XmlaType.Integer,
            Dimension.KeyUniqueness.DICTIONARY);

    public static final Enumeration VISIBILITY =
        new Enumeration(
            "Visibility",
            "Whether a dimension is visible.",
            XmlaType.UnsignedShort,
            Dimension.Visibility.DICTIONARY);

    public static final Enumeration CUBE_TYPE =
        new Enumeration(
            "CubeType",
            "Type of a cube.",
            XmlaType.String,
            Cube.Type.DICTIONARY);

    public static final Enumeration LEVEL_TYPE =
        new Enumeration(
            "LevelType",
            "Type of a level.",
            XmlaType.Integer,
            Level.Type.DICTIONARY);

    public static final Enumeration LEVEL_CUSTOM_ROLLUP =
        new Enumeration(
            "LevelCustomRollup",
            "Custom rollup options for a level.",
            XmlaType.Integer,
            Level.CustomRollup.DICTIONARY);

    public static final Enumeration ORIGIN =
        new Enumeration(
            "Origin",
            "Source of a hierarchy.",
            XmlaType.Integer,
            Hierarchy.Origin.DICTIONARY);

    public static final Enumeration FUNCTION_ORIGIN =
        new Enumeration(
            "FunctionOrigin",
            "Origin of a function.",
            XmlaType.Integer,
            XmlaConstants.FunctionOrigin.DICTIONARY);

    public static final Enumeration DBTYPE =
        new Enumeration(
            "DBType",
            "Database type.",
            XmlaType.Integer,
            XmlaConstants.DBType.DICTIONARY);

    public static final Enumeration SET_SCOPE =
        new Enumeration(
            "SetScope",
            "Scope of a set.",
            XmlaType.Integer,
            NamedSet.Scope.DICTIONARY);

    public static final Enumeration SET_RESOLUTION =
        new Enumeration(
            "SetResolution",
            "The evaluation context for a set.",
            XmlaType.Integer,
            NamedSet.Resolution.DICTIONARY);

    public static final Enumeration INSTANCE_SELECTION =
        new Enumeration(
            "InstanceSelection",
            "A hint to the client application on how to show a hierarchy.",
            XmlaType.UnsignedShort,
            Hierarchy.InstanceSelection.DICTIONARY);

    public static final Enumeration GROUPING_BEHAVIOR =
        new Enumeration(
            "GroupingBehavior",
            "Expected grouping behavior of clients of a hierarchy.",
            XmlaType.Short,
            Hierarchy.GroupingBehavior.DICTIONARY);

    public static final Enumeration STRUCTURE =
        new Enumeration(
            "Structure",
            "The structure of a hierarchy",
            XmlaType.Short,
            Hierarchy.StructureType.DICTIONARY);

    public static final Enumeration STRUCTURE_TYPE =
        new Enumeration(
            "StructureType",
            "Type of hierarchy.",
            XmlaType.String,
            Hierarchy.StructureType.DICTIONARY);

    public static final Enumeration ACTION_TYPE =
        new Enumeration(
            "ActionType",
            "Triggering method of an action.",
            XmlaType.Integer,
            XmlaConstants.ActionType.DICTIONARY);

    public static final Enumeration COORDINATE_TYPE =
        new Enumeration(
            "CoordinateType",
            "Specifies how the coordinate restriction column of an action is "
            + "interpreted.",
            XmlaType.Integer,
            XmlaConstants.CoordinateType.DICTIONARY);

    public static final Enumeration INVOCATION =
        new Enumeration(
            "Invocation",
            "Information about how an action should be invoked.",
            XmlaType.Integer,
            XmlaConstants.Invocation.DICTIONARY);

    public static final Enumeration MEMBER_SCOPE =
        new Enumeration(
            "MemberScope",
            "Scope of a calculated member.",
            XmlaType.Integer,
            Member.Scope.DICTIONARY);

    public static final Enumeration MEMBER_TYPE =
        new Enumeration(
            "MemberType",
            "The type of a member.",
            XmlaType.Integer,
            Member.Type.DICTIONARY);

    public static final Enumeration PROPERTY_TYPE =
        new Enumeration(
            "PropertyType",
            "The type of a property.",
            XmlaType.Short,
            Property.TypeFlag.DICTIONARY);

    public static final Enumeration PROPERTY_CONTENT_TYPE =
        new Enumeration(
            "PropertyContentType",
            "The type of content contained by a property.",
            XmlaType.Short,
            Property.ContentType.DICTIONARY);

    public static final Enumeration KPI_SCOPE =
        new Enumeration(
            "KpiScope",
            "Scope of a KPI.",
            XmlaType.Integer,
            XmlaKpi.ScopeEnum.DICTIONARY);

    public Enumeration(
        String name,
        String description,
        XmlaType type,
        XmlaConstant.Dictionary<?> dictionary)
    {
        this.name = name;
        this.description = description;
        this.type = type;
        this.dictionary = dictionary;
    }

    public String getName() {
        return name;
    }

    public List<? extends Enum> getValues() {
        return dictionary.getValues();
    }
}

// End Enumeration.java
