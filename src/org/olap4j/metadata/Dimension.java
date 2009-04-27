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

import org.olap4j.OlapException;

import java.util.HashMap;
import java.util.Map;

/**
 * An organized hierarchy of categories, known as levels, that describes data
 * in a cube.
 *
 * <p>A Dimension typically describes a similar set of members upon which the
 * user wants to base an analysis.
 *
 * <p>A Dimension must have at least one Hierarchy, and may have more than one,
 * but most have exactly one Hierarchy.</p>
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface Dimension extends MetadataElement {

    /**
     * Returns the hierarchies in this Dimension.
     *
     * <p>Many dimensions have only one Hierarchy, whose name is the same as the
     * Dimension.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getHierarchies
     *
     * @return hierarchies in this dimension
     */
    NamedList<Hierarchy> getHierarchies();

    /**
     * Returns the type of this Dimension.
     *
     * @return dimension type
     *
     * @throws OlapException if database error occurs
     */
    Dimension.Type getDimensionType() throws OlapException;

    /**
     * Returns the default <code>Hierarchy</code> of this Dimension.
     *
     * @return default hierarchy
     */
    Hierarchy getDefaultHierarchy();

    /**
     * Enumeration of the types of a <code>Dimension</code>.
     *
     * <p>Some of the values are as specified by XMLA.
     * For example, XMLA specifies MD_DIMTYPE_PRODUCTS with ordinal 8,
     * which corresponds to the value {@link #PRODUCTS},
     * whose {@link #xmlaOrdinal} is 8.
     *
     * @see Level.Type
     * @see Member.Type
     * @see Dimension#getDimensionType
     */
    public enum Type {
        /**
         * Indicates that the dimension is not related to time.
         */
        UNKNOWN(0),

        /**
         * Indicates that a dimension is a time dimension.
         */
        TIME(1),

        /**
         * Indicates that a dimension is the Measures dimension.
         */
        MEASURE(2),

        OTHER(3),
        QUANTITATIVE(5),
        ACCOUNTS(6),
        CUSTOMERS(7),
        PRODUCTS(8),
        SCENARIO(9),
        UTILITY(10),
        CURRENCY(11),
        RATES(12),
        CHANNEL(13),
        PROMOTION(14),
        ORGANIZATION(15),
        BILL_OF_MATERIALS(16),
        GEOGRAPHY(17);

        private final int xmlaOrdinal;

        private static final Map<Integer, Type> xmlaOrdinalTypeMap;

        static {
            Map<Integer, Type> map = new HashMap<Integer, Type>();
            for (Type type : values()) {
                map.put(type.xmlaOrdinal, type);
            }
            xmlaOrdinalTypeMap = map;
        }

        /**
         * Creates a Dimension Type.
         *
         * @param xmlaOrdinal Ordinal code as specified by XMLA
         */
        private Type(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        /**
         * Returns the ordinal code as specified by XMLA.
         *
         * <p>For example, the XMLA specification says that the ordinal of
         * {@link #PRODUCTS} is 8.
         *
         * @return ordinal code as specified by XMLA.
         */
        public final int xmlaOrdinal() {
            return xmlaOrdinal;
        }

        /**
         * Returns the type whose XMLA ordinal code is as given.
         *
         * @param xmlaOrdinal Ordinal code as specified by XMLA
         * @return Dimension type, or null
         */
        public static Type forXmlaOrdinal(int xmlaOrdinal) {
            return xmlaOrdinalTypeMap.get(xmlaOrdinal);
        }
    }
}

// End Dimension.java
