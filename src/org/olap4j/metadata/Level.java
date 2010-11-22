/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import org.olap4j.OlapException;

import java.util.*;

/**
 * Group of {@link Member} objects in a {@link Hierarchy},
 * all with the same attributes and at the same depth in the hierarchy.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 23, 2006
 */
public interface Level extends MetadataElement {
    /**
     * Returns the depth of this <code>Level</code>.
     *
     * <p>Note #1: In an access-controlled context, the first visible level of
     * a hierarchy may not have a depth of 0.</p>
     *
     * <p>Note #2: In a parent-child hierarchy, the depth of a member (as
     * returned by may not be the same as the depth of its level.
     *
     * @return depth of this level
     */
    int getDepth();

    /**
     * Returns the {@link Hierarchy} this <code>Level</code> belongs to.
     *
     * @return hierarchy this level belongs to
     */
    Hierarchy getHierarchy();

    /**
     * Returns the Dimension this <code>Level</code> belongs to.
     * (Always equivalent to <code>getHierarchy().getDimension()</code>.)
     *
     * @return dimension this level belongs to
     */
    Dimension getDimension();

    /**
     * Returns the type of this <code>Level</code>.
     *
     * @return level type
     */
    Level.Type getLevelType();

    /**
     * Returns whether the level is calculated.
     *
     * @return Whether this level is calculated
     */
    boolean isCalculated();

    /**
     * Returns a list of definitions for the properties available to members
     * of this <code>Level</code>.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapDatabaseMetaData#getProperties
     *
     * @return properties of this Level
     */
    NamedList<Property> getProperties();

    /**
     * Returns a list of Member objects which belong to this Level.
     *
     * <p>Some levels have a very many members. In this case, calling this
     * method may be expensive in space and/or time and is not recommended.
     *
     * <p>The members of a level do not have unique names, so unlike
     * {@link Hierarchy#getRootMembers()} and
     * {@link Member#getChildMembers()} the result type
     * is a {@link List} not a {@link NamedList}.
     *
     * @return List of members in this Level
     *
     * @throws OlapException if database error occurs
     */
    List<Member> getMembers() throws OlapException;

    /**
     * Returns the number of members in this Level.
     *
     * @return number of members
     */
    int getCardinality();

    /**
     * Enumeration of the types of a {@link Level}.
     *
     * <p>Several of the values are defined by OLE DB for OLAP and/or XML/A,
     * sans the "MDLEVEL_TYPE_" prefix to their name. For example,
     * {@link #GEO_CONTINENT} corresponds to
     * the value <code>MDLEVEL_TYPE_GEO_CONTINENT</code> for the
     * <code>LEVEL_TYPE</code> property in the <code>MDSCHEMA_LEVELS</code>
     * schema rowset.
     *
     * <p>Some of the values are specified by OLE DB for OLAP:
     * <ul>
     * <li>MDLEVEL_TYPE_REGULAR         (0x0000)
     * <li>MDLEVEL_TYPE_ALL             (0x0001)
     * <li>MDLEVEL_TYPE_TIME_YEARS      (0x0014)
     * <li>MDLEVEL_TYPE_TIME_HALF_YEAR  (0x0024)
     * <li>MDLEVEL_TYPE_TIME_QUARTERS   (0x0044)
     * <li>MDLEVEL_TYPE_TIME_MONTHS     (0x0084)
     * <li>MDLEVEL_TYPE_TIME_WEEKS      (0x0104)
     * <li>MDLEVEL_TYPE_TIME_DAYS       (0x0204)
     * <li>MDLEVEL_TYPE_TIME_HOURS      (0x0304)
     * <li>MDLEVEL_TYPE_TIME_MINUTES    (0x0404)
     * <li>MDLEVEL_TYPE_TIME_SECONDS    (0x0804)
     * <li>MDLEVEL_TYPE_TIME_UNDEFINED  (0x1004)
     * </ul>
     *
     * Some of the OLE DB for OLAP values are as flags, and do not become
     * values of the enumeration:
     * <ul>
     * <li>MDLEVEL_TYPE_UNKNOWN (0x0000) signals that no other flags are set.
     *     Use {@link #REGULAR}
     * <li>MDLEVEL_TYPE_CALCULATED (0x0002) indicates that the level is
     *     calculated. Use {@link Level#isCalculated}.
     * <li>MDLEVEL_TYPE_TIME (0x0004) indicates that the level is time-related.
     *     Use {@link #isTime}.
     * <li>MDLEVEL_TYPE_RESERVED1 (0x0008) is reserved for future use.
     * </ul>
     *
     * <p>Some of the values are specified by XMLA:
     * <ul>
     * <li>MDLEVEL_TYPE_GEO_CONTINENT (0x2001)
     * <li>MDLEVEL_TYPE_GEO_REGION (0x2002)
     * <li>MDLEVEL_TYPE_GEO_COUNTRY (0x2003)
     * <li>MDLEVEL_TYPE_GEO_STATE_OR_PROVINCE (0x2004)
     * <li>MDLEVEL_TYPE_GEO_COUNTY (0x2005)
     * <li>MDLEVEL_TYPE_GEO_CITY (0x2006)
     * <li>MDLEVEL_TYPE_GEO_POSTALCODE (0x2007)
     * <li>MDLEVEL_TYPE_GEO_POINT (0x2008)
     * <li>MDLEVEL_TYPE_ORG_UNIT (0x1011)
     * <li>MDLEVEL_TYPE_BOM_RESOURCE (0x1012)
     * <li>MDLEVEL_TYPE_QUANTITATIVE (0x1013)
     * <li>MDLEVEL_TYPE_ACCOUNT (0x1014)
     * <li>MDLEVEL_TYPE_CUSTOMER (0x1021)
     * <li>MDLEVEL_TYPE_CUSTOMER_GROUP (0x1022)
     * <li>MDLEVEL_TYPE_CUSTOMER_HOUSEHOLD (0x1023)
     * <li>MDLEVEL_TYPE_PRODUCT (0x1031)
     * <li>MDLEVEL_TYPE_PRODUCT_GROUP (0x1032)
     * <li>MDLEVEL_TYPE_SCENARIO (0x1015)
     * <li>MDLEVEL_TYPE_UTILITY (0x1016)
     * <li>MDLEVEL_TYPE_PERSON (0x1041)
     * <li>MDLEVEL_TYPE_COMPANY (0x1042)
     * <li>MDLEVEL_TYPE_CURRENCY_SOURCE (0x1051)
     * <li>MDLEVEL_TYPE_CURRENCY_DESTINATION (0x1052)
     * <li>MDLEVEL_TYPE_CHANNEL (0x1061)
     * <li>MDLEVEL_TYPE_REPRESENTATIVE (0x1062)
     * <li>MDLEVEL_TYPE_PROMOTION (0x1071)
     * </ul>
     *
     * @see Level#getLevelType
     * @see org.olap4j.OlapDatabaseMetaData#getLevels
     */
    public enum Type implements XmlaConstant {

