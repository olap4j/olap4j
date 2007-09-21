/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.metadata.Member;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

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
 * @version $Id$
 * @since Oct 12, 2006
 */
public interface OlapDatabaseMetaData extends DatabaseMetaData, OlapWrapper {

    // override return type
    OlapConnection getConnection() throws SQLException;

    /**
     * Retrieves a list of descriptions of an Action.
     *
     * <p>Specification as for XML/A MDSCHEMA_ACTIONS schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
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
     * Retrives a list of olap4j data sources that are available on the server.
     *
     * <p>Specification as for XML/A DISCOVER_DATASOURCES schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
     *
     * @param dataSourceName Name of data source
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         datasource description
     *
     * @exception OlapException if a database access error occurs
     */
    ResultSet getDatasources(
        String dataSourceName) throws OlapException;

    /**
     * Retrieves a list of information on supported literals, including data
     * types and values.
     *
     * <p>Specification as for XML/A DISCOVER_LITERALS schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
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
     * <p>todo: copy description of result set columns from spec
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
     * Retrieves a list of descriptions of member and cell Properties.
     *
     * <p>Specification as for XML/A MDSCHEMA_PROPERTIES schema rowset.
     *
     * <p>Not to be confused with {@link #getDatabaseProperties(String,String)}.
     *
     * <p>todo: copy description of result set columns from spec
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
     * @param dimensionNamePattern a dimension name pattern; must match the
     *        dimension name as it is stored in the database; <code>null</code>
     *        means that the dimension name should not be used to narrow the
     *        search
     *
     * @param hierarchyNamePattern a hierarchy name pattern; must match the
     *        hierarchy name as it is stored in the database; <code>null</code>
     *        means that the hierarchy name should not be used to narrow the
     *        search
     *
     * @param levelNamePattern a level name pattern; must match the
     *        level name as it is stored in the database; <code>null</code>
     *        means that the level name should not be used to narrow the
     *        search
     *
     * @param memberUniqueName unique name of member; <code>null</code>
     *        means that the member unique name should not be used to narrow
     *        the search
     *
     * @param propertyNamePattern an property name pattern; must match the
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
     */
    ResultSet getProperties(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionNamePattern,
        String hierarchyNamePattern,
        String levelNamePattern,
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
     * Retrieves a description of a cube.
     *
     * <p>Specification as for XML/A MDSCHEMA_CUBES schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
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
     *        cube name as it is stored in the database; <code>null</code>
     *        means that the cube name should not be used to narrow the search
     *
     * @return <code>ResultSet</code> in which each row is a cube description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     */
    public ResultSet getCubes(
        String catalog,
        String schemaPattern,
        String cubeNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing the shared and private dimensions
     * within a database.
     *
     * <p>Specification as for XML/A MDSCHEMA_DIMENSIONS schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
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
     */
    ResultSet getDimensions(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing the functions available to client
     * applications connected to the database.
     *
     * <p>Specification as for XML/A MDSCHEMA_FUNCTIONS schema rowset.
     *
     * <p>todo: document parameters and result set columns
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
    ResultSet getFunctions(String functionNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing each hierarchy within a particular
     * dimension.
     *
     * <p>Specification as for XML/A MDSCHEMA_HIERARCHIES schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
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
     * @param dimensionNamePattern a dimension name pattern; must match the
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
     */
    ResultSet getHierarchies(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionNamePattern,
        String hierarchyNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing each measure within a cube.
     *
     * <p>Specification as for XML/A MDSCHEMA_MEASURES schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
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
     * @param measureUniqueName unique name of measure; <code>null</code>
     *        means that the measure unique name should not be used to narrow
     *        the search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         measure description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     */
    ResultSet getMeasures(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String measureNamePattern,
        String measureUniqueName) throws OlapException;

    /**
     * Retrieves a result set describing the members within a database.
     *
     * <p>Specification as for XML/A MDSCHEMA_MEMBERS schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
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
     * @param dimensionNamePattern a dimension name pattern; must match the
     *        dimension name as it is stored in the database; <code>null</code>
     *        means that the dimension name should not be used to narrow the
     *        search
     *
     * @param hierarchyNamePattern a hierarchy name pattern; must match the
     *        hierarchy name as it is stored in the database; <code>null</code>
     *        means that the hierarchy name should not be used to narrow the
     *        search
     *
     * @param levelNamePattern a level name pattern; must match the
     *        level name as it is stored in the database; <code>null</code>
     *        means that the level name should not be used to narrow the
     *        search
     * 
     * @param memberUniqueName unique name of member; <code>null</code>
     *        means that the member unique name should not be used to narrow
     *        the search
     *
     * @param treeOp Only applies to a single member.
     *       {@link org.olap4j.metadata.Member.TreeOp#ANCESTORS}
     *       returns all of the ancestors;
     *       {@link org.olap4j.metadata.Member.TreeOp#CHILDREN}
     *       returns only the immediate children;
     *       {@link org.olap4j.metadata.Member.TreeOp#SIBLINGS}
     *       returns members on the same level;
     *       {@link org.olap4j.metadata.Member.TreeOp#PARENT}
     *       returns only the immediate parent;
     *       {@link org.olap4j.metadata.Member.TreeOp#SELF}
     *       returns itself in the list of returned rows;
     *       {@link org.olap4j.metadata.Member.TreeOp#DESCENDANTS}
     *       returns all of the descendants.
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         member description
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     */
    ResultSet getMembers(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionNamePattern,
        String hierarchyNamePattern,
        String levelNamePattern,
        String memberUniqueName,
        Member.TreeOp treeOp) throws OlapException;

    /**
     * Retrieves a result set describing each level within a particular
     * hierarchy.
     *
     * <p>Specification as for XML/A MDSCHEMA_LEVELS schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
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
     * @param dimensionNamePattern a dimension name pattern; must match the
     *        dimension name as it is stored in the database; <code>null</code>
     *        means that the dimension name should not be used to narrow the
     *        search
     *
     * @param hierarchyNamePattern a hierarchy name pattern; must match the
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
     */
    ResultSet getLevels(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionNamePattern,
        String hierarchyNamePattern,
        String levelNamePattern) throws OlapException;

    /**
     * Retrieves a result set describing each named set.
     *
     * <p>Specification as for XML/A MDSCHEMA_SETS schema rowset.
     *
     * <p>todo: copy description of result set columns from spec
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
     * @param setNamePattern a set name pattern; must match the
     *        set name as it is stored in the database; <code>null</code>
     *        means that the set name should not be used to narrow the
     *        search
     *
     * @return a <code>ResultSet</code> object in which each row is a
     *         description of a named set
     *
     * @exception OlapException if a database access error occurs
     *
     * @see #getSearchStringEscape
     */
    ResultSet getSets(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String setNamePattern) throws OlapException;
}

// End OlapDatabaseMetaData.java
