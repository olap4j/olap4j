/*
// $Id$
//
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
package org.olap4j.metadata;

/**
 * Collection of various enumerations and constants defined by the XML for
 * Analysis (XMLA) and OLE DB for OLAP specifications.
 *
 * @author jhyde
 * @version $Id$
 */
public class XmlaConstants
{
    // Suppresses default constructor, ensuring non-instantiability.
    private XmlaConstants() {
    }

    public enum VisualMode implements XmlaConstant {
        DEFAULT(
            0,
            "Provider-dependent. In Microsoft SQL Server 2000 Analysis "
            + "Services, this is equivalent to "
            + "DBPROPVAL_VISUAL_MODE_ORIGINAL."),
        VISUAL(
            1,
            "Visual totals are enabled."),
        ORIGINAL(
            2,
            "Visual totals are not enabled.");

        private final int xmlaOrdinal;
        private final String description;

        private static final DictionaryImpl<VisualMode> DICTIONARY =
            DictionaryImpl.forClass(VisualMode.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<VisualMode> getDictionary() {
            return DICTIONARY;
        }

        VisualMode(
            int xmlaOrdinal, String description)
        {
            this.xmlaOrdinal = xmlaOrdinal;
            this.description = description;
        }

        public String xmlaName() {
            return "DBPROPVAL_VISUAL_MODE_";
        }

        public String getDescription() {
            return description;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    public static enum Method implements XmlaConstant {
        DISCOVER,
        EXECUTE,
        DISCOVER_AND_EXECUTE;

        private static final DictionaryImpl<Method> DICTIONARY =
            DictionaryImpl.forClass(Method.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<Method> getDictionary() {
            return DICTIONARY;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return null;
        }

        public int xmlaOrdinal() {
            return -1;
        }
    }

    public enum Access implements XmlaConstant {
        Read(1),
        Write(2),
        ReadWrite(3);

        private final int xmlaOrdinal;

        private static final DictionaryImpl<Access> DICTIONARY =
            DictionaryImpl.forClass(Access.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<Access> getDictionary() {
            return DICTIONARY;
        }

        Access(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return null;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    public static enum AuthenticationMode implements XmlaConstant {
        Unauthenticated("no user ID or password needs to be sent."),
        Authenticated(
            "User ID and Password must be included in the information required "
            + "for the connection."),
        Integrated(
            "the data source uses the underlying security to determine "
            + "authorization, such as Integrated Security provided by "
            + "Microsoft Internet Information Services (IIS).");

        private final String description;

        private static final DictionaryImpl<AuthenticationMode> DICTIONARY =
            DictionaryImpl.forClass(AuthenticationMode.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<AuthenticationMode> getDictionary() {
            return DICTIONARY;
        }

        AuthenticationMode(String description) {
            this.description = description;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return description;
        }

        public int xmlaOrdinal() {
            return -1;
        }
    }

    public static enum ProviderType implements XmlaConstant {
        TDP("tabular data provider."),
        MDP("multidimensional data provider."),
        DMP(
            "data mining provider. A DMP provider implements the OLE DB for "
            + "Data Mining specification.");

        private final String description;

        private static final DictionaryImpl<ProviderType> DICTIONARY =
            DictionaryImpl.forClass(ProviderType.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<ProviderType> getDictionary() {
            return DICTIONARY;
        }

        private ProviderType(String description) {
            this.description = description;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return description;
        }

        public int xmlaOrdinal() {
            return -1;
        }
    }

    public static enum Updateable implements XmlaConstant {
        MD_MASK_ENABLED(
            0x00000000,
            "The cell can be updated."),

        MD_MASK_NOT_ENABLED(
            0x10000000,
            "The cell cannot be updated."),

        CELL_UPDATE_ENABLED(
            0x00000001,
            "Cell can be updated in the cellset."),

        CELL_UPDATE_ENABLED_WITH_UPDATE(
            0x00000002,
            "The cell can be updated with an update statement. The update may "
            + "fail if a leaf cell is updated that is not write-enabled."),

        CELL_UPDATE_NOT_ENABLED_FORMULA(
            0x10000001,
            "The cell cannot be updated because the cell has a calculated "
            + "member among its coordinates; the cell was retrieved with a set "
            + "in the where clause. A cell can be updated even though a "
            + "formula affects, or a calculated cell is on, the value of a "
            + "cell (is somewhere along the aggregation path). In this "
            + "scenario, the final value of the cell may not be the updated "
            + "value, because the calculation will affect the result."),

        CELL_UPDATE_NOT_ENABLED_NONSUM_MEASURE(
            0x10000002,
            "The cell cannot be updated because non-sum measures (count, min, "
            + "max, distinct count, semi-additive) can not be updated."),

        CELL_UPDATE_NOT_ENABLED_NACELL_VIRTUALCUBE(
            0x10000003,
            "The cell cannot be updated because the cell does not exist as it "
            + "is at the intersection of a measure and a dimension member "
            + "unrelated to the measure’s measure group."),

        CELL_UPDATE_NOT_ENABLED_SECURE(
            0x10000005,
            "The cell cannot be updated because the cell is secured."),

        CELL_UPDATE_NOT_ENABLED_CALCLEVEL(
            0x10000006,
            "Reserved for future use."),

        CELL_UPDATE_NOT_ENABLED_CANNOTUPDATE(
            0x10000007,
            "The cell cannot be updated because of internal reasons."),

        CELL_UPDATE_NOT_ENABLED_INVALIDDIMENSIONTYPE(
            0x10000009,
            "The cell cannot be updated because update is not supported in "
            + "mining model, indirect, or data mining dimensions.");

        private final int xmlaOrdinal;
        private final String description;

        private static final Dictionary<Updateable> DICTIONARY =
            DictionaryImpl.forClass(Updateable.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<Updateable> getDictionary() {
            return DICTIONARY;
        }

        Updateable(int xmlaOrdinal, String description) {
            this.xmlaOrdinal = xmlaOrdinal;
            this.description = description;
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

    public static enum FontFlag implements XmlaConstant {
        BOLD(1),
        ITALIC(2),
        UNDERLINE(4),
        STRIKEOUT(8);

        private final int xmlaOrdinal;

        private static final Dictionary<FontFlag> DICTIONARY =
            DictionaryImpl.forClass(FontFlag.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<FontFlag> getDictionary() {
            return DICTIONARY;
        }

        FontFlag(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MDFF_" + name();
        }

        public String getDescription() {
            return name();
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /**
     * Action type.
     *
     * <p>Fields correspond to XMLA constants MDACTION_TYPE_URL (0x01),
     * MDACTION_TYPE_HTML (0x02),
     * MDACTION_TYPE_STATEMENT (0x04),
     * MDACTION_TYPE_DATASET (0x08),
     * MDACTION_TYPE_ROWSET (0x10),
     * MDACTION_TYPE_COMMANDLINE (0x20),
     * MDACTION_TYPE_PROPRIETARY (0x40),
     * MDACTION_TYPE_REPORT (0x80),
     * MDACTION_TYPE_DRILLTHROUGH (0x100)</p>
     */
    public static enum ActionType implements XmlaConstant {
        URL(0x01),
        HTML(0x02),
        STATEMENT(0x04),
        DATASET(0x08),
        ROWSET(0x10),
        COMMANDLINE(0x20),
        PROPRIETARY(0x40),
        REPORT(0x80),
        DRILLTHROUGH(0x100);

        private final int xmlaOrdinal;

        private static final Dictionary<ActionType> DICTIONARY =
            DictionaryImpl.forClass(ActionType.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<ActionType> getDictionary() {
            return DICTIONARY;
        }

        ActionType(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MDACTION_TYPE_" + name();
        }

        public String getDescription() {
            return name();
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /**
     * How the COORDINATE restriction column is interpreted.
     *
     * <p>Fields correspond to the XMLA values
     * MDACTION_COORDINATE_CUBE (1),
     * MDACTION_COORDINATE_DIMENSION (2)
     * MDACTION_COORDINATE_LEVEL (3),
     * MDACTION_COORDINATE_MEMBER (4),
     * MDACTION_COORDINATE_SET (5),
     * MDACTION_COORDINATE_CELL (6)</p>
     */
    public static enum CoordinateType implements XmlaConstant {
        CUBE(1),
        DIMENSION(2),
        LEVEL(3),
        MEMBER(4),
        SET(5),
        CELL(6);

        private final int xmlaOrdinal;

        private static final Dictionary<ActionType> DICTIONARY =
            DictionaryImpl.forClass(ActionType.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<ActionType> getDictionary() {
            return DICTIONARY;
        }

        CoordinateType(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MDACTION_COORDINATE_" + name();
        }

        public String getDescription() {
            return name();
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /**
     * The only OLE DB Types Indicators returned by SQL Server are thoses coded
     * below.
     */
    public enum DBType implements XmlaConstant {
        // The following values exactly match VARENUM
        // in Automation and may be used in VARIANT.
        I4(
            "INTEGER", 3, "DBTYPE_I4", "A four-byte, signed integer: INTEGER"),

        R8(
            "DOUBLE", 5, "DBTYPE_R8",
            "A double-precision floating-point value: Double"),

        CY(
            "CURRENCY", 6, "DBTYPE_CY",
            "A currency value: LARGE_INTEGER, Currency is a fixed-point number "
            + "with four digits to the right of the decimal point. It is "
            + "stored in an eight-byte signed integer, scaled by 10,000."),

        BOOL(
            "BOOLEAN", 11, "DBTYPE_BOOL",
            "A Boolean value stored in the same way as in Automation: "
            + "VARIANT_BOOL; 0 means false and ~0 (bitwise, the value is not "
            + "0; that is, all bits are set to 1) means true."),

        /**
         * Used by SQL Server for value.
         */
        VARIANT(
            "VARIANT", 12, "DBTYPE_VARIANT", "An Automation VARIANT"),

        /**
         * Used by SQL Server for font size.
         */
        UI2("UNSIGNED_SHORT", 18, "DBTYPE_UI2", "A two-byte, unsigned integer"),

        /**
         * Used by SQL Server for colors, font flags and cell ordinal.
         */
        UI4(
            "UNSIGNED_INTEGER", 19, "DBTYPE_UI4",
            "A four-byte, unsigned integer"),

        // The following values exactly match VARENUM
        // in Automation but cannot be used in VARIANT.
        I8(
            "LARGE_INTEGER", 20, "DBTYPE_I8",
            "An eight-byte, signed integer: LARGE_INTEGER"),

        // The following values are not in VARENUM in OLE.
        WSTR(
            "STRING", 130, "DBTYPE_WSTR",
            "A null-terminated Unicode character string: wchar_t[length]; If "
            + "DBTYPE_WSTR is used by itself, the number of bytes allocated "
            + "for the string, including the null-termination character, is "
            + "specified by cbMaxLen in the DBBINDING structure. If "
            + "DBTYPE_WSTR is combined with DBTYPE_BYREF, the number of bytes "
            + "allocated for the string, including the null-termination "
            + "character, is at least the length of the string plus two. In "
            + "either case, the actual length of the string is determined from "
            + "the bound length value. The maximum length of the string is the "
            + "number of allocated bytes divided by sizeof(wchar_t) and "
            + "truncated to the nearest integer.");


        public final String userName;

        private final int xmlaOrdinal;

        private String description;

        private static final Dictionary<DBType> DICTIONARY =
            DictionaryImpl.forClass(DBType.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<DBType> getDictionary() {
            return DICTIONARY;
        }

        DBType(
            String userName,
            int xmlaOrdinal,
            String dbTypeIndicator,
            String description)
        {
            this.userName = userName;
            this.xmlaOrdinal = xmlaOrdinal;
            this.description = description;
            assert xmlaName().equals(dbTypeIndicator);
        }

        public String xmlaName() {
            return "DBTYPE_" + name();
        }

        public String getDescription() {
            return description;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    public enum Format implements XmlaConstant {
        Tabular(
            "a flat or hierarchical rowset. Similar to the XML RAW format in "
            + "SQL. The Format property should be set to Tabular for OLE DB "
            + "for Data Mining commands."),
        Multidimensional(
            "Indicates that the result set will use the MDDataSet format "
            + "(Execute method only)."),
        Native(
            "The client does not request a specific format, so the provider "
            + "may return the format  appropriate to the query. (The actual "
            + "result type is identified by namespace of the result.)");

        private final String description;

        private static final Dictionary<Format> DICTIONARY =
            DictionaryImpl.forClass(Format.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<Format> getDictionary() {
            return DICTIONARY;
        }

        Format(String description) {
            this.description = description;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return description;
        }

        public int xmlaOrdinal() {
            return -1;
        }
    }

    public enum AxisFormat implements XmlaConstant {
        TupleFormat(
            "The MDDataSet axis is made up of one or more CrossProduct "
            + "elements."),
        ClusterFormat(
            "Analysis Services uses the TupleFormat format for this setting."),
        CustomFormat(
            "The MDDataSet axis contains one or more Tuple elements.");

        private final String description;

        private static final XmlaConstant.Dictionary<AxisFormat> DICTIONARY =
            DictionaryImpl.forClass(AxisFormat.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static XmlaConstant.Dictionary<AxisFormat> getDictionary() {
            return DICTIONARY;
        }

        AxisFormat(String description) {
            this.description = description;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return description;
        }

        public int xmlaOrdinal() {
            return -1;
        }
    }

    public enum Content {
        None,
        Schema,
        Data,
        SchemaData,
        DataOmitDefaultSlicer,
        DataIncludeDefaultSlicer;

        /** The content type default value - shared across more than one file */
        public static final Content DEFAULT = SchemaData;
    }

    public enum MdxSupport {
        Core
    }

    public enum StateSupport {
        None,
        Sessions
    }

    public enum Literal implements XmlaConstant {
        CATALOG_NAME(
            2, null, 24, ".", "0123456789",
            "A catalog name in a text command."),
        CATALOG_SEPARATOR(3, ".", 0, null, null, null),
        COLUMN_ALIAS(5, null, -1, "'\"[]", "0123456789", null),
        COLUMN_NAME(6, null, -1, ".", "0123456789", null),
        CORRELATION_NAME(7, null, -1, "'\"[]", "0123456789", null),
        CUBE_NAME(21, null, -1, ".", "0123456789", null),
        DIMENSION_NAME(22, null, -1, ".", "0123456789", null),
        HIERARCHY_NAME(23, null, -1, ".", "0123456789", null),
        LEVEL_NAME(24, null, -1, ".", "0123456789", null),
        MEMBER_NAME(25, null, -1, ".", "0123456789", null),
        PROCEDURE_NAME(14, null, -1, ".", "0123456789", null),
        PROPERTY_NAME(26, null, -1, ".", "0123456789", null),
        QUOTE(
            15, "[", -1, null, null,
            "The character used in a text command as the opening quote for "
            + "quoting identifiers that contain special characters."),
        QUOTE_SUFFIX(
            28, "]", -1, null, null,
            "The character used in a text command as the closing quote for "
            + "quoting identifiers that contain special characters. 1.x "
            + "providers that use the same character as the prefix and suffix "
            + "may not return this literal value and can set the lt member of "
            + "the DBLITERAL structure to DBLITERAL_INVALID if requested."),
        TABLE_NAME(17, null, -1, ".", "0123456789", null),
        TEXT_COMMAND(
            18, null, -1, null, null,
            "A text command, such as an SQL statement."),
        USER_NAME(19, null, 0, null, null, null);

        // Enum DBLITERALENUM and DBLITERALENUM20, OLEDB.H.
        //
        // public static final int DBLITERAL_INVALID   = 0,
        //   DBLITERAL_BINARY_LITERAL    = 1,
        //   DBLITERAL_CATALOG_NAME  = 2,
        //   DBLITERAL_CATALOG_SEPARATOR = 3,
        //   DBLITERAL_CHAR_LITERAL  = 4,
        //   DBLITERAL_COLUMN_ALIAS  = 5,
        //   DBLITERAL_COLUMN_NAME   = 6,
        //   DBLITERAL_CORRELATION_NAME  = 7,
        //   DBLITERAL_CURSOR_NAME   = 8,
        //   DBLITERAL_ESCAPE_PERCENT    = 9,
        //   DBLITERAL_ESCAPE_UNDERSCORE = 10,
        //   DBLITERAL_INDEX_NAME    = 11,
        //   DBLITERAL_LIKE_PERCENT  = 12,
        //   DBLITERAL_LIKE_UNDERSCORE   = 13,
        //   DBLITERAL_PROCEDURE_NAME    = 14,
        //   DBLITERAL_QUOTE = 15,
        //   DBLITERAL_QUOTE_PREFIX = DBLITERAL_QUOTE,
        //   DBLITERAL_SCHEMA_NAME   = 16,
        //   DBLITERAL_TABLE_NAME    = 17,
        //   DBLITERAL_TEXT_COMMAND  = 18,
        //   DBLITERAL_USER_NAME = 19,
        //   DBLITERAL_VIEW_NAME = 20,
        //   DBLITERAL_CUBE_NAME = 21,
        //   DBLITERAL_DIMENSION_NAME    = 22,
        //   DBLITERAL_HIERARCHY_NAME    = 23,
        //   DBLITERAL_LEVEL_NAME    = 24,
        //   DBLITERAL_MEMBER_NAME   = 25,
        //   DBLITERAL_PROPERTY_NAME = 26,
        //   DBLITERAL_SCHEMA_SEPARATOR  = 27,
        //   DBLITERAL_QUOTE_SUFFIX  = 28;

        private int xmlaOrdinal;
        private final String literalValue;
        private final int literalMaxLength;
        private final String literalInvalidChars;
        private final String literalInvalidStartingChars;
        private final String description;

        private static final Dictionary<Literal> DICTIONARY =
            DictionaryImpl.forClass(Literal.class);

        /**
         * Per {@link XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<Literal> getDictionary() {
            return DICTIONARY;
        }

        Literal(
            int xmlaOrdinal,
            String literalValue,
            int literalMaxLength,
            String literalInvalidChars,
            String literalInvalidStartingChars,
            String description)
        {
            this.xmlaOrdinal = xmlaOrdinal;
            this.literalValue = literalValue;
            this.literalMaxLength = literalMaxLength;
            this.literalInvalidChars = literalInvalidChars;
            this.literalInvalidStartingChars = literalInvalidStartingChars;
            this.description = description;
        }

        public String getLiteralName() {
            return xmlaName();
        }

        public String getLiteralValue() {
            return literalValue;
        }

        public String getLiteralInvalidChars() {
            return literalInvalidChars;
        }

        public String getLiteralInvalidStartingChars() {
            return literalInvalidStartingChars;
        }

        public int getLiteralMaxLength() {
            return literalMaxLength;
        }

        public String xmlaName() {
            return "DBLITERAL_" + name();
        }

        public String getDescription() {
            return description;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    public interface EnumWithDesc {
        String getDescription();
    }
}

// End XmlaConstants.java
