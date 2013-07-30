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

import org.olap4j.OlapConnection;
import org.olap4j.OlapException;

import java.util.List;

/**
 * Highest level element in the hierarchy of metadata objects.
 *
 * <p>A Database contains one or more {@link Catalog}s.</p>
 *
 * <p>To obtain the collection of databases in the current server, call the
 * {@link OlapConnection#getOlapDatabases()} method. To obtain the current
 * active catalog object, to which a connection is bound, use
 * {@link OlapConnection#getOlapDatabase()}.
 *
 * <p>The hierarchy of metadata objects, rooted at the connection from which
 * they are accessed, is as follows:
 * <blockquote>
 * <ul>
 * <li type="circle">{@link org.olap4j.OlapConnection}<ul>
 *     <li type="circle">{@link Database}<ul>
 *         <li type="circle">{@link Catalog}<ul>
 *             <li type="circle">{@link Schema}<ul>
 *                 <li type="circle">{@link Cube}<ul>
 *                     <li type="circle">{@link Dimension}<ul>
 *                         <li type="circle">{@link Hierarchy}<ul>
 *                             <li type="circle">{@link Level}<ul>
 *                                 <li type="circle">{@link Member}</li>
 *                                 <li type="circle">{@link Property}</li>
 *                             </ul></li>
 *                         </ul></li>
 *                     </ul></li>
 *                 <li type="circle">{@link NamedSet}</li>
 *                 </ul></li>
 *             <li type="circle">{@link Dimension} (shared)</li>
 *             </ul></li>
 *         </ul></li>
 *     </ul></li>
 *  </ul>
 * </blockquote>
 * </p>
 *
 * @author Luc Boudreau
 * @since Jan 15 2011
 */
public interface Database {

    /**
     * Retrieves the parent {@link OlapConnection} of this
     * Database object.
     * @return The parent conenction object.
     */
    OlapConnection getOlapConnection();

    /**
     * Returns the unique name of this Database.
     * @return The database name.
     * @throws OlapException if error occurs.
     */
    String getName() throws OlapException;

    /**
     * Returns a human-readable description of this Database.
     *
     * @return The database description. Can be <code>null</code>.
     * @throws OlapException if error occurs.
     */
    String getDescription() throws OlapException;

    /**
     * Returns a redirection URL. This value is used only in
     * distributed architectures. An OLAP server can serve as a
     * frontal distribution server and redirect clients to delegate
     * servers.
     *
     * <p>Implementations are free to implement a distributed system.
     * Most implementations don't make any use of it and
     * will return the same URL which was used to connect in
     * the first place.
     *
     * @return The database URL. Can be <code>null</code>.
     * @throws OlapException if error occurs.
     */
    String getURL() throws OlapException;

    /**
     * Returns provider-specific information.
     *
     * @return A string containing provider-specific information.
     * @throws OlapException if error cccurs
     */
    String getDataSourceInfo() throws OlapException;

    /**
     * Returns the name of the underlying OLAP provider.
     *
     * <p>This usually is the server vendor name, for example "Mondrian" or
     * "MSOLAP".
     *
     * @return The provider name.
     * @throws OlapException if error occurs.
     */
    String getProviderName() throws OlapException;

    /**
     * Returns the types of data that are supported by this provider.
     *
     * @return The provider types.
     * @throws OlapException if error occurs.
     */
    List<ProviderType> getProviderTypes() throws OlapException;

    /**
     * Returns the authentication modes supported by this
     * server.
     * @return The authentication mode supported.
     * @throws OlapException if error occurs.
     */
    List<AuthenticationMode> getAuthenticationModes() throws OlapException;

    /**
     * Returns a list of {@link Catalog} objects which belong to
     * this Database.
     *
     * <p>The caller should assume that the list is immutable;
     * if the caller modifies the list, behavior is undefined.</p>
     *
     * @see org.olap4j.OlapConnection#getOlapCatalogs()
     * @return List of Catalog in this <code>Database</code>
     * @throws OlapException if error occurs
     */
    NamedList<Catalog> getCatalogs() throws OlapException;

    /**
     * Describes the supported authentication modes.
     */
    public enum AuthenticationMode {
        /**
         * Designates providers which don't support
         * authentication.
         */
        Unauthenticated("No user ID or password needs to be sent."),
        /**
         * Designates providers which support authentication
         * through the JDBC interface.
         */
        Authenticated(
            "User ID and Password must be included in the information required"
            + " for the connection."),
        /**
         * Designates providers which support authentication through
         * vendor or implementation specific means.
         */
        Integrated(
            "The data source uses the underlying security to determine "
            + "authorization, such as Integrated Security provided by "
            + "Microsoft Internet Information Services (IIS).");

        private final String description;

        AuthenticationMode(String description) {
            this.description = description;
        }

        /**
         * Provides a human readable description of the authentication mode.
         * @return A description string.
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * Describes the possible provider types.
     */
    public static enum ProviderType implements XmlaConstant {
        /**
         * Designates providers which provide results in the form of
         * tabular data sets.
         */
        TDP("Tabular Data Provider."),
        /**
         * Designates providers which provide results in the form of
         * multidimensional data sets.
         */
        MDP("Multidimensional Data Provider."),
        /**
         * Designates providers which provide results optimized for
         * data mining operations.
         */
        DMP(
            "Data Mining Provider. A DMP provider implements the OLE DB for "
            + "Data Mining specification.");

        private final String description;

        /** Per {@link XmlaConstant}. */
        public static final Dictionary<ProviderType> DICTIONARY =
            DictionaryImpl.forClass(ProviderType.class);

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
}

// End Database.java
