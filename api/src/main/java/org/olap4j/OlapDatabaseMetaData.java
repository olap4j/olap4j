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
package org.olap4j;

import org.olap4j.metadata.Member;

import java.sql.*;
import java.util.Set;

/**
 * Information about an OLAP database.
 *
 * <p>Methods are provided to query the metadata catalog of the database.
 * There is a method for each metadata class, and each method takes zero or more
 * parameters to qualify the instances should be returned, and returns a JDBC
 * {@link java.sql.ResultSet}.
 *
 * <p>For example, {@link #getCubes} returns the description of a cube.
 *
 * @author jhyde
 * @since Oct 12, 2006
 */
public interface OlapDatabaseMetaData extends DatabaseMetaData, OlapWrapper {

    // override return type
    /**
     * {@inheritDoc}
     */
    OlapConnection getConnection() throws SQLException;

    /**
     * Returns the granularity of changes to cell sets that the database is
     * capable of providing.
     *
     * <p>It's optional whether an olap4j provider supports cellset listeners,
     * and also optional which granularities it supports. If the provider does
     * not support the cell set listener API, returns an empty set. Never
     * returns null.
     *
     * @return set of the granularities that are supported when listening for
     * changes to a cell set, never null
     */
    Set<CellSetListener.Granularity> getSupportedCellSetListenerGranularities()
        throws OlapException;

    /**
     * Retrieves a result set describing the Actions in this database.
     *
     * <p>Specification as for XML/A MDSCHEMA_ACTIONS schema rowset.
     *
     * <p>Each action description has the following columns:
     * <ol>
     * <li><b>CATALOG_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the database.</li>
     * <li><b>SCHEMA_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the schema to which this action belongs.</li>
     * <li><b>CUBE_NAME</b> String &rarr; The name of the cube to which this
     *         action belongs.</li>
     * <li><b>ACTION_NAME</b> String &rarr; The name of the action.</li>
     * <li><b>COORDINATE</b> String &rarr; null</li>
     * <li><b>COORDINATE_TYPE</b> int &rarr; null</li>
     * </ol>
     *
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used
     *        to narrow the search
     *
     * @param schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a
     *        schema; <code>null</code> means that the schema name should not
     *        be used to narrow the search
     *
     * @param cubeNamePattern a cube name pattern; must match the
     *        cube name as it is stored in the database; "" retrieves those
     *        without a cube (such as shared dimensions);
     *        <code>null</code> means that the cube name should
     *        not be used to narrow the search
     *
     * @param actionNamePattern an action name pattern; must match the
     *        action name as it is stored in the database; <code>null</code>
     *        means that the action name should not be used to narrow the
     *        search
     *
     * @return a <code>ResultSet</code> object in which each row is an
     *         action description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     */
    ResultSet getActions(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String actionNamePattern) throws OlapException;

    /**
     * Retrieves a row set describing the databases that are available on the
     * server.
     *
     * <p>Specification as for XML/A DISCOVER_DATASOURCES schema rowset.
     *
     * <ol>
     * <li><b>DATA_SOURCE_NAME</b> String &rarr; The name of the data source,
     *         such as FoodMart 2000.</li>
     * <li><b>DATA_SOURCE_DESCRIPTION</b> String &rarr; A description of the
     *         data source, as entered by the publisher. (may be
     *         <code>null</code>)</li>
     * <li><b>URL</b> String &rarr; The unique path that shows where to invoke
     *         the XML for Analysis methods for that data source. (may be
     *         <code>null</code>)</li>
     * <li><b>DATA_SOURCE_INFO</b> String &rarr; A string containing any
     *         additional information required to connect to the data source.
     *         This can include the Initial Catalog property or other
     *         information for the provider.<br>
     *         <br>
     *         Example: "Provider=MSOLAP;Data Source=Local;"
     *         (may be <code>null</code>)</li>
     * <li><b>PROVIDER_NAME</b> String &rarr; The name of the provider behind
     *         the data source.<br>
     *         <br>
     *         Example: "MSDASQL" (may be <code>null</code>)</li>
     * <li><b>PROVIDER_TYPE</b> EnumerationArray &rarr; The types of data
     *         supported by the provider. May include one or more values of
     *         type {@link org.olap4j.metadata.Database.ProviderType}.</li>
     * <li><b>AUTHENTICATION_MODE</b> EnumString &rarr; Specification of what
     *         type of security mode the data source uses. Values can be of type
     *         {@link org.olap4j.metadata.Database.AuthenticationMode}.</li>
     * </ol>
     *
     * @return a <code>ResultSet</code> object in which each row is an
     *         OLAP database description
     * @throws OlapException if a database access error occurs
     */
    ResultSet getDatabases() throws OlapException;

