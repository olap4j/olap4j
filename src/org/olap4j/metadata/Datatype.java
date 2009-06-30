/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import java.util.Map;
import java.util.HashMap;

/**
 * Enumeration of the allowable data types of a Property or Measure.
 *
 * <p>The values derive from the OLE DB specification, specifically a
 * subset of the OLE DB Types Indicators returned by SQL Server.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 23, 2006
 */
public enum Datatype {
    /*
    * The following values exactly match VARENUM
    * in Automation and may be used in VARIANT.
    */
    INTEGER(3, "DBTYPE_I4", "A four-byte, signed integer: INTEGER"),

    DOUBLE(5, "DBTYPE_R8", "A double-precision floating-point value: Double"),

    CURRENCY(
        6,
        "DBTYPE_CY",
        "A currency value: LARGE_INTEGER, Currency is a fixed-point number with "
        + "four digits to the right of the decimal point. It is stored in an "
        + "eight-byte signed integer, scaled by 10,000."),

    BOOLEAN(
        11,
        "DBTYPE_BOOL",
        "A Boolean value stored in the same way as in Automation: VARIANT_BOOL; "
        + "0 means false and ~0 (bitwise, the value is not 0; that is, all bits "
        + "are set to 1) means true."),

    /**
     * Used by SQL Server for value.
     */
    VARIANT(12, "DBTYPE_VARIANT", "An Automation VARIANT"),

    /**
     * Used by SQL Server for font size.
     */
    UNSIGNED_SHORT(18, "DBTYPE_UI2", "A two-byte, unsigned integer"),

    /**
     * Used by SQL Server for colors, font flags and cell ordinal.
     */
    UNSIGNED_INTEGER(19, "DBTYPE_UI4", "A four-byte, unsigned integer"),

    /*
    * The following values exactly match VARENUM
    * in Automation but cannot be used in VARIANT.
    */
    LARGE_INTEGER(
        20,
        "DBTYPE_I8",
        "An eight-byte, signed integer: LARGE_INTEGER"),

    /*
    * The following values are not in VARENUM in OLE.
    */
    STRING(
        130,
        "DBTYPE_WSTR",
        "A null-terminated Unicode character string: wchar_t[length]; If "
        + "DBTYPE_WSTR is used by itself, the number of bytes allocated "
        + "for the string, including the null-termination character, is "
        + "specified by cbMaxLen in the DBBINDING structure. If "
        + "DBTYPE_WSTR is combined with DBTYPE_BYREF, the number of bytes "
        + "allocated for the string, including the null-termination character, "
        + "is at least the length of the string plus two. In either case, the "
        + "actual length of the string is determined from the bound length "
        + "value. The maximum length of the string is the number of allocated "
        + "bytes divided by sizeof(wchar_t) and truncated to the nearest "
        + "integer.");

    private final int xmlaOrdinal;

    private static final Map<Integer, Datatype> xmlaMap =
        new HashMap<Integer, Datatype>();

    static {
        for (Datatype datatype : values()) {
            xmlaMap.put(datatype.xmlaOrdinal, datatype);
        }
    }

    Datatype(
        int xmlaOrdinal,
        String dbTypeIndicator,
        String description)
    {
        this.xmlaOrdinal = xmlaOrdinal;
    }

    /**
     * Looks up a Datatype by its XMLA ordinal.
     *
     * @param xmlaOrdinal Ordinal of a Datatype according to the XMLA
     * specification.
     *
     * @return Datatype with the given ordinal, or null if there is no
     * such Datatype
     */
    public static Datatype forXmlaOrdinal(int xmlaOrdinal) {
        return xmlaMap.get(xmlaOrdinal);
    }

}

// End Datatype.java
