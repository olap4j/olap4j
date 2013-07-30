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

import org.olap4j.mdx.ParseTreeNode;

/**
 * Metadata object describing a named set defined against a {@link Cube}.
 *
 * @author jhyde
 * @since Oct 24, 2006
 */
public interface NamedSet extends MetadataElement {
    /**
     * Returns the <code>Cube</code> that this <code>NamedSet</code> belongs
     * to.
     *
     * @return cube this named set belongs to
     */
    Cube getCube();

    /**
     * Returns the expression which gives the value of this NamedSet.
     *
     * @return expression
     */
    ParseTreeNode getExpression();

    /** The scope of a set. */
    enum Scope implements XmlaConstant {
        /**
         * The set has global scope.
         *
         * <p>Corresponds to the OLE DB for OLAP constant
         * <code>MDSET_SCOPE_GLOBAL</code> (1).</p>
         */
        GLOBAL(1),

        /**
         * The set has session scope.
         *
         * <p>Corresponds to the OLE DB for OLAP constant
         * <code>MDSET_SCOPE_SESSION</code> (2).</p>
         */
        SESSION(2);

        private final int xmlaOrdinal;

        /** Per {@link XmlaConstant}. */
        public static final Dictionary<Scope> DICTIONARY =
            DictionaryImpl.forClass(Scope.class);

        Scope(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MDSET_SCOPE_" + name();
        }

        public String getDescription() {
            return null;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }

    /** The context for a set. */
    enum Resolution implements XmlaConstant {
        /**
         * The set is evaluated in static context.
         *
         * <p>Corresponds to the OLE DB for OLAP constant
         * <code>MDSET_RESOLUTION_STATIC</code> (1).</p>
         */
        STATIC(1),

        /**
         * The set is evaluated in dynamic context.
         *
         * <p>Corresponds to the OLE DB for OLAP constant
         * <code>MDSET_RESOLUTION_DYNAMIC</code> (2).</p>
         */
        DYNAMIC(2);

        private final int xmlaOrdinal;

        public static final Dictionary<Resolution> DICTIONARY =
            DictionaryImpl.forClass(Resolution.class);

        Resolution(int xmlaOrdinal) {
            this.xmlaOrdinal = xmlaOrdinal;
        }

        public String xmlaName() {
            return "MDSET_Resolution_" + name();
        }

        public String getDescription() {
            return null;
        }

        public int xmlaOrdinal() {
            return xmlaOrdinal;
        }
    }
}

// End NamedSet.java
