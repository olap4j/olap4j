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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains inner classes which define enumerations used in XML for Analysis.
 */
public class Enumeration<E extends Enum<E> & XmlaConstant> {
    public final String name;
    public final String description;
    public final XmlaType type;
    private final Class<E> enumClass;
    private XmlaConstant.Dictionary<E> dictionary;

    public static final Enumeration<Member.TreeOp> TREE_OP =
        of(
            "TREE_OP",
            "Bitmap which controls which relatives of a member are "
            + "returned",
            XmlaType.Integer,
            Member.TreeOp.class);

    public static final Enumeration<XmlaConstants.VisualMode> VISUAL_MODE =
        of(
            "VisualMode",
            "This property determines the default behavior for visual "
            + "totals.",
            XmlaType.Integer,
            XmlaConstants.VisualMode.class);

    public static final Enumeration<XmlaConstants.Method> METHODS =
        of(
            "Methods",
            "Set of methods for which a property is applicable",
            XmlaType.Enumeration,
            XmlaConstants.Method.class);

    public static final Enumeration<XmlaConstants.Access> ACCESS =
        of(
            "Access",
            "The read/write behavior of a property",
            XmlaType.Enumeration,
            XmlaConstants.Access.class);

    public static final Enumeration<XmlaConstants.AuthenticationMode>
        AUTHENTICATION_MODE =
        of(
            "AuthenticationMode",
            "Specification of what type of security mode the data source "
            + "uses.",
            XmlaType.EnumString,
            XmlaConstants.AuthenticationMode.class);

    public static final Enumeration<Database.ProviderType> PROVIDER_TYPE =
        of(
            "ProviderType",
            "The types of data supported by the provider.",
            XmlaType.Array,
            Database.ProviderType.class);

    public static final Enumeration<RowsetDefinition> REQUEST_TYPES =
        of(
            "RequestTypes",
            "The types of data supported by the provider.",
            XmlaType.Array,
            RowsetDefinition.class);

    public static final Enumeration<Dimension.Type> DIMENSION_TYPE =
        of(
            "DimensionType",
            "The type of a dimension.",
            XmlaType.Short,
            Dimension.Type.class);

    public static final Enumeration<Dimension.KeyUniqueness>
        DIMENSION_KEY_UNIQUENESS =
        of(
            "DimensionKeyUniqueness",
            "Key uniqueness.",
            XmlaType.Integer,
            Dimension.KeyUniqueness.class);

    public static final Enumeration<Dimension.Visibility> VISIBILITY =
        of(
            "Visibility",
            "Whether a dimension is visible.",
            XmlaType.UnsignedShort,
            Dimension.Visibility.class);

    public static final Enumeration<Cube.Type> CUBE_TYPE =
        of(
            "CubeType",
            "Type of a cube.",
            XmlaType.String,
            Cube.Type.class);

    public static final Enumeration<Level.Type> LEVEL_TYPE =
        of(
            "LevelType",
            "Type of a level.",
            XmlaType.Integer,
            Level.Type.class);

    public static final Enumeration<Level.CustomRollup> LEVEL_CUSTOM_ROLLUP =
        of(
            "LevelCustomRollup",
            "Custom rollup options for a level.",
            XmlaType.Integer,
            Level.CustomRollup.class);

    public static final Enumeration<Hierarchy.Origin> ORIGIN =
        of(
            "Origin",
            "Source of a hierarchy.",
            XmlaType.Integer,
            Hierarchy.Origin.class);

    public static final Enumeration<XmlaConstants.FunctionOrigin>
        FUNCTION_ORIGIN =
        of(
            "FunctionOrigin",
            "Origin of a function.",
            XmlaType.Integer,
            XmlaConstants.FunctionOrigin.class);

    public static final Enumeration<XmlaConstants.DBType> DBTYPE =
        of(
            "DBType",
            "Database type.",
            XmlaType.Integer,
            XmlaConstants.DBType.class);

    public static final Enumeration<NamedSet.Scope> SET_SCOPE =
        of(
            "SetScope",
            "Scope of a set.",
            XmlaType.Integer,
            NamedSet.Scope.class);

    public static final Enumeration<NamedSet.Resolution> SET_RESOLUTION =
        of(
            "SetResolution",
            "The evaluation context for a set.",
            XmlaType.Integer,
            NamedSet.Resolution.class);

