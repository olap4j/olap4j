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

/**
 * Data value of primary interest to the user browsing the cube.
 *
 * <p>A <code>Measure</code> provides the value of each cell, and is usually
 * numeric. Every measure is a member of a special dimension called "Measures".
 *
 * @author jhyde
 * @since Oct 13, 2006
 */
public interface Measure extends Member {
    /**
     * Returns the Aggregator of this Measure.
     *
     * @return Aggregator
     */
    Aggregator getAggregator();

    /**
     * Returns the data type of this Measure.
     *
     * @return data type
     */
    Datatype getDatatype();

    /**
     * Returns whether this Measure is visible.
     *
     * @return whether this Measure is visible
     */
    boolean isVisible();

    /**
     * Returns the path to be used when displaying this {@code Measure} in a
     * user interface. Folder names are be separated by a semicolon (;). Nested
     * folders are indicated by a backslash (\).
     *
     * @return folder path
     *
     * @since olap4j 2.0
     */
    String getDisplayFolder();

    /**
     * Enumeration of the aggregate functions which can be used to derive a
     * <code>Measure</code>.
     *
     * <p>The values are as specified by XMLA.
     * For example, XMLA specifies MDMEASURE_AGGR_SUM with ordinal 1,
     * which corresponds to the value {@link #SUM},
     * whose {@link #xmlaOrdinal} is 1.
     */
    enum Aggregator implements XmlaConstant {
        /**
         * Identifies that the measure was derived using the
         * SUM aggregation function.
         */
        SUM(1),
        /**
         * Identifies that the measure was derived using the
         * COUNT aggregation function.
         */
        COUNT(2),
        /**
         * Identifies that the measure was derived using the
         * MIN aggregation function.
         */
        MIN(3),
        /**
         * Identifies that the measure was derived using the
         * MAX aggregation function.
         */
        MAX(4),
        /**
         * Identifies that the measure was derived using the
         * AVG aggregation function.
         */
        AVG(5),
        /**
         * Identifies that the measure was derived using the
         * VAR aggregation function.
         */
        VAR(6),
        /**
         * Identifies that the measure was derived using the
         * STDEV aggregation function.
         */
        STD(7),
        /**
         * Identifies that the measure was derived from a formula that was not
         * any single function above.
         */
        CALCULATED(127),

        /**
        * Identifies that the measure was derived from an unknown aggregation
        * function or formula.
         */
        UNKNOWN(0);

        private final int xmlaOrdinal;

        /** Per {@link XmlaConstant}. */
        public static final Dictionary<Aggregator> DICTIONARY =
            DictionaryImpl.forClass(Aggregator.class);

        /**
         * Creates an Aggregator.
         *
         * @param xmlaOrdinal Ordinal of the aggregator in the XMLA
         * specification
         */
        private Aggregator(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MDMEASURE_AGGR_" + name();
        }

        public String getDescription() {
            return "";
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }
}

// End Measure.java
