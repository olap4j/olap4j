/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import java.util.*;

/**
 * Definition of a property of a {@link Member} or
 * {@link org.olap4j.Cell}.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 23, 2006
 */
public interface Property extends MetadataElement {
    /**
     * Returns the datatype of this Property.
     *
     * @return datatype of this Property
     */
    Datatype getDatatype();

    /**
     * Returns a set of flags which describe the type of this Property.
     *
     * @return type of this Property
     */
    Set<TypeFlag> getType();

    /**
     * Returns the content type of this Property.
     *
     * @return content type
     */
    ContentType getContentType();

    /**
     * Enumeration of aspects of the type of a Property. In particular, whether
     * it belongs to a member or a cell.
     *
     * <p>The values are as specified by XMLA for the PROPERTY_TYPE attribute
     * of the MDSCHEMA_PROPERTIES data set.
     * For example, XMLA specifies that the value 9 (0x1 | 0x8) means that a
     * property belongs to a member and is a binary large object (BLOB).
     * In this case, {@link Property#getType} will return the {@link Set}
     * {{@link #MEMBER}, {@link #BLOB}}.
     */
    enum TypeFlag {
        /**
         * Identifies a property of a member. This property can be used in the
         * DIMENSION PROPERTIES clause of the SELECT statement.
         */
        MEMBER(1),

        /**
         * Identifies a property of a cell. This property can be used in the
         * CELL PROPERTIES clause that occurs at the end of the SELECT
         * statement.
         */
        CELL(2),

        /**
         * Identifies an internal property.
         */
        SYSTEM(4),

        /**
         * Identifies a property which contains a binary large object (blob).
         */
        BLOB(8);

        public final int xmlaOrdinal;
        private static final Map<Integer, TypeFlag> xmlaMap =
            new HashMap<Integer, TypeFlag>();

        static {
            for (TypeFlag typeFlag : values()) {
                xmlaMap.put(typeFlag.xmlaOrdinal, typeFlag);
            }
        }

        private static final Set<TypeFlag> CELL_TYPE_FLAG =
            Collections.unmodifiableSet(EnumSet.of(TypeFlag.CELL));
        private static final Set<TypeFlag> MEMBER_TYPE_FLAG =
            Collections.unmodifiableSet(EnumSet.of(TypeFlag.MEMBER));