    /**
     * Retrieves a list of information on supported literals, including data
     * types and values.
     *
     * <p>Specification as for XML/A DISCOVER_LITERALS schema rowset.
     *
     * <ol>
     * <li><b>LITERAL_NAME</b> String &rarr; The name of the literal described
     *         in the row.<br>
     *         <br>
     *         Example: DBLITERAL_LIKE_PERCENT</li>
     * <li><b>LITERAL_VALUE</b> String (may be <code>null</code>) &rarr;
     *         Contains the actual literal value.<br>
     *         <br>
     *         Example, if LiteralName is
     *         DBLITERAL_LIKE_PERCENT and the percent character (%) is used
     *         to match zero or more characters in a LIKE clause, this
     *         column's value would be "%".</li>
     * <li><b>LITERAL_INVALID_CHARS</b> String (may be <code>null</code>) &rarr;
     *         The characters, in the literal, that are not valid.<br>
     *         <br>
     *         For example, if table names can contain anything other than a
     *         numeric character, this string would be "0123456789".</li>
     * <li><b>LITERAL_INVALID_STARTING_CHARS</b> String (may be
     *         <code>null</code>) &rarr; The characters that are not valid as
     *         the first character of the literal. If the literal can start with
     *         any valid character, this is null.</li>
     * <li><b>LITERAL_MAX_LENGTH</b> int (may be <code>null</code>) &rarr; The
     *         maximum number of characters in the literal. If there is no
     *         maximum or the maximum is unknown, the value is -1.</li>
     * </ol>
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         literal description
     *
     * @exception OlapException if a database access error occurs
     */
    ResultSet getLiterals() throws OlapException;

