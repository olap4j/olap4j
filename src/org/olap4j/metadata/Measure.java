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

/**
 * Data value of primary interest to the user browsing the cube.
 *
 * <p>A <code>Measure</code> provides the value of each cell, and is usually
 * numeric. Every measure is a member of a special dimension called "Measures".
 *
 * @author jhyde
 * @version $Id$
 * @since Oct 13, 2006
 */
public interface Measure extends Member {

    /**
     * Enumeration of the aggregate functions which can be used to derive a
     * <code>Measure</code>.
     *
     * <p>The values are as specified by XMLA.
     * For example, XMLA specifies MDMEASURE_AGGR_SUM with ordinal 1,
     * which corresponds to the value {@link #SUM},
     * whose {@link #xmlaOrdinal} is 1.
     */
    enum Aggregator {
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
         * Identifies that the measure was derived from a formula that was not any single function above.
         */
        CALCULATED(127),

        /**
        * Identifies that the measure was derived from an unknown aggregation function or formula.
         */
        UNKNOWN(0);

        private final int xmlaOrdinal;

        private Aggregator(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        /**
         * Returns the ordinal code as specified by XMLA.
         *
         * <p>For example, the XMLA specification says that the ordinal of
         * {@link #CALCULATED} is 127.
         *
         * @return ordinal code as specified by XMLA.
         */
        public final int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }
}

// End Measure.java