        private TypeFlag(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        /**
         * Looks up a TypeFlag by its XMLA ordinal.
         *
         * @param xmlaOrdinal Ordinal of a TypeFlag according to the XMLA
         * specification.
         *
         * @return TypeFlag with the given ordinal, or null if there is no
         * such TypeFlag
         */
        public static TypeFlag forXmlaOrdinal(int xmlaOrdinal) {
            return xmlaMap.get(xmlaOrdinal);
        }

        /**
         * Creates a set of TypeFlag values by parsing a mask.
         *
         * <p>For example, <code>forMask(9)</code> returns the set
         * {{@link #MEMBER}, {@link #BLOB}} because 9 = MEMBER (1) | BLOB (8).
         *
         * @param xmlaOrdinalMask Bit mask
         * @return Set of TypeFlag values
         */
        public static Set<TypeFlag> forMask(int xmlaOrdinalMask) {
            switch (xmlaOrdinalMask) {
            // Optimize common cases {MEMBER} and {CELL}.
            case 1:
                return MEMBER_TYPE_FLAG;
            case 2:
                return CELL_TYPE_FLAG;
            default:
                Set<TypeFlag> type =
                    EnumSet.noneOf(TypeFlag.class);
                for (TypeFlag typeFlag : values()) {
                    if ((xmlaOrdinalMask & typeFlag.xmlaOrdinal) != 0) {
                        type.add(typeFlag);
                    }
                }
                return type;
            }
        }
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
         * Definition of the property which
         * holds the name of the current catalog.
         */
        CATALOG_NAME(Datatype.STRING, 10, false, "Optional. The name of the catalog to which this member belongs. NULL if the provider does not support catalogs."),

        /**
         * Definition of the property which
         * holds the name of the current schema.
         */
        SCHEMA_NAME(Datatype.STRING, 11, false, "Optional. The name of the schema to which this member belongs. NULL if the provider does not support schemas."),

        /**
         * Definition of the property which
         * holds the name of the current cube.
         */
        CUBE_NAME(Datatype.STRING, 12, false, "Required. Name of the cube to which this member belongs."),

        /**
         * Definition of the property which
         * holds the unique name of the current dimension.
         */
        DIMENSION_UNIQUE_NAME(Datatype.STRING, 13, false, "Required. Unique name of the dimension to which this member belongs. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which
         * holds the unique name of the current hierarchy.
         */
        HIERARCHY_UNIQUE_NAME(Datatype.STRING, 14, false, "Required. Unique name of the hierarchy. If the member belongs to more than one hierarchy, there is one row for each hierarchy to which it belongs. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which
         * holds the unique name of the current level.
         */
        LEVEL_UNIQUE_NAME(Datatype.STRING, 15, false, "Required. Unique name of the level to which the member belongs. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which
         * holds the ordinal of the current level.
         */
        LEVEL_NUMBER(Datatype.UNSIGNED_INTEGER, 16, false, "Required. The distance of the member from the root of the hierarchy. The root level is zero."),

        /**
         * Definition of the property which
         * holds the ordinal of the current member.
         */
        MEMBER_ORDINAL(Datatype.UNSIGNED_INTEGER, 17, false, "Required. Ordinal number of the member. Sort rank of the member when members of this dimension are sorted in their natural sort order. If providers do not have the concept of natural ordering, this should be the rank when sorted by MEMBER_NAME."),

        /**
         * Definition of the property which
         * holds the name of the current member.
         */
        MEMBER_NAME(Datatype.STRING, 18, false, "Required. Name of the member."),

        /**
         * Definition of the property which
         * holds the unique name of the current member.
         */
        MEMBER_UNIQUE_NAME(Datatype.STRING, 19, false, "Required. Unique name of the member. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which
         * holds the type of the member.
         */
        MEMBER_TYPE(Datatype.STRING, 20, false, "Required. Type of the member. Can be one of the following values: MDMEMBER_Datatype.TYPE_REGULAR, MDMEMBER_Datatype.TYPE_ALL, MDMEMBER_Datatype.TYPE_FORMULA, MDMEMBER_Datatype.TYPE_MEASURE, MDMEMBER_Datatype.TYPE_UNKNOWN. MDMEMBER_Datatype.TYPE_FORMULA takes precedence over MDMEMBER_Datatype.TYPE_MEASURE. Therefore, if there is a formula (calculated) member on the Measures dimension, it is listed as MDMEMBER_Datatype.TYPE_FORMULA."),

        /**
         * Definition of the property which
         * holds the GUID of the member
         */
        MEMBER_GUID(Datatype.STRING, 21, false, "Optional. Member GUID. NULL if no GUID exists."),

        /**
         * Definition of the property which
         * holds the label or caption associated with the member, or the
         * member's name if no caption is defined.
         */
        MEMBER_CAPTION(Datatype.STRING, 22, false, "Required. A label or caption associated with the member. Used primarily for display purposes. If a caption does not exist, MEMBER_NAME is returned."),

        /**
         * Definition of the property which holds the
         * number of children this member has.
         */
        CHILDREN_CARDINALITY(Datatype.UNSIGNED_INTEGER, 23, false, "Required. Number of children that the member has. This can be an estimate, so consumers should not rely on this to be the exact count. Providers should return the best estimate possible."),

        /**
         * Definition of the property which holds the
         * distance from the root of the hierarchy of this member's parent.
         */
        PARENT_LEVEL(Datatype.UNSIGNED_INTEGER, 24, false, "Required. The distance of the member's parent from the root level of the hierarchy. The root level is zero."),

        /**
         * Definition of the property which holds the
         * Name of the current catalog.
         */
        PARENT_UNIQUE_NAME(Datatype.STRING, 25, false, "Required. Unique name of the member's parent. NULL is returned for any members at the root level. For providers that generate unique names by qualification, each component of this name is delimited."),

        /**
         * Definition of the property which holds the
         * number of parents that this member has. Generally 1, or 0 for root members.
         */
        PARENT_COUNT(Datatype.UNSIGNED_INTEGER, 26, false, "Required. Number of parents that this member has."),

        /**
         * Definition of the property which holds the
         * description of this member.
         */
        DESCRIPTION(Datatype.STRING, 27, false, "Optional. A human-readable description of the member."),

        /**
         * Definition of the internal property which holds the
         * name of the system property which determines whether to show a member
         * (especially a measure or calculated member) in a user interface such as
         * JPivot.
         */
        $visible(Datatype.BOOLEAN, 28, true, null),

        /**
         * Definition of the internal property which holds the
         * value of the member key in the original data type. MEMBER_KEY is for
         * backward-compatibility.  MEMBER_KEY has the same value as KEY0 for
         * non-composite keys, and MEMBER_KEY property is null for composite
         * keys.
         */
        MEMBER_KEY(Datatype.VARIANT, 29, true, "Optional. The value of the member key. Null for composite keys."),

        /**
         * Definition of the boolean property that indicates whether
         * a member is a placeholder member for an empty position in a
         * dimension hierarchy.
         */
        IS_PLACEHOLDERMEMBER(Datatype.BOOLEAN, 30, false, "Required. Whether the member is a placeholder member for an empty position in a dimension hierarchy."),

        /**
         * Definition of the property that indicates whether the member is a
         * data member.
         */
        IS_DATAMEMBER(Datatype.BOOLEAN, 31, false, "Required. whether the member is a data member"),

        /**
         * Definition of the property which
         * holds the level depth of a member.
         *
         * <p>Caution: Level depth of members in parent-child hierarchy isn't from their levels.
         * It's calculated from the underlying data dynamically.
         */
        DEPTH(Datatype.UNSIGNED_INTEGER, 43, true, "The level depth of a member"),

        /**
         * Definition of the property which
         * holds the DISPLAY_INFO required by XML/A.
         *
         * <p>Caution: This property's value is calculated based on a specified MDX query, so its value is dynamic at runtime.
         */
        DISPLAY_INFO(Datatype.UNSIGNED_INTEGER, 44, false, "Display instruction of a member for XML/A"),

        /**
         * Definition of the property which
         * holds the value of a cell. Is usually numeric (since most measures are
         * numeric) but is occasionally another type.
         */
        VALUE(Datatype.VARIANT, 41, false, "The unformatted value of the cell.");

        private final Datatype type;
        private final String description;
        private final boolean internal;

        private StandardMemberProperty(
            Datatype type,
            int ordinal,
            boolean internal,
            String description)
        {
//            assert ordinal == ordinal();
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

        public Datatype getDatatype() {
            return type;
        }

        public Set<TypeFlag> getType() {
            return TypeFlag.forMask(TypeFlag.MEMBER.xmlaOrdinal);
        }

        public ContentType getContentType() {
            return ContentType.REGULAR;
        }

        public boolean isInternal() {
            return internal;
        }
    }

    /**
     * Enumeration of the system properties available for every
     * {@link org.olap4j.Cell}.
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
        BACK_COLOR(Datatype.STRING, 30, false, "The background color for displaying the VALUE or FORMATTED_VALUE property. For more information, see FORE_COLOR and BACK_COLOR Contents."),

        CELL_EVALUATION_LIST(Datatype.STRING, 31, false, "The semicolon-delimited list of evaluated formulas applicable to the cell, in order from lowest to highest solve order. For more information about solve order, see Understanding Pass Order and Solve Order"),

        CELL_ORDINAL(Datatype.UNSIGNED_INTEGER, 32, false, "The ordinal number of the cell in the dataset."),

        FORE_COLOR(Datatype.STRING, 33, false, "The foreground color for displaying the VALUE or FORMATTED_VALUE property. For more information, see FORE_COLOR and BACK_COLOR Contents."),

        FONT_NAME(Datatype.STRING, 34, false, "The font to be used to display the VALUE or FORMATTED_VALUE property."),

        FONT_SIZE(Datatype.STRING, 35, false, "Font size to be used to display the VALUE or FORMATTED_VALUE property."),

        FONT_FLAGS(Datatype.UNSIGNED_INTEGER, 36, false, "The bitmask detailing effects on the font. The value is the result of a bitwise OR operation of one or more of the following constants: MDFF_BOLD  = 1, MDFF_ITALIC = 2, MDFF_UNDERLINE = 4, MDFF_STRIKEOUT = 8. For example, the value 5 represents the combination of bold (MDFF_BOLD) and underline (MDFF_UNDERLINE) font effects."),

        /**
         * Definition of the property which
         * holds the formatted value of a cell.
         */
        FORMATTED_VALUE(Datatype.STRING, 37, false, "The character string that represents a formatted display of the VALUE property."),

        /**
         * Definition of the property which
         * holds the format string used to format cell values.
         */
        FORMAT_STRING(Datatype.STRING, 38, false, "The format string used to create the FORMATTED_VALUE property value. For more information, see FORMAT_STRING Contents."),

        NON_EMPTY_BEHAVIOR(Datatype.STRING, 39, false, "The measure used to determine the behavior of calculated members when resolving empty cells."),

        /**
         * Definition of the property which
         * determines the solve order of a calculated member with respect to other
         * calculated members.
         */
        SOLVE_ORDER(Datatype.INTEGER, 40, false, "The solve order of the cell."),

        /**
         * Definition of the property which
         * holds the value of a cell. Is usually numeric (since most measures are
         * numeric) but is occasionally another type.
         */
        VALUE(Datatype.VARIANT, 41, false, "The unformatted value of the cell."),

        /**
         * Definition of the property which
         * holds the datatype of a cell. Valid values are "String",
         * "Numeric", "Integer". The property's value derives from the
         * "datatype" attribute of the "Measure" element; if the datatype attribute
         * is not specified, the datatype is "Numeric" by default, except measures
         * whose aggregator is "Count", whose datatype is "Integer".
         */
        DATATYPE(Datatype.STRING, 42, false, "The datatype of the cell.");

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
            String description)
        {
//            assert ordinal == ordinal();
            this.type = type;
            this.internal = internal;
            this.description = description;
        }

        public Datatype getDatatype() {
            return type;
        }

        public Set<TypeFlag> getType() {
            return TypeFlag.forMask(TypeFlag.CELL.xmlaOrdinal);
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

        public boolean isInternal() {
            return internal;
        }

        public ContentType getContentType() {
            return ContentType.REGULAR;
        }
    }

    /**
     * Enumeration of the types of a <code>Property</code>.
     *
     * <p>The values are as specified by XMLA.
     * For example, XMLA specifies MD_PROPTYPE_CAPTION with ordinal 0x21,
     * which corresponds to the value {@link #CAPTION},
     * whose {@link #xmlaOrdinal} is 0x21.
     */
    enum ContentType {
        REGULAR(0x00),
        ID(0x01),
        RELATION_TO_PARENT(0x02),
        ROLLUP_OPERATOR(0x03),
        ORG_TITLE(0x11),
        CAPTION(0x21),
        CAPTION_SHORT(0x22),
        CAPTION_DESCRIPTION(0x23),
        CAPTION_ABREVIATION(0x24),
        WEB_URL(0x31),
        WEB_HTML(0x32),
        WEB_XML_OR_XSL(0x33),
        WEB_MAIL_ALIAS(0x34),
        ADDRESS(0x41),
        ADDRESS_STREET(0x42),
        ADDRESS_HOUSE(0x43),
        ADDRESS_CITY(0x44),
        ADDRESS_STATE_OR_PROVINCE(0x45),
        ADDRESS_ZIP(0x46),
        ADDRESS_QUARTER(0x47),
        ADDRESS_COUNTRY(0x48),
        ADDRESS_BUILDING(0x49),
        ADDRESS_ROOM(0x4A),
        ADDRESS_FLOOR(0x4B),
        ADDRESS_FAX(0x4C),
        ADDRESS_PHONE(0x4D),
        GEO_CENTROID_X(0x61),
        GEO_CENTROID_Y(0x62),
        GEO_CENTROID_Z(0x63),
        GEO_BOUNDARY_TOP(0x64),
        GEO_BOUNDARY_LEFT(0x65),
        GEO_BOUNDARY_BOTTOM(0x66),
        GEO_BOUNDARY_RIGHT(0x67),
        GEO_BOUNDARY_FRONT(0x68),
        GEO_BOUNDARY_REAR(0x69),
        GEO_BOUNDARY_POLYGON(0x6A),
        PHYSICAL_SIZE(0x71),
        PHYSICAL_COLOR(0x72),
        PHYSICAL_WEIGHT(0x73),
        PHYSICAL_HEIGHT(0x74),
        PHYSICAL_WIDTH(0x75),
        PHYSICAL_DEPTH(0x76),
        PHYSICAL_VOLUME(0x77),
        PHYSICAL_DENSITY(0x78),
        PERSON_FULL_NAME(0x82),
        PERSON_FIRST_NAME(0x83),
        PERSON_LAST_NAME(0x84),
        PERSON_MIDDLE_NAME(0x85),
        PERSON_DEMOGRAPHIC(0x86),
        PERSON_CONTACT(0x87),
        QTY_RANGE_LOW(0x91),
        QTY_RANGE_HIGH(0x92),
        FORMATTING_COLOR(0xA1),
        FORMATTING_ORDER(0xA2),
        FORMATTING_FONT(0xA3),
        FORMATTING_FONT_EFFECTS(0xA4),
        FORMATTING_FONT_SIZE(0xA5),
        FORMATTING_SUB_TOTAL(0xA6),
        DATE(0xB1),
        DATE_START(0xB2),
        DATE_ENDED(0xB3),
        DATE_CANCELED(0xB4),
        DATE_MODIFIED(0xB5),
        DATE_DURATION(0xB6),
        VERSION(0xC1);

        private final int xmlaOrdinal;

        private static final Map<Integer, ContentType> xmlaMap =
            new HashMap<Integer, ContentType>();

        static {
            for (ContentType contentType : values()) {
                xmlaMap.put(contentType.xmlaOrdinal, contentType);
            }
        }

        /**
         * Returns the ordinal code as specified by XMLA.
         *
         * <p>For example, the XMLA specification says that the ordinal of
         * {@link #FORMATTING_FONT_EFFECTS} is 0xA4.
         *
         * @return ordinal code as specified by XMLA.
         */
        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }

        private ContentType(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        /**
         * Looks up a ContentType by its XMLA ordinal.
         *
         * @param xmlaOrdinal Ordinal of a ContentType according to the XMLA
         * specification.
         *
         * @return ContentType with the given ordinal, or null if there is no
         * such ContentType
         */
        public static ContentType forXmlaOrdinal(int xmlaOrdinal) {
            return xmlaMap.get(xmlaOrdinal);
        }
    }
}

// End Property.java
