/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import mondrian.olap.PropertyFormatter;

import java.util.Locale;

/**
 * Definition of a property of a Member or Cell.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 23, 2006
 */
public interface Property extends MetadataElement {
    /**
     * Returns the datatype of this Property.
     */
    Datatype getType();

    /**
     * Returns the scope of this property.
     */
    Scope getScope();

    boolean isInternal();

    enum Scope {
        MEMBER,
        CELL
    }

    enum Datatype {
        TYPE_STRING,
        TYPE_OTHER,
        TYPE_NUMERIC,
        TYPE_BOOLEAN
    }

    /**
     * Enumeration of the system properties available for every {@link Member}.
     *
     * <p>The following properties are mandatory for members:<ul>
     * <li>{@link #CATALOG_NAME}</li>
     * <li>{@link #SCHEMA_NAME}</li>
     * <li>{@link #CUBE_NAME}</li>
     * <li>{@link #DIMENSION_UNIQUE_NAME}</li>
     * <li>{@link #HIERARCHY_UNIQUE_NAME}</li>
     * <li>{@link #LEVEL_UNIQUE_NAME}</li>
     * <li>{@link #LEVEL_NUMBER}</li>
     * <li>{@link #MEMBER_UNIQUE_NAME}</li>
     * <li>{@link #MEMBER_NAME}</li>
     * <li>{@link #MEMBER_TYPE}</li>
     * <li>{@link #MEMBER_GUID}</li>
     * <li>{@link #MEMBER_CAPTION}</li>
     * <li>{@link #MEMBER_ORDINAL}</li>
     * <li>{@link #CHILDREN_CARDINALITY}</li>
     * <li>{@link #PARENT_LEVEL}</li>
     * <li>{@link #PARENT_UNIQUE_NAME}</li>
     * <li>{@link #PARENT_COUNT}</li>
     * <li>{@link #DESCRIPTION}</li>
     * </ul>
     */
    enum StandardMemberProperty implements Property {

        /**
         * Definition of the internal property which
         * holds a member's name.
         */
        $name(Datatype.TYPE_STRING, 2, true, null),

        /**
         * Definition of the internal property which
         * holds a member's caption.
         */
        $caption(Datatype.TYPE_STRING, 3, true, null),

        /**
         * Definition of the internal property which
         * holds, for a member of a  parent-child hierarchy, a
         * {@link java.util.List} containing the member's data
         * member and all of its children (including non-visible children).
         */
        $contributingChildren(Datatype.TYPE_OTHER, 4, true, null),

        /**
         * Definition of the internal property which
         * returns a calculated member's {@link mondrian.olap.Formula} object.
         */
        $formula(Datatype.TYPE_OTHER, 5, true, null),

        /**
         * Definition of the internal property which
         * describes whether a calculated member belongs to a query or a cube.
         */
        $member_scope(Datatype.TYPE_OTHER, 6, true, null),

        /**
         * Definition of the property which
         * holds the name of the current catalog.
         */
        CATALOG_NAME(Datatype.TYPE_STRING, 10, false, "Optional. The name of the catalog to which this member belongs. NULL if the provider does not support catalogs."),

        /**
         * Definition of the property which
         * holds the name of the current schema.
         */
        SCHEMA_NAME(Datatype.TYPE_STRING, 11, false, "Optional. The name of the schema to which this member belongs. NULL if the provider does not support schemas."),

        /**
         * Definition of the property which
         * holds the name of the current cube.
         */
        CUBE_NAME(Datatype.TYPE_STRING, 12, false, "Required. Name of the cube to which this member belongs."),

