/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2006-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import java.util.List;

import org.olap4j.OlapConnection;
import org.olap4j.OlapException;

/**
 * Highest level element in the hierarchy of metadata objects.
 *
 * <p>A Database contains one or more {@link Catalog}s.</p>
 *
 * <p>To obtain the collection of databases in the current server, call the
 * {@link OlapConnection#getOlapDatabases()} method. To obtain the current
 * active catalog object, to which a connection is bound, use
 * {@link OlapConnection#getOlapDatabase()()}.
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
 * @version $Id:$
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
     * Returns a human readable description of this Database.
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
     * Returns provider specific informations.
     * @return A string containing provider specific informations.
     * @throws OlapException if error cccurs
     */
    String getDataSourceInfo() throws OlapException;

    /**
     * Returns the name of the underlying OLAP provider.
     * This usually is the server vendor name, like Mondrian or
     * MSOLAP for example.
     * @return The provider name.
     * @throws OlapException if error occurs.
     */
    String getProviderName() throws OlapException;

    /**
     * Returns the types of data which are supported by this provider.
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
    public static enum ProviderType {
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

        private ProviderType(String description) {
            this.description = description;
        }

        /**
         * Provides a human readable description of the provider type.
         * @return A description string.
         */
        public String getDescription() {
            return description;
        }
    }
}