    public static final Enumeration<Hierarchy.InstanceSelection>
        INSTANCE_SELECTION =
        of(
            "InstanceSelection",
            "A hint to the client application on how to show a hierarchy.",
            XmlaType.UnsignedShort,
            Hierarchy.InstanceSelection.class);

    public static final Enumeration<Hierarchy.GroupingBehavior>
        GROUPING_BEHAVIOR =
        of(
            "GroupingBehavior",
            "Expected grouping behavior of clients of a hierarchy.",
            XmlaType.Short,
            Hierarchy.GroupingBehavior.class);

    public static final Enumeration<Hierarchy.Structure> STRUCTURE =
        of(
            "Structure",
            "The structure of a hierarchy",
            XmlaType.Short,
            Hierarchy.Structure.class);

    public static final Enumeration<Hierarchy.StructureType> STRUCTURE_TYPE =
        of(
            "StructureType",
            "Type of hierarchy.",
            XmlaType.String,
            Hierarchy.StructureType.class);

    public static final Enumeration<XmlaConstants.ActionType> ACTION_TYPE =
        of(
            "ActionType",
            "Triggering method of an action.",
            XmlaType.Integer,
            XmlaConstants.ActionType.class);

    public static final Enumeration<XmlaConstants.CoordinateType>
        COORDINATE_TYPE =
        of(
            "CoordinateType",
            "Specifies how the coordinate restriction column of an action is "
            + "interpreted.",
            XmlaType.Integer,
            XmlaConstants.CoordinateType.class);

    public static final Enumeration<XmlaConstants.Invocation> INVOCATION =
        of(
            "Invocation",
            "Information about how an action should be invoked.",
            XmlaType.Integer,
            XmlaConstants.Invocation.class);

    public static final Enumeration<Member.Scope> MEMBER_SCOPE =
        of(
            "MemberScope",
            "Scope of a calculated member.",
            XmlaType.Integer,
            Member.Scope.class);

    public static final Enumeration<Member.Type> MEMBER_TYPE =
        of(
            "MemberType",
            "The type of a member.",
            XmlaType.Integer,
            Member.Type.class);

    public static final Enumeration<Property.TypeFlag> PROPERTY_TYPE =
        of(
            "PropertyType",
            "The type of a property.",
            XmlaType.Short,
            Property.TypeFlag.class);

    public static final Enumeration<Property.ContentType>
        PROPERTY_CONTENT_TYPE =
        of(
            "PropertyContentType",
            "The type of content contained by a property.",
            XmlaType.Short,
            Property.ContentType.class);

    public static final Enumeration<XmlaKpi.ScopeEnum> KPI_SCOPE =
        of(
            "KpiScope",
            "Scope of a KPI.",
            XmlaType.Integer,
            XmlaKpi.ScopeEnum.class);

    private static <E extends Enum<E> & XmlaConstant> Enumeration<E> of(
        String name,
        String description,
        XmlaType type,
        Class<E> enumClass)
    {
        return new Enumeration<E>(name, description, type, enumClass);
    }

    private Enumeration(
        String name,
        String description,
        XmlaType type,
        Class<E> enumClass)
    {
        this.name = name;
        this.description = description;
        this.type = type;
        this.enumClass = enumClass;
    }

    public String getName() {
        return name;
    }

    /** Returns all instances of {@code Enumeration}. */
    public static List<Enumeration> getValues() {
        final List<Enumeration> list = new ArrayList<Enumeration>();
        for (Field field : Enumeration.class.getFields()) {
            if (Enumeration.class.isAssignableFrom(field.getType())) {
                try {
                    list.add((Enumeration) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }

    /**
     * Returns a dictionary of the values of this enumeration.
     */
    public synchronized XmlaConstant.Dictionary<E> getDictionary() {
        if (dictionary == null) {
            try {
                //noinspection unchecked
                dictionary = (XmlaConstant.Dictionary<E>)
                    enumClass.getField("DICTIONARY").get(null);
            } catch (IllegalAccessException e) {
                throw new AssertionError("must have DICTIONARY field");
            } catch (NoSuchFieldException e) {
                throw new AssertionError("must have DICTIONARY field");
            }
        }
        return dictionary;
    }
}

// End Enumeration.java