    /**
     * Retrieves a list of the standard and provider-specific properties
     * supported by an olap4j provider. Properties that are not supported by a
     * provider are not listed in the return result set.
     *
     * <p>Specification as for XML/A DISCOVER_PROPERTIES schema rowset.
     *
     * <p>Not to be confused with {@link #getProperties}.
     *
     * <ol>
     * <li><b>PROPERTY_NAME</b> String &rarr; The name of the property.</li>
     * <li><b>PROPERTY_DESCRIPTION</b> String &rarr; A localizable text
     *         description of the property.</li>
     * <li><b>PROPERTY_TYPE</b> String &rarr; The XML data type of the
     *         property.</li>
     * <li><b>PROPERTY_ACCESS_TYPE</b> EnumString &rarr; Access for the
     *         property. The value can be Read, Write, or ReadWrite.</li>
     * <li><b>IS_REQUIRED</b> Boolean &rarr; True if a property is required,
     *         false if it is not required.</li>
     * <li><b>PROPERTY_VALUE</b> String &rarr; The current value of the
     *         property.</li>
     * </ol>
     *
     * @param dataSourceName Name of data source
     *
     * @param propertyNamePattern an property name pattern; must match the
     *        property name as it is stored in the database; <code>null</code>
     *        means that the property name should not be used to narrow the
     *        search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         the description of a database property
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     */
    ResultSet getDatabaseProperties(
        String dataSourceName,
        String propertyNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing member and cell Properties.
     *
     * <p>Specification as for XML/A MDSCHEMA_PROPERTIES schema rowset.
     *
     * <p>Not to be confused with {@link #getDatabaseProperties(String,String)}.
     *
     * <ol>
     * <li><b>CATALOG_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the database.</li>
     * <li><b>SCHEMA_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the schema to which this property belongs.</li>
     * <li><b>CUBE_NAME</b> String &rarr; The name of the cube.</li>
     * <li><b>DIMENSION_UNIQUE_NAME</b> String &rarr; The unique name of the
     *         dimension.</li>
     * <li><b>HIERARCHY_UNIQUE_NAME</b> String &rarr; The unique name of the
     *         hierarchy.</li>
     * <li><b>LEVEL_UNIQUE_NAME</b> String &rarr; The unique name of the level
     *         to which this property belongs.</li>
     * <li><b>MEMBER_UNIQUE_NAME</b> String (may be <code>null</code>) &rarr;
     *         The unique name of the member to which the property belongs.</li>
     * <li><b>PROPERTY_NAME</b> String &rarr; Name of the property.</li>
     * <li><b>PROPERTY_CAPTION</b> String &rarr; A label or caption associated
     *         with the property, used primarily for display purposes.</li>
     * <li><b>PROPERTY_TYPE</b> Short &rarr; A bitmap that specifies the type of
     *         the property</li>
     * <li><b>DATA_TYPE</b> UnsignedShort &rarr; Data type of the property.</li>
     * <li><b>PROPERTY_CONTENT_TYPE</b> Short (may be <code>null</code>) &rarr;
     *         The type of the property. </li>
     * <li><b>DESCRIPTION</b> String (may be <code>null</code>) &rarr; A
     *         human-readable description of the measure. </li>
     * </ol>
     *
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used
     *        to narrow the search
     *
     * @param schemaPattern a schema name pattern; must match the schema
     *        name as it is stored in the database; "" retrieves those without
     *        a schema; <code>null</code> means that the schema name should not
     *        be used to narrow the search
     *
     * @param cubeNamePattern a cube name pattern; must match the
     *        cube name as it is stored in the database; "" retrieves those
     *        without a cube; <code>null</code> means that the cube name should
     *        not be used to narrow the search
     *
     * @param dimensionUniqueName unique name of a dimension (not a pattern);
     *        must match the dimension name as it is stored in the database;
     *        <code>null</code> means that the dimension name should not be
     *        used to narrow the search
     *
     * @param hierarchyUniqueName unique name of a hierarchy (not a pattern);
     *        must match the
     *        hierarchy name as it is stored in the database; <code>null</code>
     *        means that the hierarchy name should not be used to narrow the
     *        search
     *
     * @param levelUniqueName unique name of a level (not a pattern);
     *        must match the
     *        level name as it is stored in the database; <code>null</code>
     *        means that the level name should not be used to narrow the
     *        search
     *
     * @param memberUniqueName unique name of member (not a pattern);
     *        <code>null</code>
     *        means that the member unique name should not be used to narrow
     *        the search
     *
     * @param propertyNamePattern a property name pattern; must match the
     *        property name as it is stored in the database; <code>null</code>
     *        means that the property name should not be used to narrow the
     *        search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         description of a member or cell property
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     * @see org.olap4j.metadata.Property
     */
    ResultSet getProperties(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionUniqueName,
        String hierarchyUniqueName,
        String levelUniqueName,
        String memberUniqueName,
        String propertyNamePattern) throws OlapException;

    /**
     * Retrieves a comma-separated list of all of this database's MDX keywords.
     *
     * @return the list of this database's MDX keywords
     *
     * @exception OlapException if a database access error occurs
     */
    String getMdxKeywords() throws OlapException;

    /**
     * Retrieves a result set describing the Cubes in this database.
     *
     * <p>Specification as for XML/A MDSCHEMA_CUBES schema rowset.
     *
     * <p>Each cube description has the following columns:
     * <ol>
     * <li><b>CATALOG_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the catalog to which this cube belongs.</li>
     * <li><b>SCHEMA_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the schema to which this cube belongs.</li>
     * <li><b>CUBE_NAME</b> String &rarr; Name of the cube.</li>
     * <li><b>CUBE_TYPE</b> String &rarr; Cube type.</li>
     * <li><b>CUBE_GUID</b> UUID (may be <code>null</code>) &rarr; Cube
     *         type.</li>
     * <li><b>CREATED_ON</b> Timestamp (may be <code>null</code>) &rarr; Date
     *         and time of cube creation.</li>
     * <li><b>LAST_SCHEMA_UPDATE</b> Timestamp (may be <code>null</code>) &rarr;
     *         Date and time of last schema update.</li>
     * <li><b>SCHEMA_UPDATED_BY</b> String (may be <code>null</code>) &rarr;
     *         User ID of the person who last updated the schema.</li>
     * <li><b>LAST_DATA_UPDATE</b> Timestamp (may be <code>null</code>) &rarr;
     *         Date and time of last data update.</li>
     * <li><b>DATA_UPDATED_BY</b> String (may be <code>null</code>) &rarr; User
     *         ID of the person who last updated the data. </li>
     * <li><b>IS_DRILLTHROUGH_ENABLED</b> boolean &rarr; Describes whether
     *         DRILLTHROUGH can be performed on the members of a cube</li>
     * <li><b>IS_WRITE_ENABLED</b> boolean &rarr; Describes whether a cube is
     *         write-enabled</li>
     * <li><b>IS_LINKABLE</b> boolean &rarr; Describes whether a cube can be
     *         used in a linked cube</li>
     * <li><b>IS_SQL_ENABLED</b> boolean &rarr; Describes whether or not SQL can
     *         be used on the cube</li>
     * <li><b>DESCRIPTION</b> String (may be <code>null</code>) &rarr; A
     *         user-friendly description of the cube.</li>
     * <li><b>CUBE_CAPTION</b> String (may be <code>null</code>) &rarr;
     *         The caption of the cube.</li>
     * <li><b>BASE_CUBE_NAME</b> String (may be <code>null</code>) &rarr;
     *         The name of the source cube if this cube is a perspective
     *         cube.</li>
     * </ol>
     *
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used
     *        to narrow the search
     *
     * @param schemaPattern a schema name pattern; must match the schema
     *        name as it is stored in the database; "" retrieves those without
     *        a schema; <code>null</code> means that the schema name should not
     *        be used to narrow the search
     *
     * @param cubeNamePattern a cube name pattern; must match the
     *        cube name as it is stored in the database; <code>null</code>
     *        means that the cube name should not be used to narrow the search
     *
     * @return <code>ResultSet</code> in which each row is a cube description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     * @see org.olap4j.metadata.Cube
     */
    public ResultSet getCubes(
        String catalog,
        String schemaPattern,
        String cubeNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing the shared and private Dimensions
     * in this database.
     *
     * <p>Specification as for XML/A MDSCHEMA_DIMENSIONS schema rowset.
     *
     * <p>Each dimension description has the following columns:
     * <ol>
     * <li><b>CATALOG_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the database.</li>
     * <li><b>SCHEMA_NAME</b> String (may be <code>null</code>) &rarr; Not
     *         supported.</li>
     * <li><b>CUBE_NAME</b> String &rarr; The name of the cube.</li>
     * <li><b>DIMENSION_NAME</b> String &rarr; The name of the dimension. </li>
     * <li><b>DIMENSION_UNIQUE_NAME</b> String &rarr; The unique name of the
     *         dimension.</li>
     * <li><b>DIMENSION_GUID</b> String (may be <code>null</code>) &rarr; Not
     *         supported.</li>
     * <li><b>DIMENSION_CAPTION</b> String &rarr; The caption of the
     *         dimension.</li>
     * <li><b>DIMENSION_ORDINAL</b> int &rarr; The position of the dimension
     *         within the cube.</li>
     * <li><b>DIMENSION_TYPE</b> Short &rarr; The type of the dimension.</li>
     * <li><b>DIMENSION_CARDINALITY</b> int &rarr; The number of members in the
     *         key attribute.</li>
     * <li><b>DEFAULT_HIERARCHY</b> String &rarr; A hierarchy from the
     *         dimension. Preserved for backwards compatibility.</li>
     * <li><b>DESCRIPTION</b> String (may be <code>null</code>) &rarr; A
     *         user-friendly description of the dimension.</li>
     * <li><b>IS_VIRTUAL</b> boolean (may be <code>null</code>) &rarr; Always
     *         FALSE.</li>
     * <li><b>IS_READWRITE</b> boolean (may be <code>null</code>) &rarr; A
     *         Boolean that indicates whether the dimension is
     *         write-enabled.</li>
     * <li><b>DIMENSION_UNIQUE_SETTINGS</b> int (may be <code>null</code>)
     *         &rarr; A bitmap that specifies which columns contain unique
     *         values if the dimension contains only members with unique
     *         names.</li>
     * <li><b>DIMENSION_MASTER_UNIQUE_NAME</b> String (may be
     *         <code>null</code>) &rarr; Always NULL.</li>
     * <li><b>DIMENSION_IS_VISIBLE</b> boolean (may be <code>null</code>) &rarr;
     *         Always TRUE.</li>
     * </ol>
     *
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used
     *        to narrow the search
     *
     * @param schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a
     *        schema; <code>null</code> means that the schema name should not
     *        be used to narrow the search
     *
     * @param cubeNamePattern a cube name pattern; must match the
     *        cube name as it is stored in the database; "" retrieves those
     *        without a cube (such as shared dimensions);
     *        <code>null</code> means that the cube name should
     *        not be used to narrow the search
     *
     * @param dimensionNamePattern a dimension name pattern; must match the
     *        dimension name as it is stored in the database; <code>null</code>
     *        means that the dimension name should not be used to narrow the
     *        search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         dimension description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     * @see org.olap4j.metadata.Dimension
     */
    ResultSet getDimensions(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing the Functions available to client
     * applications connected to the database.
     *
     * <p>Specification as for XML/A MDSCHEMA_FUNCTIONS schema rowset.
     *
     * <p>Each function description has the following columns:
     *
     * <ol>
     * <li><b>FUNCTION_NAME</b> String &rarr; The name of the function.</li>
     * <li><b>DESCRIPTION</b> String (may be <code>null</code>) &rarr; A
     *         description of the function.</li>
     * <li><b>PARAMETER_LIST</b> String (may be <code>null</code>) &rarr; A
     *         comma delimited list of parameters.</li>
     * <li><b>RETURN_TYPE</b> int &rarr; The VARTYPE of the return data type of
     *         the function.</li>
     * <li><b>ORIGIN</b> int &rarr; The origin of the function:  1 for MDX
     *         functions.  2 for user-defined functions.</li>
     * <li><b>INTERFACE_NAME</b> String &rarr; The name of the interface for
     *         user-defined functions</li>
     * <li><b>LIBRARY_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the type library for user-defined functions. NULL for MDX
     *         functions.</li>
     * <li><b>CAPTION</b> String (may be <code>null</code>) &rarr; The display
     *         caption for the function.</li>
     * </ol>
     *
     * @param functionNamePattern a function name pattern; must match the
     *        function name as it is stored in the database; <code>null</code>
     *        means that the function name should not be used to narrow the
     *        search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         function description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     */
    // NOTE: '#getFunctions(String, String, String)' above generates a javadoc
    // error on JDK 1.5, because it is new in JDBC 4.0/JDK 1.6. But please leave
    // it in. Most olap4j users run on JDK 1.6 or later, and the javadoc is
    // intended for them.
    ResultSet getOlapFunctions(
        String functionNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing the Hierarchies in this database.
     *
     * <p>Specification as for XML/A MDSCHEMA_HIERARCHIES schema rowset.
     *
     * <p>Each hierarchy description has the following columns:
     *
     * <ol>
     * <li><b>CATALOG_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the catalog to which this hierarchy belongs.</li>
     * <li><b>SCHEMA_NAME</b> String (may be <code>null</code>) &rarr; Not
     *         supported</li>
     * <li><b>CUBE_NAME</b> String &rarr; The name of the cube to which this
     *         hierarchy belongs.</li>
     * <li><b>DIMENSION_UNIQUE_NAME</b> String &rarr; The unique name of the
     *         dimension to which this hierarchy belongs. </li>
     * <li><b>HIERARCHY_NAME</b> String &rarr; The name of the hierarchy. Blank
     *         if there is only a single hierarchy in the dimension.</li>
     * <li><b>HIERARCHY_UNIQUE_NAME</b> String &rarr; The unique name of the
     *         hierarchy.</li>
     * <li><b>HIERARCHY_GUID</b> String (may be <code>null</code>) &rarr;
     *         Hierarchy GUID.</li>
     * <li><b>HIERARCHY_CAPTION</b> String &rarr; A label or a caption
     *         associated with the hierarchy.</li>
     * <li><b>DIMENSION_TYPE</b> Short &rarr; The type of the dimension. </li>
     * <li><b>HIERARCHY_CARDINALITY</b> int &rarr; The number of members in the
     *         hierarchy.</li>
     * <li><b>DEFAULT_MEMBER</b> String (may be <code>null</code>) &rarr; The
     *         default member for this hierarchy. </li>
     * <li><b>ALL_MEMBER</b> String (may be <code>null</code>) &rarr; The member
     *         at the highest level of rollup in the hierarchy.</li>
     * <li><b>DESCRIPTION</b> String (may be <code>null</code>) &rarr; A
     *         human-readable description of the hierarchy. NULL if no
     *         description exists.</li>
     * <li><b>STRUCTURE</b> Short &rarr; The structure of the hierarchy.</li>
     * <li><b>IS_VIRTUAL</b> boolean &rarr; Always returns False.</li>
     * <li><b>IS_READWRITE</b> boolean &rarr; A Boolean that indicates whether
     *         the Write Back to dimension column is enabled.</li>
     * <li><b>DIMENSION_UNIQUE_SETTINGS</b> int &rarr; Always returns
     *         MDDIMENSIONS_MEMBER_KEY_UNIQUE (1).</li>
     * <li><b>DIMENSION_IS_VISIBLE</b> boolean &rarr; Always returns true.</li>
     * <li><b>HIERARCHY_ORDINAL</b> int &rarr; The ordinal number of the
     *         hierarchy across all hierarchies of the cube.</li>
     * <li><b>DIMENSION_IS_SHARED</b> boolean &rarr; Always returns true.</li>
     * <li><b>PARENT_CHILD</b> boolean (may be <code>null</code>) &rarr; Is
     *         hierarchy a parent.</li>
     * </ol>
     *
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used
     *        to narrow the search
     *
     * @param schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a
     *        schema; <code>null</code> means that the schema name should not
     *        be used to narrow the search
     *
     * @param cubeNamePattern a cube name pattern; must match the
     *        cube name as it is stored in the database; "" retrieves those
     *        without a cube; <code>null</code> means that the cube name should
     *        not be used to narrow the search
     *
     * @param dimensionUniqueName unique name of a dimension (not a pattern);
     *        must match the
     *        dimension name as it is stored in the database; <code>null</code>
     *        means that the dimension name should not be used to narrow the
     *        search
     *
     * @param hierarchyNamePattern a hierarchy name pattern; must match the
     *        hierarchy name as it is stored in the database; <code>null</code>
     *        means that the hierarchy name should not be used to narrow the
     *        search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         hierarchy description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     * @see org.olap4j.metadata.Hierarchy
     */
    ResultSet getHierarchies(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionUniqueName,
        String hierarchyNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing the Levels in this database.
     *
     * <p>Specification as for XML/A MDSCHEMA_LEVELS schema rowset.
     *
     * <p>Each level description has the following columns:
     * <ol>
     * <li><b>CATALOG_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the catalog to which this level belongs.</li>
     * <li><b>SCHEMA_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the schema to which this level belongs.</li>
     * <li><b>CUBE_NAME</b> String &rarr; The name of the cube to which this
     *         level belongs.</li>
     * <li><b>DIMENSION_UNIQUE_NAME</b> String &rarr; The unique name of the
     *         dimension to which this level belongs.</li>
     * <li><b>HIERARCHY_UNIQUE_NAME</b> String &rarr; The unique name of the
     *         hierarchy.</li>
     * <li><b>LEVEL_NAME</b> String &rarr; The name of the level.</li>
     * <li><b>LEVEL_UNIQUE_NAME</b> String &rarr; The properly escaped unique
     *         name of the level.</li>
     * <li><b>LEVEL_GUID</b> String (may be <code>null</code>) &rarr; Level
     *         GUID.</li>
     * <li><b>LEVEL_CAPTION</b> String &rarr; A label or caption associated with
     *         the hierarchy.</li>
     * <li><b>LEVEL_NUMBER</b> int &rarr; The distance of the level from the
     *         root of the hierarchy. Root level is zero (0).</li>
     * <li><b>LEVEL_CARDINALITY</b> int &rarr; The number of members in the
     *         level. This value can be an approximation of the real
     *         cardinality.</li>
     * <li><b>LEVEL_TYPE</b> int &rarr; Type of the level</li>
     * <li><b>CUSTOM_ROLLUP_SETTINGS</b> int &rarr; A bitmap that specifies the
     *         custom rollup options.</li>
     * <li><b>LEVEL_UNIQUE_SETTINGS</b> int &rarr; A bitmap that specifies which
     *         columns contain unique values, if the level only has members
     *         with unique names or keys.</li>
     * <li><b>LEVEL_IS_VISIBLE</b> boolean &rarr; A Boolean that indicates
     *         whether the level is visible.</li>
     * <li><b>DESCRIPTION</b> String (may be <code>null</code>) &rarr; A
     *         human-readable description of the level. NULL if no
     *         description exists.</li>
     * </ol>
     *
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used
     *        to narrow the search
     *
     * @param schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a
     *        schema; <code>null</code> means that the schema name should not
     *        be used to narrow the search
     *
     * @param cubeNamePattern a cube name pattern; must match the
     *        cube name as it is stored in the database; "" retrieves those
     *        without a cube; <code>null</code> means that the cube name should
     *        not be used to narrow the search
     *
     * @param dimensionUniqueName unique name of a dimension (not a pattern);
     *        must match the
     *        dimension name as it is stored in the database; <code>null</code>
     *        means that the dimension name should not be used to narrow the
     *        search
     *
     * @param hierarchyUniqueName unique name of a hierarchy (not a pattern);
     *        must match the
     *        hierarchy name as it is stored in the database; <code>null</code>
     *        means that the hierarchy name should not be used to narrow the
     *        search
     *
     * @param levelNamePattern a level name pattern; must match the
     *        level name as it is stored in the database; <code>null</code>
     *        means that the level name should not be used to narrow the
     *        search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         level description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     * @see org.olap4j.metadata.Level
     */
    ResultSet getLevels(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionUniqueName,
        String hierarchyUniqueName,
        String levelNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing the Measures in this database.
     *
     * <p>Specification as for XML/A MDSCHEMA_MEASURES schema rowset.
     *
     * <p>Each measure description has the following columns:
     * <ol>
     * <li><b>CATALOG_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the catalog to which this measure belongs. </li>
     * <li><b>SCHEMA_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the schema to which this measure belongs.</li>
     * <li><b>CUBE_NAME</b> String &rarr; The name of the cube to which this
     *         measure belongs.</li>
     * <li><b>MEASURE_NAME</b> String &rarr; The name of the measure.</li>
     * <li><b>MEASURE_UNIQUE_NAME</b> String &rarr; The Unique name of the
     *         measure.</li>
     * <li><b>MEASURE_CAPTION</b> String &rarr; A label or caption associated
     *         with the measure. </li>
     * <li><b>MEASURE_GUID</b> String (may be <code>null</code>) &rarr; Measure
     *         GUID.</li>
     * <li><b>MEASURE_AGGREGATOR</b> int &rarr; How a measure was derived. </li>
     * <li><b>DATA_TYPE</b> UnsignedShort &rarr; Data type of the measure.</li>
     * <li><b>MEASURE_IS_VISIBLE</b> boolean &rarr; A Boolean that always
     *         returns True. If the measure is not visible, it will not be
     *         included in the schema rowset.</li>
     * <li><b>LEVELS_LIST</b> String (may be <code>null</code>) &rarr; A string
     *         that always returns NULL. EXCEPT that SQL Server returns
     *         non-null values!!!</li>
     * <li><b>DESCRIPTION</b> String (may be <code>null</code>) &rarr; A
     *         human-readable description of the measure. </li>
     * </ol>
     *
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used
     *        to narrow the search
     *
     * @param schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a
     *        schema; <code>null</code> means that the schema name should not
     *        be used to narrow the search
     *
     * @param cubeNamePattern a cube name pattern; must match the
     *        cube name as it is stored in the database; "" retrieves those
     *        without a cube; <code>null</code> means that the cube name should
     *        not be used to narrow the search
     *
     * @param measureNamePattern a measure name pattern; must match the
     *        measure name as it is stored in the database; <code>null</code>
     *        means that the measure name should not be used to narrow the
     *        search
     *
     * @param measureUniqueName unique name of measure (not a pattern);
     *        <code>null</code> means that the measure unique name should not
     *        be used to narrow the search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         measure description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     * @see org.olap4j.metadata.Measure
     */
    ResultSet getMeasures(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String measureNamePattern,
        String measureUniqueName) throws OlapException;

    /**
     * Retrieves a result set describing the Members in this database.
     *
     * <p>Specification as for XML/A MDSCHEMA_MEMBERS schema rowset. Rows
     * are sorted by level number then by ordinal.
     *
     * <p>The <code>treeOps</code> parameter allows you to retrieve members
     * relative to a given member. It is only applicable if a
     * <code>memberUniqueName</code> is also specified; otherwise it is
     * ignored. The following example retrieves all descendants and ancestors
     * of California, but not California itself:
     *
     * <blockquote>
     * <pre>
     * OlapDatabaseMetaData metaData;
     * ResultSet rset = metaData.getMembers(
     *     "LOCALDB", "FoodMart", "Sales", null, null, null,
     *     "[Customers].[USA].[CA]",
     *     EnumSet.of(Member.TreeOp.ANCESTORS, Member.TreeOp.DESCENDANTS));
     * </pre>
     * </blockquote>
     *
     * <p>Each member description has the following columns:
     * <ol>
     * <li><b>CATALOG_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the catalog to which this member belongs. </li>
     * <li><b>SCHEMA_NAME</b> String (may be <code>null</code>) &rarr; The name
     *         of the schema to which this member belongs. </li>
     * <li><b>CUBE_NAME</b> String &rarr; Name of the cube to which this member
     *         belongs.</li>
     * <li><b>DIMENSION_UNIQUE_NAME</b> String &rarr; Unique name of the
     *         dimension to which this member belongs. </li>
     * <li><b>HIERARCHY_UNIQUE_NAME</b> String &rarr; Unique name of the
     *         hierarchy. If the member belongs to more than one hierarchy,
     *         there is one row for each hierarchy to which it belongs.</li>
     * <li><b>LEVEL_UNIQUE_NAME</b> String &rarr;  Unique name of the level to
     *         which the member belongs.</li>
     * <li><b>LEVEL_NUMBER</b> int &rarr; The distance of the member from the
     *         root of the hierarchy.</li>
     * <li><b>MEMBER_ORDINAL</b> int &rarr; Ordinal number of the member. Sort
     *         rank of the member when members of this dimension are sorted in
     *         their natural sort order. If providers do not have the concept
     *         of natural ordering, this should be the rank when sorted by
     *         MEMBER_NAME.</li>
     * <li><b>MEMBER_NAME</b> String &rarr; Name of the member.</li>
     * <li><b>MEMBER_UNIQUE_NAME</b> String &rarr;  Unique name of the
     *          member.</li>
     * <li><b>MEMBER_TYPE</b> int &rarr; Type of the member.</li>
     * <li><b>MEMBER_GUID</b> String (may be <code>null</code>) &rarr; Memeber
     *         GUID.</li>
     * <li><b>MEMBER_CAPTION</b> String &rarr; A label or caption associated
     *         with the member.</li>
     * <li><b>CHILDREN_CARDINALITY</b> int &rarr; Number of children that the
     *         member has.</li>
     * <li><b>PARENT_LEVEL</b> int &rarr; The distance of the member's parent
     *         from the root level of the hierarchy. </li>
     * <li><b>PARENT_UNIQUE_NAME</b> String (may be <code>null</code>) &rarr;
     *         Unique name of the member's parent.</li>
     * <li><b>PARENT_COUNT</b> int &rarr; Number of parents that this member
     *         has.</li>
     * <li><b>TREE_OP</b> Enumeration (may be <code>null</code>) &rarr; Tree
     *         Operation</li>
     * <li><b>DEPTH</b> int (may be <code>null</code>) &rarr; depth</li>
     * </ol>
     *
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used
     *        to narrow the search
     *
     * @param schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a
     *        schema; <code>null</code> means that the schema name should not
     *        be used to narrow the search
     *
     * @param cubeNamePattern a cube name pattern; must match the
     *        cube name as it is stored in the database; "" retrieves those
     *        without a cube; <code>null</code> means that the cube name should
     *        not be used to narrow the search
     *
     * @param dimensionUniqueName unique name of dimension (not a pattern);
     *        must match the
     *        dimension name as it is stored in the database; <code>null</code>
     *        means that the dimension name should not be used to narrow the
     *        search
     *
     * @param hierarchyUniqueName unique name of hierarchy (not a pattern);
     *        must match the
     *        hierarchy name as it is stored in the database; <code>null</code>
     *        means that the hierarchy name should not be used to narrow the
     *        search
     *
     * @param levelUniqueName unique name of level (not a pattern); must match
     *        the level name as it is stored in the database; <code>null</code>
     *        means that the level name should not be used to narrow the
     *        search
     *
     * @param memberUniqueName unique name of member (not a pattern);
     *        <code>null</code> means that the measure unique name should not
     *        be used to narrow the search
     *
     * @param treeOps set of tree operations to retrieve members relative
     *        to the member whose unique name was specified; or null to return
     *        just the member itself.
     *        Ignored if <code>memberUniqueName</code> is not specified.
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         member description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     * @see org.olap4j.metadata.Member
     */
    ResultSet getMembers(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionUniqueName,
        String hierarchyUniqueName,
        String levelUniqueName,
        String memberUniqueName,
        Set<Member.TreeOp> treeOps) throws OlapException;

    /**
     * Retrieves a result set describing the named Sets in this database.
     *
     * <p>Specification as for XML/A MDSCHEMA_SETS schema rowset.
     *
     * <p>Each set description has the following columns:
     *
     * <ol>
     * <li><b>CATALOG_NAME</b> String (may be <code>null</code>) &rarr;
     *         null</li>
     * <li><b>SCHEMA_NAME</b> String (may be <code>null</code>) &rarr; null</li>
     * <li><b>CUBE_NAME</b> String &rarr; null</li>
     * <li><b>SET_NAME</b> String &rarr; null</li>
     * <li><b>SCOPE</b> int &rarr; null</li>
     * </ol>
     *
     * @param catalog a catalog name; must match the catalog name as it
     *        is stored in the database; "" retrieves those without a catalog;
     *        <code>null</code> means that the catalog name should not be used
     *        to narrow the search
     *
     * @param schemaPattern a schema name pattern; must match the schema name
     *        as it is stored in the database; "" retrieves those without a
     *        schema; <code>null</code> means that the schema name should not
     *        be used to narrow the search
     *
     * @param cubeNamePattern a cube name pattern; must match the
     *        cube name as it is stored in the database; "" retrieves those
     *        without a cube; <code>null</code> means that the cube name should
     *        not be used to narrow the search
     *
     * @param setNamePattern pattern for the unique name of a set; must match
     *        the set name as it is stored in the database; <code>null</code>
     *        means that the set name should not be used to narrow the
     *        search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         description of a named set
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     * @see org.olap4j.metadata.NamedSet
     */
    ResultSet getSets(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String setNamePattern) throws OlapException;
}

// End OlapDatabaseMetaData.java
