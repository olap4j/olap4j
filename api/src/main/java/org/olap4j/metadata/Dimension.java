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
package org.olap4j.metadata;

import org.olap4j.OlapException;

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
 * @since Aug 22, 2006
 */
public interface Dimension extends MetadataElement {

    /**
     * Returns the cube that this {@code Dimension} belongs to.
     *
     * @return cube
     *
     * @since olap4j 2.0
     */
    Cube getCube();

    /**
     * Returns the ordinal number of this {@code Dimension} across all
     * hierarchies of the {@link Cube}.
     *
     * @return ordinal within cube
     *
     * @see #getCube()
     *
     * @since olap4j 2.0
     */
    int getOrdinal();

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
     * Returns the number of members in this {@code Dimension}. This value can
     * be an approximation of the real cardinality.
     *
     * @return number of members
     *
     * @since olap4j 2.0
     */
    int getCardinality();

    /**
     * Returns a bitmap that specifies which columns contain unique values
     * if this {@link Dimension} contains only members with unique names.
     *
     * @return unique settings
     *
     * @since olap4j 2.0
     */
    KeyUniqueness getUniqueSettings();

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
    public enum Type implements XmlaConstant {
        /**
         * Indicates that the dimension is not related to time.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_UNKNOWN(0)</code>.</p>
         */
        UNKNOWN(0),

        /**
         * Indicates that a dimension is a time dimension.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_TIME(1)</code>.</p>
         */
        TIME(1),

        /**
         * Indicates that a dimension is the Measures dimension.
         *
         * <p>Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_MEASURE(2)</code>.</p>
         */
        MEASURE(2),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_OTHER(3)</code>. */
        OTHER(3),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_QUANTITATIVE(5)</code>. */
        QUANTITATIVE(5),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_ACCOUNTS(6)</code>. */
        ACCOUNTS(6),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_CUSTOMERS(7)</code>. */
        CUSTOMERS(7),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_PRODUCTS(8)</code>. */
        PRODUCTS(8),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_SCENARIO(9)</code>. */
        SCENARIO(9),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_UTILIY(10)</code> (sic). */
        UTILITY(10) {
            public String xmlaName() {
                // The XMLA constant is apparently mis-spelled.
                // Who are we to question?
                return "MD_DIMTYPE_UTILIY";
            }
        },

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_CURRENCY(11)</code>. */
        CURRENCY(11),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_RATES(12)</code>. */
        RATES(12),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_CHANNEL(13)</code>. */
        CHANNEL(13),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_PROMOTION(14)</code>. */
        PROMOTION(14),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_ORGANIZATION(15)</code>. */
        ORGANIZATION(15),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_BILL_OF_MATERIALS(16)</code>. */
        BILL_OF_MATERIALS(16),

        /** Corresponds to the XMLA constant
         * <code>MD_DIMTYPE_GEOGRAPHY(17)</code>. */
        GEOGRAPHY(17);

        private final int xmlaOrdinal;

        /** Per {@link XmlaConstant}. */
        public static final Dictionary<Type> DICTIONARY =
            DictionaryImpl.forClass(Type.class);

        /**
         * Creates a Dimension Type.
         *
         * @param xmlaOrdinal Ordinal code as specified by XMLA
         */
        private Type(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MD_DIMTYPE_" + name();
        }

        public String getDescription() {
            return "";
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /** Flags that define whether a dimension's key is unique. */
    public enum KeyUniqueness implements XmlaConstant {
        /** Corresponds to the XMLA constant
         * <code>MDDIMENSIONS_MEMBER_KEY_UNIQUE</code> (1). */
        MEMBER_KEY_UNIQUE(1),

        /** Corresponds to the XMLA constant
         * <code>MDDIMENSIONS_MEMBER_NAME_UNIQUE</code> (2). */
        MEMBER_NAME_UNIQUE(2);

        private final int xmlaOrdinal;

        /** Per {@link org.olap4j.metadata.XmlaConstant}. */
        public static final DictionaryImpl<KeyUniqueness> DICTIONARY =
            DictionaryImpl.forClass(KeyUniqueness.class);

        private KeyUniqueness(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MDDIMENSIONS_" + name();
        }

        public String getDescription() {
            return "";
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /** Flags for forming restrictions whether to return dimensions based on
     * their visibility. */
    public enum Visibility implements XmlaConstant {
        /** Element is visible (1).  */
        VISIBLE(1),

        /** Element is not visible (2).  */
        NOT_VISIBLE(2);

        private final int xmlaOrdinal;

        /** Per {@link org.olap4j.metadata.XmlaConstant}. */
        public static final DictionaryImpl<Visibility> DICTIONARY =
            DictionaryImpl.forClass(Visibility.class);

        private Visibility(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return name();
        }

        public String getDescription() {
            return "";
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }
}

// End Dimension.java