        /**
         * Definition of the property which
         * holds the unique name of the current dimension.
         */
        DIMENSION_UNIQUE_NAME(Datatype.TYPE_STRING, 13, false, "Required. Unique name of the dimension to which this member belongs. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which
         * holds the unique name of the current hierarchy.
         */
        HIERARCHY_UNIQUE_NAME(Datatype.TYPE_STRING, 14, false, "Required. Unique name of the hierarchy. If the member belongs to more than one hierarchy, there is one row for each hierarchy to which it belongs. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which
         * holds the unique name of the current level.
         */
        LEVEL_UNIQUE_NAME(Datatype.TYPE_STRING, 15, false, "Required. Unique name of the level to which the member belongs. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which
         * holds the ordinal of the current level.
         */
        LEVEL_NUMBER(Datatype.TYPE_STRING, 16, false, "Required. The distance of the member from the root of the hierarchy. The root level is zero."),

        /**
         * Definition of the property which
         * holds the ordinal of the current member.
         */
        MEMBER_ORDINAL(Datatype.TYPE_NUMERIC, 17, false, "Required. Ordinal number of the member. Sort rank of the member when members of this dimension are sorted in their natural sort order. If providers do not have the concept of natural ordering, this should be the rank when sorted by MEMBER_NAME."),

        /**
         * Definition of the property which
         * holds the name of the current member.
         */
        MEMBER_NAME(Datatype.TYPE_STRING, 18, false, "Required. Name of the member."),

        /**
         * Definition of the property which
         * holds the unique name of the current member.
         */
        MEMBER_UNIQUE_NAME(Datatype.TYPE_STRING, 19, false, "Required. Unique name of the member. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which
         * holds the type of the member.
         */
        MEMBER_TYPE(Datatype.TYPE_STRING, 20, false, "Required. Type of the member. Can be one of the following values: MDMEMBER_Datatype.TYPE_REGULAR, MDMEMBER_Datatype.TYPE_ALL, MDMEMBER_Datatype.TYPE_FORMULA, MDMEMBER_Datatype.TYPE_MEASURE, MDMEMBER_Datatype.TYPE_UNKNOWN. MDMEMBER_Datatype.TYPE_FORMULA takes precedence over MDMEMBER_Datatype.TYPE_MEASURE. Therefore, if there is a formula (calculated) member on the Measures dimension, it is listed as MDMEMBER_Datatype.TYPE_FORMULA."),

        /**
         * Definition of the property which
         * holds the GUID of the member
         */
        MEMBER_GUID(Datatype.TYPE_STRING, 21, false, "Optional. Member GUID. NULL if no GUID exists."),

        /**
         * Definition of the property which
         * holds the label or caption associated with the member, or the
         * member's name if no caption is defined.
         */
        MEMBER_CAPTION(Datatype.TYPE_STRING, 22, false, "Required. A label or caption associated with the member. Used primarily for display purposes. If a caption does not exist, MEMBER_NAME is returned."),

        /**
         * Definition of the property which holds the
         * number of children this member has.
         */
        CHILDREN_CARDINALITY(Datatype.TYPE_NUMERIC, 23, false, "Required. Number of children that the member has. This can be an estimate, so consumers should not rely on this to be the exact count. Providers should return the best estimate possible."),

        /**
         * Definition of the property which holds the
         * distance from the root of the hierarchy of this member's parent.
         */
        PARENT_LEVEL(Datatype.TYPE_NUMERIC, 24, false, "Required. The distance of the member's parent from the root level of the hierarchy. The root level is zero."),

        /**
         * Definition of the property which holds the
         * Name of the current catalog.
         */
        PARENT_UNIQUE_NAME(Datatype.TYPE_STRING, 25, false, "Required. Unique name of the member's parent. NULL is returned for any members at the root level. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which holds the
         * number of parents that this member has. Generally 1, or 0 for root members.
         */
        PARENT_COUNT(Datatype.TYPE_NUMERIC, 26, false, "Required. Number of parents that this member has."),

        /**
         * Definition of the property which holds the
         * description of this member.
         */
        DESCRIPTION(Datatype.TYPE_STRING, 27, false, "Optional. A human-readable description of the member."),

        /**
         * Definition of the internal property which holds the
         * name of the system property which determines whether to show a member
         * (especially a measure or calculated member) in a user interface such as
         * JPivot.
         */
        $visible(Datatype.TYPE_BOOLEAN, 28, true, null),

        /**
         * Definition of the property which
         * holds the level depth of a member.
         *
         * <p>Caution: Level depth of members in parent-child hierarchy isn't from their levels.
         * It's calculated from the underlying data dynamically.
         */
        DEPTH(Datatype.TYPE_NUMERIC, 43, true, "The level depth of a member"),

        /**
         * Definition of the property which
         * holds the DISPLAY_INFO required by XML/A.
         *
         * <p>Caution: This property's value is calculated based on a specified MDX query, so its value is dynamic at runtime.
         */
        DISPLAY_INFO(Datatype.TYPE_NUMERIC, 44, false, "Display instruction of a member for XML/A"),

        /**
         * Definition of the property which
         * holds the value of a cell. Is usually numeric (since most measures are
         * numeric) but is occasionally another type.
         */
        VALUE(Datatype.TYPE_NUMERIC, 41, false, "The unformatted value of the cell.");

        private final Datatype type;
        private final String description;
        private final boolean internal;

        private StandardMemberProperty(
            Datatype type,
            int ordinal,
            boolean internal,
            String description) {
            assert ordinal == ordinal();
            this.internal = internal;
            this.type = type;
            this.description = description;
        }

        public String getName() {
            return name();
        }

        public String getUniqueName() {
            return name();
        }

        public String getDescription(Locale locale) {
            return description;
        }

        public String getCaption(Locale locale) {
            return name();
        }

        public Datatype getType() {
            return type;
        }

        public Scope getScope() {
            return Scope.MEMBER;
        }

        public boolean isInternal() {
            return internal;
        }
    }

    /**
     * Enumeration of the system properties available for every
     * {@link org.olap4j.ResultCell}.
     *
     * <p>The following propertiess are mandatory for cells:<ul>
     * <li>{@link #BACK_COLOR}</li>
     * <li>{@link #CELL_EVALUATION_LIST}</li>
     * <li>{@link #CELL_ORDINAL}</li>
     * <li>{@link #FORE_COLOR}</li>
     * <li>{@link #FONT_NAME}</li>
     * <li>{@link #FONT_SIZE}</li>
     * <li>{@link #FONT_FLAGS}</li>
     * <li>{@link #FORMAT_STRING}</li>
     * <li>{@link #FORMATTED_VALUE}</li>
     * <li>{@link #NON_EMPTY_BEHAVIOR}</li>
     * <li>{@link #SOLVE_ORDER}</li>
     * <li>{@link #VALUE}</li>
     * </ul>
     */
    enum StandardCellProperty implements Property {
        BACK_COLOR(Datatype.TYPE_STRING, 30, false, "The background color for displaying the VALUE or FORMATTED_VALUE property. For more information, see FORE_COLOR and BACK_COLOR Contents."),

        CELL_EVALUATION_LIST(Datatype.TYPE_STRING, 31, false, "The semicolon-delimited list of evaluated formulas applicable to the cell, in order from lowest to highest solve order. For more information about solve order, see Understanding Pass Order and Solve Order"),

        CELL_ORDINAL(Datatype.TYPE_NUMERIC, 32, false, "The ordinal number of the cell in the dataset."),

        FORE_COLOR(Datatype.TYPE_STRING, 33, false, "The foreground color for displaying the VALUE or FORMATTED_VALUE property. For more information, see FORE_COLOR and BACK_COLOR Contents."),

        FONT_NAME(Datatype.TYPE_STRING, 34, false, "The font to be used to display the VALUE or FORMATTED_VALUE property."),

        FONT_SIZE(Datatype.TYPE_STRING, 35, false, "Font size to be used to display the VALUE or FORMATTED_VALUE property."),

        FONT_FLAGS(Datatype.TYPE_NUMERIC, 36, false, "The bitmask detailing effects on the font. The value is the result of a bitwise OR operation of one or more of the following constants: MDFF_BOLD  = 1, MDFF_ITALIC = 2, MDFF_UNDERLINE = 4, MDFF_STRIKEOUT = 8. For example, the value 5 represents the combination of bold (MDFF_BOLD) and underline (MDFF_UNDERLINE) font effects."),

        /**
         * Definition of the property which
         * holds the formatted value of a cell.
         */
        FORMATTED_VALUE(Datatype.TYPE_STRING, 37, false, "The character string that represents a formatted display of the VALUE property."),

        /**
         * Definition of the property which
         * holds the format string used to format cell values.
         */
        FORMAT_STRING(Datatype.TYPE_STRING, 38, false, "The format string used to create the FORMATTED_VALUE property value. For more information, see FORMAT_STRING Contents."),

        NON_EMPTY_BEHAVIOR(Datatype.TYPE_STRING, 39, false, "The measure used to determine the behavior of calculated members when resolving empty cells."),

        /**
         * Definition of the property which
         * determines the solve order of a calculated member with respect to other
         * calculated members.
         */
        SOLVE_ORDER(Datatype.TYPE_NUMERIC, 40, false, "The solve order of the cell."),

        /**
         * Definition of the property which
         * holds the value of a cell. Is usually numeric (since most measures are
         * numeric) but is occasionally another type.
         */
        VALUE(Datatype.TYPE_NUMERIC, 41, false, "The unformatted value of the cell."),

        /**
         * Definition of the property which
         * holds the datatype of a cell. Valid values are "String",
         * "Numeric", "Integer". The property's value derives from the
         * "datatype" attribute of the "Measure" element; if the datatype attribute
         * is not specified, the datatype is "Numeric" by default, except measures
         * whose aggregator is "Count", whose datatype is "Integer".
         */
        DATATYPE(Datatype.TYPE_STRING, 42, false, "The datatype of the cell.");

        /**
         * The datatype of the property.
         */
        private final Datatype type;
        private final String description;
        private final boolean internal;

        private StandardCellProperty(
            Datatype type,
            int ordinal,
            boolean internal,
            String description) {
            assert ordinal == ordinal();
            this.type = type;
            this.internal = internal;
            this.description = description;
        }

        public Datatype getType() {
            return type;
        }

        public Scope getScope() {
            return Scope.CELL;
        }

        public String getName() {
            return name();
        }

        public String getUniqueName() {
            return name();
        }

        public String getCaption(Locale locale) {
            return name();
        }

        public String getDescription(Locale locale) {
            return description;
        }

        public PropertyFormatter getFormatter() {
            return null;
        }

        public boolean isInternal() {
            return internal;
        }
    }
}

// End Property.java