        /**
         * Indicates that the level is not related to time.
         */
        REGULAR(0x0000),

        /**
         * Indicates that the level contains the 'all' member of its hierarchy.
         */
        ALL(0x0001),

        /**
         * Indicates that a level holds the null member. Does not correspond to
         * an XMLA or OLE DB value.
         */
        NULL(-1),

        /**
         * Indicates that a level refers to years.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_YEARS(0x0014),

        /**
         * Indicates that a level refers to half years.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_HALF_YEAR(0x0024),

        /**
         * Indicates that a level refers to quarters.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_QUARTERS(0x0044),

        /**
         * Indicates that a level refers to months.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_MONTHS(0x0084),

        /**
         * Indicates that a level refers to weeks.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_WEEKS(0x0104),

        /**
         * Indicates that a level refers to days.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_DAYS(0x0204),

        /**
         * Indicates that a level refers to hours.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_HOURS(0x0304),

        /**
         * Indicates that a level refers to minutes.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_MINUTES(0x0404),

        /**
         * Indicates that a level refers to seconds.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_SECONDS(0x0804),

        /**
         * Indicates that a level refers to days.
         * It must be used in a dimension whose type is
         * {@link org.olap4j.metadata.Dimension.Type#TIME}.
         */
        TIME_UNDEFINED(0x1004),

        GEO_CONTINENT(0x2001),
        GEO_REGION(0x2002),
        GEO_COUNTRY(0x2003),
        GEO_STATE_OR_PROVINCE(0x2004),
        GEO_COUNTY(0x2005),
        GEO_CITY(0x2006),
        GEO_POSTALCODE(0x2007),
        GEO_POINT(0x2008),
        ORG_UNIT(0x1011),
        BOM_RESOURCE(0x1012),
        QUANTITATIVE(0x1013),
        ACCOUNT(0x1014),
        CUSTOMER(0x1021),
        CUSTOMER_GROUP(0x1022),
        CUSTOMER_HOUSEHOLD(0x1023),
        PRODUCT(0x1031),
        PRODUCT_GROUP(0x1032),
        SCENARIO(0x1015),
        UTILITY(0x1016),
        PERSON(0x1041),
        COMPANY(0x1042),
        CURRENCY_SOURCE(0x1051),
        CURRENCY_DESTINATION(0x1052),
        CHANNEL(0x1061),
        REPRESENTATIVE(0x1062),
        PROMOTION(0x1071);

        private final int xmlaOrdinal;

        private static final Dictionary<Type> DICTIONARY =
            DictionaryImpl.forClass(Type.class);

        /**
         * Per {@link org.olap4j.metadata.XmlaConstant}, returns a dictionary
         * of all values of this enumeration.
         *
         * @return Dictionary of all values
         */
        public static Dictionary<Type> getDictionary() {
            return DICTIONARY;
        }

        /**
         * Creates a level type.
         *
         * @param xmlaOrdinal Ordinal code in XMLA or OLE DB for OLAP
         * specification
         */
        private Type(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MDLEVEL_TYPE_" + name();
        }

        public String getDescription() {
            return "";
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }

        /**
         * Returns whether this is a time-related level
         * ({@link #TIME_YEARS},
         * {@link #TIME_HALF_YEAR},
         * {@link #TIME_QUARTERS},
         * {@link #TIME_MONTHS},
         * {@link #TIME_WEEKS},
         * {@link #TIME_DAYS},
         * {@link #TIME_HOURS},
         * {@link #TIME_MINUTES},
         * {@link #TIME_SECONDS},
         * {@link #TIME_UNDEFINED}).
         *
         * @return whether this is a time-related level
         */
        public boolean isTime() {
            switch (this) {
            case TIME_YEARS:
            case TIME_HALF_YEAR:
            case TIME_QUARTERS:
            case TIME_MONTHS:
            case TIME_WEEKS:
            case TIME_DAYS:
            case TIME_HOURS:
            case TIME_MINUTES:
            case TIME_SECONDS:
            case TIME_UNDEFINED:
                return true;
            default:
                return false;
            }
        }
    }
}

// End Level.java
